/**
 * Project Name:  WulianLibrary
 * File Name:     SipAccountBuild.java
 * Package Name:  com.wulian.siplibrary.manage
 * @Date:         2014年10月30日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.manage;

import android.text.TextUtils;

import com.wulian.siplibrary.utils.WulianDefaultPreference;
import com.wulian.siplibrary.utils.WulianLog;

import java.util.UUID;

/**
 * @ClassName: SipAccountBuild
 * @Function: 账号组建
 * @Date: 2014年10月30日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class SipAccountBuild {
	private static final String THIS_FILE = "SipAccountBuild";
	private String accountDisplayName;
	private String accountUserName;
	private String accountServer;
	private String accountPassword;
	private String sipDomain;
	private static int accountId = 1;// 账号ID
	private boolean isRestart;// 后期用

	public SipAccountBuild() {
		// TODO Auto-generated constructor stub

	}

	public SipAccountBuild(String accountDisplayName, String accountUserName,
			String accountServer, String accountPassword) {
		this.accountDisplayName = accountDisplayName;
		this.accountUserName = accountUserName;
		if (accountServer.contains(String.valueOf(WulianDefaultPreference.getsSipTransport()))) {
			this.accountServer = accountServer;
		} else {
			this.accountServer = accountServer +":"+String.valueOf(WulianDefaultPreference.getsSipTransport());
		}
		this.sipDomain=this.accountServer;
		this.accountPassword = accountPassword;
		this.isRestart = false;
	}
	
	//SipDomain RegURI
	public SipAccountBuild(String accountDisplayName, String accountUserName,
			String accountServer,String accountPassword,String sipDomain) {
		this.accountDisplayName = accountDisplayName;
		this.accountUserName = accountUserName;
		if (accountServer.contains(String.valueOf(WulianDefaultPreference.getsSipTransport()))) {
			this.accountServer = accountServer;
		} else {
			this.accountServer = accountServer +":"+ String.valueOf(WulianDefaultPreference.getsSipTransport());
		}
		if (sipDomain.contains(String.valueOf(WulianDefaultPreference.getsSipTransport()))) {
			this.sipDomain = sipDomain;
		} else {
			this.sipDomain = sipDomain +":"+ String.valueOf(WulianDefaultPreference.getsSipTransport());
		}
		this.accountPassword = accountPassword;
		this.isRestart = false;
	}

	// public SipProfile buildAccount(SipProfile account) {
	// WulianLog.d(THIS_FILE, "begin Sip Account ....");
	// account.display_name = accountDisplayName;
	// String[] serverParts = accountServer.split(":");
	// account.acc_id = "<sip:" + SipUri.encodeUser(accountUserName.trim())
	// + "@" + serverParts[0].trim() + ">";
	// String regUri = "sip:" + accountServer;
	// account.reg_uri = regUri;
	// account.proxies = new String[] { regUri };
	// account.realm = "*";
	// account.username = accountUserName;//+"@wuliangroup.cn";
	// account.data = accountPassword;
	// account.scheme = SipProfile.CRED_SCHEME_DIGEST;
	// // By default auto transport
	//
	// account.transport = SipProfile.TRANSPORT_UDP;
	// // account.transport = SipProfile.TRANSPORT_TLS;
	// // account.transport = SipProfile.TRANSPORT_TCP;
	//
	// if (account.id == SipProfile.INVALID_ID) {
	// applyNewAccountDefault(account);
	// account.id = accountId;
	// accountId++;
	// }
	// return account;
	// }

	public SipProfile buildAccount(SipProfile account) {
		WulianLog.d(THIS_FILE, "begin Sip Account ....");
		account.display_name = accountDisplayName;
		String[] serverParts = accountServer.split(":");
		account.acc_id = "<sip:" + SipUri.encodeUser(accountUserName.trim())
				+ "@" + serverParts[0].trim() + ">";
		// String regUri="";
		// if(!accountServer.contains(String.valueOf(WulianDefaultPreference.getsSipTransport()))) {
		// regUri = "sip:" + accountServer+String.valueOf(WulianDefaultPreference.getsSipTransport());
		// }else {
		// regUri = "sip:" + accountServer;
		// }
		String regUri = "sip:" + sipDomain;
		account.reg_uri = regUri;// "sip:" + accountServer+String.valueOf(WulianDefaultPreference.getsSipTransport());
		if (!TextUtils.isEmpty(WulianDefaultPreference.getsProxyServer())) {
			account.proxies = new String[] { "sip:"+WulianDefaultPreference
					.getsProxyServer() };
		} else {
			account.proxies = null;
		}
		account.realm = "*";
		account.username = accountUserName;// +"@wuliangroup.cn";
		account.data = accountPassword;
		account.scheme = SipProfile.CRED_SCHEME_DIGEST;
		// By default auto transport

		account.transport=WulianDefaultPreference.getsDefaultTransport();
		//account.transport = SipProfile.TRANSPORT_UDP;
		// account.transport = SipProfile.TRANSPORT_TLS;
		// account.transport = SipProfile.TRANSPORT_TCP;

		if (account.id == SipProfile.INVALID_ID) {
			applyNewAccountDefault(account);
			account.id = accountId;
			accountId++;
		}
		return account;
	}

	/**
	 * Apply default settings for a new account to check very basic coherence of
	 * settings and auto-modify settings missing
	 * 
	 * @param account
	 */
	private void applyNewAccountDefault(SipProfile account) {
		if (account.use_rfc5626) {
			if (TextUtils.isEmpty(account.rfc5626_instance_id)) {
				String autoInstanceId = (UUID.randomUUID()).toString();
				account.rfc5626_instance_id = "<urn:uuid:" + autoInstanceId
						+ ">";
			}
		}
	}

}
