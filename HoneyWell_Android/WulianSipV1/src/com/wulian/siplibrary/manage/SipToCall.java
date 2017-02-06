/**
 * Project Name:  WulianLibrary
 * File Name:     SipToCall.java
 * Package Name:  com.wulian.siplibrary.manage
 * @Date:         2014年10月30日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.manage;

/**
 * @ClassName: SipToCall
 * @Function: TODO
 * @Date: 2014年10月30日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class SipToCall {
	private Integer pjsipAccountId;
	private String callee;

	public SipToCall(Integer acc, String uri) {
		pjsipAccountId = acc;
		callee = uri;
	}

	public void setPjsipAccountId(Integer pjsipAccountId) {
		this.pjsipAccountId = pjsipAccountId;
	}

	/**
	 * @return the pjsipAccountId
	 */
	public Integer getPjsipAccountId() {
		return pjsipAccountId;
	}

	/**
	 * @return the callee
	 */
	public String getCallee() {
		return callee;
	}
}
