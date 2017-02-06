/**
 * Project Name:  RouteLibrary
 * File Name:     RouteApiType.java
 * Package Name:  com.wulian.routelibrary.common
 * @Date:         2014-9-6
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.common;

/**
 * @ClassName: RouteApiType
 * @Function: 路径类型
 * @Date: 2014-9-6
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public enum RouteApiType {
	V3_APP_FLAG("/v3/app/flag", RequestType.HTTP_POST),// 查询设备标识
	V3_SERVER("/v3/server", RequestType.HTTP_POST), // 获取服务器域名

	V3_PARTNER_REGISTER("/v3/partner/login", RequestType.HTTP_POST), // V3注册
	V3_PARTNER_LOGIN("/v3/partner/login", RequestType.HTTP_POST), // V3登陆
	V3_REGISTER("/v3/login", RequestType.HTTP_POST), // V3注册
	V3_LOGIN("/v3/login", RequestType.HTTP_POST), // V3登陆
	V3_LOGOUT("/v3/logout", RequestType.HTTP_POST), // 注销
	V3_USER_DEVICE("/v3/user/device", RequestType.HTTP_POST), // 修改设备描述
	V3_USER_DEVICES("/v3/user/devices", RequestType.HTTP_POST), // 获取绑定设备
	V3_USER_PASSWORD("/v3/user/password", RequestType.HTTP_POST), // 修改密码
	V3_USER_USERNAME("/v3/user/username", RequestType.HTTP_POST), // 绑定用户名，只有开头带user的初始用户可以绑定用户名

	V3_BIND_CHECK("/v3/bind/check", RequestType.HTTP_POST), // 检查设备绑定
	V3_BIND_RESULT("/v3/bind/result", RequestType.HTTP_POST), // 检查绑定状态
	V3_BIND_UNBIND("/v3/bind/unbind", RequestType.HTTP_DELETE), // 解除设备绑定
	V3_DEVICE_OWNERS("/v3/device/owners", RequestType.HTTP_POST),

	V3_VERSION_STABLE("/v3/version/stable", RequestType.HTTP_GET),//查询稳定版本更新

	V3_SHARE("/v3/share", RequestType.HTTP_POST), // 绑定用户添加设备授权
	V3_SHARE_LIST("/v3/share/list", RequestType.HTTP_POST),
	V3_SHARE_REQUEST("/v3/share/request", RequestType.HTTP_POST), // 请求授权，等待响应授权
	V3_SHARE_RESPONSE("/v3/share/response", RequestType.HTTP_POST), // 响应授权请求
	V3_SHARE_UNSHARE("/v3/share/unshare", RequestType.HTTP_DELETE), // 绑定用户删除设备授权
	V3_TOKEN_DOWNLOAD_REPLAY("/v3/token/download/replay", RequestType.HTTP_POST), // 获取回看秘钥
	V3_TOKEN_UPLOAD_AVATAR("/v3/token/upload/avatar", RequestType.HTTP_POST), // 获取头像上传秘钥
	V3_TOKEN_UPLOAD_LOG("/v3/token/upload/log", RequestType.HTTP_POST); // 获取日志上传秘钥


	private String mURL;// 路径
	private RequestType mRequestType;// 请求类型

	private RouteApiType(String url, RequestType requestType) {
		this.mURL = url;
		this.mRequestType = requestType;
	}

	/**
	 * 
	 * @ClassName getmType
	 * @Function 获取请求类型
	 * @author Puml
	 * @date: 2014-9-6 email puml@wuliangroup.cn
	 * @return
	 */
	public RequestType getRequestType() {
		return mRequestType;
	}

	/**
	 * 
	 * @ClassName getmURL
	 * @Function 获取请求路径
	 * @author Puml
	 * @date: 2014-9-6 email puml@wuliangroup.cn
	 * @return
	 */
	public String getmURL() {
		return mURL;
	}

	public void setRequestType(RequestType requestType) {
		this.mRequestType = requestType;
	}

	public void setmURL(String mURL) {
		this.mURL = mURL;
	}

}
