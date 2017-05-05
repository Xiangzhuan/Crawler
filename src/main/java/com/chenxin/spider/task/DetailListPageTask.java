package com.chenxin.spider.task;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.log4j.Logger;

import com.chenxin.core.parse.ListPageParser;
import com.chenxin.core.util.Config;
import com.chenxin.core.util.Constants;
import com.chenxin.core.util.SimpleInvocationHandler;
import com.chenxin.spider.ZhiHuHttpClient;
import com.chenxin.spider.entity.Page;
import com.chenxin.spider.entity.User;
import com.chenxin.spider.parser.ZhiHuUserListPageParser;

/**
 * 解析页面列表 线程
 * 找到新的url，继续解析
 * @author j
 *
 */
public class DetailListPageTask extends AbstractPageTask {
	
	private static Logger logger = Logger.getLogger(DetailListPageTask.class);
	private static ListPageParser proxyUserListPageParser;
	
	/**
     * Thread-数据库连接
     */
    private static Map<Thread, Connection> connectionMap = new ConcurrentHashMap<>();
    
    static {
    	//代理创建，记录时间
        proxyUserListPageParser = getProxyUserListPageParser();
    }
    
    public DetailListPageTask(HttpRequestBase request, boolean proxyFlag) {
        super(request, proxyFlag);
    }
	

	@Override
	void retry() {
		zhiHuHttpClient.getDetailListPageThreadPool().execute(new DetailListPageTask(request, Config.isProxy));
	}
	

	@Override
	void handle(Page page) {
		List<User> list = proxyUserListPageParser.parseListPage(page);
		for(User u :list){
			logger.info("解析用户成功！"+u.toString());
			if(Config.dbEnable){
				//将数据插入数据库
			}else if(!Config.dbEnable || zhiHuHttpClient.getDetailListPageThreadPool().getActiveCount() == 1){
				zhiHuHttpClient.parseUserCount.incrementAndGet();
				for(int j = 0; j < u.getFollowees(); j++){
					String nextUrl = String.format(Constants.USER_FOLLOWEES_URL, u.getUserToken(), j * 20);
					HttpGet request = new HttpGet(nextUrl);
					request.setHeader("authorization", "oauth " + ZhiHuHttpClient.getAuthorization());
					zhiHuHttpClient.getDetailListPageThreadPool().execute(new DetailListPageTask(request, true));
				}
			}
		}
		
	}
	
	/**
	 * 代理类
	 * @return
	 */
	private static ListPageParser getProxyUserListPageParser() {
		ListPageParser userListPageParser = ZhiHuUserListPageParser.getInstance();
        InvocationHandler invocationHandler = new SimpleInvocationHandler(userListPageParser);
        ListPageParser proxyUserListPageParser = (ListPageParser) Proxy.newProxyInstance(userListPageParser.getClass().getClassLoader(),
                userListPageParser.getClass().getInterfaces(), invocationHandler);
        return proxyUserListPageParser;
	}
}
