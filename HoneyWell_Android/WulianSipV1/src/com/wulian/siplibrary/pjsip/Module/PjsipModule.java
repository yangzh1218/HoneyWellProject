/**
 * Project Name:  WulianLibrary
 * File Name:     PjsipModule.java
 * Package Name:  com.wulian.siplibrary.pjsip.Module
 * @Date:         2014年11月1日
 * Copyright (c)  2014, wulian All Rights Reserved.
*/

package com.wulian.siplibrary.pjsip.Module;

import android.content.Context;

import com.wulian.siplibrary.manage.SipProfile;
/**
 * @ClassName: PjsipModule
 * @Function:  TODO
 * @Date:      2014年11月1日
 * @author     Puml
 * @email      puml@wuliangroup.cn
 */
public interface PjsipModule {
	/**
	 * Set the android context for the module. Could be usefull to get
	 * preferences for examples.
	 * 
	 * @param ctxt
	 *            android context
	 */
	void setContext(Context ctxt);

	/**
	 * Here pjsip endpoint should have this module added.
	 */
	void onBeforeStartPjsip();

	/**
	 * This is fired just after account was added to pjsip and before will
	 * be registered. Modules does not necessarily implement something here.
	 * 
	 * @param pjId
	 *            the pjsip id of the added account.
	 * @param acc
	 *            the profile account.
	 */
	void onBeforeAccountStartRegistration(int pjId, SipProfile acc);
}

