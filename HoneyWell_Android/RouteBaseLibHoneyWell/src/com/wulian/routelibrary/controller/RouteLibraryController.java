/**
 * Project Name:  RouteLibrary
 * File Name:     RouteLibraryController.java
 * Package Name:  com.wulian.routelibrary.controller
 * @Date:         2014-9-6
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.controller;

import android.content.Context;
import android.text.TextUtils;

import com.wulian.routelibrary.ConfigLibrary;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;
import com.wulian.routelibrary.http.HttpsUtils;
import com.wulian.routelibrary.http.RouteHttpRequestTask;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

/**
 * @ClassName: RouteLibraryController
 * @Function: 库文件控制器
 * @Date: 2014-9-6
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class RouteLibraryController {
	private static RouteLibraryController gInstance;// 全局变量
	private OkHttpClient innerClient;
	private String mUserAgent;
	private String mEndPoint;
	private String mAppSecret;
	private static ExecutorService executorService = Executors
			.newFixedThreadPool(ConfigLibrary.DEFAULT_BASE_THREAD_POOL_SIZE);

	/**
	 * 
	 * @MethodName getInstance
	 * @Function 获取此控制器
	 * @author Puml
	 * @date: 2014-9-9
	 * @email puml@wuliangroup.cn
	 * @return
	 */
	public static RouteLibraryController getInstance() {
		if (gInstance == null) {
			gInstance = new RouteLibraryController();
		}
		return gInstance;
	}

	public void initRouteLibrary(Context context, String secret) {
		if (TextUtils.isEmpty(secret)) {
			throw new IllegalArgumentException("Please input valid AppSecret");
		}
		this.mUserAgent = "honeywell";
		this.mAppSecret = secret;
		initOkHttp(context);
	}
	HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
	private void initOkHttp(Context context) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder()
				.followRedirects(false).followSslRedirects(false)
				.retryOnConnectionFailure(false).cache(null);
		builder.sslSocketFactory(HttpsUtils.initHttps(context));
		builder.hostnameVerifier(DO_NOT_VERIFY);
		Dispatcher dispatcher = new Dispatcher();
		dispatcher.setMaxRequests(ConfigLibrary.MAX_REQUESTS);
		builder.connectTimeout(ConfigLibrary.TIMEOUT, TimeUnit.MILLISECONDS)
				.readTimeout(ConfigLibrary.TIMEOUT, TimeUnit.MILLISECONDS)
				.writeTimeout(ConfigLibrary.TIMEOUT, TimeUnit.MILLISECONDS)
				.dispatcher(dispatcher);

		this.innerClient = builder.build();
	}

	private OkHttpClient getInnerClient() {
		return this.innerClient;
	}

	public RouteAsynTask<Void> V3Register(String username, String password,
									   TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_REGISTER,
				listener, RouteLibraryParams.V3Login(username, password,
				false), context);
		return doRequest(callable, context);
	}


	public RouteAsynTask<Void> V3PartnerRegister(String username,
			String password, TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent,
				RouteApiType.V3_PARTNER_REGISTER, listener,
				RouteLibraryParams.V3PartnerLogin(username, password,
						mAppSecret), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3PartnerLogin(String username, String password,
			TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_PARTNER_LOGIN,
				listener, RouteLibraryParams.V3PartnerLogin(username, password,
						mAppSecret), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3Server(TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_SERVER,
				listener, null, context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3Logout(String auth, TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_LOGOUT,
				listener, RouteLibraryParams.V3Logout(auth), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3AppFlag(String auth, String did,
											 TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_APP_FLAG,
				listener,
				RouteLibraryParams.V3AppFlag(auth, did), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3UserDevice(String auth, String did,
			String nick, String desc, TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_USER_DEVICE,
				listener,
				RouteLibraryParams.V3UserDevice(auth, did, nick, desc), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3UserDevices(String auth, String type,
			TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_USER_DEVICES,
				listener, RouteLibraryParams.V3UserDevices(auth, type), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3UserPassword(String auth, String password,
			TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_USER_PASSWORD,
				listener, RouteLibraryParams.V3UserPassword(auth, password),
				context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3BindCheck(String auth, String did,
			TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_BIND_CHECK,
				listener, RouteLibraryParams.V3BindCheck(auth, did), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3BindResult(String auth, String did,
			TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_BIND_RESULT,
				listener, RouteLibraryParams.V3BindResult(auth, did), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3BindUnbind(String auth, String did,
			TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_BIND_UNBIND,
				listener, RouteLibraryParams.V3BindUnbind(auth, did), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3TokenDownloadReplay(String auth, String did,
			String sdomain, TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent,
				RouteApiType.V3_TOKEN_DOWNLOAD_REPLAY, listener,
				RouteLibraryParams.V3TokenDownloadReplay(auth, did, sdomain),
				context);
		return doRequest(callable, context);
	}


	public RouteAsynTask<Void> V3Share(String auth, String did, String username,
											TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_SHARE,
				listener, RouteLibraryParams.V3Share(auth, did, username), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3Owners(String auth,
									   TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_DEVICE_OWNERS,
				listener, RouteLibraryParams.V3Owners(auth), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3ShareList(String auth, String did,
										TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_SHARE_LIST,
				listener, RouteLibraryParams.V3ShareList(auth, did), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3VersionStable(String search, int version,
										   TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_VERSION_STABLE,
				listener, RouteLibraryParams.V3VersionStable(search,version), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3ShareRequest(String auth, String did, String desc,
									   TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_SHARE_REQUEST,
				listener, RouteLibraryParams.V3ShareRequest(auth, did, desc), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3ShareResponse(String auth, String did, String username, String action,
									   TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_SHARE_RESPONSE,
				listener, RouteLibraryParams.V3ShareResponse(auth, did, username, action), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> V3UnShare(String auth, String did, String username,
									   TaskResultListener listener) {
		ExecutionContext context = new ExecutionContext(getInnerClient());
		Callable<Void> callable = new RouteHttpRequestTask<Void>(
				this.mEndPoint, this.mUserAgent, RouteApiType.V3_SHARE_UNSHARE,
				listener, RouteLibraryParams.V3UnShare(auth, did, username), context);
		return doRequest(callable, context);
	}

	public RouteAsynTask<Void> doRequest(Callable<Void> callable,
			ExecutionContext context) {
		checkEnv();
		return RouteAsynTask.wrapRequestTask(executorService.submit(callable),
				context);
	}

	/**
	 * 
	 * @MethodName setLibraryPath
	 * @Function 设置http,https路径
	 * @author Puml
	 * @date: 2014年10月8日
	 * @email puml@wuliangroup.cn
	 * @param serverHttpsPath
	 */
	public void setLibraryPath(String serverHttpsPath) {
		if (TextUtils.isEmpty(serverHttpsPath)) {
			throw new IllegalArgumentException("Please input valid server URL");
		}
		if (serverHttpsPath.startsWith(ConfigLibrary.HTTPS_PROTOCOL)) {
			this.mEndPoint = serverHttpsPath;
		} else {
			this.mEndPoint = ConfigLibrary.HTTPS_PROTOCOL + serverHttpsPath;
		}
	}

//	// 设置第一次运行参数
//	private void initVersionName(Context context) {
//		try {
//			ConfigLibrary.VERSION_NAME = LibraryPhoneStateUtil
//					.getVersionName(context);
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}
//	}

	private void checkEnv() {
		if (TextUtils.isEmpty(this.mAppSecret)) {
			throw new IllegalArgumentException(
					"Please set Server URL using setLibraryPath()");
		}
		if (TextUtils.isEmpty(this.mUserAgent)) {
			throw new IllegalArgumentException(
					"Please set User Agent using initRouteLibrary()");
		}
	}
}
