package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.model.DeviceDetailMsg;
import com.wulian.sdk.android.oem.honeywell.ipc.sip.SipFactory;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.XMLHandler;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.utils.WulianLog;

/**
 * Created by Administrator on ${date} .
 */

public class DeviceServerActivity extends BaseFragmentActivity implements View.OnClickListener {
    private TextView tv_device_server,tv_switch_info;
    private Button btn_formal,btn_test,btn_switch;
    private Device device;
    private String sipCallWithDomain,domainAddress;
    private int seq = 1;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_device_server);
        mBaseView.bindView();
        mBaseView.setTitleView(R.string.setting_switch_camera_address);
        initView();
        initListeners();
        initData();
        onSendSipRemoteAccess();
        if (device.getOnline() == 1) {
            initWebData();
        }
    }

    private void initData()
    {
        device = (Device) getIntent().getExtras().getParcelable("device");
        sipCallWithDomain = device.getDid() + "@"
                + device.getSip_domain();
    }
    private void initWebData() {
        //查询摄像机域名
        SipController.getInstance().sendMessage(sipCallWithDomain,SipHandler.QueryWebDomian("sip:" + sipCallWithDomain, seq++),SipFactory.getInstance().getSipProfile());
    }
    private void initView()
    {
        tv_device_server = (TextView) findViewById(R.id.tv_device_server);
        btn_formal = (Button) findViewById(R.id.btn_formal);
        btn_test = (Button) findViewById(R.id.btn_test);
        btn_test.setEnabled(false);
        btn_formal.setEnabled(false);
        btn_switch = (Button) findViewById(R.id.btn_switch);
        tv_switch_info = (TextView) findViewById(R.id.tv_switch_info);
    }
    private void initListeners()
    {
        btn_formal.setOnClickListener(this);
        btn_test.setOnClickListener(this);
        btn_switch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_formal)
        {
            configDomain();
        }
        else if(id == R.id.btn_test)
        {
            configDomain();
        }
        if(id == R.id.btn_switch)
        {
            configDomain();
        }
    }
    //修改摄像机域名
    private void configDomain()
    {
        if(TextUtils.isEmpty(domainAddress))
        {
            btn_formal.setEnabled(false);
            btn_test.setEnabled(false);
            return;
        }
        if(domainAddress.equals("hw.pu.sh.gg"))
        {
            SipController.getInstance().sendMessage(sipCallWithDomain, SipHandler.ConfigWebDomain("sip:" + sipCallWithDomain, seq++,"test.sh.gg"), SipFactory.getInstance().getSipProfile());
        }
        else if(domainAddress.equals("test.sh.gg"))
        {
            SipController.getInstance().sendMessage(sipCallWithDomain,SipHandler.ConfigWebDomain("sip:" + sipCallWithDomain, seq++,"hw.pu.sh.gg"),SipFactory.getInstance().getSipProfile());
        }
    }
    @Override
    protected void sipDataReturn(boolean isSuccess, SipMsgApiType apiType,
                                 String xmlData, String from, String to) {
        super.sipDataReturn(isSuccess, apiType, xmlData, from, to);
        if (isSuccess) {
            WulianLog.e("xmlData", xmlData);
            switch (apiType) {
                case QUERY_WEB_DOMAIN:
                    domainAddress = Utils.getParamFromXml(xmlData, "domain").trim();
                    tv_device_server.setText(domainAddress);
                    if(TextUtils.isEmpty(tv_device_server.getText().toString())) return;
                    if(domainAddress.equalsIgnoreCase("hw.pu.sh.gg"))
                    {
                        btn_formal.setEnabled(false);
                        btn_formal.setVisibility(View.GONE);
                        btn_test.setEnabled(true);
                        tv_switch_info.setText(getResources().getString(R.string.setting_switch_test_info));
                    }
                    else if(domainAddress.equalsIgnoreCase("test.sh.gg")){
                        btn_formal.setEnabled(true);
                        btn_test.setEnabled(false);
                        btn_test.setVisibility(View.GONE);
                        tv_switch_info.setText(getResources().getString(R.string.setting_switch_formal_info));
                    }
                    break;
                case CONFIG_WEB_DOMAIN:
                    CustomToast.show(this,R.string.setting_switch_success);
                    break;
                default:
                    break;
            }
        }
    }
}
