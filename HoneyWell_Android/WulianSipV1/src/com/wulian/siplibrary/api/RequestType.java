/**
 * Project Name:  WulianLibrary
 * File Name:     RequestType.java
 * Package Name:  com.wulian.siplibrary.api
 * @Date:         2014年11月24日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.api;

/**
 * @ClassName: RequestType
 * @Function: 请求类型
 * @Date: 2014年11月24日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public enum RequestType {
	PUSH,//推送动作
	CONTROL, // 控制动作
	CONFIG, // 配置动作
	QUERY, // 查询动作
	NOTIFY;// 通知动作
	
}
