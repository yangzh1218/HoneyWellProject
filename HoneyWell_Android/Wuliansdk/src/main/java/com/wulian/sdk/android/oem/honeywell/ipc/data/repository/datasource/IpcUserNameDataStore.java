package com.wulian.sdk.android.oem.honeywell.ipc.data.repository.datasource;

import android.util.Pair;

import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;

import java.util.List;

/**
 * 作者：Administrator on 2016/7/19 18:52
 * 邮箱：huihui@wuliangroup.com
 */
public class IpcUserNameDataStore {

    String ipcUsername;

    public IpcUserNameDataStore(String ipcUsername) {
        this.ipcUsername = ipcUsername;
    }

    public String ipcUsername(){
        return ipcUsername;
    };
}
