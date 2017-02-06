/**
 * Project Name:  RouteLibrary
 * File Name:     WiFiConnectUtil.java
 * Package Name:  com.wulian.routelibrary.utils
 * @Date:         2014年11月12日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.Status;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: WiFiConnectUtil
 * @Function: WiFi连接管理器(不完善)
 * @Date: 2014年11月12日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class WiFiConnectUtil {
	private static final String THIS_FILE = "WiFiConnectUtil";
	private WifiManager mWifiManager;
	private WifiLock mWifiLock;
	private Context mContext;

	private Object mConnectMonitor = new Object();
	private NetworkInfo.State mNetworkState = State.UNKNOWN;
	ConnectivityManager connec;
	private List<WifiConfiguration> mConfiguredNets = new ArrayList<WifiConfiguration>();

	public WiFiConnectUtil(Context context) {
		mContext = context;
		mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		connec = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		mWifiLock = mWifiManager.createWifiLock("WiFiLan");
	};

	// 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
	public enum WifiCipherType {
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}

	// 打开wifi功能
	public boolean OpenWifi() {
		boolean bRet = true;
		if (!mWifiManager.isWifiEnabled()) {
			bRet = mWifiManager.setWifiEnabled(true);
		}
		return bRet;
	}

	// 关闭wifi功能
	public boolean CloseWifi() {
		boolean bRet = true;
		if (mWifiManager.isWifiEnabled()) {
			bRet = mWifiManager.setWifiEnabled(false);
		}
		return bRet;
	}

	public Bundle getConnectInfo(Context context) {
		Bundle mBundle = new Bundle();
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netWorkInfo = connectivityManager.getActiveNetworkInfo();
		if (netWorkInfo != null) {
			if (netWorkInfo.isAvailable() && netWorkInfo.isConnected()) {
				switch (netWorkInfo.getType()) {
				case ConnectivityManager.TYPE_WIFI:
					WifiManager mWifiManager = (WifiManager) context
							.getSystemService(Context.WIFI_SERVICE);
					if (mWifiManager.isWifiEnabled()) {
						mBundle.putString("connectType", "wifi");
						WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
						mBundle.putString("ssid", mWifiInfo.getSSID());
					} else {
						mBundle.putString("connectType", "none");
					}
					break;
				case ConnectivityManager.TYPE_MOBILE:
					mBundle.putString("connectType", "mobile");
					break;
				default:
					mBundle.putString("connectType", "none");
					break;
				}
			} else {
				mBundle.putString("connectType", "none");
			}
		} else {
			mBundle.putString("connectType", "none");
		}
		return mBundle;
	}

	// 提供一个外部接口，传入要连接的无线网 (不完善)
	// 外部需要提供wifiLock
	public Bundle Connect(String SSID, String Password) {
		WifiCipherType Type = WifiCipherType.WIFICIPHER_WPA;
		int msgCount = 0;
		Bundle mBundle = getConnectInfo(mContext);
		int result = 0;
		if (!this.OpenWifi()) {
			mBundle.putInt("NetID", -1);
			return mBundle;
		}
		// 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
		// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
		msgCount = 0;
		while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
			try {
				// 为了避免程序一直while循环，让它睡个1000毫秒在检测……
				Thread.currentThread();
				// LibraryLoger.d("The OpenWifi count is:" + msgCount);
				msgCount++;
				if (msgCount == 30) {
					result = -1;
					break;
				}
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				result = -1;
				break;
			}
		}
		if (result != 0) {
			mBundle.putInt("NetID", -1);
			RollbackMobileState(mContext, mBundle);
			return mBundle;
		}
		WifiConfiguration wifiConfig = this
				.CreateWifiInfo(SSID, Password, Type);
		//
		if (wifiConfig == null) {
			mBundle.putInt("NetID", -1);
			RollbackMobileState(mContext, mBundle);
			return mBundle;
		}
		int netID = mWifiManager.addNetwork(wifiConfig);
		if (netID != -1) {
			mWifiManager.enableNetwork(netID, true);
			mWifiManager.reconnect();
		}
		msgCount = 0;
		while ((connec.getActiveNetworkInfo() == null || connec
				.getActiveNetworkInfo().getType() != ConnectivityManager.TYPE_WIFI)
				|| !mWifiManager.getConnectionInfo().getSSID().equals(SSID)) {
			try {
				Thread.currentThread();
				msgCount++;
				if (msgCount == 30) {
					result = -1;
					break;
				}
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				LibraryLoger.e("SSID wrong!");
				result = -1;
				break;
			}
		}
		if (result != 0) {
			mBundle.putInt("NetID", -1);
			RollbackMobileState(mContext, mBundle);
			return mBundle;
		}
		mBundle.putInt("NetID", netID);
		return mBundle;
	}

	private void RollbackMobileState(Context context, Bundle mBundle) {
		String connectType = mBundle.getString("connectType");
		if (connectType.equals("mobile")) {
			CloseWifi();
			LibraryPhoneStateUtil.setMobileData(context, true);
		} else if (connectType.equals("wifi")) {
			if (OpenWifi()) {
				int msgCount = 0;
				int result = 0;
				while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
					try {
						// 为了避免程序一直while循环，让它睡个1000毫秒在检测……
						Thread.currentThread();
						msgCount++;
						if (msgCount == 30) {
							result = -1;
							break;
						}
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
						result = -1;
						break;
					}
				}
				if (result == 0) {
					for (WifiConfiguration wifiConfig : mConfiguredNets) {
						if (wifiConfig.status != Status.ENABLED) {
							mWifiManager.enableNetwork(wifiConfig.networkId,
									false);
						}
					}
					mWifiManager.reconnect();
				}
			}
		} else {
			// do Nothings
			CloseWifi();
		}
	}

	// 外部需要提供wifiLock
	public boolean closeTheConfigNet(Context context, Bundle mBundle) {
		if (mBundle != null) {
			int netID = mBundle.getInt("NetID");
			if (mWifiManager != null) {
				mWifiManager.disableNetwork(netID);
				mWifiManager.removeNetwork(netID);
				mConfiguredNets = mWifiManager.getConfiguredNetworks();
				// for (WifiConfiguration wifiConfig : mConfiguredNets) {
				// if (wifiConfig.status != Status.ENABLED) {
				// mWifiManager.enableNetwork(wifiConfig.networkId, false);
				// }
				// }
				// return mWifiManager.reconnect();
				RollbackMobileState(context, mBundle);
			}
		}
		return false;
	}

	// 然后是一个实际应用方法，只验证过没有密码的情况：
	// 分为三种情况：1没有密码2用wep加密3用wpa加密
	public WifiConfiguration CreateWifiInfo(String SSID, String Password,
			WifiCipherType Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		// WifiConfiguration tempConfig = this.IsExsits(SSID);
		// if (tempConfig != null) {
		// mWifiManager.removeNetwork(tempConfig.networkId);
		// }

		if (Type == WifiCipherType.WIFICIPHER_NOPASS) // WIFICIPHER_NOPASS
		{
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == WifiCipherType.WIFICIPHER_WEP) // WIFICIPHER_WEP
		{
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == WifiCipherType.WIFICIPHER_WPA) // WIFICIPHER_WPA
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	/**
	 * 加上锁
	 */
	private void lockWifi() {
		mWifiLock.acquire();
	}

	/**
	 * 释放锁
	 */
	private void releaseLock() {
		if (mWifiLock.isHeld()) {
			mWifiLock.release();
		}
	}

	private boolean AddConnectWiFi(String SSID, String Password) {
		lockWifi();
		WifiConfiguration wifiConfig = CreateWifiInfo(SSID, Password,
				WifiCipherType.WIFICIPHER_WPA);
		boolean enableNetwork = addNetwork(wifiConfig);
		releaseLock();
		return enableNetwork;
	}

	private boolean addNetwork(WifiConfiguration wcg) {
		int wcgID = mWifiManager.addNetwork(wcg);
		return mWifiManager.enableNetwork(wcgID, false);
	}

	/**
	 * 检测是否存在这个WIFI
	 * 
	 * @param SSID
	 * @return
	 */
	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

}
