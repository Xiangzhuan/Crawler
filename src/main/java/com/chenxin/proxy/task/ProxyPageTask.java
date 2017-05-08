package com.chenxin.proxy.task;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;

import com.chenxin.core.util.Config;
import com.chenxin.core.util.Constants;
import com.chenxin.core.util.HttpClientUtil;
import com.chenxin.proxy.ProxyHttpClient;
import com.chenxin.proxy.ProxyListPageParser;
import com.chenxin.proxy.ProxyPool;
import com.chenxin.proxy.entity.Direct;
import com.chenxin.proxy.entity.Proxy;
import com.chenxin.proxy.site.ProxyListPageParserFactory;
import com.chenxin.spider.ZhiHuHttpClient;
import com.chenxin.spider.entity.Page;

/**
 * 下载代理网页并解析
 * 若下载失败，通过代理去下载代理网页
 * @author j
 *
 */
public class ProxyPageTask implements Runnable{

	private static Logger logger = Logger.getLogger(ProxyPageTask.class);
	protected String url ;
	private boolean proxyFlag; //是否通过代理下载
	private Proxy currentProxy; //当前线程使用的情况
	
	protected static ProxyHttpClient proxyHttpClient = ProxyHttpClient.getInstance();
	
	private ProxyPageTask(){}
	public ProxyPageTask(String url, boolean proxyFlag){
		this.url = url;
		this.proxyFlag = proxyFlag;
	}
	@Override
	public void run() {
		long requestStartTime = System.currentTimeMillis();
		HttpGet tempRequest = null;
		try{
			Page page = null;
			//如果使用代理
			if(proxyFlag){
				tempRequest = new HttpGet(url);
				currentProxy = ProxyPool.proxyQueue.take();
				if(!(currentProxy instanceof Direct)){
					HttpHost proxy = new HttpHost(currentProxy.getIp(),currentProxy.getPort());
					//设置代理
					tempRequest.setConfig(HttpClientUtil.getRequestConfigBuilder().setProxy(proxy).build());
				}
				page = proxyHttpClient.getWebPage(tempRequest);
			}else{  //如果不使用代理
				page = proxyHttpClient.getWebPage(url);
			}
			page.setProxy(currentProxy);
			int status = page.getStatusCode();
			long requestEndTime = System.currentTimeMillis();
			String logStr =Thread.currentThread().getName() + " " + getProxyStr(currentProxy) +
					"  executing request " + page.getUrl()  + " response statusCode:" + status +
					"  request cost time:" + (requestEndTime - requestStartTime) + "ms";
			if(status == HttpStatus.SC_OK){
				logger.debug(logStr);
				handle(page);
			}else{
				logger.error(logStr);
				Thread.sleep(100);
				retry();	//重新连接
			}
		}catch (InterruptedException e) {
			logger.error("InterruptedException", e);
		} catch (IOException e) {
			retry();
		} finally {
			if(currentProxy != null){
				//重新加载时间
				currentProxy.setTimeInterval(Constants.TIME_INTERVAL);
				ProxyPool.proxyQueue.add(currentProxy);
			}
			if (tempRequest != null){
				tempRequest.releaseConnection();
			}
		}
	}
	
	
	public void retry(){
		proxyHttpClient.getProxyDownloadThreadExecutor().execute(new ProxyPageTask(url, Config.isProxy));
	}
	
	//操作
	public void handle(Page page){
		if (page.getHtml() == null || page.getHtml().equals("")){
			return;
		}

		ProxyListPageParser parser = ProxyListPageParserFactory.getProxyListPageParser(ProxyPool.proxyMap.get(url));
		List<Proxy> proxyList = parser.parse(page.getHtml());
		for(Proxy p : proxyList){
			if(!ZhiHuHttpClient.getInstance().getDetailListPageThreadPool().isTerminated()){
				if (!ProxyPool.proxySet.contains(p.getProxyStr())){
					proxyHttpClient.getProxyTestThreadExecutor().execute(new ProxyTestTask(p));
				}
			}
		}
	}
	//日志格式
	private String getProxyStr(Proxy proxy){
		if (proxy == null){
			return "";
		}
		return proxy.getIp() + ":" + proxy.getPort();
	}

}
