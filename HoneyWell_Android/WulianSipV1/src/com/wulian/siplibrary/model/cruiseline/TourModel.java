/**
 * Project Name:  WulianLibrary
 * File Name:     CruiseLineModel.java
 * Package Name:  com.wulian.siplibrary.model.cruiseline
 * @Date:         2014年11月25日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.model.cruiseline;

import android.util.SparseArray;

/**
 * @ClassName: CruiseLineModel
 * @Function: 巡回路线
 * @Date: 2014年11月25日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class TourModel {
	int id;// 路线编号
	int model; // 巡航模式：
				// 0： 使用预置点模式 (id > 0)
				// 1： only水平巡航模式（id =0）
				// 2: only垂直巡航模式 (id = 0)
	boolean use;// 是否使用：
				// true： 使用该巡航设置
				// false： 不使用该巡航设置
	SparseArray<Preset> presetsArray;

	public TourModel() {
		id = -1;
		model = 0;
		use = false;
		presetsArray = new SparseArray<Preset>();
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setModel(int model) {
		this.model = model;
	}

	public int getModel() {
		return model;
	}

	public void setUse(boolean use) {
		this.use = use;
	}

	public boolean getUse() {
		return this.use;
	}

	public void addPreset(int position, Preset preset) {
		this.presetsArray.append(position, preset);
	}

	public SparseArray<Preset> getPresets() {
		return this.presetsArray;
	}

}
