/*
 *  Copyright (c) 2012 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc.videoengine;

import android.content.Context;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.wulian.siplibrary.utils.WulianLog;

public class ViERenderer {
    // View used for local rendering that Cameras can use for Video Overlay.
    private static SurfaceHolder g_localRenderer;
    public static final int TAKE_PICTURE=10;
    public static final int TAKE_PICTURE_FAIL=11;
    public static final String GET_PICTURE="get_picture";
    public static final String GET_PATH="get_path";
    public static final int FILE_OK=0;//图片截取成功
    public static final int FILE_MOUNT_EXCEPTION=1;//SD不能加载
    public static final int FILE_PICTURE_EXCEPTION=2;//图片截取错误
    public static final int FILE_PICTURE_CREATE_EXCEPTION=3;//图片创建失败
    
    public static final int PICTURE_HAS_COMING=10;//图片来临
    
    public static SurfaceView CreateRenderer(Context context) {
        return  CreateRenderer(context,false,false);
    }

    public static SurfaceView CreateRenderer(Context context,
            boolean useOpenGLES2,boolean isSupportReverse) {
        if(useOpenGLES2 == true && ViEAndroidGLES20.IsSupported(context))
            return new ViEAndroidGLES20(context,isSupportReverse);
        else
            return new SurfaceView(context);
    }

    public static void setTakePicHandler(Handler handler) {
    	ViEAndroidGLES20.setHandler(handler);
    }
    
    public static void setTakePic(String fileDir) {
    	WulianLog.d("VieRender", "FileDir is:"+fileDir);
    	ViEAndroidGLES20.setTakePic(fileDir);
    }
    
    public static void setTakePicNotSave() {
    	ViEAndroidGLES20.setTakePicNotSave();
    }
    
    public static void setIsReturnPictureState() {
    	ViEAndroidGLES20.setIsReturnPictureState();
    }
    
    // Creates a SurfaceView to be used by Android Camera
    // service to display a local preview.
    // This needs to be used on Android prior to version 2.1
    // in order to run the camera.
    // Call this function before ViECapture::StartCapture.
    // The created view needs to be added to a visible layout
    // after a camera has been allocated
    // (with the call ViECapture::AllocateCaptureDevice).
    // IE.
    // CreateLocalRenderer
    // ViECapture::AllocateCaptureDevice
    // LinearLayout.addview
    // ViECapture::StartCapture
    public static SurfaceView CreateLocalRenderer(Context context) {
        SurfaceView localRender = new SurfaceView(context);
        g_localRenderer = localRender.getHolder();
        g_localRenderer.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        return  localRender;
    }

    public static SurfaceHolder GetLocalRenderer() {
        return g_localRenderer;
    }

}
