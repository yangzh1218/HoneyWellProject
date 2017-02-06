/**
 * Project Name:  WulianLibrary
 * File Name:     ProviderWrapper.java
 * Package Name:  com.wulian.siplibrary.utils
 * @Date:         2014年10月27日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.wulian.siplibrary.manage.SipManager;

import java.io.File;

/**
 * @ClassName: ProviderWrapper
 * @Function: TODO
 * @Date: 2014年10月27日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class ProviderWrapper {
	private Context context;
	private static final String THIS_FILE = "ProviderWrapper";
	private ConnectivityManager connectivityManager;

	public final static PackageInfo getCurrentPackageInfos(Context ctx) {
		PackageInfo pinfo = null;
		try {
			pinfo = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			WulianLog.e(THIS_FILE,
					"Impossible to find version of current package !!");
		}
		return pinfo;
	}

	public ProviderWrapper(Context aContext) {
		context = aContext;
		connectivityManager = (ConnectivityManager) aContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			WulianLog.d(THIS_FILE, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			PackageInfo pinfo = aContext.getPackageManager().getPackageInfo(
					aContext.getPackageName(), 0);
			if (pinfo != null) {
				String packageName = pinfo.applicationInfo.packageName;
				WulianLog.d(THIS_FILE, "Package is :" + packageName);
				if (TextUtils.isEmpty(packageName)) {
					SipManager.SIP_PREFIX_MSG = SipManager.DEFAULT_SIP_PREFIX_MSG;
				} else {
					SipManager.SIP_PREFIX_MSG = packageName+".";
				}
			} else {
				SipManager.SIP_PREFIX_MSG = SipManager.DEFAULT_SIP_PREFIX_MSG;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			SipManager.SIP_PREFIX_MSG = SipManager.DEFAULT_SIP_PREFIX_MSG;
		}
	}

	/**
	 * Set all values to default
	 */
	public void resetAllDefaultValues() {
		Compatibility.setFirstRunParameters();
	}

	/**
	 * Retrieve UDP keep alive interval for the current connection
	 * 
	 * @return KA Interval in second
	 */
	public int getUdpKeepAliveInterval() {
		NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
		if (ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI) {
			return WulianDefaultPreference.getsUdpKeepAliveIntervalWIFI();
		}
		return WulianDefaultPreference.getsUdpKeepAliveIntervalMobile();
	}

	/**
	 * Retrieve Tcp keep alive interval for the current connection
	 * 
	 * @return KA Interval in second
	 */
	public int getTcpKeepAliveInterval() {
		NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
		if (ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI) {
			return WulianDefaultPreference.getsTcpKeepAliveIntervalWIFI();
		}
		return WulianDefaultPreference.getsTcpKeepAliveIntervalMobile();
	}

	String[] availableNetworks = { "nonet", "3g", "edge", "gprs", "wifi",
			"other" };

	public boolean getConnectWifi() {
		NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
		if (ni != null) {
			int type = ni.getType();
			// Wifi connected
			if (ni.isConnected() &&
			// 9 = ConnectivityManager.TYPE_ETHERNET
					(type == ConnectivityManager.TYPE_WIFI || type == 9)) {
				return true;
			}
		}
		return false;
	}

	public boolean getConnectMobile() {
		NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
		if (ni != null) {
			if (ni.isRoaming()) {
				return false;
			}
			int type = ni.getType();
			if (ni.isConnected() &&
			// Type 3,4,5 are other mobile data ways
					(type == ConnectivityManager.TYPE_MOBILE || (type <= 5 && type >= 3))) {
				int subType = ni.getSubtype();

				// 3G (or better)
				if (subType >= TelephonyManager.NETWORK_TYPE_UMTS) {
					return true;
				}

				// GPRS (or unknown)
				if ((subType == TelephonyManager.NETWORK_TYPE_GPRS || subType == TelephonyManager.NETWORK_TYPE_UNKNOWN)) {
					return true;
				}

				// EDGE
				if (subType == TelephonyManager.NETWORK_TYPE_EDGE) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Retrieve Tcp keep alive interval for the current connection
	 * 
	 * @return KA Interval in second
	 */
	public int getTlsKeepAliveInterval() {
		NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
		if (ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI) {
			return WulianDefaultPreference.getsTlsKeepAliveIntervalWIFI();
		}
		return WulianDefaultPreference.getsTlsKeepAliveIntervalMobile();
	}

	public float getInitialVolumeLevel() {
		return (float) (WulianDefaultPreference.getsSndStreamLevel() / 10.0f);
	}

	public static File getZrtpFolder(Context ctxt) {
		return ctxt.getFilesDir();
		/* return getSubFolder(ctxt, ZRTP_FOLDER, true); */
	}

}
