package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.receiver.MessageCallStateReceiver;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.adapter.AlarmListAdapter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.AlarmListPresenter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.impl.device.AlarmListPresenterImpl;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.AlarmListView;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.impl.device.AlarmListViewImpl;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.PullListView;
import com.wulian.siplibrary.manage.SipManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmListActivity extends BaseFragmentActivity {

    AlarmListView alarmListView;
    AlarmListPresenter alarmListPresenter;
    int alarmId;
    MessageCallStateReceiver messageCallStateReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        mBaseView.bindView();
        mBaseView.setTitleView(R.string.alarm_video);
        alarmListView = new AlarmListViewImpl(this);
        alarmListPresenter = new AlarmListPresenterImpl(this, alarmListView);
        alarmListView.setmDeviceListPresenter(alarmListPresenter);
        alarmId = getIntent().getExtras().getInt("alarmId");
        alarmListPresenter.setAlarmId(alarmId);
        alarmListPresenter.handleAlarmList();
        messageCallStateReceiver = new MessageCallStateReceiver();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK&&requestCode==10) {
            String DeviceID = data.getExtras().getString("DeviceID");
            LibraryLoger.d("AlarmList onResult is:"+DeviceID);
            alarmListView.refreshListItem(DeviceID);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        alarmListPresenter.onResume();

        registerReceiver(messageCallStateReceiver, new IntentFilter(
                SipManager.GET_ACTION_SIP_MESSAGE_RECEIVED()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(messageCallStateReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
