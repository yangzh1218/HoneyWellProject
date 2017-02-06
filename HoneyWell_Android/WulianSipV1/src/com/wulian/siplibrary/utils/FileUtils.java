/**
 * Project Name:  WulianICamSip
 * File Name:     FileUtils.java
 * Package Name:  com.wulian.siplibrary.utils
 * @Date:         2015年1月25日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.utils;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;

import java.io.File;
import java.util.Date;

/**
 * @ClassName: FileUtils
 * @Function: TODO
 * @Date: 2015年1月25日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class FileUtils {
	private static final String THIS_FILE = "FileUtils";
	private final static String LOGS_FOLDER = "logs";
	
	public FileUtils() {

		// TODO Auto-generated constructor stub

	}

	/**
	 * Get the SD card folder name. This folder will be used to store call
	 * records, configs and logs
	 * 
	 * @return the name of the folder to use
	 */
	public static String getSDCardFolder() {
		return "WulianSip";
	}

	private static File getStorageFolder(Context ctxt, boolean preferCache) {
		File root = Environment.getExternalStorageDirectory();
		if (!root.canWrite() || preferCache) {
			root = ctxt.getCacheDir();
		}

		if (root.canWrite()) {
			File dir = new File(root.getAbsolutePath() + File.separator
					+ getSDCardFolder());
			if (!dir.exists()) {
				dir.mkdirs();
				WulianLog.d(THIS_FILE,
						"Create directory " + dir.getAbsolutePath());
			}
			return dir;
		}
		return null;
	}

	private static File getSubFolder(Context ctxt, String subFolder,
			boolean preferCache) {
		File root = getStorageFolder(ctxt, preferCache);
		if (root != null) {
			File dir = new File(root.getAbsoluteFile() + File.separator
					+ subFolder);
			dir.mkdirs();
			return dir;
		}
		return null;
	}

	public static File getLogsFolder(Context ctxt) {
		return getSubFolder(ctxt, LOGS_FOLDER, false);
	}

	public static File getLogsFile(Context ctxt, boolean isPjsip) {
		File dir = getLogsFolder(ctxt);
		File outFile = null;
		if (dir != null) {
			Date d = new Date();
			StringBuffer fileName = new StringBuffer();
			if (isPjsip) {
				fileName.append("pjsip");
			}
			fileName.append("logs_");
			fileName.append(DateFormat.format("yy-MM-dd_kkmmss", d));
			fileName.append(".txt");
			outFile = new File(dir.getAbsoluteFile() + File.separator
					+ fileName.toString());
		}
		return outFile;
	}

}
