package com.wulian.sdk.android.oem.honeywell.ipc.data.repository.datasource;

import android.util.Pair;

import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;

import java.util.List;

/**
 * 作者：Administrator on 2016/7/19 18:52
 * 邮箱：huihui@wuliangroup.com
 */
public class DeviceDataStore {

    private Device device;
    private List<Device> deviceList;

    public DeviceDataStore(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public Device device() {
        return device;
    }

    public List<Device> deviceList() {
        return deviceList;
    }

}
