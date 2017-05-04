package com.chenxin.proxy.site;

import java.util.ArrayList;
import java.util.List;








import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.chenxin.core.util.Constants;
import com.chenxin.proxy.ProxyListPageParser;
import com.chenxin.proxy.entity.Proxy;

public class XicidailiProxyListPageParser implements ProxyListPageParser {
	
	/**
	 * 解析代理网站
	 * @param html
	 * @return
	 */
	public List<Proxy> parse(String html){
		Document doc = Jsoup.parse(html);
		Elements elements = doc.select("table[ip=ip_list] tr[class]");
		List<Proxy> proxList = new ArrayList<Proxy>(elements.size());
		for(Element element :elements){
			String ip = element.select("td:eq(1)").first().text();
			String port  = element.select("td:eq(2)").first().text();
			String isAnonymouse = element.select("td:eq(4)").first().text();
			if(isAnonymouse.contains("匿")){
				proxList.add(new Proxy(ip, Integer.valueOf(port), Constants.TIME_INTERVAL));
			}
		}
		return proxList;
		
	}

}
