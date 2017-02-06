/**
 * Project Name:  RouteLibrary
 * File Name:     RouteLibraryUtils.java
 * Package Name:  com.wulian.routelibrary.utils
 * @Date:         2014-9-9
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.StrictMode;

import java.util.Date;
import java.util.Random;

/**
 * @ClassName: RouteLibraryUtils
 * @Function: 库文件常用类
 * @Date: 2014-9-9
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class RouteLibraryUtils {
	private RouteLibraryUtils() {

	};

	/**
	 * 
	 * @MethodName getRandomCharacter
	 * @Function 获取随机字符串
	 * @author Puml
	 * @date: 2014-9-9
	 * @email puml@wuliangroup.cn
	 * @param num
	 *            随机字符串个数
	 * @return
	 */
	public static String getRandomCharacter(int num) {
		StringBuilder sb = new StringBuilder();
		Random random = new Random((new Date()).getTime());
		String AllSelectedCharacter = "0123456789abcdefghigklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
		for (int i = 0; i < num; i++) {
			sb.append(AllSelectedCharacter.charAt(Math.abs(random.nextInt() % 41)));
		}
		return sb.toString();
	}

	/**
	 * 
	 * @MethodName enableStrictMode
	 * @Function StrictMode的策略和规则
	 * @author Puml
	 * @date: 2014-9-9
	 * @email puml@wuliangroup.cn
	 */
	@TargetApi(VERSION_CODES.HONEYCOMB)
	public static void enableStrictMode() {
		if (RouteLibraryUtils.hasGingerbread()) {
			StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog();
			StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyLog();

			if (RouteLibraryUtils.hasHoneycomb()) {
				threadPolicyBuilder.penaltyFlashScreen();
				// vmPolicyBuilder
				// .setClassInstanceLimit(ImageGridActivity.class, 1)
				// .setClassInstanceLimit(ImageDetailActivity.class, 1);
			}
			StrictMode.setThreadPolicy(threadPolicyBuilder.build());
			StrictMode.setVmPolicy(vmPolicyBuilder.build());
		}
	}

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static boolean hasKitKat() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	public static boolean isCompatible(int apiLevel) {
		return android.os.Build.VERSION.SDK_INT >= apiLevel;
	}
}
