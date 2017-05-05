package com.chenxin.core.util;

import com.chenxin.proxy.entity.Proxy;

public class ProxyUtil {

	/**
	 * 是否丢弃代理 失败次数大于3，且失败率超过60%，丢弃
	 */
	public static boolean isDiscardProxy(Proxy proxy) {
		int succTime = proxy.getSuccessfulTimes();
		int failtime = proxy.getFailureTimes();
		if (failtime >= 3) {
			double failRate  = (failtime+0.0)/(succTime+failtime);
			if(failRate>0.6){
				return true;
			}
		}
		return false;
	}

}
