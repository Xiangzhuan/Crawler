package com.chenxin.csdn.test;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.chenxin.core.util.HttpClientUtil;
import com.chenxin.spider.entity.Page;

public class Main {
	
	public static void main(String[] args) {
		HttpGet request = new HttpGet("http://blog.csdn.net/qq_26566331/article/list/2");
		//HttpHost proxy = new HttpHost("106.46.2.191", 808);
		request.setConfig(HttpClientUtil.getRequestConfigBuilder().build());
		
		try {
			String response = HttpClientUtil.getWebPage(request);
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
