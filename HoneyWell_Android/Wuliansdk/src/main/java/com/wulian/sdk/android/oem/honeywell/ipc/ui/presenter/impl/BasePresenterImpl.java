package com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.impl;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.controller.RouteLibraryController;
import com.wulian.routelibrary.controller.TaskResultListener;
import com.wulian.routelibrary.exception.ClientException;
import com.wulian.routelibrary.exception.ServiceException;
import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.APPConfig;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.model.UserInfo;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.BasePresenter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.BaseView;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.manage.SipProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 作者：Administrator on 2016/5/3 08:51
 * 邮箱：huihui@wuliangroup.com
 */
public class BasePresenterImpl implements BasePresenter, CallBack {
    protected SharedPreferences sp, spUsers;
    protected BaseFragmentActivity mContext = null;
    protected BaseView mBaseView = null;
    protected boolean sipRemoteAccessFlag = false;// 远程访问标识
    public static final int DISMISS_DIALOG = 1;
    public static final int ERROR = 2;
    public static final int FINISH = 3;

    public BasePresenterImpl(BaseFragmentActivity context, BaseView baseView) {
        mContext = context;
        mBaseView = baseView;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        //后面用RxJava改成异步操作
        mBaseView.dismissProgressDialog();
        mBaseView.dismissBaseDialog();// 处理java.lang.IllegalArgumentException: View not
        handler.removeCallbacksAndMessages(null);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DISMISS_DIALOG:
                    mBaseView.dismissProgressDialog();
                    break;
                case ERROR:
                    CustomToast.show(mContext, msg.getData().getString("error"));
                    break;
                case FINISH:
                    mContext.finish();
                    break;
                default:
                    break;
            }

        }
    };


    @Override
    public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String json) {
        Message message = new Message();
        message.what = DISMISS_DIALOG;
        handler.sendMessage(message);
    }

    @Override
    public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {
        Bundle bundle = new Bundle();
        if (exception instanceof ClientException) {
            ClientException clientException = (ClientException) exception;
            LibraryLoger.d(clientException.getErrorCode().name());
            bundle.putString("error", clientException.getMessage());
        } else {
            ServiceException serviceException = (ServiceException) exception;
            LibraryLoger.d(serviceException.getStatusCode() + "");
            bundle.putString("error", serviceException.getMessage());
        }
        bundle.putString("error", exception.getMessage());

        Message message = new Message();
        message.setData(bundle);
        message.what = ERROR;
        handler.sendMessage(message);
    }

    @Override
    public Handler getUIHandler() {
        return handler;
    }
}
