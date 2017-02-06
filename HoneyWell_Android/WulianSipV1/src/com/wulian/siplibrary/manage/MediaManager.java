/**
 * Project Name:  WulianLibrary
 * File Name:     MediaManager.java
 * Package Name:  com.wulian.siplibrary.manage
 * @Date:         2014年10月31日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.manage;

import android.content.Context;
import android.media.AudioManager;

import com.wulian.siplibrary.pjsip.PjSipService;
import com.wulian.siplibrary.utils.Compatibility;
import com.wulian.siplibrary.utils.WulianDefaultPreference;
import com.wulian.siplibrary.utils.WulianLog;

/**
 * @ClassName: MediaManager
 * @Function: TODO
 * @Date: 2014年10月31日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class MediaManager {
	private final static String THIS_FILE = "MediaManager";
	private AudioManager audioManager;
	private boolean startBeforeInit;

	private boolean userWantSpeaker = false;
	private boolean userWantMicrophoneMute = false;
	private boolean useWebRTCImpl = false;

	// Media settings to save / resore
	private boolean isSavedSpeakerPhone = false;

	private static int savedVolume;
	private static int savedMode;
	// private static int modeSipInCall = AudioManager.MODE_IN_COMMUNICATION;
	private static int modeSipInCall = AudioManager.MODE_INVALID;
	private PjSipService pjService;
	private Context service;

	public MediaManager(Context aService, PjSipService pjService) {
		service = aService;
		this.pjService = pjService;
		audioManager = (AudioManager) service
				.getSystemService(Context.AUDIO_SERVICE);
	}

	public void startService() {
		useWebRTCImpl = WulianDefaultPreference.getsUseWebRtcHack();
		userWantSpeaker = WulianDefaultPreference.getsAutoConnectSpeaker();
		startBeforeInit = WulianDefaultPreference.getsSetupAudioBeforeInit();
	}

	public void stopService() {

	}
	
	public boolean isWiredHeadsetOn() {
		if(audioManager!=null) {
			return audioManager.isWiredHeadsetOn();
		}
		return false;
	}

   public int getCallStream() {
	   return Compatibility.getInCallStream();
   }
	
	public void AdjustCurrentVolume() {
		int inCallStream = Compatibility.getInCallStream();
		float max = audioManager.getStreamMaxVolume( inCallStream );
		float current = (float)audioManager.getStreamVolume( inCallStream );
		WulianLog.d("PML","max is:"+max+";current is:"+current);
		setMediaSpeakerphoneOn((float)current/max); 
	}

	private void restoreAudioState() {
		int inCallStream = Compatibility.getInCallStream();
		setStreamVolume(inCallStream, savedVolume, 0);
		audioManager.setSpeakerphoneOn(isSavedSpeakerPhone);
		audioManager.setMode(savedMode);
	}

	public int validateAudioClockRate(int clockRate) {
		if (clockRate != 8000) {
		}
		WulianLog.d(THIS_FILE, "validateAudioClockRate");
		return 0;
	}

	public void setAudioInCall(boolean beforeInit) {
		if (!beforeInit || (beforeInit && startBeforeInit)) {
			WulianLog.d(THIS_FILE, "setAudioInCall");
			actualSetAudioInCall();
		}
	}

	public void unsetAudioInCall() {
		actualUnsetAudioInCall();
	}

	/**
	 * Reset the audio mode
	 */
	private synchronized void actualUnsetAudioInCall() {
		audioManager.setMicrophoneMute(false);// PML TEST
		restoreAudioState();
	}

	private synchronized void actualSetAudioInCall() {
		saveAudioState();
		if (!useWebRTCImpl) {
			int targetMode = getAudioTargetMode();
			audioManager.setMode(targetMode);
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			audioManager.setSpeakerphoneOn(userWantSpeaker ? true : false);
			audioManager.setMicrophoneMute(true);// PML TEST
		} else {
			audioManager.setSpeakerphoneOn(userWantSpeaker);
		}
		int inCallStream = Compatibility.getInCallStream();
		
		setStreamVolume(inCallStream,savedVolume, 0);
	}

	public void setStreamVolume(int streamType, int index, int flags) {
		audioManager.setStreamVolume(streamType, index, flags);
	}

	public void setMicrophoneMute(boolean on) {
		userWantMicrophoneMute = on;
		audioManager.setMicrophoneMute(on);// PML TEST
		final float micVolume = WulianDefaultPreference.getsSndMicLevel();
//		int inCallStream = Compatibility.getInCallStream();
//		float currentVolume= audioManager.getStreamVolume(inCallStream);
//		final float speakVolume = audioManager.getStreamVolume(inCallStream);
		if (pjService != null) {
		//	pjService.confAdjustTxLevel(0, speakVolume);
			pjService.confAdjustRxLevel(0, userWantMicrophoneMute ? 0
					: micVolume);
		}
		AdjustCurrentVolume();
	}

	public void setSpeakerphoneOn(boolean on) {
		userWantSpeaker = on;
		audioManager.setSpeakerphoneOn(on);
	}

	public void setMediaSpeakerphoneOn(float speakVolume) {
		if (pjService != null) {
			pjService.confAdjustTxLevel(0, speakVolume);
		}
	}

	public void setMediaMicrophoneMute(float micVolume) {
		if (pjService != null) {
			pjService.confAdjustRxLevel(0, micVolume);
		}
	}

	public void setSoftwareVolume() {
		final float speakVolume = WulianDefaultPreference.getsSndSpeakerLevel();
		final float micVolume = WulianDefaultPreference.getsSndMicLevel();
		if (pjService != null) {
			pjService.confAdjustTxLevel(0, speakVolume);
		}
		if (pjService != null) {
			pjService.confAdjustRxLevel(0, micVolume);
		}
	}

	/**
	 * Save current audio mode in order to be able to restore it once done
	 */
	private synchronized void saveAudioState() {
		int inCallStream = Compatibility.getInCallStream();
		savedVolume = audioManager.getStreamVolume(inCallStream);
		isSavedSpeakerPhone = audioManager.isSpeakerphoneOn();

		savedMode = audioManager.getMode();
	}

	public void resetSettings() {
		userWantSpeaker = WulianDefaultPreference.getsAutoConnectSpeaker();
		userWantMicrophoneMute = false;
	}

	private int getAudioTargetMode() {
		int targetMode = modeSipInCall;
		WulianLog.d(THIS_FILE, "getAudioTargetMode is:" + targetMode);
		return targetMode;
	}
}
