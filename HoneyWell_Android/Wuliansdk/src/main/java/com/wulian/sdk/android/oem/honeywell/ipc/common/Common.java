package com.wulian.sdk.android.oem.honeywell.ipc.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.enums.DeviceType;
import com.wulian.sdk.android.oem.honeywell.ipc.model.ConfigWiFiInfoModel;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config.DeviceLaunchGuideActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 作者：Administrator on 2016/6/27 11:08
 * 邮箱：huihui@wuliangroup.com
 */
public class Common{

        public static boolean handleBarCodeJson(Activity activity, String json) {
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
                    return qrConnect;
                } catch (JSONException e) {
                    CustomToast.show(activity, R.string.config_error_deviceid);
                    activity.finish();
                }
            }
            return false;
        }

    public static boolean getQrConnectFromDeviceId(String deviceId){
        boolean qrConnect = false;
        DeviceType type = DeviceType.getDevivceTypeByDeviceID(deviceId);
        switch (type) {
            case INDOOR:
                break;
            case OUTDOOR:
                qrConnect = true;
                break;
            case SIMPLE:
                break;
            case INDOOR2:
            case SIMPLE_N:
                break;
            case DESKTOP:
                qrConnect = false;
                break;
            default:
                break;
        }
        return qrConnect;
    }

    public static ConfigWiFiInfoModel getConfigWiFiInfoModel(String seed, boolean qrConnect, String deviceId) {
        ConfigWiFiInfoModel data = new ConfigWiFiInfoModel();
        data.setDeviceId(deviceId);
        data.setSeed(seed);
        data.setQrConnect(qrConnect);
        return data;
    }

    public static String getPreUserName(String username) {
        if(username.startsWith("hw-")){
            return username;
        } else {
            return "hw-" + username;
        }
    }

    public static String getUnPreUserName(String username) {
        if(username.startsWith("hw-")){
            return username.substring(3);
        } else {
            return username;
        }
    }


    public static String getLogHead(Class clas){
        return clas.getName();
    }
}
