package com.wulian.sdk.android.oem.honeywell.ipc;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.wulian.oss.Utils.OSSXMLHandler;
import com.wulian.oss.callback.ConnectDataCallBack;
import com.wulian.oss.model.FederationToken;
import com.wulian.oss.model.GetObjectDataModel;
import com.wulian.oss.service.WulianOssClient;
import com.wulian.routelibrary.utils.LibraryPhoneStateUtil;
import com.wulian.sdk.android.oem.honeywell.ipc.common.Common;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.UserDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.sip.SipFactory;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.widget.H264CustomeView;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.MessageUtil;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.XMLHandler;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;

/**
 * 作者：Administrator on 2016/8/8 20:16
 * 邮箱：huihui@wuliangroup.com
 */
public class OSSFactory extends Object implements SipCallBack {

    private WulianOssClient mClient;
    private Context context;

    private CallBack callBack;
    private boolean mIsFirstRequestRecord = true;
    private final static int SHOW_VIDEO_REPLAY_DATE_MSG = 5;// 展示视频当前文字信息
    private final static int PLAY_VIDEO_MSG = 9;// 直接播放的信息
    private final static int REMOVE_PLAY_VIDEO_MSG = 100;
    private final static int HIDE_PLAY_PROGRESS_MSG = 101;
    private final static int NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG = 200;
    private final static int REQUEST_NEXT_OBJECT_MSG = 7;// 40秒请求下次时间的信息
    private final static int STREAM_HANDLE_MSG = 1000;// 视频流过来处理信息
    private long mCurrentTimeStamp;// 当前Date实时显示时间戳
    private long mNextTimeStamp;
    GetObjectDataModel getObjectDataModel;
    int nAlarmId;
    private boolean firstVideo = true;
    private int mSeq = 1;
    private String mDeviceControlUrl = null;
    private String mSessionID = "";
    private H264CustomeView view_h264video;// 播放View
    int mAlarmID;
    String deviceId;
    long endTime;

    public OSSFactory(Context context, String deviceId,int alarmID, long startTime, long endTime) {
        this.context = context;
        initOSS();
        this.deviceId = deviceId;
        this.mAlarmID=alarmID;
        mCurrentTimeStamp = startTime;
        this.endTime = endTime;
        mDeviceControlUrl = deviceId + "@" + UserDataRepository.getInstance().sdomain();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    private void initOSS() {
        // 初始化OSS
        Log.d("wulian.icam", Common.getLogHead(this.getClass()) + "initOSS");
        mClient = new WulianOssClient(mListener, context);
        mClient.initConfigData();
        mClient.enableLog();
    }

    public void destroyOSS() {
        if (mClient != null) {
            mClient.disconnect();
            mClient = null;
            System.gc();
        }
    }

    public void connectOSS() {
        // 连接
        mClient.connect();
    }


    ConnectDataCallBack mListener = new ConnectDataCallBack() {

        @Override
        public void onH264StreamMessage(byte[] data, int width, int height) {
            if (mPlayVideoHandler != null) {
                Bundle bd = new Bundle();
                bd.putByteArray("data", data);
                bd.putInt("width", width);
                bd.putInt("height", height);
                Message msg = new Message();
                msg.setData(bd);
                if (data == null) {
                    msg.what = REMOVE_PLAY_VIDEO_MSG;
                } else {
                    msg.what = STREAM_HANDLE_MSG;
                }
                mPlayVideoHandler.sendMessage(msg);
            }
        }

        public void onRequestObjectEndFlag() {

        }

        @Override
        public void onError(Exception error) {
        }

        @Override
        public void onDisconnect(int code, String reason) {
        }

        @Override
        public void onRequestGetObjectResultOK(long timestamp) {
            mCurrentTimeStamp = timestamp;
            if (callBack == null) {
                CustomToast.show(context, "play Callback not be set");
            }
            callBack.getObjectResultOK(mCurrentTimeStamp, mIsFirstRequestRecord);
            if (mIsFirstRequestRecord) {
                mIsFirstRequestRecord = false;
            }
            mCurrentTimeStamp = timestamp;
            mPlayVideoHandler.sendMessageDelayed(
                    mPlayVideoHandler.obtainMessage(REQUEST_NEXT_OBJECT_MSG),
                    40000);
            Log.d("wulian.icam", "onRequestGetObjectResultOK" + timestamp);
            mPlayVideoHandler.sendMessageDelayed(
                    mPlayVideoHandler.obtainMessage(PLAY_VIDEO_MSG), 60000);
        }
    };

    private Handler mPlayVideoHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STREAM_HANDLE_MSG:
                    Bundle bd = msg.getData();
                    byte[] data = bd.getByteArray("data");
                    int width = bd.getInt("width");
                    int height = bd.getInt("height");
                    callBack.playVideo(data, width, height);
                    Log.d("wulian.icam", "STREAM_HANDLE_MSG");
                    break;
                case REQUEST_NEXT_OBJECT_MSG:
                    mNextTimeStamp = getNextProgressTimeStamp(mCurrentTimeStamp);
                    if (mNextTimeStamp <= endTime) {
                        Interface.getInstance().controlAlarmHistoryRecordProgress(mNextTimeStamp, mSessionID,60,true,mAlarmID, mDeviceControlUrl);
//                        Interface.getInstance().controlHistoryRecordProgress(mNextTimeStamp, mSessionID, mDeviceControlUrl);
                    }
                    break;
                case REMOVE_PLAY_VIDEO_MSG:
                    mPlayVideoHandler.removeMessages(SHOW_VIDEO_REPLAY_DATE_MSG);
                    break;
                case PLAY_VIDEO_MSG:
                    if (mNextTimeStamp > endTime) {
                        callBack.stopVideo();
                        mPlayVideoHandler.removeCallbacksAndMessages(null);
//                        mPlayVideoHandler.removeMessages(PLAY_VIDEO_MSG);
                    } else {
                        if(mClient != null) {
                            mClient.playOSSObjectName(getObjectDataModel,
                                    mIsFirstRequestRecord);
                        } else {
//                            CustomToast.show(context, "mClient is null");
                            Log.d("wulian.icam", "mClient is null");
                        }
                    }
                    break;
                case 1:
                    CustomToast.show(context, msg.getData().getString("toast"));
                    break;
                default:
                    break;
            }
        }
    };


    private long getNextProgressTimeStamp(long initTime) {
        return initTime + 60;
    }

    public void playOSSObjectName(GetObjectDataModel getObjectDataModel) {
        this.getObjectDataModel = getObjectDataModel;
        mPlayVideoHandler.sendMessage(mPlayVideoHandler
                .obtainMessage(PLAY_VIDEO_MSG));
    }

    public void setFederationToken(FederationToken stsToken, String region, String bucketName) {
        mClient.setFederationToken(stsToken, region, bucketName);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        destroyOSS();
    }

    public interface CallBack {
        void getObjectResultOK(long timestamp, boolean isfirst);

        void playVideo(byte[] data, int width, int height);

        void stopVideo();
    }

    public void setIsFirstVideo(boolean firstVideo){
        this.firstVideo = firstVideo;
    }

    @Override
    public void SipDataReturn(boolean isSuccess, SipMsgApiType apiType, String xmlData, String from, String to) {
//        Log.d("wulian.icam", Common.getLogHead(this.getClass()) + apiType + xmlData);
        if (isSuccess) {
            switch (apiType) {
                case CONTROL_START_RECORD:
                    try {
                        String status = XMLHandler.parseXMLDataGetStatus(xmlData);
                        String session = XMLHandler
                                .parseXMLDataGetSessionID(xmlData);
                        mSessionID = session;
                        if (status == null || session == null) {
                            CustomToast.show(context, R.string.main_process_failed);
                        } else {
                            if (status.equalsIgnoreCase("OK")) {
                                connectOSS();
                                Interface.getInstance().controlAlarmHistoryRecordProgress(mCurrentTimeStamp, mSessionID,60,true,mAlarmID, mDeviceControlUrl);
//                                Interface.getInstance().controlHistoryRecordProgress(mCurrentTimeStamp, mSessionID, mDeviceControlUrl);
                            } else if (status.equalsIgnoreCase("404")) {
                                CustomToast.show(context, R.string.common_no_sdcard);
                            } else if (status.equalsIgnoreCase("-1")
                                    || status.equalsIgnoreCase("500")) {
                                CustomToast.show(context, R.string.main_process_failed);
                            } else if (status.equalsIgnoreCase("551")) {
//                                controlStopRecord();
//                                mPlayVideoHandler.sendMessage(MessageUtil.set(1, "toast", "只允许一人观看回看"));
                            } else {
                            }
                        }
                    } catch (Exception e) {
                    }
                    break;
                case CONTROL_ALARM_HISTORY_RECORD_PROGRESS:
                case CONTROL_HISTORY_RECORD_PROGRESS:
                    String fileName = XMLHandler.parseXMLDataGetFilename(xmlData);
                    if (!fileName.equalsIgnoreCase("OK")) {
                        if (fileName.equalsIgnoreCase("404")
                                || fileName.equalsIgnoreCase("403")
                                || fileName.equalsIgnoreCase("500")
                                || fileName.equalsIgnoreCase("1102")) {
                            CustomToast.show(context, R.string.main_process_failed);
                        } else if (fileName.equalsIgnoreCase("551")) {
//                            controlStopRecord();
//                            mPlayVideoHandler.sendMessage(MessageUtil.set(1, "toast", "只允许一人观看回看"));
                        } else if (fileName.length() > 10) {
                            Log.d("wulian.icam", "getObjectDataModel" + fileName);
                            GetObjectDataModel getObjectDataModel = OSSXMLHandler
                                    .getObjectData(xmlData, deviceId);
                            if (getObjectDataModel != null && getObjectDataModel.getFileSize() > 0) {
                                Log.d("wulian.icam", " getObjectDataModel:" + getObjectDataModel.getObjectName());

                                this.getObjectDataModel = getObjectDataModel;
                                if (firstVideo) {
                                    playOSSObjectName(getObjectDataModel);
                                    firstVideo = false;
                                }
                            } else {
                            }
                        }
                    }
                    break;
            }
        }
    }


    public void controlStartRecord() {
        String sip_ok = "sip:" + mDeviceControlUrl;
        Log.d("wulian.icam", Common.getLogHead(this.getClass()) + mDeviceControlUrl);
        SipController.getInstance().sendMessage(mDeviceControlUrl,
                SipHandler.ControlStartRecord(sip_ok, SipFactory.getInstance().seq++, LibraryPhoneStateUtil.getImsi(context)), SipFactory.getInstance().getSipProfile());
    }

    public void controlStopRecord() {
        if (!TextUtils.isEmpty(mSessionID)) {
            String sip_ok = "sip:" + mDeviceControlUrl;
            SipController.getInstance().sendMessage(mDeviceControlUrl,
                    SipHandler.ControlStopRecord(sip_ok, SipFactory.getInstance().seq++, mSessionID),
                    SipFactory.getInstance().getSipProfile());
        }
    }

    public void removeMessages() {
        mPlayVideoHandler.removeCallbacksAndMessages(null);
    }

}
