package com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.impl.device;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.exception.ClientException;
import com.wulian.routelibrary.exception.ServiceException;
import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.SDKSipCallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.common.Common;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.DeviceDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Alarm;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.adapter.AlarmListAdapter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.AlarmListPresenter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.AlarmListView;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.MessageUtil;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;
import com.wulian.siplibrary.api.SipMsgApiType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 作者：Administrator on 2016/6/14 15:27
 * 邮箱：huihui@wuliangroup.com
 */
public class AlarmListPresenterImpl implements AlarmListPresenter, CallBack, SDKSipCallBack {

    private AlarmListView alarmListView;
    private Context context;
    private int alarmId;
    private List<Alarm> mAlarmList;
    int alarmVideoGot = 0;
    int alarmAll = 0;
    Lock lock = new ReentrantLock();
    private boolean isProcessingRefresh;

    public AlarmListPresenterImpl(BaseFragmentActivity context, AlarmListView alarmListView) {
        this.alarmListView = alarmListView;
        this.context = context;
        mAlarmList=new ArrayList<Alarm>();
        alarmListView.getLvDevices().onRefreshComplete();
        alarmListView.getLvDevices().updateRefreshTime();
    }

    public void onResume() {
        Interface.getInstance().setContext(context);
        Interface.getInstance().setCallBack(this);
        Interface.getInstance().setSdkSipCallBack(this);
    }

    public void initAdapter(List<Alarm> list) {
        if(isProcessingRefresh) {
            isProcessingRefresh = false;
            alarmListView.getLvDevices().onRefreshComplete();
            alarmListView.getLvDevices().updateRefreshTime();
            alarmListView.setAdapter(list);
        }
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public void requestDivices() {
        Interface.getInstance().UserDevices("cmhw");
    }

    public boolean getIsProcessingRefresh() {
        return isProcessingRefresh;
    }
    public void handleAlarmList() {
        isProcessingRefresh=true;
        if(DeviceDataRepository.getInstance().deviceList()!=null) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("devices", (ArrayList<Device>)DeviceDataRepository.getInstance().deviceList());
            message.what = 1;
            message.setData(bundle);
            handler.sendMessage(message);
        }else {
            Interface.getInstance().UserDevices("cmhw");
        }
    }


    public void onDestroy() {

    }


    public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String jsonData) {
        LibraryLoger.d("jsonData is:" + jsonData);
        switch (apiType) {
            case V3_USER_DEVICES:
                try {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("devices", (ArrayList<Device>)DeviceDataRepository.getInstance().deviceList());
                    message.what = 1;
                    message.setData(bundle);
                    handler.sendMessage(message);

                } catch (Exception e) {

                }

                break;
            default:
                break;
        }
    }

    @Override
    public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {
        if (exception instanceof ClientException) {
            ClientException clientException = (ClientException) exception;
            LibraryLoger.d(clientException.getErrorCode().name());
//            CustomToast.show(context, clientException.getMessage());
            CustomToast.show(context,R.string.exception_2020);
        } else {
            ServiceException serviceException = (ServiceException) exception;
            LibraryLoger.d(serviceException.getStatusCode() + "");
            CustomToast.show(context, R.string.exception_500);
        }
    }


    void initAlarmParam(){
        alarmVideoGot = 0;
        alarmAll = 0;//DeviceDataRepository.getInstance().deviceList().size();
        mAlarmList.clear();
    }

    @Override
    public void DoSucceed(ErrorCode errorCode, SipMsgApiType apiType, String msg) {
        Log.d(Common.getLogHead(this.getClass()), "alarmVideoGot:" + alarmVideoGot);
        switch(apiType) {
            case QUERY_ALARM_EVENT:
                synchronized (mAlarmList) {
                    if (isProcessingRefresh) {
                        try {
                            JSONObject json = new JSONObject(msg);
                            String uri = json.isNull("uri") ? "" : json.getString("uri");
                            String deviceID = TextUtils.isEmpty(uri) ? "" : (uri.startsWith("sip:") ? uri.substring(4, 24) : uri.substring(0, 20));
                            boolean isContain = false;
                            if (mAlarmList.size() > 0) {
                                for (int i = 0; i < mAlarmList.size(); i++) {
                                    if (mAlarmList.get(i).getDeviceID().equalsIgnoreCase(deviceID)) {
                                        isContain = true;
                                        break;
                                    }
                                }
                            }
                            if (!isContain) {
                                alarmVideoGot++;
                                List<Device> deviceList = DeviceDataRepository.getInstance().deviceList();
                                String session = json.isNull("session") ? "" : json.getString("session");
                                long alarmID = json.isNull("nAlarmId") ? 0 : json.getInt("nAlarmId");
                                long startTime = Long.parseLong(json.isNull("sStartTime") ? "0" : json.getString("sStartTime"));
                                long endTime = Long.parseLong(json.isNull("sEndTime") ? "0" : json.getString("sEndTime"));
                                long fileSize = Long.parseLong(json.isNull("nDataLength") ? "0" : json.getString("nDataLength"));
                                Alarm alarm = new Alarm();
                                alarm.setAlarmID(alarmID);
                                alarm.setDeviceID(deviceID);
                                alarm.setSession(session);
                                alarm.setFileSize(fileSize);
                                alarm.setStartTime(startTime);
                                alarm.setEndTime(endTime);
                                for (int i = 0; i < deviceList.size(); ++i) {
                                    if (deviceID.equals(deviceList.get(i).getDid())) {
                                        alarm.setDeviceName(deviceList.get(i).getNick());
                                        break;
                                    }
                                }
                                mAlarmList.add(alarm);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (alarmVideoGot == alarmAll) {
                            initAdapter(mAlarmList);
                        }

                    }
                }
         break;
        default:
            break;
    }
    }

    @Override
    public void DoFailed(ErrorCode errorCode, SipMsgApiType apiTyp, String msg) {
        Log.d(Common.getLogHead(this.getClass()), "alarmVideoGot:" + alarmVideoGot);
        switch (apiTyp) {
            case QUERY_ALARM_EVENT:
                synchronized (mAlarmList) {
                    if (isProcessingRefresh) {
                        alarmVideoGot++;
                        if (alarmVideoGot == alarmAll) {
                            initAdapter(mAlarmList);
                        }

                    }
                }
                break;
            default:
                break;
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    initAlarmParam();
                    timeoutDo();
                    List<Device> tempDeviceList=DeviceDataRepository.getInstance().deviceList();
                    if (tempDeviceList != null&&tempDeviceList.size()>0) {
                        int deviceListSize=tempDeviceList.size();
                        for (int i = 0; i < deviceListSize; ++i) {
                            if(tempDeviceList.get(i).getOnline() == 1)
                            {
                                Interface.getInstance().GetAlarmVideoInfo(tempDeviceList.get(i).getDid(), tempDeviceList.get(i).getSip_domain(),alarmId);
                                alarmAll ++;
                            }
                        }
                    } else {
                        initAdapter(mAlarmList);
                    }
                    break;
                case 3:
                    initAdapter(mAlarmList);
                    break;
            }
        }
    };

    void timeoutDo(){
        handler.removeMessages(3);
        handler.sendEmptyMessageDelayed(3,5000);
    }
}
