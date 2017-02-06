/**
 * Project Name:  WulianLibrary
 * File Name:     SipHandler.java
 * Package Name:  com.wulian.siplibrary.api
 * @Date:         2014年10月25日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.api;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.wulian.siplibrary.model.cruiseline.Preset;
import com.wulian.siplibrary.model.cruiseline.TourModel;
import com.wulian.siplibrary.model.linkagedetection.DetectionAction;
import com.wulian.siplibrary.model.linkagedetection.LinkageDetectionModel;
import com.wulian.siplibrary.model.linkagedetection.TimePeriod;
import com.wulian.siplibrary.model.prerecordplan.PreRecordPlanModel;
import com.wulian.siplibrary.utils.WulianLog;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: SipHandler
 * @Function: Sip辅助处理器
 * @Date: 2014年10月25日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class SipHandler {
	private static final String TAG = "SipHandler";

	/******************************** 说明 *********************************/
	// 命令指导规程 请求： 1、 control 控制动作; 2、 config 配置动作; 2、 query 查询动作; 3、 notify 通知动作
	// 应答： reponse 请求动作的应答 answer 针对原来返回的cmd使用 ;
	// eventType "< 0 ： 非法或不支持的事件; 1：运动侦测事件; 2： 遮挡侦测事件; 9： PIR侦测事件（海康）"
	// ganged "< 0： 非法或不支持的联动; 1： 录像联动; 8： 上传中心(海康);9: 上传FTP（海康）"
	// status "OK: 命令执行正确;501：命令未支持"

	/**
	 * @MethodName NotifyWebAccountInfo
	 * @Function 通知web账号信息
	 * @author Puml
	 * @date: 2014年12月19日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param device_id
	 *            设备id
	 * @param timestamp
	 *            时间戳
	 * @param auth
	 *            登录校验
	 * @param type
	 *            类型:request,add,response_accept,response_decline,delete
	 * @param username
	 *            用户名/邮箱/手机(type为request时不填)
	 * @return
	 */
	public static String NotifyWebAccountInfo(String uri, String device_id,
			String type, String username) {
		HashMap<String, String> hm = new HashMap<String, String>();
		Date date = new Date();
		long timestamp = date.getTime() / 1000;
		hm.put("uri", uri);
		if (!TextUtils.isEmpty(device_id)) {
			hm.put("device_id", device_id);
		}
		hm.put("timestamp", String.valueOf(timestamp));
		hm.put("type", type);
		if (!TextUtils.isEmpty(username)) {
			hm.put("username", username);
		}
		return handleCreateXmlString(SipMsgApiType.NOTIFY_WEB_ACCOUNT_INFO, hm);
	}

	/**
	 * @MethodName QueryDeviceDescriptionInfo
	 * @Function 查询设备描述信息
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String QueryDeviceDescriptionInfo(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(
				SipMsgApiType.QUERY_DEVICE_DESCRIPTION_INFO, hm);
	}

	/**
	 * @MethodName QueryCameraInfo
	 * @Function 查询摄像头配置信息
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String QueryCameraInfo(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(SipMsgApiType.QUERY_CAMERA_INFO, hm);
	}

	/**
	 * @MethodName ConfigCSC
	 * @Function CSC设置
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param CSC
	 *            CSC值 包括亮度、对比度、饱和度、清晰度 (范围0~100)如：50,50,50,50
	 * @return
	 */
	public static String ConfigCSC(String uri, int seq, String CSC) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("CSC", CSC);
		return handleCreateXmlString(SipMsgApiType.CONFIG_CSC, hm);
	}

	/**
	 * @MethodName ConfigEncode
	 * @Function 编码设置
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param DPI
	 *            设备当前分辨率 如640x480
	 * @param fps
	 *            设备当前帧率 如30
	 * @param quality
	 *            图像质量级别: 0 一般; 1 最佳; 2 高质量
	 * @return
	 */
	public static String ConfigEncode(String uri, int seq, String DPI, int fps,
			int quality) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("DPI", DPI);
		hm.put("fps", String.valueOf(fps));
		hm.put("quality", String.valueOf(quality));
		return handleCreateXmlString(SipMsgApiType.CONFIG_ENCODE, hm);
	}

	/**
	 * @MethodName ConfigVoiceIntercom
	 * @Function 语音对讲
	 * @author Puml
	 * @date: 2015年4月1日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param audio
	 *            audio 模式： input ：客户端输入 output : 客户端输出
	 * @return
	 */
	public static String ConfigVoiceIntercom(String uri, int seq, String audio) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("audio", audio);
		return handleCreateXmlString(SipMsgApiType.CONFIG_VOICE_INTERCOM, hm);
	}

	/**
	 * @MethodName ConfigVoiceMute
	 * @Function 静音设置
	 * @author Puml
	 * @date: 2015年4月1日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param mute
	 *            是否静音： true 静音 false 不静音
	 * @return
	 */
	public static String ConfigVoiceMute(String uri, int seq, String mute) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("mute", mute);
		return handleCreateXmlString(SipMsgApiType.CONFIG_VOICE_MUTE, hm);
	}

	/**
	 * @MethodName: QueryLedAndVoicePromptInfo
	 * @Function: 查询LED及语音提示设置
	 * @author: yuanjs
	 * @date: 2015年10月22日
	 * @email: jiansheng.yuan@wuliangroup.com
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String QueryLedAndVoicePromptInfo(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(
				SipMsgApiType.QUERY_LED_AND_VOICE_PROMPT_INFO, hm);
	}

	/**
	 * @MethodName: ConfigLedAndVoicePrompt
	 * @Function: 设置LED及语音提示设置
	 * @author: yuanjs
	 * @date: 2015年10月22日
	 * @email: jiansheng.yuan@wuliangroup.com
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param led_on
	 *            0 : 关闭LED 1 ：开启LED
	 * @param audio_online
	 *            上线提醒 0 ：关闭 ;1 : 开启
	 * @return
	 */
	public static String ConfigLedAndVoicePrompt(String uri, int seq,
			String led_on, String   audio_online) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
//		if(!TextUtils.isEmpty(led_on)) {
//		}
		hm.put("led_on", led_on);
//		if(!TextUtils.isEmpty(audio_online)) {
//		}
		hm.put("audio_online", audio_online);
		return handleCreateXmlString(SipMsgApiType.CONFIG_LED_AND_VOICE_PROMPT,
				hm);
	}

	/**
	 * @MethodName ConfigMovementDetection
	 * @Function 配置运动侦测
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param enable
	 *            运动侦测使用开关: false：禁止;true：使能
	 * @param sensitivity
	 *            运动侦测灵敏度【0~100】
	 * @param areas
	 *            运动侦测区域 如：0,0,320,240
	 * @return
	 */
	public static String ConfigMovementDetection(String uri, int seq,
			boolean enable, int sensitivity, String[] areas) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("enable", enable ? "true" : "false");
		hm.put("sensitivity", String.valueOf(sensitivity));
		return handleCreateMovementDetectionXmlString(
				SipMsgApiType.CONFIG_MOVEMENT_DETECTION, hm, areas);
	}

	/**
	 * @MethodName QueryMovementDetectionInfo
	 * @Function 查询运动侦测配置信息
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String QueryMovementDetectionInfo(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(
				SipMsgApiType.QUERY_MOVEMENT_DETECTION_INFO, hm);
	}

	/**
	 * @MethodName ConfigBlockDetection
	 * @Function 遮挡侦测配置
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param enable
	 *            运动侦测使用开关:false：禁止;true：使能
	 * @param sensitivity
	 *            遮挡侦测灵敏度【0~100】
	 * @param area
	 *            区域， 格式x1,y1,x2,y2
	 * @return
	 */
	public static String ConfigBlockDetection(String uri, int seq,
			boolean enable, int sensitivity, String area) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("enable", enable ? "true" : "false");
		hm.put("sensitivity", String.valueOf(sensitivity));
		hm.put("area", area);
		return handleCreateXmlString(SipMsgApiType.CONFIG_BLOCK_DETECTION, hm);
	}

	/**
	 * @MethodName QueryBlockDetectionInfo
	 * @Function 查询遮挡侦测配置信息
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String QueryBlockDetectionInfo(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(SipMsgApiType.QUERY_BLOCK_DETECTION_INFO,
				hm);
	}

	/**
	 * @MethodName ConfigLinkageArming
	 * @Function 联动布防设置
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param eventType
	 *            事件类型（参考开头资料）
	 * @param ganged
	 *            联动类型（参考开头资料）
	 * @param detections
	 *            布防时间表模型
	 * @return
	 */
	public static String ConfigLinkageArming(String uri, int seq,
			int eventType, int ganged, LinkageDetectionModel detections) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("eventType", String.valueOf(eventType));
		hm.put("ganged", String.valueOf(ganged));
		return handleCreateLinkageDetectionXmlString(
				SipMsgApiType.CONFIG_LINKAGE_ARMING, hm, detections);
	}

	/**
	 * @MethodName QueryLinkageArmingInfo
	 * @Function 获取联动布防信息
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param eventType
	 *            事件类型（参考开头资料）
	 * @return
	 */
	public static String QueryLinkageArmingInfo(String uri, int seq,
			int eventType) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("eventType", String.valueOf(eventType));
		return handleCreateXmlString(SipMsgApiType.QUERY_LINKAGE_ARMING_INFO,
				hm);
	}

	/**
	 * @MethodName QueryAlarmEvent
	 * @Function 查询报警事件
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param eventType
	 *            事件类型（参考开头资料）all：所有
	 * @param datetime
	 *            结束时间，格式（YYYY-MM-DD HH-MM—SS）
	 * @return
	 */
	public static String QueryAlarmEvent(String uri, int seq, int alarmId) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("alarmID", String.valueOf(alarmId));
		hm.put("session",getIMEI());
//		hm.put("eventType", String.valueOf(eventType));
//		hm.put("datetime", datetime);
		return handleCreateXmlString(SipMsgApiType.QUERY_ALARM_EVENT, hm);
	}

	public static String QueryMultiAlarmEvent(String uri, int seq, int[] alarmIdArray) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("session",getIMEI());
//		hm.put("eventType", String.valueOf(eventType));
//		hm.put("datetime", datetime);
		return handleCreateMultiAlarmXmlString(SipMsgApiType.QUERY_MULTI_ALARY_EVENT, hm,alarmIdArray);
	}


	private static String getIMEI() {
		String m_szDevIDShort = "35" + //we make this look like a valid IMEI
				Build.BOARD.length()%10 +
				Build.BRAND.length()%10 +
				Build.CPU_ABI.length()%10 +
				Build.DEVICE.length()%10 +
				Build.DISPLAY.length()%10 +
				Build.HOST.length()%10 +
				Build.ID.length()%10 +
				Build.MANUFACTURER.length()%10 +
				Build.MODEL.length()%10 +
				Build.PRODUCT.length()%10 +
				Build.TAGS.length()%10 +
				Build.TYPE.length()%10 +
				Build.USER.length()%10 ; //13 digits
		return m_szDevIDShort;
	}

	public static String ConfigUserPassword(String uri, int seq, String IPCname, String user, String passwd) {
		HashMap<String, String> hm = new HashMap<String, String>();
		if(user.isEmpty() || passwd.isEmpty())
		{
			hm.put("uri", uri);
			hm.put("seq", String.valueOf(seq));
			hm.put("IPCname",IPCname);
			hm.put("session",getIMEI());
		}
		else
		{
			hm.put("uri", uri);
			hm.put("seq", String.valueOf(seq));
			hm.put("IPCname",IPCname);
			hm.put("user",user);
			hm.put("passwd",passwd);
			hm.put("session",getIMEI());
		}

		return handleCreateXmlString(SipMsgApiType.CONFIG_USER_PASSWORD, hm);
	}

	/**
	 * @MethodName QueryPTZInfo
	 * @Function 摄像头 PTZ 信息
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String QueryPTZInfo(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(SipMsgApiType.QUERY_PTZ_INFO, hm);
	}

	/**
	 * @MethodName ControlPTZMovement
	 * @Function 控制 PTZ 运动
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param pan
	 *            水平方向速度: >0 向右;=0 停止;<0 向左
	 * @param tilt
	 *            垂直方向速度:>0 向上;=0 停止;<0 向下
	 * @return
	 */
	public static String ControlPTZMovement(String uri, int pan, int tilt) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("pan", String.valueOf(pan));
		hm.put("tilt", String.valueOf(tilt));
		return handleCreateXmlString(SipMsgApiType.CONTROL_PTZ_MOVEMENT, hm);
	}

	public static String AlarmVideoInfo(String uri, int alarmId) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("alarmID", String.valueOf(alarmId));
		return handleCreateXmlString(SipMsgApiType.QUERY_ALARM_EVENT, hm);
	}

	/**
	 * @MethodName ControlPreset
	 * @Function 设置预置点
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param preset
	 *            预置点编号
	 * @return
	 */
	public static String ControlPreset(String uri, int seq, String preset) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("preset", preset);
		return handleCreateXmlString(SipMsgApiType.CONTROL_PRESET, hm);
	}

	/**
	 * @MethodName ControlDeletePreset
	 * @Function 删除预置点
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param preset
	 *            预置点编号
	 * @return
	 */
	public static String ControlDeletePreset(String uri, int seq, String preset) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("preset", preset);
		return handleCreateXmlString(SipMsgApiType.CONTROL_DELETE_PRESET, hm);
	}

	/**
	 * @MethodName QueryPreset
	 * @Function 查询预置点列表
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String QueryPreset(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(SipMsgApiType.QUERY_PRESET, hm);
	}

	/**
	 * @MethodName ConfigCruiseLine
	 * @Function 设置巡航路线
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param tours
	 *            巡回路线模型
	 * @return
	 */
	public static String ConfigCruiseLine(String uri, int seq, TourModel tours) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateCruiseLineXmlString(
				SipMsgApiType.CONFIG_CRUISE_LINE, hm, tours);
	}

	/**
	 * @MethodName ConfigDeleteCruiseLine
	 * @Function 删除巡航路线
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param tours
	 *            需要删除的巡回路线
	 * @return
	 */
	public static String ConfigDeleteCruiseLine(String uri, int seq,
			String tours) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("tours", tours);
		return handleCreateXmlString(SipMsgApiType.CONFIG_DELETE_CRUISE_LINE,
				hm);
	}

	/**
	 * @MethodName QueryCruiseLine
	 * @Function 查询巡航列表
	 * @author Puml
	 * @date: 2014年11月25日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String QueryCruiseLine(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(SipMsgApiType.QUERY_CRUISE_LINE, hm);
	}

	/**
	 * @MethodName QueryJPGCapture
	 * @Function JPEG 抓拍
	 * @author Puml
	 * @date: 2015年4月16日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String QueryJPGCapture(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(SipMsgApiType.QUERY_JPG_CAPTURE, hm);
	}

	/**
	 * @MethodName QueryStorageStatus
	 * @Function 请求存储状态信息
	 * @author Puml
	 * @date: 2015年5月27日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String QueryStorageStatus(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(SipMsgApiType.QUERY_STORAGE_STATUS, hm);
	}

	/**
	 * @MethodName ConfigLocalStorageDeviceFormat
	 * @Function 本地存储设备格式化
	 * @author Puml
	 * @date: 2015年5月27日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param format_id
	 *            格式化磁盘号
	 * @return
	 */
	public static String ConfigLocalStorageDeviceFormat(String uri, int seq,
			int format_id) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("format_id", String.valueOf(format_id));
		return handleCreateXmlString(
				SipMsgApiType.CONFIG_LOCAL_STORAGE_DEVICE_FORMAT, hm);
	}

	/**
	 * @MethodName ConfigPrerecordPlan
	 * @Function 录像计划
	 * @author Puml
	 * @date: 2015年5月27日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param enable
	 *            启用录像计划 false：停止 true：启动
	 * @param prerecord
	 *            预录时间(单位s)
	 * @param predalay
	 *            预录延时(单位s)
	 * @param schedule
	 *            任务表
	 * @return
	 */
	public static String ConfigPrerecordPlan(String uri, int seq,
			boolean enable, int prerecord, int predelay,
			PreRecordPlanModel schedule) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("enable", String.valueOf(enable));
		hm.put("prerecord", String.valueOf(prerecord));
		hm.put("predelay", String.valueOf(predelay));
		return handlePreRecordPlanXmlString(
				SipMsgApiType.CONFIG_PRERECORD_PLAN, hm, enable, schedule);
	}

	/**
	 * @MethodName QueryPrerecordPlan
	 * @Function 查询录像计划
	 * @author Puml
	 * @date: 2015年5月27日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String QueryPrerecordPlan(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(SipMsgApiType.QUERY_PRERECORD_PLAN, hm);
	}

	/**
	 * @MethodName SynchroPermission
	 * @Function 同步权限
	 * @author Puml
	 * @date: 2014年12月8日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String NotifySynchroPermission(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(SipMsgApiType.NOTIFY_SYNCHRO_PERMISSION,
				hm);
	}

	/**
	 * @MethodName QueryFirewareVersion
	 * @Function 获取固件版本
	 * @author Puml
	 * @date: 2014年12月22日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String QueryFirewareVersion(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(SipMsgApiType.QUERY_FIREWARE_VERSION, hm);
	}

	/**
	 * @MethodName NotifyFirewareUpdate
	 * @Function 固件更新
	 * @author Puml
	 * @date: 2014年12月22日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param version
	 *            从服务器拉取的最新固件版本
	 * @param version_id
	 *            从服务器拉取的最新固件版本号，整数
	 * @return
	 */
	public static String NotifyFirewareUpdate(String uri, int seq,
			String version, int version_id) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("version", version);
		hm.put("version_id", String.valueOf(version_id));
		return handleCreateXmlString(SipMsgApiType.NOTIFY_FIREWARE_UPDATE, hm);
	}

	/**
	 * @MethodName QueryDeviceOnline
	 * @Function 查询摄像在线
	 * @author Puml
	 * @date: 2014年12月29日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String QueryDeviceOnline(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(SipMsgApiType.QUERY_DEVICE_ONLINE, hm);
	}

	/**
	 * @MethodName QueryHistoryRecord
	 * @Function 查询历史记录
	 * @author Puml
	 * @date: 2015年10月22日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @return
	 */
	public static String QueryHistoryRecord(String uri, int seq) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		return handleCreateXmlString(SipMsgApiType.QUERY_HISTORY_RECORD, hm);
	}

	/**
	 * @MethodName ControlStartRecord
	 * @Function 控制开始进行历史记录
	 * @author Puml
	 * @date: 2015年10月22日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 *  @param requestID
	 *  		  手机唯一标示符
	 * @return
	 */
	public static String ControlStartRecord(String uri, int seq,String requestID) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("requestID", requestID);
		return handleCreateXmlString(SipMsgApiType.CONTROL_START_RECORD, hm);
	}

	/**
	 * @MethodName ControlStopRecord
	 * @Function 控制停止进行历史记录
	 * @author Puml
	 * @date: 2015年10月22日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param sessionId
	 *            一段时间内的会话
	 * @return
	 */
	public static String ControlStopRecord(String uri, int seq, String sessionID) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("sessionID", sessionID);
		return handleCreateXmlString(SipMsgApiType.CONTROL_STOP_RECORD, hm);
	}

	/**
	 * @MethodName ControlHistoryRecordProgress
	 * @Function 控制历史记录进度
	 * @author Puml
	 * @date: 2015年10月22日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param sessionId
	 *            一段时间内的会话
	 * @param progress
	 *            进度（时间戳，必须是分钟,比如:1427841060）
	 * @return
	 */
	public static String ControlHistoryRecordProgress(String uri, int seq,
			String sessionID, long progress) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("sessionID", sessionID);
		hm.put("progress", String.valueOf(progress));
		return handleCreateXmlString(
				SipMsgApiType.CONTROL_HISTORY_RECORD_PROGRESS, hm);
	}

	/**

	 * @param uri
	 * @param seq
	 * @param period
	 * @param local
	 * @param alarmID
	 * @param sessionID
     * @param progress
     * @return
     */
	public static String ControlAlarmHistoryRecordProgress(String uri, int seq,int period,boolean local,int alarmID,
													  String sessionID, long progress) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("period",String.valueOf(period));
		hm.put("local",local?"true":"false");
		hm.put("alarmID",String.valueOf(alarmID));
		hm.put("seq", String.valueOf(seq));
		hm.put("sessionID", sessionID);
		hm.put("progress", String.valueOf(progress));
		return handleCreateXmlString(
				SipMsgApiType.CONTROL_ALARM_HISTORY_RECORD_PROGRESS, hm);
	}

	/**
	 * @MethodName NotifyHistoryRecordHeartbeat
	 * @Function 发送历史记录心跳
	 * @author Puml
	 * @date: 2015年10月22日
	 * @email puml@wuliangroup.cn
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param sessionId
	 *            一段时间内的会话
	 * @return
	 */
	public static String NotifyHistoryRecordHeartbeat(String uri, int seq,
			String sessionID) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("uri", uri);
		hm.put("seq", String.valueOf(seq));
		hm.put("sessionID", sessionID);
		return handleCreateXmlString(
				SipMsgApiType.NOTIFY_HISTORY_RECORD_HEARTBEAT, hm);
	}

	/**
	 * @MethodName ConfigIpcName
	 * @Function 修改摄像机名称
	 * @author yangzhou
	 * @date: 2016年10月28日
	 * @email zhou.yang@wuliangroup.com
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param session
	 *            手机标识号
	 * @param IPCname
	 *            摄像机名称
	 *
	 * @return
	 */

	  public  static String ConfigIpcName(String uri ,int seq,String ipcname)
	  {
		  HashMap<String,String> hm = new HashMap<String,String>();
		  hm.put("uri",uri);
		  hm.put("seq",String.valueOf(seq));
		  hm.put("session",getIMEI());
		  hm.put("IPCname",ipcname);
		  return handleCreateXmlString(
				  SipMsgApiType.CONFIG_IPC_NAME, hm);

	  }
	/**
	 * @MethodName QueryWebDomian
	 * @Function 查询摄像机域名
	 * @author yangzhou
	 * @date: 2016年10月28日
	 * @email zhou.yang@wuliangroup.com
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 *
	 * @return
	 */
	public static String QueryWebDomian(String uri,int seq)
	{
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put("uri",uri);
		hm.put("seq",String.valueOf(seq));
		return handleCreateXmlString(
				SipMsgApiType.QUERY_WEB_DOMAIN, hm);
	}
	/**
	 * @MethodName ConfigWebDomain
	 * @Function 设置摄像机域名
	 * @author yangzhou
	 * @date: 2016年10月28日
	 * @email zhou.yang@wuliangroup.com
	 * @param uri
	 *            发送者的URI (URI 格式)
	 * @param seq
	 *            序列号
	 * @param domain
	 *            Web域名
	 * @return
	 */
	public static String ConfigWebDomain(String uri,int seq,String domain)
	{
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put("uri",uri);
		hm.put("seq",String.valueOf(seq));
		hm.put("domain",domain);
		return handleCreateXmlString(
				SipMsgApiType.CONFIG_WEB_DOMAIN, hm);
	}

	// 处理一般XML字符串
	private static String handleCreateXmlString(SipMsgApiType type,
			HashMap<String, String> hm) {
		String result = "";
		try {
			String root = type.getRequestType().name()
					.toLowerCase(Locale.getDefault());
			StringWriter xmlWriter = new StringWriter();
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlSerializer xmlSerializer = factory.newSerializer();
			xmlSerializer.setOutput(xmlWriter); // 保存创建的xml
			xmlSerializer.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					true);
			xmlSerializer.startDocument("utf-8", null); // <?xml version='1.0'
														// encoding='UTF-8'
														// standalone='yes' ?>
			xmlSerializer.startTag("", root);
			Iterator<Entry<String, String>> ite = hm.entrySet().iterator();
			xmlSerializer.startTag("", "cmd");
			xmlSerializer.text(type.getRequestCmd());
			xmlSerializer.endTag("", "cmd");

			while (ite.hasNext()) {
				Entry<String, String> item = ite.next();
				xmlSerializer.startTag("", item.getKey());
				xmlSerializer.text(item.getValue());
				xmlSerializer.endTag("", item.getKey());
			}
			xmlSerializer.endTag("", root);
			xmlSerializer.endDocument();
			result = xmlWriter.toString();
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (IOException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		WulianLog.d(TAG, "type:" + type.name() + ";\n result:" + result);
		return result;
	}


	// 处理一般XML字符串
	private static String handleCreateMultiAlarmXmlString(SipMsgApiType type,
												HashMap<String, String> hm,int[] params) {
		String result = "";
		try {
			String root = type.getRequestType().name()
					.toLowerCase(Locale.getDefault());
			StringWriter xmlWriter = new StringWriter();
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlSerializer xmlSerializer = factory.newSerializer();
			xmlSerializer.setOutput(xmlWriter); // 保存创建的xml
			xmlSerializer.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					true);
			xmlSerializer.startDocument("utf-8", null); // <?xml version='1.0'
			// encoding='UTF-8'
			// standalone='yes' ?>
			xmlSerializer.startTag("", root);
			Iterator<Entry<String, String>> ite = hm.entrySet().iterator();
			xmlSerializer.startTag("", "cmd");
			xmlSerializer.text(type.getRequestCmd());
			xmlSerializer.endTag("", "cmd");

			while (ite.hasNext()) {
				Entry<String, String> item = ite.next();
				xmlSerializer.startTag("", item.getKey());
				xmlSerializer.text(item.getValue());
				xmlSerializer.endTag("", item.getKey());
			}
			xmlSerializer.startTag("", "alarm");
			int size = params.length;
			for (int i = 0; i < size; i++) {
				xmlSerializer.startTag("", "alarmID");
				xmlSerializer.text(String.valueOf(params[i]));
				xmlSerializer.endTag("", "alarmID");
			}
			xmlSerializer.endTag("", "alarm");

			xmlSerializer.endTag("", root);
			xmlSerializer.endDocument();
			result = xmlWriter.toString();
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (IOException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		WulianLog.d(TAG, "type:" + type.name() + ";\n result:" + result);
		return result;
	}

	// 处理运动侦测XML字符串
	private static String handleCreateMovementDetectionXmlString(
			SipMsgApiType type, HashMap<String, String> hmCommon,
			String[] params) {
		String result = "";
		try {
			String root = type.getRequestType().name()
					.toLowerCase(Locale.getDefault());
			StringWriter xmlWriter = new StringWriter();
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlSerializer xmlSerializer = factory.newSerializer();
			xmlSerializer.setOutput(xmlWriter); // 保存创建的xml
			xmlSerializer.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					true);
			xmlSerializer.startDocument("utf-8", null); // <?xml version='1.0'
														// encoding='UTF-8'
														// standalone='yes' ?>
			xmlSerializer.startTag("", root);
			Iterator<Entry<String, String>> ite = hmCommon.entrySet()
					.iterator();
			xmlSerializer.startTag("", "cmd");
			xmlSerializer.text(type.getRequestCmd());
			xmlSerializer.endTag("", "cmd");
			while (ite.hasNext()) {
				Entry<String, String> item = ite.next();
				xmlSerializer.startTag("", item.getKey());
				xmlSerializer.text(item.getValue());
				xmlSerializer.endTag("", item.getKey());
			}
			xmlSerializer.startTag("", "motion_area");
			int size = params.length;
			for (int i = 0; i < size; i++) {
				xmlSerializer.startTag("", "area");
				xmlSerializer.attribute("", "id", String.valueOf(i));
				xmlSerializer.text(params[i]);
				xmlSerializer.endTag("", "area");
			}
			xmlSerializer.endTag("", "motion_area");

			xmlSerializer.endTag("", root);
			xmlSerializer.endDocument();
			result = xmlWriter.toString();
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (IOException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		WulianLog.d(TAG, "type:" + type.name() + ";\n result:" + result);
		return result;
	}

	// 处理巡航路线XML字符串
	private static String handleCreateCruiseLineXmlString(SipMsgApiType type,
			HashMap<String, String> hmCommon, TourModel tours) {
		String result = "";
		try {
			String root = type.getRequestType().name()
					.toLowerCase(Locale.getDefault());
			StringWriter xmlWriter = new StringWriter();
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlSerializer xmlSerializer = factory.newSerializer();
			xmlSerializer.setOutput(xmlWriter); // 保存创建的xml
			xmlSerializer.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					true);
			xmlSerializer.startDocument("utf-8", null); // <?xml version='1.0'
														// encoding='UTF-8'
														// standalone='yes' ?>
			xmlSerializer.startTag("", root);
			Iterator<Entry<String, String>> ite = hmCommon.entrySet()
					.iterator();
			xmlSerializer.startTag("", "cmd");
			xmlSerializer.text(type.getRequestCmd());
			xmlSerializer.endTag("", "cmd");
			while (ite.hasNext()) {
				Entry<String, String> item = ite.next();
				xmlSerializer.startTag("", item.getKey());
				xmlSerializer.text(item.getValue());
				xmlSerializer.endTag("", item.getKey());
			}
			xmlSerializer.startTag("", "tour");
			xmlSerializer.attribute("", "id", String.valueOf(tours.getId()));
			xmlSerializer.attribute("", "model",
					String.valueOf(tours.getModel()));
			xmlSerializer.attribute("", "use", tours.getUse() ? "true"
					: "false");
			SparseArray<Preset> PresetsArray = tours.getPresets();
			int size = PresetsArray.size();
			for (int i = 0; i < size; i++) {
				xmlSerializer.startTag("", "preset");
				xmlSerializer.attribute("", "stay",
						String.valueOf(PresetsArray.get(i).getStay()));
				xmlSerializer.text(PresetsArray.get(i).getPresetName());
				xmlSerializer.endTag("", "preset");
			}

			xmlSerializer.endTag("", "tour");
			xmlSerializer.endTag("", root);
			xmlSerializer.endDocument();
			result = xmlWriter.toString();
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (IOException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		WulianLog.d(TAG, "type:" + type.name() + ";\n result:" + result);
		return result;
	}

	// 处理联动布防XML字符串
	private static String handleCreateLinkageDetectionXmlString(
			SipMsgApiType type, HashMap<String, String> hmCommon,
			LinkageDetectionModel detections) {
		String result = "";
		try {
			String root = type.getRequestType().name()
					.toLowerCase(Locale.getDefault());
			StringWriter xmlWriter = new StringWriter();
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlSerializer xmlSerializer = factory.newSerializer();
			xmlSerializer.setOutput(xmlWriter); // 保存创建的xml
			xmlSerializer.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					true);
			xmlSerializer.startDocument("utf-8", null); // <?xml version='1.0'
														// encoding='UTF-8'
														// standalone='yes' ?>
			xmlSerializer.startTag("", root);
			Iterator<Entry<String, String>> ite = hmCommon.entrySet()
					.iterator();
			xmlSerializer.startTag("", "cmd");
			xmlSerializer.text(type.getRequestCmd());
			xmlSerializer.endTag("", "cmd");
			while (ite.hasNext()) {
				Entry<String, String> item = ite.next();
				xmlSerializer.startTag("", item.getKey());
				xmlSerializer.text(item.getValue());
				xmlSerializer.endTag("", item.getKey());
			}
			xmlSerializer.startTag("", "schedule");
			xmlSerializer.attribute("", "use", detections.getUse() ? "true"
					: "false");
			if (detections.getUse()) {
				SparseArray<DetectionAction> detectionsArray = detections
						.getDetections();
				int size = detectionsArray.size();
				for (int i = 0; i < size; i++) {
					xmlSerializer.startTag("", "action");
					xmlSerializer.attribute("", "day", detectionsArray.get(i)
							.getWeekModel().getDayName());
					SparseArray<TimePeriod> timePeriods = detectionsArray
							.get(i).getTimePeriods();
					int timeSize = timePeriods.size();
					for (int j = 0; j < timeSize; j++) {
						xmlSerializer.startTag("", "time");
						xmlSerializer.attribute("", "start",
								timePeriods.get(timePeriods.keyAt(j))
										.getStartTime());
						xmlSerializer.attribute("", "end",
								timePeriods.get(timePeriods.keyAt(j))
										.getEndTime());
						// xmlSerializer.text(String.valueOf(timePeriods.get(timePeriods.keyAt(j))
						// .getId()));
						xmlSerializer.text(String.valueOf(j));
						xmlSerializer.endTag("", "time");
					}
					xmlSerializer.endTag("", "action");
				}
			}
			xmlSerializer.endTag("", "schedule");
			xmlSerializer.endTag("", root);
			xmlSerializer.endDocument();
			result = xmlWriter.toString();
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (IOException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		WulianLog.d(TAG, "type:" + type.name() + ";\n result:" + result);
		return result;
	}

	// 处理录像计划XML字符串
	private static String handlePreRecordPlanXmlString(SipMsgApiType type,
			HashMap<String, String> hmCommon, boolean enable,
			PreRecordPlanModel prerecordplan) {
		String result = "";
		try {
			String root = type.getRequestType().name()
					.toLowerCase(Locale.getDefault());
			StringWriter xmlWriter = new StringWriter();
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlSerializer xmlSerializer = factory.newSerializer();
			xmlSerializer.setOutput(xmlWriter); // 保存创建的xml
			xmlSerializer.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					true);
			xmlSerializer.startDocument("utf-8", null); // <?xml version='1.0'
														// encoding='UTF-8'
														// standalone='yes' ?>
			xmlSerializer.startTag("", root);
			Iterator<Entry<String, String>> ite = hmCommon.entrySet()
					.iterator();
			xmlSerializer.startTag("", "cmd");
			xmlSerializer.text(type.getRequestCmd());
			xmlSerializer.endTag("", "cmd");
			while (ite.hasNext()) {
				Entry<String, String> item = ite.next();
				xmlSerializer.startTag("", item.getKey());
				xmlSerializer.text(item.getValue());
				xmlSerializer.endTag("", item.getKey());
			}
			xmlSerializer.startTag("", "schedule");
			if (enable) {
				SparseArray<DetectionAction> detectionsArray = prerecordplan
						.getDetections();
				int size = detectionsArray.size();
				for (int i = 0; i < size; i++) {
					xmlSerializer.startTag("", "action");
					xmlSerializer.attribute("", "day", detectionsArray.get(i)
							.getWeekModel().getDayName());
					SparseArray<TimePeriod> timePeriods = detectionsArray
							.get(i).getTimePeriods();
					int timeSize = timePeriods.size();
					for (int j = 0; j < timeSize; j++) {
						xmlSerializer.startTag("", "time");
						xmlSerializer.attribute("", "start",
								timePeriods.get(timePeriods.keyAt(j))
										.getStartTime());
						xmlSerializer.attribute("", "end",
								timePeriods.get(timePeriods.keyAt(j))
										.getEndTime());
						// xmlSerializer.text(String.valueOf(timePeriods.get(timePeriods.keyAt(j))
						// .getId()));
						xmlSerializer.text(String.valueOf(j));
						xmlSerializer.endTag("", "time");
					}
					xmlSerializer.endTag("", "action");
				}
			}
			xmlSerializer.endTag("", "schedule");
			xmlSerializer.endTag("", root);
			xmlSerializer.endDocument();
			result = xmlWriter.toString();
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (IOException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		WulianLog.d(TAG, "type:" + type.name() + ";\n result:" + result);
		return result;
	}

	// 解析XML数据，判断Sip远程访问Api类型
	public static SipMsgApiType parseXMLData(String xmlData) {
		String regEx = "<cmd>[^>]*</cmd>";
		String result = "null";
		Pattern pattern = Pattern.compile(regEx);
		Matcher m = pattern.matcher(xmlData);
		if (m.find()) {
			result = m.group();
		}
		int start = "<cmd>".length();
		int end = result.length() - "</cmd>".length();
		if (!result.equals("null")) {
			result = result.substring(start, end);
			return SipMsgApiType.getSipTypeByRespondCmd(result);
		}
		return null;
	}
}
