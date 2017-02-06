package com.wulian.sdk.android.oem.honeywell.ipc.ui.view;

import com.wulian.sdk.android.oem.honeywell.ipc.model.Alarm;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.AlarmListPresenter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.DeviceListPresenter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.PullListView;

import java.util.List;
import java.util.Set;

/**
 * 作者：Administrator on 2016/6/12 20:11
 * 邮箱：huihui@wuliangroup.com
 */
public interface AlarmListView {
    void setmDeviceListPresenter(AlarmListPresenter alarmListPresenter);
    void refreshListItem(String deviceID);
    PullListView getLvDevices();
    void progressDismiss();
    void showNoAlarmHint();
    void hideNoAlarmHint();
    void setAdapter(List<Alarm> list);
}
