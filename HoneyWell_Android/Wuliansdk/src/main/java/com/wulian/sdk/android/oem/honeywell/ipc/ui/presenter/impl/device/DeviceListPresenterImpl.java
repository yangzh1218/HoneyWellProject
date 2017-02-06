package com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.impl.device;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.Toast;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.controller.RouteLibraryController;
import com.wulian.routelibrary.exception.ClientException;
import com.wulian.routelibrary.exception.ServiceException;
import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.ToastCallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.DeviceDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.adapter.DeviceAdapter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.DeviceListPresenter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.impl.BasePresenterImpl;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.DeviceListView;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 作者：Administrator on 2016/6/14 15:27
 * 邮箱：huihui@wuliangroup.com
 */
public class DeviceListPresenterImpl implements DeviceListPresenter, CallBack {

    private DeviceAdapter deviceAdapter;// 只要改一个适配器，立马换种风格
    private DeviceListView deviceListView;
    private BaseFragmentActivity context;
    private boolean isProcessingRefresh = false;
    private ToastCallBack mToastCallBack;

    public DeviceListPresenterImpl(BaseFragmentActivity context, DeviceListView deviceListView) {
        this.deviceListView = deviceListView;
        this.context = context;
        Interface.getInstance().setContext(context);
        Interface.getInstance().setCallBack(DeviceListPresenterImpl.this);
    }

    public void onResume() {
        Interface.getInstance().setContext(context);
        Interface.getInstance().setCallBack(DeviceListPresenterImpl.this);
        if(Interface.getInstance().getNeedRefreshList()) {
            deviceListView.getLvDevices().RefreshEvent();
        }
//        if(true) {
//            deviceListView.getLvDevices().RefreshEvent();
//        }
    }

    public void setToastCallBack(ToastCallBack callBack) {
        mToastCallBack=callBack;
    }

    public void initAdapter(List<Device> deviceList) {
        if (deviceList == null || deviceList.size() <= 0) {
            deviceListView.noDeviceMode();
        } else {
            deviceListView.haveDeviceMode();
        }
        this.isProcessingRefresh=false;
        if (deviceListView.getLvDevices().getAdapter() != null) {
            // 清空截图缓存
            if (deviceAdapter.snapCache != null) {
                Utils.sysoInfo("刷新列表，清空截图缓存，便于加载最新的图片，比如从播放页退出");
                deviceAdapter.snapCache.evictAll();// 如果不清空,导致缓存的截图一直不变
            }
            deviceAdapter.setDeviceList(deviceList);
            deviceAdapter.notifyDataSetChanged();

        } else if (deviceListView.getLvDevices().getAdapter() == null && deviceList != null) {
            deviceAdapter = new DeviceAdapter(context, deviceListView, this,
                    deviceList);
            deviceListView.getLvDevices().setAdapter(deviceAdapter);// 使用全局变量作参数
        }
        Interface.getInstance().setNeedRefreshList(false);
        deviceListView.getLvDevices().onRefreshComplete();
        deviceListView.getLvDevices().updateRefreshTime();
    }


    public void requestDivices() {
        isProcessingRefresh=true;
        Interface.getInstance().UserDevices("cmhw");
    }

    public boolean getIsProcessingRefresh() {
        return isProcessingRefresh;
    }
    public  void setIsProcessingRefresh(boolean isProcessingRefresh) {
        this.isProcessingRefresh=isProcessingRefresh;
    }
    public void onDestroy() {

    }

    public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String jsonData) {
        LibraryLoger.d("DeviceListPre DoSucceed"+apiType.name()+";"+" jsonData is:" + jsonData);
        switch (apiType) {
            case V3_USER_DEVICES:
                try {
                    Message message = new Message();
//                    Bundle bundle = new Bundle();
//                    List<Device>=  DeviceDataRepository.getInstance().deviceList();
//                    bundle.putParcelableArrayList("devices", (ArrayList<? extends Parcelable>));
                    message.arg1 = 1;
//                    message.setData(bundle);
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
        LibraryLoger.d("DeviceListPre DoFailed"+apiType.name()+";"+" jsonData is:" + errorCode.name());
        if (exception instanceof ClientException) {
            ClientException clientException = (ClientException) exception;
            LibraryLoger.d(clientException.getErrorCode().name());
            if(mToastCallBack!=null) {
                mToastCallBack.sendMessageCallBack(R.string.exception_2020);
            }
        } else {
            ServiceException serviceException = (ServiceException) exception;
            LibraryLoger.d(serviceException.getStatusCode() + "");
            if(mToastCallBack!=null) {
                mToastCallBack.sendMessageCallBack(R.string.exception_500);
            }
        }
        Message message = new Message();
        message.arg1 = 2;
        handler.sendMessage(message);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    List<Device> deviceList =  DeviceDataRepository.getInstance().deviceList();
                    if(deviceList!=null) {
                        LibraryLoger.d("The DeviceList is not null:"+deviceList.size());
                    }else {
                        LibraryLoger.d("The DeviceList is   null:");
                    }
                    initAdapter(deviceList);
                    break;
                case 2:
                    initAdapter(null);
                    break;
                default:
                    break;
            }
        }
    };
}
