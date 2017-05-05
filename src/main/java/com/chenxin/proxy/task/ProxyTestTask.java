package com.chenxin.proxy.task;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;

import com.chenxin.core.util.Constants;
import com.chenxin.proxy.ProxyPool;
import com.chenxin.proxy.entity.Proxy;
import com.chenxin.spider.ZhiHuHttpClient;
import com.chenxin.spider.entity.Page;

/**
 * 代理检测task 通过访问知乎首页，能否正确相应 将可用代理添加到DelayQueue延时队列中
 * 
 * @author j
 *
 */
public class ProxyTestTask implements Runnable {

	private final static Logger logger = Logger.getLogger(ProxyTestTask.class);
	private Proxy proxy;

	public ProxyTestTask(Proxy proxy) {
		this.proxy = proxy;
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		HttpGet request = new HttpGet(Constants.INDEX_URL);

		try {
			//设置代理配置
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(Constants.TIMEOUT)
					.setConnectTimeout(Constants.TIMEOUT)
					.setConnectionRequestTimeout(Constants.TIMEOUT)
					.setProxy(new HttpHost(proxy.getIp(), proxy.getPort()))
					.setCookieSpec(CookieSpecs.STANDARD).build();
			request.setConfig(requestConfig);
			//封装好的httpclient获得page对象
			Page page = ZhiHuHttpClient.getInstance().getWebPage(request);
			long endTime = System.currentTimeMillis();
			//打印日子字符串
			String logStr = Thread.currentThread().getName() + " " + proxy.getProxyStr() +
                    "  executing request " + page.getUrl()  + " response statusCode:" + page.getStatusCode() +
                    "  request cost time:" + (endTime - startTime) + "ms";
			if(page==null||page.getStatusCode()!=200){
				logger.warn(logStr);
				return;
			}
			request.releaseConnection();
			
			logger.debug(proxy.toString() + "---------" + page.toString());
			if(!ProxyPool.proxySet.contains(proxy)){
				logger.debug(proxy.toString()+"------代理可用------请求耗时："+(endTime-startTime)+"ms");
				ProxyPool.LOCK.writeLock().lock();
				try{
					ProxyPool.proxySet.add(proxy);
				}finally{
					ProxyPool.LOCK.writeLock().unlock();
				}
				ProxyPool.proxyQueue.add(proxy);
			}
		} catch (IOException e) {
			logger.debug("IOExcption:",e);
		}finally{
			if(request!=null){
				request.releaseConnection();
			}
		}
	}
}
