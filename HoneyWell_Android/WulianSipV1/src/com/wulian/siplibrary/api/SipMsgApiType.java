/**
 * Project Name:  WulianLibrary
 * File Name:     SipMsgApiType.java
 * Package Name:  com.wulian.siplibrary.api
 * @Date:         2014年11月24日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.api;


import android.app.DownloadManager;

/**
 * @ClassName: SipMsgApiType
 * @Function: sip消息Api类型
 * @Date: 2014年11月24日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public enum SipMsgApiType {
	NOTIFY_WEB_ACCOUNT_INFO("-1",-1,RequestType.NOTIFY),//通知web账号信息
	// 通用API
	QUERY_DEVICE_DESCRIPTION_INFO("00", 0, RequestType.QUERY), // 查询设备描述信息
	QUERY_CAMERA_INFO("01", 1, RequestType.QUERY), // 查询摄像头配置信息
	CONFIG_CSC("02", 2, RequestType.CONFIG), // CSC设置
	CONFIG_ENCODE("03", 3, RequestType.CONFIG), // 编码设置
	CONFIG_VOICE_INTERCOM("04",4,RequestType.CONFIG),//语音对讲
	CONFIG_VOICE_MUTE("05",5,RequestType.CONFIG),//静音设置
	QUERY_LED_AND_VOICE_PROMPT_INFO("06",6,RequestType.QUERY),//查询LED及语音提示设置
	CONFIG_LED_AND_VOICE_PROMPT("07",7,RequestType.CONFIG),//设置LED及语音提示设置
	
	// 侦测联动布防
	CONFIG_MOVEMENT_DETECTION("20", 20, RequestType.CONFIG), // 配置运动侦测
	QUERY_MOVEMENT_DETECTION_INFO("21", 21, RequestType.QUERY), // 查询运动侦测配置信息

	CONFIG_BLOCK_DETECTION("22", 22, RequestType.CONFIG), // 遮挡侦测配置
	QUERY_BLOCK_DETECTION_INFO("23", 23, RequestType.QUERY), // 查询遮挡侦测配置信息

	CONFIG_LINKAGE_ARMING("39", 39, RequestType.CONFIG), // 联动布防设置
	QUERY_LINKAGE_ARMING_INFO("40", 40, RequestType.QUERY), // 获取联动布防信息

	// 报警
//	PUSH_ALARM_EVENT("60", 60, RequestType.PUSH), // 查询报警事件  推送
	QUERY_ALARM_EVENT("60", 60, RequestType.QUERY), // 查询报警事件
	QUERY_MULTI_ALARY_EVENT("62", 62, RequestType.QUERY), // 查询多个报警事件
	// PTZ
	QUERY_PTZ_INFO("90", 90, RequestType.QUERY), // 摄像头 PTZ 信息
	CONTROL_PTZ_MOVEMENT("91", -1, RequestType.CONTROL), // 控制 PTZ 运动****INFO

	CONTROL_PRESET("92", 92, RequestType.CONTROL), // 设置预置点
	CONTROL_DELETE_PRESET("93", 93, RequestType.CONTROL), // 删除预置点
	QUERY_PRESET("94", 94, RequestType.QUERY), // 查询预置点列表

	CONFIG_CRUISE_LINE("95", 95, RequestType.CONFIG), // 设置巡航路线
	CONFIG_DELETE_CRUISE_LINE("96", 96, RequestType.CONFIG), // 删除巡航路线
	QUERY_CRUISE_LINE("97", 97, RequestType.QUERY), // 查询巡航列表
	
	//存储回放系统
	QUERY_JPG_CAPTURE("120",120,RequestType.QUERY),// JPEG 抓拍
	QUERY_STORAGE_STATUS("122",122,RequestType.QUERY),//请求存储状态信息
	CONFIG_LOCAL_STORAGE_DEVICE_FORMAT("123",123,RequestType.CONFIG),//本地存储设备格式化
	CONFIG_NAS_DEVICE("124",124,RequestType.CONFIG),//NAS设备配置
	CONFIG_PRERECORD_PLAN("125",125,RequestType.CONFIG),//录像计划
	QUERY_PRERECORD_PLAN("126",126,RequestType.QUERY),//查询录像计划
	
	//权限管理
	NOTIFY_SYNCHRO_PERMISSION("150",150,RequestType.NOTIFY),//同步权限
	
	//固件
	QUERY_FIREWARE_VERSION("155",155,RequestType.QUERY),//获取固件版本
	NOTIFY_FIREWARE_UPDATE("156",156,RequestType.NOTIFY),//固件更新
	
	//状况
	QUERY_DEVICE_ONLINE("157",157,RequestType.QUERY),//查询摄像在线

	CONFIG_USER_PASSWORD("210", 210, RequestType.CONFIG), //配置用户名密码
	//OSS
	QUERY_HISTORY_RECORD("201",201,RequestType.QUERY),//查询历史记录
	CONTROL_START_RECORD("202",202,RequestType.CONTROL),//控制开始进行历史记录
	CONTROL_STOP_RECORD("203",203,RequestType.CONTROL),//控制停止进行历史记录
	CONTROL_HISTORY_RECORD_PROGRESS("204",204,RequestType.CONTROL),//控制历史记录进度
	CONTROL_ALARM_HISTORY_RECORD_PROGRESS("205",205,RequestType.CONTROL),//控制报警历史记录进度
	NOTIFY_HISTORY_RECORD_HEARTBEAT("209",209,RequestType.NOTIFY), //发送历史记录心跳

	//设置摄像机名称
	CONFIG_IPC_NAME("210",210,RequestType.CONFIG),//设置摄像机名称

	//查询或设置Web域名
	QUERY_WEB_DOMAIN("11",11,RequestType.QUERY),//查询Web域名
	CONFIG_WEB_DOMAIN("12",12,RequestType.CONFIG);//设置Web域名

	String requestCmd;// 请求命令
	int respondCmd;// 接收命令
	RequestType requestType;// 请求类型

	private SipMsgApiType(String request_cmd, int respond_cmd, RequestType type) {
		this.requestCmd = request_cmd;
		this.respondCmd = respond_cmd;
		this.requestType = type;
	}

	public String getRequestCmd() {
		return this.requestCmd;
	}

	public int getRespondCmd() {
		return this.respondCmd;
	}

	public RequestType getRequestType() {
		return this.requestType;
	}

	public static SipMsgApiType getSipTypeByRespondCmd(String respond) {
		if (respond.equals("")) {
			return null;
		}
		int respondNum = Integer.valueOf(respond);
		for (SipMsgApiType item : SipMsgApiType.values()) {
			if (item.getRespondCmd() == respondNum) {
				return item;
			}
		}
		return null;
	}

	public static SipMsgApiType getSipTypeByRequestCmd(String request) {
		for (SipMsgApiType item : SipMsgApiType.values()) {
			if (item.getRequestCmd().equalsIgnoreCase(request)) {
				return item;
			}
		}
		return null;
	}

}
