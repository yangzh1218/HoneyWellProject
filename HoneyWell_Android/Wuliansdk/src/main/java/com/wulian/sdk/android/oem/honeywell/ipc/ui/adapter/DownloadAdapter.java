package com.wulian.sdk.android.oem.honeywell.ipc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.model.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on ${date} .
 */

public class DownloadAdapter extends BaseAdapter {
    Context context;
    List mVideoList;
    @Override
    public int getCount() {
        return 11;
    }

    public DownloadAdapter(Context context) {
        this.context = context;
    }
    public  static class  ViewHolder{
        public ImageView iv_video_snapshot;
        public TextView tv_video_name,tv_video_startTime,tv_video_length,tv_file_size,tv_download_status;

    }
    @Override
    public Video getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final  ViewHolder holder;
        if(view == null)
        {
           view = LayoutInflater.from(context).inflate(
                    R.layout.adapter_download, viewGroup, false);
            holder = new ViewHolder();
            holder.iv_video_snapshot = (ImageView) view.findViewById(R.id.iv_video_snapshot);
            holder.tv_video_name = (TextView) view.findViewById(R.id.tv_video_name);
            holder.tv_video_startTime = (TextView) view.findViewById(R.id.tv_video_startTime);
            holder.tv_video_length = (TextView) view.findViewById(R.id.tv_video_length);
            holder.tv_file_size = (TextView) view.findViewById(R.id.tv_file_size);
            holder.tv_download_status = (TextView) view.findViewById(R.id.tv_download_status);
            view.setTag(holder);
            mVideoList = new ArrayList<Video>();
        }else {
            holder = (ViewHolder) view.getTag();
        }

        Video video = getItem(position);

        return view;
    }
}
