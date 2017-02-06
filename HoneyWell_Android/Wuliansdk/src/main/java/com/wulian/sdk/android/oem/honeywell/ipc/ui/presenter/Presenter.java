package com.wulian.sdk.android.oem.honeywell.ipc.ui.presenter;

/**
 * 作者：Administrator on 2016/5/4 09:04
 * 邮箱：huihui@wuliangroup.com
 */
public interface Presenter {

    void onStart();

    void onStop();

    void onPause();

    void onCreate();

    void onDestroy();

    void onResume();

}
