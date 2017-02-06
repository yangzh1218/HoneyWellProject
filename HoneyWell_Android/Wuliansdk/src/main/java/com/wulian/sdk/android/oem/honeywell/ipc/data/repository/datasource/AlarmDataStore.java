package com.wulian.sdk.android.oem.honeywell.ipc.data.repository.datasource;

import android.util.Pair;

import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;

import java.util.List;

/**
 * 作者：Administrator on 2016/7/19 18:52
 * 邮箱：huihui@wuliangroup.com
 */
public class AlarmDataStore {

    List<Pair<String, Device>> alarmList;

    public AlarmDataStore(List<Pair<String, Device>> alarmList) {
        this.alarmList = alarmList;
    }

    public List<Pair<String, Device>> alarmListPair(){
        return alarmList;
    };

    public void setAlarmList(List<Pair<String, Device>> alarmList) {
        this.alarmList = alarmList;
    }
}
