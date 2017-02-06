/**
 * Project Name:  WulianLibrary
 * File Name:     DetectionAction.java
 * Package Name:  com.wulian.siplibrary.api
 * @Date:         2014年11月25日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.model.linkagedetection;

import android.util.SparseArray;

/**
 * @ClassName: DetectionAction
 * @Function: 联动布防模型 (哪一天，哪些个时间段)
 * @Date: 2014年11月25日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class DetectionAction {
	WeekModel weekModel;
	SparseArray<TimePeriod> timePeriods;

	public DetectionAction() {
		this.weekModel = WeekModel.MONDAY;
		this.timePeriods = new SparseArray<TimePeriod>();
	}

	public DetectionAction(WeekModel weekModel) {
		this.weekModel = weekModel;
		this.timePeriods = new SparseArray<TimePeriod>();
	}

	public void setWeekModel(WeekModel weekModel) {
		this.weekModel = weekModel;
	}

	public WeekModel getWeekModel() {
		return weekModel;
	}

	public void addLinkageDetection(TimePeriod timePeriod) {
		timePeriods.append(timePeriod.getId(), timePeriod);
	}

	public SparseArray<TimePeriod> getTimePeriods() {
		return this.timePeriods;
	}

}
