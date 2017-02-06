package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.BasePresenter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.impl.BasePresenterImpl;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.BaseView;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.impl.BaseViewImpl;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.NetCommon;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.manage.SipManager;
import com.wulian.siplibrary.manage.SipMessage;


public class BaseFragmentActivity extends FragmentActivity {

    protected BasePresenter baseActivityPresenter = null;
    protected BaseView mBaseView;
    private boolean sipRemoteAccessFlag = false;// 远程访问标识

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseView = new BaseViewImpl(this);
        baseActivityPresenter = new BasePresenterImpl(this, mBaseView);
        baseActivityPresenter.onCreate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState,
                                    PersistableBundle outPersistentState) {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baseActivityPresenter.onDestroy();
        onStopSipRemoteAccess();
    }

    /**
     * @Function 远程控制请求，必须调用该方法注册广播接收器
     * @author Wangjj
     * @date 2015年1月5日
     */
    protected void onSendSipRemoteAccess() {
        if (sipRemoteAccessFlag == false) {
            registerReceiver(MessageCallStateReceiver, new IntentFilter(
                    SipManager.GET_ACTION_SIP_MESSAGE_RECEIVED()));
            sipRemoteAccessFlag = true;
        }
    }

    protected void onStopSipRemoteAccess() {
        if (sipRemoteAccessFlag) {
            unregisterReceiver(MessageCallStateReceiver);
            sipRemoteAccessFlag = false;
        }
    }

    private BroadcastReceiver MessageCallStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(SipManager.GET_ACTION_SIP_MESSAGE_RECEIVED())) {
                SipMessage sm = (SipMessage) intent.getParcelableExtra("SipMessage");
                if (sm != null) {
                    SipMsgApiType apiType = SipHandler.parseXMLData(sm
                            .getBody());
                    if (sm.getType() == SipMessage.MESSAGE_TYPE_SENT) {
                        if (!sm.getContact().equalsIgnoreCase("200") || apiType == SipMsgApiType.NOTIFY_WEB_ACCOUNT_INFO) {
                            sipDataReturn(false, apiType, sm.getBody(),
                                    sm.getFrom(), sm.getTo());
                        }
                    } else if (sm.getType() == SipMessage.MESSAGE_TYPE_INBOX) {
                        sipDataReturn(true, apiType, sm.getBody(),
                                sm.getFrom(), sm.getTo());
                    }
                }
            }
        }
    };

    protected void sipDataReturn(boolean isSuccess, SipMsgApiType apiType,
                                 String xmlData, String from, String to) {
        mBaseView.dismissProgressDialog();
    }
}