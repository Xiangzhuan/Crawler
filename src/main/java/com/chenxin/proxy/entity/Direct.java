package com.chenxin.proxy.entity;

public class Direct extends Proxy {

	public Direct(String ip, int port, long timeInterval) {
		super(ip, port, timeInterval);
	}

	public Direct(long delayTime) {
		this("", 0, delayTime);
	}

}
