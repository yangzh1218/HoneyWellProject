package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.login;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.demo.R;
import com.wulian.sdk.demo.WulianSDK;

import org.json.JSONException;
import org.json.JSONObject;

public class RegActivity extends Activity {

    public EditText bt_user_name;
    public EditText bt_user_password;
    public Button bt_reg;
    public ImageView titlebar_back;
    public TextView titlebar_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        bindView();
    }


    public void bindView() {
        bt_user_name = (EditText) findViewById(R.id.bt_user_name);
        bt_user_password = (EditText) findViewById(R.id.bt_user_password);
        bt_reg = (Button) findViewById(R.id.bt_reg);
        bt_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reg(view);
            }
        });
        titlebar_title = (TextView) findViewById(R.id.titlebar_title);
        titlebar_title.setText("注册");
        titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
        titlebar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegActivity.this.finish();
            }
        });
    }

    void reg(View view) {
        String account = bt_user_name.getText().toString().trim();
        String password = bt_user_password.getText().toString().trim();
        Interface.getInstance().UserRegister(account, password);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
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

    //
//    @Override
//    public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String jsonData) {
//        LibraryLoger.d("jsonData is:" + jsonData);
//        switch (apiType) {
//            case V3_PARTNER_REGISTER:
//                try {
//                    JSONObject json = new JSONObject(jsonData);
//                } catch (JSONException e) {
//
//                }
//                Message message = new Message();
//                message.what = 0;
//                handler.sendMessage(message);
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {
//
//    }
}
