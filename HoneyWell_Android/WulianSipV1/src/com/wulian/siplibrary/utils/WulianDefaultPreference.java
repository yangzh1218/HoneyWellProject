/**
 * Project Name:  WulianLibrary
 * File Name:     DefaultPreference.java
 * Package Name:  com.wulian.siplibrary.utils
 * @Date:         2014年10月25日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.utils;

import android.media.AudioManager;
import android.media.MediaRecorder.AudioSource;

import com.wulian.siplibrary.manage.SipProfile;

/**
 * @ClassName: DefaultPreference
 * @Function: 常量设置
 * @Date: 2014年10月25日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class WulianDefaultPreference {
	public static final boolean forV5 = false;
	/**
	 * Automatic echo mode.
	 * 
	 * @see #ECHO_MODE
	 */
	public static final int ECHO_MODE_AUTO = 0;
	/**
	 * Simple echo mode. It's a basic implementation
	 * 
	 * @see #ECHO_MODE
	 */
	public static final int ECHO_MODE_SIMPLE = 1;
	/**
	 * Accoustic echo cancellation of Speex
	 * 
	 * @see #ECHO_MODE
	 */
	public static final int ECHO_MODE_SPEEX = 2;
	/**
	 * Accoustic echo cancellation of WebRTC
	 * 
	 * @see #ECHO_MODE
	 */
	public static final int ECHO_MODE_WEBRTC_M = 3;
	/**
	 * Uses java/jni implementation audio implementation.
	 * 
	 * @see #AUDIO_IMPLEMENTATION
	 */
	public static final int AUDIO_IMPLEMENTATION_JAVA = 0;
	/**
	 * Uses opensl-ES implementation audio implementation.
	 * 
	 * @see #AUDIO_IMPLEMENTATION
	 */
	public static final int AUDIO_IMPLEMENTATION_OPENSLES = 1;

	/**
	 * Auto detect options, depending on android settings.
	 */
	public static final int GENERIC_TYPE_AUTO = 0;
	/**
	 * Force this option on.
	 */
	public static final int GENERIC_TYPE_FORCE = 1;
	/**
	 * Disable this option.
	 */
	public static final int GENERIC_TYPE_PREVENT = 2;

	private static String sUserAgent = forV5 ? "SimpleWulianLibrary"
			: "WulianLibrary";
	private static String sTurnServer = "";
	private static String sTurnUsername = "";
	private static String sTurnPassword = "";
	private static String sSipSystemLibPath="";
	// private static String sStunServer =
	// "stun.wuliangroup.cn,stun.ideasip.com";// "222.190.121.158:8060"
	// private static String sStunServer =
	// "stun.wuliangroup.cn,hk.stun.wuliangroup.cn";// "222.190.121.158:8060"
	// private static String sStunServer =
	// "stun.counterpath.com,hk.stun.wuliangroup.cn";// "222.190.121.158:8060"
	// private static String sStunServer =
	// "stun.pjsip.org,hk.stun.wuliangroup.cn";// "222.190.121.158:8060"
	// private static String sStunServer =
	// "stun.pjsip.org,stun.counterpath.com";// "222.190.121.158:8060"(海外版本)
	// private static String sStunServer =
	// "stun.simplegg.com,stun2.simplegg.com";// "222.190.121.158:8060"(海外版本)
	private static String sStunServer = "stun.sh.gg,hk.stun.sh.gg";// "222.190.121.158:8060"
	// private static String sStunServer = "47.88.3.63,hk.stun.sh.gg";//
	// "222.190.121.158:8060"
	// private static String sStunServer =
	// "stun.pjsip.org,stun.counterpath.com";// "222.190.121.158:8060"(海外版本)
	// private static String sStunServer = "222.190.121.158";//
	// "222.190.121.158:8060"
	// private static String sStunServer = "	ucloud.sh.gg";

	// private static String sStunServer = "114.215.239.25";//
	// "222.190.121.158:8060"

	// private static String sStunServer =
	// "hk.stun.wuliangroup.cn,stun.wuliangroup.cn";// "222.190.121.158:8060"
	// private static String sStunServer = "stun.ideasip.com";//
	// "222.190.121.158:8060" ,stun.wuliangroup.cn:8060;
	private static String sTlsServerName = "";
	private static String sTlsPassword = "";

	private static String sCaListFile = "";
	private static String sCertFile = "";
	private static String sPrivKeyFile = "";
	private static String sDefaultCallerID = "";
	// private static String sVideoCaptureSize = "144x176@30";
	// private static String sVideoCaptureSize = "1280x720@30";
	private static String sVideoCaptureSize = "640x480@1";
	// private static String sNameServer = "114.114.114.114";
	private static String sNameServer = "";
	private static String sProxyServer = "";// 正常版本
	// private static String sProxyServer = "simplegg.com";// 设置海外版本

	private static int sMediaThreadCount = 1;
	private static int sMaxCalls = 4;
	private static int sThreadCount = 1;
	// private static int sLogLevel = 1;
	private static int sUseSrtp = 0;
	private static int sUseZrtp = 1;
	private static int sDefaultTransport = SipProfile.TRANSPORT_UDP;
	private static int sDefaultLocalTransport = SipProfile.TRANSPORT_TCP;
	private static int sUdpTranslatePort = forV5 ? 8070 : 8060;
	private static int sTcpTranslatePort = forV5 ? 8070 : 8060;
	private static int sTlsTranslatePort = forV5 ? 8071 : 8061;
	private static int sSipTransport = ((sDefaultTransport == SipProfile.TRANSPORT_TLS) ? sTlsTranslatePort
			: sUdpTranslatePort);
	// private static int sUdpTranslatePort = forV5?5061:5060;
	// private static int sTcpTranslatePort = forV5?5061:5060;
	// private static int sTlsTranslatePort = forV5?5061:5060;
	// private static int sSipTransport = forV5?5061:5060;
	private static int sUdpKeepAliveIntervalWIFI = 80;
	private static int sUdpKeepAliveIntervalMobile = 40;
	private static int sTcpKeepAliveIntervalWIFI = 180;
	private static int sTcpKeepAliveIntervalMobile = 120;
	private static int sTlsKeepAliveIntervalWIFI = 180;
	private static int sTlsKeepAliveIntervalMobile = 120;
	private static int sRtpPort = 0;// 部分路由器stun超时
	private static int sTimerMinSec = 90;
	private static int sTimerSessExpires = 1800;
	// private static int sTimerSessExpires = 180;
	private static int sTsxT1Timeout = 700;
	private static int sTsxT2Timeout = 8000;
	private static int sTsxT4Timeout = -1;
	private static int sTsxTdTimeout = -1;
	private static int sSndAutoCloseTime = 1;
	private static int sEchoCancelLationTail = 200;
	private static int sEchoMode = ECHO_MODE_AUTO;
	// private static int sEchoMode = ECHO_MODE_WEBRTC_M;
	private static int sSndMediaQuality = 4;
	private static int sSndClockRate = 16000;
	private static int sSndPtime = 20;
	private static int sSipAudioMode = AudioManager.MODE_NORMAL;
	private static int sMicroSource = AudioSource.VOICE_COMMUNICATION;
	private static int sAudioImplementation = AUDIO_IMPLEMENTATION_OPENSLES;
	private static int sH264Profile = 66;
	private static int sH264Level = 32;
	private static int sH264BitRate = 0;

	private static int sTurnTransport = 0;
	private static int sTlsMethod = 0;
	private static int sDscpVal = 24;
	private static int sDscpRtpVal = 46;

	private static boolean sEnableTcp = true;
	private static boolean sEnableUdp = true;
	private static boolean sEnableTls = false;
	private static boolean sEnableIce = true;
	private static boolean sIceAggressive = true;
	private static boolean sEnableDNSSrv = false;
	private static boolean sEnableTurn = false;
	private static boolean sEnableStun = true;
	private static boolean sEnableStun2 = false;
	private static boolean sEnableQos = false;
	private static boolean sUseCompactForm = true;
	private static boolean sDisableRport = false;
	private static boolean sForceNoUpdate = true;
	// Media
	private static boolean sEchoCancellation = true;
	private static boolean sEnableVad = true;
	private static boolean sEnableNoiseSuppression = false;
	private static boolean sHasIoQueue = true;
	private static boolean sUseWebRtcHack = false;
	private static boolean sAutoConnectSpeaker = false;
	private static boolean sAutoDetectSpeaker = false;
	private static boolean sSetupAudioBeforeInit = true;
	private static boolean sRestartAudioWhenRoutingChange = true;
	private static boolean sSupportMultipleCalls = true;
	private static boolean sTlsVerifyServer = false;
	private static boolean sTlsVerifyClient = false;

	private static boolean sUseModeApi = false;
	private static boolean sUseRoutingApi = false;
	private static boolean sSetAudioGenetateTone = false;

	private static float sSndMicLevel = 1.0f;
	private static float sSndSpeakerLevel = 1.0f;
	private static float sSndStreamLevel = 6.0f;

	public static void setsUserAgent(String sUserAgent) {
		WulianDefaultPreference.sUserAgent = sUserAgent;
	}

	public static String getsUserAgent() {
		return sUserAgent;
	}

	public static void setsTurnServer(String sTurnServer) {
		WulianDefaultPreference.sTurnServer = sTurnServer;
	}

	public static String getsTurnServer() {
		return sTurnServer;
	}

	public static void setsTurnUsername(String sTurnUsername) {
		WulianDefaultPreference.sTurnUsername = sTurnUsername;
	}

	public static String getsTurnUsername() {
		return sTurnUsername;
	}

	public static void setsTurnPassword(String sTurnPassword) {
		WulianDefaultPreference.sTurnPassword = sTurnPassword;
	}

	public static String getsTurnPassword() {
		return sTurnPassword;
	}

	public static void setsStunServer(String sStunServer) {
		WulianDefaultPreference.sStunServer = sStunServer;
	}

	public static String getsStunServer() {
		return sStunServer;
	}

	public static void setsTlsServerName(String sTlsServerName) {
		WulianDefaultPreference.sTlsServerName = sTlsServerName;
	}

	public static String getsTlsServerName() {
		return sTlsServerName;
	}

	public static void setsTlsPassword(String sTlsPassword) {
		WulianDefaultPreference.sTlsPassword = sTlsPassword;
	}

	public static String getsTlsPassword() {
		return sTlsPassword;
	}

	public static void setsCaListFile(String sCaListFile) {
		WulianDefaultPreference.sCaListFile = sCaListFile;
	}

	public static String getsCaListFile() {
		return sCaListFile;
	}

	public static void setsCertFile(String sCertFile) {
		WulianDefaultPreference.sCertFile = sCertFile;
	}

	public static String getsCertFile() {
		return sCertFile;
	}

	public static void setsPrivKeyFile(String sPrivKeyFile) {
		WulianDefaultPreference.sPrivKeyFile = sPrivKeyFile;
	}

	public static String getsPrivKeyFile() {
		return sPrivKeyFile;
	}

	public static void setsDefaultCallerID(String sDefaultCallerID) {
		WulianDefaultPreference.sDefaultCallerID = sDefaultCallerID;
	}

	public static String getsDefaultCallerID() {
		return sDefaultCallerID;
	}

	public static void setsUseSrtp(int sUseSrtp) {
		WulianDefaultPreference.sUseSrtp = sUseSrtp;
	}

	public static int getsUseSrtp() {
		return sUseSrtp;
	}

	public static void setsUseZrtp(int sUseZrtp) {
		WulianDefaultPreference.sUseZrtp = sUseZrtp;
	}

	public static int getsUseZrtp() {
		return sUseZrtp;
	}

	public static void setsUdpTranslatePort(int sUdpTranslatePort) {
		WulianDefaultPreference.sUdpTranslatePort = sUdpTranslatePort;
	}

	public static int getsUdpTranslatePort() {
		return sUdpTranslatePort;
	}

	public static void setsTcpTranslatePort(int sTcpTranslatePort) {
		WulianDefaultPreference.sTcpTranslatePort = sTcpTranslatePort;
	}

	public static int getsTcpTranslatePort() {
		return sTcpTranslatePort;
	}

	public static void setsTlsTranslatePort(int sTlsTranslatePort) {
		WulianDefaultPreference.sTlsTranslatePort = sTlsTranslatePort;
	}

	public static int getsTlsTranslatePort() {
		return sTlsTranslatePort;
	}

	public static void setsSipTransport(int sSipTransport) {
		WulianDefaultPreference.sSipTransport = sSipTransport;
	}

	public static int getsSipTransport() {
		return sSipTransport;
	}

	public static void setsUdpKeepAliveIntervalWIFI(
			int sUdpKeepAliveIntervalWIFI) {
		WulianDefaultPreference.sUdpKeepAliveIntervalWIFI = sUdpKeepAliveIntervalWIFI;
	}

	public static int getsUdpKeepAliveIntervalWIFI() {
		return sUdpKeepAliveIntervalWIFI;
	}

	public static void setsUdpKeepAliveIntervalMobile(
			int sUdpKeepAliveIntervalMobile) {
		WulianDefaultPreference.sUdpKeepAliveIntervalMobile = sUdpKeepAliveIntervalMobile;
	}

	public static int getsUdpKeepAliveIntervalMobile() {
		return sUdpKeepAliveIntervalMobile;
	}

	public static void setsTcpKeepAliveIntervalWIFI(
			int sTcpKeepAliveIntervalWIFI) {
		WulianDefaultPreference.sTcpKeepAliveIntervalWIFI = sTcpKeepAliveIntervalWIFI;
	}

	public static int getsTcpKeepAliveIntervalWIFI() {
		return sTcpKeepAliveIntervalWIFI;
	}

	public static void setsTcpeepAliveIntervalMobile(
			int sTcpeepAliveIntervalMobile) {
		WulianDefaultPreference.sTcpKeepAliveIntervalMobile = sTcpeepAliveIntervalMobile;
	}

	public static int getsTcpeepAliveIntervalMobile() {
		return sTcpKeepAliveIntervalMobile;
	}

	public static void setsTlsKeepAliveIntervalWIFI(
			int sTlsKeepAliveIntervalWIFI) {
		WulianDefaultPreference.sTlsKeepAliveIntervalWIFI = sTlsKeepAliveIntervalWIFI;
	}

	public static int getsTlsKeepAliveIntervalWIFI() {
		return sTlsKeepAliveIntervalWIFI;
	}

	public static void setsTlsKeepAliveIntervalMobile(
			int sTlseepAliveIntervalMobile) {
		WulianDefaultPreference.sTlsKeepAliveIntervalMobile = sTlseepAliveIntervalMobile;
	}

	public static int getsTlsKeepAliveIntervalMobile() {
		return sTlsKeepAliveIntervalMobile;
	}

	public static void setsRtpPort(int sRtpPort) {
		WulianDefaultPreference.sRtpPort = sRtpPort;
	}

	public static int getsRtpPort() {
		return sRtpPort;
	}

	public static void setsTimerMinSec(int sTimerMinSec) {
		WulianDefaultPreference.sTimerMinSec = sTimerMinSec;
	}

	public static int getsTimerMinSec() {
		return sTimerMinSec;
	}

	public static void setsTimerSessExpires(int sTimerSessExpires) {
		WulianDefaultPreference.sTimerSessExpires = sTimerSessExpires;
	}

	public static int getsTimerSessExpires() {
		return sTimerSessExpires;
	}

	public static void setsTsxT1Timeout(int sTsxT1Timeout) {
		WulianDefaultPreference.sTsxT1Timeout = sTsxT1Timeout;
	}

	public static int getsTsxT1Timeout() {
		return sTsxT1Timeout;
	}

	public static void setsTsxT2Timeout(int sTsxT2Timeout) {
		WulianDefaultPreference.sTsxT2Timeout = sTsxT2Timeout;
	}

	public static int getsTsxT2Timeout() {
		return sTsxT2Timeout;
	}

	public static void setsTsxT4Timeout(int sTsxT4Timeout) {
		WulianDefaultPreference.sTsxT4Timeout = sTsxT4Timeout;
	}

	public static int getsTsxT4Timeout() {
		return sTsxT4Timeout;
	}

	public static void setsTsxTdTimeout(int sTsxTdTimeout) {
		WulianDefaultPreference.sTsxTdTimeout = sTsxTdTimeout;
	}

	public static int getsTsxTdTimeout() {
		return sTsxTdTimeout;
	}

	public static void setsSndAutoCloseTime(int sSndAutoCloseTime) {
		WulianDefaultPreference.sSndAutoCloseTime = sSndAutoCloseTime;
	}

	public static int getsSndAutoCloseTime() {
		return sSndAutoCloseTime;
	}

	public static void setsEchoCancelLationTail(int sEchoCancelLationTail) {
		WulianDefaultPreference.sEchoCancelLationTail = sEchoCancelLationTail;
	}

	public static int getsEchoCancelLationTail() {
		return sEchoCancelLationTail;
	}

	public static void setsEchoCancellation(boolean sEchoCancellation) {
		WulianDefaultPreference.sEchoCancellation = sEchoCancellation;
	}

	public static boolean getsEchoCancellation() {
		return sEchoCancellation;
	}

	public static void setsEchoMode(int sEchoMode) {
		WulianDefaultPreference.sEchoMode = sEchoMode;
	}

	public static int getsEchoMode() {
		return sEchoMode;
	}

	public static void setsSndMediaQuality(int sSndMediaQuality) {
		WulianDefaultPreference.sSndMediaQuality = sSndMediaQuality;
	}

	public static int getsSndMediaQuality() {
		return sSndMediaQuality;
	}

	public static void setsSndClockRate(int sSndClockRate) {
		WulianDefaultPreference.sSndClockRate = sSndClockRate;
	}

	public static int getsSndClockRate() {
		return sSndClockRate;
	}

	public static void setsSndPtime(int sSndPtime) {
		WulianDefaultPreference.sSndPtime = sSndPtime;
	}

	public static int getsSndPtime() {
		return sSndPtime;
	}

	public static void setsSipAudioMode(int sSipAudioMode) {
		WulianDefaultPreference.sSipAudioMode = sSipAudioMode;
	}

	public static int getsSipAudioMode() {
		return sSipAudioMode;
	}

	public static void setsMicroSource(int sMicroSource) {
		WulianDefaultPreference.sMicroSource = sMicroSource;
	}

	public static int getsMicroSource() {
		return sMicroSource;
	}

	public static void setsAudioImplementation(int sAudioImplementation) {
		WulianDefaultPreference.sAudioImplementation = sAudioImplementation;
	}

	public static int getsAudioImplementation() {
		return sAudioImplementation;
	}

	public static void setsH264Level(int sH264Level) {
		WulianDefaultPreference.sH264Level = sH264Level;
	}

	public static int getsH264Level() {
		return sH264Level;
	}

	public static void setsH264Profile(int sH264Profile) {
		WulianDefaultPreference.sH264Profile = sH264Profile;
	}

	public static int getsH264Profile() {
		return sH264Profile;
	}

	public static void setsH264BitRate(int sH264BitRate) {
		WulianDefaultPreference.sH264BitRate = sH264BitRate;
	}

	public static int getsH264BitRate() {
		return sH264BitRate;
	}

	public static void setsVideoCaptureSize(String sVideoCaptureSize) {
		WulianDefaultPreference.sVideoCaptureSize = sVideoCaptureSize;
	}

	public static String getsVideoCaptureSize() {
		return sVideoCaptureSize;
	}

	public static void setsTurnTransport(int sTurnTransport) {
		WulianDefaultPreference.sTurnTransport = sTurnTransport;
	}

	public static int getsTurnTransport() {
		return sTurnTransport;
	}

	public static void setsTlsMethod(int sTlsMethod) {
		WulianDefaultPreference.sTlsMethod = sTlsMethod;
	}

	public static int getsTlsMethod() {
		return sTlsMethod;
	}

	public static void setsDscpVal(int sDscpVal) {
		WulianDefaultPreference.sDscpVal = sDscpVal;
	}

	public static int getsDscpVal() {
		return sDscpVal;
	}

	public static void setsDscpRtpVal(int sDscpRtpVal) {
		WulianDefaultPreference.sDscpRtpVal = sDscpRtpVal;
	}

	public static int getsDscpRtpVal() {
		return sDscpRtpVal;
	}

	public static void setsEnableUdp(boolean sEnableUdp) {
		WulianDefaultPreference.sEnableUdp = sEnableUdp;
	}

	public static boolean getsEnableUdp() {
		return sEnableUdp;
	}

	public static void setsEnableTcp(boolean sEnableTcp) {
		WulianDefaultPreference.sEnableTcp = sEnableTcp;
	}

	public static boolean getsEnableTcp() {
		return sEnableTcp;
	}

	public static void setsEnableTls(boolean sEnableTls) {
		WulianDefaultPreference.sEnableTls = sEnableTls;
	}

	public static boolean getsEnableTls() {
		return sEnableTls;
	}

	public static void setsEnableIce(boolean sEnableIce) {
		WulianDefaultPreference.sEnableIce = sEnableIce;
	}

	public static boolean getsEnableIce() {
		return sEnableIce;
	}

	public static void setsIceAggressive(boolean sIceAggressive) {
		WulianDefaultPreference.sIceAggressive = sIceAggressive;
	}

	public static boolean getsIceAggressive() {
		return sIceAggressive;
	}

	public static void setsEnableTurn(boolean sEnableTurn) {
		WulianDefaultPreference.sEnableTurn = sEnableTurn;
	}

	public static boolean getsEnableTurn() {
		return sEnableTurn;
	}

	public static void setsEnableStun(boolean sEnableStun) {
		WulianDefaultPreference.sEnableStun = sEnableStun;
	}

	public static boolean getsEnableStun() {
		return sEnableStun;
	}

	public static void setsEnableStun2(boolean sEnableStun2) {
		WulianDefaultPreference.sEnableStun2 = sEnableStun2;
	}

	public static boolean getsEnableStun2() {
		return sEnableStun2;
	}

	public static void setsEnableQos(boolean sEnableQos) {
		WulianDefaultPreference.sEnableQos = sEnableQos;
	}

	public static boolean getsEnableQos() {
		return sEnableQos;
	}

	public static void setsEnableNoiseSuppression(
			boolean sEnableNoiseSuppression) {
		WulianDefaultPreference.sEnableNoiseSuppression = sEnableNoiseSuppression;
	}

	public static boolean getsEnableNoiseSuppression() {
		return sEnableNoiseSuppression;
	}

	public static void setsUseCompactForm(boolean sUseCompactForm) {
		WulianDefaultPreference.sUseCompactForm = sUseCompactForm;
	}

	public static boolean getsUseCompactForm() {
		return sUseCompactForm;
	}

	public static void setsUseWebRtcHack(boolean sUseWebRtcHack) {
		WulianDefaultPreference.sUseWebRtcHack = sUseWebRtcHack;
	}

	public static boolean getsUseWebRtcHack() {
		return sUseWebRtcHack;
	}

	public static void setsDisableRport(boolean sDisableRport) {
		WulianDefaultPreference.sDisableRport = sDisableRport;
	}

	public static boolean getsDisableRport() {
		return sDisableRport;
	}

	public static void setsHasIoQueue(boolean sHasIoQueue) {
		WulianDefaultPreference.sHasIoQueue = sHasIoQueue;
	}

	public static boolean getsHasIoQueue() {
		return sHasIoQueue;
	}

	public static void setsAutoConnectSpeaker(boolean sAutoConnectSpeaker) {
		WulianDefaultPreference.sAutoConnectSpeaker = sAutoConnectSpeaker;
	}

	public static boolean getsAutoConnectSpeaker() {
		return sAutoConnectSpeaker;
	}

	public static void setsAutoDetectSpeaker(boolean sAutoDetectSpeaker) {
		WulianDefaultPreference.sAutoDetectSpeaker = sAutoDetectSpeaker;
	}

	public static boolean getsAutoDetectSpeaker() {
		return sAutoDetectSpeaker;
	}

	public static void setsSetupAudioBeforeInit(boolean sSetupAudioBeforeInit) {
		WulianDefaultPreference.sSetupAudioBeforeInit = sSetupAudioBeforeInit;
	}

	public static boolean getsSetupAudioBeforeInit() {
		return sSetupAudioBeforeInit;
	}

	public static void setsSupportMultipleCalls(boolean sSupportMultipleCalls) {
		WulianDefaultPreference.sSupportMultipleCalls = sSupportMultipleCalls;
	}

	public static boolean getsSupportMultipleCalls() {
		return sSupportMultipleCalls;
	}

	public static void setsTlsVerifyServer(boolean sTlsVerifyServer) {
		WulianDefaultPreference.sTlsVerifyServer = sTlsVerifyServer;
	}

	public static boolean getsTlsVerifyServer() {
		return sTlsVerifyServer;
	}

	public static void setsTlsVerifyClient(boolean sTlsVerifyClient) {
		WulianDefaultPreference.sTlsVerifyClient = sTlsVerifyClient;
	}

	public static boolean getsTlsVerifyClient() {
		return sTlsVerifyClient;
	}

	public static void setsSndMicLevel(float sSndMicLevel) {
		WulianDefaultPreference.sSndMicLevel = sSndMicLevel;
	}

	public static float getsSndMicLevel() {
		return sSndMicLevel;
	}

	public static void setsSndSpeakerLevel(float sSndSpeakerLevel) {
		WulianDefaultPreference.sSndSpeakerLevel = sSndSpeakerLevel;
	}

	public static float getsSndSpeakerLevel() {
		return sSndSpeakerLevel;
	}

	public static void setsEnableVad(boolean sEnableVad) {
		WulianDefaultPreference.sEnableVad = sEnableVad;
	}

	public static boolean getsEnableVad() {
		return sEnableVad;
	}

	public static void setsTcpKeepAliveIntervalMobile(
			int sTcpKeepAliveIntervalMobile) {
		WulianDefaultPreference.sTcpKeepAliveIntervalMobile = sTcpKeepAliveIntervalMobile;
	}

	public static int getsTcpKeepAliveIntervalMobile() {
		return sTcpKeepAliveIntervalMobile;
	}

	public static void setsForceNoUpdate(boolean sForceNoUpdate) {
		WulianDefaultPreference.sForceNoUpdate = sForceNoUpdate;
	}

	public static boolean getsForceNoUpdate() {
		return sForceNoUpdate;
	}

	// public static void setsLogLevel(int sLogLevel) {
	// WulianDefaultPreference.sLogLevel = sLogLevel;
	// }
	//
	// public static int getsLogLevel() {
	// if (sLogLevel <= 6 && sLogLevel >= 1) {
	// return sLogLevel;
	// }
	// return 1;
	// }

	public static void setsMediaThreadCount(int sMediaThreadCount) {
		WulianDefaultPreference.sMediaThreadCount = sMediaThreadCount;
	}

	public static int getsMediaThreadCount() {
		return sMediaThreadCount;
	}

	public static void setsSndStreamLevel(float sSndStreamLevel) {
		WulianDefaultPreference.sSndStreamLevel = sSndStreamLevel;
	}

	public static float getsSndStreamLevel() {
		return sSndStreamLevel;
	}

	public static void setsThreadCount(int sThreadCount) {
		WulianDefaultPreference.sThreadCount = sThreadCount;
	}

	public static int getsThreadCount() {
		return sThreadCount;
	}

	public static void setsEnableDNSSrv(boolean sEnableDNSSrv) {
		WulianDefaultPreference.sEnableDNSSrv = sEnableDNSSrv;
	}

	public static boolean getsEnableDNSSrv() {
		return sEnableDNSSrv;
	}

	public static void setsUseModeApi(boolean sUseModeApi) {
		WulianDefaultPreference.sUseModeApi = sUseModeApi;
	}

	public static boolean getsUseModeApi() {
		return sUseModeApi;
	}

	public static void setsUseRoutingApi(boolean sUseRoutingApi) {
		WulianDefaultPreference.sUseRoutingApi = sUseRoutingApi;
	}

	public static boolean getsUseRoutingApi() {
		return sUseRoutingApi;
	}

	public static void setsSetAudioGenetateTone(boolean sSetAudioGenetateTone) {
		WulianDefaultPreference.sSetAudioGenetateTone = sSetAudioGenetateTone;
	}

	public static boolean getsSetAudioGenetateTone() {
		return sSetAudioGenetateTone;
	}

	public static void setsNameServer(String sNameServer) {
		WulianDefaultPreference.sNameServer = sNameServer;
	}

	public static String getsNameServer() {
		return sNameServer;
	}

	public static void setsMaxCalls(int sMaxCalls) {
		WulianDefaultPreference.sMaxCalls = sMaxCalls;
	}

	public static int getsMaxCalls() {
		return sMaxCalls;
	}

	public static boolean getsRestartAudioWhenRoutingChange() {
		return sRestartAudioWhenRoutingChange;
	}

	public static void setsRestartAudioWhenRoutingChange(
			boolean sRestartAudioWhenRoutingChange) {
		WulianDefaultPreference.sRestartAudioWhenRoutingChange = sRestartAudioWhenRoutingChange;
	}

	public static String getsProxyServer() {
		return sProxyServer;
	}

	public static void setsProxyServer(String sProxyServer) {
		WulianDefaultPreference.sProxyServer = sProxyServer;
	}

	public static int getsDefaultTransport() {
		return sDefaultTransport;
	}

	public static void setsDefaultTransport(int sDefaultTransport) {
		WulianDefaultPreference.sDefaultTransport = sDefaultTransport;
	}

	public static int getsDefaultLocalTransport() {
		return sDefaultLocalTransport;
	}

	public static void setsDefaultLocalTransport(int sDefaultLocalTransport) {
		WulianDefaultPreference.sDefaultLocalTransport = sDefaultLocalTransport;
	}

	public static String getsSipSystemLibPath() {
		return sSipSystemLibPath;
	}

	public static void setsSipSystemLibPath(String sSipSystemLibPath) {
		WulianDefaultPreference.sSipSystemLibPath = sSipSystemLibPath;
	}
}
