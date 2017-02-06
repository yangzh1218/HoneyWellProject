package com.wulian.sdk.android.oem.honeywell.ipc.ui.view;

import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.DeviceListPresenter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.PullListView;

/**
 * 作者：Administrator on 2016/6/12 20:11
 * 邮箱：huihui@wuliangroup.com
 */
public interface DeviceListView {
    void setmDeviceListPresenter(DeviceListPresenter mDeviceListPresenter);
    void noDeviceMode();
    void haveDeviceMode();
    PullListView getLvDevices();
}
