package com.wulian.sdk.android.oem.honeywell.ipc.data.repository.datasource;

import android.content.Context;
import android.util.Pair;

import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.AlarmCache;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.DeviceCache;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.UserInfoCache;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.model.UserInfo;

import java.util.List;


/**
 * 作者：Administrator on 2016/7/19 18:51
 * 邮箱：huihui@wuliangroup.com
 */

public class DeviceDataStoreFactory {

    private final Context context;

    private DeviceCache deviceCache;
    private AlarmCache alarmCache;
    public DeviceDataStoreFactory(Context context, DeviceCache deviceCache, AlarmCache alarmCache) {
        if (context == null) {
            throw new IllegalArgumentException("Constructor parameters cannot be null!!!");
        }
        this.context = context.getApplicationContext();
        this.deviceCache = deviceCache;
        this.alarmCache = alarmCache;

    }

    public void initDeviceCache(List<Device> deviceList){
        this.deviceCache.put(deviceList);
    }

    public void initAlarmCache(List<Pair<String, Device>> alarmList){
        this.alarmCache.put(alarmList);
    }
    public void initIpcUsername(String ipcUsername){
        this.deviceCache.putIpcUsername(ipcUsername);
    }
    /**
     * Create {@link UserDataStore} from a user id.
     */
//    public DeviceDataStore createDevice() {
//        DeviceDataStore deviceDataStore;
//        deviceDataStore = new DeviceDataStore(deviceCache.getDeviceList());
//        return deviceDataStore;
//    }

    public DeviceCache getDeviceCache() {
         return deviceCache;
    }

    /**
     * Create {@link UserDataStore} from a user id.
     */
    public AlarmDataStore createAlarm() {
        AlarmDataStore alarmDataStore;
        alarmDataStore = new AlarmDataStore(alarmCache.getListPair());
        return alarmDataStore;
    }

    public IpcUserNameDataStore createIpcUserName() {
        IpcUserNameDataStore ipcUserNameDataStore;
        ipcUserNameDataStore = new IpcUserNameDataStore(deviceCache.getIpcUsername());
        return ipcUserNameDataStore;
    }

    public void evictAll(){
        this.deviceCache.evictAll();
        this.deviceCache = null;
        this.alarmCache.evictAll();
        this.alarmCache = null;
    }
}
