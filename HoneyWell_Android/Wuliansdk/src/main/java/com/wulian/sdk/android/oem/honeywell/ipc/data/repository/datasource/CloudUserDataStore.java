package com.wulian.sdk.android.oem.honeywell.ipc.data.repository.datasource;

import com.wulian.sdk.android.oem.honeywell.ipc.model.UserInfo;

/**
 * 作者：Administrator on 2016/7/19 18:52
 * 邮箱：huihui@wuliangroup.com
 */
public class CloudUserDataStore implements UserDataStore {
    private UserInfo userInfo;

    public CloudUserDataStore() {
    }

    @Override
    public UserInfo userInfo() {
        return userInfo;
    }
}
