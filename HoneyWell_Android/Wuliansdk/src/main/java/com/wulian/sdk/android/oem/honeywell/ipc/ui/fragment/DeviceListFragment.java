package com.wulian.sdk.android.oem.honeywell.ipc.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.ToastCallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.DeviceDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.navigation.Navigator;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.DeviceListPresenter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter.impl.device.DeviceListPresenterImpl;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.DeviceListView;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.impl.device.DeviceListViewImpl;

import java.util.List;

/**
 * 作者：Administrator on 2016/7/12 21:20
 * 邮箱：huihui@wuliangroup.com
 */
public class DeviceListFragment extends Fragment implements ToastCallBack{

    DeviceListView mDeviceListView;
    DeviceListPresenter mDeviceListPresenter;
    View fragmentView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Navigator navigator = new Navigator(this);
        navigator.initBackListener();
        mDeviceListPresenter.requestDivices();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_device_list, container,
                false);
        mDeviceListView = new DeviceListViewImpl(fragmentView);
        mDeviceListPresenter = new DeviceListPresenterImpl((BaseFragmentActivity) getActivity(), mDeviceListView);
        mDeviceListPresenter.setToastCallBack(this);
        mDeviceListView.setmDeviceListPresenter(mDeviceListPresenter);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        mDeviceListPresenter.onResume();
        mDeviceListPresenter.requestDivices();
    }

    @Override
    public void sendMessageCallBack(String resStr) {
        Message msg=new Message();
        msg.what=1;
        msg.obj=resStr;
        mToastHandler.sendMessage(msg);
    }

    @Override
    public void sendMessageCallBack(int res) {
        Message msg=new Message();
        msg.what=1;
        msg.obj=getResources().getString(res);
        mToastHandler.sendMessage(msg);
    }


    Handler mToastHandler=new Handler() {
    public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(getActivity(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
        }
    };
}
