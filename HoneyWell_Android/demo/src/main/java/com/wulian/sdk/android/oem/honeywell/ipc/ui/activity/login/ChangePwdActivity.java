
package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.login;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.demo.R;
import com.wulian.sdk.demo.WulianSDK;

public class ChangePwdActivity extends Activity{

    public EditText et_new_pwd;
    public EditText et_old_pwd;
    public Button btn_sure;
    public ImageView titlebar_back;
    public TextView titlebar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        bindView();
        WulianSDK.getInstance().bindActivity(this);
    }

    public void bindView() {
        et_old_pwd = (EditText) findViewById(R.id.et_old_pwd);
        et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePwd(view);
            }
        });

        titlebar_title = (TextView) findViewById(R.id.titlebar_title);
        titlebar_title.setText("修改密码");
        titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
        titlebar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePwdActivity.this.finish();
            }
        });
    }

    void changePwd(View view) {
        String oldPassword = et_old_pwd.getText().toString().trim();
        String newPassword = et_new_pwd.getText().toString().trim();
        Interface.getInstance().ChangePassword(oldPassword, newPassword);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
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
