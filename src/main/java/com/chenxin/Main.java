package com.chenxin;

import com.chenxin.core.util.Config;
import com.chenxin.proxy.ProxyHttpClient;
import com.chenxin.spider.ZhiHuHttpClient;

public class Main {
	public static void main(String[] args) {
		ProxyHttpClient.getInstance().startCrawl();
		ZhiHuHttpClient.getInstance().startCrawl(Config.startURL);
	}

}
