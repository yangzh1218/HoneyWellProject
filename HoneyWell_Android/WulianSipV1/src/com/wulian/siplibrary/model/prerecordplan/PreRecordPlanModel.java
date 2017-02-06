/**
 * Project Name:  WulianICamSip
 * File Name:     PreRecordPlanModel.java
 * Package Name:  com.wulian.siplibrary.model.prerecordplan
 * @Date:         2015年5月27日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.model.prerecordplan;

import android.util.SparseArray;

import com.wulian.siplibrary.model.linkagedetection.DetectionAction;

/**
 * @ClassName: PreRecordPlanModel
 * @Function: 录像计划模型
 * @Date: 2015年5月27日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class PreRecordPlanModel {
	SparseArray<DetectionAction> DetectionsArray;// 一天的布防计划

	public PreRecordPlanModel() {
		DetectionsArray = new SparseArray<DetectionAction>();
	}

	public void addDetectionAction(int position, DetectionAction detection) {
		this.DetectionsArray.append(position, detection);
	}

	public SparseArray<DetectionAction> getDetections() {
		return this.DetectionsArray;
	}

}
