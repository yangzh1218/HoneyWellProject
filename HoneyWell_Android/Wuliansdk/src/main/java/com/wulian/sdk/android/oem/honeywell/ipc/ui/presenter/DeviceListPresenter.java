package com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter;

import com.wulian.sdk.android.oem.honeywell.ipc.ToastCallBack;

/**
 * 作者：Administrator on 2016/6/13 21:03
 * 邮箱：huihui@wuliangroup.com
 */
public interface DeviceListPresenter {
    void requestDivices();
    boolean getIsProcessingRefresh();
    void setIsProcessingRefresh(boolean isProcessingRefresh);
    void setToastCallBack(ToastCallBack callBack);
    void onResume();
}
