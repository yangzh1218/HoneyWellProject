package com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.wulian.sdk.android.oem.honeywell.ipc.APPConfig;
import com.wulian.sdk.android.oem.honeywell.ipc.model.UserInfo;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;

import java.io.File;

/**
 * 作者：Administrator on 2016/7/19 19:22
 * 邮箱：huihui@wuliangroup.com
 */
public class UserInfoCachImpl implements UserInfoCache  {

    private final Context context;
    private final File cacheDir;
    private UserInfo userInfo;
    private long lastUpdateTime;
    private static final long EXPIRATION_TIME = 60 * 60 * 1000;
    SharedPreferences spUsers;

    public UserInfoCachImpl(Context context) {
        this.context = context.getApplicationContext();
        this.cacheDir = this.context.getCacheDir();
        spUsers = context.getSharedPreferences(APPConfig.SP_USERS, context.MODE_PRIVATE);// 不可以一开始就初始化，至少在onCreate里
    }

    @Override
    public UserInfo getUserInfo() {
        if(userInfo == null) {
            return Utils.parseBean(UserInfo.class, spUsers.getString(APPConfig.ACCOUNT_USERINFO, ""));
        } else {
            return userInfo;
        }
    }

    @Override
    public void putPassword(String pwd) {
        userInfo.setPassword(pwd);
    }

    @Override
    public void put(UserInfo userInfo) {
        this.userInfo = userInfo;
        lastUpdateTime = System.currentTimeMillis();
        SharedPreferences.Editor editor = spUsers.edit();
        editor.putString(APPConfig.ACCOUNT_USERINFO, userInfo.toString());
        editor.commit();
    }

    @Override
    public boolean isCached() {
        if(userInfo == null){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        boolean expired = ((currentTime - lastUpdateTime) > EXPIRATION_TIME);

        if (expired) {
            this.evictAll();
        }

        return expired;
    }

    @Override
    public void evictAll() {
        userInfo = null;
        lastUpdateTime = 0;
    }
}
