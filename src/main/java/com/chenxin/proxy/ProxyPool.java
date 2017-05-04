package com.chenxin.proxy;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.chenxin.core.util.Constants;
import com.chenxin.proxy.entity.Direct;
import com.chenxin.proxy.entity.Proxy;
import com.chenxin.proxy.site.Ip181ProxyListPageParser;
import com.chenxin.proxy.site.Ip66ProxyListPageParser;
import com.chenxin.proxy.site.MimiipProxyListPageParser;
import com.chenxin.proxy.site.XicidailiProxyListPageParser;

/**
 * 代理池，代理集合
 * 
 * @author j
 *
 */
public class ProxyPool {
	/**
	 * proxySet读写锁
	 */
	public final static ReadWriteLock LOCK = new ReentrantReadWriteLock();
	public final static Set<Proxy> proxySet = new HashSet<Proxy>();

	/**
	 * 代理池延迟队列
	 */
	public final static DelayQueue<Proxy> proxyQueue = new DelayQueue<Proxy>();
	public final static Map<String, Class> proxyMap = new HashMap<String, Class>();

	static {
		int pages = 8;
		for (int i = 0; i <= pages; i++) {
			  proxyMap.put("http://www.xicidaili.com/wt/" + i + ".html", XicidailiProxyListPageParser.class);
	            proxyMap.put("http://www.xicidaili.com/nn/" + i + ".html", XicidailiProxyListPageParser.class);
	            proxyMap.put("http://www.xicidaili.com/wn/" + i + ".html", XicidailiProxyListPageParser.class);
	            proxyMap.put("http://www.xicidaili.com/nt/" + i + ".html", XicidailiProxyListPageParser.class);
	            proxyMap.put("http://www.ip181.com/daili/" + i + ".html", Ip181ProxyListPageParser.class);
	            proxyMap.put("http://www.mimiip.com/gngao/" + i, MimiipProxyListPageParser.class);//高匿
	            proxyMap.put("http://www.mimiip.com/gnpu/" + i, MimiipProxyListPageParser.class);//普匿
	            proxyMap.put("http://www.66ip.cn/" + i + ".html", Ip66ProxyListPageParser.class);
	            for(int j = 1; j < 34; j++){
	                proxyMap.put("http://www.66ip.cn/areaindex_" + j + "/" + i + ".html", Ip66ProxyListPageParser.class);
	            }
	        }
	        proxyQueue.add(new Direct(Constants.TIME_INTERVAL));
		}
}


