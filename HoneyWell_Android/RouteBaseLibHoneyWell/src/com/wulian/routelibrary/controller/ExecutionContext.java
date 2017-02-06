/**
 * Project Name:  RouteBaseLibV2
 * File Name:     ExecutionContext.java
 * Package Name:  com.wulian.routelibrary.controller
 * @Date:         2016年5月31日
 * Copyright (c)  2016, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.controller;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * @ClassName: ExecutionContext
 * @Function: TODO
 * @Date: 2016年5月31日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class ExecutionContext {
	private OkHttpClient mClient;

	private volatile boolean isCancelled;

	private volatile Call call;

	public ExecutionContext(OkHttpClient client) {
		this.mClient = client;
	}

	public void setClient(OkHttpClient client) {
		this.mClient = client;
	}

	public OkHttpClient getClient() {
		return this.mClient;
	}


	public void cancel() {
		if (call != null) {
			call.cancel();
		}
		isCancelled = true;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	public void setCall(Call call) {
		this.call = call;
	}
}
