package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.play;

/**
 * 作者：Administrator on 2016/6/15 20:07
 * 邮箱：huihui@wuliangroup.com
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.APPConfig;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.UserDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Scene;
import com.wulian.sdk.android.oem.honeywell.ipc.sip.SipFactory;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.AngleMeter;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.MyHorizontalScrollView;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.YuntaiButton;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.YuntaiLeftRightButton;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.DialogUtils;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.manage.SipCallSession;
import com.wulian.siplibrary.manage.SipManager;
import com.wulian.siplibrary.manage.SipProfile;
import com.wulian.siplibrary.utils.WulianLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.videoengine.ViERenderer;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Wangjj
 * @ClassName: PlayVideoActivity
 * @Function: 视频播放页
 * @Date: 2014年10月21日
 * @email wangjj@wuliangroup.cn
 */
public class PlayVideoActivity extends BaseFragmentActivity implements
        OnClickListener, OnTouchListener {

    private Scene scene;
    private YuntaiButton yuntaiBtn, yuntai_btn_nospeak,
            yuntai_btn_nospeak_landscape, yuntai_btn_new;
    private YuntaiLeftRightButton yuntai_left_right_btn_nospeak_landscape,
            yuntai_left_right_btn_new;
    private PopupWindow popDefinitionWindow;
    private ImageView iv_control_csc_bar, btn_titlebar_back;
    private View popDefinitionView, divider_silence;
    private TextView tv_control_definition_bar, tv_control_definition1,
            tv_control_definition2, tv_control_definition3, tv_speed,
            tv_speed_landscape, tv_video_play_timeorname,
            tv_control_fullscreen_bar;
    private ImageView iv_cap_effect;
    private ImageView iv_cap_gallery, iv_cap_gallery_landscape;
    private AlertDialog cscDialog;
    private View cscDialogView;
    private Button btn_csc_dismiss, btn_csc_restore_default, progress_refresh,
            btn_control_landscape_to_portrait,
            btn_control_definition_bar_landscape,
            btn_control_snapshot_landscape, btn_control_talkback_landscape,
            btn_control_silence_landscape, btn_album_new, btn_scene_new,
            btn_history, btn_video_replay;
    private SeekBar sb_csc_luminance;
    // , sb_csc_contrast, sb_csc_saturability,
    private Context mContext;

    private SurfaceView cameraPreview;
    private WakeLock wakeLock, videoWakeLock;
    private PowerManager powerManager;
    private Dialog mTipDialog;
    private final static String TAG = "PlayVideoActivity";
    private Device device;
    private SipProfile sipProfile;
    private AngleMeter anglemeter;
    SipCallSession sipCallSession;
    int callId = -1;
    int seq = 0;
    String savePath = "", snapSavePath = "";
    private boolean isMuteOpen/* 静音是否打开 */ = true;
    private boolean showSnap = false;
    private int widthRatio = 16;
    private int heightRatio = 9;
    private int minWidth, maxWidth, beginWidth;
    GestureDetector mGestureDetector;
    ScaleGestureDetector mScaleGestureDetector;
    private boolean is_portrait_fullSize = true;
    private MyHorizontalScrollView horizontal_sv;
    private static int SCROLLBY = 18;// 每次偏移的距离
    private RelativeLayout rl_video;// 竖屏宿主

    private boolean isControling = false, hasYuntai = false,
            hasYuntaileftRight = false, hasSpeak = false;// 使得控制和断开成对匹配，同时避免只能长按取消
    private boolean isIgnoreSingleTapConfirmed = false;// 如果按下时，当前正在控制中，则只要停止控制即可，忽略控制条的显隐。

    String deviceControlUrl;// 设备控制sip地址
    String deviceCallUrl;// 设备呼叫sip地址

    private RelativeLayout rl_video_control_panel,
            rl_video_control_panel_nospeak;
    LinearLayout include_control_bar;
    private LinearLayout ll_play_container, ll_contain_fullscreen_btn,
            ll_control_panel_new, ll_video_control_panel_new,
            ll_control_yuntai, ll_control_forv5;
    private RelativeLayout rl_control_panel, rl_control_panel_nospeak,
            rl_control_landscape;
    private SharedPreferences sp, spLan;
    private boolean isRunOnUI = false, isConncted = false,
            isShowVideo = true/* 视频是否显示,默认显示，便于判断 */, isMediaPlaying = false,
            isVideoInvert = false, isSnapshot = false/* 退出截屏 */;

    private boolean isStop = false;
    private boolean hasSDCard = false;
    private int disconnectCount = 0;
    private long startTime = System.currentTimeMillis();// 启动时间
    private int isNegotiationState = NEGOTIATION_UNKNOWN;
    private String lastSpeed = "0", devicePwd = "", deviceCallIp = "";

    private Button btn_snapshot, btn_talkback, btn_mute, btn_silence_new,
            btn_snapshot_nospeak, btn_snapshot_new, btn_speak_new,
            btn_speak_no_yuntai_new;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    // 传感器相关
    private SensorManager sensorManager;
    private Sensor acceleSensor, magneticSensor, origintationSensor;
    private boolean isSensorRegister = false;
    private SensorEventListener sensorEventListener;
    /*
     * private float[] gravity, geomagnetic, matrixR = new float[9], results =
     * new float[3];
     */
    private float lastRotation = -1, detlaRotation = 3/* 越小越灵敏 */;

    // nat状态
    private static final int NEGOTIATION_UNKNOWN = 0;
    private static final int NEGOTIATION_INPROGRESS = 1;
    private static final int NEGOTIATION_SUCCESS = 2;
    private static final int NEGOTIATION_FAIL = 3;

    private static final int SHOWSPEED = 3;
    private static final int KEYFRAME = 4;
    private static final int INENABLE = 10;
    private static final int ENABLE = 11;
    private static final int SPEED_RETRY = 5;
    private static final int SPEED_RETRY_FORCE = 6;
    private static final int SPEED_RETRY_TIME = 15000;// 15秒0kb监控
    private static final int AUTO_SILENCE = 12;// 自动切换为静音状态
    private static final int VIDEO_CENTER = 13;// 视频画面居中
    private static final int AUTO_SILENCE_TIME = 20;// 20秒
    private static final int NET_CHECK = 7;
    private static final int NET_CHECK_RANGE = 10;// 在前10秒检测
    private static final int SHOWSPEED_INTERVAL = 5;// 速度间隔为5秒
    private static final int SEND_RTP = 100;
    private static final int SCENE_RESULT = 110;
    private static final int SCENE_OVERTIME = 111;

    private static final int YUNTAI_CONTROL = 1;
    private static final int YUNTAI_LEFT_RIGHT_CONTROL = 2;
    private LinearLayout ll_linking_video, ll_linking_video_refresh;
    UpdateUIFromCallRunnable uiUpdate = new UpdateUIFromCallRunnable();
    private YuntaiButton.Direction curDirection;
    private YuntaiLeftRightButton.Direction curLeftRightDirection;
    private List<Scene.SData> mSDataList;
    private boolean isInPlayUI;
    private Date toggleOldDate;//切换横竖屏的时间计时器，超过两秒才允许切换
    private Runnable showSnapRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            showSnap = false;
            iv_cap_gallery.setVisibility(View.GONE);
            iv_cap_gallery_landscape.setVisibility(View.GONE);
        }
    };

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCENE_OVERTIME:
                    mBaseView.dismissBaseDialog();
                    if (isInPlayUI)
                        CustomToast.show(PlayVideoActivity.this, getResources()
                                .getString(R.string.scene_timeout));
                    break;
                case SCENE_RESULT:
                    myHandler.removeMessages(SCENE_OVERTIME);
                    if (msg.arg1 == 1) {
                        scene = Scene.getInstance();
                        int idx = scene.getSelectdIdx();
                        mSDataList = scene.getDataList();
                        mBaseView.dismissBaseDialog();
                        if (isInPlayUI)
                            CustomToast.show(PlayVideoActivity.this, getResources()
                                            .getString(R.string.scene_success)
                        /* + scene.getDataList().get(idx).title */);
                    } else {
                        mBaseView.dismissBaseDialog();
                        if (isInPlayUI)
                            CustomToast.show(PlayVideoActivity.this, getResources()
                                    .getString(R.string.scene_failure));
                    }
                    break;
                case SPEED_RETRY:
                    if (callId != -1) {
                        // CustomToast.show(PlayVideoActivity.this, "没有数据流,重连...");
                        Utils.sysoInfo("没有数据流,重连callId=" + callId);
                        myHandler.removeMessages(SPEED_RETRY);
                        hangUpVideo();
                        reCallVideo();
                    }
                    break;
                case SPEED_RETRY_FORCE:
                    hangUpVideo();
                    reCallVideo();
                    break;
                case SHOWSPEED:
                    if (callId != -1) {
                        String speedInfo = SipController.getInstance()
                                .getCallSpeedInfos(callId);// 越来越大，用long
                        if (!TextUtils.isEmpty(speedInfo)) {
                            long speed = 0;
                            long delatSpeed = 0;
                            try {
                                speed = Long.parseLong(speedInfo);
                                delatSpeed = speed - Long.parseLong(lastSpeed);
                                delatSpeed = (delatSpeed > 0 ? delatSpeed : 0)
                                        / SHOWSPEED_INTERVAL;// 除以SHOWSPEED_INTERVAL因为有SHOWSPEED_INTERVAL秒间隔
                                lastSpeed = speedInfo;
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            if (delatSpeed == 0) {// 连续10秒，则重试
                                Utils.sysoInfo("检测到速度为0KB");
                                if (!myHandler.hasMessages(SPEED_RETRY)) {
                                    Utils.sysoInfo("发送延迟10秒重呼");
                                    myHandler.sendEmptyMessageDelayed(SPEED_RETRY,
                                            SPEED_RETRY_TIME);
                                } else {
                                    Utils.sysoInfo("10秒重呼已经存在,不再发送");
                                }
                            } else {

                                if (myHandler.hasMessages(SPEED_RETRY)) {
                                    Utils.sysoInfo("检测到速度>0,移除重呼消息");
                                    myHandler.removeMessages(SPEED_RETRY);
                                }
                            }
                            long perSpeed = delatSpeed / 8 / 1000;
                            tv_speed.setText("" + (perSpeed > 0 ? perSpeed : 1)
                                    + "KB/s");
                            tv_speed_landscape.setText(""
                                    + (perSpeed > 0 ? perSpeed : 1) + "KB/s");
                            if (tv_speed.getVisibility() == View.GONE
                                    && isPortrait()) {
                                tv_speed.setVisibility(View.VISIBLE);
                            }
                            if (tv_speed_landscape.getVisibility() == View.GONE
                                    && !isPortrait()) {
                                tv_speed_landscape.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    myHandler.removeMessages(SHOWSPEED);// 清空历史消息，避免频繁
                    myHandler.sendEmptyMessageDelayed(SHOWSPEED,
                            SHOWSPEED_INTERVAL * 1000);
                    break;
                case KEYFRAME:
                    if (!isRunOnUI) {
                        if (callId != -1 && isConncted) {
                            Utils.sysoInfo("10秒钟,已经呼通，关键帧还没来,直接runOnUI");
                            isRunOnUI = true;
                            isMediaPlaying = true;
                            runOnUiThread(uiUpdate);
                        } else {
                            myHandler.sendEmptyMessageDelayed(KEYFRAME, 5000);
                            Utils.sysoInfo("10秒钟,还没有视频流,延迟5秒判断关键帧");
                        }
                    } else {
                        Utils.sysoInfo("关键帧判断handler消息KEYFRAME来了，但是已经播放中");
                        isMediaPlaying = true;
                    }
                    break;
                case NET_CHECK:
                    if (System.currentTimeMillis() < startTime
                            + NET_CHECK_RANGE * 1000) {
                        myHandler.sendEmptyMessageDelayed(NET_CHECK, 1000);
                    } else {
                        myHandler.removeMessages(NET_CHECK);
                        ViERenderer.setIsReturnPictureState();
                        makeCallDevice(device);
                    }
                    break;
                case AUTO_SILENCE:
                    isMuteOpen = true;
                    btn_mute.setBackgroundResource(R.drawable.selector_function_silence_off);
                    btn_control_silence_landscape
                            .setBackgroundResource(R.drawable.selector_video_btn_landscape_voice_off);
                    btn_silence_new
                            .setBackgroundResource(R.drawable.cb_silence_off);
                    if (callId != -1)
                        SipController.getInstance().closeAudioTransport(callId);
                    break;
                case VIDEO_CENTER:
                    videoCenter();
                    break;
                default:
                    break;
            }
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ViERenderer.FILE_OK:// 可能是会被上一个消息接受到，导致其他设备也获取该截图作为背景
                    if (isSnapshot) {// 退出截屏,不要重置isSnapshot =
                        // false，否则导致下面面的!isSnapshot成立
                        Bundle bundle = msg.getData();
                        final Bitmap bitmap = bundle
                                .getParcelable(ViERenderer.GET_PICTURE);
                        if (bitmap != null) {
                            Utils.sysoInfo(this + "接受到handler图片"
                                    + device.getDid());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {// 800毫秒的卡顿
                                    Utils.saveBitmap(device.getDid(), bitmap,
                                            PlayVideoActivity.this);
                                    if (bitmap != null && !bitmap.isRecycled()) {
                                        bitmap.recycle();
                                    }
                                }
                            }).start();
                            mHandler.removeMessages(ViERenderer.FILE_OK);
                        }
                        hangUpVideo();// final,挂断
                    } else {
                        Bundle bundle = msg.getData();
                        Bitmap bitmap = bundle
                                .getParcelable(ViERenderer.GET_PICTURE);
                        String picPath = bundle.getString(ViERenderer.GET_PATH);
                        if (bitmap != null) {
                            //
                            // if (bitmap != null && !bitmap.isRecycled()) {
                            // bitmap.recycle();
                            // bitmap = null;
                            // CustomToast.show(PlayVideoActivity.this,R.string.play_take_picture_ok);
                            showSnap = true;
                            if (isPortrait()) {
                                iv_cap_gallery.setVisibility(View.VISIBLE);
                                iv_cap_gallery.setTag(picPath);
                                // Picasso.with(mContext).load("file://" + picPath)
                                // .fit().into(iv_cap_gallery);
                                // ImageLoader.getInstance().displayImage(
                                // "file://" + picPath, iv_cap_gallery);
                                iv_cap_gallery.setImageBitmap(bitmap);
                                // ImageLoader.getInstance().displayImage(
                                // "file://" + picPath, iv_cap_gallery);
                            } else {
                                iv_cap_gallery_landscape
                                        .setVisibility(View.VISIBLE);
                                iv_cap_gallery_landscape.setTag(picPath);
                                // Picasso.with(mContext).load("file://" + picPath)
                                // .fit().into(iv_cap_gallery_landscape);
                                // ImageLoader.getInstance().displayImage(
                                // "file://" + picPath,
                                // iv_cap_gallery_landscape);
                                iv_cap_gallery_landscape.setImageBitmap(bitmap);
                                // ImageLoader.getInstance().displayImage(
                                // "file://" + picPath,
                                // iv_cap_gallery_landscape);
                            }

                            myHandler.removeCallbacks(showSnapRunnable);
                            myHandler.postDelayed(showSnapRunnable, 3000);

                        }
                        if (PlayVideoActivity.this != null) {

                            // MediaScannerConnection.scanFile(PlayVideoActivity.this,
                            // new String[] { snapSavePath },
                            // new String[] { "image/png" }, null);
                            // Utils.sysoInfo("请求扫描" + snapSavePath);
                        }
                    }
                    break;
                case ViERenderer.TAKE_PICTURE_FAIL:
                    if (!isSnapshot) {
                        CustomToast.show(PlayVideoActivity.this, R.string.play_take_picture_exception);
                    }
                    break;
                case ViERenderer.FILE_MOUNT_EXCEPTION:
                    if (!isSnapshot) {
                        CustomToast.show(PlayVideoActivity.this, R.string.play_take_picture_mount_exception);
                    }
                    break;
                case ViERenderer.FILE_PICTURE_CREATE_EXCEPTION:
                    if (!isSnapshot) {
                        CustomToast.show(PlayVideoActivity.this, R.string.play_take_picture_create_exception);
                    }
                    break;
                case ViERenderer.FILE_PICTURE_EXCEPTION:
                    if (!isSnapshot) {
                        CustomToast.show(PlayVideoActivity.this, R.string.play_take_picture_exception);
                    }
                    break;
                case SEND_RTP:
                    WulianLog.d(TAG, "SendRTP");
                    SipController.getInstance().sendRtp(callId);
                    mHandler.sendEmptyMessageDelayed(SEND_RTP, 5000);
                    break;
                case ViERenderer.PICTURE_HAS_COMING:
                    WulianLog.d(TAG, "PICTURE_HAS_COMING");
//                    ICamGlobal.isNeedRefreshSnap = true;
                    ll_linking_video.setVisibility(View.GONE);
                    myHandler.sendEmptyMessage(SHOWSPEED);
                    break;
                default:
                    break;
            }
        }
    };

    public void videoCenter() {
        if (horizontal_sv != null) {
            horizontal_sv.scrollTo((maxWidth - minWidth) / 2, 0);
        }
    }

    Handler InputHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ENABLE:
                    WulianLog.d("PlayVideo", "ENABLE");
                    SipController.getInstance().setMicrophoneMute(false, callId);
                    InputHandler.sendEmptyMessageDelayed(INENABLE, 2000);
                    break;
                case INENABLE:
                    WulianLog.d("PlayVideo", "INENABLE");
                    SipController.getInstance().setMicrophoneMute(true, callId);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    Handler ytHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case YUNTAI_CONTROL:
                    curDirection = (YuntaiButton.Direction) msg.obj;
                    switch (curDirection) {
                        case left:
                            yuntai_left();
                            break;
                        case up:
                            yuntai_up();
                            break;
                        case right:
                            yuntai_right();
                            break;
                        case down:
                            yuntai_down();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    Handler leftRighttHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case YUNTAI_LEFT_RIGHT_CONTROL:
                    curLeftRightDirection = (YuntaiLeftRightButton.Direction) msg.obj;
                    switch (curLeftRightDirection) {
                        case left:
                            yuntai_left();
                            break;
                        case right:
                            yuntai_right();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        mContext = this;
        initViews();
        initListeners();
        initData();
        attachVideoPreview();
        showVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.sysoInfo("onResume");
        reCallVideo();// 弹窗也会照成onResume
        disconnectCount = 0;
        isStop = false;
        int callStream = SipController.getInstance().getCallStream();
        if (callStream != -1)
            this.setVolumeControlStream(callStream);
        registerSensorListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterSeneorListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utils.sysoInfo("onStop");
        isStop = true;
        hangUpVideo();// 如果不关闭，电话岂不是被窃听了
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }

    private void showVideo() {

        // 2、用户注册账号
        sipProfile = SipFactory.getInstance().registerAccount(UserDataRepository.getInstance().suid(), UserDataRepository.getInstance().sdomain(), UserDataRepository.getInstance().password());
        if (sipProfile == null) {
            CustomToast.show(this,
                    R.string.login_user_account_register_fail);
            PlayVideoActivity.this.finish();
            return;
        }
        // 3、显示视频
        if (cameraPreview.getVisibility() == View.GONE) {
            cameraPreview.setVisibility(View.VISIBLE);
        }
    }

    private void hangUpVideo() {
        ll_linking_video.setVisibility(View.GONE);
        isShowVideo = false;
        isConncted = false;// 立即标记断开
        isMediaPlaying = false;
        if (callId != -1) {
            SipController.getInstance().setMicrophoneMute(true, callId);
            SipController.getInstance().setSpeakerphoneOn(false, callId);
        }
        SipFactory.getInstance().hangupAllCall();
        callId = -1;
        isRunOnUI = false;
        tv_speed.setText("0KB/s");
        tv_speed_landscape.setText("0KB/s");

        myHandler.removeMessages(SPEED_RETRY);
        myHandler.removeMessages(SHOWSPEED);
        myHandler.removeMessages(KEYFRAME);
        myHandler.removeMessages(AUTO_SILENCE);

        InputHandler.removeMessages(ENABLE);
        InputHandler.removeMessages(INENABLE);

        mHandler.removeMessages(ViERenderer.FILE_OK);
        mHandler.removeMessages(ViERenderer.TAKE_PICTURE_FAIL);
        mHandler.removeMessages(ViERenderer.FILE_MOUNT_EXCEPTION);
        mHandler.removeMessages(ViERenderer.FILE_PICTURE_CREATE_EXCEPTION);
        mHandler.removeMessages(ViERenderer.FILE_PICTURE_EXCEPTION);
        mHandler.removeMessages(SEND_RTP);
    }

    private void reCallVideo() {
        isShowVideo = true;
        isConncted = false;// 先标记断开，等待状态通知改变
        isMediaPlaying = false;
        ll_linking_video_refresh.setVisibility(View.GONE);
        ll_linking_video.setVisibility(View.VISIBLE);
        initDefinition();
        if (deviceCallUrl != null) {
            ViERenderer.setIsReturnPictureState();
            makeCallDevice(device);
        }
    }

    public void makeCallDevice(Device device) {
        SipFactory.getInstance().makeCall(deviceCallUrl,
                SipFactory.getInstance().getSipProfile());
    }

    private void initData() {
        isInPlayUI = true;
        mGestureDetector = new GestureDetector(this, new OnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Utils.sysoInfo("onSingleTapUp");
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                Utils.sysoInfo("onShowPress");
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                // Utils.sysoInfo("onScroll");
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Utils.sysoInfo("onLongPress");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {

                Utils.sysoInfo("onFling");
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                Utils.sysoInfo("mGestureDetector onDown" + e.getPointerCount());// 个数始终是1

                return true;// false会忽略掉手势
            }
        });
        mGestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Utils.sysoInfo("onSingleTapConfirmed");
                if (popDefinitionWindow.isShowing()) {
                    popDefinitionWindow.dismiss();
                    return false;
                }
                if (isIgnoreSingleTapConfirmed) {
                    isIgnoreSingleTapConfirmed = false;
                    Utils.sysoInfo("由于云台控制中，这里的单击显隐控制条方法被忽略！");
                    return false;
                }
                // TODO　显示横屏下控件
                if (!isPortrait()) {
                    if (rl_control_landscape.getVisibility() == View.GONE) {
                        rl_control_landscape.setVisibility(View.VISIBLE);
                    } else {
                        rl_control_landscape.setVisibility(View.GONE);
                    }
                }
                // }// 控制云台时，不是有效的单击
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                Utils.sysoInfo("onDoubleTapEvent");
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Utils.sysoInfo("onDoubleTap");
                // 要求双击不填冲,改为全屏
                if (isPortrait()) {
                    goLandscape();
                } else {
                    goPortrait();
                }
                //切换完，开始重新计时
                toggleOldDate = new Date();
                return false;
            }
        });

        mScaleGestureDetector = new ScaleGestureDetector(this,
                new OnScaleGestureListener() {

                    @Override
                    public void onScaleEnd(ScaleGestureDetector detector) {
                        Utils.sysoInfo("onScaleEnd");
                        updateAngleMeter(horizontal_sv);
                    }

                    @Override
                    public boolean onScaleBegin(ScaleGestureDetector detector) {
                        Utils.sysoInfo("onScaleBegin");
                        RelativeLayout.LayoutParams lp = (LayoutParams) cameraPreview
                                .getLayoutParams();
                        beginWidth = lp.width;
                        // 缩放手势开始，忽略HorizontalScrollView的水平滚动手势
                        return true;
                    }

                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        Utils.sysoInfo("onScale" + detector.getScaleFactor());
                        if (isPortrait()) {
                            float finalWidth = beginWidth
                                    * detector.getScaleFactor();
                            if (minWidth < finalWidth && finalWidth < maxWidth) {
                                RelativeLayout.LayoutParams lpf = (LayoutParams) cameraPreview
                                        .getLayoutParams();
                                lpf.width = (int) finalWidth;
                                lpf.height = (int) (finalWidth * heightRatio / widthRatio);
                                cameraPreview.setLayoutParams(lpf);
                            }
                        }
                        return false;
                    }
                });
        sp = getSharedPreferences(APPConfig.SP_CONFIG, MODE_PRIVATE);
        spLan = getSharedPreferences(APPConfig.SP_LAN_CONFIG, MODE_PRIVATE);
        device = (Device) getIntent().getParcelableExtra("device");
        String deviceIdSerials = device.getDid().substring(4, 6);
        if (deviceIdSerials.equals("01")) {
            anglemeter.setMaxAngle("86°");
        } else if (deviceIdSerials.equals("06")) {
            anglemeter.setMaxAngle("100°");
        } else {
            anglemeter.setMaxAngle("111°");
        }
        anglemeter.invalidate();
        tv_video_play_timeorname.setText(device.getNick());
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acceleSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        origintationSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {

                if (event.sensor == null)// 没有传感器
                    return;

                // 简单方式
                if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                    if (lastRotation != -1) {
                        if (event.values[0] - lastRotation > detlaRotation) {
                            // scrollViewRight();//暂时不需要传感器功能
                        } else if (event.values[0] - lastRotation < -detlaRotation) {
                            // scrollViewLeft();
                        }
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        mediaPlayer = MediaPlayer.create(this, R.raw.snapshot);

        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // mp.release();
            }
        });

        mediaPlayer.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.release();
                return false;
            }
        });
        disconnectCount = 0;

        deviceCallUrl = device.getDid() + "@" + device.getSip_domain();
        deviceControlUrl = deviceCallUrl;// sip:cmicxxx@wuliangroup.cn
        isVideoInvert = sp.getBoolean(device.getDid()
                + APPConfig.VIDEO_INVERT, false);
        // 设备的功能规则
        if (device.getDid().toLowerCase(Locale.US).startsWith("cmhw01")) {// 大小写铭感,统一转成小写比较
            hasYuntai = true;
            // hasSpeak = true;// PML TEST
            hasSpeak = false;
            hasYuntaileftRight = false;
        } else {
            hasYuntai = false;
            /**added by huihui 2016-09-09
             *临时设置
             */
//            hasSpeak = true;
            hasYuntaileftRight = false;
        }

        if (device.getDid().toLowerCase(Locale.US).startsWith("cmhw04")) {// 大小写铭感,统一转成小写比较
            hasYuntai = true;
            hasSpeak = true;
            hasYuntaileftRight = false;
        }
        if (device.getDid().toLowerCase(Locale.US).startsWith("cmhw02")) {// 大小写铭感,统一转成小写比较
            hasYuntai = false;
            hasSpeak = true;
            hasYuntaileftRight = true;
        }
        hasSpeak=false;
        btn_control_definition_bar_landscape.setVisibility(View.GONE);
        /*
         * if (hasYuntai) { yuntaiBtn.setVisibility(View.VISIBLE);
		 * yuntai_btn_nospeak.setVisibility(View.VISIBLE);
		 * yuntai_btn_nospeak_landscape.setVisibility(View.VISIBLE);
		 * yuntai_btn_nospeak_landscape.setBackground(this,
		 * R.drawable.video_control_panel); } else {
		 * yuntaiBtn.setVisibility(View.GONE);
		 * yuntai_btn_nospeak.setVisibility(View.GONE);
		 * yuntai_btn_nospeak_landscape.setVisibility(View.GONE); }
		 */

		/*
         * if (!hasSpeak) { // btn_talkback.setVisibility(View.GONE); //
		 * btn_mute.setVisibility(View.GONE);
		 * rl_control_panel.setVisibility(View.GONE);
		 * rl_control_panel_nospeak.setVisibility(View.VISIBLE);
		 * btn_control_silence_landscape.setVisibility(View.GONE);
		 * btn_control_talkback_landscape.setVisibility(View.GONE); } else {
		 * rl_control_panel_nospeak.setVisibility(View.GONE);
		 * rl_control_panel.setVisibility(View.VISIBLE); }
		 */

        // TODO 新版布局控件显示
        if (hasYuntai) {
            yuntai_btn_new.setVisibility(View.VISIBLE);
            yuntai_btn_nospeak_landscape.setVisibility(View.VISIBLE);
            yuntai_left_right_btn_new.setVisibility(View.GONE);
            yuntai_left_right_btn_nospeak_landscape.setVisibility(View.GONE);
            yuntai_btn_nospeak_landscape.setBackground(this,
                    R.drawable.video_control_panel);
            if (hasSpeak) {
                btn_speak_new.setVisibility(View.VISIBLE);
                btn_speak_no_yuntai_new.setVisibility(View.GONE);
                btn_silence_new.setVisibility(View.VISIBLE);
                divider_silence.setVisibility(View.VISIBLE);
                btn_control_talkback_landscape.setVisibility(View.VISIBLE);
                btn_control_silence_landscape.setVisibility(View.VISIBLE);
            } else {
                btn_speak_new.setVisibility(View.GONE);
                btn_speak_no_yuntai_new.setVisibility(View.GONE);
                // btn_silence_new.setVisibility(View.GONE);
                // divider_silence.setVisibility(View.GONE);
                btn_control_talkback_landscape.setVisibility(View.GONE);
                btn_control_silence_landscape.setVisibility(View.GONE);
            }
        } else {
            yuntai_btn_new.setVisibility(View.GONE);
            yuntai_btn_nospeak_landscape.setVisibility(View.GONE);
            if (hasSpeak) {
                btn_speak_new.setVisibility(View.GONE);
                btn_speak_no_yuntai_new.setVisibility(View.VISIBLE);
                btn_silence_new.setVisibility(View.VISIBLE);
                divider_silence.setVisibility(View.VISIBLE);
                btn_control_talkback_landscape.setVisibility(View.VISIBLE);
                btn_control_silence_landscape.setVisibility(View.VISIBLE);
            }
            if (hasYuntaileftRight) {
                yuntai_left_right_btn_new.setVisibility(View.VISIBLE);
                yuntai_left_right_btn_nospeak_landscape
                        .setVisibility(View.VISIBLE);
            } else {
                yuntai_left_right_btn_new.setVisibility(View.GONE);
                yuntai_left_right_btn_nospeak_landscape
                        .setVisibility(View.GONE);
            }
        }
        if (hasSpeak && hasYuntai) {
            ll_control_forv5.setVisibility(View.INVISIBLE);
        } else {
            ll_control_forv5.setVisibility(View.GONE);
        }
        ViERenderer.setTakePicHandler(mHandler);
        isNegotiationState = NEGOTIATION_UNKNOWN;
        registerHeadsetPlugReceiver();
        btn_mute.setBackgroundResource(R.drawable.selector_function_silence_off);
        btn_control_silence_landscape
                .setBackgroundResource(R.drawable.selector_video_btn_landscape_voice_off);
        btn_silence_new.setBackgroundResource(R.drawable.cb_silence_off);
        yuntaiBtn.setOnDirectionLisenter(new MyDirection());
        yuntai_left_right_btn_new
                .setOnDirectionLisenter(new MyLeftRightDirection());
        yuntai_left_right_btn_nospeak_landscape
                .setOnDirectionLisenter(new MyLeftRightDirection());
        yuntai_btn_nospeak.setOnDirectionLisenter(new MyDirection());
        yuntai_btn_nospeak_landscape.setOnDirectionLisenter(new MyDirection());
        yuntai_btn_new.setOnDirectionLisenter(new MyDirection());
        initDefinition();
    }

    class MyDirection implements YuntaiButton.OnDirectionLisenter {

        @Override
        public void directionLisenter(
                YuntaiButton.Direction direction) {
            yuntai_stop();
            ytHandler.removeMessages(YUNTAI_CONTROL);
            if (direction != YuntaiButton.Direction.none) {
                ytHandler.sendMessageDelayed(
                        Message.obtain(ytHandler, YUNTAI_CONTROL, direction),
                        500);
            }
        }
    }

    class MyLeftRightDirection implements
            YuntaiLeftRightButton.OnDirectionLisenter {

        @Override
        public void directionLisenter(
                YuntaiLeftRightButton.Direction direction) {
            yuntai_stop();
            leftRighttHandler.removeMessages(YUNTAI_LEFT_RIGHT_CONTROL);
            if (direction != YuntaiLeftRightButton.Direction.none) {
                leftRighttHandler.sendMessageDelayed(Message
                        .obtain(leftRighttHandler, YUNTAI_LEFT_RIGHT_CONTROL,
                                direction), 500);
            }
        }
    }

    protected void scrollViewRight() {
        int newDeltaWidth = rl_video.getWidth() - minWidth;
        if (horizontal_sv.getScrollX() + SCROLLBY < newDeltaWidth) {
            horizontal_sv.smoothScrollBy(SCROLLBY, 0);
        }
    }

    protected void scrollViewLeft() {
        if (horizontal_sv.getScrollX() - SCROLLBY > 0) {
            horizontal_sv.smoothScrollBy(-SCROLLBY, 0);
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        unregisterReceiver(headsetPlugReceiver);
        Utils.sysoInfo("onDestory");
        // 健壮性:恢复item的点击
//        ICamGlobal.isItemClickProcessing = false;
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        if (popDefinitionWindow != null && popDefinitionWindow.isShowing()) {
            popDefinitionWindow.dismiss();
        }
        myHandler.removeMessages(SPEED_RETRY);
        myHandler.removeMessages(SHOWSPEED);
        myHandler.removeMessages(KEYFRAME);
        myHandler.removeMessages(AUTO_SILENCE);

        InputHandler.removeMessages(ENABLE);
        InputHandler.removeMessages(INENABLE);

        isSnapshot = true;
        mHandler.removeMessages(ViERenderer.FILE_OK);
        mHandler.removeMessages(ViERenderer.TAKE_PICTURE_FAIL);
        mHandler.removeMessages(ViERenderer.FILE_MOUNT_EXCEPTION);
        mHandler.removeMessages(ViERenderer.FILE_PICTURE_CREATE_EXCEPTION);
        mHandler.removeMessages(ViERenderer.FILE_PICTURE_EXCEPTION);
        mHandler.removeMessages(SEND_RTP);
        ViERenderer.setTakePicNotSave();
        unregisterReceiver(callStateReceiver);
        detachVideoPreview();
        if (callId != -1) {// 已经建立连接
            // SipController.getInstance().hangupCall(callId);
        }
        SipController.getInstance().setVideoAndroidRenderer(
                SipCallSession.INVALID_CALL_ID, null);
    }

    private void attachVideoPreview() {

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE,
                "com.sip.sipdemo.onIncomingCall");
        wakeLock.setReferenceCounted(false);

        if (cameraPreview == null) {
            cameraPreview = ViERenderer.CreateRenderer(this, true,
                    isVideoInvert);// 现在是取不到尺寸的
            int deviceHeight = Utils.getDeviceSize(this).heightPixels;
            int  ss = Utils.getDeviceSize(this).widthPixels;
            int cameraPreviewHeight = deviceHeight * 4 / 9;// 根据布局中的上下比例
            int cameraPreviewWidth = (int) ((float) cameraPreviewHeight
                    / heightRatio * widthRatio);
            minWidth = Utils.getDeviceSize(this).widthPixels;
            maxWidth = cameraPreviewWidth;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    cameraPreviewWidth, cameraPreviewHeight);

            lp.addRule(RelativeLayout.CENTER_IN_PARENT);// 全尺寸时居中显示
            rl_video.addView(cameraPreview, 0, lp);
            is_portrait_fullSize = true;

            rl_video.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mGestureDetector.onTouchEvent(event);// 手势双击
                    // mScaleGestureDetector.onTouchEvent(event);// 双指缩放功能暂时关闭
                    return true;// 自定义方向判断
                }
            });
            cameraPreview.setKeepScreenOn(true);
            if (videoWakeLock == null) {
                videoWakeLock = powerManager.newWakeLock(
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                                | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                        "com.sip.sipdemo.videoCall");
                videoWakeLock.setReferenceCounted(false);
            }
        } else {
            // Log.d(TAG, "NO NEED TO Create Local Renderer");
        }

        registerReceiver(callStateReceiver,
                new IntentFilter(SipManager.GET_ACTION_SIP_CALL_CHANGED()));// "com.wulian.siplibrary.icam.service.CALL_CHANGED"
    }

    private void detachVideoPreview() {
        if (rl_video != null && cameraPreview != null) {
            rl_video.removeView(cameraPreview);
        }
        if (videoWakeLock != null && videoWakeLock.isHeld()) {
            videoWakeLock.release();
        }
        if (cameraPreview != null) {
            cameraPreview = null;
        }
    }

    private void initViews() {
        btn_titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
        iv_control_csc_bar = (ImageView) findViewById(R.id.iv_control_csc_bar);
        yuntai_left_right_btn_nospeak_landscape = (YuntaiLeftRightButton) findViewById(R.id.yuntai_left_right_btn_nospeak_landscape);
        yuntai_left_right_btn_new = (YuntaiLeftRightButton) findViewById(R.id.yuntai_left_right_btn_new);
        yuntaiBtn = (YuntaiButton) findViewById(R.id.yuntai_btn);
        yuntai_btn_nospeak = (YuntaiButton) findViewById(R.id.yuntai_btn_nospeak);
        yuntai_btn_nospeak_landscape = (YuntaiButton) findViewById(R.id.yuntai_btn_nospeak_landscape);
        yuntai_btn_new = (YuntaiButton) findViewById(R.id.yuntai_btn_new);
        anglemeter = (AngleMeter) findViewById(R.id.anglemeter);
        btn_snapshot = (Button) findViewById(R.id.btn_snapshot);
        btn_snapshot_nospeak = (Button) findViewById(R.id.btn_snapshot_nospeak);
        btn_snapshot_new = (Button) findViewById(R.id.btn_snapshot_new);
        btn_snapshot_new.setVisibility(View.INVISIBLE);
        btn_control_snapshot_landscape = (Button) findViewById(R.id.btn_control_snapshot_landscape);
        btn_talkback = (Button) findViewById(R.id.btn_talkback);
        btn_speak_new = (Button) findViewById(R.id.btn_speak_new);
        btn_speak_new.setVisibility(View.INVISIBLE);
        btn_speak_no_yuntai_new = (Button) findViewById(R.id.btn_speak_no_yuntai_new);
        btn_speak_no_yuntai_new.setVisibility(View.INVISIBLE);
        btn_control_talkback_landscape = (Button) findViewById(R.id.btn_control_talkback_landscape);
        btn_mute = (Button) findViewById(R.id.btn_silence);
        btn_silence_new = (Button) findViewById(R.id.cb_silence);
        btn_control_silence_landscape = (Button) findViewById(R.id.btn_control_silence_landscape);
        btn_album_new = (Button) findViewById(R.id.btn_album_new);
        btn_scene_new = (Button) findViewById(R.id.btn_scene_new);
        // btn_history = (Button) findViewById(R.id.btn_history);
        btn_video_replay = (Button) findViewById(R.id.btn_video_replay);

        ll_linking_video = (LinearLayout) findViewById(R.id.ll_linking_video);
        ll_linking_video_refresh = (LinearLayout) findViewById(R.id.ll_linking_video_refresh);

        rl_video_control_panel = (RelativeLayout) findViewById(R.id.rl_video_control_panel);
        rl_video_control_panel_nospeak = (RelativeLayout) findViewById(R.id.rl_video_control_panel_nospeak);
        rl_control_panel = (RelativeLayout) findViewById(R.id.rl_control_panel);
        ll_video_control_panel_new = (LinearLayout) findViewById(R.id.ll_video_control_panel_new);
        ll_control_panel_new = (LinearLayout) findViewById(R.id.ll_control_panel_new);
        ll_control_yuntai = (LinearLayout) findViewById(R.id.ll_control_yuntai);
        ll_control_forv5 = (LinearLayout) findViewById(R.id.ll_control_forv5);
        rl_control_panel_nospeak = (RelativeLayout) findViewById(R.id.rl_control_panel_nospeak);
        rl_control_landscape = (RelativeLayout) findViewById(R.id.rl_control_landscape);

        include_control_bar = (LinearLayout) findViewById(R.id.include_control_bar);
        ll_play_container = (LinearLayout) findViewById(R.id.ll_play_container);
        ll_contain_fullscreen_btn = (LinearLayout) findViewById(R.id.ll_contain_fullscreen_btn);
        tv_control_fullscreen_bar = (TextView) findViewById(R.id.tv_control_fullscreen_bar);
        btn_control_landscape_to_portrait = (Button) findViewById(R.id.btn_control_landscape_to_portrait);

        tv_video_play_timeorname = (TextView) findViewById(R.id.tv_video_play_timeorname);
        horizontal_sv = (MyHorizontalScrollView) findViewById(R.id.horizontal_sv);

        rl_video = (RelativeLayout) findViewById(R.id.rl_video);
        tv_speed = (TextView) findViewById(R.id.tv_speed);
        tv_speed_landscape = (TextView) findViewById(R.id.tv_speed_landscape);
        ll_linking_video_refresh.setVisibility(View.GONE);
        progress_refresh = (Button) findViewById(R.id.progress_refresh);
        tv_control_definition_bar = (TextView) findViewById(R.id.tv_control_definition_bar);
        btn_control_definition_bar_landscape = (Button) findViewById(R.id.btn_control_definition_bar_landscape);
        divider_silence = findViewById(R.id.divider_silence);
        popDefinitionView = getLayoutInflater().inflate(
                R.layout.control_definition, null);
        tv_control_definition1 = (TextView) popDefinitionView
                .findViewById(R.id.tv_control_definition1);
        tv_control_definition2 = (TextView) popDefinitionView
                .findViewById(R.id.tv_control_definition2);
        tv_control_definition3 = (TextView) popDefinitionView
                .findViewById(R.id.tv_control_definition3);
        popDefinitionWindow = new PopupWindow(popDefinitionView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
        popDefinitionWindow.setAnimationStyle(R.style.bottom_menu_scale);

        // csc相关设置初始化
        cscDialog = new AlertDialog.Builder(this, R.style.alertDialog).create();

        cscDialogView = LinearLayout.inflate(this, R.layout.control_csc,
                (ViewGroup) findViewById(R.id.rl_csc));

        btn_csc_dismiss = (Button) cscDialogView
                .findViewById(R.id.btn_csc_dissmis);
        btn_csc_restore_default = (Button) cscDialogView
                .findViewById(R.id.btn_csc_restore_default);

        sb_csc_luminance = (SeekBar) cscDialogView
                .findViewById(R.id.sb_luminance);
        // sb_csc_contrast = (SeekBar) cscDialogView
        // .findViewById(R.id.sb_contrast);
        // sb_csc_saturability = (SeekBar) cscDialogView
        // .findViewById(R.id.sb_saturability);
        // sb_csc_definition = (SeekBar) cscDialogView
        // .findViewById(R.id.sb_definition);
        tv_test = (TextView) findViewById(R.id.tv_test);

        iv_cap_gallery = (ImageView) findViewById(R.id.iv_cap_gallery);
        iv_cap_gallery_landscape = (ImageView) findViewById(R.id.iv_cap_gallery_landscape);
        iv_cap_effect = (ImageView) findViewById(R.id.iv_cap_effect);
        findViewById(R.id.btn_show_qt)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v)
                    {
                        if (mTipDialog == null) {
                            String result = SipController.getInstance()
                                    .getCallInfos(callId);
                            mTipDialog = DialogUtils.showCommonTipDialog(
                                    PlayVideoActivity.this,
                                    false,
                                    getResources().getString(
                                            R.string.common_prompt),
                                    result, "刷新", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            int i = v.getId();
                                            if (i == R.id.btn_positive) {// //
                                                // .dismiss();

                                                TextView message_tv = (TextView) mTipDialog
                                                        .getWindow()
                                                        .getDecorView()
                                                        .findViewById(
                                                                R.id.tv_info);
                                                message_tv
                                                        .setMovementMethod(new ScrollingMovementMethod());
                                                message_tv.setText(DateFormat
                                                        .format("HH:mm:ss",
                                                                System.currentTimeMillis())
                                                        + "\n"
                                                        + SipController
                                                        .getInstance()
                                                        .getCallInfos(
                                                                callId));


                                            } else {
                                            }
                                        }
                                    });
                        }
                        mTipDialog.show();
                        mTipDialog.setCanceledOnTouchOutside(true);
                    }
                });

    }

    boolean temp = false;
    private TextView tv_test;

    private void initListeners() {
        btn_titlebar_back.setOnClickListener(this);
        iv_control_csc_bar.setOnClickListener(this);
        progress_refresh.setOnClickListener(this);
        btn_snapshot.setOnClickListener(this);
        btn_snapshot_nospeak.setOnClickListener(this);
        btn_snapshot_new.setOnClickListener(this);
        btn_control_snapshot_landscape.setOnClickListener(this);
        btn_talkback.setOnClickListener(this);// 必须有,配合触摸事件一起工作
        btn_speak_new.setOnClickListener(this);// 必须有,配合触摸事件一起工作
        btn_speak_no_yuntai_new.setOnClickListener(this);// 必须有,配合触摸事件一起工作
        btn_control_talkback_landscape.setOnClickListener(this);// 必须有,配合触摸事件一起工作
        tv_control_fullscreen_bar.setOnClickListener(this);
        btn_control_landscape_to_portrait.setOnClickListener(this);
        ll_contain_fullscreen_btn.setOnClickListener(this);
        btn_control_talkback_landscape.setOnTouchListener(this);
        btn_talkback.setOnTouchListener(this);
        btn_speak_new.setOnTouchListener(this);
        btn_speak_no_yuntai_new.setOnTouchListener(this);
        btn_mute.setOnClickListener(this);
        btn_control_silence_landscape.setOnClickListener(this);
        btn_silence_new.setOnClickListener(this);
        tv_control_definition_bar.setOnClickListener(this);
        btn_control_definition_bar_landscape.setOnClickListener(this);
        tv_control_definition1.setOnClickListener(this);
        tv_control_definition2.setOnClickListener(this);
        tv_control_definition3.setOnClickListener(this);

        btn_csc_dismiss.setOnClickListener(this);
        btn_csc_restore_default.setOnClickListener(this);

        btn_album_new.setOnClickListener(this);
        btn_scene_new.setOnClickListener(this);
        // btn_history.setOnClickListener(this);
        btn_video_replay.setOnClickListener(this);
        iv_cap_gallery.setOnClickListener(this);
        iv_cap_gallery_landscape.setOnClickListener(this);

        OnSeekBarChangeListener sbcListener = new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // Utils.sysoInfo("onProgressChanged");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Utils.sysoInfo("onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Utils.sysoInfo("onStopTrackingTouch");
                setCsc();
            }

        };
        // sb_csc_contrast.setOnSeekBarChangeListener(sbcListener);
        // sb_csc_definition.setOnSeekBarChangeListener(sbcListener);
        sb_csc_luminance.setOnSeekBarChangeListener(sbcListener);
        // sb_csc_saturability.setOnSeekBarChangeListener(sbcListener);

        yuntaiBtn.setOnTouchListener(this);
        yuntai_btn_nospeak.setOnTouchListener(this);
        yuntai_btn_nospeak_landscape.setOnTouchListener(this);
        yuntai_left_right_btn_nospeak_landscape.setOnTouchListener(this);
        yuntai_left_right_btn_new.setOnTouchListener(this);
        horizontal_sv.setOnScrollChangedListener(new MyHorizontalScrollView.OnScrollChangedListener() {

            @Override
            public void onScrollChanged(HorizontalScrollView sv, int l, int t,
                                        int oldl, int oldt) {
                updateAngleMeter(sv);
            }

        });
    }

    public void updateAngleMeter(HorizontalScrollView sv) {
        int newDeltaWidth = rl_video.getWidth() - minWidth;
        if (newDeltaWidth <= 0) {
            anglemeter.refreshAngle(0);
        } else {
            anglemeter.refreshAngle(sv.getScrollX() * 1.0 / newDeltaWidth);
        }
    }

    private BroadcastReceiver callStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Log.d("PML", "BroadcastReceiver callStateReceiver");
            if (action.equals(SipManager.GET_ACTION_SIP_CALL_CHANGED())) {// "com.wulian.siplibrary.icam.service.CALL_CHANGED"
                // 启用控制按钮。。。即使挂断也会触发changed方法。。。

                int key_found = intent.getIntExtra("key_found", -1);
                sipCallSession = (SipCallSession) intent
                        .getParcelableExtra("call_info");
                Utils.sysoInfo("key_found=" + key_found + " sp="
                        + sipCallSession);
                if (sipCallSession != null) {
                    int lastCode = sipCallSession.getLastStatusCode();
                    Utils.sysoInfo("sip状态码:" + lastCode);

                    switch (sipCallSession.getCallState()) {
                        case SipCallSession.InvState.INVALID:
                            // titlebar_title.setText(R.string.call_fail);
                            break;
                        case SipCallSession.InvState.CALLING:
                            // titlebar_title.setText(R.string.calling);
                            break;
                        case SipCallSession.InvState.INCOMING:
                        case SipCallSession.InvState.EARLY:
                        case SipCallSession.InvState.CONNECTING:
                            // titlebar_title.setText(R.string.linking);
                            break;
                        case SipCallSession.InvState.CONFIRMED:
                            // titlebar_title.setText(R.string.linked);
                            isConncted = true;
                            ll_linking_video_refresh.setVisibility(View.GONE);
                            Utils.sysoInfo("收到confirmed消息");
                            // mHandler.sendEmptyMessage(SEND_RTP);
                            break;
                        case SipCallSession.InvState.DISCONNECTED:
                            // titlebar_title.setText(R.string.breaked);
                            tv_speed.setText("0KB/s");
                            tv_speed_landscape.setText("0KB/s");
                            tv_speed.setVisibility(View.GONE);
                            tv_speed_landscape.setVisibility(View.GONE);
                            // pd_loading.dismiss();
                            ll_linking_video.setVisibility(View.GONE);
                            if (isPortrait()) {
                                if (disconnectCount == 1 && !isStop) {
                                    // 第一次连接失败，有重连的机会，此时不显示刷新按钮
                                } else
                                    ll_linking_video_refresh
                                            .setVisibility(View.VISIBLE);
                            }
                            mHandler.removeMessages(SEND_RTP);
                            myHandler.removeMessages(SHOWSPEED);
                            // iv_control_play_or_pause
                            // .setBackgroundResource(R.drawable.selector_video_btn_play_new);//
                            // 后面可能无法收到播放消息，所以按钮没切换回来
                            // CustomToast.show(PlayVideoActivity.this, lastCode +
                            // "");
                            isConncted = false;
                            isShowVideo = false;
                            // if (isFirstCall) {// 有一次重呼机会
                            // isFirstCall = false;
                            // myHandler.sendEmptyMessageDelayed(
                            // SPEED_RETRY_FORCE, 1000);
                            // }
                            break;
                    }
                    switch (sipCallSession.getMediaStatus()) {
                        case SipCallSession.MediaState.NONE:
                            Utils.sysoInfo("media none");
                            // 可能遇到的设备返回的状态码提示
                            if (lastCode == 404) {
                                CustomToast.show(PlayVideoActivity.this,
                                        R.string.play_sip_server_not_found);
                            } else if (lastCode == 407) {
                                CustomToast
                                        .show(PlayVideoActivity.this,
                                                R.string.play_sip_proxy_authentication_required);
                            } else if (lastCode == 408 || lastCode == 480) {
                                CustomToast.show(PlayVideoActivity.this,
                                        R.string.play_sip_request_timeout);
                                disconnectCount++;
                                if (disconnectCount == 1 && !isStop) {// 有一次重呼机会
                                    hangUpVideo();
                                    reCallVideo();
                                }
                            } else if (lastCode == 486) {
                                CustomToast.show(PlayVideoActivity.this,
                                        R.string.play_sip_device_busy);
                            } else if (lastCode == 487) {
                                CustomToast.show(PlayVideoActivity.this,
                                        R.string.play_sip_request_terminated);
                            } else if (lastCode > 500) {
                                CustomToast.show(PlayVideoActivity.this,
                                        R.string.play_sip_serve_error);
                            }
                            break;
                        case SipCallSession.MediaState.LOCAL_HOLD:
                            Utils.sysoInfo("media local_hold");
                            break;

                        case SipCallSession.MediaState.REMOTE_HOLD:
                            Utils.sysoInfo("media remote_hold");
                            break;
                        case SipCallSession.MediaState.ACTIVE:// 有时候会延迟到来
                            Utils.sysoInfo("media active");
                            if (!isMediaPlaying)
                                if (sipCallSession.mediaHasVideo()) {
                                    callId = sipCallSession.getCallId();
                                    if (callId != -1) {
                                        SipController.getInstance()
                                                .closeAudioTransport(callId);// 关闭语音通道
                                    }
                                    boolean isWiredHeadsetOn = SipController
                                            .getInstance().isWiredHeadsetOn();
                                    SipController.getInstance().setSpeakerPhone(
                                            !isWiredHeadsetOn);

                                    String natStr = SipController.getInstance()
                                            .getCallNatInfos(callId);
                                    WulianLog.d("PlayVideo", TextUtils
                                            .isEmpty(natStr) ? "natStr null"
                                            : natStr);
                                    if (!TextUtils.isEmpty(natStr)) {
                                        try {
                                            JSONObject json = new JSONObject(natStr);
                                            WulianLog.d("PlayVideo",
                                                    "TextUtils.isEmpty no ");
                                            if (!json.isNull("video_ICEstate")) {
                                                WulianLog.d("PlayVideo",
                                                        "video_ICEstate no ");
                                                int layer = 0;
                                                if (json.getString("video_ICEstate")
                                                        .equals("Negotiation Success")
                                                        || json.getString("video_ICEstate")
                                                        .equals("Candidate Gathering")
                                                        || json.getString("video_ICEstate")
                                                        .equals("Negotiation Failed")) {
                                                    isNegotiationState = NEGOTIATION_SUCCESS;
                                                    isShowVideo = true;
                                                    isMediaPlaying = true;
                                                    isRunOnUI = true;
                                                    Utils.sysoInfo("sipCallSession.mediaHasVideo(),runOnUiThread");
                                                    runOnUiThread(uiUpdate);
                                                    if (myHandler.hasMessages(SPEED_RETRY)) {
                                                        Utils.sysoInfo("检测到NEGOTIATION_SUCCESS,移除重呼消息");
                                                        myHandler.removeMessages(SPEED_RETRY);
                                                    }
                                                    // InputHandler
                                                    // .sendEmptyMessageDelayed(
                                                    // ENABLE, 2000);
                                                    if (!json.getString("video_ICEstate").equals("Negotiation Failed")) {
                                                        WulianLog.d("PlayVideo", "video_ICEstate Negotiation Success ");
                                                        String video_peer = json
                                                                .isNull("video_peer") ? ""
                                                                : json.getString("video_peer");
                                                        String audio_peer = json
                                                                .isNull("audio_peer") ? ""
                                                                : json.getString("audio_peer");
                                                        String video_addr_0_L = json
                                                                .isNull("video_addr_0_L") ? ""
                                                                : json.getString("video_addr_0_L");
                                                        String video_addr_0_R = json
                                                                .isNull("video_addr_0_R") ? ""
                                                                : json.getString("video_addr_0_R");
                                                        if (!TextUtils
                                                                .isEmpty(video_peer)
                                                                && !TextUtils
                                                                .isEmpty(audio_peer)) {
                                                            audio_peer = audio_peer
                                                                    .split(":")[0];
                                                            video_peer = video_peer
                                                                    .split(":")[0];
                                                            video_addr_0_L = video_addr_0_L
                                                                    .split(":")[0];
                                                            video_addr_0_R = video_addr_0_R
                                                                    .split(":")[0];
                                                            WulianLog.d("PlayVideo",
                                                                    "video_addr_0_L:"
                                                                            + video_addr_0_L
                                                                            + ";video_addr_0_R:"
                                                                            + video_addr_0_R
                                                                            + ";audio_peer:"
                                                                            + audio_peer
                                                                            + ";video_peer:"
                                                                            + video_peer);
                                                            if (video_addr_0_L
                                                                    .equals(video_peer)) {
                                                                layer = 3333;
                                                            } else {
                                                                String oneIP = video_addr_0_L
                                                                        .split("\\.")[0];
                                                                if (oneIP
                                                                        .equals("10")
                                                                        || oneIP.equals("172")
                                                                        || oneIP.contains("192")) {
                                                                    layer = 1111;
                                                                } else {
                                                                    layer = 2222;// 包含移动网络
                                                                }
                                                            }

//                                                            sendRequest(
//                                                                    RouteApiType.LOG_P2P,
//                                                                    RouteLibraryParams
//                                                                            .LogP2P(sipCallSession
//                                                                                            .getRemoteContact(),
//                                                                                    1,
//                                                                                    ICamGlobal
//                                                                                            .getInstance()
//                                                                                            .getNatNum(),
//                                                                                    layer),
//                                                                    false, false);
                                                        }
                                                    } else {
//                                                        sendRequest(
//                                                                RouteApiType.LOG_P2P,
//                                                                RouteLibraryParams
//                                                                        .LogP2P(sipCallSession
//                                                                                        .getRemoteContact(),
//                                                                                0,
//                                                                                ICamGlobal
//                                                                                        .getInstance()
//                                                                                        .getNatNum(),
//                                                                                layer),
//                                                                false, false);
                                                    }
                                                } else if (json.getString(
                                                        "video_ICEstate").equals(
                                                        "Negotiation In Progress")) {
                                                    isNegotiationState = NEGOTIATION_INPROGRESS;

                                                    Utils.sysoInfo("检测到 NEGOTIATION_INPROGRESS");
                                                    if (!myHandler
                                                            .hasMessages(SPEED_RETRY)) {
                                                        Utils.sysoInfo("发送延迟10秒重呼");
                                                        myHandler
                                                                .sendEmptyMessageDelayed(
                                                                        SPEED_RETRY,
                                                                        SPEED_RETRY_TIME);
                                                    } else {
                                                        Utils.sysoInfo("10秒重呼已经存在,不再发送");
                                                    }

                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    Utils.sysoInfo("sp有视频流!callId=" + callId);
                                }

                            break;

                        case SipCallSession.MediaState.ERROR:
                            Utils.sysoInfo("media error");
                            break;

                        default:
                            break;
                    }
                } else {
                    if (key_found != -1 && !isRunOnUI) {
                    }
                }
            }
        }
    };

    /**
     * Update the user interface from calls state.
     **/
    private class UpdateUIFromCallRunnable implements Runnable {
        @Override
        public void run() {
            SipController.getInstance().setVideoAndroidRenderer(callId,
                    cameraPreview);
            SipController.getInstance().setEchoCancellation(true);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            if (isControling) {
                Utils.sysoInfo("左上角 退出时 还在控制云台");
                yuntai_stop();

            } else {
                // hangUpVideo();//导致退出截图失败
                this.finish();
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_right_out);
            }
        } else if (id == R.id.tv_control_fullscreen_bar
                || id == R.id.btn_control_landscape_to_portrait
                || id == R.id.ll_contain_fullscreen_btn
                || id == R.id.iv_control_fullscreen) {
            if (isPortrait()) {
                goLandscape();
            } else {
                goPortrait();
            }
        } else if (id == R.id.progress_refresh || id == R.id.iv_control_play) {
            if (isPortrait()) {
                ll_linking_video_refresh.setVisibility(View.GONE);
            }
            if (isShowVideo && isConncted) {// CONFIRMED
                hangUpVideo();
                disconnectCount = 0;
            } else if (!isShowVideo && !isConncted) {// DISCONNECTED
                reCallVideo();
            }
        } else if (id == R.id.iv_control_csc_bar || id == R.id.iv_control_csc) {
            if (cscDialog.isShowing()) {
                cscDialog.dismiss();
            } else {
                cscDialog.show();
                cscDialog.setContentView(cscDialogView);
            }
        } else if (id == R.id.tv_control_definition_bar
                || id == R.id.btn_control_definition_bar_landscape) {
            // case R.id.tv_control_definition:
            if (popDefinitionWindow != null) {
                if (popDefinitionWindow.isShowing()) {
                    popDefinitionWindow.dismiss();
                } else {
                    if (cameraPreview != null) {
                        if (!isPortrait()) {
                            popDefinitionWindow
                                    .showAsDropDown(
                                            btn_control_definition_bar_landscape,
                                            (btn_control_definition_bar_landscape
                                                    .getWidth() - tv_control_definition_bar
                                                    .getWidth()) / 2,
                                            -tv_control_definition_bar
                                                    .getHeight()
                                                    * 3
                                                    - btn_control_definition_bar_landscape
                                                    .getHeight() - 3);
                        } else {
                            popDefinitionWindow
                                    .showAsDropDown(tv_control_definition_bar,
                                            0, -tv_control_definition_bar
                                                    .getHeight() * 4 - 3);
                        }

                    }
                }
            }
        } else if (id == R.id.tv_control_definition1) {
            if (isPortrait()) {
                if (tv_control_definition_bar.getText().equals(
                        tv_control_definition1.getText())) {
                    Utils.sysoInfo("相同清晰度1 return ");
                    popDefinitionWindow.dismiss();
                    return;
                }
            } else {
                if (btn_control_definition_bar_landscape.getText().equals(
                        tv_control_definition1.getText())) {
                    Utils.sysoInfo("相同清晰度1 return ");
                    popDefinitionWindow.dismiss();
                    return;
                }
            }
            sendDpiMessage("320x240");
            // sp.edit()
            // .putString(CONFIG_DEFINITION + "_" + device.getDid(),
            // "320x240").commit();

			/*
			 * if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
			 * SipController.getInstance().sendLocalMessage( deviceCallIp,
			 * SipHandler.ConfigEncode("sip:" + deviceCallIp, seq++, "320x240",
			 * 15, 0), devicePwd, null); } else {
			 * SipController.getInstance().sendMessage( deviceControlUrl,
			 * SipHandler.ConfigEncode(deviceControlUrl, seq++, "320x240", 15,
			 * 0), sipProfile); }
			 */
            tv_control_definition_bar
                    .setText(getString(R.string.play_definition1));
            btn_control_definition_bar_landscape
                    .setText(getString(R.string.play_definition1));
            popDefinitionWindow.dismiss();
        } else if (id == R.id.tv_control_definition2) {
            if (isPortrait()) {
                if (tv_control_definition_bar.getText().equals(
                        tv_control_definition2.getText())) {
                    Utils.sysoInfo("相同清晰度2 return ");
                    popDefinitionWindow.dismiss();
                    return;
                }
            } else {
                if (btn_control_definition_bar_landscape.getText().equals(
                        tv_control_definition2.getText())) {
                    Utils.sysoInfo("相同清晰度2 return ");
                    popDefinitionWindow.dismiss();
                    return;
                }
            }
            sendDpiMessage("640x480");
            // sp.edit()
            // .putString(CONFIG_DEFINITION + "_" + device.getDid(),
            // "640x480").commit();
			/*
			 * if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
			 *
			 * SipController.getInstance().sendLocalMessage( deviceCallIp,
			 * SipHandler.ConfigEncode("sip:" + deviceCallIp, seq++, "640x480",
			 * 15, 0), devicePwd, null); } else {
			 * SipController.getInstance().sendMessage( deviceControlUrl,
			 * SipHandler.ConfigEncode(deviceControlUrl, seq++, "640x480", 15,
			 * 0), sipProfile); }
			 */
            tv_control_definition_bar
                    .setText(getString(R.string.play_definition2));
            btn_control_definition_bar_landscape
                    .setText(getString(R.string.play_definition2));
            popDefinitionWindow.dismiss();
        } else if (id == R.id.tv_control_definition3) {
            if (isPortrait()) {
                if (tv_control_definition_bar.getText().equals(
                        tv_control_definition3.getText())) {
                    Utils.sysoInfo("相同清晰度3 return ");
                    popDefinitionWindow.dismiss();
                    return;
                }
            } else {
                if (btn_control_definition_bar_landscape.getText().equals(
                        tv_control_definition3.getText())) {
                    Utils.sysoInfo("相同清晰度3 return ");
                    popDefinitionWindow.dismiss();
                    return;
                }
            }
            sendDpiMessage("1280x720");
            // sp.edit()
            // .putString(CONFIG_DEFINITION + "_" + device.getDid(),
            // "1280x720").commit();
			/*
			 * if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
			 * SipController.getInstance().sendLocalMessage( deviceCallIp,
			 * SipHandler.ConfigEncode("sip:" + deviceCallIp, seq++, "1280x720",
			 * 15, 0), devicePwd, null); } else {
			 * SipController.getInstance().sendMessage( deviceControlUrl,
			 * SipHandler.ConfigEncode(deviceControlUrl, seq++, "1280x720", 15,
			 * 0), sipProfile); }
			 */
            tv_control_definition_bar
                    .setText(getString(R.string.play_definition3));
            btn_control_definition_bar_landscape
                    .setText(getString(R.string.play_definition3));
            popDefinitionWindow.dismiss();
        } else if (id == R.id.btn_csc_dissmis) {
            cscDialog.dismiss();
        } else if (id == R.id.btn_csc_restore_default) {
            sb_csc_luminance.setProgress(50);
            // sb_csc_contrast.setProgress(50);
            // sb_csc_saturability.setProgress(50);
            // sb_csc_definition.setProgress(50);
            setCsc();
        } else if (id == R.id.btn_snapshot || id == R.id.btn_snapshot_nospeak
                || id == R.id.btn_control_snapshot_landscape
                || id == R.id.btn_snapshot_new) {
            // 判断是否挂载了SD卡
            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                savePath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + APPConfig.ALBUM_DIR;
                File dir = new File(savePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
            // 没有挂载SD卡，无法下载文件
            if (TextUtils.isEmpty(savePath)) {
                mHandler.sendEmptyMessage(ViERenderer.FILE_MOUNT_EXCEPTION);
                return;
            }
            snapSavePath = savePath + device.getDid() + "/";
            ViERenderer.setTakePic(snapSavePath);
            iv_cap_effect.setVisibility(View.VISIBLE);

            AlphaAnimation alphaAnimation1 = new AlphaAnimation(1.0f, 1.0f);
            alphaAnimation1.setDuration(200);
            iv_cap_effect.setAnimation(alphaAnimation1);
            alphaAnimation1.start();
            alphaAnimation1.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // TODO Auto-generated method stub
                    iv_cap_effect.setVisibility(View.GONE);
                }
            });

            if (mediaPlayer != null) {
                try {
                    mediaPlayer.stop();
                    mediaPlayer
                            .setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                    mediaPlayer.prepare();
                    mediaPlayer.setVolume(0.1f, 0.1f);
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (id == R.id.btn_talkback
                || id == R.id.btn_control_talkback_landscape
                || id == R.id.btn_speak_new
                || id == R.id.btn_speak_no_yuntai_new) {
            Utils.sysoInfo("对讲点击了");
        } else if (id == R.id.btn_control_silence_landscape
                || id == R.id.btn_silence || id == R.id.cb_silence) {
            if (!isConncted) {
                Utils.sysoInfo("静音 return ");
                return;
            }
            myHandler.removeMessages(AUTO_SILENCE);// 移除自动静音消息
            isMuteOpen = !isMuteOpen;// 首先就切换为新的状态值，而不是最后才切换。
            if (isMuteOpen) {
                btn_mute.setBackgroundResource(R.drawable.selector_function_silence_off);
                btn_control_silence_landscape
                        .setBackgroundResource(R.drawable.selector_video_btn_landscape_voice_off);
                btn_silence_new
                        .setBackgroundResource(R.drawable.cb_silence_off);
            } else {
                btn_mute.setBackgroundResource(R.drawable.selector_function_silence_on);
                btn_control_silence_landscape
                        .setBackgroundResource(R.drawable.selector_video_btn_landscape_voice_on);
                btn_silence_new.setBackgroundResource(R.drawable.cb_silence_on);
            }
            // setMicrophoneMute这个方法内部逻辑与实际的相反！！！哎。
            SipController.getInstance().setMicrophoneMute(true, callId);
            SipController.getInstance().setSpeakerphoneOn(!isMuteOpen, callId);
            silenceControl(isMuteOpen);
			/*
			 * if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
			 * SipController.getInstance().sendLocalInfo( deviceCallIp,
			 * SipHandler.ConfigVoiceMute(deviceCallIp, seq, isMuteOpen ? "true"
			 * : "false"), callId, devicePwd, null); } else {
			 *
			 * SipController.getInstance().sendInfo( deviceCallUrl,
			 * SipHandler.ConfigVoiceMute(deviceCallUrl, seq, isMuteOpen ?
			 * "true" : "false"), callId, SipFactory.getInstance().registerAccount());// 这里又不相反了，哎。 }
			 */
        } else if (id == R.id.btn_scene_new) {
            // TODO 进行场景切换
//            showSceneDialog();
        }
        // TODO 进入视频回放
        // startActivity(new Intent(PlayVideoActivity.this,
        // ReplayVideoActivity.class).putExtra("device", device));
        // overridePendingTransition(0, 0);
        else if (id == R.id.btn_video_replay) {
            startActivity(new Intent(PlayVideoActivity.this,
                    ReplayVideoActivity.class).putExtra("device", device));
        }

    }

    StringBuilder sb = new StringBuilder();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isLandscape()) {
                goPortrait();
            } else {
                if (isControling) {
                    Utils.sysoInfo("back键按下 退出时 还在控制云台");
                    yuntai_stop();
                } else {
                    // hangUpVideo();//导致退出截图失败
                    return super.onKeyDown(keyCode, event);
                }
            }
            return true;
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                SipController.getInstance().AdjustCurrentVolume();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @Function 竖屏模式
     * @author Wangjj
     * @date 2014年12月4日
     */
    private void goPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void goPortraitDo() {

        ll_play_container.setBackgroundColor(getResources().getColor(
                R.color.background));
        iv_cap_gallery_landscape.setVisibility(View.GONE);
        if (iv_cap_gallery_landscape.getTag() != null && showSnap) {
            iv_cap_gallery.setTag(iv_cap_gallery_landscape.getTag());
            iv_cap_gallery.setVisibility(View.VISIBLE);

//            ImageLoader.getInstance().displayImage(
//                    "file://" + iv_cap_gallery_landscape.getTag().toString(),
//                    iv_cap_gallery);
        }
        tv_speed.setVisibility(View.GONE);
        tv_speed_landscape.setVisibility(View.GONE);
        btn_titlebar_back.setVisibility(View.VISIBLE);
        registerSensorListener();
        anglemeter.setVisibility(View.VISIBLE);
        include_control_bar.setVisibility(View.VISIBLE);
        ll_control_panel_new.setVisibility(View.VISIBLE);
        if (popDefinitionWindow != null && popDefinitionWindow.isShowing()) {
            popDefinitionWindow.dismiss();
        }
		/*
		 * if (hasSpeak) { rl_control_panel.setVisibility(View.VISIBLE); } else
		 * { rl_control_panel_nospeak.setVisibility(View.VISIBLE); }
		 */
        if (!isConncted) {
            // 横屏下，重新连接，显示ll_linking_video，切换到竖屏，不显示ll_linking_video_refresh
            if (ll_linking_video.getVisibility() == View.VISIBLE) {
                ll_linking_video_refresh.setVisibility(View.GONE);
            } else {
                ll_linking_video_refresh.setVisibility(View.VISIBLE);
            }
        }
        if (rl_control_landscape.getVisibility() == View.VISIBLE) {
            rl_control_landscape.setVisibility(View.GONE);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 以宽度 推测 高度
        RelativeLayout.LayoutParams lp = (LayoutParams) cameraPreview
                .getLayoutParams();
        lp.width = maxWidth;
        lp.height = (int) ((float) maxWidth / widthRatio * heightRatio);
        cameraPreview.setLayoutParams(lp);
        is_portrait_fullSize = true;
    }

    public void registerSensorListener() {
        if (!isSensorRegister) {
            sensorManager.registerListener(sensorEventListener,
                    origintationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            isSensorRegister = true;
        }
    }

    public void unRegisterSeneorListener() {
        if (isSensorRegister) {
            sensorManager.unregisterListener(sensorEventListener);
            isSensorRegister = false;
        }
    }

    /**
     * @Function 横屏模式
     * @author Wangjj
     * @date 2014年12月4日
     */
    private void goLandscape() {

        // 进入横屏模式 需要layout_weight
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
    }

    /**
     * @Function 双击视频界面，铺满窗口，仅针对竖屏模式
     * @author Wangjj
     * @date 2015年1月25日
     */

    private void fullSize() {
        if (isPortrait()) {// 仅针对竖屏模式
            if (is_portrait_fullSize) {
                RelativeLayout.LayoutParams lp = (LayoutParams) cameraPreview
                        .getLayoutParams();
                // 以屏幕宽度 推测 高度
                lp.width = Utils.getDeviceSize(this).widthPixels;
                lp.height = (int) ((float) lp.width / widthRatio * heightRatio);
                cameraPreview.setLayoutParams(lp);
                anglemeter.setVisibility(View.GONE);// 全尺寸时不显示角度
                is_portrait_fullSize = false;

            } else {
                RelativeLayout.LayoutParams lp = (LayoutParams) cameraPreview
                        .getLayoutParams();
                lp.width = maxWidth;
                lp.height = (int) ((float) maxWidth / widthRatio * heightRatio);
                cameraPreview.setLayoutParams(lp);
                anglemeter.setVisibility(View.VISIBLE);
                is_portrait_fullSize = true;
                myHandler.sendEmptyMessageDelayed(VIDEO_CENTER, 50);// 需要延迟设置，否则没效果
            }
        }
    }

    int defalutAngle = 30;
    int defalutDistance = 10;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isConncted) {
            return false;
        }
        int id = v.getId();
        if (id == R.id.btn_control_talkback_landscape
                || id == R.id.btn_talkback || id == R.id.btn_speak_new
                || id == R.id.btn_speak_no_yuntai_new) {
            // v.performClick();//move时，导致不断调用
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    btn_talkback.setText(R.string.play_loosen_end);
                    btn_speak_new.setText(R.string.play_loosen_end);
                    btn_speak_no_yuntai_new.setText(R.string.play_loosen_end);
                    if (!isConncted) {
                        Utils.sysoInfo("语音 return ");
                        return false;
                    }
				/*
				 * if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
				 * SipController.getInstance().sendLocalInfo( deviceCallIp,
				 * SipHandler.ConfigVoiceMute(deviceCallIp, seq, "true"),
				 * callId, devicePwd, null); } else {
				 * SipController.getInstance().sendInfo( deviceCallUrl,
				 * SipHandler.ConfigVoiceMute(deviceCallUrl, seq, "true"),
				 * callId, SipFactory.getInstance().registerAccount());// 这里又不相反了，哎。 }
				 */
                    silenceControl(true);
                    isMuteOpen = true;
                    // ui变化 声音off
                    btn_mute.setBackgroundResource(R.drawable.selector_function_silence_off);
                    btn_control_silence_landscape
                            .setBackgroundResource(R.drawable.selector_video_btn_landscape_voice_off);
                    btn_silence_new
                            .setBackgroundResource(R.drawable.cb_silence_off);
                    myHandler.removeMessages(AUTO_SILENCE);
                    SipController.getInstance().setMicrophoneMute(!isMuteOpen,
                            callId);
                    SipController.getInstance().setSpeakerphoneOn(!isMuteOpen,
                            callId);
				/*
				 * if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
				 * SipController.getInstance().sendLocalInfo( deviceCallIp,
				 * SipHandler.ConfigVoiceIntercom(deviceCallIp, seq, "input"),
				 * callId, devicePwd, null); } else {
				 * SipController.getInstance().sendInfo( deviceCallUrl,
				 * SipHandler.ConfigVoiceIntercom(deviceCallUrl, seq, "input"),
				 * callId, SipFactory.getInstance().registerAccount()); }
				 */
                    configVoiceIntercom("input");

                    break;
                case MotionEvent.ACTION_UP:
                    btn_talkback.setText(R.string.play_press_talk);
                    btn_speak_new.setText(R.string.play_press_talk);
                    btn_speak_no_yuntai_new.setText(R.string.play_press_talk);
                    if (!isConncted) {
                        Utils.sysoInfo("语音 return ");
                        return false;
                    }
                    Utils.sysoInfo("松开对讲，结束讲话");
                    isMuteOpen = false;
                    // ui变化 声音on
                    btn_mute.setBackgroundResource(R.drawable.selector_function_silence_on);
                    btn_control_silence_landscape
                            .setBackgroundResource(R.drawable.selector_video_btn_landscape_voice_on);
                    btn_silence_new.setBackgroundResource(R.drawable.cb_silence_on);
                    SipController.getInstance().setMicrophoneMute(!isMuteOpen,
                            callId);
                    SipController.getInstance().setSpeakerphoneOn(!isMuteOpen,
                            callId);

				/*
				 * if (device.getIs_lan() && ICamGlobal.isPureLanModel) {
				 * SipController.getInstance().sendLocalInfo( deviceCallIp,
				 * SipHandler.ConfigVoiceIntercom(deviceCallIp, seq, "output"),
				 * callId, devicePwd, null); } else {
				 * SipController.getInstance().sendInfo( deviceCallUrl,
				 * SipHandler.ConfigVoiceIntercom(deviceCallUrl, seq, "output"),
				 * callId, SipFactory.getInstance().registerAccount()); }
				 */
                    configVoiceIntercom("output");

                    myHandler.sendEmptyMessageDelayed(AUTO_SILENCE,
                            AUTO_SILENCE_TIME * 1000);
                    break;
                default:
                    break;
            }
            return false;
        }
        return false;
    }

    public void yuntai_stop() {
        if (!hasYuntai && !hasYuntaileftRight) {
            return;
        }
        stopMove();
    }

    public void yuntai_left() {
        if (!hasYuntai && !hasYuntaileftRight) {
            return;
        }
        if (isControling) {
            Utils.sysoInfo("按下 span时间已过,但控制中,return");
            return;
        }
        Utils.sysoInfo("按下 control_left");
        isControling = true;

        if (!isVideoInvert) {

            SipController.getInstance().sendInfo(
                    deviceCallUrl,
                    SipHandler.ControlPTZMovement(deviceControlUrl,
                            -APPConfig.MOVE_SPEED, 0), callId, sipProfile);

        } else {

            SipController.getInstance().sendInfo(
                    deviceCallUrl,
                    SipHandler.ControlPTZMovement(deviceControlUrl,
                            APPConfig.MOVE_SPEED, 0), callId, sipProfile);

        }
    }

    public void yuntai_down() {
        if (!hasYuntai) {
            return;
        }
        if (isControling) {
            Utils.sysoInfo("按下 span时间已过,但控制中,return");
            return;
        }
        Utils.sysoInfo("按下 control_down");
        isControling = true;
        if (!isVideoInvert) {


            SipController.getInstance().sendInfo(
                    deviceCallUrl,
                    SipHandler.ControlPTZMovement(deviceControlUrl, 0,
                            -APPConfig.MOVE_SPEED), callId, sipProfile);

        } else {


            SipController.getInstance().sendInfo(
                    deviceCallUrl,
                    SipHandler.ControlPTZMovement(deviceControlUrl, 0,
                            APPConfig.MOVE_SPEED), callId, sipProfile);

        }
    }

    public void yuntai_right() {
        if (!hasYuntai && !hasYuntaileftRight) {
            return;
        }
        if (isControling) {
            Utils.sysoInfo("按下 span时间已过,但控制中,return");
            return;
        }
        Utils.sysoInfo("按下 control_right");
        isControling = true;
        if (!isVideoInvert) {


            SipController.getInstance().sendInfo(
                    deviceCallUrl,
                    SipHandler.ControlPTZMovement(deviceControlUrl,
                            APPConfig.MOVE_SPEED, 0), callId, sipProfile);
        } else {

            SipController.getInstance().sendInfo(
                    deviceCallUrl,
                    SipHandler.ControlPTZMovement(deviceControlUrl,
                            -APPConfig.MOVE_SPEED, 0), callId, sipProfile);

        }
    }

    public void yuntai_up() {
        if (!hasYuntai) {
            return;
        }
        if (isControling) {
            Utils.sysoInfo("按下 span时间已过,但控制中,return");
            return;
        }
        Utils.sysoInfo("按下 control_up");
        isControling = true;
        if (!isVideoInvert) {

            SipController.getInstance().sendInfo(
                    deviceCallUrl,
                    SipHandler.ControlPTZMovement(deviceControlUrl, 0,
                            APPConfig.MOVE_SPEED), callId, sipProfile);

        } else {

            SipController.getInstance().sendInfo(
                    deviceCallUrl,
                    SipHandler.ControlPTZMovement(deviceControlUrl, 0,
                            -APPConfig.MOVE_SPEED), callId, sipProfile);

        }
    }

    private void stopMove() {
        if (isControling) {
            Utils.sysoInfo("抬起 stopMove,stop control");
            isControling = false;

            SipController.getInstance().sendInfo(deviceCallUrl,
                    SipHandler.ControlPTZMovement(deviceControlUrl, 0, 0),
                    callId, sipProfile);

        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            landScapeDo();
        } else if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT) {
            goPortraitDo();
        }
    }

    void landScapeDo(){
        ll_play_container.setBackgroundColor(getResources().getColor(
                R.color.black));
        iv_cap_gallery.setVisibility(View.GONE);
        if (iv_cap_gallery.getTag() != null && showSnap) {
            iv_cap_gallery_landscape.setTag(iv_cap_gallery.getTag());
            iv_cap_gallery_landscape.setVisibility(View.VISIBLE);
        }
        tv_speed.setVisibility(View.GONE);
        tv_speed_landscape.setVisibility(View.GONE);
        btn_titlebar_back.setVisibility(View.INVISIBLE);
        ll_linking_video_refresh.setVisibility(View.GONE);
        ll_control_panel_new.setVisibility(View.GONE);
        unRegisterSeneorListener();
        include_control_bar.setVisibility(View.GONE);
        if (hasSpeak) {
            rl_control_panel.setVisibility(View.GONE);
        } else {
            rl_control_panel_nospeak.setVisibility(View.GONE);
        }
        if (!isConncted) {
            if (ll_linking_video.getVisibility() == View.VISIBLE) {
                ll_linking_video_refresh.setVisibility(View.GONE);
            } else {
                ll_linking_video_refresh.setVisibility(View.VISIBLE);
            }
        }
        if (popDefinitionWindow != null && popDefinitionWindow.isShowing()) {
            popDefinitionWindow.dismiss();
        }
        anglemeter.setVisibility(View.GONE);


        RelativeLayout.LayoutParams lp = (LayoutParams) cameraPreview
                .getLayoutParams();
        // 以屏幕宽度 推测 高度
        lp.width = Utils.getDeviceSize(this).widthPixels;
        lp.height = (int) ((float) lp.width / widthRatio * heightRatio);

        Utils.sysoInfo(lp.width + "--" + lp.height + " 布局容器:"
                + ll_play_container.getWidth() + "--"
                + ll_play_container.getHeight());
        cameraPreview.setLayoutParams(lp);
    }

    public boolean isLandscape() {
        return this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public boolean isPortrait() {
        return this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    HeadsetPlugReceiver headsetPlugReceiver;

    private void registerHeadsetPlugReceiver() {
        headsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetPlugReceiver, intentFilter);
    }

    class HeadsetPlugReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) {
                    WulianLog.d("PML", "HeadsetPlugReceiver is 0");
                    SipController.getInstance().setSpeakerPhone(true);
                } else if (intent.getIntExtra("state", 0) == 1) {
                    WulianLog.d("PML", "HeadsetPlugReceiver is 1");
                    SipController.getInstance().setSpeakerPhone(false);
                } else {
                    WulianLog.d(
                            "PML",
                            "HeadsetPlugReceiver is XXX:"
                                    + intent.getIntExtra("state", 0));
                }
            }
        }
    }

    AlertDialog dialogPwdName;
    Dialog pwdDialog;
    View dialogPwdView;

    /*
     * public void showVideoPwd() { if (dialogPwdName == null) { dialogPwdName =
     * new AlertDialog.Builder(this, R.style.alertDialog) .create(); }
     *
     * if (dialogPwdView == null) { dialogPwdView = LinearLayout.inflate(this,
     * R.layout.custom_alertdialog_lan_pwd, (ViewGroup)
     * findViewById(R.id.ll_custom_alertdialog)); final EditText et_input =
     * (EditText) dialogPwdView .findViewById(R.id.et_input);
     * et_input.setFilters(new InputFilter[] { new InputFilter.LengthFilter( 20)
     * }); ((Button) dialogPwdView.findViewById(R.id.btn_positive))
     * .setOnClickListener(new View.OnClickListener() {
     *
     * @Override public void onClick(View v) { String pwd =
     * et_input.getText().toString().trim(); if (!TextUtils.isEmpty(pwd)) {
     * spLan.edit() .putString( device.getDid() + APPConfig.LAN_VIDEO_PWD,
     * pwd).commit(); reCallVideo(); dialogPwdName.dismiss(); } else {
     * Utils.shake(PlayVideoActivity.this, et_input); } } }); ((Button)
     * dialogPwdView.findViewById(R.id.btn_negative)) .setOnClickListener(new
     * View.OnClickListener() {
     *
     * @Override public void onClick(View v) { dialogPwdName.dismiss(); } }); }
     * dialogPwdName.show(); dialogPwdName.getWindow().clearFlags(
     * WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
     * WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
     * dialogPwdName.getWindow().setSoftInputMode(
     * WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
     *
     * dialogPwdName.setContentView(dialogPwdView);
     *
     * }
     */
    // FIXME 局域网查看，密码是通过本地存储sp判断，不是从sip服务器端获取的，多方操作会导致不同步
    private void showVideoPwd() {
        Resources rs = getResources();
        pwdDialog = DialogUtils.showCommonEditDialog(this, false,
                rs.getString(R.string.lan_video_pwd), null, null,
                rs.getString(R.string.lan_video_pwd_input), "",
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.et_input) {
                            EditText infoEt = (EditText) v;
                            infoEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                                    20)});
                            String lan_pwd = infoEt.getText().toString().trim();
                            if (!TextUtils.isEmpty(lan_pwd)) {
                                spLan.edit()
                                        .putString(
                                                device.getDid()
                                                        + APPConfig.LAN_VIDEO_PWD,
                                                lan_pwd).commit();
                                reCallVideo();
                                pwdDialog.dismiss();
                            } else {
                                Utils.shake(PlayVideoActivity.this, infoEt);
                            }
                        } else if (id == R.id.btn_negative) {
                            pwdDialog.dismiss();
                        }
                    }
                });
    }

    private void setCsc() {
        sb.delete(0, sb.length());
        sb.append(sb_csc_luminance.getProgress()).append(",").append("50")
                .append(",").append("50").append(",").append("50");

        SipController.getInstance()
                .sendMessage(
                        deviceControlUrl,
                        SipHandler.ConfigCSC(deviceControlUrl, seq++,
                                sb.toString()), sipProfile);
    }

    private void silenceControl(boolean flag) {

        SipController.getInstance().sendInfo(
                deviceCallUrl,
                SipHandler.ConfigVoiceMute(deviceCallUrl, seq,
                        flag ? "true" : "false"), callId,
                sipProfile);// 这里又不相反了，哎。
    }

    private void configVoiceIntercom(String function) {

        SipController.getInstance().sendInfo(
                deviceCallUrl,
                SipHandler.ConfigVoiceIntercom(deviceCallUrl, seq, function),
                callId, SipFactory.getInstance().getSipProfile());
    }

    private void sendDpiMessage(String dpi) {

        SipController.getInstance().sendMessage(
                deviceControlUrl,
                SipHandler.ConfigEncode(deviceControlUrl, seq++, dpi, 15, 0),
                sipProfile);
    }

    private void initDefinition() {
        tv_control_definition_bar.setText(R.string.play_definition2);
        btn_control_definition_bar_landscape.setText(R.string.play_definition2);
    }
}