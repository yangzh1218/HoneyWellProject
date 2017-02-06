/**
 * Project Name:  WulianLibrary
 * File Name:     TimePeriod.java
 * Package Name:  com.wulian.siplibrary.model
 * @Date:         2014年11月25日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.model.linkagedetection;

/**
 * @ClassName: TimePeriod
 * @Function: 时间段
 * @Date: 2014年11月25日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class TimePeriod {
	String startTime;// 开始时间
	String endTime;// 结束时间
	int id;// 时间编号， 一天支持不超过４个

	public TimePeriod() {
		this.startTime = "12:00";
		this.endTime = "12:00";
		this.id = -1;
	}

	public TimePeriod(int id, String startTime, String endTime) {
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getEndTime() {
		return endTime;
	}
}
