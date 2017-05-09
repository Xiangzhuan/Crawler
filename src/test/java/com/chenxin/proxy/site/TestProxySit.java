package com.chenxin.proxy.site;

import java.nio.charset.Charset;
import java.util.List;

import org.junit.Test;

import com.chenxin.proxy.ProxyHttpClient;
import com.chenxin.proxy.entity.Proxy;
import com.chenxin.spider.entity.Page;


public class TestProxySit {
	
	@Test
	public void testIp181Parse() throws Exception{
		System.out.println(Charset.defaultCharset().toString());
		Page page = ProxyHttpClient.getInstance().getWebPage("http://www.ip181.com/daili/1.html","gbk");
		List<Proxy> urlList = new Ip181ProxyListPageParser().parse(page.getHtml());
		System.out.println(urlList.size());
	}
	
	@Test
	public void testIp66Parse() throws Exception{
		Page page = ProxyHttpClient.getInstance().getWebPage("http://www.66ip.cn/index.html","UTF-8");
		System.out.println(page.getHtml());
	}
	
	@Test
	public void testMimiipParse() throws Exception{
		Page page = ProxyHttpClient.getInstance().getWebPage("http://www.mimiip.com/gngao/");
		System.out.println(page.getHtml());
	}
	
	@Test
	public void XicidailiParse() throws Exception{
		Page page = ProxyHttpClient.getInstance().getWebPage("http://www.xicidaili.com/nt/1.html");
		System.out.println(page.getHtml());
	}
	
}
