/**
 * Project Name:  RouteLibrary
 * File Name:     ErrorCode.java
 * Package Name:  com.wulian.routelibrary.common
 * @Date:         2014-9-6
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.common;

/**
 * @ClassName: ErrorCode
 * @Function: 网络请求错误码
 * @Date: 2014-9-6
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public enum RouteErrorCode {
	SUCCESS(200, "成功", "Success"), //
	INVALID_IO(-1, "请求错误", "Invalid Request"), //
	UNKNOWN_ERROR(-2, "未知错误", "Unknown Error"), //
	INVALID_PARAMS(400, "参数不合法","Invalid Params"), //
	INVALID_REQUEST(401, "非法请求","Invalid Request"), //
	FORBIDDEN_ERROR(403, "越权的访问","Forbidden"),//
	NOT_FOUND(404, "路径未找到", "Path not found"),// 
	PARAMS_CONFLICT(409, "参数冲突", "Params Conflict"),// 
	LIMIT_EXCEEDED(416, "超过限制", "More than limit"), //
	NO_MORE_ID(429, "用户ID分配完", "No More ID"), //
	PARAMS_ILLEGAL(451, "非法参数", "Params Illegal"), //
	SERVER_ERROR(500, "服务器错误", "Server Error"), //
	SERVER_TIMEOUT_ERROR(503, "服务器连接超时", "Server Timeout Error");//

	int errorCode;// 错误码
	String description;// 描述
	String description_en;// 描述

	private RouteErrorCode(int errorCode, String desc, String desc_en) {
		this.errorCode = errorCode;
		this.description = desc;
		this.description_en = desc_en;
	}

	/**
	 * 
	 * @ClassName getErrorCode
	 * @Function 获取错误码
	 * @author Puml
	 * @date: 2014-9-6 email puml@wuliangroup.cn
	 * @return
	 */
	public int getErrorCode() {
		return this.errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription_en() {
		return description_en;
	}

	public void setDescription_en(String description_en) {
		this.description_en = description_en;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @ClassName getTypeByCode
	 * @Function 根据Code获取类型
	 * @author Puml
	 * @date: 2014-9-6 email puml@wuliangroup.cn
	 * @param code
	 * @return
	 */
	public static RouteErrorCode getTypeByCode(int code) {
		for (RouteErrorCode mErrorCode : RouteErrorCode.values()) {
			if (mErrorCode.getErrorCode() == code) {
				return mErrorCode;
			}
		}
		UNKNOWN_ERROR.setDescription("未知错误" + ":" + code);
		UNKNOWN_ERROR.setDescription_en("Unknown Error" + ":" + code);
		return UNKNOWN_ERROR;
	}
}
