/**
 * Project Name:  RouteBaseLibV2
 * File Name:     RouteRequestTask.java
 * Package Name:  com.wulian.routelibrary.controller
 * @Date:         2016年5月31日
 * Copyright (c)  2016, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.http;

import android.text.TextUtils;

import com.wulian.routelibrary.ConfigLibrary;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteErrorCode;
import com.wulian.routelibrary.controller.ExecutionContext;
import com.wulian.routelibrary.controller.TaskResultListener;
import com.wulian.routelibrary.exception.ClientException;
import com.wulian.routelibrary.exception.ServiceException;
import com.wulian.routelibrary.utils.LibraryLoger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @ClassName: RouteRequestTask
 * @Function: TODO
 * @Date: 2016年5月31日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class RouteHttpRequestTask<T extends Void> implements Callable<T> {
	private OkHttpClient mOkHttpClient;
	private RouteApiType mApiType;
	private TaskResultListener mListener;
	private ExecutionContext mExecutionContext;
	private HashMap<String, String> mParams;
	private String mEndPoint;
	private String mUserAgent;

	public RouteHttpRequestTask(String serverURL, String userAgent,
			RouteApiType api, TaskResultListener listener,
			HashMap<String, String> params, ExecutionContext execution) {
		this.mEndPoint = serverURL;
		this.mUserAgent = userAgent;
		this.mApiType = api;
		this.mListener = listener;
		this.mParams = params;
		this.mOkHttpClient = execution.getClient();
		this.mExecutionContext = execution;
	}

	@Override
	public T call() throws Exception {
		Response response = null;
		Exception exception = null;
		Call call = null;
		if (mExecutionContext.isCancelled()) {
			throw new InterruptedIOException("This task is cancelled!");
		}
		Request.Builder requestBuilder = new Request.Builder();
		String URL = getEndPoint() + mApiType.getmURL();
		LibraryLoger.d("URL is:" + URL);
		requestBuilder.addHeader("User-Agent", mUserAgent);
		switch (mApiType.getRequestType()) {
		case HTTP_POST:
			requestBuilder = requestBuilder.url(URL);
			requestBuilder.post(formPostParamsData());
			break;
		case HTTP_GET:
			requestBuilder = requestBuilder.url(formGetParamsData(URL));
			requestBuilder = requestBuilder.get();
			break;
		case HTTP_DELETE:
			requestBuilder = requestBuilder.url(URL);
			requestBuilder.delete(formDeleteParamsData());
			break;
		default:
			break;
		}
		try {
			call = mOkHttpClient.newCall(requestBuilder.build());
			mExecutionContext.setCall(call);
			response = call.execute();
		} catch (IOException e) {
			exception = new ClientException(e.getMessage(),
					RouteErrorCode.INVALID_IO);
		} catch (Exception e) {
			exception = new ClientException(e.getMessage(),
					RouteErrorCode.UNKNOWN_ERROR);
		}
		if (exception == null) {
			LibraryLoger.d("code is:" + response.code() + ";protocol:"
					+ response.protocol().toString());
		} else {
			LibraryLoger.d("exception is:" + exception.getMessage());
		}
		if (exception == null
				&& (response.code() == 203 || response.code() >= 300)) {
			exception = new ServiceException(response.code(),
					"Service exception!");
		} else if (exception == null) {
			if (mListener != null && !mExecutionContext.isCancelled()) {
				String responseBody = response.body().string();
				RouteErrorCode errorCode = parseJsonData(responseBody);
				switch (errorCode) {
				case SUCCESS:
					mListener.OnSuccess(mApiType, responseBody);
					break;
				default:
					mListener.OnFail(mApiType, new ClientException(
							responseBody, errorCode));
					break;
				}
			}
			safeCloseResponse(response);
			return null;
		}
		safeCloseResponse(response);
		mListener.OnFail(mApiType, exception);
		return null;
	}

	private String getEndPoint1() {
		if (TextUtils.isEmpty(mEndPoint)) {
			if(LibraryLoger.getLoger()) {
				return ConfigLibrary.HTTPS_PROTOCOL
						+ ConfigLibrary.TEST_HTTPS_SERVER_URL;
			}else {
				return ConfigLibrary.HTTPS_PROTOCOL
						+ ConfigLibrary.HTTPS_SERVER_URL;
			}
		}
		return mEndPoint;
	}
	//打开正式服务器日志,测试服务器不能切换
	private String getEndPoint() {
		if (TextUtils.isEmpty(mEndPoint)) {

				return ConfigLibrary.HTTPS_PROTOCOL
						+ ConfigLibrary.HTTPS_SERVER_URL;

		}
		return mEndPoint;
	}

	private void safeCloseResponse(Response response) {
		try {
			response.body().close();
		} catch (Exception e) {
		}
	}
	private FormBody formDeleteParamsData() {
		FormBody.Builder body = new FormBody.Builder();
		if (mParams != null) {

			Iterator<Entry<String, String>> iter = mParams.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				if (!TextUtils.isEmpty(entry.getValue())) {
					body.add(entry.getKey(), entry.getValue());// Post 请求不能编码
				}
			}
		}
		return body.build();
	}

	private FormBody formPostParamsData() {
		FormBody.Builder body = new FormBody.Builder();
		if (mParams != null) {
			Iterator<Entry<String, String>> iter = mParams.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				LibraryLoger.d("The params is:  " + entry.getKey()
						+ ";Value is:" + entry.getValue());
				if (!TextUtils.isEmpty(entry.getValue())) {
					body.add(entry.getKey(), entry.getValue());// Post 请求不能编码
				}
			}
		}
		return body.build();
	}

	private String formGetParamsData(String url)
			throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		sb.append(url);
		if (mParams != null) {
			sb.append("?");
			Iterator<Entry<String, String>> iter = mParams.entrySet()
					.iterator();
			boolean isFirstParam = true;
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				if (!TextUtils.isEmpty(entry.getValue())) {
					if (!isFirstParam) {
						sb.append("&");
					} else {
						isFirstParam = false;
					}
					// Get 请求可以编码
					sb.append(URLEncoder.encode(entry.getKey(),
							ConfigLibrary.ENCODING));
					sb.append("=");
					String value = entry.getValue();
					sb.append(URLEncoder.encode(value, ConfigLibrary.ENCODING));
				}
			}
		}
		return sb.toString();
	}

	private RouteErrorCode parseJsonData(String jsonData) {
		try {
			JSONObject dataJson = new JSONObject(jsonData);
			int status = dataJson.optInt("status");
			if (status == 1) {
				return RouteErrorCode.SUCCESS;
			} else {
				int code = dataJson.optInt("error_code");
				for (RouteErrorCode mErrorCode : RouteErrorCode.values()) {
					if (mErrorCode.getErrorCode() == code) {
						return mErrorCode;
					}
				}
				return RouteErrorCode.UNKNOWN_ERROR;
			}
		} catch (JSONException e) {
		}
		return RouteErrorCode.UNKNOWN_ERROR;
	}
}
