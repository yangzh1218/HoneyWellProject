package com.wulian.sdk.android.oem.honeywell.ipc;

import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;

import java.util.List;

/**
 * Created by Administrator on 2016/10/13.
 */

public interface DeviceListCallBack {
      void getDeviceListSuccess(List<Device> devices);
      void getDeviceListFail(Exception exception);
}
