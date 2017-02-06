/**
 * Project Name:  iCam
 * File Name:     V2DeviceQueryActivity.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2015年7月25日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.UserDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.enums.DeviceType;
import com.wulian.sdk.android.oem.honeywell.ipc.model.ConfigWiFiInfoModel;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.MessageUtil;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


/**
 * @author Puml
 * @ClassName: DeviceIdQueryActivity
 * @Function: 设备查询界面
 * @Date: 2015年7月25日
 * @email puml@wuliangroup.cn
 */
public class DeviceIdQueryActivity extends BaseFragmentActivity implements CallBack {
    private RelativeLayout rl_query_device;
    private RelativeLayout rl_query_device_fail;
    private String deviceId;
    private String seed;
    private static final int MSG_BIND_CHECK = 0;
    Button btn_retry_query_device;

    boolean resetWifi = false;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_deviceid_query);
        mBaseView.bindView();
        mBaseView.setTitleView(R.string.config_add_camera);
        initView();
        initData();
        Interface.getInstance().setContext(this);
        Interface.getInstance().setCallBack(this);
    }

    private void initView() {
//        rl_query_device = (RelativeLayout) findViewById(R.id.rl_query_device);
        rl_query_device_fail = (RelativeLayout) findViewById(R.id.rl_query_device_fail);
        mBaseView.setTitleView(R.string.config_device_search);
        btn_retry_query_device = (Button) findViewById(R.id.btn_retry_query_device);
        btn_retry_query_device.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startBindingCheck();
            }
        });
    }

    private void initData() {
        String msgData = getIntent().getStringExtra("msgData");
        if (TextUtils.isEmpty(msgData)) {
            CustomToast.show(this, R.string.config_error_deviceid);
            finish();
            return;
        }

        if (msgData.contains("device_id=")) {
            deviceId = Utils.getRequestParams(msgData).get("device_id");
        } else {
            deviceId = msgData;
        }
        deviceId = deviceId.toLowerCase(Locale.getDefault());
        if (deviceId != null && deviceId.length() == 20) {// 6+2+12
            showDeviceQuery();
            startBindingCheck();
        } else {
            CustomToast.show(this, R.string.config_error_deviceid);
            finish();
        }
        resetWifi = getIntent().getBooleanExtra("resetWifi", false);
    }


    private void showDeviceQuery() {
        rl_query_device.setVisibility(View.VISIBLE);
        rl_query_device_fail.setVisibility(View.GONE);
    }

    private void showDeviceQueryFail() {
        mBaseView.setTitleView(R.string.common_result);
        rl_query_device.setVisibility(View.GONE);
        rl_query_device_fail.setVisibility(View.VISIBLE);
    }

    private void getDeviceFlag() {
        Interface.getInstance().V3AppFlag(deviceId);
    }

    private void startBindingCheck() {
        Interface.getInstance().BindingCheck(deviceId);
    }

    private void saveDeviceIdToSp() {
        String id = deviceId.substring(4);

        Utils.saveHandInput2Sp(UserDataRepository.getInstance().uuid(), id, this);
    }

    private void handleData(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject jsonObj = new JSONObject(json);
                // JSONObject flagObj = jsonObj.getJSONObject("flag");
                boolean qrConnect = false;
                boolean smartConnect = false;
                boolean wiredConnect = false;
                boolean apConnect = false;
                int currentConfigType = 1;
                if (!jsonObj.isNull("qr") && jsonObj.getInt("qr") == 1) {
                    qrConnect = true;
                }
                if (!jsonObj.isNull("sc") && jsonObj.getInt("sc") == 1) {
                    smartConnect = true;
                }
                DeviceType type = DeviceType.getDevivceTypeByDeviceID(deviceId);
                switch (type) {
                    case INDOOR:
                        apConnect = true;
                        smartConnect = false;
                        wiredConnect = false;
                        break;
                    case OUTDOOR:
                        qrConnect = true;
                        smartConnect = true;
                        apConnect = false;
                        wiredConnect = true;
                        break;
                    case SIMPLE:
                        if (smartConnect) {
                            apConnect = false;
                        } else {
                            apConnect = true;
                        }
                        wiredConnect = false;
                        break;
                    case INDOOR2:
                    case SIMPLE_N:
                        apConnect = false;
                        wiredConnect = false;
                        smartConnect = true;
                        break;
                    case DESKTOP:
                        qrConnect = false;
                        apConnect = false;
                        smartConnect = true;
                        wiredConnect = true;
                        break;
                    default:
                        CustomToast.show(this, R.string.config_error_deviceid);
                        finish();
                        break;
                }

                ConfigWiFiInfoModel data = new ConfigWiFiInfoModel();
                data.setDeviceId(deviceId);
                data.setSeed(seed);
                data.setQrConnect(qrConnect);
                data.setAddDevice(true);
                Intent it = new Intent(this, DeviceLaunchGuideActivity.class);
                it.putExtra("configInfo", data);
                it.putExtra("resetWifi", resetWifi);
                startActivity(it);
                finish();
            } catch (JSONException e) {
                CustomToast.show(this, R.string.config_error_deviceid);
                finish();
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_BIND_CHECK:
                    CustomToast.show(DeviceIdQueryActivity.this, R.string.config_device_already_in_list);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String json) {
        LibraryLoger.d("jsonData is:" + json);
        switch (apiType) {
            case V3_APP_FLAG:
                handleData(json);
                break;
            case V3_BIND_CHECK:
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String uid = jsonObject.getString("uid");
                    String username = jsonObject.getString("username");
                    String email = jsonObject.getString("email");
                    if (!TextUtils.isEmpty(uid) || !TextUtils.isEmpty(username) || !TextUtils.isEmpty(email)) {
                        if (UserDataRepository.getInstance().userInfo().getUuid().equalsIgnoreCase(uid) ||
                                UserDataRepository.getInstance().userInfo().getEmail().equalsIgnoreCase(email) ||
                                UserDataRepository.getInstance().userInfo().getUsername().equalsIgnoreCase(username)) {
                            handler.sendMessage(MessageUtil.set(MSG_BIND_CHECK, "", ""));
                        } else {
                            Intent it = new Intent(this, DeviceAlreadyBindedResultActivity.class);
                            it.putExtra("deviceId", deviceId);
                            startActivity(it);
                        }
                    } else {
                        seed = jsonObject.getString("seed");
                        getDeviceFlag();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
