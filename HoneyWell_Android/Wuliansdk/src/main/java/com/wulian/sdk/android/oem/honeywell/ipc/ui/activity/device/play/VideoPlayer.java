package com.wulian.sdk.android.oem.honeywell.ipc.ui.activity.device.play;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

import com.wulian.sdk.android.oem.honeywell.ipc.R;

/**
 * Created by Administrator on ${date} .
 */

public class VideoPlayer extends Activity implements View.OnTouchListener {


    // private
    MediaController mController;
    VideoView viv;
    int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        viv = (VideoView) findViewById(R.id.videoView);
        mController = new MediaController(this);
        viv.setMediaController(mController);
        // String videopath=getIntent().getStringExtra("path");
        // if (videopath!=null)
        // {
        // viv.setVideoPath(videopath);
        // }
        viv.setVideoURI(Uri.parse("android.resource://" + getPackageName()
                + "/" + R.raw.snapshot));
        viv.requestFocus();
        viv.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        progress = viv.getCurrentPosition();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        viv.seekTo(progress);
        viv.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        return false;

    }
}
