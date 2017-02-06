package com.wulian.sdk.android.oem.honeywell.ipc;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.controller.RouteLibraryController;
import com.wulian.routelibrary.controller.TaskResultListener;
import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.common.Common;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.DeviceDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.UserDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.AlarmCache;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.AlarmCacheImpl;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.DeviceCache;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.DeviceCacheImpl;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.UserInfoCachImpl;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.UserInfoCache;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Alarm;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.model.UserInfo;
import com.wulian.sdk.android.oem.honeywell.ipc.sip.SipFactory;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.AlarmListActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.DeviceListActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.play.ReplayVideoActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.MessageUtil;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 作者：Administrator on 2016/7/19 17:06
 * 邮箱：huihui@wuliangroup.com
 */
public class Interface extends Object implements TaskResultListener, SipCallBack {

    private static final int SHOW_TOAST=0;
    private static final int GET_ALARM_NUMS_TIMEOUT=1;
    private static final int DEFAULT_ALARM_NUM_SEND=30;

    static private Interface anInterface;
    private Context context;
    private CallBack callBack;
    private boolean login = false;
    private boolean needRefreshList=false;
    private String password;
    private String username;
    private final static String HW = "hw-";
    private final static String PREFIX_DEVICE="cmhw05";
    private final static String DEVICE_TYPE_CMIC = "cmic";
    private final static String DEVICE_TYPE_CMHW = "cmhw";
    private SDKSipCallBack sdkSipCallBack;
    private SipCallBack sipCallBack;
    private AlarmCntCallBack alarmCntCallBack;
    private boolean beginTransaction, endTransaction;

//    private int[] nAlarmID;
    private int haveGotAlarmInfoNum;
    private int AlarmInfoNums;
    private SparseIntArray alarmNumArray;
    private Map<String,Integer> mOnlineMap;
    private boolean isAlarmNumProgressing;
    private int alarmId;
    private String deviceId;
    boolean isGetAlarmFlag = false;
    boolean gotAlarmCnt = false;

    private Handler  handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_TOAST:
                    CustomToast.show(context, msg.getData().getString("toast"));
                    break;
                case GET_ALARM_NUMS_TIMEOUT:
                    if (!gotAlarmCnt) {
                        if (alarmCntCallBack != null) {
                            alarmCntEnd();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public static Interface getInstance() {
        if (anInterface == null) {
            anInterface = new Interface();
        }
        return anInterface;
    }

    private Interface() {

    }

    public void setContext(Context context) {
        this.context = context;
        haveGotAlarmInfoNum = 0;
        isAlarmNumProgressing=false;
        alarmNumArray=new SparseIntArray();
        mOnlineMap=new HashMap<String,Integer>();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public void detachContext(){
        this.context = null;
        this.callBack = null;
        handler.removeCallbacksAndMessages(null);
    }

    public boolean getNeedRefreshList() {
        return needRefreshList;
    }

    public void setNeedRefreshList(boolean value) {
        this.needRefreshList=value;
    }

    public void setSdkSipCallBack(SDKSipCallBack sdkSipCallBack) {
        this.sdkSipCallBack = sdkSipCallBack;
    }

    public void setSipCallBack(SipCallBack sipCallBack) {
        this.sipCallBack = sipCallBack;
    }


    public void setAlarmCntCallBack(AlarmCntCallBack alarmCntCallBack) {
        this.alarmCntCallBack = alarmCntCallBack;
    }

    public Enum<ErrorCode> initRouteLib(Context context, String secret) {
        RouteLibraryController.getInstance().initRouteLibrary(context, secret);
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> Login(String userName, String password) {
        RouteLibraryController.getInstance().V3PartnerLogin(HW + userName, password,
                this);
        this.password = password;
        this.username = userName;
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> Logout() {
        if (login) {
            RouteLibraryController.getInstance().V3Logout(UserDataRepository.getInstance().oauth(), this);
        }
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> UserRegister(String userName, String password) {
        userName = Common.getPreUserName(userName);
        RouteLibraryController.getInstance().V3PartnerRegister(userName, password,
                this);
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> handleChangePassword(String oldPassword, String newPassword) {
        password = newPassword;
        if (oldPassword.equals(UserDataRepository.getInstance().password())) {
            RouteLibraryController.getInstance().V3UserPassword(UserDataRepository.getInstance().oauth(), newPassword,
                    this);
            return ErrorCode.Succeed;
        } else {
            return ErrorCode.Error_ChangePwdError;
        }
    }

    public Enum<ErrorCode> ChangePassword(final String oldPassword,final String newPassword) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
               return handleChangePassword(oldPassword,newPassword);
        }else {
                RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                        new TaskResultListener() {
                            public void OnSuccess(RouteApiType apiType, String json) {
                                handleV3PartnerLogin(json);
                                handleChangePassword(oldPassword,newPassword);
                            }
                            public void OnFail(RouteApiType apiType, Exception exception) {
                                if(callBack!=null) {
                                    callBack.DoFailed(ErrorCode.Error_NoNetwork, apiType, exception);
                                }
                            }
                        });
                return ErrorCode.Succeed;
        }
    }

    public Enum<ErrorCode> OpenCameraManage(String ipcUsername) {
        if (login) {
            registerSipAccount();
            Intent intent = new Intent(context, DeviceListActivity.class);
            DeviceDataRepository.getInstance().initIpcUsername(ipcUsername);
            context.startActivity(intent);

        }
        return ErrorCode.Succeed;
    }





    private Enum<ErrorCode> handleGetUserDevices(String type,final DeviceListCallBack deviceCallback) {
        RouteLibraryController.getInstance().V3UserDevices(UserDataRepository.getInstance().oauth(), type, new TaskResultListener() {
            @Override
            public void OnSuccess(RouteApiType apiType, String jsonData) {

                ArrayList<Device> deviceList = new ArrayList<>();
                try {
                    JSONArray ownedDevices = new JSONObject(jsonData).getJSONArray("owned");
                    int len = ownedDevices.length();
                    Device device = null;
                    for (int i = 0; i < len; i++) {
                        device = Utils.parseBean(Device.class,
                                ownedDevices.getJSONObject(i));
                        device.setIs_BindDevice(true);
//                        if(LibraryLoger.getLoger()) {
//                            device.setSip_domain("test.sh.gg");
//                        }
                        checkNickName(device);
                        deviceList.add(device);
                    }
                    JSONArray sharedDevices = new JSONObject(jsonData).getJSONArray("shared");
                    len = sharedDevices.length();
                    for (int i = 0; i < len; i++) {
                        device = Utils.parseBean(Device.class,
                                sharedDevices.getJSONObject(i));
                        device.setIs_BindDevice(false);
//                        if(LibraryLoger.getLoger()) {
//                            device.setSip_domain("test.sh.gg");
//                        }
                        checkNickName(device);
                        deviceList.add(device);
                    }
                    if (deviceCallback != null) {
                        deviceCallback.getDeviceListSuccess(deviceList);
                    }
                } catch (Exception exception) {
                    if(deviceCallback!=null) {
                        deviceCallback.getDeviceListFail(exception);
                    }
                }
            }

            @Override
            public void OnFail(RouteApiType apiType, Exception exception) {
                if(deviceCallback!=null) {
                    deviceCallback.getDeviceListFail(exception);
                }
            }
        });
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> getDeviceList(final DeviceListCallBack deviceCcallBack) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleGetUserDevices("cmhw",deviceCcallBack);
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                            handleGetUserDevices("cmhw",deviceCcallBack);
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(deviceCcallBack!=null) {
                                deviceCcallBack.getDeviceListFail(exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
        }
    }




    private Enum<ErrorCode> handleUnbindDevice(String deviceId , final UnBindDeviceCallBack unbindCallback) {
        RouteLibraryController.getInstance().V3BindUnbind(UserDataRepository.getInstance().oauth(), deviceId, new TaskResultListener() {
            @Override
            public void OnSuccess(RouteApiType apiType, String json) {
                if(unbindCallback!=null) {
                    unbindCallback.UnBindDeviceSuccess();
                }
            }

            @Override
            public void OnFail(RouteApiType apiType, Exception exception) {
                if(unbindCallback!=null) {
                    unbindCallback.UnBindDeviceFail(exception);
                }
            }
        });
        return ErrorCode.Succeed;
    }

    public  Enum<ErrorCode> unbindDevice(final String deviceId,final UnBindDeviceCallBack unbindCallback) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleUnbindDevice(deviceId,unbindCallback);
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                            if(unbindCallback!=null) {
                                handleUnbindDevice(deviceId, unbindCallback);
                            }
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(unbindCallback!=null) {
                                unbindCallback.UnBindDeviceFail(exception);
                            }
                        }
                    });
        }
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> handleV3AppFlag(String deviceId) {
        RouteLibraryController.getInstance().V3AppFlag(UserDataRepository.getInstance().oauth(), deviceId, this);
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> V3AppFlag(final String deviceId) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleV3AppFlag(deviceId);
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                              handleV3AppFlag(deviceId);
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(callBack!=null) {
                                callBack.DoFailed(ErrorCode.Error_NoNetwork,  RouteApiType.V3_APP_FLAG, exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
        }
    }

    public Enum<ErrorCode> handleBindingCheck(String deviceId) {
            RouteLibraryController.getInstance().V3BindCheck(UserDataRepository.getInstance().oauth(), deviceId, this);
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> BindingCheck(final String deviceId) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleBindingCheck(deviceId);
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                            handleBindingCheck(deviceId);
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(callBack!=null) {
                                callBack.DoFailed(ErrorCode.Error_NoNetwork, RouteApiType.V3_BIND_CHECK, exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
        }
    }

    public Enum<ErrorCode> handleBindUnbind(String deviceId) {
        RouteLibraryController.getInstance().V3BindUnbind(UserDataRepository.getInstance().oauth(), deviceId, this);
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> BindUnbind(final String deviceId) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleBindUnbind(deviceId);
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                            handleBindUnbind(deviceId);
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(callBack!=null) {
                                callBack.DoFailed(ErrorCode.Error_NoNetwork, RouteApiType.V3_BIND_UNBIND, exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
        }
    }

    public Enum<ErrorCode> handleUserDevice(String deviceId, String nickName, String desc) {
        RouteLibraryController.getInstance().V3UserDevice(UserDataRepository.getInstance().oauth(), deviceId, nickName, desc, this);
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> UserDevice(final String deviceId,final  String nickName,final  String desc) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleUserDevice(deviceId,nickName,desc);
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                            handleUserDevice(deviceId,nickName,desc);
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(callBack!=null) {
                                callBack.DoFailed(ErrorCode.Error_NoNetwork, RouteApiType.V3_USER_DEVICE, exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
        }
    }

    public Enum<ErrorCode> handleBindResult(String deviceId) {
        RouteLibraryController.getInstance().V3BindResult(UserDataRepository.getInstance().oauth(), deviceId, this);
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> BindResult(final String deviceId) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleBindResult(deviceId);
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                            handleBindResult(deviceId);
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(callBack!=null) {
                                callBack.DoFailed(ErrorCode.Error_NoNetwork, RouteApiType.V3_BIND_RESULT, exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
        }
    }

    public Enum<ErrorCode> handleShareResponse(String deviceId, String username, String type) {
        RouteLibraryController.getInstance().V3ShareResponse(UserDataRepository.getInstance().oauth(), deviceId, Common.getPreUserName(username), type, this);
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> ShareResponse(final String deviceId, final String username,final String type) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleShareResponse(deviceId,username,type);
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                            handleShareResponse(deviceId,username,type);
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(callBack!=null) {
                                callBack.DoFailed(ErrorCode.Error_NoNetwork, RouteApiType.V3_SHARE_RESPONSE, exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
        }
    }

    public Enum<ErrorCode> handleShareRequest(String deviceId, String desc) {
        RouteLibraryController.getInstance().V3ShareRequest(UserDataRepository.getInstance().oauth(), deviceId, desc, this);
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> ShareRequest(final String deviceId,final String desc) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleShareRequest(deviceId,desc);
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                            handleShareRequest(deviceId,desc);
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(callBack!=null) {
                                callBack.DoFailed(ErrorCode.Error_NoNetwork, RouteApiType.V3_SHARE_REQUEST, exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
        }
    }

    public Enum<ErrorCode> handleShare(String deviceId, String username) {
        RouteLibraryController.getInstance().V3Share(UserDataRepository.getInstance().oauth(), deviceId, Common.getPreUserName(username), this);
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> Share(final String deviceId,final String username) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleShare(deviceId,username);
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                            handleShare(deviceId,username);
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(callBack!=null) {
                                callBack.DoFailed(ErrorCode.Error_NoNetwork, RouteApiType.V3_SHARE, exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
        }
    }

    public Enum<ErrorCode> handleUnshareRequest(String deviceId, String username) {
        String realUsername;
        if (TextUtils.isEmpty(username)) {
            realUsername = "";
        } else {
            realUsername = Common.getPreUserName(username);
        }
        RouteLibraryController.getInstance().V3UnShare(UserDataRepository.getInstance().oauth(), deviceId, realUsername, this);
        return ErrorCode.Succeed;
    }


    public Enum<ErrorCode> UnshareRequest(final  String deviceId,final String username) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleUnshareRequest(deviceId,username);
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                            handleUnshareRequest(deviceId,username);
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(callBack!=null) {
                                callBack.DoFailed(ErrorCode.Error_NoNetwork,  RouteApiType.V3_SHARE_UNSHARE, exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
        }
    }

    public Enum<ErrorCode> handleOwners() {
        RouteLibraryController.getInstance().V3Owners(UserDataRepository.getInstance().oauth(), this);
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> Owners() {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleOwners();
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                            handleOwners();
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(callBack!=null) {
                                callBack.DoFailed(ErrorCode.Error_NoNetwork, RouteApiType.V3_DEVICE_OWNERS, exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
        }
    }

    public Enum<ErrorCode> handleShareList(String did) {
        RouteLibraryController.getInstance().V3ShareList(UserDataRepository.getInstance().oauth(), did, this);
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> ShareList(final String did) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleShareList(did);
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                            handleShareList(did);
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(callBack!=null) {
                                callBack.DoFailed(ErrorCode.Error_NoNetwork, RouteApiType.V3_SHARE_LIST, exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
        }
    }


    public Enum<ErrorCode> handleTokenDownloadReplay(String deviceId, String sdomain) {
        RouteLibraryController.getInstance().V3TokenDownloadReplay(UserDataRepository.getInstance().oauth(), deviceId, sdomain, this);
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> TokenDownloadReplay(final String deviceId,final String sdomain) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleTokenDownloadReplay(deviceId,sdomain);
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                            handleTokenDownloadReplay(deviceId,sdomain);
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(callBack!=null) {
                                callBack.DoFailed(ErrorCode.Error_NoNetwork,  RouteApiType.V3_TOKEN_DOWNLOAD_REPLAY, exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
        }
    }

    public Enum<ErrorCode> V3VersionStable( final CallBack versionCallBack) {
            RouteLibraryController.getInstance().V3VersionStable(PREFIX_DEVICE,1,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            if(versionCallBack!=null) {
                                versionCallBack.DoSucceed(ErrorCode.Succeed,apiType, json);
                            }
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(versionCallBack!=null) {
                                versionCallBack.DoFailed(ErrorCode.Error_NoNetwork,apiType, exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> handleUserDevices(String type) {
        RouteLibraryController.getInstance().V3UserDevices(UserDataRepository.getInstance().oauth(), type, this);
        return ErrorCode.Succeed;
    }
    public Enum<ErrorCode> UserDevices(final String type) {
        UserInfo userInfo=UserDataRepository.getInstance().getUserInfo();
        if (login&&userInfo!=null&&userInfo.isExpire()) {
            return handleUserDevices(type);
        }else {
            RouteLibraryController.getInstance().V3PartnerLogin(HW +   this.username, this.password,
                    new TaskResultListener() {
                        public void OnSuccess(RouteApiType apiType, String json) {
                            handleV3PartnerLogin(json);
                            handleUserDevices(type);
                        }
                        public void OnFail(RouteApiType apiType, Exception exception) {
                            if(callBack!=null) {
                                callBack.DoFailed(ErrorCode.Error_NoNetwork, RouteApiType.V3_USER_DEVICES, exception);
                            }
                        }
                    });
            return ErrorCode.Succeed;
        }
    }

    private void handleV3PartnerLogin(String jsonData) {
        try {
            UserInfoCache userInfoCache = new UserInfoCachImpl(ContextHolder.getContext());
            DeviceCache deviceCache = new DeviceCacheImpl(ContextHolder.getContext());
            AlarmCache alarmCache = new AlarmCacheImpl(ContextHolder.getContext());
            UserDataRepository.getInstance().init(ContextHolder.getContext(), userInfoCache);
            DeviceDataRepository.getInstance().init(ContextHolder.getContext(), deviceCache, alarmCache);
            JSONObject json = new JSONObject(jsonData);
            UserInfo userInfo = Utils.parseBean(UserInfo.class, json);
            userInfo.setUsername(username);
            userInfo.setPassword(password);
            UserDataRepository.getInstance().initUserInfoCache(userInfo);
//                    UserDataRepository.getInstance().setUsername(username);
//                    UserDataRepository.getInstance().setPassword(password);
            login = true;
        } catch (JSONException e) {
        }
    }

    @Override
    public void OnSuccess(RouteApiType apiType, String jsonData) {
        if(!TextUtils.isEmpty(jsonData)) {
            LibraryLoger.d("jsonData is:" + jsonData);
        }
        LibraryLoger.d("Interface OnSuccess :" + apiType.name());
        switch (apiType) {
            case V3_PARTNER_REGISTER:
                break;
            case V3_PARTNER_LOGIN:
                handleV3PartnerLogin(jsonData);
                break;
            case V3_SERVER:
                break;
            case V3_USER_PASSWORD:
                UserDataRepository.getInstance().setPassword(password);
                break;
            case V3_LOGOUT:
                UserDataRepository.getInstance().evictAll();
                DeviceDataRepository.getInstance().evictAll();
                login = false;
                break;
            case V3_BIND_UNBIND:
                break;
            case V3_USER_DEVICES:
                ArrayList<Device> deviceList = new ArrayList<>();
                try {
                    JSONArray ownedDevices = new JSONObject(jsonData).getJSONArray("owned");
                    int len = ownedDevices.length();
                    Device device = null;
                    for (int i = 0; i < len; i++) {
                        device = Utils.parseBean(Device.class,
                                ownedDevices.getJSONObject(i));
                        device.setIs_BindDevice(true);
//                        if(LibraryLoger.getLoger()) {
//                            device.setSip_domain("test.sh.gg");
//                        }
                        checkNickName(device);
                        deviceList.add(device);
                    }
                    JSONArray sharedDevices = new JSONObject(jsonData).getJSONArray("shared");
                    len = sharedDevices.length();
                    for (int i = 0; i < len; i++) {
                        device = Utils.parseBean(Device.class,
                                sharedDevices.getJSONObject(i));
                        device.setIs_BindDevice(false);
//                        if(LibraryLoger.getLoger()) {
//                            device.setSip_domain("test.sh.gg");
//                        }
                        checkNickName(device);
                        deviceList.add(device);
                    }
                    DeviceDataRepository.getInstance().initUserInfoCache(deviceList);
                    if (isGetAlarmFlag) {
                       boolean isGetAlarm=iterDeviceGetMultiAlarm();
//                        if(!isGetAlarm&&alarmCntCallBack!=null) {
//                            alarmCntCallBack.DoSucceed(ErrorCode.Succeed, SipMsgApiType.QUERY_MULTI_ALARY_EVENT, com.alibaba.fastjson.JSON.toJSONString(alarmNumMap));
//                        }
                        isGetAlarmFlag = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        if(callBack!=null) {
            callBack.DoSucceed(ErrorCode.Succeed, apiType, jsonData);
        }
    }

    @Override
    public void OnFail(RouteApiType apiType, Exception exception) {
        LibraryLoger.d("Interface OnFail :" + apiType.name());
        ErrorCode errorCode = ErrorCode.Error_NoNetwork;
        if (exception.getMessage().contains("Failed to connect")) {
            errorCode = ErrorCode.Error_NoNetwork;
        } else {
            switch (apiType) {
                case V3_PARTNER_REGISTER:
                    errorCode = ErrorCode.Error_RegError;
                    break;
                case V3_PARTNER_LOGIN:
                    errorCode = ErrorCode.Error_LoginError;
                    break;
                case V3_SERVER:
                    break;
                case V3_USER_PASSWORD:
                    errorCode = ErrorCode.Error_ChangePwdError;
                    break;
                case V3_LOGOUT:
                    break;
                case V3_BIND_UNBIND:
                    break;
                default:
                    break;
            }
        }
        if(callBack!=null) {
            callBack.DoFailed(errorCode, apiType, exception);
        }
    }

    /*------------------------------------------------------------------------------------------ sip -------------------------------------------------------------*/


    public Enum<ErrorCode> GetAlarmVideoInfo(String sDeviceId, String domain,int nAlarmId) {
        /**added by huihui 2016-08-26
         *这个地方一定要设置alarmCntCallBack = null;回调函数的交叉调用导致的
         */
        alarmCntCallBack = null;
        alarmId = nAlarmId;
        deviceId = sDeviceId;
        queryAlarmEvent(sDeviceId,domain, nAlarmId);
        return ErrorCode.Succeed;
    }



    public boolean GetAlarmVideoNum(int[] nAlarmID, int timeOut) {

            if(!alarmCntBegin()) {
                return false;
            }
//        this.nAlarmID = nAlarmID;
            alarmNumArray.clear();

            haveGotAlarmInfoNum = 0;
            for (int i = 0; i < nAlarmID.length; ++i) {
                alarmNumArray.put(nAlarmID[i], 0);
//            alarmNumMap.put(nAlarmID[i], 0);
            }
            if (DeviceDataRepository.getInstance().deviceList() == null || DeviceDataRepository.getInstance().deviceList().size() == 0) {
                Interface.getInstance().UserDevices("cmhw");
                isGetAlarmFlag = true;
            } else {
                boolean isGetAlarm = iterDeviceGetMultiAlarm();
                if (!isGetAlarm && alarmCntCallBack != null) {
                    alarmCntCallBack.DoSucceed(ErrorCode.Succeed, SipMsgApiType.QUERY_MULTI_ALARY_EVENT, getJsonString(alarmNumArray));
                }
            }
            handler.removeMessages(GET_ALARM_NUMS_TIMEOUT);
            handler.sendEmptyMessageDelayed(GET_ALARM_NUMS_TIMEOUT, timeOut);
            return true;

    }

    public Enum<ErrorCode> OpenAlarmVideo(String sDeviceId, int nAlarmId, String sStartTime, String sEndTime) {
        try {
//            Log.d("wulian.icam", Common.getLogHead(this.getClass()) + " beginTransaction:" + beginTransaction + "endTransaction" + endTransaction);
            if (beginTransaction && !endTransaction) {
                handler.sendMessage(MessageUtil.set(SHOW_TOAST, "toast", context.getString(R.string.data_updating)));
                return ErrorCode.Succeed;
            }
            Intent intent = new Intent(context, ReplayVideoActivity.class);
            intent.putExtra("deviceId", sDeviceId);
            intent.putExtra("sStartTime", sStartTime);
            intent.putExtra("sEndTime", sEndTime);
            intent.putExtra("nAlarmId", nAlarmId);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> OpenAlarmVideoList(int alarmId) {
        try {
//            Log.d("wulian.icam", Common.getLogHead(this.getClass()) + " beginTransaction:" + beginTransaction + "endTransaction" + endTransaction);
            if (beginTransaction && !endTransaction) {
                handler.sendMessage(MessageUtil.set(SHOW_TOAST, "toast", context.getString(R.string.data_updating)));
                return ErrorCode.Succeed;
            }
            beginTransaction = false;
            endTransaction = false;
            Intent intent = new Intent(context, AlarmListActivity.class);
            intent.putExtra("alarmId", alarmId);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ErrorCode.Succeed;
    }

    public Enum<ErrorCode> registerSipAccount() {
        //     1、初始化sip
        if (login) {
            SipFactory.getInstance().initSip(context.getApplicationContext());

            SipFactory.getInstance().registerAccount(UserDataRepository.getInstance().suid(), UserDataRepository.getInstance().sdomain(), UserDataRepository.getInstance().password());
        }
        return ErrorCode.Succeed;
    }

    public void makeCall(String deviceCallUrl) {
        SipFactory.getInstance().makeCall(deviceCallUrl,
                SipFactory.getInstance().getSipProfile());
    }

    public void queryAlarmEvent(String sDeviceId,String domain, int nAlarmId) {
        if (SipFactory.getInstance().getSipProfile() == null) {
            CustomToast.show(context, "sipProfile is null");
            return;
        }
        String requestStr=SipHandler.QueryAlarmEvent("sip:" + sDeviceId + "@"
                        + domain, SipFactory.getInstance().seq++,
                nAlarmId);
//        Log.d("PML","The request queryAlarmEvent string is:"+requestStr);
        SipFactory.getInstance().sendMessage(
                sDeviceId + "@" + domain,requestStr
                , SipFactory.getInstance().getSipProfile());
    }

    public void queryAlarmEvent(String sDeviceId,String domain, SparseIntArray nAlarmIdArray) {
        if (SipFactory.getInstance().getSipProfile() == null) {
            CustomToast.show(context, "sipProfile is null");
            return;
        }
        int[] tempArray=new int[nAlarmIdArray.size()];
        for(int i=0;i<nAlarmIdArray.size();i++) {
            tempArray[i]=nAlarmIdArray.keyAt(i);
        }
        String requestStr=SipHandler.QueryMultiAlarmEvent("sip:" + sDeviceId + "@"
                        + domain, SipFactory.getInstance().seq++,
                tempArray);
//        Log.d("PML","xwoofdsfsdlfjldsfjdsfldsfjds");
//        Log.d("PML","The request queryAlarmEvent Array string is:"+requestStr);
        SipFactory.getInstance().sendMessage(
                sDeviceId + "@" + domain,requestStr, SipFactory.getInstance().getSipProfile());
    }

    public void queryAlarmEvent(String sDeviceId,String domain, int[] tempArray) {
        if (SipFactory.getInstance().getSipProfile() == null) {
            CustomToast.show(context, "sipProfile is null");
            return;
        }
        String requestStr=SipHandler.QueryMultiAlarmEvent("sip:" + sDeviceId + "@"
                        + domain, SipFactory.getInstance().seq++,
                tempArray);
//        Log.d("PML","xwoofdsfsdlfjldsfjdsfldsfjds");
//        Log.d("PML","The request queryAlarmEvent Array string is:"+requestStr);
        SipFactory.getInstance().sendMessage(
                sDeviceId + "@" + domain,requestStr, SipFactory.getInstance().getSipProfile());
    }

    public void controlHistoryRecordProgress(long time, String sessionID, String deviceControlUrl) {
        String sip_ok = "sip:" + deviceControlUrl;
        SipController.getInstance().sendMessage(
                deviceControlUrl,
                SipHandler.ControlHistoryRecordProgress(sip_ok, SipFactory.getInstance().seq++,
                        sessionID, time), SipFactory.getInstance().getSipProfile());
    }

    public void controlAlarmHistoryRecordProgress(long time, String sessionID, int period,boolean local,int alarmID,String deviceControlUrl) {
        String sip_ok = "sip:" + deviceControlUrl;
        SipController.getInstance().sendMessage(
                deviceControlUrl,
                SipHandler.ControlAlarmHistoryRecordProgress(sip_ok, SipFactory.getInstance().seq++,period,local,alarmID,
                        sessionID, time), SipFactory.getInstance().getSipProfile());
    }

    Lock lock = new ReentrantLock();

    private void handleAlarmData(String xmlData) {

        StringReader xmlReader = new StringReader(xmlData);
        XmlPullParserFactory pullFactory;
        try {
            pullFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = pullFactory.newPullParser();
            xmlPullParser.setInput(xmlReader); // 保存创建的xml
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String localName = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        localName = xmlPullParser.getName();
//                        Log.d("PML","localName is:"+localName);
                        if("alarmID".equalsIgnoreCase(localName)&& alarmCntCallBack != null) {
                                            lock.lock();
                            String fileSize=xmlPullParser.getAttributeValue(2);
                            try {
                                int innerAlarmID = Integer.parseInt(xmlPullParser.nextText());
//                                Log.d("PML","innerAlarmID is:"+innerAlarmID);
                                if (!TextUtils.isEmpty(fileSize) && !fileSize.equalsIgnoreCase("0")) {
                                    resetAlarmNum(innerAlarmID);
                                }
                            }catch(NumberFormatException e) {

                            }

                                            lock.unlock();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        if (haveGotAlarmInfoNum == AlarmInfoNums && !gotAlarmCnt) {
            alarmCntEnd();
        }
    }

    @Override
    public void SipDataReturn(boolean isSuccess, SipMsgApiType apiType,
                              String xmlData, String from, String to) {
//        if(!TextUtils.isEmpty(xmlData)) {
//            Log.d("PML", xmlData);
//        }
        if (isSuccess) {
            switch (apiType) {
                case QUERY_MULTI_ALARY_EVENT: {
                    String uri = Utils.getParamFromXml(xmlData,
                            "uri");
                    if (!TextUtils.isEmpty(uri)) {
                        String deviceID = uri;
                        if (uri.startsWith("sip")) {
                            deviceID = uri.substring(4, 24);
                        }else {
                            deviceID = uri.substring(0, 20);
                        }
                        if (mOnlineMap.containsKey(deviceID)&&mOnlineMap.get(deviceID)==0) {
                            haveGotAlarmInfoNum++;
                            mOnlineMap.put(deviceID,1);
                        }
                    }
                    handleAlarmData(xmlData);
                }
                        break;
                case QUERY_ALARM_EVENT:
//                    Log.d("wulian.icam", Common.getLogHead(this.getClass()) + xmlData + " haveGotAlarmInfoNum:" + haveGotAlarmInfoNum);
                    String uri = Utils.getParamFromXml(xmlData,
                            "uri");
                    String alarmIDStr=Utils.getParamFromXml(xmlData,
                            "alarmID");
                    if(!TextUtils.isEmpty(alarmIDStr)) {
                        try {
                            int alarmId = Integer.valueOf(alarmIDStr);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("nAlarmId", alarmId);
                            String startTime = Utils.getParamFromXml(xmlData, "startTime");
                            jsonObject.put("sStartTime", startTime);
                            String endTime = Utils.getParamFromXml(xmlData, "endTime");
                            jsonObject.put("sEndTime", endTime);
                            String session = Utils.getParamFromXml(xmlData, "sessioin");
                            jsonObject.put("session", session);
                            jsonObject.put("nDataLength", Utils.getParamFromXml(xmlData,
                                    "fileSize"));
                            jsonObject.put("uri", uri);
                            if (alarmCntCallBack != null) {
                                lock.lock();
                                if (!startTime.equals("0")) {
                                    resetAlarmNum(alarmId);
                                }
                                haveGotAlarmInfoNum++;
                                if (haveGotAlarmInfoNum == DeviceDataRepository.getInstance().deviceList().size() * alarmNumArray.size() && !gotAlarmCnt) {
                                    alarmCntEnd();
                                }
                                lock.unlock();
                            } else {
                                if(alarmId != this.alarmId && !uri.contains(deviceId)){
                                    return;
                                }
                                if (startTime.equals("0")) {
                                    jsonObject.put("nError", ErrorCode.Error_NoExistVideo);
                                    if (sdkSipCallBack != null) {
                                        sdkSipCallBack.DoFailed(ErrorCode.Error_NoExistVideo, SipMsgApiType.QUERY_ALARM_EVENT, jsonObject.toString());
                                    }
                                } else {
                                    jsonObject.put("nError", ErrorCode.Succeed);
                                    if (sdkSipCallBack != null) {
                                        sdkSipCallBack.DoSucceed(ErrorCode.Succeed, SipMsgApiType.QUERY_ALARM_EVENT, jsonObject.toString());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    if (sipCallBack != null) {
                        sipCallBack.SipDataReturn(isSuccess, apiType, xmlData, from, to);
                    }
                    break;
            }
        } else {
            switch (apiType) {
                case QUERY_ALARM_EVENT:
                    if (alarmCntCallBack != null) {
                        lock.lock();
                        haveGotAlarmInfoNum++;
                        if (haveGotAlarmInfoNum == DeviceDataRepository.getInstance().deviceList().size() * alarmNumArray.size() && !gotAlarmCnt) {
                            alarmCntEnd();
                        }
                        lock.unlock();
                    } else {
                        if(alarmId != this.alarmId){
                            return;
                        }
                        sdkSipCallBack.DoFailed(ErrorCode.Error_NoExistVideo, SipMsgApiType.QUERY_ALARM_EVENT, "");
                    }
                    break;
                case QUERY_MULTI_ALARY_EVENT:
                    if (alarmCntCallBack != null) {
                        lock.lock();
                        String uri = Utils.getParamFromXml(xmlData,
                                "uri");
                        if (!TextUtils.isEmpty(uri)) {
                            String deviceID = uri;
                            if (uri.startsWith("sip")) {
                                deviceID = uri.substring(4, 24);
                                if (mOnlineMap.containsKey(deviceID)&&mOnlineMap.get(deviceID)==0) {
                                    haveGotAlarmInfoNum++;
                                    mOnlineMap.put(deviceID,1);
                                }
                            }
                        };
                        if (haveGotAlarmInfoNum ==AlarmInfoNums&& !gotAlarmCnt) {
                            alarmCntEnd();
                        }
                        lock.unlock();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private String getJsonString(SparseIntArray alarmArray) {
        String result="";
        JSONObject jsonObject=new JSONObject();
        try {
//            Log.d("PML","alarm num is:"+alarmArray.size());
            for (int i = 0; i < alarmArray.size(); i++) {
//                Log.d("PML","key is:"+alarmArray.keyAt(i)+";value is:"+alarmArray.valueAt(i));
                jsonObject.put(String.valueOf(alarmArray.keyAt(i)), alarmArray.valueAt(i));
            }
            result=jsonObject.toString();
        }catch(JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    private void alarmCntEnd() {
//        Log.d("PML","alarmCntEnd");
//        Log.d("PML","alarm sss"+com.alibaba.fastjson.JSON.toJSONString(alarmNumMap));
        if(alarmCntCallBack!=null) {
            alarmCntCallBack.DoSucceed(ErrorCode.Succeed, SipMsgApiType.QUERY_MULTI_ALARY_EVENT, getJsonString(alarmNumArray));
        }
        gotAlarmCnt = true;
        endTransaction = true;
        beginTransaction = false;
    }

    private boolean alarmCntBegin(){
        if (beginTransaction && !endTransaction) {
            handler.sendMessage(MessageUtil.set(SHOW_TOAST, "toast", context.getString(R.string.data_updating)));
            return false;
        }
        gotAlarmCnt = false;
        beginTransaction = true;
        endTransaction = false;
        return true;
    }
    public  void  switchWebDomamin(String serverHttpsPath)
    {
        if(!TextUtils.isEmpty(serverHttpsPath))
        {
            RouteLibraryController.getInstance().setLibraryPath(serverHttpsPath);
        }
    }

    /**
     * @param device
     * @Function 对服务器返回的设备昵称默认空值国际化
     * @author Wangjj
     * @date 2015年5月21日
     */
    private void checkNickName(Device device) {
        String nickName = device.getNick();
        if (TextUtils.isEmpty(nickName)) {
            String deviceId = device.getDid();
            // 采用默认的规则命名
            if (deviceId.toLowerCase(Locale.ENGLISH).startsWith("cmhw01")
                    || deviceId.toLowerCase(Locale.ENGLISH)
                    .startsWith("cmhw04")) {
                nickName = context.getResources().getString(R.string.main_cmic01name)
                        + deviceId.substring(deviceId.length() - 4);
            } else if (deviceId.toLowerCase(Locale.ENGLISH)
                    .startsWith("cmhw03")
                    || deviceId.toLowerCase(Locale.ENGLISH)
                    .startsWith("cmhw05")) {
                nickName = context.getResources().getString(R.string.main_cmic03name)
                        + deviceId.substring(deviceId.length() - 4);
            } else if (deviceId.toLowerCase(Locale.ENGLISH)
                    .startsWith("cmhw02")) {
                nickName = context.getResources().getString(R.string.main_cmic02name)
                        + deviceId.substring(deviceId.length() - 4);
            } else {
                nickName = deviceId.substring(deviceId.length() - 4);
            }
            device.setNick(nickName);
        }
    }

    private void resetAlarmNum(int alarmId) {
        if (alarmNumArray.get(alarmId) != 0) {
            alarmNumArray.put(alarmId, alarmNumArray.get(alarmId) + 1);
        } else {
            alarmNumArray.put(alarmId, 1);
        }
    }

    private boolean iterDeviceGetMultiAlarm1() {
        List<Device> tempDeviceList=DeviceDataRepository.getInstance().deviceList();
        if (tempDeviceList!= null&&tempDeviceList .size()>0&&alarmNumArray.size()>0) {
//            for (int i = 0; i < nAlarmID.length; ++i) {
//                for (int j = 0; j < tempDeviceList.size(); ++j) {
//                    queryAlarmEvent(tempDeviceList.get(j).getDid(),tempDeviceList.get(j).getSip_domain(), nAlarmID[i]);
//                }
//            }

            for (int j = 0; j < tempDeviceList.size(); ++j) {
//                if(tempDeviceList.get(j).getOnline()==1) {

                    for (int i = 0; i < alarmNumArray.size(); ++i) {
                        queryAlarmEvent(tempDeviceList.get(j).getDid(), tempDeviceList.get(j).getSip_domain(), alarmNumArray .keyAt(i));
                    }
//                }
            }
            return true;
        }else {
            return false;
        }
    }

    private void queryMultiAlarmSnipSnippet(String deviceID,String deviceDomain) {
        int AlarmLength=alarmNumArray.size();
        int[] tempAlarmNumArray=new int[AlarmLength];
        for(int i=0;i<AlarmLength;i++) {
            tempAlarmNumArray[i]=alarmNumArray.keyAt(i);
        }
        int AllSnippets=(int)Math.ceil((Float)(AlarmLength*1.0f)/DEFAULT_ALARM_NUM_SEND);
        for(int i=1;i<=AllSnippets;i++) {
            int length=0;
            if(i==AllSnippets) {
                 length=AlarmLength-(i-1)*DEFAULT_ALARM_NUM_SEND;
            }else {
                 length=DEFAULT_ALARM_NUM_SEND;
            }
            int[] snippetArray=new int[length];
            for(int j=0;j<length;j++) {
                snippetArray[j]=tempAlarmNumArray[(i-1)*DEFAULT_ALARM_NUM_SEND+j];
            }
            queryAlarmEvent(deviceID,deviceDomain,snippetArray);
        }
    }

    private boolean iterDeviceGetMultiAlarm() {
        List<Device> tempDeviceList=DeviceDataRepository.getInstance().deviceList();
//        LibraryLoger.d("iterDeviceGetMultiAlarm tempDeviceList size is:"+tempDeviceList.size());
        if (tempDeviceList!= null&&tempDeviceList .size()>0&&alarmNumArray.size()>0) {
//            LibraryLoger.d("nAlarmID is:");
//            for(int i=0;i<nAlarmID.length;i++) {
//                LibraryLoger.d("i :"+i+";is:"+nAlarmID[i]);
//            }
            mOnlineMap.clear();
            AlarmInfoNums=0;
            for (int j = 0; j < tempDeviceList.size(); ++j) {
                if(tempDeviceList.get(j).getOnline()==1) {
                    AlarmInfoNums++;
                    mOnlineMap.put(tempDeviceList.get(j).getDid(),0);
                    queryMultiAlarmSnipSnippet(tempDeviceList.get(j).getDid(), tempDeviceList.get(j).getSip_domain());
                }
            }
            if(mOnlineMap.size()==0) {
                return false;
            }else {
                return true;
            }
        }else {
            return false;
        }
    }
}
