package com.chenxin.proxy.site;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.chenxin.core.util.Constants;
import com.chenxin.proxy.ProxyListPageParser;
import com.chenxin.proxy.entity.Proxy;

/**
 * 占时不能用了
 * @author j
 *
 */
public class MimiipProxyListPageParser implements ProxyListPageParser {
	public List<Proxy> parse(String hmtl) {
		Document document = Jsoup.parse(hmtl);
		Elements elements = document.select("table[class=list] tr");
		List<Proxy> proxyList = new ArrayList<>(elements.size());
		for (int i = 1; i < elements.size(); i++) {
			String isAnonymous = elements.get(i).select("td:eq(3)").first().text();
			if (isAnonymous.contains("匿")) {
				String ip = elements.get(i).select("td:eq(0)").first().text();
				String port = elements.get(i).select("td:eq(1)").first().text();
				proxyList.add(new Proxy(ip, Integer.valueOf(port),
						Constants.TIME_INTERVAL));
			}
		}
		return proxyList;
	}
}
