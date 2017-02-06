package com.wulian.routelibrary.utils;

import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {
	private String SDPath;
	private static int columnWidth;

	public void SetSDPath(String SDPath) {
		this.SDPath = SDPath;
	}

	public String GetSDPath() {
		return this.SDPath;
	}

	public FileUtils() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * 构造方法    获取SD卡路径    
	 */
	public FileUtils(int width) {
		// 获得当前外部存储设备的目录  
		SDPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
		columnWidth = width;
	}

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
			for (int i = 0;i < children.length;i++) {
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
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				&& Environment.getExternalStorageDirectory().canWrite()) {
			return true;

		}
		return false;
	}

	public static String readTextFile(InputStream inputStream) throws UnsupportedEncodingException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		byte buf[] = new byte[1024];

		int len;

		try {

			while ((len = inputStream.read(buf)) != -1) {

				outputStream.write(buf, 0, len);

			}

			outputStream.close();

			inputStream.close();

		} catch (IOException e) {
			return "";
		}
		return outputStream.toString("gbk");
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength,
			int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
			int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h
				/ maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 获取图片的本地存储路径。
	 *
	 * @param imageUrl
	 *            图片的URL地址。
	 * @return 图片的本地存储路径。
	 */
	public static String getImagePath(String imageUrl) {
		int lastSlashIndex = imageUrl.lastIndexOf("/");
		String imageName = imageUrl.substring(lastSlashIndex + 1);
		String imagePath = imageName;
		LibraryLoger.d("The iamgePath is:" + imagePath);
		return imagePath;
	}

	public static boolean hasExStorage() {
		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			return false;
		}
		return true;
	}
//
//	public static boolean hasStoraged(String url) {
//		String fileName = SafetyApp.getInstance().getImagePath() + getImagePath(url);
//		File f = new File(fileName);
//		if (f.isFile() && f.exists()) {
//			return true;
//		}
//		return false;
//	}

	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPath + fileName);
		file.createNewFile();
		return file;
	}

	public File CreateSDDir(String dirName) {
		File dir = new File(SDPath + dirName);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			dir.mkdir();
		}
		return dir;
	}

	public static boolean IsSDExist() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public boolean SDMakeDir(String DirName) {
		File file = new File(DirName);
		if (!file.exists()) {
			return file.mkdirs();
		} else {
			return false;
		}
	}

	public boolean isFileExist(String FileName) {
		File file = new File(SDPath + FileName);
		return file.exists();
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
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date localDate = new Date();
		String filename = String.valueOf(localSimpleDateFormat.format(localDate));
		return filename;
	}

	public static boolean checksdfilepath(String path) {
		if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return false;
		}
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return true;
	}

	public static String Unzip(String zipFile, String targetDir) throws IOException { // 参数为文件所在路径,比如zipFile
		String fileName = ""; // 为E:\ABC.ZIP,destination为E:\
		int BUFFER = 4096; // 这里缓冲区我们使用4KB，
		String strEntry; // 保存每个zip的条目名称

		try {
			BufferedOutputStream dest = null; // 缓冲输出流
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry; // 每个zip条目的实例

			while ((entry = zis.getNextEntry()) != null) {
				try {
					int count;
					byte data[] = new byte[BUFFER];
					strEntry = entry.getName();

					File entryFile = new File(targetDir + strEntry);
					fileName = targetDir + strEntry;
					File entryDir = new File(entryFile.getParent());
					if (!entryDir.exists()) {
						entryDir.mkdirs();
					}

					FileOutputStream fos = new FileOutputStream(entryFile);
					dest = new BufferedOutputStream(fos, BUFFER);
					while ((count = zis.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			zis.close();
		} catch (Exception cwj) {
			cwj.printStackTrace();
		}
		return fileName;
	}

	public static void copy(InputStream minput, FileOutputStream moutput) throws IOException {
		byte[] buffer = new byte[1024];
		int length;

		while ((length = minput.read(buffer)) > 0) {
			moutput.write(buffer, 0, length);
		}

		moutput.flush();
		moutput.close();
		minput.close();
	}

	public static void saveToSDCard(String filename, String content) throws Exception {
		File file = new File("/mnt/sdcard", filename);
		OutputStream out = new FileOutputStream(file);
		out.write(content.getBytes());
		out.close();
	}

	public static String getFileName(String url) {
		int index = url.lastIndexOf("/");
		return url.substring(index);
	}
}
