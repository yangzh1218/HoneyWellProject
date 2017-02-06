/**
 * Project Name:  iCam
 * File Name:     V2DeviceAlreadyBindedResultActivity.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2015年7月24日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.enums.DeviceType;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;

import java.util.Locale;


/**
 * @ClassName: DeviceAlreadyBindedResultActivity
 * @Function: V2设备已经绑定的结果界面
 * @Date: 2015年7月24日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class DeviceAlreadyBindedResultActivity extends BaseFragmentActivity {

     ImageView iv_device_picture;
    TextView tv_device_name;
    TextView tv_back_home;
    Button btn_request_view_device;

    private String deviceId;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_device_already_binded_result);
        mBaseView.bindView();
        mBaseView.setTitleView(R.string.config_scan_result);
        initView();
        initData();
    }

    private void initView() {
        iv_device_picture = (ImageView) findViewById(R.id.iv_device_picture);
        tv_device_name = (TextView) findViewById(R.id.tv_device_name);
        tv_back_home = (TextView) findViewById(R.id.tv_back_home);
        tv_back_home.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tv_back_home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceAlreadyBindedResultActivity.this.finish();
            }
        });
        btn_request_view_device = (Button) findViewById(R.id.btn_request_view_device);
        btn_request_view_device.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RequestViewIntent = new Intent(DeviceAlreadyBindedResultActivity.this,
                        DeviceSendAuthRequestActivity.class);
                RequestViewIntent.putExtra("deviceId", deviceId);
                startActivity(RequestViewIntent);
                DeviceAlreadyBindedResultActivity.this.finish();
            }
        });
    }

    private void initData() {
        deviceId = getIntent().getStringExtra("deviceId");
        if (TextUtils.isEmpty(deviceId)) {
            this.finish();
            return;
        }
        handleDevice();
    }

    private void handleDevice() {
        DeviceType type = DeviceType.getDevivceTypeByDeviceID(deviceId);
        if (type != DeviceType.NONE) {
            tv_device_name.setText(deviceId.toUpperCase(Locale.getDefault()));
        }
        switch (type) {
            case INDOOR:
            case OUTDOOR:
                iv_device_picture.setImageResource(R.drawable.type_04_device);
                break;
            case SIMPLE:
            case SIMPLE_N:
                iv_device_picture.setImageResource(R.drawable.type_03_device);
                break;
            case INDOOR2:
                iv_device_picture.setImageResource(R.drawable.type_04_device);
                break;
            case DESKTOP:
                iv_device_picture.setImageResource(R.drawable.type_unknown);
                break;
            default:
                CustomToast.show(this, R.string.config_not_support_device);
                this.finish();
                break;
        }
    }
}
