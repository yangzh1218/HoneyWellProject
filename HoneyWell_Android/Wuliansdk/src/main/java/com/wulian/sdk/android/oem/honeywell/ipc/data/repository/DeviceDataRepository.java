package com.wulian.sdk.android.oem.honeywell.ipc.data.repository;

import android.content.Context;
import android.util.Pair;

import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.AlarmCache;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.DeviceCache;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.UserInfoCache;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.datasource.DeviceDataStoreFactory;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.datasource.UserDataStoreFactory;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.model.UserInfo;

import java.util.List;

/**
 * 作者：Administrator on 2016/8/17 22:04
 * 邮箱：huihui@wuliangroup.com
 */
public class DeviceDataRepository {
    static DeviceDataRepository anInterface;

    public static DeviceDataRepository getInstance() {
        if (anInterface == null) {
            anInterface = new DeviceDataRepository();
        }
        return anInterface;
    }

    private DeviceDataRepository() {
    }

    private DeviceDataStoreFactory deviceDataStoreFactory = null;

    public void init(Context context, DeviceCache deviceCache, AlarmCache alarmCache) {
        deviceDataStoreFactory = new DeviceDataStoreFactory(context, deviceCache, alarmCache);
    }

    public void initUserInfoCache(List<Device> deviceList) {
        if (deviceDataStoreFactory != null) {
            if(deviceList==null) {
                LibraryLoger.d("deviceList is NULL in deviceDataStoreFactory");
            }
            deviceDataStoreFactory.initDeviceCache(deviceList);
        }else {
            LibraryLoger.d("deviceDataStoreFactory is NULL in deviceDataStoreFactory");
        }
    }

    public List<Device> deviceList() {
        if (deviceDataStoreFactory != null) {
            return deviceDataStoreFactory.getDeviceCache().getDeviceList();
        }
        LibraryLoger.d("The deviceDataStoreFactory is null");
        return null;
    }

    public List<Pair<String, Device>> alarmListPair() {
        if(deviceDataStoreFactory != null) {
            return deviceDataStoreFactory.createAlarm().alarmListPair();
        }
        return null;
    }

    public void initAlarmCache(List<Pair<String, Device>> alarmList) {
        if (deviceDataStoreFactory != null) {
            deviceDataStoreFactory.initAlarmCache(alarmList);
        }
    }

    public void initIpcUsername(String ipcUsername) {
        if (deviceDataStoreFactory != null) {
            deviceDataStoreFactory.initIpcUsername(ipcUsername);
        }
    }

    public String ipcUsername() {
        if (deviceDataStoreFactory != null) {
            return deviceDataStoreFactory.createIpcUserName().ipcUsername();
        }
        return null;
    }

    public void evictAll() {
        deviceDataStoreFactory.evictAll();
        deviceDataStoreFactory = null;
    }
}
