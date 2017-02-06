/**
 * Project Name:  WulianLibrary
 * File Name:     LinkageDetectionModel.java
 * Package Name:  com.wulian.siplibrary.model.linkagedetection
 * @Date:         2014年11月25日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.model.linkagedetection;

import android.util.SparseArray;

/**
 * @ClassName: LinkageDetectionModel
 * @Function: 布防时间表
 * @Date: 2014年11月25日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class LinkageDetectionModel {
	boolean use;// 是否使用布防
	SparseArray<DetectionAction> DetectionsArray;// 一天的布防计划

	public LinkageDetectionModel() {
		use = false;
		DetectionsArray = new SparseArray<DetectionAction>();
	}
	public LinkageDetectionModel(boolean isUse) {
		use = isUse;
		DetectionsArray = new SparseArray<DetectionAction>();
	}

	public void setUse(boolean use) {
		this.use = use;
	}

	public boolean getUse() {
		return this.use;
	}

	public void addDetectionAction(int position, DetectionAction detection) {
		this.DetectionsArray.append(position, detection);
	}

	public SparseArray<DetectionAction> getDetections() {
		return this.DetectionsArray;
	}

}
