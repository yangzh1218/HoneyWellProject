package com.wulian.sdk.android.oem.honeywell.ipc.sip;

import android.content.Context;

import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.NetCommon;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.manage.SipProfile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 作者：Administrator on 2016/6/16 09:53
 * 邮箱：huihui@wuliangroup.com
 */
public class SipFactory {

    ExecutorService pjSipThreadExecutor = Executors.newSingleThreadExecutor();
    private SipProfile sipProfile;// 用户账号信息,app范围只有一个
    private static SipFactory mInstance;
    public int callId;
    public int seq = 0;
    boolean initedSip = false;
    public static SipFactory getInstance() {
        if (mInstance == null) {
            SipFactory sipFactory = new SipFactory();
            mInstance = sipFactory;
        }
        return mInstance;
    }

    public SipProfile getSipProfile() {
        return sipProfile;
    }

    /**
     * @Function 建立sip通道，app范围内，只会初始化一次
     * @author Wangjj
     * @date 2014年12月5日
     */

    public void initSip(final Context context) {
        if(initedSip == false) {
            pjSipThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    long start = System.currentTimeMillis();
                    Utils.sysoInfo("init initSip  runable start "
                            + Thread.currentThread().getName() + ":"
                            + Thread.currentThread().getId());
                    SipController.getInstance().CreateSip(
                            context, false);
                    initedSip = true;
                }
            });
        }
    }

    public void setPref(Context context){
        SipController.getInstance().SetPref(context);
    }

    public void destorySip() {
        pjSipThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                Utils.sysoInfo("init destorySip start "
                        + Thread.currentThread().getName() + ":"
                        + Thread.currentThread().getId());
                SipController.getInstance().DestroySip();// 必须同一线程?
                Utils.sysoInfo("init destorySip end "
                        + (System.currentTimeMillis() - start));
            }
        });
    }

    /**
     * @Function 用户账号信息，app范围内只有一个
     * @author Wangjj
     * @date 2014年12月5日
     * @return
     */
    public SipProfile registerAccount(final String Suid, final String Sdomain, final String password) {
            pjSipThreadExecutor.execute(new Runnable() {

                @Override
                public void run() {
                    LibraryLoger.d("registerAccount   in SIPFFFFF");
                    LibraryLoger.d("The sipProfile is:"+(sipProfile==null?"NULL":SipController.getInstance().getAccountInfo(sipProfile)+""));
                    if(sipProfile==null||SipController.getInstance().getAccountInfo(sipProfile)!=200) {
                        sipProfile = SipController.getInstance()
                                .registerAccount(Suid, Sdomain, password);
                    }
                }
            });
        return sipProfile;
    }


    public void makeCall(final String remoteFrom, final SipProfile profile) {
        pjSipThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SipController.getInstance().makeCall(remoteFrom, profile);
            }
        });
    }

    public boolean sendMessage(final String remoteFrom,final String message,
                               final SipProfile profile) {
        pjSipThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SipController.getInstance().sendMessage(remoteFrom,message,profile);
            }
        });
        return true;
    }

    public void makeLocalCall(final String remoteIP, final String password,
                              final String sipAccountCallUrl) {
        pjSipThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SipController.getInstance().makeLocalCall(remoteIP,
                        password, sipAccountCallUrl);
            }
        });
    }

    public void hangupAllCall() {
        pjSipThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SipController.getInstance().hangupAllCall();
            }
        });
    }

    public void getAccountInfo(){
        pjSipThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SipController.getInstance().getAccountInfo(sipProfile);
            }
        });
    }
}
