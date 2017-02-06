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

import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import java.util.Locale;

/**
 * Manage global configuration of the application.<br/>
 * Provides wrapper around preference content provider and define preference
 * keys constants
 */
public class SipConfigManager {
	/**
	 * Narrow band type codec preference key.<br/>
	 * 
	 * @see #getCodecKey(String, String)
	 */
	public static final String CODEC_NB = "nb";

	/**
	 * Wide band type codec preference key.<br/>
	 * 
	 * @see #getCodecKey(String, String)
	 */
	public static final String CODEC_WB = "wb";

	/**
	 * Get the preference key for a codec priority
	 * 
	 * @param codecName
	 *            Name of the codec as known by pjsip. Example PCMU/8000/1
	 * @param type
	 *            Type of the codec {@link #CODEC_NB} or {@link #CODEC_WB}
	 * @return The key to use to set/get the priority of a codec for a given
	 *         bandwidth
	 */
	public static String getCodecKey(String codecName, String type) {
		String[] codecParts = codecName.split("/");
		String preferenceKey = null;
		if (codecParts.length >= 2) {
			return "codec_" + codecParts[0].toLowerCase(Locale.getDefault())
					+ "_" + codecParts[1] + "_" + type;
		}
		return preferenceKey;
	}

	/**
	 * Get the preference <b>partial</b> key for a given network kind
	 * 
	 * @param networkType
	 *            Type of the network {@link ConnectivityManager}
	 * @param subType
	 *            Subtype of the network {@link TelephonyManager}
	 * @return The partial key for the network kind
	 */
	private static String keyForNetwork(int networkType, int subType) {
		if (networkType == ConnectivityManager.TYPE_WIFI) {
			return "wifi";
		} else if (networkType == ConnectivityManager.TYPE_MOBILE) {
			// 3G (or better)
			if (subType >= TelephonyManager.NETWORK_TYPE_UMTS) {
				return "3g";
			}

			// GPRS (or unknown)
			if (subType == TelephonyManager.NETWORK_TYPE_GPRS
					|| subType == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
				return "gprs";
			}

			// EDGE
			if (subType == TelephonyManager.NETWORK_TYPE_EDGE) {
				return "edge";
			}
		}

		return "other";
	}

	/**
	 * Get preference key for the kind of bandwidth to associate to a network
	 * 
	 * @param networkType
	 *            Type of the network {@link ConnectivityManager}
	 * @param subType
	 *            Subtype of the network {@link TelephonyManager}
	 * @return the preference key for the network kind passed in argument
	 */
	public static String getBandTypeKey(int networkType, int subType) {
		return "band_for_" + keyForNetwork(networkType, subType);
	}
}
