/**
 * Project Name:  iCam
 * File Name:     CheckBind.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2014年11月25日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.model;

import java.io.Serializable;

/**
 * @ClassName: CheckBind
 * @Function: 绑定状态
 * @Date: 2014年11月25日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class CheckBind implements Serializable{
	/**
	 * serialVersionUID 作用:TODO
	 */
	private static final long serialVersionUID = -6026158830330184980L;
	public String uid;
	public String seed;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

}
