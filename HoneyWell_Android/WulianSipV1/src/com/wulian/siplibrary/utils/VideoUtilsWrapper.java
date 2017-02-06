/**
 * Project Name:  WulianLibrary
 * File Name:     VideoUtilsWrapper.java
 * Package Name:  com.wulian.siplibrary.utils
 * @Date:         2014年10月29日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: VideoUtilsWrapper
 * @Function: 视频类
 * @Date: 2014年10月29日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class VideoUtilsWrapper {
	private static VideoUtilsWrapper instance;
	private static final String THIS_FILE = "VideoUtilsWrapper";

	public class VideoCaptureDeviceInfo {
		public List<VideoCaptureCapability> capabilities;
		public VideoCaptureCapability bestCapability;

		public VideoCaptureDeviceInfo() {
			capabilities = new ArrayList<VideoCaptureCapability>();
		}
	}

	public static class VideoCaptureCapability {
		public int width;
		public int height;
		public int fps;

		public VideoCaptureCapability() {

		}

		public VideoCaptureCapability(String preferenceValue) {
			if (!TextUtils.isEmpty(preferenceValue)) {
				String[] size_fps = preferenceValue.split("@");
				if (size_fps.length == 2) {
					String[] width_height = size_fps[0].split("x");
					if (width_height.length == 2) {
						try {
							width = Integer.parseInt(width_height[0]);
							height = Integer.parseInt(width_height[1]);
							fps = Integer.parseInt(size_fps[1]);
						} catch (NumberFormatException e) {
							WulianLog
									.e(THIS_FILE,
											"Cannot parse the preference for video capture cap");
						}
					}
				}
			}
		}

		public String toPreferenceValue() {
			return (width + "x" + height + "@" + fps);
		}

		public String toPreferenceDisplay() {
			return (width + " x " + height + " @" + fps + "fps");
		}
	}

	public static VideoUtilsWrapper getInstance() {
		if(instance==null) {
			instance=new VideoUtilsWrapper();
		}
		return instance;
	}
}
