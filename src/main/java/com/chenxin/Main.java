package com.chenxin;

import com.chenxin.proxy.ProxyHttpClient;

public class Main {
	public static void main(String[] args) {
		ProxyHttpClient.getInstance().startCrawl();
	}

}
