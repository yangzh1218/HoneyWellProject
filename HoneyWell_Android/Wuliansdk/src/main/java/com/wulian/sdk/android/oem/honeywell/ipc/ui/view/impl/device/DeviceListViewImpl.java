package com.wulian.sdk.android.oem.honeywell.ipc.ui.view.impl.device;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config.AddDeviceFirstPageActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.DeviceListPresenter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.DeviceListView;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.impl.BaseViewImpl;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.PullListView;


/**
 * 作者：Administrator on 2016/6/12 20:08
 * 邮箱：huihui@wuliangroup.com
 */
public class DeviceListViewImpl implements DeviceListView {

    ImageView titlebar_add;
    TextView tv_nodevice_hint;
    ImageView iv_nodevice_img;
    RelativeLayout rl_click_add;
    PullListView lv_devices;
    View layoutView;
    private DeviceListPresenter mDeviceListPresenter;
    public DeviceListViewImpl(View view) {
        layoutView = view;
        bindView(layoutView);

    }


    public void bindView(View view) {
        titlebar_add = (ImageView) view.findViewById(R.id.titlebar_add);
        tv_nodevice_hint = (TextView) view.findViewById(R.id.tv_nodevice_hint);
        iv_nodevice_img = (ImageView) view.findViewById(R.id.iv_nodevice_img);
        rl_click_add = (RelativeLayout) view.findViewById(R.id.rl_click_add);
        lv_devices = (PullListView) view.findViewById(R.id.lv_devices);
        titlebar_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDevice(view);
            }
        });
        lv_devices.setOnRefreshListener(new PullListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!mDeviceListPresenter.getIsProcessingRefresh()) {
                    mDeviceListPresenter.requestDivices();
                }
            }
        });
    }

    @Override
    public void setmDeviceListPresenter(DeviceListPresenter deviceListPresenter) {
        mDeviceListPresenter = deviceListPresenter;
    }

   void addDevice(View view){
        Intent it = new Intent(layoutView.getContext(),
                AddDeviceFirstPageActivity.class);
       layoutView.getContext().startActivity(it);
    }

    @Override
    public void noDeviceMode() {
        tv_nodevice_hint.setVisibility(View.VISIBLE);
        iv_nodevice_img.setVisibility(View.VISIBLE);
        rl_click_add.setVisibility(View.VISIBLE);
    }

    @Override
    public void haveDeviceMode() {
        tv_nodevice_hint.setVisibility(View.GONE);
        iv_nodevice_img.setVisibility(View.GONE);
        rl_click_add.setVisibility(View.GONE);
    }

    public PullListView getLvDevices(){
        return lv_devices;
    }


}

