package com.wulian.siplibrary.api;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.wulian.siplibrary.Configproperty;
import com.wulian.siplibrary.manage.SipAccountBuild;
import com.wulian.siplibrary.manage.SipCallSession;
import com.wulian.siplibrary.manage.SipProfile;
import com.wulian.siplibrary.pjsip.PjSipService;
import com.wulian.siplibrary.utils.WulianDefaultPreference;
import com.wulian.siplibrary.utils.WulianLog;

public class SipController {
    private static final String THIS_FILE = "SipController";
    private static SipController gInstance = null;
    private PjSipService pjService;

    // private SipService sipService;

    public static SipController getInstance() {
        if (gInstance == null) {
            gInstance = new SipController();
        }
        if (gInstance.pjService == null) {
            gInstance.pjService = new PjSipService();
        }
        return gInstance;
    }

    public  void setSipSystemLibPath(String path) {
        if(TextUtils.isEmpty(path)) {
            throw new IllegalArgumentException("Please input valid path");
        }
        WulianDefaultPreference.setsSipSystemLibPath(path);
    }

    // 创建Sip
    public boolean CreateSip(Context context, boolean isLan) {
        if (pjService.loadStack()) {
            pjService.setIsLan(isLan);
            WulianLog.d(THIS_FILE, "CreateSip");
            if (pjService.sipStart()) {
                return true;
            }
            return false;
        }
        return false;
    }

    public void SetPref(Context context){
        pjService.setContext(context);
        pjService.setPref(context);
    }

    // 注销Sip
    public boolean DestroySip() {
        if (pjService != null) {
            boolean result = pjService.sipStop();
            pjService = null;
            return result;
        }
        return false;
    }

    public boolean isWiredHeadsetOn() {
        if (pjService != null) {
            return this.pjService.isWiredHeadsetOn();
        }
        return false;
    }

    public int getCallStream() {
        if (pjService != null) {
            return this.pjService.getCallStream();
        }
        return -1;
    }

    public void AdjustCurrentVolume() {
        if (pjService != null) {
            this.pjService.AdjustCurrentVolume();
        }
    }

    // 添加账号
    public SipProfile registerAccount(String accountUserName,
                                      String accountServer, String accountPassword) {
        WulianLog.d(THIS_FILE, "registerAccount");
        if (pjService != null) {
            SipAccountBuild sipBuild = new SipAccountBuild(accountUserName,
                    accountUserName, accountServer, accountPassword);
            SipProfile profile = new SipProfile();
            profile = sipBuild.buildAccount(profile);
            boolean result = pjService.addAccount(profile);
            if (result) {
                return profile;
            }
        }
        return null;
    }

    // 添加账号
    //SipDomain RegURI
    public SipProfile registerAccount(String accountUserName,
                                      String accountServer, String accountPassword,String sipDomain) {
        WulianLog.d(THIS_FILE, "registerAccount");
        if (pjService != null) {
            SipAccountBuild sipBuild = new SipAccountBuild(accountUserName,
                    accountUserName, accountServer, accountPassword,sipDomain);
            SipProfile profile = new SipProfile();
            profile = sipBuild.buildAccount(profile);
            boolean result = pjService.addAccount(profile);
            if (result) {
                return profile;
            }
        }
        return null;
    }

    public SipProfile registerAccount(SipProfile profile) {
        WulianLog.d(THIS_FILE, "registerAccount");
        if (pjService != null) {
            boolean result = pjService.addAccount(profile);
            if (result) {
                return profile;
            }
        }
        return null;
    }

    public boolean registerLocalAccount() {
        WulianLog.d(THIS_FILE, "registerLocalAccount");
        if (pjService != null) {
            return pjService.addLocalAccount();
        }
        return false;
    }

    public int getAccountInfo(SipProfile profile) {
        if (pjService != null) {
            return pjService.getAccountInfo(profile);
        }
        return -3;
    }

    // 获取Nat类型
    public void detectNatType() {
        WulianLog.d(THIS_FILE, "detectNatType 111");
        if (pjService != null) {

            pjService.detectNatType();
        }
    }

    // 获取通话信息
    public String getCallInfos(int callId) {
        if (pjService != null) {
            return pjService.getCallInfos(callId);
        }
        return "";
    }

    // 获取通话信息
    public String getCallSpeedInfos(int callId) {
        if (pjService != null) {
            return pjService.getCallSpeedInfos(callId);
        }
        return "";
    }

    public String getCallNatInfos(int callId) {
        if (pjService != null) {
            return pjService.getCallNatInfos(callId);
        }
        return "";
    }

    // 注销账号
    public boolean unregistenerAccount(SipProfile profile) {
        if (pjService != null && profile != null) {
            return pjService.unregistener(profile);
        }
        return false;
    }

    // 发送消息
    public boolean sendMessage(String remoteFrom, String message,
                               SipProfile profile) {
        WulianLog.d("PML", "sendMessage remoteFrom is:" + remoteFrom);
        if (pjService != null && profile != null) {
            boolean toCall = pjService.sendMessage(remoteFrom, message,
                    profile.id, profile);
            return toCall;
        }
        return false;
    }

    // 发送消息
    public boolean sendLocalMessage(String remoteFrom, String message,
                                    String password, String sipAccount) {
        WulianLog.d("PML", "sendLocalMessage remoteFrom is:" + remoteFrom);
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(password)) {
            bundle.putString(Configproperty.DEFAULT_LOCAL_NAME, password);
        }
        if (!TextUtils.isEmpty(sipAccount)) {
            bundle.putString(Configproperty.DEFAULT_LOCAL_URI, sipAccount);
        }
        Bundle b = new Bundle();
        b.putBundle(SipCallSession.OPT_MSG_EXTRA_HEADERS, bundle);
        if (pjService != null) {
            boolean toCall = pjService.sendLocalMessage(remoteFrom, message, b);
            return toCall;
        }
        return false;
    }

    // 发送消息
    public boolean sendInfo(String remoteFrom, String message, int callID,
                            SipProfile profile) {
        WulianLog.d("PML", "sendInfo remoteFrom is:" + remoteFrom);
        if (pjService != null && profile != null) {
            boolean toCall = pjService.sendInfo(remoteFrom, message, callID,
                    profile);
            return toCall;
        }
        return false;
    }

    // 发送消息
    public boolean sendLocalInfo(String remoteIp, String message, int callID,
                                 String password, String sipAccount) {
        WulianLog.d("PML", "sendLocalInfo remoteIp is:" + remoteIp);
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(password)) {
            bundle.putString(Configproperty.DEFAULT_LOCAL_NAME, password);
        }
        if (!TextUtils.isEmpty(sipAccount)) {
            bundle.putString(Configproperty.DEFAULT_LOCAL_URI, sipAccount);
        }
        Bundle b = new Bundle();
        b.putBundle(SipCallSession.OPT_MSG_EXTRA_HEADERS, bundle);
        if (pjService != null) {
            boolean toCall = pjService.sendLocalInfo(remoteIp, message, callID,
                    b);
            return toCall;
        }
        return false;
    }

    // 拨打电话
    public boolean callUpdate(int call_id) {
        if (pjService != null && call_id != -1) {
            return pjService.callUpdate(call_id);
        }
        return false;
    }

    // 错误类型:
    // 0 代表未创建
    // 1 代表局域网创建失败
    // 2 代表局域网的IP格式不对
    // 3 代表拨打失败
    // 200 代表拨打成功
    // 拨打局域网时，如果在视频呼叫中，返回486表示密码错误
    public int makeLocalCall(String remoteIP, String password, String sipAccount) {
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(password)) {
            bundle.putString(Configproperty.DEFAULT_LOCAL_NAME, password);
        }
        if (!TextUtils.isEmpty(sipAccount)) {
            bundle.putString(Configproperty.DEFAULT_LOCAL_URI, sipAccount);
        }
        Bundle b = new Bundle();
        b.putBundle(SipCallSession.OPT_CALL_EXTRA_HEADERS, bundle);
        if (pjService != null) {
            return pjService.makeLocalCall(remoteIP, b);
        }
        return 0;
    }

    // 拨打电话
    public boolean makeCall(final String remoteFrom, SipProfile profile) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(SipCallSession.OPT_CALL_VIDEO, true);
        if (pjService != null && profile != null) {
            return pjService.makeCall(remoteFrom,(int) profile.id, bundle,
                    profile);
        }
        return false;
    }

    // 绑定
    public int addBuddy(String buddyUri) {
        if (pjService != null) {
            return pjService.addBuddy(buddyUri);
        }
        return -1;
    }

    // 删除绑定
    public void removeBuddy(String buddyUri) {
        if (pjService != null) {
            pjService.removeBuddy(buddyUri);
        }
    }

    public boolean hangupCall(int callId) {
        if (pjService != null) {
            return pjService.callHangup(callId, 0);
        }
        return false;
    }

    // 挂断所有电话
    public void hangupAllCall() {
        if (pjService != null) {
            pjService.callHangupAll();
        }
    }

    // 关闭语音通道
    public void closeAudioTransport(int callId) {
        if (pjService != null) {
            pjService.closeAudioTransport(callId);
        }
    }

    /**
     * 扬声器 调节(接收声音调节)
     *
     * @param speakVolume
     *            值范围为0.0-1.0 0.0 表示关闭扬声器 1.0 表示扬声器最大声音
     */
    public void setMediaSpeakerOne(float speakVolume) {
        if (pjService != null && speakVolume >= 0.0f && speakVolume <= 1.0f) {
            pjService.setMediaSpeakerOne(speakVolume);
        }
    }

    /**
     * 调节麦克风 （发送声音调节）
     *
     * @param micVolume
     *            值范围为0.0-1.0 0.0 表示关闭麦克风 1.0 表示麦克风最大声音
     */
    public void setMediaMicroOne(float micVolume) {
        if (pjService != null && micVolume >= 0.0f && micVolume <= 1.0f) {
            pjService.setMediaMicroOne(micVolume);
        }
    }

    public void setSpeakerPhone(boolean on) {
        if (pjService != null) {
            pjService.setSpeakerPhone(on);
        }
    }

    /**
     * 扬声器开关
     *
     * @param on
     *            true 表示打开扬声器 false 表示关闭扬声器
     */
    public void setSpeakerphoneOn(boolean on, int callId) {
        if (pjService != null) {
            pjService.setSpeakerphoneOn(on, callId);
        }
    }

    /**
     * Micro 开关 麦克风静音开关
     *
     * @param enable
     *            true 表示麦克风静音 false 表示麦克风打开
     */
    public void setMicrophoneInputEnable(boolean enable, int callId) {
        if (pjService != null) {
            pjService.setMicrophoneInputEnable(enable, callId);
        }
    }

    /**
     * Micro 开关 麦克风静音开关
     *
     * @param on
     *            true 表示麦克风静音 false 表示麦克风打开
     */
    public void setMicrophoneMute(boolean on, int callId) {
        if (pjService != null) {
            pjService.setMicrophoneMute(on, callId);
        }
    }

    public int getCallId(int position) {
        if (pjService != null) {
            SipCallSession[] listOfCallsImpl = pjService.getCalls();
            SipCallSession[] result = new SipCallSession[listOfCallsImpl.length];
            WulianLog.d("PML", "getCall length is:" + listOfCallsImpl.length);
            for (int sessIdx = 0; sessIdx < listOfCallsImpl.length; sessIdx++) {

                result[sessIdx] = new SipCallSession(listOfCallsImpl[sessIdx]);
                WulianLog.d("PML", "i is:" + sessIdx + ";CallId is:"
                        + result[sessIdx].getCallId());
            }
            if (position >= result.length && result.length > 0) {
                position = result.length - 1;
            }
            if (result.length <= 0) {
                return -1;
            }
            WulianLog.d("PML", "position is:" + position + "getCallId is:"
                    + result[position].getCallId());
            // return listOfCallsImpl[position].getCallId();
            return listOfCallsImpl[listOfCallsImpl.length - 1].getCallId();
        } else {
            return -1;
        }
    }

    public void setVideoAndroidRenderer(int callId, SurfaceView window) {
        if (pjService != null) {
            pjService.setVideoAndroidRenderer(callId, window);
        }
    }

    public void sendRtp(int callId) {
        if (pjService != null) {
            pjService.sendRtp(callId);
        }
    }

    public void setICEEnable1(boolean enable) {
        WulianDefaultPreference.setsEnableIce(enable);
        WulianDefaultPreference.setsEnableStun(enable);
    }

    // 设置证书文件(只限于TLS)
    public void setUserCalist(String calist, String cert, String privkey) {
        if (pjService != null) {
            pjService.setUserCalist(calist, cert, privkey);
        }
    }

    public void setEchoCancellation(boolean on) {
        if (pjService != null) {
            pjService.setEchoCancellation(on);
        }
    }
}