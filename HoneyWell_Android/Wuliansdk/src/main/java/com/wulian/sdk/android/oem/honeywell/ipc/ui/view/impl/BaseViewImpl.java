package com.wulian.sdk.android.oem.honeywell.ipc.ui.view.impl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.model.UserInfo;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.BaseView;

/**
 * 作者：Administrator on 2016/6/12 19:30
 * 邮箱：huihui@wuliangroup.com
 */
public class BaseViewImpl implements BaseView {

    ProgressDialog progressDialog;// 单个请求时使用,一般由父类管理
    ProgressDialog baseProgressDialog;// 多个请求连续调用时使用,一般由子类管理，第一个请求show，最后一个请求dismiss。
    protected EditText urlEt;
    private View contentView, baseContentView;
    protected UserInfo userInfo;// 父类维护的一个用户信息引用，重新登录后，需要更新这个引用
    ImageView titlebarBack;
    TextView titlebarTitle;
    ImageView titlebarRight;

    protected Activity activity;
    
    public BaseViewImpl(Activity activity){
        this.activity = activity;
//        bindView();
    }

    public void bindView(){
        setViewContent();
        titlebarBack = (ImageView) activity.findViewById(R.id.titlebar_back);
        titlebarTitle = (TextView) activity.findViewById(R.id.titlebar_title);
        titlebarRight = (ImageView) activity.findViewById(R.id.titlebar_right);
        if(titlebarBack != null) {
            titlebarBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.finish();
                }
            });
        }
    }

    protected void setViewContent(){

    }


    public ProgressDialog getBaseProgressDialog() {
        return baseProgressDialog;
    }

    public void showBaseDialog() {
        if (baseProgressDialog == null || baseContentView == null) {
            baseProgressDialog = new ProgressDialog(activity, R.style.dialog);
            baseProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    baseProgressDialogDissmissed();
                }
            });
            baseContentView = activity.getLayoutInflater().inflate(
                    R.layout.custom_progress_dialog,
                    (ViewGroup) activity.findViewById(R.id.custom_progressdialog));
            ((TextView) baseContentView.findViewById(R.id.tv_desc))
                    .setText(activity.getResources().getText(
                            R.string.common_in_processing));

        }
        if (!baseProgressDialog.isShowing()) {
            baseProgressDialog.show();
            baseProgressDialog.setContentView(baseContentView);
        }
    }

    public void progressDialogDissmissed() {

    }

    public void baseProgressDialogDissmissed() {

    }

    @Override
    public void setTitleView(String title) {
        titlebarTitle.setText(title);
    }

    @Override
    public void setTitleView(int title_id) {
        titlebarTitle.setText(title_id);
    }

    public void dismissBaseDialog() {
        if (baseProgressDialog != null && baseProgressDialog.isShowing()) {
            baseProgressDialog.dismiss();
        }
    }

    public void showProgressDialog() {
        if (progressDialog == null || contentView == null) {
            progressDialog = new ProgressDialog(activity, R.style.dialog);
            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    // Utils.sysoInfo("baseDialogDissmissed");
                    progressDialogDissmissed();
                }
            });
            contentView = activity.getLayoutInflater().inflate(
                    R.layout.custom_progress_dialog,
                    (ViewGroup) activity.findViewById(R.id.custom_progressdialog));
            ((TextView) contentView.findViewById(R.id.tv_desc))
                    .setText(activity.getResources().getText(
                            R.string.common_in_processing));
        }
        if (!activity.isFinishing() && !progressDialog.isShowing()) {
            progressDialog.show();
            progressDialog.setContentView(contentView);
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
