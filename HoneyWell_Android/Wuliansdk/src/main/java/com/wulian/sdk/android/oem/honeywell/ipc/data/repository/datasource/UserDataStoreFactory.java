package com.wulian.sdk.android.oem.honeywell.ipc.data.repository.datasource;

import android.content.Context;

import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.UserInfoCache;
import com.wulian.sdk.android.oem.honeywell.ipc.model.UserInfo;


/**
 * 作者：Administrator on 2016/7/19 18:51
 * 邮箱：huihui@wuliangroup.com
 */

public class UserDataStoreFactory {

    private final Context context;

    private UserInfoCache userInfoCache;

    public UserDataStoreFactory(Context context, UserInfoCache userCache) {
        if (context == null) {
            throw new IllegalArgumentException("Constructor parameters cannot be null!!!");
        }
        this.context = context.getApplicationContext();
        this.userInfoCache = userCache;
    }

    public void initUserInfoCache(UserInfo userInfo){
        this.userInfoCache.put(userInfo);
    }

    public UserInfo getUserInfo() {
        if(this.userInfoCache!=null) {
            return this.userInfoCache.getUserInfo();
        }
        return null;
    }

    /**
     * Create {@link UserDataStore} from a user id.
     */
    public UserDataStore create() {
        UserDataStore userDataStore;

        if (!this.userInfoCache.isExpired() && this.userInfoCache.isCached()) {
            userDataStore = new DiskUserDataStore(this.userInfoCache.getUserInfo());
        } else {
            userDataStore = new CloudUserDataStore();
        }

        return userDataStore;
    }

    public void evictAll(){
        this.userInfoCache.evictAll();
        this.userInfoCache = null;
    }
}
