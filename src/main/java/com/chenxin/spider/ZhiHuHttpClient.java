package com.chenxin.spider;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.chenxin.core.httpclient.AbstractHttpclient;
import com.chenxin.core.util.Config;
import com.chenxin.core.util.HttpClientUtil;
import com.chenxin.core.util.SimpleThreadPoolExecutor;
import com.chenxin.core.util.ThreadPoolMonitor;
import com.chenxin.spider.task.DetailPageTask;



public class ZhiHuHttpClient extends AbstractHttpclient {
	
	private final static Logger LOGGER = Logger.getLogger(ZhiHuHttpClient.class);
	private volatile static ZhiHuHttpClient instance;
	
	/**
	 * 统计用户数量
	 */
	public static AtomicInteger parseUserCount = new AtomicInteger(0);
	private static long startTime = System.currentTimeMillis();
	public static volatile boolean isStop = false;
	
	public static ZhiHuHttpClient getInstance(){
		if(instance==null){
			synchronized (ZhiHuHttpClient.class) {
				if(instance==null){
					instance = new ZhiHuHttpClient();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 详细页下载线程池
	 */
	private ThreadPoolExecutor detailPageThreadPool;
	
	/**
	 * 列表页下载线程池
	 */
	private ThreadPoolExecutor listPageThreadPool;
	/**
	 * 详细页列表下载线程池
	 */
	private ThreadPoolExecutor detailListPageThreadPool;
	
	private static String authorization;
	
	private ZhiHuHttpClient(){
		initHttpClient();    //初始化
		intiThreadPool();	 //初始化线程池
	}
	
	
	private void intiThreadPool() {
		 detailPageThreadPool = new SimpleThreadPoolExecutor(Config.downloadThreadSize,
	                										Config.downloadThreadSize,
	                										0L, 
	                										TimeUnit.MILLISECONDS,
	                										new LinkedBlockingQueue<Runnable>(),
	                										"detailPageThreadPool");
		 listPageThreadPool = new SimpleThreadPoolExecutor(50,
				 										   80,
	                									   0L, 
	                									   TimeUnit.MILLISECONDS,
	                									   new LinkedBlockingQueue<Runnable>(5000),
	                									   new ThreadPoolExecutor.DiscardPolicy(), 
	                									   "listPageThreadPool");
		 detailListPageThreadPool = new SimpleThreadPoolExecutor(Config.downloadThreadSize,
				 												 Config.downloadThreadSize,
				 												 0L, 
				 												 TimeUnit.MILLISECONDS,
				 												 new LinkedBlockingQueue<Runnable>(2000),
				 												 new ThreadPoolExecutor.DiscardPolicy(),
				 												"detailListPageThreadPool");
		 
		 new Thread(new ThreadPoolMonitor(detailPageThreadPool, "DetailPageDownloadThreadPool")).start();
	     new Thread(new ThreadPoolMonitor(listPageThreadPool, "ListPageDownloadThreadPool")).start();
	     new Thread(new ThreadPoolMonitor(detailListPageThreadPool, "DetailListPageThreadPool")).start();
	}
	
	public void startCrawl(String url){
        detailPageThreadPool.execute(new DetailPageTask(url, Config.isProxy));
        manageHttpClient();
    }
	public void startCrawl(){
		
	}
	
	/**
     * 管理知乎客户端
     * 关闭整个爬虫
     */
    public void manageHttpClient(){
        while (true) {
            /**
             * 下载网页数
             */
            long downloadPageCount = detailListPageThreadPool.getTaskCount();
//            if (downloadPageCount >= Config.downloadPageCount &&
//                    ! ZhiHuHttpClient.getInstance().getDetailPageThreadPool().isShutdown()) {
//                isStop = true;
//                /**
//                 * shutdown 下载网页线程池
//                 */
//                ZhiHuHttpClient.getInstance().getDetailPageThreadPool().shutdown();
//            }
//            if(ZhiHuHttpClient.getInstance().getDetailPageThreadPool().isTerminated() &&
//                    !ZhiHuHttpClient.getInstance().getListPageThreadPool().isShutdown()){
//                /**
//                 * 下载网页线程池关闭后，再关闭解析网页线程池
//                 */
//                ZhiHuHttpClient.getInstance().getListPageThreadPool().shutdown();
//            }
//            if(ZhiHuHttpClient.getInstance().getListPageThreadPool().isTerminated()){
//                /**
//                 * 关闭线程池Monitor
//                 */
//                ThreadPoolMonitor.isStopMonitor = true;
//                /**
//                 * 关闭ProxyHttpClient客户端
//                 */
//                ProxyHttpClient.getInstance().getProxyTestThreadExecutor().shutdownNow();
//                ProxyHttpClient.getInstance().getProxyDownloadThreadExecutor().shutdownNow();
//                logger.info("--------------爬取结果--------------");
//                logger.info("获取用户数:" + parseUserCount.get());
//                break;
//            }
            if (downloadPageCount >= Config.downloadPageCount &&
                    !detailListPageThreadPool.isShutdown()) {
                isStop = true;
                detailListPageThreadPool.shutdown();
            }
            //占时没有数据库
            /*if(detailListPageThreadPool.isShutdown()){
                //关闭数据库连接
                Map<Thread, Connection> map = DetailListPageTask.getConnectionMap();
                for(Connection cn : map.values()){
                    try {
                        if (cn != null && !cn.isClosed()){
                            cn.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }*/
            double costTime = (System.currentTimeMillis() - startTime) / 1000.0;//单位s
            LOGGER.debug("抓取速率：" + parseUserCount.get() / costTime + "个/s");
//            logger.info("downloadFailureProxyPageSet size:" + ProxyHttpClient.downloadFailureProxyPageSet.size());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        System.exit(0);
    }
	

	/**
	 * 初始化httpClient
	 */
	private void initHttpClient() {
		authorization = initAuthorization();
		if (Config.dbEnable) {// 如果持久化到数据库，则进行数据库初始化 TODO
		}		
	}
	
	
	private String initAuthorization() {
		String content = null;
		try {
			content = HttpClientUtil.getWebPage(Config.startURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Pattern pattern = Pattern.compile("https://static\\.zhihu\\.com/heifetz/main\\.app\\.([0-9]|[a-z])*\\.js");
		Matcher matcher = pattern.matcher(content);
		String jsSrc = null;
		if(matcher.find()){
			jsSrc = matcher.group(0);
		}else{
			throw new RuntimeException("not find javascript url");
		}
		
		String jsContent = null;
		try {
			jsContent = HttpClientUtil.getWebPage(jsSrc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		pattern = Pattern.compile("CLIENT_ALIAS=\"(([0-9]|[a-z])*)\"");
		matcher = pattern.matcher(jsContent);
		if (matcher.find()){
            String authorization = matcher.group(1);
            return authorization;
        }
		throw new RuntimeException("not get authorization");
	}
	public static String getAuthorization(){
        return authorization;
    }
	
	
	
	
	
	/**
	 * 获得线程池
	 * @return
	 */
	public ThreadPoolExecutor getDetailPageThreadPool() {
        return detailPageThreadPool;
    }

    public ThreadPoolExecutor getListPageThreadPool() {
        return listPageThreadPool;
    }
    public ThreadPoolExecutor getDetailListPageThreadPool() {
        return detailListPageThreadPool;
    }
	
}
