/**
 * Project Name:  iCam
 * File Name:     PersonalInfoActivity.java
 * Package Name:  com.wulian.icam.view.setting
 *
 * @Date: 2014年10月16日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.setting;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.controller.RouteLibraryController;
import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.APPConfig;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.DeviceDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.model.VersionInfo;
import com.wulian.sdk.android.oem.honeywell.ipc.sip.SipFactory;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config.ConfigDeviceFirstPageActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.DialogUtils;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.MessageUtil;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.NetCommon;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.WifiAdmin;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.manage.SipProfile;
import com.wulian.siplibrary.model.linkagedetection.LinkageDetectionModel;
import com.wulian.siplibrary.utils.WulianLog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Wangjj
 * @ClassName: DeviceSettingActivity
 * @Function: 设备设置
 * @Date: 2014年10月16日
 * @email wangjj@wuliangroup.cn
 */
public class DeviceSettingActivity extends BaseFragmentActivity implements
        OnClickListener, OnCheckedChangeListener, CallBack {
    private LinearLayout ll_wifi_setting, ll_delete_device, ll_firmware_update,
            ll_history_video_setting, ll_device_desc, ll_device_description,
            ll_device_protect, ll_device_share, ll_device_for_v5,
            ll_video_invert, ll_led_invert, ll_voice_invert, ll_locale_setting,
            ll_change_pwd,ll_switch_server,ll_divider1,ll_divider2,ll_divider3;
    private TextView tv_device_name, tv_delete_info, tv_sdcard_status,
            tv_device_desc, tv_device_version, tv_user_name,tv_camera_domain;
    private CheckBox cb_video_invert, cb_led_invert, cb_voice_invert;
    private Dialog mRenameDialog;
    private Dialog mDeleteDialog;
    private Dialog mNotifyUpdateDialog;
    private Dialog mLocaleDialog;
    private LinearLayout ll_device_function;
    private EditText et_focus;

    private WifiAdmin wifiAdmin;
    private boolean hasSDCard;
    private boolean isClickToUpdateVersion,
            isQueryLedAndVoicePromptInfo = true;
    private Device device;
    private int callback_flag;
    private int seq = 1;
    private String deviceId;
    private String destDeviceIP;
    private String selectedLocale;
    private String deviceSipAccount;// 设备sip账号
    private String deviceControlUrl;// 设备控制sip地址
    private String deviceCallUrl;// 设备呼叫sip地址
    private SipProfile account;
    private String sipCallWithDomain;// xxx@wuliangruop.cn
    private VersionInfo cmicWebVersionInfo = null;
    private int deviceVersionCode;
    private static final int FLAG_UNBIND_DEVICE = 0;
    private static final int FLAG_EDITMETA_DEVICE = 1;
    private SharedPreferences sp;
    private String device_name;
    private static final int MSG_FINISH = 1;
    private static final int MSG_EDIT_META = 2;
    private static final int MSG_HIDE_IME = 3;
    private String led_on = "1", audio_online = "1";

    private final static String CONFIG_LED = "config_led";
    private final static String CONFIG_VOICE = "config_voice";
    private long lastClickTime;
    private String domainAddress;
    private TextView tv_domain_address,tv_wifi_setting;
    private Button  btn_formal,btn_test;
    private boolean isConfigIpcName = false ;
    private boolean isClickSwitch = false;
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH:// 结束页面
                    DeviceSettingActivity.this.finish();
                    break;
                case MSG_EDIT_META:// 编辑设备信息
                    sendEditMeta(device_name);
                    tv_device_desc.setText(device_name);
                    break;
                case MSG_HIDE_IME:// 隐藏键盘
                    Utils.hideIme(DeviceSettingActivity.this);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelable("device", device);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        device = savedInstanceState.getParcelable("device");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);
        mBaseView.bindView();
        initViews();
        initListeners();
        initData();
        onSendSipRemoteAccess();
        if (device.getOnline() == 1) {
            initWebData();
        }
        Interface.getInstance().setCallBack(this);
        Interface.getInstance().setContext(this);
    }

    private void initWebData() {
        if (NetCommon.getNetworkType().equals("")) {
            CustomToast.show(this, R.string.error_no_network);
            return;
        }
        // 122 查询存储状态
        SipController.getInstance().sendMessage(
                sipCallWithDomain,
                SipHandler.QueryStorageStatus("sip:" + sipCallWithDomain, seq++),
                SipFactory.getInstance().getSipProfile());
        // TODO 接口暂不支持
        //查询摄像机域名
        SipController.getInstance().sendMessage(sipCallWithDomain,SipHandler.QueryWebDomian("sip:" + sipCallWithDomain, seq++),SipFactory.getInstance().getSipProfile());
        queryLedAndVoicePromptInfo();
        // System.out.println("开始执行查询操作");
        // initQueryLedAndVoice();
    }

    // 查询LED及语音提示设置
    private void queryLedAndVoicePromptInfo() {
        if (NetCommon.getNetworkType().equals("")) {
            CustomToast.show(this, R.string.error_no_network);
            return;
        }
        SipController.getInstance().sendMessage(
                sipCallWithDomain,
                SipHandler.QueryLedAndVoicePromptInfo(
                        "sip:" + sipCallWithDomain, seq++),
                SipFactory.getInstance().getSipProfile());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.sysoInfo("onStart registerReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (device == null) {// 必须的数据为空了，直接结束
            this.finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isClickToUpdateVersion = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViews() {
        ((TextView) findViewById(R.id.titlebar_title)).setText(R.string.setting_device_setting);
        ll_wifi_setting = (LinearLayout) findViewById(R.id.ll_wifi_setting);
        ll_delete_device = (LinearLayout) findViewById(R.id.ll_delete_device);
        et_focus = (EditText) findViewById(R.id.et_focus);
        tv_device_name = (TextView) findViewById(R.id.tv_device_name);
        ll_device_function = (LinearLayout) findViewById(R.id.ll_device_function);
        ll_firmware_update = (LinearLayout) findViewById(R.id.ll_firmware_update);
        tv_delete_info = (TextView) findViewById(R.id.tv_delete_info);
        tv_sdcard_status = (TextView) findViewById(R.id.tv_sdcard_status);
        tv_device_desc = (TextView) findViewById(R.id.tv_device_desc);
        tv_device_version = (TextView) findViewById(R.id.tv_device_version);
        ll_history_video_setting = (LinearLayout) findViewById(R.id.ll_history_video_setting);
        ll_video_invert = (LinearLayout) findViewById(R.id.ll_video_invert);
        ll_led_invert = (LinearLayout) findViewById(R.id.ll_led_invert);
        ll_voice_invert = (LinearLayout) findViewById(R.id.ll_voice_invert);
        ll_device_desc = (LinearLayout) findViewById(R.id.ll_device_desc);
        ll_device_description = (LinearLayout) findViewById(R.id.ll_device_description);
        ll_device_for_v5 = (LinearLayout) findViewById(R.id.ll_device_for_v5);
        ll_device_protect = (LinearLayout) findViewById(R.id.ll_device_protect);
        ll_device_share = (LinearLayout) findViewById(R.id.ll_device_share);
        ll_locale_setting = (LinearLayout) findViewById(R.id.ll_locale_setting);
        cb_video_invert = (CheckBox) findViewById(R.id.cb_video_invert);
        cb_led_invert = (CheckBox) findViewById(R.id.cb_led_invert);
        cb_voice_invert = (CheckBox) findViewById(R.id.cb_voice_invert);
        ll_change_pwd = (LinearLayout) findViewById(R.id.ll_change_pwd);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        ll_device_for_v5.setVisibility(View.GONE);
        ll_locale_setting.setVisibility(View.GONE);
        btn_test = (Button) findViewById(R.id.btn_test);
        btn_formal = (Button) findViewById(R.id.btn_formal);
        btn_test.setEnabled(false);
        btn_formal.setEnabled(false);
        tv_domain_address = (TextView) findViewById(R.id.tv_domain_address);
        tv_camera_domain = (TextView) findViewById(R.id.tv_camera_domain);
        ll_switch_server = (LinearLayout) findViewById(R.id.ll_switch_server);

        tv_wifi_setting = (TextView) findViewById(R.id.tv_wifi_setting);
        ll_divider1 = (LinearLayout) findViewById(R.id.ll_divider1);
        ll_divider2 = (LinearLayout) findViewById(R.id.ll_divider2);
        ll_divider3 = (LinearLayout) findViewById(R.id.ll_divider3);
    }

    private void initListeners() {
        ll_wifi_setting.setOnClickListener(this);
        ll_delete_device.setOnClickListener(this);
//		ll_firmware_update.setOnClickListener(this);
        ll_history_video_setting.setOnClickListener(this);
        ll_video_invert.setOnClickListener(this);
        ll_led_invert.setOnClickListener(this);
        ll_voice_invert.setOnClickListener(this);
        ll_device_desc.setOnClickListener(this);
        tv_device_desc.setOnClickListener(this);
        ll_device_description.setOnClickListener(this);
        ll_device_protect.setOnClickListener(this);
        ll_device_share.setOnClickListener(this);
        ll_locale_setting.setOnClickListener(this);
        cb_video_invert.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                sp.edit().putBoolean(deviceId + APPConfig.VIDEO_INVERT,
                        isChecked).commit();
            }
        });
        cb_video_invert.setOnCheckedChangeListener(this);
        cb_led_invert.setOnCheckedChangeListener(this);
        cb_voice_invert.setOnCheckedChangeListener(this);
        ll_change_pwd.setOnClickListener(this);
        btn_formal.setOnClickListener(this);
        btn_test.setOnClickListener(this);
        MultiClickListener multiClickListener = new MultiClickListener(){

            @Override
            public void toNext() {
                super.toNext();
                if (device.getOnline() == 0) {
                    CustomToast.show(getApplicationContext(), R.string.setting_device_offline);
                    return;
                }
                isClickToUpdateVersion = true;
                getLastDeviceVersion();
            }
        };
        ll_firmware_update.setOnClickListener(multiClickListener);
        ll_switch_server.setOnClickListener(this);
    }

    private void initData() {
        sp = getSharedPreferences(APPConfig.SP_CONFIG, MODE_PRIVATE);
        device = (Device) getIntent().getParcelableExtra("device");
        if (device == null) {
            finish();
            return;
        }
        deviceId = device.getDid();
//        device.setIs_BindDevice(true);
        cb_video_invert.setChecked(sp.getBoolean(deviceId
                + APPConfig.VIDEO_INVERT, false));
        tv_device_desc.setText(device.getNick());
//        ll_device_function
//                .setVisibility(device.getIs_BindDevice() ? View.VISIBLE
//                        : View.GONE);
        boolean isBindDevice = device.getIs_BindDevice();
        if(!isBindDevice)
        {
            ll_change_pwd.setVisibility(View.GONE);
            ll_history_video_setting.setVisibility(View.GONE);
            ll_firmware_update.setVisibility(View.GONE);
            ll_switch_server.setVisibility(View.GONE);
            ll_wifi_setting.setVisibility(View.GONE);
            tv_wifi_setting.setVisibility(View.GONE);
            ll_divider1.setVisibility(View.GONE);
            ll_divider2.setVisibility(View.GONE);
            ll_divider3.setVisibility(View.GONE);
        }
        sipCallWithDomain = device.getDid() + "@"
                + device.getSip_domain();
        if (device.getOnline() == 1) {
            getLastDeviceVersion();
        }
        deviceSipAccount = device.getDid();
        deviceCallUrl = deviceSipAccount + "@" + device.getSip_domain();
        deviceControlUrl = deviceCallUrl;
        tv_user_name.setText(DeviceDataRepository.getInstance().ipcUsername());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }

    private void renameDeviceDialog() {
        Resources rs = getResources();
        mRenameDialog = DialogUtils.showCommonEditDialog(this, false,
                rs.getString(R.string.setting_enter_device_name), null, null,
                rs.getString(R.string.setting_enter_device_name),
                device.getNick(), new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.et_input) {
                            EditText infoEt = (EditText) v;
                            device_name = infoEt.getText().toString().trim();
                            if (!TextUtils.isEmpty(device_name)) {
                                if (!device_name.equals(device.getNick())) {
                                    myHandler.sendEmptyMessageDelayed(
                                            MSG_EDIT_META, 100);
                                    myHandler.sendEmptyMessageDelayed(
                                            MSG_HIDE_IME, 50);
                                    mRenameDialog.dismiss();
                                } else {
                                    mRenameDialog.dismiss();
                                }
                            } else {
                                Utils.shake(DeviceSettingActivity.this, infoEt);
                            }
                        } else if (id == R.id.btn_negative) {
                            mRenameDialog.dismiss();
                        }
                    }
                });
    }

    private void deleteDeviceDialog() {
        String tip;
        Resources rs = getResources();
        if (device.getIs_BindDevice()) {
            tip = rs.getString(R.string.setting_unbind_device);
        } else {
            tip = rs.getString(R.string.setting_unbind_auth_device);
        }

        mDeleteDialog = DialogUtils.showCommonDialog(this, true,
                rs.getString(R.string.setting_delete_device), tip, null, null,
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.btn_positive) {
                            unBindDevice();
                            stopProtect();//删除绑定设备同时关闭防护否则报警消息还会推送
                            deleteSnapPic();//删除Snap图片
                            mDeleteDialog.dismiss();
                        } else if (id == R.id.btn_negative) {
                            mDeleteDialog.dismiss();
                        }
                    }
                });
    }

    private void deleteSnapPic() {
        SharedPreferences sp = this.getSharedPreferences(
                APPConfig.SP_SNAPSHOT, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(device.getDid() + "_snapshot_or_avatar");
        editor.commit();
    }

    private void stopProtect() {
        if (NetCommon.getNetworkType().equals("")) {
            CustomToast.show(this, R.string.error_no_network);
            return;
        }
        mBaseView.showProgressDialog();
        String moveArea = sp.getString(device.getDid()
                + APPConfig.MOVE_AREA, ";");
        SipController.getInstance().sendMessage(
                deviceControlUrl,
                SipHandler.ConfigMovementDetection(deviceControlUrl, seq++,
                        false, 50, moveArea.split(";")), account);

        LinkageDetectionModel model = new LinkageDetectionModel();
        model.setUse(false);

        SipController.getInstance().sendMessage(
                deviceControlUrl,
                SipHandler.ConfigLinkageArming(deviceControlUrl, seq++, 1, 1,
                        model), account);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_wifi_setting) {
            Intent it = new Intent(this, ConfigDeviceFirstPageActivity.class);
            it.putExtra("deviceId", deviceId);
            startActivity(it);
        } else if (id == R.id.ll_video_invert) {
            cb_video_invert.toggle();
        } else if (id == R.id.ll_history_video_setting) {
            if (device.getOnline() == 0) {
                CustomToast.show(this, R.string.setting_device_offline);
                return;
            }
            if (!hasSDCard) {
                CustomToast.show(this, R.string.setting_please_insert_sdcard);
                return;
            }
            startActivity(new Intent(DeviceSettingActivity.this,
                    HistoryVideoSettingActivity.class).putExtra("device",
                    device));
        } else if (id == R.id.ll_led_invert) {
//            mBaseView.showProgressDialog();
            isQueryLedAndVoicePromptInfo = false;
            // TODO led设置
            cb_led_invert.toggle();
        } else if (id == R.id.ll_voice_invert) {
//            mBaseView.showProgressDialog();
            isQueryLedAndVoicePromptInfo = false;
            // TODO 提示音设置
            cb_voice_invert.toggle();
        } else if (id == R.id.ll_device_description) {
            // 进入设备详细界面
            startActivity(new Intent(DeviceSettingActivity.this,
                    DeviceDetailActivity.class).putExtra("device", device));
        } else if (id == R.id.tv_device_desc || id == R.id.ll_device_desc) {
            if (device.getIs_BindDevice()) {
                renameDeviceDialog();
            }
        }
//        else if (id == R.id.ll_firmware_update) {
//            if (device.getOnline() == 0) {
//                CustomToast.show(this, R.string.setting_device_offline);
//                return;
//            }
//            isClickToUpdateVersion = true;
//            getLastDeviceVersion();
////            mBaseView.showProgressDialog();
//        }
        else if (id == R.id.tv_device_desc || id == R.id.ll_device_desc) {
            if (device.getIs_BindDevice()) {
                renameDeviceDialog();
            }
        } else if (id == R.id.ll_delete_device) {
            deleteDeviceDialog();
        } else if (id == R.id.ll_change_pwd) {
            showChangePwdDialog();
        } else if(id == R.id.btn_formal)
        {
            configDomain();
        }else if(id == R.id.btn_test){
            configDomain();
        }else if(id == R.id.ll_switch_server)
        {
            startActivity(new Intent(DeviceSettingActivity.this,
                    DeviceServerActivity.class).putExtra("device", device));
        }

        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }


    private boolean checkDeviceVersion = true;

    /**
     * @Function 解除设备的绑定
     * @author Wangjj
     * @date 2014年11月28日
     */
    private void unBindDevice() {
        callback_flag = FLAG_UNBIND_DEVICE;
        if (NetCommon.getNetworkType().equals("")) {
            CustomToast.show(this, R.string.error_no_network);
            return;
        }
        if (device.getIs_BindDevice()) {// 解除绑定设备
            Interface.getInstance().BindUnbind(device.getDid());
        } else {// 解除授权设备
            Interface.getInstance().UnshareRequest(device.getDid(), "");
        }
    }

    // 固件升级
    private void showNoticeDialog() {
        Resources rs = getResources();
        mNotifyUpdateDialog = DialogUtils.showCommonDialog(this, true,
                rs.getString(R.string.setting_version_update) + " "
                        + cmicWebVersionInfo.getVersion(),
                Html.fromHtml(cmicWebVersionInfo.getDescription()),
                rs.getString(R.string.setting_update_now),
                rs.getString(R.string.setting_update_later),
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.btn_positive) {
                            mNotifyUpdateDialog.dismiss();
                            if (NetCommon.getNetworkType().equals("")) {
                                CustomToast.show(DeviceSettingActivity.this, R.string.error_no_network);
                                return;
                            }
                            CustomToast.show(DeviceSettingActivity.this, R.string.setting_downloading_new_version);
                            // 发送远程更新命令
//                            mBaseView.showProgressDialog();
                            SipController.getInstance().sendMessage(
                                    device.getDid() + "@"
                                            + device.getSip_domain(),
                                    SipHandler.NotifyFirewareUpdate("sip:"
                                                    + device.getDid() + "@"
                                                    + device.getSip_domain(), seq++,
                                            cmicWebVersionInfo
                                                    .getVersion(),
                                            cmicWebVersionInfo
                                                    .getCode()),
                                    SipFactory.getInstance().getSipProfile());
                        } else if (id == R.id.btn_negative) {
                            mNotifyUpdateDialog.dismiss();
                        }
                    }
                });
    }

    private String uuid;


    /**
     * @param led_on       0 : 关闭LED 1 ：开启LED
     * @param audio_online 上线提醒 0 ：关闭 1 : 开启
     * @MethodName: configLedAndVoicePrompt
     * @Function: 设置LED及语音提示设置
     * @author: yuanjs
     * @date: 2015年10月22日
     * @email: jiansheng.yuan@wuliangroup.com
     */
    private void configLedAndVoicePrompt(String led_on, String audio_online) {
        if (NetCommon.getNetworkType().equals("")) {
            CustomToast.show(this, R.string.error_no_network);
            return;
        }
        SipController.getInstance().sendMessage(
                device.getDid() + "@" + device.getSip_domain(),
                SipHandler.ConfigLedAndVoicePrompt(
                        "sip:" + device.getDid() + "@"
                                + device.getSip_domain(), seq++, led_on,
                        audio_online), SipFactory.getInstance().getSipProfile());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if(!buttonView.isPressed()) return;
        if (id == R.id.cb_video_invert) {
            sp.edit().putBoolean(deviceId + APPConfig.VIDEO_INVERT, isChecked)
                    .commit();
        } else if (id == R.id.cb_led_invert) {
            // TODO
            if (isChecked) {
                led_on = "1";
                // sp.edit().putString(CONFIG_LED + "_" + device.getDid(),
                // "1").commit();
            } else {
                led_on = "0";
                // sp.edit().putString(CONFIG_LED + "_" + device.getDid(),
                // "0").commit();
            }
            if (isQueryLedAndVoicePromptInfo) {
                isQueryLedAndVoicePromptInfo = false;
            } else {
                // System.out.println("执行LED设置");
                configLedAndVoicePrompt(led_on, audio_online);
            }
        } else if (id == R.id.cb_voice_invert) {
            // TODO
            if (isChecked) {
                audio_online = "1";
                // sp.edit().putString(CONFIG_VOICE + "_" +
                // device.getDid(), "1").commit();
            } else {
                audio_online = "0";
                // sp.edit().putString(CONFIG_VOICE + "_" +
                // device.getDid(), "0").commit();
            }
            if (isQueryLedAndVoicePromptInfo) {
                isQueryLedAndVoicePromptInfo = false;
            } else {
                //
                // System.out.println("执行语音提示设置");
                configLedAndVoicePrompt(led_on, audio_online);
            }
        }
    }

    private void getLastDeviceVersion() {
        checkDeviceVersion = true;
        Interface.getInstance().V3VersionStable(new CallBack() {
            @Override
            public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String json) {
                cmicWebVersionInfo = Utils.parseBean(VersionInfo.class,
                        json);
                SipController.getInstance().sendMessage(
                        sipCallWithDomain,
                        SipHandler.QueryFirewareVersion("sip:" + sipCallWithDomain, seq++),
                        SipFactory.getInstance().getSipProfile());
            }
            @Override
            public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {
                tv_device_version.setText(R.string.setting_query_fireware_fail);
            }
        });
    }

    /**
     * @Function 修改设备信息
     * @author Wangjj
     * @date 2014年11月27日
     */
    private void sendEditMeta(String nickName) {
        if (NetCommon.getNetworkType().equals("")) {
            CustomToast.show(this, R.string.error_no_network);
            return;
        }
        callback_flag = FLAG_EDITMETA_DEVICE;
        SipController.getInstance().sendMessage(device.getDid() + "@" + device.getSip_domain(),SipHandler.ConfigUserPassword("sip:" + device.getDid() + "@"
                + device.getSip_domain(), seq++,nickName,"",""),SipFactory.getInstance().getSipProfile());
        isConfigIpcName = true;
    }
    //修改摄像机域名
    private void configDomain()
    {
        if(TextUtils.isEmpty(domainAddress))
        {
            btn_formal.setEnabled(false);
            btn_test.setEnabled(false);
            return;
        }
        if(domainAddress.equals("hw.pu.sh.gg"))
        {
            SipController.getInstance().sendMessage(sipCallWithDomain,SipHandler.ConfigWebDomain("sip:" + sipCallWithDomain, seq++,"test.sh.gg"),SipFactory.getInstance().getSipProfile());
        }
        else if(domainAddress.equals("test.sh.gg"))
        {
            SipController.getInstance().sendMessage(sipCallWithDomain,SipHandler.ConfigWebDomain("sip:" + sipCallWithDomain, seq++,"hw.pu.sh.gg"),SipFactory.getInstance().getSipProfile());
        }
    }



    private Dialog changePwdDialog;

    private void showChangePwdDialog() {
        Resources rs = getResources();
        if (changePwdDialog == null)
            changePwdDialog = DialogUtils.
                    showSetPwdEditDialog(this, false,
                            rs.getString(R.string.common_change_pwd), null, null,
                            rs.getString(R.string.setting_input_pwd), "",
                            new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int id = v.getId();
                                    if (id == R.id.et_input) {
                                        String confirmPwd = ((EditText) v.getRootView().findViewById(R.id.et_input_confirm)).getText().toString().trim();
                                        String pwd = ((EditText) v).getText().toString().trim();
                                        if (confirmPwd.equals(pwd)) {
                                            // 122 查询存储状态
                                            SipController.getInstance().sendMessage(
                                                    sipCallWithDomain,
                                                    SipHandler.ConfigUserPassword("sip:" + sipCallWithDomain, seq++, device.getNick(), tv_user_name.getText().toString().trim(),
                                                            ((EditText) v).getText().toString().trim()),
                                                    SipFactory.getInstance().getSipProfile());
                                            isConfigIpcName = false;
                                            changePwdDialog.dismiss();
                                        } else {
                                            CustomToast.show(DeviceSettingActivity.this, R.string.digital_no_match);
                                        }
                                        // TODO 接口暂不支持

                                    } else if (id == R.id.btn_negative) {
                                        changePwdDialog.dismiss();
                                    } else {
                                    }
                                }
                            });
        if (!changePwdDialog.isShowing()) {
            changePwdDialog.show();
        }

    }

    @Override
    public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String json) {
        LibraryLoger.d("jsonData is:" + json);
        mBaseView.dismissProgressDialog();
        switch (apiType) {
            case V3_USER_DEVICE:
                CustomToast.show(this, json);
                device.setNick(device_name);
                break;
            case V3_BIND_UNBIND:
                Interface.getInstance().setNeedRefreshList(true);
                handler.sendMessage(MessageUtil.set(2, "message", getResources().getString(R.string.setting_device_unbind_success)));
                myHandler.sendEmptyMessageDelayed(MSG_FINISH,200);
                break;
            default:
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mBaseView.dismissProgressDialog();
                    CustomToast.show(DeviceSettingActivity.this, msg.getData().getString("exception"));
                    break;
                case 2:
                    CustomToast.show(DeviceSettingActivity.this, msg.getData().getString("message"));
                    DeviceSettingActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {
        switch (apiType) {
            case V3_BIND_UNBIND:

                break;
            default:
                break;
        }
        Message message = new Message();
        message.what = 0;
        Bundle bundle = new Bundle();
        bundle.putString("exception", exception.getMessage());
        message.setData(bundle);
        handler.sendMessage(message);
    }


    @Override
    protected void sipDataReturn(boolean isSuccess, SipMsgApiType apiType,
                                 String xmlData, String from, String to) {
        super.sipDataReturn(isSuccess, apiType, xmlData, from, to);
        if (isSuccess) {
            Utils.sysoInfo("===" + xmlData);
            WulianLog.e(apiType.name(), xmlData);
            switch (apiType) {
                case QUERY_STORAGE_STATUS:// 122 查询存储状态
                    Utils.sysoInfo("查询存储状态:" + xmlData);
                    // <storage num="1" type="SD" status="1" attr="rw"
                    // totalsize="1000K" freesize="0K"></storage>
                    Pattern pstatus = Pattern
                            .compile("<storage.*status=\"(\\d)\"\\s+.*/?>(</storage>)?");
                    Matcher matchers = pstatus.matcher(xmlData);
                    if (matchers.find()) {
                        String status = matchers.group(1).trim();
                        if ("1".equals(status)) {
                            hasSDCard = false;
                            tv_sdcard_status.setText(getResources().getString(
                                    R.string.common_no_sdcard));
                        } else if ("2".equals(status)) {
                            hasSDCard = true;
                            tv_sdcard_status.setText(getResources().getString(
                                    R.string.setting_has_sdcard));
                        }
                    }

                    break;
                case QUERY_FIREWARE_VERSION:// 查询固件版本
                    Utils.sysoInfo("===" + "QUERY_FIREWARE_VERSION");
                    try {
                        deviceVersionCode = Integer.parseInt(Utils.getParamFromXml(
                                xmlData, "version_id"));
                        String deviceVersion = Utils.getParamFromXml(xmlData,
                                "version");
                        Utils.sysoInfo("###########" + deviceVersion);
                    } catch (NumberFormatException e) {

                        Utils.sysoInfo("服务器返回的固件版本号错误!");
                        deviceVersionCode = 0;
                    }
                    if (cmicWebVersionInfo == null) {
                        return;
                    }
                    // 比较版本
                    if (cmicWebVersionInfo.getCode() > deviceVersionCode) {
                        // 需要更新
                        if (isClickToUpdateVersion) {
                            showNoticeDialog();
                        }
                        tv_device_version.setText(getResources().getString(
                                R.string.setting_device_version_past));
                    } else {
                        // 已经是最新版本
//                        if (isClickToUpdateVersion) {
//                            CustomToast
//                                    .show(this, R.string.setting_latest_fireware);
//                            // showLatestOrFailDialog(DIALOG_TYPE_LATEST);
//                        }
                        tv_device_version.setText(getResources().getString(
                                R.string.setting_device_version_newest));
                    }
                    break;
                case NOTIFY_FIREWARE_UPDATE:// 通知版本更新
                    Utils.sysoInfo("===" + "NOTIFY_FIREWARE_UPDATE");
                    CustomToast.show(this,
                            R.string.setting_already_notice_fireware_update);
                    break;
                case QUERY_LED_AND_VOICE_PROMPT_INFO:// 查询LED及语音提示设置
                    // TODO 解析xml
                    led_on = Utils.getParamFromXml(xmlData, "led_on").trim();
                    audio_online = Utils.getParamFromXml(xmlData, "audio_online")
                            .trim();
                    // System.out.println("查询LED请求");
                    if (!TextUtils.isEmpty(led_on)) {
                        if (led_on.equals("0")) {
                            cb_led_invert.setChecked(false);
                        } else {// 1
                            cb_led_invert.setChecked(true);
                        }
                    }
                    if (!TextUtils.isEmpty(audio_online)) {
                        if (audio_online.equals("0")) {
                            cb_voice_invert.setChecked(false);
                        } else {// 1
                            cb_voice_invert.setChecked(true);
                        }
                    }
                    break;
                case CONFIG_LED_AND_VOICE_PROMPT://
                    mBaseView.dismissProgressDialog();
                    // System.out.println("设置LED请求");
                    if (led_on == "0" && audio_online == "0") {

                    } else {
                        CustomToast.show(this, R.string.common_setting_success);
                    }
                    break;
                case CONFIG_USER_PASSWORD:
                    CustomToast.show(this, R.string.common_setting_success);
                    if(isConfigIpcName)
                    {

                        Interface.getInstance().UserDevice(device.getDid(), device_name, TextUtils.isEmpty(device.getDescription()) ? "" : device.getDescription());
                    }
                    break;
                case QUERY_WEB_DOMAIN:
                    domainAddress = Utils.getParamFromXml(xmlData, "domain").trim();
                    tv_domain_address.setText(domainAddress);
                    tv_camera_domain.setText(domainAddress);
                    if(TextUtils.isEmpty(tv_domain_address.getText().toString())) return;
                    if(domainAddress.equalsIgnoreCase("hw.pu.sh.gg"))
                    {
                        btn_formal.setEnabled(false);
                        btn_test.setEnabled(true);
                    }
                    else if(domainAddress.equalsIgnoreCase("test.sh.gg")){
                        btn_formal.setEnabled(true);
                        btn_formal.setClickable(true);
                        btn_test.setEnabled(false);
                    }
                    break;
                case CONFIG_WEB_DOMAIN:
                    CustomToast.show(this,R.string.setting_switch_success);
                    break;


            }
        } else {
            Utils.sysoInfo("sip fail:" + xmlData);
            switch (apiType) {
                case QUERY_FIREWARE_VERSION:// 查询固件版本
                    if (isClickToUpdateVersion) {
                        CustomToast.show(this, R.string.setting_query_fireware_fail);
                    }
                    tv_device_version.setText(R.string.setting_query_fireware_fail);
                    break;
                case NOTIFY_FIREWARE_UPDATE:// 通知版本更新
                    CustomToast.show(this, R.string.setting_notice_fireware_fail);
                    break;
                case QUERY_LED_AND_VOICE_PROMPT_INFO:// 查询LED及语音提示设置
                    break;
                case CONFIG_LED_AND_VOICE_PROMPT://
                    mBaseView.dismissProgressDialog();
                    CustomToast.show(this, R.string.common_setting_fail);
                    break;
                case CONFIG_USER_PASSWORD:
                    CustomToast.show(this, R.string.common_setting_fail);
                    break;
                case CONFIG_IPC_NAME:
                    CustomToast.show(this,R.string.common_setting_fail);
                    break;
            }
        }
    }
}
 class MultiClickListener implements View.OnClickListener {
    private long lastClickTime;
    public synchronized boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
    @Override
    public void onClick(View view) {
        if(!isFastClick() && view.isPressed()){
            toNext();
        }
    }

    public void toNext(){

    };
}
