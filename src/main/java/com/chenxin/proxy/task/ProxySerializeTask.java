package com.chenxin.proxy.task;

import org.apache.log4j.Logger;

import com.chenxin.core.util.Config;
import com.chenxin.core.util.HttpClientUtil;
import com.chenxin.core.util.ProxyUtil;
import com.chenxin.proxy.ProxyPool;
import com.chenxin.proxy.entity.Proxy;
import com.chenxin.spider.ZhiHuHttpClient;

public class ProxySerializeTask implements Runnable {
	
	private static Logger logger = Logger.getLogger(ProxySerializeTask.class);
	
	@Override
	public void run() {
		while(!ZhiHuHttpClient.isStop){
			try {
				Thread.sleep(1000*60*1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Proxy[] proxyArray = null;
			ProxyPool.LOCK.readLock().lock();
			try{
				
			proxyArray = new Proxy[ProxyPool.proxySet.size()];
			int i = 0;
			for(Proxy p:ProxyPool.proxySet){
				if(!ProxyUtil.isDiscardProxy(p)){
					proxyArray[i++] = p ;
				}
			}
			}finally{
				ProxyPool.LOCK.readLock().unlock();
			}
			
			HttpClientUtil.serializeObject(proxyArray, Config.proxyPath);
			logger.info("成功序列化" + proxyArray.length + "个代理");
			
		}
	}

}
