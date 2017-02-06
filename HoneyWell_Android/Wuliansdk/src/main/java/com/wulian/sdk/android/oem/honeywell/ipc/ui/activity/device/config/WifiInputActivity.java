/**
 * Project Name:  iCam
 * File Name:     SingleWiFiSettingActivity.java
 * Package Name:  com.wulian.icam.view.setting
 *
 * @Date: 2015年6月29日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config;

import android.content.Intent;
import android.graphics.Paint;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.model.ConfigWiFiInfoModel;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.DialogUtils;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.DirectUtils;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.WiFiLinker;

import java.util.List;

/**
 * @author Puml
 * @ClassName: WifiInputActivity
 * @Function: 设置当前的Wi-Fi的密码
 * @Date: 2015年6月29日
 * @email puml@wuliangroup.cn
 */
public class WifiInputActivity extends BaseFragmentActivity implements
        OnClickListener {
    private EditText et_wifi_name;
    private EditText et_wifi_pwd;
    private Button btn_start_linkwifi;
    private TextView tv_config_wifi_tips;
    private CheckBox cb_wifi_pwd_show;
    private RelativeLayout rl_wifi_pwd;
    private TextView et_wifi_toggle;

    // 参数
    private String deviceId;// 设备ID
    private int configWiFiType;// 配置Wi-Fi方式
    private WiFiLinker mWiFiLinker;
    private String originSSid;
    private String originBssid;
    private String originSecurity;
    private String originPwd;
    private ConfigWiFiInfoModel mData;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_wifi_input);
        mBaseView.bindView();
        mBaseView.setTitleView(R.string.config_wifi_input);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        et_wifi_name = (EditText) findViewById(R.id.et_wifi_name);
        et_wifi_pwd = (EditText) findViewById(R.id.et_wifi_pwd);
        btn_start_linkwifi = (Button) findViewById(R.id.btn_start_linkwifi);
        tv_config_wifi_tips = (TextView) findViewById(R.id.tv_config_wifi_tips);
        cb_wifi_pwd_show = (CheckBox) findViewById(R.id.cb_wifi_pwd_show);
        tv_config_wifi_tips.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        rl_wifi_pwd = (RelativeLayout) findViewById(R.id.rl_wifi_pwd);
//        et_wifi_toggle = (TextView) findViewById(R.id.et_wifi_toggle);
//        et_wifi_toggle.setText(getClickableSpan());
//        et_wifi_toggle.setClickable(true);
//        et_wifi_toggle.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initData() {
        Bundle bd = getIntent().getExtras();
        mData = bd.getParcelable("configInfo");
        if (mData == null || TextUtils.isEmpty(mData.getDeviceId())) {
            this.finish();
            return;
        }
        deviceId = mData.getDeviceId();
        originPwd = mData.getWifiPwd();

        /** 网络操作初始化 */
        mWiFiLinker = new WiFiLinker();
        mWiFiLinker.WifiInit(this);

        if (mWiFiLinker.isWiFiEnable()) {
            WifiInfo info = mWiFiLinker.getWifiInfo();
            String currentSsid = "";
            if (info != null) {
                String ssid = info.getSSID();
                if (!TextUtils.isEmpty(ssid) && !info.getHiddenSSID()
                        && !"<unknown ssid>".equals(ssid)) {
                    currentSsid = ssid.replace("\"", "");
                } else {
                    CustomToast.show(this, R.string.config_confirm_wifi_hidden);
                    this.finish();
                    return;
                }
            } else {
                CustomToast.show(this, R.string.config_open_wifi);
                this.finish();
                return;
            }

            ScanResult result = null;
            List<ScanResult> scanList = mWiFiLinker.WifiGetScanResults();
            if (scanList == null || scanList.size() == 0) {
                CustomToast.show(this, R.string.config_no_wifi_scan_result);
                this.finish();
                return;
            }
            for (ScanResult item : scanList) {
                if (item.SSID.equalsIgnoreCase(currentSsid)) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                CustomToast.show(this, R.string.config_open_wifi);
                this.finish();
                return;
            }
            if (DirectUtils.isAdHoc(result.capabilities)) {
                CustomToast.show(this,R.string.config_adhoc_is_not_suppored);
                this.finish();
                return;
            }
            originSecurity = DirectUtils
                    .getStringSecurityByCap(result.capabilities);
            originBssid = result.BSSID;
            if (DirectUtils.isOpenNetwork(originSecurity)) {
                rl_wifi_pwd.setVisibility(View.GONE);
            }
            String localMac = info.getMacAddress();
            if (TextUtils.isEmpty(localMac)) {
                CustomToast.show(this, R.string.config_wifi_not_allocate_ip);
                this.finish();
                return;
            }

            originSSid = currentSsid;
            et_wifi_name.setText(currentSsid);
            if (!TextUtils.isEmpty(originPwd)
                    && originSSid.equals(mData.getWifiName())) {
                et_wifi_pwd.setText(originPwd);
            }
        } else {
            CustomToast.show(this, R.string.config_open_wifi);
            this.finish();
        }
    }

    private void setListener() {
        btn_start_linkwifi.setOnClickListener(this);
        cb_wifi_pwd_show.setOnClickListener(this);
        tv_config_wifi_tips.setOnClickListener(this);
        et_wifi_name.setOnClickListener(this);
        cb_wifi_pwd_show
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            et_wifi_pwd
                                    .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        } else {
                            et_wifi_pwd.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        CharSequence charsequence = et_wifi_pwd.getText();
                        if (charsequence instanceof Spannable) {
                            Spannable spanText = (Spannable) charsequence;
                            Selection.setSelection(spanText, charsequence.length());
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_start_linkwifi) {
            String pwd = "";
            if (!DirectUtils.isOpenNetwork(originSecurity)) {
                pwd = et_wifi_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    CustomToast.show(this, R.string.config_please_input);
                    return;
                }
                if (pwd.length() != pwd.getBytes().length) {
                    CustomToast.show(this, R.string.config_please_input_char);
                    return;
                }
            }
            mData.setWifiName(originSSid);
            mData.setWifiPwd(pwd);
            mData.setBssid(originBssid);
            mData.setSecurity(originSecurity);
            Intent it = new Intent();
            it.putExtra("configInfo", mData);
            it.setClass(this, DeviceGetReadyGuideActivity.class);
            startActivity(it);
            this.finish();
        } else if (id == R.id.tv_config_wifi_tips) {
            DialogUtils.showCommonInstructionsWebViewTipDialog(
                    this,
                    getResources().getString(
                            R.string.config_how_to_speed_up_config_wifi),
                    "fastconn");
        } else if (id == R.id.et_wifi_name) {

        }
    }
//
//    private SpannableString getClickableSpan() {
//        OnClickListener l = new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 1);
//            }
//        };
//
//        String wifi_limit = getResources().getString(R.string.setting_input_wifi_limit);
//        SpannableString spanableInfo = new SpannableString(wifi_limit);
//        int start = wifi_limit.indexOf("点击");
//        int end = start + "点击".length();
//        if (CommonUtils.getLanguageEnv().contains("en")) {
//            start = wifi_limit.indexOf("click");
//            end = start + "click".length();
//        } else if (CommonUtils.getLanguageEnv().contains("ko")) {
//            start = wifi_limit.indexOf("클릭");
//            end = start + "클릭".length();
//        } else if (CommonUtils.getLanguageEnv().contains("pt")) {
//            start = wifi_limit.indexOf("clique");
//            end = start + "clique".length();
//        }
//        spanableInfo.setSpan(new Clickable(l), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spanableInfo.setSpan(new ForegroundColorSpan(Color.rgb(0, 204, 204)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return spanableInfo;
//    }
//
//    class Clickable extends ClickableSpan implements OnClickListener {
//        private final OnClickListener mListener;
//
//        public Clickable(OnClickListener l) {
//            mListener = l;
//        }
//
//        @Override
//        public void onClick(View v) {
//            mListener.onClick(v);
//        }
//    }

}
