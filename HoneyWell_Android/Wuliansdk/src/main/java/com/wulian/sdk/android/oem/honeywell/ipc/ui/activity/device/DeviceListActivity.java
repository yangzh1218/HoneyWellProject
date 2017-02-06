package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device;
/**
 * Project Name:  FamilyRoute
 * File Name:     LoginActivity.java
 * Package Name:  com.wulian.familyroute.view
 *
 * @Date: 2014年9月24日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.fragment.DeviceListFragment;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.fragment.DownloadListFragment;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.fragment.UserMsgFragment;


/**
 * @author Wangjj
 * @ClassName: LoginActivity
 * @Function: 登录
 * @Date: 2014年9月24日
 * @email wangjj@wuliangroup.cn
 */
public class DeviceListActivity extends BaseFragmentActivity implements RadioGroup.OnCheckedChangeListener {
    public DeviceListFragment deviceListFragment;
    public UserMsgFragment userMsgFragment;
    public DownloadListFragment downloadListFragment;
    FragmentManager fm;
    RadioGroup rg_menu_radio;
    RadioButton rb_device, rb_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        fm = getSupportFragmentManager();
        if (savedInstanceState != null) { // 修复fragment重叠问题，参考
            deviceListFragment = (DeviceListFragment) fm.findFragmentByTag("1");
//            userMsgFragment = (UserMsgFragment) fm.findFragmentByTag("2");
            downloadListFragment = (DownloadListFragment) fm.findFragmentByTag("2");
            if (deviceListFragment == null) {
                deviceListFragment = new DeviceListFragment();
            }
            if(downloadListFragment == null)
            {
                downloadListFragment = new DownloadListFragment();
            }
//            if (userMsgFragment == null) {
//                userMsgFragment = new UserMsgFragment();
//            }
        } else {
            deviceListFragment = new DeviceListFragment();
            downloadListFragment = new DownloadListFragment();
            fm.beginTransaction().add(R.id.container,
                    deviceListFragment, "1")
                    .add(R.id.container, downloadListFragment, "2")
                    .commit();
//            userMsgFragment = new UserMsgFragment();
//            fm.beginTransaction().add(R.id.container,
//                    deviceListFragment, "1")
//                    .add(R.id.container, userMsgFragment, "2")
//                    .commit();
//            fm.beginTransaction().add(R.id.container, userMsgFragment, "2").commit();
        }
        onSendSipRemoteAccess();
        initViews();
        initListeners();
    }

    private void initViews() {
        rg_menu_radio = (RadioGroup) findViewById(R.id.rg_menu_radio);
        rb_device = (RadioButton) findViewById(R.id.rb_menu_device);
        rb_msg = (RadioButton) findViewById(R.id.rb_menu_msg);
    }

    private void initListeners() {
        rg_menu_radio.setOnCheckedChangeListener(this);
        rg_menu_radio.check(R.id.rb_menu_device);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_menu_device) {
            fm.beginTransaction().show(deviceListFragment).hide(downloadListFragment).commit();
        } else if (checkedId == R.id.rb_menu_msg) {
            fm.beginTransaction().show(downloadListFragment).hide(deviceListFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}


