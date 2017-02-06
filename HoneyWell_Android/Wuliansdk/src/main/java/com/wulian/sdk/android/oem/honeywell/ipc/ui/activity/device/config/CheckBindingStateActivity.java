/**
 * Project Name:  iCam
 * File Name:     CheckBindingStateActivity.java
 * Package Name:  com.wulian.icam.view.device.config
 * @Date:         2016年4月26日
 * Copyright (c)  2016, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.controller.RouteLibraryController;
import com.wulian.routelibrary.controller.TaskResultListener;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.enums.DeviceType;
import com.wulian.sdk.android.oem.honeywell.ipc.model.ConfigWiFiInfoModel;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.DeviceListActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.navigation.Navigator;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.DialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName: CheckBindingStateActivity
 * @Function: 检查绑定状态
 * @Date: 2016年4月26日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class CheckBindingStateActivity extends BaseFragmentActivity implements CallBack{
	private ImageView iv_oval_left_device;
	private ImageView iv_config_wifi_step_state;
	private AnimationDrawable mAnimation;
	private Dialog mExitDialog;

	private ConfigWiFiInfoModel mInfoData;
	private String originDeviceId;
	private static final long START_DELAY = 1000;
	private static final int BIND_RESULT_MSG = 1;
	private int mCurrentNum = 0;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_check_binding_state);
		mBaseView.bindView();
		mBaseView.setTitleView(R.string.config_link_camera);
		initView();
		initData();
		Interface.getInstance().setContext(this);
		Interface.getInstance().setCallBack(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mDrawHandler.postDelayed(mRunnable, START_DELAY);
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopAnimation(mAnimation);
	}

	@Override
	protected void onDestroy() {
		mDrawHandler.removeCallbacksAndMessages(null);
		myHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	private void initView() {
		iv_oval_left_device = (ImageView) findViewById(R.id.iv_oval_left_device);
		iv_config_wifi_step_state = (ImageView) findViewById(R.id.iv_config_wifi_step_state);
	}

	private void initData() {
		Bundle bd = getIntent().getExtras();
		mInfoData = bd.getParcelable("configInfo");
		originDeviceId = mInfoData.getDeviceId();
		if (mInfoData == null) {
			this.finish();
			return;
		}
		mAnimation = (AnimationDrawable) iv_config_wifi_step_state
				.getDrawable();
		handleDevice();
		myHandler.sendEmptyMessage(BIND_RESULT_MSG);
	}


	private void handleDevice() {
		DeviceType type = DeviceType.getDevivceTypeByDeviceID(originDeviceId);
		switch (type) {
		case INDOOR:
			iv_oval_left_device.setImageResource(R.drawable.icon_oval_device_4);
			break;
		case OUTDOOR:
			iv_oval_left_device.setImageResource(R.drawable.icon_oval_device_2);
			break;
		case SIMPLE:
		case SIMPLE_N:
			iv_oval_left_device.setImageResource(R.drawable.icon_oval_device_3);
			break;
		case INDOOR2:
			iv_oval_left_device.setImageResource(R.drawable.icon_oval_device_4);
			break;
		case DESKTOP:
			iv_oval_left_device.setImageResource(R.drawable.icon_oval_device_3);
			break;
		default:
			CustomToast.show(this, R.string.config_not_support_device);
			this.finish();
			break;
		}
	}

	private Handler mDrawHandler = new Handler();

	private Runnable mRunnable = new Runnable() {
		public void run() {
			startAnimation(mAnimation);
		}
	};

	private Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BIND_RESULT_MSG:
				if (mCurrentNum <= 15) {
					checkBindingState();
					mCurrentNum++;
					myHandler.sendEmptyMessageDelayed(BIND_RESULT_MSG, 6000);
				} else {
					jumpToResult(false);
				}
				break;
			default:
				break;
			}
		};
	};

	private void checkBindingState() {
		Interface.getInstance().BindResult(originDeviceId);
	}

	private void jumpToResult(boolean isSuccess) {
		myHandler.removeMessages(BIND_RESULT_MSG);
		Intent it = new Intent();
		it.putExtra("configInfo", mInfoData);
		if (!isSuccess) {
			it.setClass(this, DeviceConfigFailResultActivity.class);
		} else {
			it.setClass(this, DeviceConfigSuccessActivity.class);
		}
		startActivity(it);
		this.finish();
	}

	protected void startAnimation(final AnimationDrawable animation) {
		if (animation != null && !animation.isRunning()) {
			animation.run();
		}
	}

	protected void stopAnimation(final AnimationDrawable animation) {
		if (animation != null && animation.isRunning())
			animation.stop();
	}

	private void showExitDialog() {
		Resources rs = getResources();
		mExitDialog = DialogUtils.showCommonDialog(this, true,
				rs.getString(R.string.common_tip),
				rs.getString(R.string.config_is_exit_current_config),
				rs.getString(R.string.config_exit),
				rs.getString(R.string.common_cancel), new OnClickListener() {

					@Override
					public void onClick(View v) {
						int id = v.getId();
						if (id == R.id.btn_positive) {
							mExitDialog.dismiss();
							Navigator navigator = new Navigator(CheckBindingStateActivity.this);
							navigator.navigateToDeviceList(CheckBindingStateActivity.this);
							finish();
						} else if (id == R.id.btn_negative) {
							mExitDialog.dismiss();
						}
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			showExitDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String json) {
		switch (apiType) {
			case V3_BIND_RESULT:
				try {
					JSONObject jsonObject = new JSONObject(json);
					int result = jsonObject.isNull("result") ? 0 : jsonObject
							.getInt("result");
					if (result == 1) {
						jumpToResult(true);
					}
				} catch (JSONException e) {

				}
				break;
			default:
				break;
		}
	}

	@Override
	public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {
		CustomToast.show(this, R.string.config_query_device_fail);
	}
}
