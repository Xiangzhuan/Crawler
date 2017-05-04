package com.chenxin.proxy.site;

import java.util.HashMap;
import java.util.Map;

import com.chenxin.proxy.ProxyListPageParser;

public class ProxyListPageParserFactory {
	
	private static Map<String, ProxyListPageParser> map = new HashMap<String, ProxyListPageParser>();
	public static ProxyListPageParser getProxyListPageParser(Class clazz){
		String parserName = clazz.getSimpleName();
		ProxyListPageParser proxyListPageParser = null;
		if(map.containsKey(parserName)){
			return map.get(parserName);
		}else{
			try {
				ProxyListPageParser parser = (ProxyListPageParser) clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
