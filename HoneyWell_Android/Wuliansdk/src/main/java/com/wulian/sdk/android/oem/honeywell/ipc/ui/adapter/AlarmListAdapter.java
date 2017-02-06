/**
 * Project Name:  iCam
 * File Name:     DeviceAdapter.java
 * Package Name:  com.wulian.icam.adpter
 *
 * @Date: 2014年10月21日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.sdk.android.oem.honeywell.ipc.Interface;
import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.DeviceDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.data.repository.UserDataRepository;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Alarm;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.view.AlarmListView;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CommonUtils;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.DateTimeUtils;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.JsonUtil;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.SnapCache;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * @Function: 新版设备适配器V2
 * @date: 2015年6月24日
 * @author Wangjj
 */

public class AlarmListAdapter extends BaseAdapter {
    private Context context;
//    private Set<String> stringSet;
//    ConnectivityManager connectivity;
//    AlarmListView alarmListView;
    List<Alarm> mAlarmList;
    public SnapCache snapCache;
    private View.OnClickListener mClickListener;

    private static class ViewHolder {
        public TextView tv_start_time, tv_end_time, tv_video_length, tv_device_name, tv_online;
        public ImageView iv_preview;
        public Button btn_download;
    }

    public AlarmListAdapter(Context context) {
        this.context = context;
        mAlarmList=new ArrayList<Alarm>();
        snapCache = new SnapCache(12 * 1024 * 1024);
    }

    public void refreshList(List<Alarm> data) {
        mAlarmList.clear();
        mAlarmList.addAll(data);
        this.notifyDataSetChanged();
    }

    public void updateSingleRow(ListView listView, String id) {
        if (listView != null) {
            for (int i = 0 ; i <getCount(); i++)
                if(id.equalsIgnoreCase(getItem(i).getDeviceID())) {
//                    View view = listView.getChildAt(i);
                    getView(i, null, listView);
                    break;
                }
        }
    }

    public void setImageViewOnClickEvent(View.OnClickListener onClickEvent) {
        mClickListener=onClickEvent;
    }



    @Override
    public int getCount() {
        return mAlarmList.size();
    }

    @Override
    public Alarm getItem(int i) {
        return  mAlarmList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (null == view) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.adapter_alarm_device, viewGroup, false);
            holder = new ViewHolder();
            holder.tv_start_time = (TextView) view
                    .findViewById(R.id.tv_start_time);
            holder.tv_end_time = (TextView) view
                    .findViewById(R.id.tv_end_time);
            holder.tv_video_length = (TextView) view
                    .findViewById(R.id.tv_video_length);
            holder.tv_device_name = (TextView) view.findViewById(R.id.tv_device_name);
            holder.tv_online = (TextView) view.findViewById(R.id.tv_isonline);
            holder.iv_preview = (ImageView) view.findViewById(R.id.iv_preview);
            holder.btn_download = (Button) view.findViewById(R.id.btn_download);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if(holder==null) {
            LibraryLoger.d("The holder is NULL");
        }
        Alarm alarm=getItem(i);
//        JSONObject jsonObject = JsonUtil.getJsonObj( DeviceDataRepository.getInstance().alarmListPair().get(i).first);
        try {
            String   sStartTime = DateTimeUtils.longToString((Long.valueOf(Long.valueOf(alarm.getStartTime()) / 60)) * 60*1000, "yyyy-MM-dd HH:mm:ss");
            holder.tv_start_time.setText(sStartTime);
            String  sEndTime = DateTimeUtils.longToString((Long.valueOf(Long.valueOf(alarm.getEndTime()) / 60)+1) * 60*1000, "yyyy-MM-dd HH:mm:ss");
            holder.tv_end_time.setText(sEndTime);
        } catch (ParseException e){
            e.printStackTrace();
        } catch (NumberFormatException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        float dataLen = Float.valueOf(alarm.getFileSize()) / (1024*1024);
        DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String fDataLen = decimalFormat.format(dataLen);//format 返回的是字符串
        holder.tv_video_length.setText("" + fDataLen + "M");
       // Device device =  DeviceDataRepository.getInstance().alarmListPair().get(i).second;
//        if (!TextUtils.isEmpty(device.getNick())) {
            holder.tv_device_name.setText(alarm.getDeviceName());
//        }
//        Pair<String, Device> tt = DeviceDataRepository.getInstance().alarmListPair().get(i);
        try {
            holder.iv_preview.setTag(R.id.tag_first, alarm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setSkipListener(holder.iv_preview);
        setPic(alarm.getDeviceID(), holder.iv_preview);
        return view;
    }

    private void setSkipListener(ImageView imageView){
        imageView.setOnClickListener(mClickListener);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Alarm alarm = (Alarm)v.getTag(R.id.tag_first);
//                Interface.getInstance().OpenAlarmVideo(alarm.getDeviceID() ,(int)alarm.getAlarmID(), String.valueOf(alarm.getStartTime()), String.valueOf(alarm.getEndTime()));
//            }
//        });
    }

    private void setPic(String deviceid, ImageView imageView){
        // 背景显示
        // 固定尺寸为16:9
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView
                .getLayoutParams();
        // 屏幕宽度-2边的padding
        lp.width = Utils.getDeviceSize(context).widthPixels
                - 2
                * context.getResources().getDimensionPixelSize(
                R.dimen.device_list_padding);

        lp.height = (int) (lp.width * 9.0 / 16.0);
        imageView.setLayoutParams(lp);
        Bitmap bitmap = snapCache.get(deviceid + "-alarm");
        if (bitmap == null) {
            bitmap = Utils.getBitmap(deviceid + "-alarm", context).get();
            if (bitmap != null) {
                Utils.sysoInfo("snap放入缓存");
                snapCache.put(deviceid + "-alarm", bitmap);
            }
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}