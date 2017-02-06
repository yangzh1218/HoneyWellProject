package com.wulian.sdk.android.oem.honeywell.ipc.ui.view.impl.device;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Alarm;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config.AddDeviceFirstPageActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.play.ReplayVideoActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.adapter.AlarmListAdapter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.AlarmListPresenter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.AlarmListView;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.PullListView;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.progress.CircularProgressBar;

import java.util.List;
import java.util.Set;


/**
 * 作者：Administrator on 2016/6/12 20:08
 * 邮箱：huihui@wuliangroup.com
 */
public class AlarmListViewImpl implements AlarmListView {

    PullListView lv_devices;
    CircularProgressBar circularProgressBar;
    TextView tv_hint;
    Activity activity;
    AlarmListAdapter alarmListAdapter;

    private AlarmListPresenter alarmListPresenter;
    public AlarmListViewImpl(final Activity activity) {
        this.activity = activity;
        bindView(activity);
        alarmListAdapter = new AlarmListAdapter(activity);
        lv_devices.setAdapter(alarmListAdapter);
        alarmListAdapter.setImageViewOnClickEvent(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm alarm = (Alarm)v.getTag(R.id.tag_first);
                Interface.getInstance().OpenAlarmVideo(alarm.getDeviceID() ,(int)alarm.getAlarmID(), String.valueOf(alarm.getStartTime()), String.valueOf(alarm.getEndTime()));
            }
        });
    }

    public void refreshListItem(String deviceID) {
        alarmListAdapter.updateSingleRow(lv_devices,deviceID);
    }

    public void setAdapter(List<Alarm> list){

        progressDismiss();
        if(list != null) {
            if (list.size() == 0) {
                showNoAlarmHint();
            } else {
                hideNoAlarmHint();
            }
            alarmListAdapter.refreshList(list);
        }
    }

    public void bindView(Activity activity) {
        lv_devices = (PullListView) activity.findViewById(R.id.lv_devices);
        circularProgressBar = (CircularProgressBar) activity.findViewById(R.id.common_load_progress);
        tv_hint = (TextView) activity.findViewById(R.id.tv_hint);
        lv_devices.setOnRefreshListener(new PullListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!alarmListPresenter.getIsProcessingRefresh()||lv_devices.isDone()) {
                    alarmListPresenter.handleAlarmList();
                }
            }
        });
    }

    @Override
    public void setmDeviceListPresenter(AlarmListPresenter alarmListPresenter) {
        this.alarmListPresenter = alarmListPresenter;
    }

    public PullListView getLvDevices(){
        return lv_devices;
    }

    public void progressDismiss(){
        circularProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showNoAlarmHint() {
        tv_hint.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoAlarmHint() {
        tv_hint.setVisibility(View.GONE);
    }
}

