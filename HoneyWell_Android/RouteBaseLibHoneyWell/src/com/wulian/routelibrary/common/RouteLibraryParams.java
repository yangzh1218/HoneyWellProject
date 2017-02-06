/**
 * Project Name:  RouteLibrary
 * File Name:     RouteLibraryParams.java
 * Package Name:  com.wulian.routelibrary.common
 * @Date:         2014-9-6
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.common;

import java.util.Date;
import java.util.HashMap;

/**
 * @ClassName: RouteLibraryParams
 * @Function: 库文件的参数
 * @Date: 2014-9-6
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class RouteLibraryParams {
	public static String getDecodeString(String needDecode, String key) {
		return getDecodeStringNative(needDecode, key, 300);
	}

	protected static native String getDecodeStringNative(String needDecode,
			String key, int time);
	
	public static native String EncodeMappingString(String encodeWiFiString,
			int needEncodeLength);

	/**
	 * @MethodName V3AppFlag
	 * @Function 查询设备标识
	 * @author Puml
	 * @date: 2016年4月27日
	 * @email puml@wuliangroup.cn
	 * @param device_id
	 *            设备id
	 * @param auth
	 *            Auth Token
	 * @return
	 */
	public static HashMap<String, String> V3AppFlag(String auth, String did) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("auth", auth);
		mMap.put("did", did);
		return mMap;
	}

	public static native HashMap<String, String> V3PartnerLogin(String username,
			String password,String appsecret);
	
	/**
	 * 
	 * @MethodName V3Login
	 * @Function V3登陆
	 * @author Puml
	 * @date: 2016年4月27日
	 * @email puml@wuliangroup.cn
	 * @param username
	 * @param password
	 * @param meta
	 * @return
	 */
	public static native HashMap<String, String> V3Login(String username,
			String password, boolean meta);

	/**
	 * @MethodName V3Logout
	 * @Function 注销
	 * @author Puml
	 * @date: 2016年6月2日
	 * @email puml@wuliangroup.cn
	 * @param auth
	 * @return
	 */
	public static HashMap<String, String> V3Logout(String auth) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("auth", auth);
		Date date = new Date();
		long timestamp = date.getTime() / 1000L;
		mMap.put("timestamp", String.valueOf(timestamp));
		return mMap;
	}

	/**
	 * @MethodName V3UserDevice
	 * @Function 修改设备描述
	 * @author Puml
	 * @date: 2016年6月3日
	 * @email puml@wuliangroup.cn
	 * @param auth
	 *            Auth Token
	 * @param did
	 *            设备惟一编号
	 * @param nick
	 *            设备别名
	 * @param desc
	 *            设备描述
	 * @return
	 */
	public static HashMap<String, String> V3UserDevice(String auth, String did,
			String nick, String desc) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("auth", auth);
		mMap.put("did", did);
		mMap.put("nick", nick);
		mMap.put("desc", desc);
		return mMap;
	}

	/**
	 * @MethodName V3UserDevices
	 * @Function 获取绑定设备
	 * @author Puml
	 * @date: 2016年6月2日
	 * @email puml@wuliangroup.cn
	 * @param type
	 *            设备类型，最大长度为6
	 * @param auth
	 *            Auth Token
	 * @return
	 */
	public static HashMap<String, String> V3UserDevices(String auth, String type) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("type", type);
		mMap.put("auth", auth);
		mMap.put("size","20");
		return mMap;
	}

	/**
	 * @MethodName V3UserPassword
	 * @Function 修改密码
	 * @author Puml
	 * @date: 2016年6月2日
	 * @email puml@wuliangroup.cn
	 * @param auth
	 * @param password
	 *            密码
	 * @return
	 */
	public static native HashMap<String, String> V3UserPassword(String auth,
			String password);

	public static HashMap<String, String> V3UserUserName(String auth,
			String username) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("auth", auth);
		mMap.put("username", username);
		Date date = new Date();
		long timestamp = date.getTime() / 1000;
		mMap.put("timestamp", String.valueOf(timestamp));
		return mMap;
	}

	/**
	 * @MethodName V3BindCheck
	 * @Function V3检查设备绑定
	 * @author Puml
	 * @date: 2016年4月26日
	 * @email puml@wuliangroup.cn
	 * @param device_id
	 *            设备id
	 * @param auth
	 *            Auth Token
	 * @return
	 */
	public static HashMap<String, String> V3BindCheck(String auth, String did) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("auth", auth);
		mMap.put("did", did);
		return mMap;
	}

	/**
	 * @MethodName V3BindResult
	 * @Function 查询设备绑定结果
	 * @author Puml
	 * @date: 2016年4月26日
	 * @email puml@wuliangroup.cn
	 * @param device_id
	 *            设备id
	 * @param auth
	 *            Auth Token
	 * @return
	 */
	public static HashMap<String, String> V3BindResult(String auth, String did) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("auth", auth);
		mMap.put("did", did);
		return mMap;
	}

	/**
	 * 
	 * @MethodName V3BindUnbind
	 * @Function TODO
	 * @author Puml
	 * @date: 2016年6月2日
	 * @email puml@wuliangroup.cn
	 * @param device_id
	 * @param auth
	 * @return
	 */
	public static HashMap<String, String> V3BindUnbind(String auth, String did) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("auth", auth);
		mMap.put("did", did);
		return mMap;
	}

	public static HashMap<String, String> V3TokenDownloadReplay(String auth, String did,String username) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("auth", auth);
		mMap.put("did", did);
		mMap.put("sdomain", username);
		return mMap;
	}

	public static HashMap<String, String> V3Share(String auth, String did,String username) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("auth", auth);
		mMap.put("did", did);
		mMap.put("username", username);
		return mMap;
	}

	public static HashMap<String, String> V3ShareRequest(String auth, String did,String desc) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("auth", auth);
		mMap.put("did", did);
		mMap.put("desc", desc);
		return mMap;
	}

	public static HashMap<String, String> V3ShareResponse(String auth, String did,String username, String action) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("auth", auth);
		mMap.put("did", did);
		mMap.put("username", username);
		mMap.put("action", action);
		return mMap;
	}

	public static HashMap<String, String> V3UnShare(String auth, String did,String username) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("auth", auth);
		mMap.put("did", did);
		mMap.put("username", username);
		return mMap;
	}

	public static HashMap<String, String> V3VersionStable(String search,int version) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("search", search);
		mMap.put("version", String.valueOf(version));
		return mMap;
	}

	public static HashMap<String, String> V3Owners(String auth) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("auth", auth);
		return mMap;
	}

	public static HashMap<String, String> V3ShareList(String auth, String did) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.put("auth", auth);
		mMap.put("did", did);
		return mMap;
	}

	/**
	 * @MethodName ConvertMapValue
	 * @Function 转化
	 * @author Puml
	 * @date: 2016年2月3日
	 * @email puml@wuliangroup.cn
	 * @param map
	 * @param auth
	 * @return
	 */
	public static HashMap<String, String> ConvertMapValue(
			HashMap<String, String> map, String auth) {
		HashMap<String, String> mMap = new HashMap<String, String>();
		mMap.putAll(map);
		if (mMap.containsKey("auth")) {
			mMap.put("auth", auth);
		}
		if (mMap.containsKey("timestamp")) {
			Date date = new Date();
			long timestamp = date.getTime() / 1000;
			mMap.put("timestamp", String.valueOf(timestamp));
		}
		return mMap;
	}

	/***************************** 局域网 *******************************/
	static {
		System.loadLibrary("wulianrouteparams");
	}
}
