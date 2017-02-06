package com.wulian.sdk.android.oem.honeywell.ipc.data.repository;

import android.content.Context;

import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache.UserInfoCache;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.datasource.UserDataStoreFactory;
import com.wulian.sdk.android.oem.honeywell.ipc.model.UserInfo;

/**
 * 作者：Administrator on 2016/7/19 18:38
 * 邮箱：huihui@wuliangroup.com
 */
public class UserDataRepository {

    static UserDataRepository anInterface;

    public static UserDataRepository getInstance() {
        if (anInterface == null) {
            anInterface = new UserDataRepository();
        }
        return anInterface;
    }

    private UserDataRepository() {
    }

    private UserDataStoreFactory userDataStoreFactory = null;

    public void init(Context context, UserInfoCache userInfoCache) {
        userDataStoreFactory = new UserDataStoreFactory(context, userInfoCache);
    }

    public void initUserInfoCache(UserInfo userInfo) {
        if (userDataStoreFactory != null) {
            userDataStoreFactory.initUserInfoCache(userInfo);
        }
    }

    public String oauth() {
        if (userDataStoreFactory != null) {
            UserInfo userinfo=getUserInfo();
            if(userinfo!=null) {
                return userinfo.getAuth();
            }
            return "";
//            return userDataStoreFactory.create().userInfo().getAuth();
        }
        return null;
    }

    public UserInfo getUserInfo() {
        if (userDataStoreFactory != null) {
            return userDataStoreFactory.getUserInfo();
        }
        return null;
    }

    public String password() {
        if (userDataStoreFactory != null) {
            UserInfo userinfo=getUserInfo();
            if(userinfo!=null) {
                return userinfo.getPassword();
            }
            return "";
        }
        return null;
    }

    public void setUsername1(String username) {
        if (userDataStoreFactory != null) {
            userDataStoreFactory.getUserInfo().setUsername(username);
        }
    }

    public void setPassword(String password) {
        if (userDataStoreFactory != null) {
            userDataStoreFactory.getUserInfo().setPassword(password);
        }
    }

    public String suid() {
        if (userDataStoreFactory != null) {
            UserInfo userinfo=getUserInfo();
            if(userinfo!=null) {
                return userinfo.getSuid();
            }
            return "";
        }
        return null;
    }

    public String sdomain() {
        if (userDataStoreFactory != null) {
            UserInfo userinfo=getUserInfo();
            if(userinfo!=null) {
                return userinfo.getSdomain();
            }
            return "";
        }
        return null;
    }

    public String uuid() {
        if (userDataStoreFactory != null) {
            UserInfo userinfo=getUserInfo();
            if(userinfo!=null) {
                return userinfo.getUuid();
            }
            return "";
        }
        return null;
    }

    public UserInfo userInfo() {
        if (userDataStoreFactory != null) {
            return userDataStoreFactory.getUserInfo();
        }
        return null;
    }

    public void evictAll() {
        userDataStoreFactory.evictAll();
        userDataStoreFactory = null;
    }
}
