package com.chenxin.spider.task;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;

import com.chenxin.core.parse.DetailPageParser;
import com.chenxin.core.util.Config;
import com.chenxin.core.util.SimpleInvocationHandler;
import com.chenxin.spider.ZhiHuHttpClient;
import com.chenxin.spider.entity.Page;
import com.chenxin.spider.entity.User;
import com.chenxin.spider.parser.ZhiHuNewUserDetailPageParser;
/**
 * 知乎用户详情页task
 * 下载成功解析出用户信息并添加到数据库，获取该用户的关注用户list url，添加到ListPageDownloadThreadPool
 */
public class DetailPageTask extends AbstractPageTask {
	
	private static Logger logger = Logger.getLogger(DetailPageTask.class);
	private static DetailPageParser proxyDetailPageParser;
	
	static {
        proxyDetailPageParser = getProxyDetailParser();
    }
	
	public DetailPageTask(String url, boolean proxyFlag) {
		super(url, proxyFlag);
	}
	
	@Override
	void retry() {
		zhiHuHttpClient.getDetailPageThreadPool().execute(new DetailPageTask(url, Config.isProxy));
	}

	@Override
	void handle(Page page) {
		DetailPageParser parser = null;
		// parser = ZhiHuNewUserDetailPageParser.getInstance();
		parser = proxyDetailPageParser;
		User u = parser.parseDetailPage(page);
		logger.info("解析用户成功:" + u.toString());
		if (Config.dbEnable) {
			// ZhiHuDAO.insertUser(u);
			// zhiHuDao1.insertUser(u);
		}
		zhiHuHttpClient.parseUserCount.incrementAndGet();
		for (int i = 0; i < u.getFollowees() / 20 + 1; i++) {
			String userFolloweesUrl = formatUserFolloweesUrl(u.getUserToken(),
					20 * i);
			handleUrl(userFolloweesUrl);
		}
	}

	private void handleUrl(String userFolloweesUrl) {
		HttpGet request = new HttpGet(url);
        request.setHeader("authorization", "oauth " + ZhiHuHttpClient.getAuthorization());
        if(!Config.dbEnable){
            zhiHuHttpClient.getListPageThreadPool().execute(new ListPageTask(request, Config.isProxy));
            return ;
        }
        zhiHuHttpClient.getListPageThreadPool().execute(new ListPageTask(request, Config.isProxy));
		
	}

	private String formatUserFolloweesUrl(String userToken, int offset) {
		String url = "https://www.zhihu.com/api/v4/members/" + userToken + "/followees?include=data%5B*%5D.answer_count%2Carticles_count%2Cfollower_count%2C" +
                "is_followed%2Cis_following%2Cbadge%5B%3F(type%3Dbest_answerer)%5D.topics&offset=" + offset + "&limit=20";
        return url;
	}

	/**
     * 代理类
     * @return
     */
    private static DetailPageParser getProxyDetailParser(){
        DetailPageParser detailPageParser = ZhiHuNewUserDetailPageParser.getInstance();
        InvocationHandler invocationHandler = new SimpleInvocationHandler(detailPageParser);
        DetailPageParser proxyDetailPageParser = (DetailPageParser) Proxy.newProxyInstance(detailPageParser.getClass().getClassLoader(),
                detailPageParser.getClass().getInterfaces(), invocationHandler);
        return proxyDetailPageParser;
    }
}
 