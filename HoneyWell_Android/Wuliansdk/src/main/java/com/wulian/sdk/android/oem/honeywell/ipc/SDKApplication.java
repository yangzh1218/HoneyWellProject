package com.wulian.sdk.android.oem.honeywell.ipc;

import android.app.Application;
import android.content.Context;

import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.DeviceDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.UserDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.AlarmCache;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.AlarmCacheImpl;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.DeviceCache;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.DeviceCacheImpl;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.UserInfoCachImpl;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.UserInfoCache;

/**
 * 作者：Administrator on 2016/7/27 08:29
 * 邮箱：huihui@wuliangroup.com
 */
public class SDKApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextHolder.initial(this);
        UserInfoCache userInfoCache = new UserInfoCachImpl(this);
        DeviceCache deviceCache = new DeviceCacheImpl(this);
        AlarmCache alarmCache = new AlarmCacheImpl(this);
        UserDataRepository.getInstance().init(this, userInfoCache);
        DeviceDataRepository.getInstance().init(this, deviceCache, alarmCache);
        instance = this;
    }

    private static SDKApplication instance;

    public static SDKApplication getInstance() {
        return instance;
    }
}
