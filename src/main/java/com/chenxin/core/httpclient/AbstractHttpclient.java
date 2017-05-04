package com.chenxin.core.httpclient;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.chenxin.core.util.HttpClientUtil;
import com.chenxin.spider.entity.Page;

public abstract class AbstractHttpclient {

	private Logger logger = Logger.getLogger(AbstractHttpclient.class);

	public InputStream getWebPageInputStream(String url) {
		try {
			CloseableHttpResponse response = HttpClientUtil.getResponse(url);
			return response.getEntity().getContent();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 返回Page对象
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public Page getWebPage(String url) throws IOException {
		return getWebPage(url, "UTF-8");
	}
	
	/**
	 * 返回page对象
	 * @param url
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	private Page getWebPage(String url, String charset) throws IOException {
		Page page = new Page();
		CloseableHttpResponse response = null;

		response = HttpClientUtil.getResponse(url);
		page.setStatusCode(response.getStatusLine().getStatusCode());
		page.setUrl(url);
		try {
			if (page.getStatusCode() == 200) {
				page.setHtml(EntityUtils.toString(response.getEntity(), charset));
			}
		} finally {
			response.close();
		}
		return page;
	}
	/**
	 * 返回page对象
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public Page getWebPage(HttpRequestBase request) throws IOException {
		CloseableHttpResponse response = null;
		response = HttpClientUtil.getResponse(request);
		Page page = new Page();
		page.setStatusCode(response.getStatusLine().getStatusCode());
		page.setHtml(EntityUtils.toString(response.getEntity()));
		page.setUrl(request.getURI().toString());
		return page;

	}

}
