/**
 * Project Name:  iCam
 * File Name:     V2DeviceInfoConfigFailActivity.java
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.model.ConfigWiFiInfoModel;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;



/**
 * @author Puml
 * @ClassName: DeviceConfigFailResultActivity
 * @Function: 配置失败界面
 * @Date: 2015年7月26日
 * @email puml@wuliangroup.cn
 */
public class DeviceConfigFailResultActivity extends BaseFragmentActivity {
    private RelativeLayout rl_retry_one;
    private RelativeLayout rl_tv_choose_type;
    private Button btn_retry;

    private TextView tv_more_solutions;
    private TextView tv_choose_type;
    private TextView tv_config_wifi_fail;
    private TextView tv_config_wifi_fail_tip;

    private int ChooseType;

    private ConfigWiFiInfoModel mConfigInfo;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_device_config_fail);
        mBaseView.bindView();
        mBaseView.setTitleView(R.string.common_result);
        initView();
        initData();
    }

    private void initView() {
        rl_retry_one = (RelativeLayout) findViewById(R.id.rl_retry_one);
        btn_retry = (Button) findViewById(R.id.btn_retry);
        btn_retry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(DeviceConfigFailResultActivity.this, WifiInputActivity.class);
                it.putExtra("configInfo", mConfigInfo);
                startActivity(it);
            }
        });

        tv_config_wifi_fail = (TextView) findViewById(R.id.tv_config_wifi_fail);
        tv_config_wifi_fail_tip = (TextView) findViewById(R.id.tv_config_wifi_fail_tip);

    }

    private void initData() {
        mConfigInfo = getIntent().getParcelableExtra("configInfo");
        if (mConfigInfo.isAddDevice())
            tv_config_wifi_fail.setText(getResources().getString(
                    R.string.config_add_fail));
        else
            tv_config_wifi_fail.setText(getResources().getString(
                    R.string.config_wifi_fail));
        btn_retry.setText(getResources().getString(R.string.common_retry));
    }

}
