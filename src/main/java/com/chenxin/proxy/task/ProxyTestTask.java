package com.chenxin.proxy.task;

import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;

import com.chenxin.core.util.Constants;
import com.chenxin.proxy.entity.Proxy;
import com.chenxin.spider.ZhiHuHttpClient;
import com.chenxin.spider.entity.Page;

/**
 * 代理检测task
 * 通过访问知乎首页，能否正确相应
 * 将可用代理添加到DelayQueue延时队列中
 * @author j
 *
 */
public class ProxyTestTask implements Runnable {
	
	private final static Logger logger = Logger.getLogger(ProxyTestTask.class);
	private Proxy proxy;
	public ProxyTestTask(Proxy proxy){
		this.proxy = proxy;
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		HttpGet request = new HttpGet(Constants.INDEX_URL);
		
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Constants.TIMEOUT)
															.setConnectTimeout(Constants.TIMEOUT)
															.setConnectionRequestTimeout(Constants.TIMEOUT)
															.setProxy(new HttpHost(proxy.getIp(),proxy.getPort()))
															.setCookieSpec(CookieSpecs.STANDARD)
															.build();
		request.setConfig(requestConfig);
		
		
	}
	
	

}
