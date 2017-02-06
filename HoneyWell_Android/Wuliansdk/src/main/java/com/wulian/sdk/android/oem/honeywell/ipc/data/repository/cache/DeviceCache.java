package com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache;

import android.util.Pair;

import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.model.UserInfo;

import java.util.List;

/**
 * 作者：Administrator on 2016/7/19 19:13
 * 邮箱：huihui@wuliangroup.com
 */
public interface DeviceCache {

    List<Device> getDeviceList();
    boolean isCached();

    /**
     * Evict all elements of the cache.
     */
    void evictAll();

    void put(List<Device> deviceList);

    void putIpcUsername(String ipcUsername);

    String getIpcUsername();

}
