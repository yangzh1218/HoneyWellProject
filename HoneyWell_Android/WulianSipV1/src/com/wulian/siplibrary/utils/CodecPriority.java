/**
 * Project Name:  WulianLibrary
 * File Name:     CodecPriority.java
 * Package Name:  com.wulian.siplibrary.utils
 * @Date:         2014年10月29日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.utils;

import java.io.Serializable;

/**
 * @ClassName: CodecPriority
 * @Function: TODO
 * @Date: 2014年10月29日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class CodecPriority implements Serializable {
	/**
	 * serialVersionUID 作用:TODO
	 */
	private static final long serialVersionUID = 3848893536804429936L;
	String codecName;
	String type;
	int newValue;

	public CodecPriority() {
		codecName = "";
		type = "";
		newValue = -1;
	}

	public void setCodecPriority(String codecName, String type, int newValue) {
		this.codecName = codecName;
		this.type = type;
		this.newValue = newValue;
	}

	public String getCodecName() {
		return codecName;
	}

	public int getNewValue() {
		return newValue;
	}

	public String getType() {
		return type;
	}

	public void setCodecName(String codecName) {
		this.codecName = codecName;
	}

	public void setNewValue(int newValue) {
		this.newValue = newValue;
	}

	public void setType(String type) {
		this.type = type;
	}
}
