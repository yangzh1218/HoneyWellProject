package com.wulian.sdk.android.oem.honeywell.ipc;

/**
 * Created by Administrator on 2016/10/13.
 */

public interface UnBindDeviceCallBack {
      void UnBindDeviceSuccess();
      void UnBindDeviceFail(Exception exception);
}
