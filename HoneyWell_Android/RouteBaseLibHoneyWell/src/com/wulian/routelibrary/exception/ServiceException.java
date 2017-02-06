/**
 * Project Name:  RouteLibrary
 * File Name:     ServerException.java
 * Package Name:  com.wulian.routelibrary.exception
 * Date:          2014-9-5
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.exception;

/**
 * ClassName: ServerException Function:
 * 
 * @author Puml email puml@wuliangroup.cn
 */
public class ServiceException extends Exception {
	/**
	 * serialVersionUID 作用:TODO
	 */
	private static final long serialVersionUID = -1373453557480246617L;
	private int mStatusCode;

	public ServiceException() {
		super();
	}

	public ServiceException(int statusCode, String msg) {
		super(msg);
		this.mStatusCode = statusCode;
	}
	
	public int getStatusCode() {
		return this.mStatusCode;
	}
	

	public ServiceException(String msg) {
		super(msg);
	}

	public ServiceException(String msg, Exception exception) {
		super(msg, exception);
	}

	public ServiceException(Exception exception) {
		super(exception);
	}

	@Override
	public String toString() {
		return "ServerException " + super.toString();
	}
}
