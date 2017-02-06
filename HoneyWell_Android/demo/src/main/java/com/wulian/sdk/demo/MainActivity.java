package com.wulian.sdk.demo;
/**
 * Project Name:  FamilyRoute
 * File Name:     LoginActivity.java
 * Package Name:  com.wulian.familyroute.view
 *
 * @Date: 2014年9月24日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

import android.app.Activity;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.APPConfig;
import com.wulian.sdk.android.oem.honeywell.ipc.AlarmCntCallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.DeviceListCallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.UnBindDeviceCallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.receiver.MessageCallStateReceiver;
import com.wulian.sdk.android.oem.honeywell.ipc.sip.SipFactory;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.manage.SipManager;
import com.wulian.siplibrary.utils.WulianLog;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author Wangjj
 * @ClassName: LoginActivity
 * @Function: 登录
 * @Date: 2014年9月24日
 * @email wangjj@wuliangroup.cn
 */
public class MainActivity extends Activity implements AlarmCntCallBack, CallBack {

    public Button register;
    public Button login;
    public Button login_out;
    public Button change_password;
    public Button alarm_message;
    public Button alarm_video;
    public Button camera_manage;
    public Button camera_unbind;
    public Button alarm_list;
    public String mjson = "";
    public EditText et_domain_name;
    public Button btn_web_domain,btn_formal_server,btn_test_server;
    public boolean isSetDomain = false;
    private SharedPreferences sp;
    @Override
    public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String json) {

    }

    @Override
    public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences(APPConfig.SP_CONFIG,MODE_PRIVATE);
        bindView();
//        LibraryLoger.setLoger(true);
        WulianLog.setLoger(true);
//        WulianSDK.getInstance().sdkInit(this);

//        WulianSDK.getInstance().Login("342091024@qq.com", "78bce15437f6789cc03a433a");
//        WulianSDK.getInstance().Login("xizho100", "12345678");
//        WulianSDK.getInstance().Login("hhabc", "asdzxc");

//          WulianSDK.getInstance().Login("pugong@qq.com","78bce15437f6789cc03a433a");

//          WulianSDK.getInstance().Login("pugong@qq.com", "78bce15437f6789cc03a433a");
//        WulianSDK.getInstance().Login("ooo", "12345678");
//        if(sp.getBoolean(APPConfig.SP_IS_REM_ACCOUNT,false))
//        {
//            Utils.sysoInfo("##autoLogin");
//            autoLogin();
//        }
    }


    MessageCallStateReceiver messageCallStateReceiver;

    @Override
    protected void onResume() {
        // TODO 待完成方法 及时删除
        super.onResume();
        WulianSDK.getInstance().bindActivity(this);
        messageCallStateReceiver = new MessageCallStateReceiver();
        SipFactory.getInstance().setPref(this);
        registerReceiver(messageCallStateReceiver, new IntentFilter(
                SipManager.GET_ACTION_SIP_MESSAGE_RECEIVED()));
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(messageCallStateReceiver);
    }

    public void bindView() {

        register = (Button) this.findViewById(R.id.register);
        login_out = (Button) this.findViewById(R.id.login_out);
        change_password = (Button) this.findViewById(R.id.change_password);
        alarm_message = (Button) this.findViewById(R.id.alarm_message);
        alarm_video = (Button) this.findViewById(R.id.alarm_video);
        camera_manage = (Button) this.findViewById(R.id.camera_manage);
        camera_unbind= (Button) this.findViewById(R.id.camera_unbind);
        alarm_list = (Button) this.findViewById(R.id.alarm_list);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register(view);
            }
        });
        login_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginOut(view);
            }
        });
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSetDomain)
                {
                    CustomToast.show(getApplicationContext(),R.string.setting_no_set_domain);
                    return;
                }
                changePwd(view);
            }
        });
        alarm_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSetDomain)
                {
                    CustomToast.show(getApplicationContext(),R.string.setting_no_set_domain);
                    return;
                }
                alarmMessage(view);
            }
        });
        alarm_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSetDomain)
                {
                    CustomToast.show(getApplicationContext(),R.string.setting_no_set_domain);
                    return;
                }
                alarmVideo(view);
            }
        });
        camera_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSetDomain)
                {
                    CustomToast.show(getApplicationContext(),R.string.setting_no_set_domain);
                    return;
                }
                else
                {

                    cameraManage(view);
                }
            }
        });
        camera_unbind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!isSetDomain)
                {
                    CustomToast.show(getApplicationContext(),R.string.setting_no_set_domain);
                    return;
                }
                unbindCamera(view);
            }
        });
        alarm_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSetDomain)
                {
                    CustomToast.show(getApplicationContext(),R.string.setting_no_set_domain);
                    return;
                }
                alarmList(view);
            }
        });
        et_domain_name = (EditText) this.findViewById(R.id.et_domain_name);
        btn_web_domain = (Button) this.findViewById(R.id.btn_web_domain);
//        btn_web_domain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String domain = et_domain_name.getText().toString().trim();
//                if(TextUtils.isEmpty(et_domain_name.getText().toString().trim()))
//                {
//                    CustomToast.show(getApplicationContext(),R.string.input_domain_name);
//                    return;
//                }
//                else
//                {
//                    if(domain.equalsIgnoreCase("test.sh.gg"))
//                    {
//                        LibraryLoger.setLoger(true);
//                        WulianSDK.getInstance().sdkInit(MainActivity.this);
//                        WulianSDK.getInstance().Login("pugong@qq.com","78bce15437f6789cc03a433a");
//                    }else if(domain.equalsIgnoreCase("hw.pu.sh.gg"))
//                    {
//                        LibraryLoger.setLoger(false);
//                        WulianSDK.getInstance().sdkInit(MainActivity.this);
//                        WulianSDK.getInstance().Login("pugong@qq.com","78bce15437f6789cc03a433a");
//                    }
//                }
//            }
//        });
        btn_formal_server = (Button) this.findViewById(R.id.btn_formal_server);
        btn_formal_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LibraryLoger.setLoger(false);
                WulianSDK.getInstance().sdkInit(MainActivity.this);
//                WulianSDK.getInstance().Login("pugong@qq.com","78bce15437f6789cc03a433a");
                WulianSDK.getInstance().Login("ceshi18","qweasd");
                sp.edit().putString(APPConfig.ACCOUNT_NAME,"pugong@qq.com").putString(APPConfig.PASSWORD,"78bce15437f6789cc03a433a").putBoolean(APPConfig.SP_IS_REM_ACCOUNT,true).commit();
                isSetDomain = true;
            }
        });
        btn_test_server = (Button) this.findViewById(R.id.btn_test_server);
        btn_test_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LibraryLoger.setLoger(true);
                WulianSDK.getInstance().sdkInit(MainActivity.this);
//                WulianSDK.getInstance().Login("pugong@qq.com","78bce15437f6789cc03a433a");
                WulianSDK.getInstance().Login("ceshi18","qweasd");
                sp.edit().putString(APPConfig.ACCOUNT_NAME,"pugong@qq.com").putString(APPConfig.PASSWORD,"78bce15437f6789cc03a433a").putBoolean(APPConfig.SP_IS_REM_ACCOUNT,true).commit();
                isSetDomain = true;
            }
        });
    }
    void autoLogin()
    {
        String account = sp.getString(APPConfig.ACCOUNT_NAME,"");
        String password = sp.getString(APPConfig.PASSWORD,"");
        if(TextUtils.isEmpty(account)|| TextUtils.isEmpty(password))
        {
            return;
        }
        WulianSDK.getInstance().Login(account,password);
    }

    boolean checkLogin() {
        return WulianSDK.getInstance().CheckLogin();
    }

    void register(View view) {
        WulianSDK.getInstance().UserRegister("ooo", "12345678");
//        WulianSDK.getInstance().UserRegister("pugong@qq.com", "78bce15437f6789cc03a433a");
    }


    void loginOut(View view) {
        if (checkLogin()) {
            WulianSDK.getInstance().Logout();
        } else {
            Toast.makeText(this, R.string.login, Toast.LENGTH_SHORT).show();
        }
    }

    void changePwd(View view) {
        if (checkLogin()) {
            WulianSDK.getInstance().ChangePassword("12345678", "123456789");
        } else {
            Toast.makeText(this, R.string.login, Toast.LENGTH_SHORT).show();
        }
    }

    void alarmMessage(View view) {
        if (!checkLogin()) {
            Toast.makeText(this, R.string.login, Toast.LENGTH_SHORT).show();
            return;
        }
//        WulianSDK.getInstance().GetAlarmVideoInfo("cmic05ac50294d407ac8", 1);//cmic057550294d406ea3
//        WulianSDK.getInstance().GetAlarmVideoInfo("cmic05c250294d4080b7", 2);//cmic057550294d406ea3
//        int[] als = {276836,276866,257643,257644,257645,257646,257647,257648};
        int[] als = { 277279,277311,277329,277353,277291,277317,277327,277280,277285,277286,277298,277351,277303,277365,277363,
                277347,277274,277297,277292,277357,277275,277281,277334,277304,277345,277293,277340,277309,277328,277359,
                277352,277269,277321,277322,277335,277323,277346,277339,277358,277310,277341,277305,277316,277364,277333,277299,277273,277315,277287};
        //1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50
//        11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,
//                51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,91,92,93,94,95,96,97,98,99,10,81,82,83,84,85,86,87,88,89,100
        Interface.getInstance().setAlarmCntCallBack(this);
        Interface.getInstance().GetAlarmVideoNum(als, 30000);

    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Toast.makeText(, "=====DoSucceed=alarmNumMap==").show();

            if(msg.what == 1)
            {
                // Log.i("=====", "=====DoSucceed=alarmNumMap=="+mjson);
                Toast.makeText(MainActivity.this, "=====DoSucceed=alarmNumMap=="+mjson, Toast.LENGTH_SHORT).show();
                Log.d("wulian.icam", mjson);
            }else if(msg.what == 10)
            {
                WulianSDK.getInstance().Login("pugong@qq.com","78bce15437f6789cc03a433a");
            }

        }
    };
    @Override
    public void DoSucceed(ErrorCode errorCode, SipMsgApiType apiType, String json) {
        Log.d("PML","json is::::::"+json);
        switch(apiType) {
            case QUERY_MULTI_ALARY_EVENT:
//                Map<Integer, Integer> alarmNumMap = (Map<Integer,Integer>) JSON.parse(json);
                mjson = json;
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
                break;
        }

    }

    void alarmVideo(View view) {
        if (checkLogin()) {
//            WulianSDK.getInstance().OpenAlarmVideo("cmic05ac50294d407ac8", "2016-08-21 11:30:00");
            WulianSDK.getInstance().OpenAlarmVideo("cmic05c250294d4080b7", "1471750200", "1471750260");

        } else {
            Toast.makeText(this, R.string.login, Toast.LENGTH_SHORT).show();
            WulianSDK.getInstance().Login("xizho100", "12345678");
        }
    }

    void cameraManage(View view) {
        if (!checkLogin()) {
            Toast.makeText(this, R.string.login, Toast.LENGTH_SHORT).show();
            WulianSDK.getInstance().Login("xizho100", "12345678");
        } else {

            WulianSDK.getInstance().OpenCameraManage("3410a59e96eb");
        }
    }

    void unbindCamera(View view) {
        if (!checkLogin()) {
            Toast.makeText(this, R.string.login, Toast.LENGTH_SHORT).show();
            WulianSDK.getInstance().Login("xizho100", "12345678");
        } else {
            WulianSDK.getInstance().GetDeviceList(new DeviceListCallBack(){

                @Override
                public void getDeviceListSuccess(List<Device> devices) {
                    if(devices!=null&&devices.size()>0) {
                        int size=devices.size();
                        Log.d("PML","Device List size is:"+size);
                        for(int i=0;i<size;i++) {
                             WulianSDK.getInstance().unBindDevice(devices.get(i).getDid(), new UnBindDeviceCallBack() {
                                @Override
                                public void UnBindDeviceSuccess() {
                                        Log.d("PML","UnBindDeviceSuccess");
                                }

                                @Override
                                public void UnBindDeviceFail(Exception exception) {
                                    Log.d("PML","UnBindDeviceFail");
                                }
                            });
                        }
                    }
                }

                @Override
                public void getDeviceListFail(Exception exception) {

                }
            });
        }
    }

    void alarmList(View view) {
        if (checkLogin()) {
            WulianSDK.getInstance().OpenAlarmVideoList(23530);//257641
//            startActivity(new Intent(this, LoginActivity.class));
        } else {
            Toast.makeText(this, R.string.login, Toast.LENGTH_SHORT).show();
            WulianSDK.getInstance().Login("xizho100", "12345678");
        }
    }
    private Timer timer = new Timer(true);
    private Timer timer2 = new Timer(true);
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 10;
            handler.sendMessage(msg);
        }
    };

}


