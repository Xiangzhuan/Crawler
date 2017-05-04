package com.chenxin.proxy;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.chenxin.core.httpclient.AbstractHttpclient;
import com.chenxin.core.util.Config;
import com.chenxin.core.util.Constants;
import com.chenxin.core.util.HttpClientUtil;
import com.chenxin.core.util.SimpleThreadPoolExecutor;
import com.chenxin.proxy.entity.Proxy;
import com.chenxin.spider.entity.Page;

public class ProxyHttpClient extends AbstractHttpclient {

	private static final Logger logger = Logger
			.getLogger(ProxyHttpClient.class);

	private volatile static ProxyHttpClient instance;

	private static Set<Page> downloadFailureProxyPageSet = new HashSet<Page>();

	/**
	 * 单例构造
	 * 
	 * @return
	 */
	public static ProxyHttpClient getInstance() {
		if (instance == null) {
			synchronized (ProxyHttpClient.class) {
				if (instance == null) {
					instance = new ProxyHttpClient();
				}
			}
		}
		return instance;
	}

	/**
	 * 代理线程池
	 */
	private ThreadPoolExecutor proxyTestThreadExecutor;

	/**
	 * 代理网站下载线程池
	 */
	private ThreadPoolExecutor proxyDownloadThreadExecutor;

	private ProxyHttpClient() {
		initThreadPool();
		initProxy();
	}

	/**
	 * 初始化proxy
	 */
	private void initProxy() {
		Proxy[] proxyArray = null;

		try {
			proxyArray = (Proxy[]) HttpClientUtil.deserializeObject(Config.proxyPath);
			int usableProxyCount = 0;
			for (Proxy p : proxyArray) {
				if (p == null) {
					continue;
				}
				p.setTimeInterval(Constants.TIME_INTERVAL);
				p.setFailureTimes(0);
				p.setSuccessfulTimes(0);
				long nowTime = System.currentTimeMillis();
				if (nowTime - p.getLastSuccessfulTime() < 1000 * 60 * 60) {
					//上次成功离现在少于一小时
					ProxyPool.proxyQueue.add(p);
					ProxyPool.proxySet.add(p);
					usableProxyCount++;
				}
			}
			logger.info("反序列化成功，"+proxyArray.length+"个代理，可用代理"+usableProxyCount+"个");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化线程池
	 */
	private void initThreadPool() {
		proxyTestThreadExecutor = new SimpleThreadPoolExecutor(100, 100,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(10000),
                new ThreadPoolExecutor.DiscardPolicy(),
                "proxyTestThreadExecutor");
		proxyDownloadThreadExecutor = new SimpleThreadPoolExecutor(10, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), "" +
                "proxyDownloadThreadExecutor");
		//两个线程池监视工具 TODO
	}
	
	public void startCrawl(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					for(String url :ProxyPool.proxyMap.keySet()){
						/**
						 * 首次本机直接下载代理页面
						 */
						//proxyDownloadThreadExecutor.execute(command);
					}
				}
			}
		}).start();
	}

	public ThreadPoolExecutor getProxyTestThreadExecutor() {
        return proxyTestThreadExecutor;
    }

    public ThreadPoolExecutor getProxyDownloadThreadExecutor() {
        return proxyDownloadThreadExecutor;
    }

}
