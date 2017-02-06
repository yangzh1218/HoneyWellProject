/**
 * Project Name:  iCam
 * File Name:     ConfigDeviceFirstPageActivity.java
 * Package Name:  com.wulian.icam.view.device.config
 *
 * @Date: 2015年10月10日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.controller.RouteLibraryController;
import com.wulian.routelibrary.controller.TaskResultListener;
import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.common.Common;
import com.wulian.sdk.android.oem.honeywell.ipc.model.ConfigWiFiInfoModel;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.DialogUtils;

import org.json.JSONObject;

import java.util.Locale;

/**
 * @ClassName: ConfigDeviceFirstPageActivity
 * @Function: 配置摄像机Wifi引导页
 * @Date: 2015年10月10日
 * @author Yanmin
 * @email min.yan@wuliangroup.cn
 */
public class ConfigDeviceFirstPageActivity extends BaseFragmentActivity implements CallBack {
    private Button btn_start;
    private ImageView iv_advent;
    private TextView tv_advent;
    private String deviceId;
    private Dialog queryDialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_add_device_first_page);
        mBaseView.bindView();
        mBaseView.setTitleView(R.string.setting_wifi_setting);
        initView();
        initData();
        Interface.getInstance().setContext(this);
        Interface.getInstance().setCallBack(this);
    }

    private void initView() {
        btn_start = (Button) findViewById(R.id.btn_start);
        iv_advent = (ImageView) findViewById(R.id.iv_advent);
        tv_advent = (TextView) findViewById(R.id.tv_info);

        iv_advent.setImageResource(R.drawable.config_wifi_advent);
        tv_advent.setText(R.string.config_config_wifi_advent);
        btn_start.setText(R.string.common_start_setting);
        btn_start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//				Intent it = new Intent(ConfigDeviceFirstPageActivity.this,
//						DeviceIdQueryActivity.class);
//				it.putExtra("msgData", deviceId);
//				it.putExtra("resetWifi", true);
//				it.putExtra("launchFrom", "deviceSetting");
//				startActivity(it);
//				finish();
                queryDialog = DialogUtils.showQueryIdDialog(ConfigDeviceFirstPageActivity.this);
                deviceId = deviceId.toLowerCase(Locale.getDefault());
                if (deviceId != null && deviceId.length() == 20) {// 6+2+12
                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getDeviceFlag();
                        }
                    };
                    DialogUtils.initQueryIdDialog(queryDialog, true, onClickListener);
                    getDeviceFlag();
                } else {
                    CustomToast.show(ConfigDeviceFirstPageActivity.this, R.string.config_error_deviceid);
                    queryDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(queryDialog != null){
            queryDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if(queryDialog != null){
            queryDialog.dismiss();
        }
        super.onDestroy();
    }

    private void getDeviceFlag() {
        Interface.getInstance().V3AppFlag(deviceId);
    }

    private void initData() {
        deviceId = getIntent().getStringExtra("deviceId");
    }

    @Override
    public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String json) {
        LibraryLoger.d("jsonData is:" + json);
        switch (apiType) {
            case V3_APP_FLAG:
                boolean isQrConnect = false;
                isQrConnect = Common.handleBarCodeJson(ConfigDeviceFirstPageActivity.this, json);
                if (TextUtils.isEmpty(deviceId) == false) {
                    isQrConnect = Common.getQrConnectFromDeviceId(deviceId);
                }
                ConfigWiFiInfoModel configWiFiInfoModel = Common.getConfigWiFiInfoModel("", isQrConnect, deviceId);
                Intent it1 = new Intent(this, DeviceLaunchGuideActivity.class);
                it1.putExtra("configInfo", configWiFiInfoModel);
                it1.putExtra("resetWifi", true);
                startActivity(it1);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {

    }
}
