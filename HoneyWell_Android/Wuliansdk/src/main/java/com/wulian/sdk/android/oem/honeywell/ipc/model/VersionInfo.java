/**
 * Project Name:  iCam
 * File Name:     VersionInfo.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2014年11月5日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.model;

/**
 * @ClassName: VersionInfo
 * @Function: 版本细腻
 * @Date: 2014年11月5日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class VersionInfo {
	private String version;
	private int code;
	private String md5;
	private String origin;
	private int size;
	private String description;
	private String created;
	private String important;



	public String getVersion() {
		return version;
	}

	public void setVersion(String version ) {
		this.version = version;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getImportant() {
		return important;
	}

	public void setImportant(String important) {
		this.important = important;
	}

}
