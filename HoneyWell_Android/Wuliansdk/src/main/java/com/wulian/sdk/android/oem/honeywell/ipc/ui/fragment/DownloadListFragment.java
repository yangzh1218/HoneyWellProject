package com.wulian.sdk.android.oem.honeywell.ipc.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.play.ReplayVideoActivity;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.play.VideoPlayer;
import com.wulian.sdk.android.oem.honeywell.ipc.ui.adapter.DownloadAdapter;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.CustomToast;

/**
 * Created by Administrator on ${date} .
 */

public class DownloadListFragment extends Fragment {
    private View outView;
    private DownloadAdapter downloadAdapter;
    private ListView mVideoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        outView = inflater.inflate(R.layout.fragment_download_list, container,
                false);
        mVideoList = (ListView) outView.findViewById(R.id.lv_video);
        mVideoList.setAdapter(new DownloadAdapter(getActivity()));
        mVideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int posit = position + 1;
                Intent intent = new Intent(getActivity(), VideoPlayer.class);
                DownloadListFragment.this.startActivity(intent);
                CustomToast.show(getContext(),"开始播放第" + posit + "个视频");
            }
        });
        return outView;
    }


}
