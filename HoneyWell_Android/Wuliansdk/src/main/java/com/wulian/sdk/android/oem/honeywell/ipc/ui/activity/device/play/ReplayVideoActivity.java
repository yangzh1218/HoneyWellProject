package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.play;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.oss.model.FederationToken;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.APPConfig;
import com.wulian.sdk.android.oem.honeywell.ipc.CallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.ErrorCode;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.SDKSipCallBack;
import com.wulian.sdk.android.oem.honeywell.ipc.common.Common;
import com.wulian.sdk.android.oem.honeywell.ipc.receiver.MessageCallStateReceiver;
import com.wulian.sdk.android.oem.honeywell.ipc.OSSFactory;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.UserDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.BaseFragmentActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.H264CustomeView;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.DateTimeUtils;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.JsonUtil;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.MessageUtil;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.manage.SipManager;
import com.wulian.siplibrary.manage.SipProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ReplayVideoActivity extends BaseFragmentActivity implements CallBack, OSSFactory.CallBack {

    private final static int SHOW_VIDEO_REPLAY_DATE_MSG = 5;// 展示视频当前文字信息
    private final static int VIDEO_REPLAY_COMEING_MSG = 6;// 展示视频当前文字信息
    private SimpleDateFormat mDateAllFormat;
    private SimpleDateFormat mDateYMDFormat;
    private SimpleDateFormat mDateSimpleFormat;

    private RelativeLayout rl_replay_player;
    private LinearLayout ll_titlebar_back;// 返回
    private RelativeLayout rl_video_top_landscape;// 横屏时Top布局
    private RelativeLayout rl_control_landscape;// 横屏时布局
    private LinearLayout ll_control_portrait;// 竖屏时布局
    private RelativeLayout rl_seekbar_layout;// 进度布局
    private RelativeLayout rl_progress_replay_video;// 进度条提示布局

    private TextView tv_play_date;// 播放时间
    private TextView tv_seekbar_date;// 进度条日期
    private TextView tv_progress_video_tip;// 进度条视频提示

    private ImageView iv_progress_video;
    OSSFactory ossFactory;
    String bucketName;
    String region;
    private Button btn_control_snapshot_landscape;// 横屏截图
    private Button btn_control_snapshot_portrait;// 竖屏时截图
    private Button btn_control_back_live_portrait;// 竖屏时返回直播
    private LinearLayout ll_control_fullscreen_bar;// 全屏

    private CheckBox cb_control_record_landscape;// 横屏录制
    private CheckBox cb_control_record_portrait;// 竖屏时录制

    private GestureDetector mGestureDetector;
    private Animation mUpDownAnim;
    private MediaPlayer mMediaPlayer;

    private ImageView iv_titlebar_back;
    private int mMaxWidth;
    private boolean mIsVideoInvert = false;
    private boolean mIsPortrait = true;
    private boolean mIsRecording = false;
    private boolean mIsFirstRequestRecord = true;
    private long mCurrentTimeStamp;// 当前Date实时显示时间戳
    private long mPlayProgressTimeStamp;// 当前选择播放进度显示时间戳
    private Date mDate;
    private List<Pair<Integer, Integer>> mRecordList;

    private int recordTime = 0;
    private SipProfile mAccount;
    private boolean mQueryHistory = true;
    private boolean mHasQueryData = false;

    private final static int STREAM_HANDLE_MSG = 1000;// 视频流过来处理信息
    private WakeLock mVideoWakeLock;
    private PowerManager mPowerManager;
    private H264CustomeView view_h264video;// 播放View
    private REPLAY_VIDEO_STATUS mReplayVideoStatus;
    String deviceId;
    int nAlarmId;
    long startTime;
    long endTime;
    boolean havePlayed;

    public com.wulian.sdk.android.oem.honeywell.ipc.receiver.MessageCallStateReceiver messageCallStateReceiver;

    private enum REPLAY_VIDEO_STATUS {
        INIT, SEEKBAR_PROGRESS, GET_VIDEO, PLAY_VIDEO, DESTROY
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewContent();
        initData();

    }

    @Override
    public void getObjectResultOK(long timestamp, boolean mIsFirstRequestRecord) {
        this.mIsFirstRequestRecord = mIsFirstRequestRecord;
        handler.sendEmptyMessage(VIDEO_REPLAY_COMEING_MSG);
    }

    @Override
    public void playVideo(byte[] data, int width, int height) {
        Log.d("wulian.icam", Common.getLogHead(this.getClass()) + "playVideo");
        view_h264video.PlayVideo(data, width, height);
        havePlayed = true;
    }

    @Override
    public void stopVideo() {
        handler.removeCallbacksAndMessages(null);
        handler.removeMessages(SHOW_VIDEO_REPLAY_DATE_MSG);
        if (ossFactory != null) {
            ossFactory.controlStopRecord();
            ossFactory.destroyOSS();
            ossFactory.removeMessages();
            ossFactory = null;
            System.gc();
        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void showTopDate(long time) {
        String tt = mDateAllFormat.format(new Date(time * 1000));
        tv_play_date.setText(tt);
    }

    private void initData() {
        mDateAllFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        mDateSimpleFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
        mDateYMDFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        mMediaPlayer = MediaPlayer.create(this, R.raw.snapshot);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        int upDownDis = Utils.dip2px(getBaseContext(), 10);
        mUpDownAnim = new TranslateAnimation(0, 0, -upDownDis, upDownDis);
        mUpDownAnim.setRepeatCount(Animation.INFINITE);
        mUpDownAnim.setDuration(getResources().getInteger(
                android.R.integer.config_longAnimTime));
        mUpDownAnim.setRepeatMode(Animation.REVERSE);
        iv_progress_video.startAnimation(mUpDownAnim);
//        if (mPowerManager != null) {
//            mVideoWakeLock = mPowerManager.newWakeLock(
//                    PowerManager.FULL_WAKE_LOCK
//                            | PowerManager.ACQUIRE_CAUSES_WAKEUP,
//                    "com.wulian.Replay");
//        }
    }

    protected void setViewContent() {
        setContentView(R.layout.activity_replay_video);
        initViews();
        iv_titlebar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Interface.getInstance().detachContext();
                ReplayVideoActivity.this.finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        messageCallStateReceiver = new MessageCallStateReceiver();
        registerReceiver(messageCallStateReceiver, new IntentFilter(
                SipManager.GET_ACTION_SIP_MESSAGE_RECEIVED()));

        Interface.getInstance().setCallBack(this);
        Interface.getInstance().setContext(this);
        deviceId = getIntent().getExtras().getString("deviceId");
        nAlarmId = getIntent().getExtras().getInt("nAlarmId");
        String sStartTime = getIntent().getExtras().getString("sStartTime");
        String sEndTime = getIntent().getExtras().getString("sEndTime");
//        Interface.getInstance().GetAlarmVideoInfo(deviceId, nAlarmId);

        startTime = Long.valueOf(sStartTime);
        startTime = (Long.valueOf(startTime / 60)) * 60;
        endTime = Long.valueOf(sEndTime);
        endTime = (Long.valueOf(endTime / 60)) * 60;
        Log.d("wulian.icam", Common.getLogHead(this.getClass()) + "DoSucceed--sStartTime:" + startTime);
        try {
            mCurrentTimeStamp = startTime;
            ossFactory = new OSSFactory(ReplayVideoActivity.this, deviceId,nAlarmId, startTime, endTime);
            ossFactory.setCallBack(ReplayVideoActivity.this);
            Interface.getInstance().setSipCallBack(ossFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Interface.getInstance().TokenDownloadReplay(deviceId, UserDataRepository.getInstance().userInfo().getSdomain());

        havePlayed = false;
        timeoutDo();
    }

    void timeoutDo() {
        handler.removeMessages(3);
        handler.sendMessageDelayed(MessageUtil.set(3, "", ""),10000);
    }

    boolean haveDownload = false;

    protected void onStop() {
        Log.d("wulian.icam", Common.getLogHead(this.getClass()) + "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopVideo();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }

        if (mUpDownAnim != null && mUpDownAnim.hasStarted()) {
            mUpDownAnim.cancel();
        }

        if (view_h264video != null && view_h264video.getBitmap() != null) {
            Utils.saveBitmap(deviceId + "-alarm", view_h264video.getBitmap(),
                    ReplayVideoActivity.this);
        }
        unregisterReceiver(messageCallStateReceiver);
    }

    protected void onDestroy() {
        super.onDestroy();

//        if (mVideoWakeLock != null) {
//            mVideoWakeLock.release();
//        }
    }

    private void initViews() {
        rl_progress_replay_video = (RelativeLayout) findViewById(R.id.rl_progress_replay_video);
        rl_video_top_landscape = (RelativeLayout) findViewById(R.id.rl_video_top_landscape);
        rl_replay_player = (RelativeLayout) findViewById(R.id.rl_replay_player);
        rl_control_landscape = (RelativeLayout) findViewById(R.id.rl_control_landscape);
        ll_control_portrait = (LinearLayout) findViewById(R.id.ll_control_portrait);
        view_h264video = (H264CustomeView) findViewById(R.id.view_h264video);
        ll_titlebar_back = (LinearLayout) findViewById(R.id.ll_titlebar_back);
        tv_play_date = (TextView) findViewById(R.id.tv_play_date);
        tv_progress_video_tip = (TextView) findViewById(R.id.tv_progress_video_tip);
        iv_progress_video = (ImageView) findViewById(R.id.iv_progress_video);
        cb_control_record_landscape = (CheckBox) findViewById(R.id.cb_control_record_landscape);
        btn_control_snapshot_landscape = (Button) findViewById(R.id.btn_control_snapshot_landscape);
        btn_control_snapshot_portrait = (Button) findViewById(R.id.btn_control_snapshot_portrait);
        btn_control_back_live_portrait = (Button) findViewById(R.id.btn_control_back_live_portrait);
        ll_control_fullscreen_bar = (LinearLayout) findViewById(R.id.ll_control_fullscreen_bar);
        cb_control_record_portrait = (CheckBox) findViewById(R.id.cb_control_record_portrait);
        iv_titlebar_back = (ImageView) findViewById(R.id.iv_titlebar_back);
        btn_control_back_live_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Interface.getInstance().detachContext();
                ReplayVideoActivity.this.finish();
            }
        });

        ll_control_fullscreen_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPortrait()) {
                    goLandscape();
                } else {
                    goPortrait();
                }
            }
        });
    }

    public boolean isPortrait() {
        return this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    private void goLandscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void landscapeDo() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏

        ll_titlebar_back.setVisibility(View.GONE);
        rl_video_top_landscape.setVisibility(View.GONE);
        rl_control_landscape.setVisibility(View.GONE);

        mIsPortrait = false;
        ll_control_portrait.setVisibility(View.GONE);

        LinearLayout.LayoutParams replay_lp = new LinearLayout.LayoutParams(
                rl_replay_player.getLayoutParams());
        replay_lp.height = LinearLayout.LayoutParams.MATCH_PARENT;
        rl_replay_player.setLayoutParams(replay_lp);
    }

    private void goPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void portraitDo() {
        rl_video_top_landscape.setVisibility(View.VISIBLE);

        ll_titlebar_back.setVisibility(View.VISIBLE);

        rl_control_landscape.setVisibility(View.GONE);
        mIsPortrait = true;
        ll_control_portrait.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams replay_lp = new LinearLayout.LayoutParams(
                rl_replay_player.getLayoutParams());
        WindowManager wm = this.getWindowManager();
        int height = (int) (wm.getDefaultDisplay().getWidth() * APPConfig.DEFAULT_WIDTH_HEIGHT_RATIO);
        replay_lp.height = height;
        rl_replay_player.setLayoutParams(replay_lp);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            landscapeDo();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            portraitDo();
        }
    }

    @Override
    public void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String jsonData) {
        LibraryLoger.d("The download_replay is:"+jsonData);
        switch (apiType) {
            case V3_TOKEN_DOWNLOAD_REPLAY:
                try {
                    JSONObject json = new JSONObject(jsonData);
                    json = json.getJSONObject("data");
                    stsToken = new FederationToken();
                    stsToken.setRequestId(json.getString("RequestId"));
                    stsToken.setAccessKeySecret(json.getString("AccessKeySecret"));
                    stsToken.setAccessKeyId(json.getString("AccessKeyId"));
                    stsToken.setSecurityToken(json.getString("SecurityToken"));
                    stsToken.setExpiration(60 * 60 * 2);
                    bucketName = json.getString("Bucket");
                    region=json.getString("Region");
//                    bucketName = "wulian-icam-cn";//json.getString("Bucket");//
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {

                }
                break;
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_VIDEO_REPLAY_DATE_MSG:
                    mCurrentTimeStamp++;
                    showTopDate(mCurrentTimeStamp);
                    Log.d("wulian.icam", Common.getLogHead(ReplayVideoActivity.this.getClass()) + "SHOW_VIDEO_REPLAY_DATE_MSG");
                    sendMessageDelayed(handler.obtainMessage(SHOW_VIDEO_REPLAY_DATE_MSG), 1000);
                    break;
                case VIDEO_REPLAY_COMEING_MSG:
                    if (mIsFirstRequestRecord) {
                        rl_progress_replay_video.setVisibility(View.GONE);
                        handler.sendEmptyMessage(SHOW_VIDEO_REPLAY_DATE_MSG);
                    }
                    break;
                case 0:
                    ossFactory.setFederationToken(stsToken, region, bucketName);
                    ossFactory.connectOSS();
                    ossFactory.controlStartRecord();
                    Log.d("wulian.icam", "connectOSS, controlStartRecord commit");
                    break;
                case 1:
                    CustomToast.show(ReplayVideoActivity.this, msg.getData().getString("toast"));
                    break;
                case 3:
                    handler.removeMessages(3);
                    if (!havePlayed) {
                        tv_progress_video_tip.setText(R.string.click_retrieve);
                        iv_progress_video.setImageResource(R.drawable.video_control_play_pressed_new);
                        iv_progress_video.clearAnimation();
                        rl_progress_replay_video.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                haveDownload = false;
                                iv_progress_video.startAnimation(mUpDownAnim);
                                iv_progress_video.setImageResource(R.drawable.icon_replay_progress);
                                Interface.getInstance().TokenDownloadReplay(deviceId, UserDataRepository.getInstance().userInfo().getSdomain());
                                Log.d("wulian.icam", deviceId);
                                ossFactory.setIsFirstVideo(true);
                                tv_progress_video_tip.setText(R.string.replay_fetching_videos);
                                havePlayed = false;
                                timeoutDo();
                            }

                        });
                    }
                    break;
                default:
                    break;
            }
        }
    };

    FederationToken stsToken;


    @Override
    public void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception) {
        handler.sendMessage(MessageUtil.set(1, "toast", exception.toString()));
    }


    static {
        try {
            System.loadLibrary("openh264");
            System.loadLibrary("WulianICamOpenH264");
        } catch (Exception e) {

        }
    }
}
