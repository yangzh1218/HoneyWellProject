package com.wulian.sdk.demo;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.DeviceListCallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.UnBindDeviceCallBack;


import android.widget.Toast;

/**
 * Created by H122633 on 7/27/2016.
 */
public class WulianSDK implements CallBack {
    public static WulianSDK sdk;
    public static boolean loggedin = false;
    public Activity mActivity;

    public static WulianSDK getInstance() {
        if (sdk == null)
            sdk = new WulianSDK();
        return sdk;
    }

    public WulianSDK() {
    }

    public ErrorCode sdkInit(Activity lActivity) {
        Interface.getInstance().initRouteLib(lActivity.getApplicationContext(), "3v6WDvq8a4c4Fi0Koar61v2UifYXpvTu");
        return ErrorCode.Succeed;
    }

    public ErrorCode bindActivity(Activity lActivity) {
        mActivity = lActivity;
        Interface.getInstance().setContext(mActivity);
        Interface.getInstance().setCallBack(this);
        return ErrorCode.Succeed;
    }

    public boolean CheckLogin() {
        return loggedin;
    }

    public ErrorCode UserRegister(String sUserName, String sPassword) {
        Interface.getInstance().UserRegister(sUserName, sPassword);
        return ErrorCode.Succeed;
    }

    public ErrorCode Login(String sUserName, String sPassword) {
        Interface.getInstance().Login(sUserName, sPassword);
        return ErrorCode.Succeed;
    }

    public ErrorCode ChangePassword(String sOldPassword, String sNewPassword) {
        if (!loggedin) {
            return ErrorCode.Error_NotLogin;
        }
        Interface.getInstance().ChangePassword(sOldPassword, sNewPassword);
        return ErrorCode.Succeed;
    }

    public ErrorCode Logout() {
        if (!loggedin) {
            return ErrorCode.Error_NotLogin;
        }
        Interface.getInstance().Logout();
        return ErrorCode.Succeed;
    }

    public ErrorCode GetAlarmVideoInfo(String sDeviceId, String domain,int nAlarmId) {
        if (!loggedin) {
            return ErrorCode.Error_NotLogin;
        }
        Interface.getInstance().GetAlarmVideoInfo(sDeviceId, domain,nAlarmId);
        return ErrorCode.Succeed;
    }

    public ErrorCode OpenAlarmVideo(String did, String sStartTime, String sEndTime) {
        if (!loggedin) {
            return ErrorCode.Error_NotLogin;
        }
        Interface.getInstance().OpenAlarmVideo(did, 2, sStartTime, sEndTime);
        return ErrorCode.Succeed;
    }

    public ErrorCode OpenAlarmVideoList(int alarmId) {
        if (!loggedin) {
            return ErrorCode.Error_NotLogin;
        }
        Interface.getInstance().OpenAlarmVideoList(alarmId);
        return ErrorCode.Succeed;
    }

    public ErrorCode OpenCameraManage(String ipcUsername) {
        if (!loggedin) {
            return ErrorCode.Error_NotLogin;
        }
        Interface.getInstance().OpenCameraManage(ipcUsername);
        return ErrorCode.Succeed;
    }

    public ErrorCode GetDeviceList(DeviceListCallBack callBack) {
        if (!loggedin) {
            return ErrorCode.Error_NotLogin;
        }
        Interface.getInstance().getDeviceList(callBack);
        return ErrorCode.Succeed;
    }

    public ErrorCode unBindDevice(String deviceID,UnBindDeviceCallBack callBack) {
        if (!loggedin) {
            return ErrorCode.Error_NotLogin;
        }
        Interface.getInstance().unbindDevice(deviceID,callBack);
        return ErrorCode.Succeed;
    }
    public  ErrorCode SwitchWebDomain(String serverHttpsPath)
    {
        if(!loggedin)
        {
            return ErrorCode.Error_NotLogin;
        }
        Interface.getInstance().switchWebDomamin(serverHttpsPath);
        return ErrorCode.Succeed;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                default:
                    break;
            }
            if (msg.what == ErrorCode.Succeed.ordinal()) {
                Toast.makeText(mActivity, "===DoSucceed===", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mActivity, "===DoFailed===" + msg.what , Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String json) {
        switch (apiType) {
            case V3_PARTNER_LOGIN:
                loggedin = true;
                Interface.getInstance().registerSipAccount();
                break;
            default:
                break;
        }
        Message message = new Message();
        message.what = errorCode.ordinal();
        handler.sendMessage(message);
    }

    @Override
    public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {
        Message message = new Message();
        message.what = errorCode.ordinal();
        handler.sendMessage(message);
    }
}
