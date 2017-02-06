package com.wulian.sdk.android.oem.honeywell.ipc.data.repository.datasource;

import com.wulian.sdk.android.oem.honeywell.ipc.model.UserInfo;

/**
 * 作者：Administrator on 2016/7/19 18:52
 * 邮箱：huihui@wuliangroup.com
 */
public class DiskUserDataStore implements UserDataStore {

    private UserInfo userInfo;

    DiskUserDataStore(UserInfo userInfo){
        this.userInfo = userInfo;
    }

    public UserInfo userInfo(){
        return userInfo;
    }

}
