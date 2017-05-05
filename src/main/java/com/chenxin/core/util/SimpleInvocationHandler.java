package com.chenxin.core.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * 动态代理创建对象，可打印日志并记录时间
 * @author j
 *
 */
public class SimpleInvocationHandler implements InvocationHandler {

	private static Logger logger = Logger.getLogger(SimpleInvocationHandler.class);
	
	private Object target;
	
	public SimpleInvocationHandler() {
        super();
    }
	
	public SimpleInvocationHandler(Object target) {
        super();
        this.target = target;
    }
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		long startTime = System.currentTimeMillis();
		Object result = method.invoke(target, args);
		long endTime = System.currentTimeMillis();
		logger.debug(target.getClass().getSimpleName() + " " + method.getName() + " cost time:" + (endTime - startTime) + "ms");
        return result;
		
	}

}
