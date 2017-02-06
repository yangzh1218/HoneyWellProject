/**
 * Project Name:  RouteBaseLibV2
 * File Name:     ClientException.java
 * Package Name:  com.wulian.routelibrary.exception
 * @Date:         2016年5月31日
 * Copyright (c)  2016, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.exception;

import com.wulian.routelibrary.common.RouteErrorCode;

/**
 * @ClassName: ClientException
 * @Function: TODO
 * @Date: 2016年5月31日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class ClientException extends Exception {
	/**
	 * serialVersionUID 作用:TODO
	 */
	private static final long serialVersionUID = 1419289198030479230L;

	RouteErrorCode mErrorCode;

	public ClientException() {
		super();
	}

	public ClientException(String msg) {
		super(msg);
	}

	public ClientException(String msg, RouteErrorCode code) {
		super(msg);
		this.mErrorCode = code;
	}

	public ClientException(String msg, Exception exception) {
		super(msg, exception);
	}

	public ClientException(Exception exception) {
		super(exception);
	}

	public RouteErrorCode getErrorCode() {
		return this.mErrorCode;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
