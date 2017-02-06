/**
 * Project Name:  WulianLibrary
 * File Name:     Compatibility.java
 * Package Name:  com.wulian.siplibrary.utils
 * @Date:         2014年10月29日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.utils;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder.AudioSource;
import android.os.Build;
import android.provider.Settings;

import com.wulian.siplibrary.manage.SipConfigManager;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @ClassName: Compatibility
 * @Function: TODO
 * @Date: 2014年10月29日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class Compatibility {
	private final static String THIS_FILE = "Compatibility";
	public static List<CodecPriority> codecPrioritys = new ArrayList<CodecPriority>();
	public static List<CodecPriority> videoPrioritys = new ArrayList<CodecPriority>();
	static {
		CodecPriority cp = null;
		cp = new CodecPriority();
		cp.setCodecPriority("G722/16000/1", SipConfigManager.CODEC_WB, 200);
		codecPrioritys.add(cp);
		cp = new CodecPriority();
		cp.setCodecPriority("GSM/8000/1", SipConfigManager.CODEC_WB, 252);
		codecPrioritys.add(cp);
		cp = new CodecPriority();
		cp.setCodecPriority("PCMU/8000/1", SipConfigManager.CODEC_WB, 250);
		codecPrioritys.add(cp);
		cp = new CodecPriority();
		cp.setCodecPriority("PCMA/8000/1", SipConfigManager.CODEC_WB, 254);
		codecPrioritys.add(cp);

		// cp = new CodecPriority();
		// cp.setCodecPriority("speex/8000/1", SipConfigManager.CODEC_WB, 55);
		// codecPrioritys.add(cp);
		// cp = new CodecPriority();
		// cp.setCodecPriority("speex/16000/1", SipConfigManager.CODEC_WB, 54);
		// codecPrioritys.add(cp);
		// cp = new CodecPriority();
		// cp.setCodecPriority("speex/32000/1", SipConfigManager.CODEC_WB, 53);
		// codecPrioritys.add(cp);
		// cp = new CodecPriority();
		// cp.setCodecPriority("AMR/8000/1", SipConfigManager.CODEC_WB, 52);
		// codecPrioritys.add(cp);
		// cp = new CodecPriority();
		// cp.setCodecPriority("AMR-WB/16000/1", SipConfigManager.CODEC_WB, 51);
		// codecPrioritys.add(cp);
		// cp = new CodecPriority();
		// cp.setCodecPriority("ISAC/16000/1", SipConfigManager.CODEC_WB, 34);
		// codecPrioritys.add(cp);
		// cp = new CodecPriority();
		// cp.setCodecPriority("ILBC/8000/1", SipConfigManager.CODEC_WB, 23);
		// codecPrioritys.add(cp);

		cp = new CodecPriority();
		cp.setCodecPriority("G722/16000/1", SipConfigManager.CODEC_NB, 200);
		codecPrioritys.add(cp);
		cp = new CodecPriority();
		cp.setCodecPriority("GSM/8000/1", SipConfigManager.CODEC_NB, 252);
		codecPrioritys.add(cp);
		cp = new CodecPriority();
		cp.setCodecPriority("PCMA/8000/1", SipConfigManager.CODEC_NB, 254);
		codecPrioritys.add(cp);
		cp = new CodecPriority();
		cp.setCodecPriority("PCMU/8000/1", SipConfigManager.CODEC_NB, 250);
		codecPrioritys.add(cp);

		// cp = new CodecPriority();
		// cp.setCodecPriority("speex/8000/1", SipConfigManager.CODEC_NB, 55);
		// codecPrioritys.add(cp);
		// cp = new CodecPriority();
		// cp.setCodecPriority("speex/16000/1", SipConfigManager.CODEC_NB, 54);
		// codecPrioritys.add(cp);
		// cp = new CodecPriority();
		// cp.setCodecPriority("speex/32000/1", SipConfigManager.CODEC_NB, 53);
		// codecPrioritys.add(cp);
		// cp = new CodecPriority();
		// cp.setCodecPriority("AMR/8000/1", SipConfigManager.CODEC_NB, 52);
		// codecPrioritys.add(cp);
		// cp = new CodecPriority();
		// cp.setCodecPriority("AMR-WB/16000/1", SipConfigManager.CODEC_NB, 51);
		// codecPrioritys.add(cp);
		// cp = new CodecPriority();
		// cp.setCodecPriority("ISAC/16000/1", SipConfigManager.CODEC_NB, 34);
		// codecPrioritys.add(cp);
		// cp = new CodecPriority();
		// cp.setCodecPriority("ILBC/8000/1", SipConfigManager.CODEC_WB, 23);
		// codecPrioritys.add(cp);
		//
		//
		//
		//
		// String keys[] = { "speex/8000/1", "speex/16000/1", "G729/8000/1",
		// "iLBC/8000/1", "SILK/8000/1", "SILK/12000/1", "SILK/16000/1",
		// "SILK/24000/1", "CODEC2/8000/1", "G7221/16000/1",
		// "G7221/32000/1", "ISAC/16000/1", "ISAC/32000/1", "AMR/8000/1",
		// "AMR-WB/16000/1", "opus/8000/1", "opus/16000/1",
		// "opus/24000/1", "opus/48000/1", "G726-16/8000/1",
		// "G726-24/8000/1", "G726-32/8000/1", "G726-40/8000/1",
		// "mpeg4-generic/48000/1" };
		// for (int i = 0; i < keys.length; i++) {
		// cp = new CodecPriority();
		// cp.setCodecPriority(keys[i], SipConfigManager.CODEC_WB, i + 1);
		// codecPrioritys.add(cp);
		// }

	}
	static {
		CodecPriority videoCP = null;
		videoCP = new CodecPriority();
		videoCP.setCodecPriority("VP8", SipConfigManager.CODEC_WB, 102);
		videoPrioritys.add(videoCP);
		videoCP = new CodecPriority();
		videoCP.setCodecPriority("H264/97", SipConfigManager.CODEC_WB, 255);
		videoPrioritys.add(videoCP);
		videoCP = new CodecPriority();
		videoCP.setCodecPriority("H263-1998/96", SipConfigManager.CODEC_WB, 100);
		videoPrioritys.add(videoCP);

		videoCP = new CodecPriority();
		videoCP.setCodecPriority("VP8", SipConfigManager.CODEC_NB, 102);
		videoPrioritys.add(videoCP);
		videoCP = new CodecPriority();
		videoCP.setCodecPriority("H264/97", SipConfigManager.CODEC_NB, 255);
		videoPrioritys.add(videoCP);
		videoCP = new CodecPriority();
		videoCP.setCodecPriority("H263-1998/96", SipConfigManager.CODEC_NB, 100);
		videoPrioritys.add(videoCP);
	}

	public static short getCodecPriority(String codecName, String type,
			int defaultValue) {
		int size = codecPrioritys.size();
		CodecPriority cp = null;

		for (int i = 0; i < size; i++) {
			cp = codecPrioritys.get(i);
			if (cp.getCodecName().equals(codecName)
					&& cp.getType().equals(type)) {
				return (short) cp.getNewValue();
			}
		}
		return (short) defaultValue;
	}

	public static short getVideoCodecPriority(String codecName, String type,
			int defaultValue) {
		int size = videoPrioritys.size();
		CodecPriority cp = null;
		for (int i = 0; i < size; i++) {
			cp = videoPrioritys.get(i);
			if (cp.getCodecName().equals(codecName)
					&& cp.getType().equals(type)) {
				return (short) cp.getNewValue();
			}
		}
		return (short) defaultValue;
	}

	public static void setFirstRunParameters() {
		WulianDefaultPreference.setsSndMediaQuality(getCpuAbi()
				.equalsIgnoreCase("armeabi-v7a") ? 4 : 3);
		WulianDefaultPreference.setsSndClockRate(getDefaultFrequency());
		WulianDefaultPreference.setsMediaThreadCount(getNumCores() > 1 ? 2 : 1);
		WulianDefaultPreference
				.setsAudioImplementation(getDefaultAudioImplementation());
		WulianDefaultPreference.setsUseWebRtcHack(needWebRTCImplementation());
		WulianDefaultPreference.setsMicroSource(getDefaultMicroSource());
	}

	public static int getApiLevel() {
		return android.os.Build.VERSION.SDK_INT;
	}

	public static String getCpuAbi() {
		Field field;
		try {
			field = android.os.Build.class.getField("CPU_ABI");
			return field.get(null).toString();
		} catch (Exception e) {
			WulianLog.w(THIS_FILE,
					"Announce to be android 1.6 but no CPU ABI field", e);
		}
		return "armeabi";
	}

	public static int getDefaultFrequency() {
		if (android.os.Build.DEVICE.equalsIgnoreCase("olympus")) {
			// Atrix bug
			return 32000;
		}
		if (android.os.Build.DEVICE.toUpperCase(Locale.getDefault()).equals(
				"GT-P1010")) {
			// Galaxy tab see issue 932
			return 32000;
		}

		return 16000;
	}

	private static boolean needWebRTCImplementation() {
		if (android.os.Build.DEVICE.toLowerCase(Locale.getDefault()).contains(
				"droid2")) {
			return true;
		}
		if (android.os.Build.MODEL.toLowerCase(Locale.getDefault()).contains(
				"droid bionic")) {
			return true;
		}
		if (android.os.Build.DEVICE.toLowerCase(Locale.getDefault()).contains(
				"sunfire")) {
			return true;
		}
		// Huawei Y300
		if (android.os.Build.DEVICE.equalsIgnoreCase("U8833")) {
			return true;
		}
		return false;
	}

	/**
	 * Get the stream id for in call track. Can differ on some devices. Current
	 * device for which it's different :
	 * 
	 * @return
	 */
	public static int getInCallStream() {
		/* Archos 5IT */
		if (android.os.Build.BRAND.equalsIgnoreCase("archos")
				&& android.os.Build.DEVICE.equalsIgnoreCase("g7a")) {
			// Since archos has no voice call capabilities, voice call stream is
			// not implemented
			// So we have to choose the good stream tag, which is by default
			// falled back to music
			return AudioManager.STREAM_MUSIC;
		}

		return AudioManager.STREAM_MUSIC;
		// return AudioManager.STREAM_VOICE_CALL;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static int getWifiSleepPolicy(Context context) {
		if (Compatibility.isCompatible(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
			if (context != null)
				return Settings.Global.getInt(context.getContentResolver(),
						Settings.Global.WIFI_SLEEP_POLICY,
						Settings.Global.WIFI_SLEEP_POLICY_DEFAULT);
			else
				return Settings.Global.WIFI_SLEEP_POLICY_DEFAULT;
		} else {
			if (context != null)
				return Settings.System.getInt(context.getContentResolver(),
						Settings.System.WIFI_SLEEP_POLICY,
						Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
			else
				return Settings.Global.WIFI_SLEEP_POLICY_DEFAULT;
		}
	}

	private static int getDefaultMicroSource() {
		// Except for galaxy S II :(
		if (!isCompatible(11)
				&& android.os.Build.DEVICE.toUpperCase(Locale.getDefault())
						.startsWith("GT-I9100")) {
			return AudioSource.MIC;
		}
		return AudioSource.VOICE_COMMUNICATION;
	}

	public static boolean needPspWorkaround() {
		// New api for 2.3 does not work on Incredible S
		if (android.os.Build.DEVICE.equalsIgnoreCase("vivo")) {
			return true;
		}
		return false;
	}

	public final static int getNumCores() {
		// Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				// Check if filename is "cpu", followed by a single digit number
				if (Pattern.matches("cpu[0-9]", pathname.getName())) {
					return true;
				}
				return false;
			}
		}
		try {
			// Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			// Return the number of cores (virtual CPU devices)
			return files.length;
		} catch (Exception e) {
			return Runtime.getRuntime().availableProcessors();
		}
	}

	private static int getDefaultAudioImplementation() {
		// Acer A510
		if (android.os.Build.DEVICE.toLowerCase(Locale.getDefault())
				.startsWith("picasso")) {
			return WulianDefaultPreference.AUDIO_IMPLEMENTATION_JAVA;
		}
		if (Compatibility.isCompatible(11)) {
			return WulianDefaultPreference.AUDIO_IMPLEMENTATION_OPENSLES;
		}
		if (android.os.Build.DEVICE.equalsIgnoreCase("ST25i")
				&& Compatibility.isCompatible(10)) {
			return WulianDefaultPreference.AUDIO_IMPLEMENTATION_OPENSLES;
		}
		if (android.os.Build.DEVICE.equalsIgnoreCase("u8510")
				&& Compatibility.isCompatible(10)) {
			return WulianDefaultPreference.AUDIO_IMPLEMENTATION_OPENSLES;
		}
		return WulianDefaultPreference.AUDIO_IMPLEMENTATION_JAVA;
	}

	/**
	 * Wrapper to set alarm at exact time
	 * 
	 * @see android.app.AlarmManager#setExact(int, long, PendingIntent)
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void setExactAlarm(AlarmManager alarmManager, int alarmType,
			long firstTime, PendingIntent pendingIntent) {
		if (isCompatible(Build.VERSION_CODES.KITKAT)) {
			alarmManager.setExact(alarmType, firstTime, pendingIntent);
		} else {
			alarmManager.set(alarmType, firstTime, pendingIntent);
		}
	}

	public static boolean isCompatible(int apiLevel) {
		return android.os.Build.VERSION.SDK_INT >= apiLevel;
	}
}
