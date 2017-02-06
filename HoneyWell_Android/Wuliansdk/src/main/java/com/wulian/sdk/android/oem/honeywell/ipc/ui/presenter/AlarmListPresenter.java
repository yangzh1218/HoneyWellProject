package com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter;

/**
 * 作者：Administrator on 2016/6/13 21:03
 * 邮箱：huihui@wuliangroup.com
 */
public interface AlarmListPresenter {
//    void requestDivices();
    void handleAlarmList();
    boolean getIsProcessingRefresh();
    void onResume();
    void setAlarmId(int alarmId);
}
