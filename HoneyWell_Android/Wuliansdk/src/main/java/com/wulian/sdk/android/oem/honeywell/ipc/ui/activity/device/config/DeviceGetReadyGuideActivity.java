/**
 * Project Name:  iCam
 * File Name:     V2DeviceGetReadyActivity.java
 * Package Name:  com.wulian.icam.view.device
 * @Date:         2015年6月29日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.enums.DeviceType;
import com.wulian.sdk.android.oem.honeywell.ipc.model.ConfigWiFiInfoModel;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.DialogUtils;

/**
 * @ClassName: DeviceGetReadyGuideActivity
 * @Function: 摄像头准备界面
 * @Date: 2015年6月29日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class DeviceGetReadyGuideActivity extends BaseFragmentActivity implements
		OnClickListener {
	private TextView tv_tips;
	private ImageView iv_device_type;
	private Button btn_next_step;// 听到提示音按钮
	private LinearLayout ll_choose_config_network_way;
	private TextView tv_help;// 没有听到提示音

	private ConfigWiFiInfoModel infoData;
	private String deviceId;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_device_reset_guide);
		mBaseView.bindView();
		mBaseView.setTitleView(R.string.config_device_getready);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		tv_tips = (TextView) findViewById(R.id.tv_tips);
		btn_next_step = (Button) findViewById(R.id.btn_next_step);
		iv_device_type = (ImageView) findViewById(R.id.iv_device_type);
		tv_help = (TextView) findViewById(R.id.tv_help);
		ll_choose_config_network_way=(LinearLayout) findViewById(R.id.ll_choose_config_network_way);
		tv_help.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_help.setVisibility(View.VISIBLE);
		ll_choose_config_network_way.setVisibility(View.GONE);
		btn_next_step.setVisibility(View.VISIBLE);
	}

	private void initData() {
		infoData = getIntent().getParcelableExtra("configInfo");
		deviceId = infoData.getDeviceId();
		if (TextUtils.isEmpty(deviceId) || infoData == null) {
			this.finish();
		}
		handleDevice();
	}

	private void setListener() {
		btn_next_step.setOnClickListener(this);
		tv_help.setOnClickListener(this);
	}

	private void handleDevice() {
		btn_next_step.setText(getResources().getString(
				R.string.config_already_hear_tip_voice));
		tv_help.setText(getResources().getString(
				R.string.config_not_hear_tip_voice));

		DeviceType type = DeviceType.getDevivceTypeByDeviceID(deviceId);
		switch (type) {
		case INDOOR:
			iv_device_type.setImageResource(R.drawable.icon_04_device_sys);
			tv_tips.setText(Html.fromHtml(getResources().getString(
					R.string.config_click_device_sys_button)));
			break;
		case OUTDOOR:
			iv_device_type.setImageResource(R.drawable.icon_02_device_set);
			tv_tips.setText(Html.fromHtml(getResources().getString(
					R.string.config_click_device_sys_button)));
			break;
		case SIMPLE:
		case SIMPLE_N:
			iv_device_type.setImageResource(R.drawable.icon_03_device_set);
			tv_tips.setText(Html.fromHtml(getResources().getString(
					R.string.config_click_device_set_button)));
			break;
		case INDOOR2:
			iv_device_type.setImageResource(R.drawable.icon_04_device_sys);
			tv_tips.setText(Html.fromHtml(getResources().getString(
					R.string.config_click_device_sys_button)));
			break;
		case DESKTOP:
			iv_device_type.setImageResource(R.drawable.icon_03_device_set);
			tv_tips.setText(Html.fromHtml(getResources().getString(
					R.string.config_click_device_set_button)));
			break;
		default:
			CustomToast.show(this, R.string.config_not_support_device);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_next_step) {
				Intent intent = new Intent(this, BarCodeSettingActivity.class);
				intent.putExtra("configInfo", infoData);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
			this.finish();
		} else if (id == R.id.tv_help) {
			DialogUtils.showCommonInstructionsWebViewTipDialog(this,
					getResources()
							.getString(R.string.config_not_hear_tip_voice),
					"no_voice");
		}
	}

}
