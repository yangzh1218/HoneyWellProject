/**
 * Project Name:  iCam
 * File Name:     V2RequestViewDevice.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2015年7月26日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.navigation.Navigator;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.MessageUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName: DeviceSendAuthRequestActivity
 * @Function: TODO
 * @Date: 2015年7月26日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class DeviceSendAuthRequestActivity extends BaseFragmentActivity
        implements OnClickListener, CallBack {
    private static int LIMIT = 60;
    private final static int SHARE_REQUEST = 0;
    private EditText et_request_content;
    private TextView tv_word_limit;
    private Button btn_send;

    private String deviceId;
    private String editString;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_device_send_auth_request);
        mBaseView.bindView();
        mBaseView.setTitleView(R.string.config_request_view);
        initView();
        initData();
        setListener();
        Interface.getInstance().setCallBack(this);
        Interface.getInstance().setContext(this);
    }

    private void initView() {
        et_request_content = (EditText) findViewById(R.id.et_request_content);
        tv_word_limit = (TextView) findViewById(R.id.tv_word_limit);
        btn_send = (Button) findViewById(R.id.btn_send);
    }

    private void initData() {
        deviceId = getIntent().getStringExtra("deviceId");
        if (TextUtils.isEmpty(deviceId)) {
            this.finish();
            return;
        }
    }

    private void setListener() {
        btn_send.setOnClickListener(this);
        et_request_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                int len = text.length();
                tv_word_limit.setText(len + "/" + LIMIT);
            }
        });
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHARE_REQUEST:
                    CustomToast.show(DeviceSendAuthRequestActivity.this, R.string.share_send_success);
                    Navigator navigator = new Navigator(DeviceSendAuthRequestActivity.this);
                    navigator.navigateToDeviceList(DeviceSendAuthRequestActivity.this);
                    DeviceSendAuthRequestActivity.this.finish();
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
            case V3_SHARE_REQUEST:
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.getInt("status") == 1 && jsonObject.getInt("result") == 1){
                        handler.sendMessage(MessageUtil.set(SHARE_REQUEST, "", ""));
                    }
//                    String username = jsonObject.getString("sip_username");
//                    String domain = jsonObject.getString("sip_domain");
//                    String sip_account = Utils.decrypt(username, domain);
//                    String uri = "sip:" + sip_account + "@" + domain;
//                    SipController.getInstance().sendMessage(
//                            sip_account,
//                            SipHandler.NotifyWebAccountInfo(uri, deviceId,
//                                    "request", ""),
//                            SipFactory.getInstance().getSipProfile());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }
    }

    @Override
    public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {
        CustomToast.show(this, R.string.common_send_fail);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            this.finish();
        } else if (id == R.id.btn_send) {
            editString = et_request_content.getText().toString();
            if (TextUtils.isEmpty(editString)) {
                CustomToast.show(this, R.string.config_please_input_desc);
                return;
            }
            Interface.getInstance().ShareRequest(deviceId, editString);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
