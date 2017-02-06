/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  This file and this file only is also released under Apache license as an API file
 */

package com.wulian.siplibrary.manage;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Holder for a sip message.<br/>
 */
public class SipMessage implements Parcelable {

//	/**
//	 * serialVersionUID 作用:TODO
//	 */
	private static final long serialVersionUID = -2629293889053495338L;
	/**
	 * Primary key id.
	 * 
	 * @see Long
	 */
	public static final String FIELD_ID = "id";
	/**
	 * From / sender.
	 * 
	 * @see String
	 */
	public static final String FIELD_FROM = "sender";
	/**
	 * To / receiver.
	 * 
	 * @see String
	 */
	public static final String FIELD_TO = "receiver";
	/**
	 * Contact of the sip message.
	 */
	public static final String FIELD_CONTACT = "contact";
	/**
	 * Body / content of the sip message.
	 * 
	 * @see String
	 */
	public static final String FIELD_BODY = "body";
	/**
	 * Mime type of the sip message.
	 * 
	 * @see String
	 */
	public static final String FIELD_MIME_TYPE = "mime_type";
	/**
	 * Way type of the message.
	 * 
	 * @see Integer
	 * @see #MESSAGE_TYPE_INBOX
	 * @see #MESSAGE_TYPE_FAILED
	 * @see #MESSAGE_TYPE_QUEUED
	 * @see #MESSAGE_TYPE_SENT
	 */
	public static final String FIELD_TYPE = "type";
	/**
	 * Reception date of the message.
	 * 
	 * @see Long
	 */
	public static final String FIELD_DATE = "date";
	/**
	 * Latest pager status.
	 * 
	 * @see Integer
	 */
	public static final String FIELD_STATUS = "status";
	/**
	 * Read status of the message.
	 * 
	 * @see Boolean
	 */
	public static final String FIELD_READ = "read";
	/**
	 * Non canonical sip from
	 * 
	 * @see String
	 */
	public static final String FIELD_FROM_FULL = "full_sender";

	/**
	 * Message received type.(接收)
	 */
	public static final int MESSAGE_TYPE_INBOX = 1;
	/**
	 * Message sent type.(发送)
	 */
	public static final int MESSAGE_TYPE_SENT = 2;
	/**
	 * Failed outgoing message.(拨出电话)
	 */
	public static final int MESSAGE_TYPE_FAILED = 5;
	/**
	 * Message to send later.(顺序发送)
	 */
	public static final int MESSAGE_TYPE_QUEUED = 6;

	// Content Provider - account
	/**
	 * Table for sip message.
	 */
	public static final String MESSAGES_TABLE_NAME = "messages";
	/**
	 * Status unknown for a message.
	 */
	public static final int STATUS_NONE = -1;
	/**
	 * Constant to represent self as sender or receiver of the message.
	 */
	public static final String SELF = "SELF";

	private String from;
	private String fullFrom;
	private String to;
	private String contact;
	private String body;
	private String mimeType;
	private long date;
	private int type;
	private int status = STATUS_NONE;
	private boolean read = false;

	/**
	 * Construct from raw datas.
	 * 
	 * @param aForm
	 *            {@link #FIELD_FROM}
	 * @param aTo
	 *            {@link #FIELD_TO}
	 * @param aContact
	 *            {@link #FIELD_CONTACT}
	 * @param aBody
	 *            {@link #FIELD_BODY}
	 * @param aMimeType
	 *            {@link #FIELD_MIME_TYPE}
	 * @param aDate
	 *            {@link #FIELD_DATE}
	 * @param aType
	 *            {@link #FIELD_TYPE}
	 * @param aFullFrom
	 *            {@link #FIELD_FROM_FULL}
	 */
	// public SipMessage(String aForm, String aTo, String aContact, String
	// aBody,
	// String aMimeType, long aDate, int aType, String aFullFrom) {
	// from = aForm;
	// to = aTo;
	// contact = aContact;
	// body = aBody;
	// mimeType = aMimeType;
	// date = aDate;
	// type = aType;
	// fullFrom = aFullFrom;
	// }

	@Override
	public int describeContents() {
		
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static final Parcelable.Creator<SipMessage> CREATOR = new Creator<SipMessage>() {

		@Override
		public SipMessage[] newArray(int size) {
			return new SipMessage[size];
		}

		@Override
		public SipMessage createFromParcel(Parcel source) {
			SipMessage result = new SipMessage();
			result.from = source.readString();
			result.to = source.readString();
			result.contact = source.readString();
			result.body = source.readString();
			result.mimeType = source.readString();
			result.date = source.readLong();
			result.type = source.readInt();
			result.fullFrom = source.readString();
			return result;
		}
	};
	
	 @Override
	    public void writeToParcel(Parcel dest, int flags) {
	        dest.writeString(from);
	        dest.writeString(to);
	        dest.writeString(contact);
	        dest.writeString(body);
	        dest.writeString(mimeType);
	        dest.writeLong(date);       
	        dest.writeInt(type);
	        dest.writeString(fullFrom);
	    }

	/**
	 * Get the from of the message.
	 * 
	 * @return the From of the sip message
	 */
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * Get the body of the message.
	 * 
	 * @return the Body of the message
	 */
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Get to of the message.
	 * 
	 * @return the To of the sip message
	 */
	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Set the message as read or unread.
	 * 
	 * @param b
	 *            true when read.
	 */
	public void setRead(boolean b) {
		read = b;

	}

	/**
	 * Get the display name of remote party.
	 * 
	 * @return The remote display name
	 */
	public String getDisplayName() {
		return SipUri.getDisplayedSimpleContact(fullFrom);
	}

	/**
	 * Get the number of the remote party.
	 * 
	 * @return The remote party number
	 */
	public String getRemoteNumber() {
		if (SipMessage.SELF.equalsIgnoreCase(from)) {
			return to;
		} else {
			return from;
		}
	}

	/**
	 * Get the content of the body without error tag
	 * 
	 * @return The body of the sip message
	 */
	public String getBodyContent() {
		int splitIndex = body.indexOf(" // ");
		if (splitIndex != -1) {
			return body.substring(0, splitIndex);
		}
		return body;
	}

	/**
	 * Get optional error of the sip message
	 * 
	 * @return The error string repr if any, null otherwise.
	 */
	public String getErrorContent() {
		if (status == SipMessage.STATUS_NONE
				|| status == SipCallSession.StatusCode.OK
				|| status == SipCallSession.StatusCode.ACCEPTED) {
			return null;
		}

		int splitIndex = body.indexOf(" // ");
		if (splitIndex != -1) {
			return body.substring(splitIndex + 4, body.length());
		}
		return null;
	}

	/**
	 * Get the way of the message is send by the user of the application
	 * 
	 * @return true if message is sent by the user of the application
	 */
	public boolean isOutgoing() {
		if (SipMessage.SELF.equalsIgnoreCase(from)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get the send/receive date of the message.
	 * 
	 * @return the date of the send of the message for outgoing, of receive for
	 *         incoming.
	 */
	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	/**
	 * Get the complete remote contact from which the message comes.<br/>
	 * This includes display name.
	 * 
	 * @return the sip uri of remote contact as announced when sending this
	 *         message.
	 */
	public String getFullFrom() {
		return fullFrom;
	}

	public void setFullFrom(String fullFrom) {
		this.fullFrom = fullFrom;
	}

	/**
	 * Get the type of the message.
	 * 
	 * @return Message type
	 * @see #MESSAGE_TYPE_FAILED
	 * @see #MESSAGE_TYPE_INBOX
	 * @see #MESSAGE_TYPE_QUEUED
	 * @see #MESSAGE_TYPE_SENT
	 */
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Get the mime type of the message.
	 * 
	 * @return the message mime type sent by remote party.
	 */
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}
}
