/**
 * Project Name:  iCam
 * File Name:     MessageInfoService.java
 * Package Name:  com.wulian.icam.service
 * @Date:         2015年3月30日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;
import com.wulian.routelibrary.controller.RouteLibraryController;
import com.wulian.routelibrary.controller.TaskResultListener;
import com.wulian.sdk.android.oem.honeywell.ipc.APPConfig;
import com.wulian.sdk.android.oem.honeywell.ipc.model.UserInfo;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.internal.framed.ErrorCode;

/**
 * @Function 从服务器轮询下载授权消息=>极光推送
 * @date: 2015年6月11日
 * @author Wangjj
 */

public class AuthMsgService extends Service implements TaskResultListener {
	private Timer timer;
	private TimerTask mTimerTask;
	private String lastAuthTime;
	private WifiManager mWifiManager;

	public class MsgBinder extends Binder {
		public AuthMsgService getService() {
			return AuthMsgService.this;
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		return new MsgBinder();
	}

	public void setAuthLastTime(String time) {
		lastAuthTime = time;
	}

//	private void getAuthNotices() {
//		if (TextUtils.isEmpty(lastAuthTime)) {
//			getAuthNoticesAll();
//		} else {
//			getAuthNoticesByLastTime(lastAuthTime);
//		}
//	}
//
//	private void getAuthNoticesAll() {
//
//		UserInfo userInfo = ICamGlobal.getInstance().getUserinfo();
//
//		// 仅在有效期内发送请求
//
//		if (userInfo == null
//				|| (userInfo != null && System.currentTimeMillis() >= userInfo
//						.getExpires() * 1000L)) {
//			HttpRequestModel requestModel = new HttpRequestModel();
//			requestModel.setmApiType(RouteApiType.BINDING_AUTH_DOWNLOAD);
//			requestModel.setmListener(this);
//			requestModel.setmParamsMap(RouteLibraryParams
//					.BindingNotices(userInfo.getAuth()));
//			reLoginAndCallBack(requestModel);
//		} else {
//			doRequest(RouteApiType.BINDING_AUTH_DOWNLOAD,
//					RouteLibraryParams.BindingNotices(userInfo.getAuth()));
//		}
//
//		/**
//		 * if (userInfo != null && System.currentTimeMillis() <
//		 * userInfo.getExpires() * 1000L) {
//		 * doRequest(RouteApiType.BINDING_AUTH_DOWNLOAD,
//		 * RouteLibraryParams.BindingNotices(userInfo.getAuth())); } else {
//		 * reLogin(); }
//		 **/
//
//	}
//
//	private void getAuthNoticesByLastTime(String update_time) {
//		Utils.sysoInfo("MessageInfoService======》" + update_time);
//		UserInfo userInfo = ICamGlobal.getInstance().getUserinfo();
//		// 仅在有效期内发送请求
//
//		if (userInfo == null
//				|| (userInfo != null && System.currentTimeMillis() >= userInfo
//						.getExpires() * 1000L)) {
//			HttpRequestModel requestModel = new HttpRequestModel();
//			requestModel.setmApiType(RouteApiType.BINDING_AUTH_DOWNLOAD);
//			requestModel.setmListener(this);
//			requestModel.setmParamsMap(RouteLibraryParams.BindingNotices(
//					ICamGlobal.getInstance().getUserinfo().getAuth(),
//					update_time));
//			reLoginAndCallBack(requestModel);
//		} else {
//			doRequest(
//					RouteApiType.BINDING_AUTH_DOWNLOAD,
//					RouteLibraryParams.BindingNotices(ICamGlobal.getInstance()
//							.getUserinfo().getAuth(), update_time));
//		}
//	}

	/**
	 * @Function 开始轮询授权消息的任务->基于推送
	 * @author Wangjj
	 * @date 2015年6月11日
	 */
	public void beginOauthTask() {
		timer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
					WifiInfo info = mWifiManager.getConnectionInfo();
					if (info != null
							&& info.getSSID() != null
							&& info.getSSID().contains(
									APPConfig.DEVICE_WIFI_SSID_PREFIX)) {
						return;
					}
				}
//				getAuthNotices();
			}
		};
		timer.schedule(mTimerTask, 0,
				APPConfig.BINDING_NOTICES_CYCLE_TIME * 1000);
		// timer.schedule(mTimerTask, 0);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		return super.onUnbind(intent);
	}

	public interface OauthMsgCallBack {// 回调：通过接口规范，将数据回传
		void updateOauthMsg(String oauthMsg);
	}

	private OauthMsgCallBack oauthMsgCallBack;

	public void setOauthMsgCallBack(OauthMsgCallBack oauthMsgCallBack) {
		this.oauthMsgCallBack = oauthMsgCallBack;
	}

	/**
	 * @Function 更新授权消息：数据库+界面 （service不好直接更新，通过接口回调）
	 * @author Wangjj
	 * @date 2015年6月11日
	 * @param json
	 */
	private void updateAuthMsg(String json) {
		if (oauthMsgCallBack != null) {
			oauthMsgCallBack.updateOauthMsg(json);
		}
	}

	@Override
	public void OnSuccess(RouteApiType apiType, String json) {
		try {
			JSONObject dataJson = new JSONObject(json);
			int status = dataJson.optInt("status");
			if (status == 1) {
				DataReturn(true, apiType, json);// 第3个参数为需要的json数据
			}
		} catch (JSONException e) {
			DataReturn(false, apiType, json);// 第3个参数为服务器返回的非json格式数据
		}
	}


	@Override
	public void OnFail(RouteApiType apiType, Exception exception) {

	}


	private void DataReturn(boolean success, RouteApiType apiType, String json) {
		if (success) {
			switch (apiType) {
			default:
				break;
			}
		}
	}

}
