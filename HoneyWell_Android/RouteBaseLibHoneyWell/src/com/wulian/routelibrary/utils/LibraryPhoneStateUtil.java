/**
 * Project Name:  RouteLibrary
 * File Name:     LibraryPhoneStateUtil.java
 * Package Name:  com.wulian.routelibrary.utils
 * @Date:         2014-9-6
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @ClassName: LibraryPhoneStateUtil
 * @Function: 库文件手机状态辅助类
 * @Date: 2014-9-6
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class LibraryPhoneStateUtil {
	/**
	 * @MethodName hasInternet
	 * @Function 是否有网络
	 * @author Puml
	 * @date: 2014-9-6
	 * @email puml@wuliangroup.cn
	 * @param mContext
	 * @return
	 */
	public static boolean hasInternet(Context mContext) {
		ConnectivityManager manager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected() && info.isAvailable()) {
			if (info.getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @MethodName getVersionCode
	 * @Function 获取版本CODE
	 * @author Puml
	 * @date: 2014-9-9
	 * @email puml@wuliangroup.cn
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context)
			throws NameNotFoundException {
		int versionCode = 1;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			// 当前版本的版本号
			versionCode = info.versionCode;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
			throw new NameNotFoundException();
		}
		return versionCode;
	}

	/**
	 * 
	 * @MethodName getVersionName
	 * @Function 获取版本号
	 * @author Puml
	 * @date: 2014-9-9
	 * @email puml@wuliangroup.cn
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */
	public static String getVersionName(Context context)
			throws NameNotFoundException {
		String versionName = "1.0.0";
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			// 当前应用的版本名称
			versionName = info.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
			throw new NameNotFoundException();
		}
		return versionName;
	}

	/**
	 * 
	 * @MethodName isWifiConnect
	 * @Function 判断Wifi是否连接
	 * @author Puml
	 * @date: 2014-9-11
	 * @email puml@wuliangroup.cn
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnect(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}

	/**
	 * 
	 * @MethodName getIpAddress
	 * @Function 获取网关地址
	 * @author Puml
	 * @date: 2014-9-11
	 * @email puml@wuliangroup.cn
	 * @param context
	 */
	public static String getIpAddress(Context context) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo di = wm.getDhcpInfo();
		long getewayIpL = di.gateway;
		String getwayIpS = long2ip(getewayIpL);// 网关地址
		long netmaskIpL = di.netmask;
		String netmaskIpS = long2ip(netmaskIpL);// 子网掩码地址
		return getwayIpS;
	}

	private static String long2ip(long ip) {
		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf((int) (ip & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
		return sb.toString();
	}

	/**
	 * 
	 * @MethodName getLocalIpAddress
	 * @Function 获取本地V4 IP
	 * @author Puml
	 * @date: 2014年11月12日
	 * @email puml@wuliangroup.cn
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (RouteLibraryUtils.isCompatible(14)) {
						if (!inetAddress.isLoopbackAddress()
								&& inetAddress instanceof Inet4Address) {
							return inetAddress.getHostAddress().toString();
						}
					} else {
						if (!inetAddress.isLoopbackAddress()) {
							return inetAddress.getHostAddress().toString();
						}
					}
				}
			}
		} catch (SocketException ex) {
			LibraryLoger.e("PML", ex.toString());
		}
		return null;
	}

	/**
	 * 设置手机的移动数据
	 */
	public static void setMobileData(Context pContext, boolean pBoolean) {
		try {
			LibraryLoger.d("setMobileData is " + pBoolean);
			ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			Class ownerClass = mConnectivityManager.getClass();
			Class[] argsClass = new Class[1];
			argsClass[0] = boolean.class;
			Method method = ownerClass.getMethod("setMobileDataEnabled",
					argsClass);
			method.invoke(mConnectivityManager, pBoolean);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LibraryLoger.e("移动数据设置错误: " + e.toString());
		}
	}

	/**
	 * 返回手机移动数据的状态
	 *
	 * @param pContext
	 * @param arg
	 *            默认填null
	 * @return true 连接 false 未连接
	 */
	public static boolean getMobileDataState(Context pContext, Object[] arg) {
		try {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			Class ownerClass = mConnectivityManager.getClass();
			Class[] argsClass = null;
			if (arg != null) {
				argsClass = new Class[1];
				argsClass[0] = arg.getClass();
			}
			Method method = ownerClass.getMethod("getMobileDataEnabled",
					argsClass);
			Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);
			return isOpen;
		} catch (Exception e) {
			// TODO: handle exception
			LibraryLoger.e("得到移动数据状态出错");
			return false;
		}

	}

	/**
	 * @MethodName getPhoneInformation
	 * @Function 获取手机信息
	 * @author Puml
	 * @date: 2014年12月23日
	 * @email puml@wuliangroup.cn
	 * @return
	 */
//	public static String getPhoneInformation() {
//		String version = ConfigLibrary.VERSION_NAME;
//		String result = ConfigLibrary.APP_NAME + "/" + version + " "
//				+ ConfigLibrary.OS + "/" + android.os.Build.VERSION.RELEASE;
//		LibraryLoger.d("RouteLibrary", "getPhoneInformation:" + result);
//		return result;
//	}

	public static boolean isAirplaneModeOn(Context context) {
		return (Settings.System.getInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) != 0);
	}

	public static boolean hasInternet(Activity mActivity) {
		ConnectivityManager manager = (ConnectivityManager) mActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasWiFi(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			}
		}
		return false;
	}


	/**
	 * 获取Imsi
	 */
	public static String getImsi(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String wifiMac = info.getMacAddress();
		// 如果为空，取设备号
		if (wifiMac == null || wifiMac.equals("")) {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			wifiMac = imei;
			// 如果为空，取序列号
			if (wifiMac == null || wifiMac.equals("")) {
				wifiMac = tm.getSimSerialNumber();
				if (wifiMac == null) {
					wifiMac = "";
				}
			}
		}
		LibraryLoger.d("wifimac is:" + wifiMac);
		return wifiMac;
	}
}
