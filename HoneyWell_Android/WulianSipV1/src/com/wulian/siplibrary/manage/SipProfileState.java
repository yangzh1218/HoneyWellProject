/**
 * Project Name:  WulianLibrary
 * File Name:     SipProfileState.java
 * Package Name:  com.wulian.siplibrary.manage
 * @Date:         2014年10月30日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.manage;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * @ClassName: SipProfileState
 * @Function: TODO
 * @Date: 2014年10月30日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class SipProfileState implements Parcelable, Serializable {
	/**
	 * serialVersionUID 作用:TODO
	 */
	private static final long serialVersionUID = -1909447568863435677L;
	public int primaryKey = -1;
	private int pjsuaId;
	private boolean active;
	private int statusCode;
	private String statusText;
	private int addedStatus;
	private int expires;
	private String displayName;
	private int priority;
	private String regUri = "";

	/**
	 * Account id.<br/>
	 * Identifier of the SIP account associated. It's the identifier of the
	 * account for the API.
	 * 
	 * @see SipProfile#FIELD_ID
	 * @see Integer
	 */
	public final static String ACCOUNT_ID = "account_id";
	/**
	 * Identifier for underlying sip stack. <br/>
	 * This is an internal identifier you normally don't need to use when using
	 * the api from an external application.
	 * 
	 * @see Integer
	 */
	public final static String PJSUA_ID = "pjsua_id";
	/**
	 * Activation state.<br/>
	 * Active state of the account. This is a shortcut to not have to query
	 * {@link SipProfile} database
	 * 
	 * @see Boolean
	 */
	public final static String ACTIVE = "active";
	/**
	 * Status code of the latest registration.<br/>
	 * SIP code of latest registration.
	 * 
	 * @see Integer
	 */
	public final static String STATUS_CODE = "status_code";
	/**
	 * Status comment of latest registration.<br/>
	 * Sip comment of latest registration.
	 * 
	 * @see String
	 */
	public final static String STATUS_TEXT = "status_text";
	/**
	 * Status of sip stack adding of the account.<br/>
	 * When the application adds the account to the stack it may fails if the
	 * sip profile is invalid.
	 * 
	 * @see Integer
	 */
	public final static String ADDED_STATUS = "added_status";
	/**
	 * Latest know expires time. <br/>
	 * Expires value of latest registration. It's actually usefull to detect
	 * that it was unregister testing 0 value. Else it's not necessarily
	 * relevant information.
	 * 
	 * @see Integer
	 */
	public final static String EXPIRES = "expires";
	/**
	 * Display name of the account.<br.>
	 * This is a shortcut to not have to query {@link SipProfile} database
	 */
	public final static String DISPLAY_NAME = "display_name";
	/**
	 * Registration uri of the account.<br.>
	 * This is a shortcut to not have to query {@link SipProfile} database
	 */
	public final static String REG_URI = "reg_uri";

	public static final String[] FULL_PROJECTION = new String[] { ACCOUNT_ID,
			PJSUA_ID, ACTIVE, STATUS_CODE, STATUS_TEXT, EXPIRES, DISPLAY_NAME,
			REG_URI };

	public SipProfileState(Parcel in) {
		readFromParcel(in);
	}

	/**
	 * Should not be used for external use of the API. Default constructor.
	 */
	public SipProfileState() {
		// Set default values
		addedStatus = -1;
		pjsuaId = -1;
		statusCode = -1;
		statusText = "";
		expires = 0;
	}

	/**
	 * Should not be used for external use of the API. Constructor on the top of
	 * a sip account.
	 * 
	 * @param account
	 *            The sip profile to associate this wrapper info to.
	 */
	public SipProfileState(SipProfile account) {
		this();

		active = account.active;
		displayName = account.display_name;
		regUri = account.reg_uri;
	}

	private final void readFromParcel(Parcel in) {
		primaryKey = in.readInt();
		pjsuaId = in.readInt();
		active = (in.readInt() == 1);
		statusCode = in.readInt();
		statusText = in.readString();
		addedStatus = in.readInt();
		expires = in.readInt();
		displayName = in.readString();
		priority = in.readInt();
		regUri = in.readString();
	}

	@Override
	public int describeContents() {

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {

		// TODO Auto-generated method stub
		out.writeInt(primaryKey);
		out.writeInt(pjsuaId);
		out.writeInt((active ? 1 : 0));
		out.writeInt(statusCode);
		out.writeString(statusText);
		out.writeInt(addedStatus);
		out.writeInt(expires);
		out.writeString(displayName);
		out.writeInt(priority);
		out.writeString(regUri);
	}

	/**
	 * Parcelable creator. So that it can be passed as an argument of the aidl
	 * interface
	 */
	public static final Parcelable.Creator<SipProfileState> CREATOR = new Parcelable.Creator<SipProfileState>() {
		public SipProfileState createFromParcel(Parcel in) {
			return new SipProfileState(in);
		}

		public SipProfileState[] newArray(int size) {
			return new SipProfileState[size];
		}
	};

	/**
	 * Should not be used for external use of the API.
	 * 
	 * @param pjsuaId
	 *            the pjsuaId to set
	 */
	public void setPjsuaId(int pjsuaId) {
		this.pjsuaId = pjsuaId;
	}

	/**
	 * Should not be used for external use of the API.
	 * 
	 * @return the pjsuaId {@link #PJSUA_ID}
	 */
	public int getPjsuaId() {
		return pjsuaId;
	}

	/**
	 * Should not be used for external use of the API.
	 * 
	 * @param active
	 *            the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the active {@link #ACTIVE}
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Should not be used for external use of the API.
	 * 
	 * @param statusCode
	 *            the statusCode to set
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the statusCode {@link #STATUS_TEXT}
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Should not be used for external use of the API.
	 * 
	 * @param statusText
	 *            the statusText to set
	 */
	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	/**
	 * @return the statusText {@link #STATUS_TEXT}
	 */
	public String getStatusText() {
		return statusText;
	}

	/**
	 * Should not be used for external use of the API.
	 * 
	 * @param addedStatus
	 *            the addedStatus to set
	 */
	public void setAddedStatus(int addedStatus) {
		this.addedStatus = addedStatus;
	}

	/**
	 * @return the addedStatus {@link #ADDED_STATUS}
	 */
	public int getAddedStatus() {
		return addedStatus;
	}

	/**
	 * Should not be used for external use of the API.
	 * 
	 * @param expires
	 *            the expires to set
	 */
	public void setExpires(int expires) {
		this.expires = expires;
	}

	/**
	 * @return the expires {@link #EXPIRES}
	 */
	public int getExpires() {
		return expires;
	}

	/**
	 * @return the display name {@link #DISPLAY_NAME}
	 */
	public CharSequence getDisplayName() {
		return displayName;
	}

	/**
	 * @return the priority {@link #PRIORITY}
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Should not be used for external use of the API.
	 * 
	 * @param priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Should not be used for external use of the API.
	 * 
	 * @param regUri
	 *            the regUri to set
	 */
	public void setRegUri(String regUri) {
		this.regUri = regUri;
	}

	/**
	 * @return the regUri {@link #REG_URI}
	 */
	public String getRegUri() {
		return regUri;
	}

	/**
	 * Is the account added to sip stack yet?
	 * 
	 * @return true if the account has been added to sip stack and has a sip
	 *         stack id.
	 */
	public boolean isAddedToStack() {
		return pjsuaId != -1;
	}

	/**
	 * Is the account valid for sip calls?
	 * 
	 * @return true if it should be possible to make a call using the associated
	 *         account.
	 */
	public boolean isValidForCall() {
		if (active) {
			if (TextUtils.isEmpty(getRegUri())) {
				return true;
			}
			return (isAddedToStack()
					&& getStatusCode() == SipCallSession.StatusCode.OK && getExpires() > 0);
		}
		return false;
	}

	public final void createFromBefore(SipProfileState state) {
		pjsuaId = state.pjsuaId;
		active = state.active;
		statusCode = state.statusCode;
		statusText = state.statusText;
		addedStatus = state.addedStatus;
		expires = state.expires;
		displayName = state.displayName;
		regUri = state.regUri;
	}

	// @Override
	// public String toString() {
	// return "SipProfileState [primaryKey=" + primaryKey + ", pjsuaId="
	// + pjsuaId + ", active=" + active + ", statusCode=" + statusCode
	// + ", statusText=" + statusText + ", addedStatus=" + addedStatus
	// + ", expires=" + expires + ", displayName=" + displayName
	// + ", priority=" + priority + ", regUri=" + regUri + "]";
	// }

}
