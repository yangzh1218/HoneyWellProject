package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config.barcode.decode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.controller.RouteLibraryController;
import com.wulian.routelibrary.controller.TaskResultListener;
import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.common.Common;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.UserDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.model.ConfigWiFiInfoModel;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config.DeviceAlreadyBindedResultActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config.DeviceIdQueryActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config.DeviceLaunchGuideActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.config.barcode.camera.CameraManager;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.navigation.Navigator;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.ViewfinderView;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.DialogUtils;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.MessageUtil;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;

import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.CookieHandler;
import java.text.DateFormat;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;


/**
 * 作者：Administrator on 2016/6/14 18:52
 * 邮箱：huihui@wuliangroup.com
 */
public class CaptureActivity extends BaseFragmentActivity implements
        SurfaceHolder.Callback, View.OnClickListener, CallBack {
    private boolean hasSurface = false;
    private String characterSet = "UTF-8";
    private ViewfinderView viewfinderView;
    private InactivityTimer inactivityTimer;
    private CameraManager cameraManager;
    private Vector<BarcodeFormat> decodeFormats;// 编码格式
    private CaptureActivityHandler mHandler;// 解码线程
    Button titlebar_flash;// 闪光灯
    private ProgressDialog mProgress;// 进度
    private String deviceId;
    private String seed;
    private Dialog queryDialog;
    private static final int MSG_BIND_CHECK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initSetting();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        mBaseView.bindView();
        initView();
        Interface.getInstance().setCallBack(this);
        Interface.getInstance().setContext(this);
    }

    /**
     * @Function 初始化窗口设置
     * @author Wangjj
     * @date 2014年10月24日
     */
    private void initSetting() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 保持屏幕高亮
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 无标题
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
    }

    private void initView() {
        titlebar_flash = (Button) findViewById(R.id.titlebar_flash);
        titlebar_flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFlash();
            }
        });
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        cameraManager = new CameraManager(getApplicationContext());//getApplication()
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {

        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }


    @Override
    public void onClick(View view) {

    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    /**
     * 主要对相机进行初始化工作
     */
    @Override
    protected void onResume() {
        super.onResume();
        inactivityTimer.onActivity();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            // 如果SurfaceView已经渲染完毕，会回调surfaceCreated，其中已经调用了initCamera()
            surfaceHolder.addCallback(this);
            // surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        // 恢复活动监控器
        inactivityTimer.onResume();
        if (queryDialog != null) {
            queryDialog.dismiss();
        }
    }

    /**
     * 暂停活动监控器,关闭摄像头
     */
    @Override
    protected void onPause() {
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        // 暂停活动监控器
        inactivityTimer.onPause();
        // 关闭摄像头
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
        super.onPause();
        if (!cameraManager.isOpen()) {
            titlebar_flash.setBackgroundResource(R.drawable.flash_off);
        } else {
            titlebar_flash.setBackgroundResource(R.drawable.flash_on);
        }
    }

    /**
     * 停止活动监控器,保存最后选中的扫描类型
     */
    @Override
    protected void onDestroy() {
        // 停止活动监控器
        inactivityTimer.shutdown();
        if (mProgress != null) {
            mProgress.dismiss();
        }
        /**added by huihui 2016-07-21
         * resolve leaked window
         */
        if (queryDialog != null) {
            queryDialog.dismiss();
        }

        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    // 感兴趣的数据
    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet
            .of(ResultMetadataType.ISSUE_NUMBER,
                    ResultMetadataType.SUGGESTED_PRICE,
                    ResultMetadataType.ERROR_CORRECTION_LEVEL,
                    ResultMetadataType.POSSIBLE_COUNTRY);

    /**
     * 获取扫描结果，由CaptureActivityHandler回调 。这里缺少方法申明的约束。
     *
     * @param rawResult
     * @param barcode
     * @param scaleFactor
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        mProgress = new ProgressDialog(CaptureActivity.this, R.style.dialog);
        mProgress.show();
        View mProgressView = getLayoutInflater().inflate(
                R.layout.custom_progress_dialog,
                (ViewGroup) findViewById(R.id.custom_progressdialog));
        ((TextView) mProgressView.findViewById(R.id.tv_desc))
                .setText(R.string.config_scan_success_processing);
        mProgress.setContentView(mProgressView);

        // mProgress = ProgressDialog.show(CaptureActivity.this, null,
        // "已扫描，正在处理···", true, true);
        mProgress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                restartPreviewAfterDelay(1l);
            }
        });

        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT);
        Map<ResultMetadataType, Object> metadata = rawResult
                .getResultMetadata();
        StringBuilder metadataText = new StringBuilder(20);
        if (metadata != null) {
            for (Map.Entry<ResultMetadataType, Object> entry : metadata
                    .entrySet()) {
                if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
                    metadataText.append(entry.getValue()).append('\n');
                }
            }
            if (metadataText.length() > 0) {
                metadataText.setLength(metadataText.length() - 1);
            }
        }
        parseBarCode(rawResult.getText());
    }

    // 解析二维码
    private void parseBarCode(String msg) {
        // 手机震动
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
        // if (!msg.startsWith("cmic")) {
        // msg = "cmic" + msg;
        // }

        if (!TextUtils.isEmpty(msg)) {
//            Intent it = new Intent(CaptureActivity.this,
//                    DeviceIdQueryActivity.class);
//            it.putExtra("msgData", msg);
//            startActivity(it);
            queryDialog = DialogUtils.showQueryIdDialog(this);
            initData(queryDialog, msg);
        } else {
            setResult(RESULT_OK, new Intent().putExtra("scan_result", msg));
            finish();
        }
    }

    private void initDialog(Dialog dialog, boolean isQuery) {
//        rl_query_device = (RelativeLayout) findViewById(R.id.rl_query_device);
//        RelativeLayout rl_query_device_fail = (RelativeLayout) dialog.findViewById(R.id.rl_query_device_fail);
        TextView textView = (TextView) dialog.findViewById(R.id.tv_query_device_fail);
        Button btn_retry_query_device = (Button) dialog.findViewById(R.id.btn_retry_query_device);
        if (isQuery) {
            textView.setText(R.string.config_query_device_now);
            btn_retry_query_device.setVisibility(View.GONE);
        }
        btn_retry_query_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBindingCheck();
            }
        });
    }

    private void initData(Dialog dialog, String msgData) {
        if (TextUtils.isEmpty(msgData)) {
            CustomToast.show(this, R.string.config_error_deviceid);
            dialog.dismiss();
            return;
        }

        if (msgData.contains("device_id=")) {
            deviceId = Utils.getRequestParams(msgData).get("device_id");
        } else {
            deviceId = msgData;
        }
        deviceId = deviceId.toLowerCase(Locale.getDefault());
        if (deviceId != null && deviceId.length() == 20) {// 6+2+12
            initDialog(dialog, true);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startBindingCheck();
                }
            };
            DialogUtils.initQueryIdDialog(dialog, true, onClickListener);
            startBindingCheck();
        } else {
            CustomToast.show(this, R.string.config_error_deviceid);
            dialog.dismiss();
        }
    }

    /**
     * 在经过一段延迟后重置相机以进行下一次扫描。 成功扫描过后可调用此方法立刻准备进行下次扫描
     *
     * @param delayMS
     */
    public void restartPreviewAfterDelay(long delayMS) {
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    /**
     * 初始化摄像头。打开摄像头，检查摄像头是否被开启及是否被占用
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the mHandler starts the preview, which can also throw a
            // RuntimeException.
            if (mHandler == null) {
                mHandler = new CaptureActivityHandler(this, decodeFormats,
                        null, characterSet, cameraManager);

            }
        } catch (IOException ioe) {
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            displayFrameworkBugMessageAndExit();
        }
    }


    /**
     * 初始化照相机失败显示窗口
     */
    private void displayFrameworkBugMessageAndExit() {
        final AlertDialog noticeDialog = new AlertDialog.Builder(this,
                R.style.alertDialog).create();
        View noticeDialogView = LinearLayout.inflate(this,
                R.layout.custom_alertdialog_singlebutton, null);
        TextView tv_info = (TextView) noticeDialogView
                .findViewById(R.id.tv_info);
        tv_info.setText(getString(R.string.config_camera_framework_bug));
        Button btn_sure = (Button) noticeDialogView.findViewById(R.id.btn_sure);
        // 界面事件
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeDialog.dismiss();
                CaptureActivity.this.finish();
            }
        });
        noticeDialog.show();
        noticeDialog.setContentView(noticeDialogView);
    }

    /**
     * 闪光灯调节器。自动检测环境光线强弱并决定是否开启闪光灯
     */
    private void setFlash() {
        if (titlebar_flash.getTag() == null) {// 默认关闭状态
            cameraManager.setTorch(true);
            titlebar_flash.setTag("on");
            titlebar_flash.setBackgroundResource(R.drawable.flash_on);
        } else {// 开启状态
            cameraManager.setTorch(false);
            titlebar_flash.setTag(null);
            titlebar_flash.setBackgroundResource(R.drawable.flash_off);
        }
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }


    private void getDeviceFlag() {
        Interface.getInstance().V3AppFlag(deviceId);
    }

    private void startBindingCheck() {
        Interface.getInstance().BindingCheck(deviceId);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_BIND_CHECK:
                    CustomToast.show(CaptureActivity.this, R.string.config_device_already_in_list);
                    Navigator navigator = new Navigator(CaptureActivity.this);
                    navigator.navigateToDeviceList(CaptureActivity.this);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String json) {
        LibraryLoger.d("jsonData is:" + json);
        switch (apiType) {
            case V3_APP_FLAG:
                boolean isQrConnect = false;
                isQrConnect = Common.handleBarCodeJson(CaptureActivity.this, json);

                if (TextUtils.isEmpty(deviceId) == false) {
                    isQrConnect = Common.getQrConnectFromDeviceId(deviceId);
                }
                ConfigWiFiInfoModel configWiFiInfoModel = Common.getConfigWiFiInfoModel(seed, isQrConnect, deviceId);
                configWiFiInfoModel.setAddDevice(true);
                Intent it1 = new Intent(this, DeviceLaunchGuideActivity.class);
                it1.putExtra("configInfo", configWiFiInfoModel);
                it1.putExtra("resetWifi", false);
                startActivity(it1);
                this.finish();
                break;
            case V3_BIND_CHECK:
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String uuid = jsonObject.getString("uuid");
                    if (!TextUtils.isEmpty(uuid)) {
                        if (UserDataRepository.getInstance().userInfo().getUuid().equalsIgnoreCase(uuid)) {
                            handler.sendMessage(MessageUtil.set(MSG_BIND_CHECK, "", ""));
                        } else {
                            Intent it = new Intent(this, DeviceAlreadyBindedResultActivity.class);
                            it.putExtra("deviceId", deviceId);
                            startActivity(it);
                        }
                    } else {
                        seed = jsonObject.getString("seed");
                        getDeviceFlag();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {
        Bundle bundle = new Bundle();
        bundle.putString("error", exception.getMessage());
        Message message = new Message();
        message.setData(bundle);
        message.what = 2;
        baseActivityPresenter.getUIHandler().sendMessage(message);
    }


}
