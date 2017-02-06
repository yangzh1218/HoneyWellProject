/**
 * Project Name:  WulianLibrary
 * File Name:     UAStateReceiver.java
 * Package Name:  com.wulian.siplibrary.pjsip
 * @Date:         2014年10月27日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.pjsip;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.TelephonyManager;
import android.util.SparseArray;

import com.wulian.siplibrary.Configproperty;
import com.wulian.siplibrary.manage.SipCallSession;
import com.wulian.siplibrary.manage.SipCallSessionImpl;
import com.wulian.siplibrary.manage.SipManager;
import com.wulian.siplibrary.manage.SipMessage;
import com.wulian.siplibrary.manage.SipUri;
import com.wulian.siplibrary.utils.Compatibility;
import com.wulian.siplibrary.utils.Threading;
import com.wulian.siplibrary.utils.TransportSendRtp;
import com.wulian.siplibrary.utils.WulianDefaultPreference;
import com.wulian.siplibrary.utils.WulianLog;

import org.pjsip.pjsua.Callback;
import org.pjsip.pjsua.SWIGTYPE_p_pjmedia_event;
import org.pjsip.pjsua.SWIGTYPE_p_pjsip_transaction;
import org.pjsip.pjsua.pj_str_t;
import org.pjsip.pjsua.pj_stun_nat_detect_result;
import org.pjsip.pjsua.pjsip_event;
import org.pjsip.pjsua.pjsip_redirect_op;
import org.pjsip.pjsua.pjsip_status_code;
import org.pjsip.pjsua.pjsua;
import org.pjsip.pjsua.pjsua_call_info;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: UAStateReceiver
 * @Function: 客户端接收
 * @Date: 2014年10月27日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class UAStateReceiver extends Callback {
	private final static String THIS_FILE = "UAStateReceiver";
	private final static String ACTION_PHONE_STATE_CHANGED = "android.intent.action.PHONE_STATE";
	private PjSipService pjService;
	private WakeLock eventLock;
	private WorkerHandler msgHandler;
	private HandlerThread handlerThread;
	private int eventLockCount = 0;

	public void initService(PjSipService srv) {
		pjService = srv;

		if (handlerThread == null) {
			handlerThread = new HandlerThread("UAStateAsyncWorker");
			handlerThread.start();
		}
		if (msgHandler == null) {
			msgHandler = new WorkerHandler(handlerThread.getLooper(), this);
		}
		if (eventLock == null) {
			PowerManager pman = (PowerManager) srv.getContext()
					.getSystemService(Context.POWER_SERVICE);
			eventLock = pman.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					"com.wuliansip.inEventLock");
			eventLock.setReferenceCounted(true);
		}
	}

	public void stopService() {
		Threading.stopHandlerThread(handlerThread, true);
		handlerThread = null;
		msgHandler = null;

		// Ensure lock is released since this lock is a ref counted one.
		if (eventLock != null) {
			while (eventLock.isHeld()) {
				eventLock.release();
			}
		}
	}

	private void lockCpu() {
		if (eventLock != null) {
			WulianLog.d(THIS_FILE, "< LOCK CPU");
			eventLock.acquire();
			eventLockCount++;
		}
	}

	private void unlockCpu() {
		if (eventLock != null && eventLock.isHeld()) {
			eventLock.release();
			eventLockCount--;
			WulianLog.d(THIS_FILE, "> UNLOCK CPU " + eventLockCount);
		}
	}

	//
	// @Override
	// public void on_incoming_call(int acc_id, int call_id,
	// SWIGTYPE_p_pjsip_rx_data rdata) {
	// lockCpu();
	// WulianLog.d(THIS_FILE, "on_incoming_call..acc_id is:" + acc_id
	// + ";call_id" + call_id);
	// unlockCpu();
	// }
	//
	@Override
	public void on_nat_detect(pj_stun_nat_detect_result res) {
	}

	@Override
	public void on_pager(int callId, pj_str_t from, pj_str_t to,
			pj_str_t contact, pj_str_t mime_type, pj_str_t body) {
		lockCpu();
		WulianLog.d(THIS_FILE, "on_pager..." + callId);

		long date = System.currentTimeMillis();
		String fromStr = PjSipService.pjStrToString(from);
		String canonicFromStr = SipUri.getCanonicalSipContact(fromStr);
		String contactStr = PjSipService.pjStrToString(contact);
		String toStr = PjSipService.pjStrToString(to);
		String bodyStr = PjSipService.pjStrToString(body);
		if (bodyStr != null) {
			// Log.d(THIS_FILE, "The body is;" + bodyStr);
		} else {
			// Log.d(THIS_FILE, "The body is null;");
		}
		String mimeStr = PjSipService.pjStrToString(mime_type);

		// Sanitize from sip uri
		int slashIndex = fromStr.indexOf("/");
		if (slashIndex != -1) {
			fromStr = fromStr.substring(0, slashIndex);
		}
		WulianLog.d(THIS_FILE, "mineStr is:" + mimeStr);
		// if (!TextUtils.isEmpty(bodyStr)) {
		// WulianLog.d(THIS_FILE, "bodyStr is:" + bodyStr);
		// }
		// SipMessage msg = new SipMessage(canonicFromStr, toStr, contactStr,
		// bodyStr, mimeStr, date, SipMessage.MESSAGE_TYPE_INBOX, fromStr);
		SipMessage msg = new SipMessage();
		msg.setFrom(canonicFromStr);
		msg.setTo(toStr);
		msg.setContact(contactStr);

		msg.setBody(bodyStr);
		msg.setMimeType(mimeStr);
		msg.setDate(date);
		msg.setType(SipMessage.MESSAGE_TYPE_INBOX);
		msg.setFullFrom(fromStr);
		// Broadcast the message
		Intent intent = new Intent();
		if (mimeStr.equalsIgnoreCase(Configproperty.ALARM_TYPE)) {
			intent.setAction(SipManager.GET_ACTION_SIP_ALARM_MESSAGE_RECEIVED());
		} else {
			intent.setAction(SipManager.GET_ACTION_SIP_MESSAGE_RECEIVED());
		}
		intent.putExtra("SipMessage", msg);
		pjService.getContext().sendBroadcast(intent);
		unlockCpu();
	}

	@Override
	public void on_pager_status(int callId, pj_str_t to, pj_str_t body,
			pjsip_status_code status, pj_str_t reason) {
		lockCpu();
		long date = System.currentTimeMillis();
		WulianLog.d(THIS_FILE, "on_pager_status..." + callId);;
		// TODO : treat error / acknowledge of messages
		int messageType = (status.equals(pjsip_status_code.PJSIP_SC_OK) || status
				.equals(pjsip_status_code.PJSIP_SC_ACCEPTED)) ? SipMessage.MESSAGE_TYPE_SENT
				: SipMessage.MESSAGE_TYPE_FAILED;
		String toStr = SipUri.getCanonicalSipContact(PjSipService
				.pjStrToString(to));
//		String reasonStr = PjSipService.pjStrToString(reason);
		String bodyStr = PjSipService.pjStrToString(body);
		int statusInt=-1;
		try {
			statusInt = status.swigValue();
		}catch(Exception e) {
			statusInt = -1;
		}
		WulianLog.d(THIS_FILE, "SipMessage in on pager status..."
				+ "messageType :" + messageType + ";toStr:" + toStr
				+ ";bodyStr:" + bodyStr + ";statusInt" + statusInt + ";"
				+ status.toString() );
		// SipMessage msg = new SipMessage("", toStr, String.valueOf(statusInt),
		// bodyStr, "", date, SipMessage.MESSAGE_TYPE_SENT, "");
		SipMessage msg = new SipMessage();
		msg.setFrom("");
		msg.setTo(toStr);
		msg.setContact(String.valueOf(statusInt));

		msg.setBody(bodyStr);
		msg.setMimeType("");
		msg.setDate(date);
		msg.setType(SipMessage.MESSAGE_TYPE_SENT);
		msg.setFullFrom("");

		Intent intent = new Intent();
		intent.setAction(SipManager.GET_ACTION_SIP_MESSAGE_RECEIVED());
		intent.putExtra("SipMessage", msg);
		pjService.getContext().sendBroadcast(intent);
		unlockCpu();
	}

	public int on_validate_audio_clock_rate(int clockRate) {
		if (pjService != null) {
			WulianLog.d("PML", "on_validate_audio_clock_rate");
			return pjService.validateAudioClockRate(clockRate);
		}
		return -1;
	}

	private void onBroadcastCallState(final SipCallSession callInfo) {
		WulianLog
				.d(THIS_FILE, "onBroadcastCallState..." + callInfo.getCallId());
		SipCallSession publicCallInfo = new SipCallSession(callInfo);
		Intent callStateChangedIntent = new Intent(
				SipManager.GET_ACTION_SIP_CALL_CHANGED());
		callStateChangedIntent.putExtra(SipManager.EXTRA_CALL_INFO,
				publicCallInfo);
		pjService.getContext().sendBroadcast(callStateChangedIntent);
	}

//	private void onBroadcastCallState(final int call_id, String action) {
//		WulianLog.d(THIS_FILE, "onBroadcastCallState call_id");
//		Intent callStateChangedIntent = new Intent(action);
//		callStateChangedIntent.putExtra(SipManager.EXTRA_CALL_KEY_FOUND_INFO,
//				call_id);
//		pjService.getContext().sendBroadcast(callStateChangedIntent);
//	}

//	@Override
//	public int timer_schedule(int entry, int entryId, int time) {
//		WulianLog.d(THIS_FILE, "timer_schedule...entry:" + entry + "; entryId:"
//				+ entryId + "; time:" + time);
//		return TimerWrapper.schedule(entry, entryId, time);
//	}
//
//	@Override
//	public int timer_cancel(int entry, int entryId) {
//		WulianLog.d("PML", "timer_cancel is:" + entry + ";entryId is:"
//				+ entryId);
//		return TimerWrapper.cancel(entry, entryId);
//	}

	@Override
	public void on_setup_audio(int beforeInit) {
		if (pjService != null) {
			WulianLog.d("PML", "beforeInit on_setup_audio is:" + beforeInit);
			pjService.setAudioInCall(beforeInit);
		}
	}

	@Override
	public void on_teardown_audio() {
		if (pjService != null) {
			WulianLog.d("PML", "on_teardown_audio ");
			pjService.unsetAudioInCall();
		}
	}

	@Override
	public int on_set_micro_source() {
		return WulianDefaultPreference.getsMicroSource();
	}

	// @Override
	// public SWIGTYPE_p_pjmedia_transport on_create_media_transport(int
	// call_id,
	// long media_idx, SWIGTYPE_p_pjmedia_transport base_tp, long flags) {
	// WulianLog.d(THIS_FILE, "SWIGTYPE_p_pjmedia_transport call_id is:"
	// + call_id + ";media_idx:" + media_idx);
	// if (media_idx == 0) {
	// TransportSendRtp rtp = new TransportSendRtp();
	// rtp.setCallId(call_id);
	// rtp.setPjmedia_transport(base_tp);
	// transportList.put(call_id, rtp);
	// }
	// return base_tp;
	// }

	// 电话状态改变
	@Override
	public void on_call_state(int callId, pjsip_event e) {
		WulianLog.d(THIS_FILE, "on_call_state..." + callId);
		pjsua.css_on_call_state(callId, e);
		lockCpu();
		WulianLog.d(THIS_FILE, "Call state <<");
		try {
			final SipCallSession callInfo = updateCallInfoFromStack(callId, e);
			int callState = callInfo.getCallState();
			// If disconnected immediate stop required stuffs
			if (callState == SipCallSession.InvState.DISCONNECTED) {
				if (pjService.mediaManager != null) {
					if (getRingingCall() == null) {
						pjService.mediaManager.resetSettings();
					}
				}
			}
			if (msgHandler != null)
				msgHandler.sendMessage(msgHandler.obtainMessage(ON_CALL_STATE,
						callInfo));
		} catch (Exception exception) {

		} finally {
			unlockCpu();
		}
	}

	// 电话媒体状态改变
	@Override
	public void on_call_media_state(int callId) {
		pjsua.css_on_call_media_state(callId);
		WulianLog.d(THIS_FILE, "on_call_media_state..." + callId);
		lockCpu();
		final SipCallSession callInfo = updateCallInfoFromStack(callId, null);
		boolean connectToOtherCalls = false;
		int callConfSlot = callInfo.getConfPort();
		int mediaStatus = callInfo.getMediaStatus();
		if (mediaStatus == SipCallSession.MediaState.ACTIVE
				|| mediaStatus == SipCallSession.MediaState.REMOTE_HOLD) {
			connectToOtherCalls = true;
			pjsua.conf_connect(callConfSlot, 0);// 输出
			pjsua.conf_connect(0, callConfSlot);// 输入
			// Adjust software volume
			if (pjService.mediaManager != null) {
				pjService.mediaManager.setSoftwareVolume();
			}
			// Auto record
			// if (mAutoRecordCalls && pjService.canRecord(callId)
			// && !pjService.isRecording(callId)) {
			// pjService.startRecording(callId, SipManager.BITMASK_IN
			// | SipManager.BITMASK_OUT);
			// }
		}
		if (msgHandler != null)
			msgHandler.sendMessage(msgHandler.obtainMessage(ON_MEDIA_STATE,
					callInfo));
	}

	@Override
	public void event_keyframe_found(int call_id) {
		lockCpu();
		WulianLog.d(THIS_FILE, "event_keyframe_found  call_id is:" + call_id);
		if (msgHandler != null)
			msgHandler.sendMessage(msgHandler.obtainMessage(ON_KEY_FRAME_FOUND,
					call_id));
		unlockCpu();
	}

	@Override
	public void on_call_media_event(int call_id, long med_idx,
			SWIGTYPE_p_pjmedia_event event) {
		WulianLog.d(THIS_FILE, "on_call_media_event  call_id is:" + call_id
				+ "med_idx is:" + med_idx);
		if (med_idx == 1) {
			// pjsua_call_info pjInfo = new pjsua_call_info();
			// if (pjsua.acc_is_valid(call_id) != 0) {
			// WulianLog
			// .d(THIS_FILE,
			// "sendRtp call_get_info  acc_is_valid   is: true");
			// int status = pjsua.call_get_info(call_id, pjInfo);
			// if (status == pjsua.PJ_SUCCESS) {
			// pjInfo.get
			// }
			// }
		}
		pjsua.css_on_call_media_event(call_id, med_idx, event);
	}

	@Override
	public void on_call_tsx_state(int call_id,
			SWIGTYPE_p_pjsip_transaction tsx, pjsip_event e) {
		lockCpu();
		WulianLog.d(THIS_FILE, "on_call_tsx_state...call_id:" + call_id + "");
		try {
			// tsx.
			updateCallInfoFromStack(call_id, e);
		} catch (Exception exception) {

		} finally {
			// Unlock CPU anyway
			unlockCpu();
		}
	}

	@Override
	public pjsip_redirect_op on_call_redirected(int call_id, pj_str_t target) {
		WulianLog.w(THIS_FILE,
				"Ask for redirection, not yet implemented, for now allow all "
						+ PjSipService.pjStrToString(target));
		return pjsip_redirect_op.PJSIP_REDIRECT_ACCEPT;
	}

	@Override
	public void on_reg_state(int acc_id) {
		// Log.d(THIS_FILE, "on_reg_state " + acc_id);
	}

	private static SparseArray<SipCallSessionImpl> callsList = new SparseArray<SipCallSessionImpl>();
	private static SparseArray<TransportSendRtp> transportList = new SparseArray<TransportSendRtp>();

	public void clearCallList() {
		if (callsList != null && callsList.size() > 0) {
			callsList.clear();
		}
		if (transportList != null && transportList.size() > 0) {
			transportList.clear();
		}
	}

	public void sendRtp(int callId) {
		synchronized (transportList) {
			TransportSendRtp rtp = transportList.get(callId);
			if (rtp != null && rtp.getCallId() != -1
					&& rtp.getPjmedia_transport() != null) {
				WulianLog.d(THIS_FILE, "sendRtp  rtp.getCallId() is:" + callId);
				try {
					// pjsua.my_pjmedia_transport_send_rtp(rtp.getPjmedia_transport(),
					// (Object) "1234567890", 10);
					pjsua_call_info pjInfo = new pjsua_call_info();
					if (pjsua.acc_is_valid(callId) != 0) {
						WulianLog
								.d(THIS_FILE,
										"sendRtp call_get_info  acc_is_valid   is: true");
						int status = pjsua.call_get_info(callId, pjInfo);

						WulianLog.d(THIS_FILE,
								"sendRtp call_get_info  status  is:" + status);
						// StringBuilder sb=new StringBuilder();
						// for(int i=0;i<100;i++) {
						// sb.append("1234567890");
						// }
						if (status == pjsua.PJ_SUCCESS) {
							// pjsua.my_pjmedia_transport_send_rtp(rtp.getPjmedia_transport(),
							// (Object) "1234567890", 10);
							WulianLog
									.d(THIS_FILE,
											"sendRtp my_pjmedia_transport_send_rtp :::"
													+ rtp.getPjmedia_transport() == null ? "NULL"
													: "not null");
							// pjsua.my_pjmedia_transport_send_rtp(rtp.getPjmedia_transport(),
							// (Object) ("1234567890"), 10);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private SipCallSessionImpl updateCallInfoFromStack(Integer callId,
			pjsip_event e) {
		SipCallSessionImpl callInfo;
		WulianLog.d(THIS_FILE, "updateCallInfoFromStack :" + callId);
		synchronized (callsList) {
			callInfo = callsList.get(callId);
			if (callInfo == null) {
				callInfo = new SipCallSessionImpl();
				callInfo.setCallId(callId);
			}
		}
		PjSipCalls.updateSessionFromPj(callInfo, e, pjService.getContext());
		// callInfo.setIsRecording(pjService.isRecording(callId));
		// callInfo.setCanRecord(pjService.canRecord(callId));
		synchronized (callsList) {
			// Re-add to list mainly for case newly added session
			callsList.put(callId, callInfo);
		}
		return callInfo;
	}

	public SipCallSession getActiveCallInProgress() {
		// Go through the whole list of calls and find the first active state.
		synchronized (callsList) {
			for (int i = 0; i < callsList.size(); i++) {
				SipCallSession callInfo = getCallInfo(i);
				if (callInfo != null && callInfo.isActive()) {
					return callInfo;
				}
			}
		}
		return null;
	}

	/**
	 * Get list of calls session available.
	 * 
	 * @return List of calls.
	 */
	public SipCallSessionImpl[] getCalls() {
		if (callsList != null) {
			List<SipCallSessionImpl> calls = new ArrayList<SipCallSessionImpl>();

			synchronized (callsList) {
				for (int i = 0; i < callsList.size(); i++) {
					SipCallSessionImpl callInfo = getCallInfo(i);
					if (callInfo != null) {
						calls.add(callInfo);
					}
				}
			}
			return calls.toArray(new SipCallSessionImpl[calls.size()]);
		}
		return new SipCallSessionImpl[0];
	}

	public SipCallSession getRingingCall() {
		// Go through the whole list of calls and find the first ringing state.
		synchronized (callsList) {
			for (int i = 0; i < callsList.size(); i++) {
				SipCallSession callInfo = getCallInfo(i);
				if (callInfo != null && callInfo.isActive()
						&& callInfo.isBeforeConfirmed()
						&& callInfo.isIncoming()) {
					return callInfo;
				}
			}
		}
		return null;
	}

	/**
	 * Get call info for a given call id.
	 * 
	 * @param callId
	 *            the id of the call we want infos for
	 * @return the call session infos.
	 */
	public SipCallSessionImpl getCallInfo(Integer callId) {
		SipCallSessionImpl callInfo;
		synchronized (callsList) {
			callInfo = callsList.get(callId, null);
		}
		return callInfo;
	}

	// public static void cleanList() {
	// callsList.clear();
	// }

	private static final int ON_CALL_STATE = 1;
	private static final int ON_MEDIA_STATE = 2;
	private static final int ON_KEY_FRAME_FOUND = 4;

	private static class WorkerHandler extends Handler {
		WeakReference<UAStateReceiver> sr;

		public WorkerHandler(Looper looper, UAStateReceiver stateReceiver) {
			super(looper);
			WulianLog.d(THIS_FILE, "Create async worker !!!");
			sr = new WeakReference<UAStateReceiver>(stateReceiver);
		}

		public void handleMessage(Message msg) {
			UAStateReceiver stateReceiver = sr.get();
			if (stateReceiver == null) {
				return;
			}
			WulianLog.d(THIS_FILE, "Fix Ring handleMessage " + msg.what);
			stateReceiver.lockCpu();
			switch (msg.what) {
			// case ON_KEY_FRAME_FOUND:
			// int call_id = (Integer) msg.obj;
			// stateReceiver.onBroadcastCallState(call_id,
			// SipManager.ACTION_SIP_CALL_CHANGED);
			// break;
			case ON_CALL_STATE: {
				SipCallSessionImpl callInfo = (SipCallSessionImpl) msg.obj;
				if (callInfo != null) {
					final int callState = callInfo.getCallState();
					WulianLog.d(THIS_FILE, "ON_CALL_STATE is:" + callState);
					switch (callState) {
						case SipCallSession.InvState.INCOMING:
						case SipCallSession.InvState.CALLING:
//						stateReceiver.broadCastAndroidCallState("RINGING",
//								callInfo.getRemoteContact());
							break;
						case SipCallSession.InvState.EARLY:
						case SipCallSession.InvState.CONNECTING:
						case SipCallSession.InvState.CONFIRMED:
//						stateReceiver.broadCastAndroidCallState("OFFHOOK",
//								callInfo.getRemoteContact());
							if (callState == SipCallSession.InvState.CONFIRMED
									&& callInfo.getCallStart() == 0) {
								callInfo.setCallStart(System.currentTimeMillis());
								stateReceiver.onBroadcastCallState(callInfo);
							}
							break;
						case SipCallSession.InvState.DISCONNECTED:
//						stateReceiver.broadCastAndroidCallState("IDLE",
//								callInfo.getRemoteContact());
							callInfo.applyDisconnect();
							stateReceiver.onBroadcastCallState(callInfo);
							// cleanList();
							break;
					}
				}
				break;
			}
//			case ON_MEDIA_STATE: {
//				SipCallSession mediaCallInfo = (SipCallSession) msg.obj;
//				if (mediaCallInfo != null) {
//					SipCallSessionImpl callInfo = stateReceiver.callsList
//							.get(mediaCallInfo.getCallId());
//					if (callInfo != null) {
//						callInfo.setMediaStatus(mediaCallInfo.getMediaStatus());
//						stateReceiver.callsList.put(mediaCallInfo.getCallId(),
//								callInfo);
//						stateReceiver.onBroadcastCallState(callInfo);
//					}
//				}
//				break;
//			}
			}
			stateReceiver.unlockCpu();
		}
	};

//	private void broadCastAndroidCallState(String state, String number) {
//		WulianLog.d(THIS_FILE, "broadCastAndroidCallState...state is:" + state
//				+ ";number is:" + number);
//		if (!Compatibility.isCompatible(19)) {
//			Intent intent = new Intent(ACTION_PHONE_STATE_CHANGED);
//			intent.putExtra(TelephonyManager.EXTRA_STATE, state);
//			if (number != null) {
//				intent.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, number);
//			}
//			intent.putExtra("Wulian", true);
//			pjService.getContext().sendBroadcast(intent,
//					android.Manifest.permission.READ_PHONE_STATE);
//		}
//	}

	public void updateCallMediaState(int callId) {
		SipCallSession callInfo = updateCallInfoFromStack(callId, null);
		if (msgHandler != null)
			msgHandler.sendMessage(msgHandler.obtainMessage(ON_MEDIA_STATE,
					callInfo));
	}
}
