package com.chenxin.core.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleThreadPoolExecutor extends ThreadPoolExecutor {

	private String threadPoolName;
	
	/**
	 * 
	 * @param corePoolSize 		 核心线程池大小
	 * @param maximumPoolSize   最大线程池大小
	 * @param keepAliveTime     线程池中超过corePoolSize数目的空闲线程最大存活时间；
	 * @param unit              keepAliveTime时间单位
	 * @param workQueue      	阻塞任务队列
	 * @param threadPoolName	线程池名
	 */
	public SimpleThreadPoolExecutor(int corePoolSize, 
									int maximumPoolSize,
									long keepAliveTime, 
									TimeUnit unit,
									BlockingQueue<Runnable> workQueue, 
									String threadPoolName) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		this.threadPoolName = threadPoolName;
	}
	
	/**
	 * 
	 * @param corePoolSize   	核心线程池大小
	 * @param maximumPoolSize	最大线程池大小
	 * @param keepAliveTime		线程池中超过corePoolSize数目的空闲线程最大存活时间；
	 * @param unit				时间单位
	 * @param workQueue			阻塞任务队列
	 * @param threadFactory		新建线程工厂
	 * @param threadPoolName
	 */
	public SimpleThreadPoolExecutor(int corePoolSize, 
									int maximumPoolSize,
									long keepAliveTime, TimeUnit unit,
									BlockingQueue<Runnable> workQueue, 
									ThreadFactory threadFactory,
									String threadPoolName) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory);
		this.threadPoolName = threadPoolName;
	}
	
	/**
	 * 
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 * @param unit
	 * @param workQueue
	 * @param handler   当提交任务数超过maxmumPoolSize+workQueue之和时，
	 * 						任务会交给RejectedExecutionHandler来处理
	 * @param threadPoolName 
	 */
	public SimpleThreadPoolExecutor(int corePoolSize, 
									int maximumPoolSize,
									long keepAliveTime, 
									TimeUnit unit,
									BlockingQueue<Runnable> workQueue,
									RejectedExecutionHandler handler, 
									String threadPoolName) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				handler);
		this.threadPoolName = threadPoolName;
	}
	
	/**
	 * 
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 * @param unit
	 * @param workQueue
	 * @param threadFactory
	 * @param handler
	 * @param threadPoolName
	 */
	public SimpleThreadPoolExecutor(int corePoolSize, 
									int maximumPoolSize,
									long keepAliveTime, 
									TimeUnit unit,
									BlockingQueue<Runnable> workQueue, 
									ThreadFactory threadFactory,
									RejectedExecutionHandler handler,
									String threadPoolName) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);
		this.threadPoolName = threadPoolName;
	}

	/**
	 * 修改thread name
	 * 
	 * @param t
	 * @param r
	 */
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		if (t.getName().startsWith("pool-")) {
			t.setName(t.getName().replaceAll("pool-\\d", this.threadPoolName));
		}
	}

}
