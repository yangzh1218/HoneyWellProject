/**
 * Project Name:  RouteLibrary
 * File Name:     ConfigLibrary.java
 * Package Name:  com.wulian.routelibrary
 * Date:          2014-9-5
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.routelibrary;

/**
 * ClassName: ConfigLibrary Function: Date: 2014-9-5
 * 
 * @author Administrator
 */
public class ConfigLibrary {
	/** Network request parameters of the code **/
	public static final String ENCODING = "UTF-8";
	/** The network connection overtime 10000 ms **/
	public static final int TIMEOUT = 10000;
	public static final int MAX_REQUESTS = 1;
	/** Https Server URL **/
	// public static final String HTTPS_SERVER_URL = "api.simplegg.com";
	public static final String HTTPS_SERVER_URL = "hw.pu.sh.gg";
	public static final String TEST_HTTPS_SERVER_URL = "test.sh.gg";
	/** App Name **/
	public static String APP_NAME = "";// 
	/** OS **/
	public static final String OS = "android";
	/** Https 常量 **/
	public static final String HTTPS_PROTOCOL = "https://";
	public static final int DEFAULT_BASE_THREAD_POOL_SIZE = 5;
}
