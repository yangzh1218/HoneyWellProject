/**
 * Project Name:  RouteBaseLibV2
 * File Name:     RouteAsynTask.java
 * Package Name:  com.wulian.routelibrary.controller
 * @Date:         2016年5月31日
 * Copyright (c)  2016, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.controller;

import java.util.concurrent.Future;

/**
 * @ClassName: RouteAsynTask
 * @Function: TODO
 * @Date: 2016年5月31日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class RouteAsynTask<T extends Void> {
	private Future<T> future;

	private ExecutionContext context;

	private volatile boolean canceled;

	public static RouteAsynTask<Void> wrapRequestTask(Future<Void> future,
			ExecutionContext context) {
		RouteAsynTask<Void> asynTask = new RouteAsynTask<Void>();
		asynTask.future = future;
		asynTask.context = context;
		return asynTask;
	}

	/**
	 * 取消任务
	 */
	public void cancel() {
		canceled = true;
		if (context != null) {
			context.cancel();
		}
	}

	/**
	 * 检查任务是否已经完成
	 *
	 * @return
	 */
	public boolean isCompleted() {
		return future.isDone();
	}

	/**
	 * 任务是否已经被取消过
	 */
	public boolean isCanceled() {
		return canceled;
	}
}
