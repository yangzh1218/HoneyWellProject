/**
 * Project Name:  iCam
 * File Name:     DeviceDetailActivity.java
 * Package Name:  com.wulian.icam.view.detail
 * @Date:         2015年9月15日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.enums.DeviceType;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.model.DeviceDetailMsg;
import com.wulian.sdk.android.oem.honeywell.ipc.sip.SipFactory;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.XMLHandler;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.utils.WulianLog;

/**
 * @ClassName: DeviceDetailActivity
 * @Function: 摄像机详细信息界面
 * @Date: 2015年9月15日
 * @author: yuanjs
 * @email: JianSheng.Yuan@wuliangroup.com
 */
public class DeviceDetailActivity extends BaseFragmentActivity {
	private Device device;
	private TextView tv_device_type, tv_linked_wifi, tv_linked_wifi_strength,
			tv_linked_wifi_ip_address, tv_linked_wifi_mac_address,
			tv_device_id, tv_device_version;
	private ImageView iv_device_desc_bg;
	private LinearLayout ll_device_detail_msg;
	private int seq = 1;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_device_detail);
		mBaseView.bindView();
		mBaseView.setTitleView(R.string.setting_detail_device_title);
		initData();
		onSendSipRemoteAccess();
		initView();
	}

	private void initView() {
		tv_device_type = (TextView) this.findViewById(R.id.tv_device_type);
		tv_linked_wifi = (TextView) this.findViewById(R.id.tv_linked_wifi);
		tv_linked_wifi_strength = (TextView) this
				.findViewById(R.id.tv_linked_wifi_strength);
		tv_linked_wifi_ip_address = (TextView) this
				.findViewById(R.id.tv_linked_wifi_ip_address);
		tv_linked_wifi_mac_address = (TextView) this
				.findViewById(R.id.tv_linked_wifi_mac_address);
		tv_device_id = (TextView) this.findViewById(R.id.tv_device_id);
		tv_device_version = (TextView) this
				.findViewById(R.id.tv_device_version);
		iv_device_desc_bg = (ImageView) this
				.findViewById(R.id.iv_device_desc_bg);
		ll_device_detail_msg = (LinearLayout) this
				.findViewById(R.id.ll_device_detail_msg);
		DeviceType type = DeviceType.getDevivceTypeByDeviceID(device
				.getDid());
		switch (type) {
		case OUTDOOR:
		case SIMPLE_N:
		case SIMPLE:
			iv_device_desc_bg.setBackgroundResource(R.drawable.type03_bg);
			tv_device_type.setText(getResources().getString(
					R.string.setting_detail_device_03));
			break;
		case INDOOR:
		case INDOOR2:
			iv_device_desc_bg.setBackgroundResource(R.drawable.type04_bg);
			tv_device_type.setText(getResources().getString(
					R.string.setting_detail_device_04));
			break;
		case DESKTOP:
			iv_device_desc_bg.setBackgroundResource(R.drawable.type03_bg);
			tv_device_type.setText(getResources().getString(
					R.string.setting_detail_device_03));
			break;
		default:
			break;
		}
		tv_device_id.setText(device.getDid());
		if (device.getOnline() != 1) {
			tv_device_version.setText(getResources().getString(
					R.string.setting_detail_device_outline_version));
		}
		sendForDeviceDetailMsg();
	}

	private void sendForDeviceDetailMsg() {
		// 查询设备详细信息
		SipController.getInstance().sendMessage(
				device.getDid() + "@" + device.getSip_domain(),
				SipHandler.QueryDeviceDescriptionInfo(
						"sip:" + device.getDid() + "@"
								+ device.getSip_domain(), seq++),
				SipFactory.getInstance().getSipProfile());
	}

	private void showDeviceVersion() {
		// 查询设备版本
		SipController.getInstance().sendMessage(
				device.getDid() + "@" + device.getSip_domain(),
				SipHandler.QueryFirewareVersion(
						"sip:" + device.getDid() + "@"
								+ device.getSip_domain(), seq++),
				SipFactory.getInstance().getSipProfile());
	}


	@Override
	protected void sipDataReturn(boolean isSuccess, SipMsgApiType apiType,
			String xmlData, String from, String to) {
		super.sipDataReturn(isSuccess, apiType, xmlData, from, to);
		if (isSuccess) {
			WulianLog.e("xmlData", xmlData);
			switch (apiType) {
			case QUERY_FIREWARE_VERSION:
				try {
					String version = Utils.getParamFromXml(xmlData, "version");
//					System.out.println("----QUERY_FIREWARE_VERSION-->" + version);
//					tv_device_version.setText(version);
				} catch (NumberFormatException e) {
				}
				break;
			case QUERY_DEVICE_DESCRIPTION_INFO:
				WulianLog.e("QUERY_DEVICE_DESCRIPTION_INFO:", xmlData);
				DeviceDetailMsg detailMsg = XMLHandler
						.getDeviceDetailMsg(xmlData);
				if (detailMsg != null) {
					if (detailMsg.getWifi_ip() == null) {
						ll_device_detail_msg.setVisibility(View.GONE);
					} else if (detailMsg != null) {
						tv_linked_wifi.setText(detailMsg.getWifi_ssid());
						tv_linked_wifi_ip_address.setText(detailMsg
								.getWifi_ip());
						tv_linked_wifi_mac_address.setText(detailMsg
								.getWifi_mac());
						tv_linked_wifi_strength.setText(detailMsg
								.getWifi_signal() + "%");
						tv_device_version.setText(detailMsg.getVersion());
//						System.out.println("----- QUERY_DEVICE_DESCRIPTION_INFO -->" + detailMsg.getVersion());
					}
				} else {
					CustomToast.show(this, R.string.common_send_fail);
				}
				break;
			default:
				break;
			}
		}
	}

	private void initData() {
		device = (Device) getIntent().getExtras().getParcelable("device");
	}


}
