package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.login;
/**
 * Project Name:  FamilyRoute
 * File Name:     LoginActivity.java
 * Package Name:  com.wulian.familyroute.view
 *
 * @Date: 2014年9月24日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.InputChecker;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;
import com.wulian.sdk.demo.R;
import com.wulian.sdk.demo.WulianSDK;


/**
 * @author Wangjj
 * @ClassName: LoginActivity
 * @Function: 登录
 * @Date: 2014年9月24日
 * @email wangjj@wuliangroup.cn
 */
public class LoginActivity extends Activity
{
    public ImageView logo_more_icon;
    public AutoCompleteTextView et_accountnum;
    public EditText et_passwrod;
    public CheckBox ck_remPass;
    public CheckBox ck_autoLogin;
    public Button btn_login;
    public LinearLayout ll_login;
    public Button new_reg;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        WulianSDK.getInstance().bindActivity(this);
    }


    public void initView() {
        logo_more_icon = (ImageView)findViewById(R.id.logo_more_icon);
        et_accountnum = (AutoCompleteTextView)findViewById(R.id.account_num);
        et_passwrod = (EditText)findViewById(R.id.password);
        ck_remPass = (CheckBox)findViewById(R.id.ck_remPass);
        ck_autoLogin = (CheckBox)findViewById(R.id.ck_autoLogin);
        btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        });
    }

    public void login(View view) {
        String account = et_accountnum.getText().toString().trim();
        String password = et_passwrod.getText().toString().trim();

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
            CustomToast.show(this,
                    com.wulian.sdk.android.oem.honeywell.ipc.R.string.login_username_password_not_null);
            return;
        }
        if (!InputChecker.isMobile(account) && !InputChecker.isEmail(account)
                && !InputChecker.isUserName(account)
                && !InputChecker.isUserID(account)) {
            CustomToast.show(this, com.wulian.sdk.android.oem.honeywell.ipc.R.string.login_input_right_user);
            return;
        }


        Utils.hideKeyboard(this);
        btn_login.setEnabled(false);// 禁用登录，避免狂点导致的
        Interface.getInstance().Login(account, password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    setResult(RESULT_OK, LoginActivity.this.getIntent());
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        WulianSDK.getInstance().bindActivity(this);
    }
//
//    @Override
//    public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String json) {
//        Message message = new Message();
//        message.what = 0;
//        handler.sendMessage(message);
//    }
//
//    @Override
//    public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {
//
//    }
}


