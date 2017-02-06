package org.webrtc.videoengine;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {
	/**
	 *
	 * Deletes all files and subdirectories under "dir".
	 *
	 * @param dir
	 *            Directory to be deleted
	 *
	 * @return boolean Returns "true" if all deletions were successful.
	 *
	 *         If a deletion fails, the method stops attempting to
	 *
	 *         delete and returns "false".
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			if (children.length == 0) {
				return dir.delete();
			}
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty so now it can be smoked
		return dir.delete();

	}

	/**
	 * Whether the memory card in writing form
	 *
	 * @Title: exterStorageReady
	 * @return true,false
	 */
	public static boolean exterStorageReady() {
		// Get SdCard state
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				&& Environment.getExternalStorageDirectory().canWrite()) {
			return true;
		}
		return false;
	}

	public boolean SDMakeDir(String DirName) {
		File file = new File(DirName);
		if (!file.exists()) {
			return file.mkdirs();
		} else {
			return false;
		}
	}

	public static boolean isFilePathExist(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		return true;
	}

	public static String getfilename() {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyyMMddHHmmss");
		Date localDate = new Date();
		String filename = String.valueOf(localSimpleDateFormat
				.format(localDate));
		return filename;
	}

	public static boolean checksdfilepath(String path) {
		if (!exterStorageReady()) {
			return false;
		}
		File file = new File(path);
		if (!file.exists()) {
			return file.mkdirs();
		}
		return true;
	}
}
