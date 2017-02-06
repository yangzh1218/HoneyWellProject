/**
 * Project Name:  WulianLibrary
 * File Name:     PjSipService.java
 * Package Name:  com.wulian.siplibrary.pjsip
 * @Date:         2014年10月24日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.pjsip;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;

import com.wulian.siplibrary.Configproperty;
import com.wulian.siplibrary.manage.MediaManager;
import com.wulian.siplibrary.manage.SipCallSession;
import com.wulian.siplibrary.manage.SipConfigManager;
import com.wulian.siplibrary.manage.SipManager;
import com.wulian.siplibrary.manage.SipManager.PresenceStatus;
import com.wulian.siplibrary.manage.SipProfile;
import com.wulian.siplibrary.manage.SipProfileState;
import com.wulian.siplibrary.manage.SipToCall;
import com.wulian.siplibrary.manage.SipUri.ParsedSipContactInfos;
import com.wulian.siplibrary.pjsip.Module.PjsipModule;
import com.wulian.siplibrary.pjsip.Module.RegHandlerModule;
import com.wulian.siplibrary.pjsip.Module.SipClfModule;
import com.wulian.siplibrary.utils.Compatibility;
import com.wulian.siplibrary.utils.ExtraPlugins;
import com.wulian.siplibrary.utils.ExtraPlugins.DynCodecInfos;
import com.wulian.siplibrary.utils.ProviderWrapper;
import com.wulian.siplibrary.utils.VideoUtilsWrapper;
import com.wulian.siplibrary.utils.VideoUtilsWrapper.VideoCaptureCapability;
import com.wulian.siplibrary.utils.WulianDefaultPreference;
import com.wulian.siplibrary.utils.WulianLog;

import org.pjsip.pjsua.csipsimple_acc_config;
import org.pjsip.pjsua.csipsimple_config;
import org.pjsip.pjsua.dynamic_factory;
import org.pjsip.pjsua.pj_pool_t;
import org.pjsip.pjsua.pj_qos_params;
import org.pjsip.pjsua.pj_str_t;
import org.pjsip.pjsua.pjmedia_srtp_use;
import org.pjsip.pjsua.pjsip_ssl_method;
import org.pjsip.pjsua.pjsip_tls_setting;
import org.pjsip.pjsua.pjsip_transport_type_e;
import org.pjsip.pjsua.pjsua;
import org.pjsip.pjsua.pjsuaConstants;
import org.pjsip.pjsua.pjsua_acc_config;
import org.pjsip.pjsua.pjsua_acc_info;
import org.pjsip.pjsua.pjsua_buddy_config;
import org.pjsip.pjsua.pjsua_call_info;
import org.pjsip.pjsua.pjsua_call_setting;
import org.pjsip.pjsua.pjsua_config;
import org.pjsip.pjsua.pjsua_ice_config;
import org.pjsip.pjsua.pjsua_logging_config;
import org.pjsip.pjsua.pjsua_media_config;
import org.pjsip.pjsua.pjsua_msg_data;
import org.pjsip.pjsua.pjsua_transport_config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @ClassName: PjSipService
 * @Function: TODO
 * @Date: 2014年10月24日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class PjSipService {
	private static final String THIS_FILE = "PjSipService";

	public UAStateReceiver userAgentReceiver;
	public MediaManager mediaManager;

	private Context context;
	private ProviderWrapper pref;

	private int pjsipTcpId = -1;
	private boolean hasSipStack = false;// 是否加载了库文件
	private boolean sipStackIsCorrupted = false;// 是否库文件中断
	private boolean created = false;// 是否创建
	private static boolean isLan = false;// 是否是局域网

	public boolean isCreated() {
		return created;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setPref(Context context){
		pref = new ProviderWrapper(context);
	}

	public Context getContext() {
		return this.context;
	}

	public ProviderWrapper getPref() {
		return this.pref;
	}

	// 加载库文件
	public boolean loadStack() {
		if (hasSipStack) {
			return true;
		}
		if (!sipStackIsCorrupted) {
			try {
				System.loadLibrary(NativeLibManager.STD_LIB_NAME);
				System.loadLibrary(NativeLibManager.STACK_NAME);
				hasSipStack = true;
				return true;
			} catch (UnsatisfiedLinkError e) {
				WulianLog.e(THIS_FILE,
						"We have a problem with the current stack", e);
				hasSipStack = false;
				sipStackIsCorrupted = true;
				return false;
			} catch (Exception e) {
				WulianLog.e(THIS_FILE,
						"We have a problem with the current stack....", e);
			}
		}
		return false;
	}

	public void setIsLan(boolean sipIsLan) {
		isLan = sipIsLan;
		WulianDefaultPreference.setsEnableStun(!sipIsLan);
		WulianDefaultPreference.setsEnableIce(!sipIsLan);
	}

	public boolean sipStart() {
		if (!hasSipStack) {
			WulianLog.e(THIS_FILE, "We have no sip stack, we can't start");
			return false;
		}
		if (!created) {
			WulianLog.d(THIS_FILE, "Starting sip stack");
			int status;
			getPref().resetAllDefaultValues();
			// TimerWrapper.create(context);
			status = pjsua.create();
			WulianLog.i(THIS_FILE, "Created " + status);
			// General config
			{
				pj_str_t[] stunServers = null;
				int stunServersCount = 0;
				pjsua_config cfg = new pjsua_config();
				pjsua_logging_config logCfg = new pjsua_logging_config();
				pjsua_media_config mediaCfg = new pjsua_media_config();
				csipsimple_config cssCfg = new csipsimple_config();
				// SERVICE CONFIG
				if (userAgentReceiver == null) {
					WulianLog.d(THIS_FILE, "create ua receiver");
					userAgentReceiver = new UAStateReceiver();
					userAgentReceiver.initService(this);
				}

				// if (zrtpReceiver == null) {
				// WulianLog.d(THIS_FILE, "create zrtp receiver");
				// zrtpReceiver = new ZrtpStateReceiver(this);
				// }
				if (mediaManager == null) {
					mediaManager = new MediaManager(context, this);
				}
				mediaManager.startService();
				// initModules();
				pjsua.setCallbackObject(userAgentReceiver);
				// pjsua.setZrtpCallbackObject(zrtpReceiver);
				// CSS CONFIG
				pjsua.csipsimple_config_default(cssCfg);
				cssCfg.setUse_compact_form_headers(WulianDefaultPreference
						.getsUseCompactForm() ? pjsua.PJ_TRUE : pjsua.PJ_FALSE);
				cssCfg.setUse_compact_form_sdp(WulianDefaultPreference
						.getsUseCompactForm() ? pjsua.PJ_TRUE : pjsua.PJ_FALSE);
				WulianLog.d(THIS_FILE, "ACTION_GET_EXTRA_CODECS is:"
						+ SipManager.GET_ACTION_GET_EXTRA_CODECS());
				WulianLog.d(THIS_FILE, "SIP PRE is:"
						+ SipManager.SIP_PREFIX_MSG);
				Map<String, DynCodecInfos> availableCodecs = ExtraPlugins
						.getDynCodecPlugins(context,
								SipManager.GET_ACTION_GET_EXTRA_CODECS());
				dynamic_factory[] cssCodecs = cssCfg.getExtra_aud_codecs();
				int i = 0;
				for (Entry<String, DynCodecInfos> availableCodec : availableCodecs
						.entrySet()) {
					DynCodecInfos dyn = availableCodec.getValue();
					if (!TextUtils.isEmpty(dyn.libraryPath)) {
						WulianLog.d("PML", "The librarypath is:"
								+ dyn.libraryPath + ";"
								+ dyn.factoryInitFunction);
						cssCodecs[i].setShared_lib_path(pjsua
								.pj_str_copy(dyn.libraryPath));
						cssCodecs[i++].setInit_factory_name(pjsua
								.pj_str_copy(dyn.factoryInitFunction));
					}
				}
				cssCfg.setExtra_aud_codecs_cnt(i);
				// Audio implementation
				int implementation = WulianDefaultPreference
						.getsAudioImplementation();
				if (implementation == WulianDefaultPreference.AUDIO_IMPLEMENTATION_OPENSLES) {
					dynamic_factory audImp = cssCfg.getAudio_implementation();
					audImp.setInit_factory_name(pjsua
							.pj_str_copy("pjmedia_opensl_factory"));
					File openslLib = NativeLibManager.getBundledStackLibFile(
							context, "libpj_opensl_dev.so");
					audImp.setShared_lib_path(pjsua.pj_str_copy(openslLib
							.getAbsolutePath()));
					cssCfg.setAudio_implementation(audImp);
					WulianLog.d(THIS_FILE, "Use OpenSL-ES implementation");
				}
				// Video implementation
				{
					// Have plugins per capture / render / video codec /
					WulianLog.d(THIS_FILE, "ACTION_GET_VIDEO_PLUGIN is:"
							+ SipManager.GET_ACTION_GET_VIDEO_PLUGIN());
					Map<String, DynCodecInfos> videoPlugins = ExtraPlugins
							.getDynCodecPlugins(context,
									SipManager.GET_ACTION_GET_VIDEO_PLUGIN());
					if (videoPlugins.size() > 0) {
						DynCodecInfos videoPlugin = videoPlugins.values()
								.iterator().next();
						pj_str_t pjVideoFile = pjsua
								.pj_str_copy(videoPlugin.libraryPath);
						WulianLog.d(THIS_FILE, "Load video plugin at "
								+ videoPlugin.libraryPath);
						// Render
						{
							dynamic_factory vidImpl = cssCfg
									.getVideo_render_implementation();
							vidImpl.setInit_factory_name(pjsua
									.pj_str_copy("pjmedia_webrtc_vid_render_factory"));
							vidImpl.setShared_lib_path(pjVideoFile);
						}
						// Capture
						{
							dynamic_factory vidImpl = cssCfg
									.getVideo_capture_implementation();
							vidImpl.setInit_factory_name(pjsua
									.pj_str_copy("pjmedia_webrtc_vid_capture_factory"));
							vidImpl.setShared_lib_path(pjVideoFile);
						}
						WulianLog
								.d(THIS_FILE,
										"ACTION_GET_EXTRA_VIDEO_CODECS is:"
												+ SipManager
														.GET_ACTION_GET_EXTRA_VIDEO_CODECS());
						// Video codecs
						availableCodecs = ExtraPlugins.getDynCodecPlugins(
								context,
								SipManager.GET_ACTION_GET_EXTRA_VIDEO_CODECS());
						cssCodecs = cssCfg.getExtra_vid_codecs();
						dynamic_factory[] cssCodecsDestroy = cssCfg
								.getExtra_vid_codecs_destroy();
						i = 0;
						for (Entry<String, DynCodecInfos> availableCodec : availableCodecs
								.entrySet()) {
							DynCodecInfos dyn = availableCodec.getValue();
							if (!TextUtils.isEmpty(dyn.libraryPath)) {
								// Create
								cssCodecs[i].setShared_lib_path(pjsua
										.pj_str_copy(dyn.libraryPath));
								cssCodecs[i].setInit_factory_name(pjsua
										.pj_str_copy(dyn.factoryInitFunction));
								// Destroy
								cssCodecsDestroy[i].setShared_lib_path(pjsua
										.pj_str_copy(dyn.libraryPath));
								cssCodecsDestroy[i]
										.setInit_factory_name(pjsua
												.pj_str_copy(dyn.factoryDeinitFunction));
							}
							i++;
						}
						cssCfg.setExtra_vid_codecs_cnt(i);

						// Converter
						dynamic_factory convertImpl = cssCfg.getVid_converter();
						convertImpl.setShared_lib_path(pjVideoFile);
						convertImpl
								.setInit_factory_name(pjsua
										.pj_str_copy("pjmedia_libswscale_converter_init"));

					}
				}
				// MAIN CONFIG
				pjsua.config_default(cfg);
				// cfg.setMax_calls(WulianDefaultPreference.getsMaxCalls());
				cfg.setCb(pjsuaConstants.WRAPPER_CALLBACK_STRUCT);
				WulianLog.d("PML", "The useragent is:"
						+ WulianDefaultPreference.getsUserAgent());
				// cfg.setUser_agent(pjsua.pj_str_copy(WulianDefaultPreference
				// .getsUserAgent()));
				int threadCount = WulianDefaultPreference.getsThreadCount();
				if (threadCount <= 0) {
					threadCount = 1;
				}
				cfg.setThread_cnt(threadCount);
				boolean isStunEnabled = WulianDefaultPreference
						.getsEnableStun();
				if (isStunEnabled) {
					WulianLog.d(THIS_FILE, "StunEnabled");
					String[] servers = WulianDefaultPreference.getsStunServer()
							.split(",");
					cfg.setStun_srv_cnt(servers.length);
					stunServers = cfg.getStun_srv();
					for (String server : servers) {
						WulianLog.d(THIS_FILE, "add server " + server.trim());
						stunServers[stunServersCount] = pjsua
								.pj_str_copy(server.trim());
						// if (stunServersCount == 0) {
						// cfg.setStun_host(pjsua.pj_str_copy(server.trim()));
						// }
						stunServersCount++;
					}
					cfg.setStun_srv(stunServers);
					// cfg.setStun_ignore_failure(boolToPjsuaConstant(true));
					// cfg.setStun_map_use_stun2(boolToPjsuaConstant(false));
					// cfg.setStun_map_use_stun2(boolToPjsuaConstant(WulianDefaultPreference
					// .getsEnableStun2()));
				}
				// LOGGING CONFIG
				pjsua.logging_config_default(logCfg);
				logCfg.setConsole_level(WulianLog.getLogLevel());
				logCfg.setLevel(WulianLog.getLogLevel());
				if (WulianLog.getLogLevel() >= 4) {
					logCfg.setMsg_logging(pjsuaConstants.PJ_TRUE);// 关闭pjsip日志
				} else {
					logCfg.setMsg_logging(pjsuaConstants.PJ_FALSE);// 关闭pjsip日志
				}

				// MEDIA CONFIG
				pjsua.media_config_default(mediaCfg);
				WulianLog.d(THIS_FILE, "media_config_default");
				// For now only this cfg is supported
				boolean iceEnabled = WulianDefaultPreference.getsEnableIce();
				mediaCfg.setEnable_ice(boolToPjsuaConstant(iceEnabled));
				// INITIALIZE
				status = pjsua.csipsimple_init(cfg, logCfg, mediaCfg, cssCfg,
						context);
				if (status != pjsuaConstants.PJ_SUCCESS) {
					String msg = "Fail to init pjsua "
							+ pjStrToString(pjsua.get_error_message(status));
					WulianLog.e(THIS_FILE, msg);
					cleanPjsua();
					return false;
				}
			}
//			pjsua.set_no_snd_dev();
//
//			pjsua.set_null_snd_dev();
			// Add transports
			{
				// TODO : allow to configure local accounts.

				// We need a local account for each transport
				// to not have the
				// application lost when direct call to the IP

				// UDP
				if (WulianDefaultPreference.getsEnableUdp()) {
					int udpPort = WulianDefaultPreference.getsUdpTranslatePort();
					udpPort = 0;
					Integer localUdpAccPjId = createTransportAndAccount(
							pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, udpPort);
					if (localUdpAccPjId == null) {
						cleanPjsua();
						return false;
					}
				}
				WulianLog.d(THIS_FILE,"EnableUdp()"+ WulianDefaultPreference.getsEnableUdp());
				// TCP
				if (WulianDefaultPreference.getsEnableTcp()) {
					int tcpPort = WulianDefaultPreference.getsTcpTranslatePort();
					tcpPort = 0;
					Integer localTcpAccPjId = createLocalTransportAndAccount(
							pjsip_transport_type_e.PJSIP_TRANSPORT_TCP, tcpPort);
					if (localTcpAccPjId == null) {
						cleanPjsua();
						return false;
					}
					pjsipTcpId = localTcpAccPjId;
				}
				WulianLog
						.d(THIS_FILE,
								"EnableTcp()"
										+ WulianDefaultPreference
												.getsEnableTcp());
				// TLS
				if (WulianDefaultPreference.getsEnableTls()) {
					int tlsPort = WulianDefaultPreference
							.getsTlsTranslatePort();
					Integer localTlsAccPjId = createTransportAndAccount(
							pjsip_transport_type_e.PJSIP_TRANSPORT_TLS, tlsPort);
					if (localTlsAccPjId == null) {
						cleanPjsua();
						return false;
					}
				}
				WulianLog
						.d(THIS_FILE,
								"EnableTls()"
										+ WulianDefaultPreference
												.getsEnableTls());
			}
			// // Add pjsip modules
			// for (PjsipModule mod : pjsipModules.values()) {
			// mod.onBeforeStartPjsip();
			// }
			// Initialization is done, now start pjsua
			status = pjsua.start();
			WulianLog.d(THIS_FILE, "pjsua start");
			if (status != pjsua.PJ_SUCCESS) {
				String msg = "Fail to start pjsip  "
						+ pjStrToString(pjsua.get_error_message(status));
				WulianLog.e(THIS_FILE, msg);
				cleanPjsua();
				return false;
			}
			// Init media codecs
			initCodecs();
			setCodecsPriorities(context);

			created = true;

			return true;
		}
		return false;
	}

	public boolean sipStop() {
		WulianLog.d(THIS_FILE, ">> SIP STOP <<");
		// if (getActiveCallInProgress() != null) {}
		if (created) {
			cleanPjsua();
		}
		context = null;
		pref = null;
		return true;
	}

	// public int registerLocalAccount() {
	// int tcpPort = WulianDefaultPreference
	// .getsTcpTranslatePort();
	// tcpPort = 0;
	// Integer localTcpAccPjId = createLocalTransportAndAccount(
	// pjsip_transport_type_e.PJSIP_TRANSPORT_TCP, tcpPort);
	// if (localTcpAccPjId == null) {
	// cleanPjsua();
	// return false;
	// }
	// }
	//

	public boolean unregistener(SipProfile profile) {
		int status = -1;
		int pjsuaId = -1;
		SipProfileState currentAccountStatus = profile.getSipProfileState();
		if (currentAccountStatus != null) {
			pjsuaId = currentAccountStatus.getPjsuaId();
		} else {
			pjsuaId = profile.getPjsuaId();
		}
		if (pjsuaId != -1) {
			status = pjsua.acc_del(pjsuaId);
		}
		return status == 0;
	}

	public int getLocalAccountInfo() {
		if (pjsipTcpId != -1) {
			int success = pjsuaConstants.PJ_FALSE;
			pjsua_acc_info pjAccountInfo;
			pjAccountInfo = new pjsua_acc_info();
			success = pjsua.acc_get_info(pjsipTcpId, pjAccountInfo);
			if (success == pjsuaConstants.PJ_SUCCESS && pjAccountInfo != null) {
				int statusCode = SipCallSession.StatusCode.OK;
				try {
					statusCode = pjAccountInfo.getStatus().swigValue();
				} catch (IllegalArgumentException e) {
					statusCode = SipCallSession.StatusCode.INTERNAL_SERVER_ERROR;
				}
				WulianLog.d("PML", "the statusCode is:" + statusCode);
				return statusCode;
			} else {
				WulianLog.d("PML", "the statusCode null");
				return -2;
			}
		} else {
			return -1;
		}
	}

	public int getAccountInfo(SipProfile profile) {
		if (profile != null && profile.getSipProfileState() != null
				&& profile.getSipProfileState().getPjsuaId() != -1) {
			int success = pjsuaConstants.PJ_FALSE;
			pjsua_acc_info pjAccountInfo;
			pjAccountInfo = new pjsua_acc_info();
			success = pjsua.acc_get_info(profile.getSipProfileState()
					.getPjsuaId(), pjAccountInfo);
			if (success == pjsuaConstants.PJ_SUCCESS && pjAccountInfo != null) {
				int statusCode = SipCallSession.StatusCode.OK;
				try {
					statusCode = pjAccountInfo.getStatus().swigValue();
				} catch (IllegalArgumentException e) {
					statusCode = SipCallSession.StatusCode.INTERNAL_SERVER_ERROR;
				}
				WulianLog.d("PML", "the statusCode is:" + statusCode);
				return statusCode;
			} else {
				WulianLog.d("PML", "the statusCode null");
				return -2;
			}
		} else {
			return -1;
		}

	}

	public boolean addLocalAccount() {
		// +
		WulianLog.d("PML", "addLocalAccount");
		csipsimple_acc_config css_cfg = new csipsimple_acc_config();
		pjsua.csipsimple_acc_config_default(css_cfg);
		pjsua_acc_config acc_cfg = new pjsua_acc_config();
		pj_pool_t pool = pjsua.pool_create("call_tmp", 512, 512);
		pjsua.acc_get_config(pjsipTcpId, pool, acc_cfg);

		pjsua_ice_config iceCfg = acc_cfg.getIce_cfg();
		iceCfg.setEnable_ice(pjsuaConstants.PJ_FALSE);
		acc_cfg.setIce_cfg(iceCfg);

		pjsua.csipsimple_set_acc_user_data(pjsipTcpId, css_cfg);
		pjsua.acc_modify(pjsipTcpId, acc_cfg);
		pjsua.pj_pool_release(pool);
		return true;
	}

	public boolean addAccount(SipProfile profile) {
		int status = pjsuaConstants.PJ_FALSE;
		if (!created) {
			WulianLog.e(THIS_FILE,
					"PJSIP is not started here, nothing can be done");
			return status == pjsuaConstants.PJ_SUCCESS;
		}
		PjSipAccount account = new PjSipAccount(profile);
		account.applyExtraParams(context);
		SipProfileState currentAccountStatus = profile.getSipProfileState();
		account.cfg.setRegister_on_acc_add(pjsuaConstants.PJ_FALSE);
		if (currentAccountStatus != null
				&& currentAccountStatus.isAddedToStack()) {
			pjsua.csipsimple_set_acc_user_data(
					currentAccountStatus.getPjsuaId(), account.css_cfg);
			status = pjsua.acc_modify(currentAccountStatus.getPjsuaId(),
					account.cfg);
			beforeAccountRegistration(currentAccountStatus.getPjsuaId(),
					profile);
			profile.getSipProfileState().setAddedStatus(status);
			if (status == pjsuaConstants.PJ_SUCCESS) {
				status = pjsua.acc_set_registration(
						currentAccountStatus.getPjsuaId(), 1);
				if (status == pjsuaConstants.PJ_SUCCESS) {
					pjsua.acc_set_online_status(
							currentAccountStatus.getPjsuaId(), 1);
				}
			}
		} else {
			int[] accId = new int[1];
			status = pjsua.acc_add(account.cfg, pjsuaConstants.PJ_FALSE, accId);
			pjsua.csipsimple_set_acc_user_data(accId[0], account.css_cfg);

			beforeAccountRegistration(accId[0], profile);
			pjsua.acc_set_registration(accId[0], 1);
			if (status == pjsuaConstants.PJ_SUCCESS) {
				SipProfileState ps = new SipProfileState(profile);
				ps.setAddedStatus(status);
				ps.setPjsuaId(accId[0]);
				profile.setPjsuaId(accId[0]);
				profile.setSipProfileState(ps);
				pjsua.acc_set_online_status(accId[0], 1);
			}
		}
		return status == pjsuaConstants.PJ_SUCCESS;
	}

	/**
	 * public boolean addAccount(SipProfile profile) { int status =
	 * pjsuaConstants.PJ_FALSE; if (!created) { WulianLog.e(THIS_FILE,
	 * "PJSIP is not started here, nothing can be done"); return status ==
	 * pjsuaConstants.PJ_SUCCESS; }
	 * 
	 * pjsua_acc_config acc_cfg = new pjsua_acc_config();
	 * 
	 * pjsua.acc_config_default(acc_cfg);
	 * 
	 * 
	 * 
	 * 
	 * acc_cfg.setId(pjsua.pj_str_copy("sip:" +profile.getSipUserName() +
	 * "@wuliangroup.cn"));
	 * acc_cfg.setReg_uri(pjsua.pj_str_copy("sip:wuliangroup.cn:8060"));
	 * 
	 * acc_cfg.setCred_count(1);
	 * 
	 * pjsip_cred_info cred_info = acc_cfg.getCred_info();
	 * cred_info.setRealm(pjsua.pj_str_copy("*"));
	 * cred_info.setScheme(pjsua.pj_str_copy("Digest"));
	 * cred_info.setUsername(pjsua.pj_str_copy(profile.getSipUserName()));
	 * cred_info.setData_type(0);
	 * cred_info.setData(pjsua.pj_str_copy(profile.getPassword()));
	 * 
	 * pjsua_transport_config rtpCfg = acc_cfg.getRtp_cfg();
	 * rtpCfg.setPort(4000);
	 * 
	 * acc_cfg.setVid_in_auto_show(pjsuaConstants.PJ_TRUE);
	 * acc_cfg.setVid_out_auto_transmit(pjsuaConstants.PJ_TRUE); // int[] accId
	 * = new int[1]; // status = pjsua.acc_add(acc_cfg, pjsuaConstants.PJ_TRUE,
	 * accId); // // status = pjsua.acc_set_online_status(accId[0], 1); // //
	 * /**
	 * 
	 * // PjSipAccount account = new PjSipAccount(profile); //
	 * account.applyExtraParams(context); SipProfileState currentAccountStatus =
	 * profile.getSipProfileState(); //
	 * acc_cfg.setRegister_on_acc_add(pjsuaConstants.PJ_FALSE); if
	 * (currentAccountStatus != null && currentAccountStatus.isAddedToStack()) {
	 * // pjsua.csipsimple_set_acc_user_data( //
	 * currentAccountStatus.getPjsuaId(), account.css_cfg); status =
	 * pjsua.acc_modify(currentAccountStatus.getPjsuaId(), acc_cfg); // void
	 * beforeAccountRegistration(int pjId, SipProfile profile)
	 * profile.getSipProfileState().setAddedStatus(status); if (status ==
	 * pjsuaConstants.PJ_SUCCESS) { status = pjsua.acc_set_registration(
	 * currentAccountStatus.getPjsuaId(), 1); if (status ==
	 * pjsuaConstants.PJ_SUCCESS) { pjsua.acc_set_online_status(
	 * currentAccountStatus.getPjsuaId(), 1); } } } else { int[] accId = new
	 * int[1]; status = pjsua.acc_add(acc_cfg, pjsuaConstants.PJ_TRUE, accId);
	 * // pjsua.csipsimple_set_acc_user_data(accId[0], account.css_cfg);
	 * 
	 * beforeAccountRegistration(accId[0], profile);
	 * pjsua.acc_set_registration(accId[0], 1); if (status ==
	 * pjsuaConstants.PJ_SUCCESS) { SipProfileState ps = new
	 * SipProfileState(profile); ps.setAddedStatus(status);
	 * ps.setPjsuaId(accId[0]); profile.setSipProfileState(ps);
	 * pjsua.acc_set_online_status(accId[0], 1); } } return status ==
	 * pjsuaConstants.PJ_SUCCESS; }
	 **/
	/**
	 * Send sms/message using SIP server
	 */
	public SipToCall sendMessage1(String callee, String message,
			long accountId, SipProfile profile) {
		if (!created) {
			return null;
		}
		WulianLog.d("PML", "sendMessage callee is:" + callee);
		SipToCall toCall = sanitizeSipUri(callee, accountId, profile);
		if (toCall != null) {
			pj_str_t uri = pjsua.pj_str_copy(toCall.getCallee());
			pj_str_t text = pjsua.pj_str_copy(message);
			pj_str_t mime_type = pjsua.pj_str_copy(Configproperty.MESSAGE_TYPE);

			/*
			 * Log.d(THIS_FILE, "get for outgoing"); int finalAccountId =
			 * accountId; if (accountId == -1) { finalAccountId =
			 * pjsua.acc_find_for_outgoing(uri); }
			 */
			// Nothing to do with this values
			// byte[] userData = new byte[1];
			// pjsua.call_send_request(call_id, method, msg_data)
			int status = pjsua.im_send(toCall.getPjsipAccountId(), uri,
					mime_type, text, null, null);
			return (status == pjsuaConstants.PJ_SUCCESS) ? toCall : null;
		}
		return toCall;
	}

	public boolean sendMessage(String callee, String message, long accountId,
			SipProfile profile) {
		if (!created) {
			return false;
		}
		WulianLog.d("PML", "sendMessage callee is:" + callee);
		int pjsipAccountId = (int) SipProfile.INVALID_ID;
		if (profile.getPjsuaId() >= 0) {
			pjsipAccountId = profile.getPjsuaId();
		}
		if (!TextUtils.isEmpty(callee) && pjsipAccountId >= 0) {
			String transportType = "udp";
			switch (profile.transport) {
			case SipProfile.TRANSPORT_UDP:
				transportType = "udp";
				break;
			case SipProfile.TRANSPORT_TCP:
				transportType = "tcp";
				break;
			case SipProfile.TRANSPORT_TLS:
				transportType = "tls";
				break;
			default:
				break;
			}
			String transportParams = ";transport=" + transportType;
			String finalCallee = "<sip:"
					+ callee
					+ ":"
					+ String.valueOf(WulianDefaultPreference.getsSipTransport())
					+ transportParams + ">";
			pj_str_t uri = pjsua.pj_str_copy(finalCallee);
			pj_str_t text = pjsua.pj_str_copy(message);
			pj_str_t mime_type = pjsua.pj_str_copy(Configproperty.MESSAGE_TYPE);

			/*
			 * Log.d(THIS_FILE, "get for outgoing"); int finalAccountId =
			 * accountId; if (accountId == -1) { finalAccountId =
			 * pjsua.acc_find_for_outgoing(uri); }
			 */
			// Nothing to do with this values
			// byte[] userData = new byte[1];
			// pjsua.call_send_request(call_id, method, msg_data)
			int status = pjsua.im_send(pjsipAccountId, uri, mime_type, text,
					null, null);
			return (status == pjsuaConstants.PJ_SUCCESS) ? true : false;
		}
		return false;
	}

	public boolean sendLocalMessage(String remoteIP, String message, Bundle b) {
		if (!created) {
			return false;
		}

		String transportType = "tcp";
		switch (WulianDefaultPreference.getsDefaultLocalTransport()) {
		case SipProfile.TRANSPORT_UDP:
			transportType = "udp";
			break;
		case SipProfile.TRANSPORT_TCP:
			transportType = "tcp";
			break;
		case SipProfile.TRANSPORT_TLS:
			transportType = "tls";
			break;
		default:
			break;
		}
		String transportParams = ";transport=" + transportType;

		String finalCallee = "<sip:" + remoteIP + transportParams + ">";
		pjsua_msg_data msgData = new pjsua_msg_data();
		pj_pool_t pool = pjsua.pool_create("call_tmp", 512, 512);
		pjsua.msg_data_init(msgData);
		if (b != null) {
			Bundle extraHeaders = b
					.getBundle(SipCallSession.OPT_MSG_EXTRA_HEADERS);
			if (extraHeaders != null) {
				for (String key : extraHeaders.keySet()) {
					try {
						String value = extraHeaders.getString(key);
						if (!TextUtils.isEmpty(value)) {
							int res = pjsua.csipsimple_msg_data_add_string_hdr(
									pool, msgData, pjsua.pj_str_copy(key),
									pjsua.pj_str_copy(value));
							if (res == pjsuaConstants.PJ_SUCCESS) {
								Log.e(THIS_FILE, "Failed to add Xtra hdr ("
										+ key + " : " + value
										+ ") probably not X- header");
							}
						}
					} catch (Exception e) {
						Log.e(THIS_FILE, "Invalid header value for key : "
								+ key);
					}
				}
			}
		}
		if (!TextUtils.isEmpty(finalCallee) && pjsipTcpId >= 0) {
			pj_str_t uri = pjsua.pj_str_copy(finalCallee);
			pj_str_t text = pjsua.pj_str_copy(message);
			pj_str_t mime_type = pjsua.pj_str_copy(Configproperty.MESSAGE_TYPE);
			int status = pjsua.im_send(pjsipTcpId, uri, mime_type, text,
					msgData, null);
			pjsua.pj_pool_release(pool);
			return (status == pjsuaConstants.PJ_SUCCESS) ? true : false;
		}
		return false;
	}

	public boolean sendLocalInfo(String callee, String info, int callId,
			Bundle b) {
		if (!created) {
			return false;
		}
		pjsua_msg_data msgData = new pjsua_msg_data();
		pj_pool_t pool = pjsua.pool_create("call_tmp", 512, 512);
		pjsua.msg_data_init(msgData);
		if (b != null) {
			Bundle extraHeaders = b
					.getBundle(SipCallSession.OPT_MSG_EXTRA_HEADERS);
			if (extraHeaders != null) {
				for (String key : extraHeaders.keySet()) {
					try {
						String value = extraHeaders.getString(key);
						if (!TextUtils.isEmpty(value)) {
							int res = pjsua.csipsimple_msg_data_add_string_hdr(
									pool, msgData, pjsua.pj_str_copy(key),
									pjsua.pj_str_copy(value));
							if (res == pjsuaConstants.PJ_SUCCESS) {
								Log.e(THIS_FILE, "Failed to add Xtra hdr ("
										+ key + " : " + value
										+ ") probably not X- header");
							}
						}
					} catch (Exception e) {
						Log.e(THIS_FILE, "Invalid header value for key : "
								+ key);
					}
				}
			}
		}
		pj_str_t SIP_INFO = pjsua.pj_str_copy(Configproperty.SIP_INFO);
		pj_str_t content_type = pjsua.pj_str_copy(Configproperty.MESSAGE_TYPE);
		pj_str_t msg_body = pjsua.pj_str_copy(info);
		msgData.setContent_type(content_type);
		msgData.setMsg_body(msg_body);
		int status = pjsua.call_send_request(callId, SIP_INFO, msgData);
		pjsua.pj_pool_release(pool);
		return (status == pjsuaConstants.PJ_SUCCESS) ? true : false;
	}

	/* 使用INFO 发送xml信息 */
	public SipToCall sendInfo1(String callee, String info, int callId,
			SipProfile profile) {
		if (!created) {
			return null;
		}
		WulianLog.d("PML", "sendInfo is:" + callee);
		SipToCall toCall = sanitizeSipUri(callee, profile.id, profile);
		if (toCall != null) {
			pjsua_msg_data msgData = new pjsua_msg_data();
			pj_str_t SIP_INFO = pjsua.pj_str_copy(Configproperty.SIP_INFO);

			pjsua.msg_data_init(msgData);
			pj_str_t content_type = pjsua
					.pj_str_copy(Configproperty.MESSAGE_TYPE);
			pj_str_t msg_body = pjsua.pj_str_copy(info);
			msgData.setContent_type(content_type);
			msgData.setMsg_body(msg_body);
			int status = pjsua.call_send_request(callId, SIP_INFO, msgData);
			return (status == pjsuaConstants.PJ_SUCCESS) ? toCall : null;
		}
		return toCall;
	}

	/* 使用INFO 发送xml信息 */
	public boolean sendInfo(String callee, String info, int callId,
			SipProfile profile) {
		if (!created) {
			return false;
		}
		WulianLog.d("PML", "sendInfo is:" + callee);

		if (callId >= 0) {
			// String finalCallee = "<sip:" + callee + ":8060;transport=udp>";
			pjsua_msg_data msgData = new pjsua_msg_data();
			pj_str_t SIP_INFO = pjsua.pj_str_copy(Configproperty.SIP_INFO);

			pjsua.msg_data_init(msgData);
			pj_str_t content_type = pjsua
					.pj_str_copy(Configproperty.MESSAGE_TYPE);
			pj_str_t msg_body = pjsua.pj_str_copy(info);
			msgData.setContent_type(content_type);
			msgData.setMsg_body(msg_body);
			int status = pjsua.call_send_request(callId, SIP_INFO, msgData);
			return (status == pjsuaConstants.PJ_SUCCESS) ? true : false;
		}
		return false;
	}

	/**
	 * 挂断一个电话，相当于断掉一个终端
	 * 
	 * @param callId
	 *            the id of the call to hangup
	 * @param code
	 *            the status code to send in the response
	 * @return
	 */
	public boolean callHangup(int callId, int code) {
		WulianLog
				.d("PML", "callHangup is:" + callId + ";created is:" + created);
		if (created) {
			int status = pjsua.call_hangup(callId, code, null, null);
			return status == pjsuaConstants.PJ_SUCCESS;
		}
		if (userAgentReceiver != null) {
			userAgentReceiver.clearCallList();
		}
		return false;
	}

	// 挂断所有电话
	public void callHangupAll() {
		WulianLog.d("PML", "call all Hangup" + ";created is:" + created);
		if (created) {
			pjsua.call_hangup_all();
		}
		if (userAgentReceiver != null) {
			userAgentReceiver.clearCallList();
		}
	}

	public boolean callUpdate(int call_id) {
		pjsua_msg_data msgData = new pjsua_msg_data();
		pjsua.msg_data_init(msgData);
		int status = pjsua.call_update(call_id, 0, null);
		return status == pjsuaConstants.PJ_SUCCESS ? true : false;
	}

	// 错误类型:
	// 0 代表未创建
	// 1 代表局域网创建失败
	// 2 代表局域网的IP格式不对
	// 3 代表拨打失败
	// 200 代表拨打成功
	public int makeLocalCall(String remoteIP, Bundle b) {
		if (!created) {
			return 0;
		}
		if (pjsipTcpId == -1) {
			return 1;
		}
		pjsua.acc_set_default(pjsipTcpId);
		String finalCallee = "";

		String transportType = "tcp";
		switch (WulianDefaultPreference.getsDefaultLocalTransport()) {
		case SipProfile.TRANSPORT_UDP:
			transportType = "udp";
			break;
		case SipProfile.TRANSPORT_TCP:
			transportType = "tcp";
			break;
		case SipProfile.TRANSPORT_TLS:
			transportType = "tls";
			break;
		default:
			break;
		}
		String transportParams = ";transport=" + transportType;

		finalCallee = "<sip:" + remoteIP + transportParams + ">";

		WulianLog.d("PML", "make a local call... callee is:" + finalCallee);
		// 检测URL
		if (pjsua.verify_sip_url(finalCallee) != 0) {
			return 2;
		}

		pj_str_t uri = pjsua.pj_str_copy(finalCallee);
		// Nothing to do with this values
		byte[] userData = new byte[1];
		int[] callId = new int[1];
		pjsua_call_setting cs = new pjsua_call_setting();
		pjsua_msg_data msgData = new pjsua_msg_data();
		int pjsuaAccId = pjsipTcpId;
		// pjsua.set_ec(WulianDefaultPreference.getsEchoCancelLationTail(),
		// WulianDefaultPreference.getsEchoMode());
		// Call settings to add video
		pjsua.call_setting_default(cs);
		cs.setAud_cnt(1);
		cs.setVid_cnt(1);
		cs.setFlag(0);
		pj_pool_t pool = pjsua.pool_create("call_tmp", 512, 512);

		// Msg data to add headers
		pjsua.msg_data_init(msgData);
		pjsua.csipsimple_init_acc_msg_data(pool, pjsuaAccId, msgData);
		if (b != null) {
			Bundle extraHeaders = b
					.getBundle(SipCallSession.OPT_CALL_EXTRA_HEADERS);
			if (extraHeaders != null) {
				for (String key : extraHeaders.keySet()) {
					try {
						String value = extraHeaders.getString(key);
						if (!TextUtils.isEmpty(value)) {
							int res = pjsua.csipsimple_msg_data_add_string_hdr(
									pool, msgData, pjsua.pj_str_copy(key),
									pjsua.pj_str_copy(value));
							if (res == pjsuaConstants.PJ_SUCCESS) {
								Log.e(THIS_FILE, "Failed to add Xtra hdr ("
										+ key + " : " + value
										+ ") probably not X- header");
							}
						}
					} catch (Exception e) {
						Log.e(THIS_FILE, "Invalid header value for key : "
								+ key);
					}
				}
			}
		}
		int status = pjsua.call_make_call(pjsuaAccId, uri, cs, userData,
				msgData, callId);
		pjsua.pj_pool_release(pool);
		return status == pjsuaConstants.PJ_SUCCESS ? 200 : 3;
	}

	public boolean makeCall1(String callee, int accountId, Bundle b,
			SipProfile profile) {
		if (!created) {
			return false;
		}
		WulianLog.d("PML", "make a call... callee is:" + callee
				+ ";accountId is:" + accountId);
		final SipToCall toCall = sanitizeSipUri(callee, accountId, profile);
		if (toCall != null) {
			WulianLog.d("PML", "makeCall :callee " + callee + ";accountId "
					+ accountId + ";getCallee " + toCall.getCallee()
					+ ";getPjsipAccountId" + toCall.getPjsipAccountId());
			pj_str_t uri = pjsua.pj_str_copy(toCall.getCallee());
			// Nothing to do with this values
			byte[] userData = new byte[1];
			int[] callId = new int[1];
			pjsua_call_setting cs = new pjsua_call_setting();
			pjsua_msg_data msgData = new pjsua_msg_data();
			int pjsuaAccId = toCall.getPjsipAccountId();
			// pjsua.set_ec(WulianDefaultPreference.getsEchoCancelLationTail(),
			// WulianDefaultPreference.getsEchoMode());
			// Call settings to add video
			pjsua.call_setting_default(cs);
			cs.setAud_cnt(1);
			cs.setVid_cnt(1);
			// if (b != null && b.getBoolean(SipCallSession.OPT_CALL_VIDEO,
			// true)) {
			// cs.setVid_cnt(1);
			// }
			cs.setFlag(0);
			pj_pool_t pool = pjsua.pool_create("call_tmp", 512, 512);

			// Msg data to add headers
			pjsua.msg_data_init(msgData);
			pjsua.csipsimple_init_acc_msg_data(pool, pjsuaAccId, msgData);
			if (b != null) {
				Bundle extraHeaders = b
						.getBundle(SipCallSession.OPT_CALL_EXTRA_HEADERS);
				if (extraHeaders != null) {
					for (String key : extraHeaders.keySet()) {
						try {
							String value = extraHeaders.getString(key);
							if (!TextUtils.isEmpty(value)) {
								int res = pjsua
										.csipsimple_msg_data_add_string_hdr(
												pool, msgData,
												pjsua.pj_str_copy(key),
												pjsua.pj_str_copy(value));
								if (res == pjsuaConstants.PJ_SUCCESS) {
									Log.e(THIS_FILE, "Failed to add Xtra hdr ("
											+ key + " : " + value
											+ ") probably not X- header");
								}
							}
						} catch (Exception e) {
							Log.e(THIS_FILE, "Invalid header value for key : "
									+ key);
						}
					}
				}
			}

			WulianLog.d("PML", "call_make_call " + pjsuaAccId + ";");
			int status = pjsua.call_make_call(pjsuaAccId, uri, cs, userData,
					msgData, callId);
			if (status == pjsuaConstants.PJ_SUCCESS) {
				WulianLog.d(THIS_FILE, "DTMF - Store for " + callId[0] + " - ");
			}
			pjsua.pj_pool_release(pool);
			return status == pjsuaConstants.PJ_SUCCESS ? true : false;
		}
		// pjsua.call_update(call_id, options, msg_data)
		return false;
	}

	public boolean makeCall(String callee, int accountId, Bundle b,
			SipProfile profile) {
		if (!created) {
			return false;
		}
		WulianLog.d("PML", "make a call... callee is:" + callee
				+ ";accountId is:" + accountId);
		int pjsipAccountId = (int) SipProfile.INVALID_ID;
		if (profile.getPjsuaId() >= 0) {
			pjsipAccountId = profile.getPjsuaId();
		}
		if (!TextUtils.isEmpty(callee) && pjsipAccountId >= 0) {
			// String finalCallee = "<sip:" + callee + ":8060;transport=udp>";
			String transportType = "udp";
			switch (profile.transport) {
			case SipProfile.TRANSPORT_UDP:
				transportType = "udp";
				break;
			case SipProfile.TRANSPORT_TCP:
				transportType = "tcp";
				break;
			case SipProfile.TRANSPORT_TLS:
				transportType = "tls";
				break;
			default:
				break;
			}
			String transportParams = ";transport=" + transportType;
			String finalCallee = "<sip:"
					+ callee
					+ ":"
					+ String.valueOf(WulianDefaultPreference.getsSipTransport())
					+ transportParams + ">";
			WulianLog.d("PML", "makeCall :callee " + callee + ";accountId "
					+ accountId + ";getCallee " + finalCallee
					+ ";getPjsipAccountId" + pjsipAccountId);
			pj_str_t uri = pjsua.pj_str_copy(finalCallee);
			// Nothing to do with this values
			byte[] userData = new byte[1];
			int[] callId = new int[1];
			pjsua_call_setting cs = new pjsua_call_setting();
			pjsua_msg_data msgData = new pjsua_msg_data();
			int pjsuaAccId = pjsipAccountId;
			// pjsua.set_ec(WulianDefaultPreference.getsEchoCancelLationTail(),
			// WulianDefaultPreference.getsEchoMode());
			// Call settings to add video
			pjsua.call_setting_default(cs);
			cs.setAud_cnt(1);
			cs.setVid_cnt(1);
			// if (b != null && b.getBoolean(SipCallSession.OPT_CALL_VIDEO,
			// true)) {
			// cs.setVid_cnt(1);
			// }
			cs.setFlag(0);
			pj_pool_t pool = pjsua.pool_create("call_tmp", 512, 512);

			// Msg data to add headers
			pjsua.msg_data_init(msgData);
			pjsua.csipsimple_init_acc_msg_data(pool, pjsuaAccId, msgData);
			if (b != null) {
				Bundle extraHeaders = b
						.getBundle(SipCallSession.OPT_CALL_EXTRA_HEADERS);
				if (extraHeaders != null) {
					for (String key : extraHeaders.keySet()) {
						try {
							String value = extraHeaders.getString(key);
							if (!TextUtils.isEmpty(value)) {
								int res = pjsua
										.csipsimple_msg_data_add_string_hdr(
												pool, msgData,
												pjsua.pj_str_copy(key),
												pjsua.pj_str_copy(value));
								if (res == pjsuaConstants.PJ_SUCCESS) {
									Log.e(THIS_FILE, "Failed to add Xtra hdr ("
											+ key + " : " + value
											+ ") probably not X- header");
								}
							}
						} catch (Exception e) {
							Log.e(THIS_FILE, "Invalid header value for key : "
									+ key);
						}
					}
				}
			}

			WulianLog.d("PML", "call_make_call " + pjsuaAccId + ";");
			int status = pjsua.call_make_call(pjsuaAccId, uri, cs, userData,
					msgData, callId);
			if (status == pjsuaConstants.PJ_SUCCESS) {
				WulianLog.d(THIS_FILE, "DTMF - Store for " + callId[0] + " - ");
			}
			pjsua.pj_pool_release(pool);
			return status == pjsuaConstants.PJ_SUCCESS ? true : false;
		}

		// pjsua.call_update(call_id, options, msg_data)
		return false;
	}

	public long getSipCountCall() {
		return pjsua.call_get_count();
	}

	// 获取通话具体信息
	public String getCallInfos(int callId) {
		String infos = "";
		if (callId != -1 && WulianLog.getLogLevel() >= 5) {
			infos = PjSipCalls.dumpCallInfo(callId);
			WulianLog.d(THIS_FILE, infos);
		}
		return infos;
	}

	// 获取Nat 类型
	public void detectNatType() {
		// int[] type=new int[0];
		WulianLog.d(THIS_FILE, "before detectNatType");
		if (created) {
			WulianLog.d(THIS_FILE, "detectNatType");
			int status = pjsua.detect_nat_type();
			WulianLog.d(THIS_FILE, "detectNatType status is:" + status);
		}
	}

	public String getPjsipInfo(int callId) {
		String SessionId = "";
		pjsua_call_info pjInfo = new pjsua_call_info();
		int status = pjsua.call_get_info(callId, pjInfo);
		if (status == pjsua.PJ_SUCCESS) {
			SessionId = PjSipService.pjStrToString(pjInfo.getCall_id());
		}
		return SessionId;
	}

	// 获取通话具体信息
	public String getCallSpeedInfos(int callId) {
		String infos = "";
		if (callId != -1) {
			infos = PjSipCalls.dumpCallSpeedInfo(callId);
			WulianLog.d(THIS_FILE, infos);
		}
		return infos;
	}

	// 获取通话具体信息
	public String getCallNatInfos(int callId) {
		String infos = "";
		if (callId != -1) {
			infos = PjSipCalls.dumpCallNatInfo(callId);
			WulianLog.d(THIS_FILE, infos);
		}
		return infos;
	}

	// 绑定
	public int addBuddy(String buddyUri) {
		if (!created) {
			return -1;
		}
		int[] p_buddy_id = new int[1];

		pjsua_buddy_config buddy_cfg = new pjsua_buddy_config();
		pjsua.buddy_config_default(buddy_cfg);
		buddy_cfg.setSubscribe(1);
		buddy_cfg.setUri(pjsua.pj_str_copy(buddyUri));

		pjsua.buddy_add(buddy_cfg, p_buddy_id);

		return p_buddy_id[0];
	}

	// 删除绑定
	public void removeBuddy(String buddyUri) {
		if (!created) {
			return;
		}
		int buddyId = pjsua.buddy_find(pjsua.pj_str_copy(buddyUri));
		if (buddyId >= 0) {
			pjsua.buddy_del(buddyId);
		}
	}

	public boolean isWiredHeadsetOn() {
		if (mediaManager != null) {
			return this.mediaManager.isWiredHeadsetOn();
		}
		return false;
	}

	public int getCallStream() {
		if (mediaManager != null) {
			return this.mediaManager.getCallStream();
		}
		return -1;
	}

	public void AdjustCurrentVolume() {
		if (mediaManager != null) {
			this.mediaManager.AdjustCurrentVolume();
		}
	}

	public void confAdjustTxLevel(int port, float value) {
		if (created && userAgentReceiver != null) {
			pjsua.conf_adjust_tx_level(port, value);
		}
	}

	public void confAdjustRxLevel(int port, float value) {
		if (created && userAgentReceiver != null) {
			pjsua.conf_adjust_rx_level(port, value);
		}
	}

	public void setEchoCancellation(boolean on) {
		if (created && userAgentReceiver != null) {
			WulianLog.d(THIS_FILE, "set echo cancelation " + on);
			pjsua.set_ec(
					on ? WulianDefaultPreference.getsEchoCancelLationTail() : 0,
					WulianDefaultPreference.getsEchoMode());
		}
	}

	public boolean setAccountRegistration(SipProfile account, int renew,
			boolean forceReAdd) {
		int status = -1;
		if (!created || account == null) {
			WulianLog.e(THIS_FILE,
					"PJSIP is not started here, nothing can be done");
			return false;
		}
		if (account.id == SipProfile.INVALID_ID) {
			Log.w(THIS_FILE, "Trying to set registration on a deleted account");
			return false;
		}
		SipProfileState profileState = getProfileState(account);
		if (profileState != null && profileState.isAddedToStack()) {
			if (renew == 1) {
				if (forceReAdd) {
					status = pjsua.acc_del(profileState.getPjsuaId());
					addAccount(account);
				} else {
					pjsua.acc_set_online_status(profileState.getPjsuaId(),
							getOnlineForStatus(PresenceStatus.ONLINE));
					status = pjsua.acc_set_registration(
							profileState.getPjsuaId(), renew);
				}
			} else {
				// if(status == pjsuaConstants.PJ_SUCCESS && renew == 0) {
				WulianLog.d(THIS_FILE, "Delete account !!");
				status = pjsua.acc_del(profileState.getPjsuaId());
			}
		} else {
			if (renew == 1) {
				addAccount(account);
			} else {
				WulianLog.w(THIS_FILE,
						"Ask to unregister an unexisting account !!"
								+ account.id);
			}
		}
		return status == pjsuaConstants.PJ_SUCCESS;
	}

	public void setPresence(PresenceStatus presence, long accountId) {
		if (!created) {
			WulianLog.e(THIS_FILE,
					"PJSIP is not started here, nothing can be done");
			return;
		}
		SipProfile account = new SipProfile();
		account.id = accountId;
		SipProfileState profileState = getProfileState(account);
		if (profileState != null && profileState.isAddedToStack()) {
			// The account is already there in accounts list
			pjsua.acc_set_online_status(profileState.getPjsuaId(),
					getOnlineForStatus(presence));
		}
	}

	@SuppressWarnings("deprecation")
	private SipProfileState getProfileState(SipProfile account) {
		if (!created || account == null) {
			return null;
		}
		if (account.id == SipProfile.INVALID_ID) {
			return null;
		}
		SipProfileState accountInfo = new SipProfileState(account);
		// if (profileState != null) {
		// accountInfo.createFromBefore(profileState);
		// }
		return accountInfo;
	}

	private int getOnlineForStatus(PresenceStatus presence) {
		return presence == PresenceStatus.ONLINE ? 1 : 0;
	}

	// public void setConnectStateChange(boolean is_wifi, boolean is_mobile) {
	// this.is_wifi = is_wifi;
	// this.is_mobile = is_mobile;
	// }

	private int getTcpKeepAliveInterval() {
		// if (is_wifi) {
		// return WulianDefaultPreference.getsTcpKeepAliveIntervalWIFI();
		// } else {
		// return WulianDefaultPreference.getsTcpKeepAliveIntervalMobile();
		// }
		return pref.getTcpKeepAliveInterval();
	}

	private int getTlsKeepAliveInterval() {
		// if (is_wifi) {
		// return WulianDefaultPreference.getsTlsKeepAliveIntervalWIFI();
		// } else {
		// return WulianDefaultPreference.getsTlsKeepAliveIntervalMobile();
		// }
		return pref.getTlsKeepAliveInterval();
	}

	public void setUserCalist(String calist, String cert, String privkey) {
		WulianDefaultPreference.setsCaListFile(calist);
		WulianDefaultPreference.setsCertFile(calist);
		WulianDefaultPreference.setsPrivKeyFile(privkey);
	}

	/**
	 * Transform a string callee into a valid sip uri in the context of an
	 * account
	 * 
	 * @param callee
	 *            the callee string to call
	 * @param accountId
	 *            the context account
	 * @return ToCall object representing what to call and using which account
	 */
	private SipToCall sanitizeSipUri(String callee, long accountId,
			SipProfile account) {
		// accountId is the id in term of csipsimple database
		// pjsipAccountId is the account id in term of pjsip adding
		int pjsipAccountId = (int) SipProfile.INVALID_ID;

		// Fake a sip profile empty to get it's profile state
		// Real get from db will be done later
		// SipProfile account = new SipProfile();
		// account.id = accountId;
		SipProfileState profileState = account.getSipProfileState();
		// long finalAccountId = accountId;
		// If the account is valid
		pjsipAccountId = profileState.getPjsuaId();

		if (pjsipAccountId == SipProfile.INVALID_ID) {
			WulianLog.e(THIS_FILE,
					"Unable to find a valid account for this call");
			return null;
		}
		// TODO:temp fix bug
		WulianLog.d("PML", "The transport is:" + account.transport);
		// switch (account.transport) {
		// case SipProfile.TRANSPORT_UDP:
		// callee += ":8060";
		// break;
		// case SipProfile.TRANSPORT_TCP:
		// callee += ":8060";
		// break;
		// case SipProfile.TRANSPORT_TLS:
		// callee += ":8061";
		// break;
		// default:
		// break;
		// }
		// if (!callee.contains(":")) {
		// callee += ":8061";
		// }
		// if (callee.contains(":")) {
		// callee += ";transport=udp";
		// } else {
		// callee +=
		// ":"+String.valueOf(WulianDefaultPreference.getsSipTransport())+";transport=udp";
		// }
		String transportType = "udp";
		switch (account.transport) {
		case SipProfile.TRANSPORT_UDP:
			transportType = "udp";
			break;
		case SipProfile.TRANSPORT_TCP:
			transportType = "tcp";
			break;
		case SipProfile.TRANSPORT_TLS:
			transportType = "tls";
			break;
		default:
			break;
		}
		String transportParams = ";transport=" + transportType;
		if (callee.contains(":")) {
			callee += transportParams;
		} else {
			callee += ":"
					+ String.valueOf(WulianDefaultPreference.getsSipTransport())
					+ transportParams;
		}

		WulianLog.d("PML", "call is:" + callee);
		ParsedSipContactInfos finalCallee = account.formatCalleeNumber(callee);
		// String digitsToAdd = null;
		if (!TextUtils.isEmpty(finalCallee.userName)
				&& (finalCallee.userName.contains(",") || finalCallee.userName
						.contains(";"))) {
			int commaIndex = finalCallee.userName.indexOf(",");
			int semiColumnIndex = finalCallee.userName.indexOf(";");
			if (semiColumnIndex > 0 && semiColumnIndex < commaIndex) {
				commaIndex = semiColumnIndex;
			}
			// digitsToAdd = finalCallee.userName.substring(commaIndex);
			finalCallee.userName = finalCallee.userName
					.substring(0, commaIndex);
		}

		WulianLog.d(THIS_FILE, "will call " + finalCallee);

		if (pjsua.verify_sip_url(finalCallee.toString(false)) == 0) {
			// In worse worse case, find back the account id for uri.. but
			// probably useless case
			if (pjsipAccountId == SipProfile.INVALID_ID) {
				pjsipAccountId = pjsua.acc_find_for_outgoing(pjsua
						.pj_str_copy(finalCallee.toString(false)));
			}
			return new SipToCall(pjsipAccountId, finalCallee.toString(true));
		}

		return null;
	}

	private pjmedia_srtp_use getUseSrtp() {
		try {
			int use_srtp = WulianDefaultPreference.getsUseSrtp();
			if (use_srtp >= 0) {
				return pjmedia_srtp_use.swigToEnum(use_srtp);
			}
		} catch (NumberFormatException e) {
			WulianLog.e(THIS_FILE, "Transport port not well formated");
		}
		return pjmedia_srtp_use.PJMEDIA_SRTP_DISABLED;
	}

	private static ArrayList<String> codecs = new ArrayList<String>();
	private static ArrayList<String> video_codecs = new ArrayList<String>();
	private static boolean codecs_initialized = false;

	/**
	 * Reset the list of codecs stored
	 */
	public static void resetCodecs() {
		synchronized (codecs) {
			if (codecs_initialized) {
				codecs.clear();
				video_codecs.clear();
				codecs_initialized = false;
			}
		}
	}

	private void initCodecs() {
		synchronized (codecs) {
			if (!codecs_initialized) {
				int nbrCodecs, i;

				// Audio codecs
				nbrCodecs = pjsua.codecs_get_nbr();
				for (i = 0; i < nbrCodecs; i++) {
					String codecId = pjStrToString(pjsua.codecs_get_id(i));
					codecs.add(codecId);
					WulianLog.d(THIS_FILE, "Added codec " + codecId);
				}

				// Video codecs
				nbrCodecs = pjsua.codecs_vid_get_nbr();
				for (i = 0; i < nbrCodecs; i++) {
					String codecId = pjStrToString(pjsua.codecs_vid_get_id(i));
					video_codecs.add(codecId);
					WulianLog.d(THIS_FILE, "Added video codec " + codecId);
				}
				codecs_initialized = true;
			}
		}
	}

	private void setCodecsPriorities(Context context) {
		ConnectivityManager cm = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE));

		synchronized (codecs) {
			if (codecs_initialized) {
				NetworkInfo ni = cm.getActiveNetworkInfo();
				if (ni != null) {

					StringBuilder audioSb = new StringBuilder();
					StringBuilder videoSb = new StringBuilder();
					audioSb.append("Audio codecs : ");
					videoSb.append("Video codecs : ");
					WulianLog.d("PML", "Codecs size is:" + codecs.size());
					String currentBandType = pref.getConnectWifi() ? SipConfigManager.CODEC_WB
							: SipConfigManager.CODEC_NB;

					synchronized (codecs) {

						for (String codec : codecs) {
							short aPrio = Compatibility.getCodecPriority(codec,
									currentBandType, 0);
							buffCodecLog(audioSb, codec, aPrio);
							pj_str_t codecStr = pjsua.pj_str_copy(codec);
							if (aPrio >= 0) {
								pjsua.codec_set_priority(codecStr, aPrio);
							}
						}

						for (String codec : video_codecs) {
							short aPrio = Compatibility.getVideoCodecPriority(
									codec, currentBandType, -1);
							buffCodecLog(videoSb, codec, aPrio);
							if (aPrio >= 0) {
								pjsua.vid_codec_set_priority(
										pjsua.pj_str_copy(codec), aPrio);
							}
							String videoSize = WulianDefaultPreference
									.getsVideoCaptureSize();
							VideoCaptureCapability videoCap = new VideoUtilsWrapper.VideoCaptureCapability(
									videoSize);
							if (codec.startsWith("H264")) {
								int h264profile = WulianDefaultPreference
										.getsH264Profile();
								int h264level = WulianDefaultPreference
										.getsH264Level();
								int h264bitrate = WulianDefaultPreference
										.getsH264BitRate();

								if (h264profile > 0) {
									pjsua.codec_h264_set_profile(h264profile,
											h264level, videoCap.width,
											videoCap.height, videoCap.fps,
											h264bitrate, 0);
									WulianLog.d(THIS_FILE,
											"Set h264 profile : " + h264profile
													+ ", " + h264level + ", "
													+ h264bitrate);
								}
							}
						}
					}
					WulianLog.d(THIS_FILE, audioSb.toString());
					WulianLog.d(THIS_FILE, videoSb.toString());
				}

			}
		}
	}

	public SipCallSession[] getCalls() {
		if (created && userAgentReceiver != null) {
			SipCallSession[] callsInfo = userAgentReceiver.getCalls();
			return callsInfo;
		}
		return new SipCallSession[0];
	}

	private Integer createTransportAndAccount(pjsip_transport_type_e type,
			int port) {
		Integer transportId = createTransport(type, port);
		return transportId;
	}

	private Integer createLocalTransportAndAccount(pjsip_transport_type_e type,
			int port) {
		Integer transportId = createTransport(type, port);
		return createLocalAccount(transportId);
	}

	private Integer createLocalAccount(Integer transportId) {
		if (transportId == null) {
			return null;
		}
		int[] p_acc_id = new int[1];
		pjsua.acc_add_local(transportId, pjsua.PJ_TRUE, p_acc_id);
		return p_acc_id[0];
	}

	/**
	 * Utility to create a transport
	 * 
	 * @return transport id or -1 if failed
	 */
	private Integer createTransport(pjsip_transport_type_e type, int port) {
		pjsua_transport_config cfg = new pjsua_transport_config();
		int[] tId = new int[1];
		int status;
		pjsua.transport_config_default(cfg);
		cfg.setPort(port);

		if (type.equals(pjsip_transport_type_e.PJSIP_TRANSPORT_TLS)) {
			pjsip_tls_setting tlsSetting = cfg.getTls_setting();
			WulianLog.d("PML", "PJSIP_TRANSPORT_TLS ");
			/*
			 * TODO : THIS IS OBSOLETE -- remove from UI String serverName =
			 * prefsWrapper
			 * .getPreferenceStringValue(SipConfigManager.TLS_SERVER_NAME); if
			 * (!TextUtils.isEmpty(serverName)) {
			 * tlsSetting.setServer_name(pjsua.pj_str_copy(serverName)); }
			 */

			// String caListFile = WulianDefaultPreference.getsCaListFile();
			// WulianLog.d("PML", "PJSIP_TRANSPORT_TLS caListFile:" +
			// caListFile);
			// if (!TextUtils.isEmpty(caListFile)) {
			// tlsSetting.setCa_list_file(pjsua.pj_str_copy(caListFile));
			// }
			//
			// String certFile = WulianDefaultPreference.getsCertFile();
			// WulianLog.d("PML", "PJSIP_TRANSPORT_TLS certFile:" + certFile);
			// if (!TextUtils.isEmpty(certFile)) {
			// tlsSetting.setCert_file(pjsua.pj_str_copy(certFile));
			// }
			// String privKey = WulianDefaultPreference.getsPrivKeyFile();
			// WulianLog.d("PML", "PJSIP_TRANSPORT_TLS privKey:" + privKey);
			// if (!TextUtils.isEmpty(privKey)) {
			// tlsSetting.setPrivkey_file(pjsua.pj_str_copy(privKey));
			// }
			//
			// String tlsPwd = WulianDefaultPreference.getsTlsPassword();
			// if (!TextUtils.isEmpty(tlsPwd)) {
			// tlsSetting.setPassword(pjsua.pj_str_copy(tlsPwd));
			// }

			boolean checkClient = WulianDefaultPreference.getsTlsVerifyClient();
			tlsSetting.setVerify_client(checkClient ? 1 : 0);

			tlsSetting.setMethod(pjsip_ssl_method
					.swigToEnum(WulianDefaultPreference.getsTlsMethod()));
			boolean checkServer = WulianDefaultPreference.getsTlsVerifyServer();
			tlsSetting.setVerify_server(checkServer ? 1 : 0);

			tlsSetting.setRequire_client_cert(0);
			tlsSetting.setSockopt_ignore_error(1);
			// tlsSetting.setProto(0);
			cfg.setTls_setting(tlsSetting);
		}

		if (WulianDefaultPreference.getsEnableQos()) {
			WulianLog.d(THIS_FILE, "Activate qos for this transport");
			pj_qos_params qosParam = cfg.getQos_params();
			qosParam.setDscp_val((short) WulianDefaultPreference.getsDscpVal());
			qosParam.setFlags((short) 1); // DSCP
			cfg.setQos_params(qosParam);
		}

		status = pjsua.transport_create(type, cfg, tId);
		if (status != pjsuaConstants.PJ_SUCCESS) {
			String errorMsg = pjStrToString(pjsua.get_error_message(status));
			String msg = "Fail to create transport " + errorMsg + " (" + status
					+ ")";
			WulianLog.e(THIS_FILE, msg);
			if (status == 120098) { /* Already binded */
				WulianLog.e(THIS_FILE, "another_application_use_sip_port");
			}
			return null;
		}
		return tId[0];
	}

	private void cleanPjsua() {
		WulianLog.d(THIS_FILE, "Detroying...");
		long flags = 0;
		if (!pref.getConnectWifi() && !pref.getConnectMobile()) {
			flags = 3;
		}
		pjsua.csipsimple_destroy(flags);
		if (userAgentReceiver != null) {
			userAgentReceiver.stopService();
			userAgentReceiver = null;
		}
		if (mediaManager != null) {
			mediaManager.stopService();
			mediaManager = null;
		}
		// TimerWrapper.destroy();
		created = false;
	}

	public void setVideoAndroidRenderer(int callId, SurfaceView window) {
		// this.view = window;
		pjsua.vid_set_android_renderer(callId, (Object) window);
	}

	public static long getAccountIdForPjsipId(Context ctxt, int pjId) {
		long accId = SipProfile.INVALID_ID;
		// Cursor c = ctxt.getContentResolver().query(
		// SipProfile.ACCOUNT_STATUS_URI, null, null, null, null);
		// if (c != null) {
		// try {
		// c.moveToFirst();
		// do {
		// int pjsuaId = c.getInt(c
		// .getColumnIndex(SipProfileState.PJSUA_ID));
		// Log.d(THIS_FILE, "Found pjsua " + pjsuaId + " searching "
		// + pjId);
		// if (pjsuaId == pjId) {
		// accId = c.getInt(c
		// .getColumnIndex(SipProfileState.ACCOUNT_ID));
		// break;
		// }
		// } while (c.moveToNext());
		// } catch (Exception e) {
		// Log.e(THIS_FILE, "Error on looping over sip profiles", e);
		// } finally {
		// c.close();
		// }
		// }
		return accId;
	}

	private Map<String, PjsipModule> pjsipModules = new HashMap<String, PjsipModule>();

	private void initModules() {
		PjsipModule rModule = new RegHandlerModule();
		pjsipModules.put(RegHandlerModule.class.getCanonicalName(), rModule);

		rModule = new SipClfModule();
		pjsipModules.put(SipClfModule.class.getCanonicalName(), rModule);

		// rModule = new EarlyLockModule();
		// pjsipModules.put(EarlyLockModule.class.getCanonicalName(), rModule);

		for (PjsipModule mod : pjsipModules.values()) {
			mod.setContext(context);
		}
	}

	void beforeAccountRegistration(int pjId, SipProfile profile) {
		for (PjsipModule mod : pjsipModules.values()) {
			mod.onBeforeAccountStartRegistration(pjId, profile);
		}
	}

	public int validateAudioClockRate(int aClockRate) {
		if (mediaManager != null) {
			return mediaManager.validateAudioClockRate(aClockRate);
		}
		return -1;
	}

	public void setAudioInCall(int beforeInit) {
		if (mediaManager != null) {
			mediaManager.setAudioInCall(beforeInit == pjsuaConstants.PJ_TRUE);
		}
	}

	public void unsetAudioInCall() {
		if (mediaManager != null) {
			mediaManager.unsetAudioInCall();
		}
	}

	public void refreshCallMediaState(final int callId) {
		if (created && userAgentReceiver != null) {
			userAgentReceiver.updateCallMediaState(callId);
		}
	}

	// Mute microphone 设置话筒开关（如果关闭将停止发送声音）
	public void setMicrophoneInputEnable(boolean enable, int callId) {
		if (created && mediaManager != null && callId >= 0) {
			pjsua_call_info pjInfo = new pjsua_call_info();
			int status = pjsua.call_get_info(callId, pjInfo);
			if (status == pjsua.PJ_SUCCESS) {
				int ConfPort = pjInfo.getConf_slot();
				if (!enable)
					pjsua.conf_disconnect(0, ConfPort);// 输入
				else
					pjsua.conf_connect(0, ConfPort);// 输入
			}
		}
	}

	// Mute microphone 设置话筒开关（如果关闭将停止发送声音）
	public void setMicrophoneMute(boolean on, int callId) {
		if (created && mediaManager != null && callId >= 0) {
			mediaManager.setMicrophoneMute(on);
			pjsua_call_info pjInfo = new pjsua_call_info();
			int status = pjsua.call_get_info(callId, pjInfo);
			if (status == pjsua.PJ_SUCCESS) {
				int ConfPort = pjInfo.getConf_slot();
				if (on)
					pjsua.conf_disconnect(0, ConfPort);// 输入
				else
					pjsua.conf_connect(0, ConfPort);// 输入
			}
			// pjsip.opus_sdp_rewriter_init
		}
	}

	public void setSpeakerPhone(boolean on) {
		if (created && mediaManager != null) {
			mediaManager.setSpeakerphoneOn(on);
		}
	}

	// Change speaker phone mode 设置扬声器开关（对于摄像头自动对应）
	public void setSpeakerphoneOn(boolean on, int callId) {
		if (created && mediaManager != null && callId >= 0) {
			// mediaManager.setSpeakerphoneOn(false);
			boolean isWiredHeadsetOn = mediaManager.isWiredHeadsetOn();
			if (isWiredHeadsetOn)
				mediaManager.setSpeakerphoneOn(false);
			else
				mediaManager.setSpeakerphoneOn(on);
			pjsua_call_info pjInfo = new pjsua_call_info();
			int status = pjsua.call_get_info(callId, pjInfo);
			if (status == pjsua.PJ_SUCCESS) {
				int ConfPort = pjInfo.getConf_slot();
				if (!on)
					pjsua.conf_disconnect(ConfPort, 0);// 输出
				else
					pjsua.conf_connect(ConfPort, 0);// 输出
			}
		}
	}

	// Change speaker phone mode
	public void setMediaMicroOne(float micVolume) {
		if (created && mediaManager != null) {
			mediaManager.setMediaMicrophoneMute(micVolume);
		}
	}

	public void closeAudioTransport(int callId) {
		pjsua_call_info pjInfo = new pjsua_call_info();
		int status = pjsua.call_get_info(callId, pjInfo);
		if (status == pjsua.PJ_SUCCESS) {
			int ConfPort = pjInfo.getConf_slot();
			pjsua.conf_disconnect(0, ConfPort);// 输入
			pjsua.conf_disconnect(ConfPort, 0);// 输出
		}
	}

	// Change speaker phone mode
	public void setMediaSpeakerOne(float speakVolume) {
		if (created && mediaManager != null) {
			mediaManager.setMediaSpeakerphoneOn(speakVolume);
		}
	}

	public void sendRtp(int callId) {
		if (callId != -1 && created && userAgentReceiver != null) {
			userAgentReceiver.sendRtp(callId);
		}
	}

	public static String pjStrToString(pj_str_t pjStr) {
		try {
			if (pjStr != null) {
				// If there's utf-8 ptr length is possibly lower than slen
				int len = pjStr.getSlen();
				WulianLog.d("PML", "pjStrToString len is:" + len);
				if (len > 0) {
					String resultStr = pjStr.getPtr();
					if (resultStr != null) {
						// Be robust to smaller length detected
						if (pjStr.getPtr().length() < len) {
							len = pjStr.getPtr().length();
						}
						if (len > 0) {
							String result = resultStr.substring(0, len);
							WulianLog
									.d("PML", "pjStrToString len is:" + result);
							return result;
						}
					}
				}
			}
		} catch (StringIndexOutOfBoundsException e) {
			WulianLog.e(THIS_FILE, "Impossible to retrieve string from pjsip ",
					e);
		}
		return "";
	}

	private void buffCodecLog(StringBuilder sb, String codec, short prio) {
		if (WulianLog.getLogLevel() >= 4) {
			sb.append(codec);
			sb.append(" (");
			sb.append(prio);
			sb.append(") - ");
		}
	}

	private static int boolToPjsuaConstant(boolean v) {
		return v ? pjsuaConstants.PJ_TRUE : pjsuaConstants.PJ_FALSE;
	}
}
