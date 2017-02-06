package com.wulian.sdk.android.oem.honeywell.ipc.ui.view;

/**
 * 作者：Administrator on 2016/4/29 16:53
 * 邮箱：huihui@wuliangroup.com
 */
public interface BaseView {

    void bindView();

    void dismissBaseDialog();

    void showProgressDialog();

    void dismissProgressDialog();

    void setTitleView(String title);

    void setTitleView(int title_id);

    void progressDialogDissmissed();

    void baseProgressDialogDissmissed();

}
