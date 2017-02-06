/**
 * Project Name:  WulianLibrary
 * File Name:     Preset.java
 * Package Name:  com.wulian.siplibrary.model.cruiseline
 * @Date:         2014年11月25日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.model.cruiseline;

/**
 * @ClassName: Preset
 * @Function: 预置点
 * @Date: 2014年11月25日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class Preset {
	int stay;// 预置点停留时间（0 ~ 120s）
	String presetName;// 预置点名称

	public Preset() {
		stay = 0;
		presetName = "";
	}

	public Preset(int stay, String presetName) {
		this.stay = stay;
		this.presetName = presetName;
	}

	public void setStay(int stay) {
		this.stay = stay;
	}

	public int getStay() {
		return stay;
	}

	public void setPresetName(String presetName) {
		this.presetName = presetName;
	}

	public String getPresetName() {
		return presetName;
	}
}
