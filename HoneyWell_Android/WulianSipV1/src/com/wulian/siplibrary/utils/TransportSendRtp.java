/**
 * Project Name:  WulianICamSip
 * File Name:     TransportSendRtp.java
 * Package Name:  com.wulian.siplibrary.utils
 * @Date:         2015年4月3日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.utils;

import org.pjsip.pjsua.SWIGTYPE_p_pjmedia_transport;

/**
 * @ClassName: TransportSendRtp
 * @Function: TODO
 * @Date: 2015年4月3日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class TransportSendRtp {
	int callId;
	SWIGTYPE_p_pjmedia_transport pjmedia_transport;

	public TransportSendRtp() {
		callId = -1;
		pjmedia_transport = null;
	}

	public void setCallId(int callId) {
		this.callId = callId;
	}

	public int getCallId() {
		return callId;
	}

	public void setPjmedia_transport(
			SWIGTYPE_p_pjmedia_transport pjmedia_transport) {
		this.pjmedia_transport = pjmedia_transport;
	}

	public SWIGTYPE_p_pjmedia_transport getPjmedia_transport() {
		return pjmedia_transport;
	}

}
