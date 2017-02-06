/**
 * Project Name:  iCam
 * File Name:     V2DeviceInfoConfigSuccessActivity.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2015年7月26日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.model.ConfigWiFiInfoModel;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.DeviceListActivity;

/**
 * @author Puml
 * @ClassName: DeviceConfigSuccessActivity
 * @Function: TODO
 * @Date: 2015年7月26日
 * @email puml@wuliangroup.cn
 */
public class DeviceConfigSuccessActivity extends BaseFragmentActivity {
    private Button btn_config_success;
    private TextView tv_config_wifi_success;
    private ConfigWiFiInfoModel mConfigInfo;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_device_config_success);
        mBaseView.bindView();
        mBaseView.setTitleView(R.string.common_result);
        initView();
        initData();
    }

    private void initView() {
        btn_config_success = (Button) findViewById(R.id.btn_config_success);
        tv_config_wifi_success = (TextView) findViewById(R.id.tv_config_wifi_success);
        btn_config_success.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(DeviceConfigSuccessActivity.this, DeviceListActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
                finish();
            }
        });
    }

    private void initData() {
        mConfigInfo = getIntent().getParcelableExtra("configInfo");
        tv_config_wifi_success.setText(R.string.config_success);
    }


}
