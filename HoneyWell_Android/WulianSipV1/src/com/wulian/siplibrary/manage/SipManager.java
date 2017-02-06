/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  This file and this file only is also released under Apache license as an API file
 */

package com.wulian.siplibrary.manage;

import com.wulian.siplibrary.utils.WulianDefaultPreference;

public final class SipManager {
	public static String DEFAULT_SIP_PREFIX_MSG = WulianDefaultPreference.forV5 ? "com.wulian.siplibrary."
			: "com.wulian.siplibrary.icam.";
	public static String SIP_PREFIX_MSG = "";
	
	public static final String PERMISSION_USE_SIP = "android.permission.USE_SIP";

	public static final String PROTOCOL_CSIP = "csip";
	/**
	 * Scheme for sip uri.
	 */
	public static final String PROTOCOL_SIP = "sip";
	/**
	 * Scheme for sips (sip+tls) uri.
	 */
	public static final String PROTOCOL_SIPS = "sips";

	public static String GET_ACTION_GET_EXTRA_CODECS() {
		return SIP_PREFIX_MSG + "codecs.action.REGISTER_CODEC";
	}

	public static String GET_ACTION_SIP_CALL_CHANGED() {
		return SIP_PREFIX_MSG + "service.CALL_CHANGED";
	}

	public static String GET_ACTION_SIP_MESSAGE_RECEIVED() {
		return SIP_PREFIX_MSG + "service.MESSAGE_RECEIVED";
	}

	public static String GET_ACTION_SIP_ALARM_MESSAGE_RECEIVED() {
		return SIP_PREFIX_MSG + "service.ALARM_MESSAGE_RECEIVED";
	}

	public static String GET_ACTION_GET_EXTRA_VIDEO_CODECS() {
		return SIP_PREFIX_MSG + "codecs.action.REGISTER_VIDEO_CODEC";
	}

	public static String GET_ACTION_GET_VIDEO_PLUGIN() {
		return SIP_PREFIX_MSG + "plugins.action.REGISTER_VIDEO";
	}

	/**
	 * Bitmask to keep media/call coming from outside
	 */
	public final static int BITMASK_IN = 1 << 0;
	/**
	 * Bitmask to keep only media/call coming from the app
	 */
	public final static int BITMASK_OUT = 1 << 1;
	/**
	 * Bitmask to keep all media/call whatever incoming/outgoing
	 */
	public final static int BITMASK_ALL = BITMASK_IN | BITMASK_OUT;
	/**
	 * Meta constant name for library name.
	 */
	public static final String META_LIB_NAME = "lib_name";
	/**
	 * Meta constant name for the factory name.
	 */
	public static final String META_LIB_INIT_FACTORY = "init_factory";
	/**
	 * Meta constant name for the factory deinit name.
	 */
	public static final String META_LIB_DEINIT_FACTORY = "deinit_factory";
	/**
	 * The account used for this call
	 */
	public static final String CALLLOG_PROFILE_ID_FIELD = "account_id";
	/**
	 * The final latest status code for this call.
	 */
	public static final String CALLLOG_STATUS_CODE_FIELD = "status_code";
	/**
	 * The final latest status text for this call.
	 */
	public static final String CALLLOG_STATUS_TEXT_FIELD = "status_text";

	public static final String EXTRA_CALL_INFO = "call_info";

	public static final String EXTRA_CALL_KEY_FOUND_INFO = "key_found";
	public static final String EXTRA_OUTGOING_ACTIVITY = "outgoing_activity";

	public static final String EXTRA_FILE_PATH = "file_path";

	public static final String EXTRA_SIP_CALL_TARGET = "call_target";
	public static final String EXTRA_SIP_CALL_OPTIONS = "call_options";
	public static final String EXTRA_FALLBACK_BEHAVIOR = "fallback_behavior";
	public static final int FALLBACK_ASK = 0;
	public static final int FALLBACK_PREVENT = 1;

	public static final int FALLBACK_AUTO_CALL_OTHER = 2;

	public static final int SUCCESS = 0;

	public static final int ERROR_CURRENT_NETWORK = 10;

	/**
	 * Possible presence status.(可能的实时状态)
	 */
	public enum PresenceStatus {
		/**
		 * Unknown status(未知状态)
		 */
		UNKNOWN,
		/**
		 * Online status(在线状态)
		 */
		ONLINE,
		/**
		 * Offline status(离线)
		 */
		OFFLINE,
		/**
		 * Busy status(忙碌状态)
		 */
		BUSY,
		/**
		 * Away status(离开)
		 */
		AWAY,
	}
}
