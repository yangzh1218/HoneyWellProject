/**
 * Project Name:  WulianLibrary
 * File Name:     SipProfile.java
 * Package Name:  com.wulian.siplibrary.manage
 * @Date:         2014年10月30日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.siplibrary.manage;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.wulian.siplibrary.manage.SipUri.ParsedSipContactInfos;
import com.wulian.siplibrary.manage.SipUri.ParsedSipUriInfos;
import com.wulian.siplibrary.utils.WulianLog;

import java.util.regex.Pattern;

/**
 * @ClassName: SipProfile
 * @Function: TODO
 * @Date: 2014年10月30日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class SipProfile implements Parcelable {
	private static final String THIS_FILE = "SipProfile";
	public final static long INVALID_ID = -1;
	public final static int TRANSPORT_AUTO = 0;
	/**
	 * Force UDP transport.
	 */
	public final static int TRANSPORT_UDP = 1;
	/**
	 * Force TCP transport.
	 */
	public final static int TRANSPORT_TCP = 2;
	/**
	 * Force TLS transport.
	 */
	public final static int TRANSPORT_TLS = 3;

	// Stack choices
	/**
	 * Use pjsip as backend.<br/>
	 * For now it's the only one supported
	 */
	public static final int PJSIP_STACK = 0;
	// Password type choices
	/**
	 * Plain password mode.<br/>
	 * <a target="_blank" href=
	 * "http://www.pjsip.org/pjsip/docs/html/structpjsip__cred__info.htm#a8b1e563c814bdf8012f0bdf966d0ad9d"
	 * >Pjsip documentation</a>
	 * 
	 * @see #datatype
	 */
	public static final int CRED_DATA_PLAIN_PASSWD = 0;
	/**
	 * Digest mode.<br/>
	 * <a target="_blank" href=
	 * "http://www.pjsip.org/pjsip/docs/html/structpjsip__cred__info.htm#a8b1e563c814bdf8012f0bdf966d0ad9d"
	 * >Pjsip documentation</a>
	 * 
	 * @see #datatype
	 */
	public static final int CRED_DATA_DIGEST = 1;
	// Scheme credentials choices
	/**
	 * Digest scheme for credentials.<br/>
	 * <a target="_blank" href=
	 * "http://www.pjsip.org/pjsip/docs/html/structpjsip__cred__info.htm#ae31c9ec1c99fb1ffa20be5954ee995a7"
	 * >Pjsip documentation</a>
	 * 
	 * @see #scheme
	 */
	public static final String CRED_SCHEME_DIGEST = "Digest";
	/**
	 * PGP scheme for credentials.<br/>
	 * <a target="_blank" href=
	 * "http://www.pjsip.org/pjsip/docs/html/structpjsip__cred__info.htm#ae31c9ec1c99fb1ffa20be5954ee995a7"
	 * >Pjsip documentation</a>
	 * 
	 * @see #scheme
	 */
	public static final String CRED_SCHEME_PGP = "PGP";

	/**
	 * Separator for proxy field once stored in database.<br/>
	 * It's the pipe char.
	 * 
	 * @see #FIELD_PROXY
	 */
	public static final String PROXIES_SEPARATOR = "|";
	// Content Provider - account status
	/**
	 * Virutal table name for sip profile adding/registration table.<br/>
	 * An application should use it in read only mode.
	 */
	public final static String ACCOUNTS_STATUS_TABLE_NAME = "accounts_status";
	// /**
	// * Content type for sip profile adding/registration state
	// */
	// public final static String ACCOUNT_STATUS_CONTENT_TYPE =
	// SipManager.BASE_DIR_TYPE
	// + ".account_status";
	// /**
	// * Content type for sip profile adding/registration state item
	// */
	// public final static String ACCOUNT_STATUS_CONTENT_ITEM_TYPE =
	// SipManager.BASE_ITEM_TYPE
	// + ".account_status";

	// Properties
	/**
	 * Primary key for serialization of the object.
	 */
	public int primaryKey = -1;
	/**
	 * @see #FIELD_ID
	 */
	public long id = INVALID_ID;
	/**
	 * @see #FIELD_DISPLAY_NAME
	 */
	public String display_name = "";
	/**
	 * @see #FIELD_TRANSPORT
	 */
	public Integer transport = 0;
	/**
	 * @see #FIELD_DEFAULT_URI_SCHEME
	 */
	public String default_uri_scheme = "sip";
	/**
	 * @see #FIELD_ACTIVE
	 */
	public boolean active = true;
	/**
	 * @see #FIELD_ACC_ID
	 */
	public String acc_id = null;

	/**
	 * @see #FIELD_REG_URI
	 */
	public String reg_uri = null;
	/**
	 * @see #FIELD_PUBLISH_ENABLED
	 */
	public int publish_enabled = 1;
	/**
	 * @see #FIELD_REG_TIMEOUT
	 */
	public int reg_timeout = 120;
	/**
	 * @see #FIELD_KA_INTERVAL
	 */
	public int ka_interval = 1;
	/**
	 * @see #FIELD_PIDF_TUPLE_ID
	 */
	public String pidf_tuple_id = null;
	/**
	 * @see #FIELD_FORCE_CONTACT
	 */
	public String force_contact = null;
	/**
	 * @see #FIELD_ALLOW_CONTACT_REWRITE
	 */
	public boolean allow_contact_rewrite = true;
	/**
	 * @see #FIELD_CONTACT_REWRITE_METHOD
	 */
	public int contact_rewrite_method = 2;
	/**
	 * @see #FIELD_ALLOW_VIA_REWRITE
	 */
	public boolean allow_via_rewrite = false;
	/**
	 * @see #FIELD_ALLOW_SDP_NAT_REWRITE
	 */
	public boolean allow_sdp_nat_rewrite = false;
//	/**
//	 * @see #FIELD_ALLOW_SDP_NAT_REWRITE
//	 */
//	public boolean allow_sdp_nat_rewrite = true;
	/**
	 * Exploded array of proxies
	 * 
	 * @see #FIELD_PROXY
	 */
	public String[] proxies = null;
	/**
	 * @see #FIELD_REALM
	 */
	public String realm = null;
	/**
	 * @see #FIELD_USERNAME
	 */
	public String username = null;
	/**
	 * @see #FIELD_SCHEME
	 */
	public String scheme = null;
	/**
	 * @see #FIELD_DATATYPE
	 */
	public int datatype = 0;
	/**
	 * @see #FIELD_DATA
	 */
	public String data = null;
	/**
	 * @see #FIELD_AUTH_INITIAL_AUTH
	 */
	public boolean initial_auth = false;
	/**
	 * @see #FIELD_AUTH_ALGO
	 */
	public String auth_algo = "";
	/**
	 * @see #FIELD_USE_SRTP
	 */
	public int use_srtp = -1;
	/**
	 * @see #FIELD_USE_ZRTP
	 */
	public int use_zrtp = -1;
	/**
	 * @see #FIELD_REG_USE_PROXY
	 */
	public int reg_use_proxy = 3;
	/**
	 * @see #FIELD_SIP_STACK
	 */
	public int sip_stack = PJSIP_STACK;
	/**
	 * @see #FIELD_VOICE_MAIL_NBR
	 */
	public String vm_nbr = null;
	/**
	 * @see #FIELD_REG_DELAY_BEFORE_REFRESH
	 */
	public int reg_delay_before_refresh = -1;
	/**
	 * @see #FIELD_TRY_CLEAN_REGISTERS
	 */
	public int try_clean_registers = 1;
	/**
	 * Chache holder icon for the account wizard representation.<br/>
	 * This will not not be filled by default. You have to get it from wizard
	 * infos.
	 */
	public Bitmap icon = null;
	/**
	 * @see #FIELD_USE_RFC5626
	 */
	public boolean use_rfc5626 = true;
	/**
	 * @see #FIELD_RFC5626_INSTANCE_ID
	 */
	public String rfc5626_instance_id = "";
	/**
	 * @see #FIELD_RFC5626_REG_ID
	 */
	public String rfc5626_reg_id = "";
	/**
	 * @see #FIELD_VID_IN_AUTO_SHOW
	 */
	public int vid_in_auto_show = -1;
	/**
	 * @see #FIELD_VID_OUT_AUTO_TRANSMIT
	 */
	public int vid_out_auto_transmit = -1;
	/**
	 * @see #FIELD_RTP_PORT
	 */
	public int rtp_port = -1;
	/**
	 * @see #FIELD_RTP_PUBLIC_ADDR
	 */
	public String rtp_public_addr = "";
	/**
	 * @see #FIELD_RTP_BOUND_ADDR
	 */
	public String rtp_bound_addr = "";
	/**
	 * @see #FIELD_RTP_ENABLE_QOS
	 */
	public int rtp_enable_qos = -1;
	/**
	 * @see #FIELD_RTP_QOS_DSCP
	 */
	public int rtp_qos_dscp = -1;
	/**
	 * @see #FIELD_ANDROID_GROUP
	 */
	public String android_group = "";
	/**
	 * @see #FIELD_MWI_ENABLED
	 */
	public boolean mwi_enabled = false;
	/**
	 * @see #FIELD_SIP_STUN_USE
	 */
	public int sip_stun_use = -1;
	/**
	 * @see #FIELD_MEDIA_STUN_USE
	 */
	public int media_stun_use = -1;
	
//	/**
//	 * @see #FIELD_ICE_CFG_USE
//	 */
//	public int ice_cfg_use = 1;
////	/**
////	 * @see #FIELD_ICE_CFG_USE
////	 */
////	public int ice_cfg_use = 1;
//	/**
//	 * @see #FIELD_ICE_CFG_ENABLE
//	 */
//	public int ice_cfg_enable = 1;
//	
	
	/**
	 * @see #FIELD_ICE_CFG_USE
	 */
	public int ice_cfg_use = -1;
//	/**
//	 * @see #FIELD_ICE_CFG_USE
//	 */
//	public int ice_cfg_use = 1;
	/**
	 * @see #FIELD_ICE_CFG_ENABLE
	 */
	public int ice_cfg_enable = 0;
	/**
	 * @see #FIELD_TURN_CFG_USE
	 */
	public int turn_cfg_use = -1;
	/**
	 * @see #FIELD_TURN_CFG_ENABLE
	 */
	public int turn_cfg_enable = 0;
	/**
	 * @see #FIELD_TURN_CFG_SERVER
	 */
	public String turn_cfg_server = "";
	/**
	 * @see #FIELD_TURN_CFG_USER
	 */
	public String turn_cfg_user = "";
	/**
	 * @see #FIELD_TURN_CFG_PASSWORD
	 */
	public String turn_cfg_password = "";
	/**
	 * @see #FIELD_IPV6_MEDIA_USE
	 */
	public int ipv6_media_use = 0;
	
	public int pjsipId=-1;

	public SipProfile() {
		display_name = "";
		active = true;
		if (id == SipProfile.INVALID_ID) {
			state = null;
		} else {
			state = new SipProfileState(this);
		}
	}

	/**
	 * Construct from parcelable <br/>
	 * Only used by {@link #CREATOR}
	 * 
	 * @param in
	 *            parcelable to build from
	 */
	private SipProfile(Parcel in) {
		primaryKey = in.readInt();
		id = in.readInt();
		display_name = in.readString();
		transport = in.readInt();
		active = (in.readInt() != 0) ? true : false;
		acc_id = getReadParcelableString(in.readString());
		reg_uri = getReadParcelableString(in.readString());
		publish_enabled = in.readInt();
		reg_timeout = in.readInt();
		ka_interval = in.readInt();
		pidf_tuple_id = getReadParcelableString(in.readString());
		force_contact = getReadParcelableString(in.readString());
		proxies = TextUtils.split(getReadParcelableString(in.readString()),
				Pattern.quote(PROXIES_SEPARATOR));
		realm = getReadParcelableString(in.readString());
		username = getReadParcelableString(in.readString());
		datatype = in.readInt();
		data = getReadParcelableString(in.readString());
		use_srtp = in.readInt();
		allow_contact_rewrite = (in.readInt() != 0);
		contact_rewrite_method = in.readInt();
		sip_stack = in.readInt();
		reg_use_proxy = in.readInt();
		use_zrtp = in.readInt();
		vm_nbr = getReadParcelableString(in.readString());
		reg_delay_before_refresh = in.readInt();
		icon = (Bitmap) in.readParcelable(Bitmap.class.getClassLoader());
		try_clean_registers = in.readInt();
		use_rfc5626 = (in.readInt() != 0);
		rfc5626_instance_id = getReadParcelableString(in.readString());
		rfc5626_reg_id = getReadParcelableString(in.readString());
		vid_in_auto_show = in.readInt();
		vid_out_auto_transmit = in.readInt();
		rtp_port = in.readInt();
		rtp_public_addr = getReadParcelableString(in.readString());
		rtp_bound_addr = getReadParcelableString(in.readString());
		rtp_enable_qos = in.readInt();
		rtp_qos_dscp = in.readInt();
		android_group = getReadParcelableString(in.readString());
		mwi_enabled = (in.readInt() != 0);
		allow_via_rewrite = (in.readInt() != 0);
		sip_stun_use = in.readInt();
		media_stun_use = in.readInt();
		ice_cfg_use = in.readInt();
		ice_cfg_enable = in.readInt();
		turn_cfg_use = in.readInt();
		turn_cfg_enable = in.readInt();
		turn_cfg_server = getReadParcelableString(in.readString());
		turn_cfg_user = getReadParcelableString(in.readString());
		turn_cfg_password = getReadParcelableString(in.readString());
		ipv6_media_use = in.readInt();
		initial_auth = (in.readInt() != 0);
		auth_algo = getReadParcelableString(in.readString());
		default_uri_scheme = getReadParcelableString(in.readString());
		allow_sdp_nat_rewrite = (in.readInt() != 0);
		if (id == SipProfile.INVALID_ID) {
			state = null;
		} else {
			state = new SipProfileState(this);
		}
	}

	/**
	 * Parcelable creator. So that it can be passed as an argument of the aidl
	 * interface
	 */
	public static final Parcelable.Creator<SipProfile> CREATOR = new Parcelable.Creator<SipProfile>() {
		public SipProfile createFromParcel(Parcel in) {
			return new SipProfile(in);
		}

		public SipProfile[] newArray(int size) {
			return new SipProfile[size];
		}
	};

	@Override
	public int describeContents() {

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		// TODO Auto-generated method stub
		dest.writeInt(primaryKey);
		dest.writeInt((int) id);
		dest.writeString(display_name);
		dest.writeInt(transport);
		dest.writeInt(active ? 1 : 0);
		dest.writeString(getWriteParcelableString(acc_id));
		dest.writeString(getWriteParcelableString(reg_uri));
		dest.writeInt(publish_enabled);
		dest.writeInt(reg_timeout);
		dest.writeInt(ka_interval);
		dest.writeString(getWriteParcelableString(pidf_tuple_id));
		dest.writeString(getWriteParcelableString(force_contact));
		if (proxies != null) {
			dest.writeString(getWriteParcelableString(TextUtils.join(
					PROXIES_SEPARATOR, proxies)));
		} else {
			dest.writeString("");
		}
		dest.writeString(getWriteParcelableString(realm));
		dest.writeString(getWriteParcelableString(username));
		dest.writeInt(datatype);
		dest.writeString(getWriteParcelableString(data));
		dest.writeInt(use_srtp);
		dest.writeInt(allow_contact_rewrite ? 1 : 0);
		dest.writeInt(contact_rewrite_method);
		dest.writeInt(sip_stack);
		dest.writeInt(reg_use_proxy);
		dest.writeInt(use_zrtp);
		dest.writeString(getWriteParcelableString(vm_nbr));
		dest.writeInt(reg_delay_before_refresh);
		dest.writeParcelable((Parcelable) icon, flags);
		dest.writeInt(try_clean_registers);
		dest.writeInt(use_rfc5626 ? 1 : 0);
		dest.writeString(getWriteParcelableString(rfc5626_instance_id));
		dest.writeString(getWriteParcelableString(rfc5626_reg_id));
		dest.writeInt(vid_in_auto_show);
		dest.writeInt(vid_out_auto_transmit);
		dest.writeInt(rtp_port);
		dest.writeString(getWriteParcelableString(rtp_public_addr));
		dest.writeString(getWriteParcelableString(rtp_bound_addr));
		dest.writeInt(rtp_enable_qos);
		dest.writeInt(rtp_qos_dscp);
		dest.writeString(getWriteParcelableString(android_group));
		dest.writeInt(mwi_enabled ? 1 : 0);
		dest.writeInt(allow_via_rewrite ? 1 : 0);
		dest.writeInt(sip_stun_use);
		dest.writeInt(media_stun_use);
		dest.writeInt(ice_cfg_use);
		dest.writeInt(ice_cfg_enable);
		dest.writeInt(turn_cfg_use);
		dest.writeInt(turn_cfg_enable);
		dest.writeString(getWriteParcelableString(turn_cfg_server));
		dest.writeString(getWriteParcelableString(turn_cfg_user));
		dest.writeString(getWriteParcelableString(turn_cfg_password));
		dest.writeInt(ipv6_media_use);
		dest.writeInt(initial_auth ? 1 : 0);
		dest.writeString(getWriteParcelableString(auth_algo));
		dest.writeString(getWriteParcelableString(default_uri_scheme));
		dest.writeInt(allow_sdp_nat_rewrite ? 1 : 0);
	}

	private String getReadParcelableString(String str) {
		return str.equalsIgnoreCase("null") ? null : str;
	}

	// Yes yes that's not clean but well as for now not problem with that.
	// and we send null.
	private String getWriteParcelableString(String str) {
		return (str == null) ? "null" : str;
	}

	/**
	 * Get the default domain for this account
	 * 
	 * @return the default domain for this account
	 */
	public String getDefaultDomain() {
		ParsedSipContactInfos parsedAoR = SipUri.parseSipContact(acc_id);
		ParsedSipUriInfos parsedInfo = null;
		if (TextUtils.isEmpty(parsedAoR.domain)) {
			// Try to fallback
			if (!TextUtils.isEmpty(reg_uri)) {
				parsedInfo = SipUri.parseSipUri(reg_uri);
			} else if (proxies != null && proxies.length > 0) {
				parsedInfo = SipUri.parseSipUri(proxies[0]);
			}
		} else {
			parsedInfo = parsedAoR.getServerSipUri();
		}

		if (parsedInfo == null) {
			return null;
		}

		if (parsedInfo.domain != null) {
			String dom = parsedInfo.domain;
			if (parsedInfo.port != 5060) {
				dom += ":" + Integer.toString(parsedInfo.port);
			}
			return dom;
		} else {
			WulianLog.d(THIS_FILE, "Domain not found for this account");
		}
		return null;
	}

	// Android API

	/**
	 * Gets the flag of 'Auto Registration'
	 * 
	 * @return true if auto register this account
	 */
	public boolean getAutoRegistration() {
		return true;
	}

	/**
	 * Gets the display name of the user.
	 * 
	 * @return the caller id for this account
	 */
	public String getDisplayName() {
		if (acc_id != null) {
			ParsedSipContactInfos parsed = SipUri.parseSipContact(acc_id);
			if (parsed.displayName != null) {
				return parsed.displayName;
			}
		}
		return "";
	}

	/**
	 * Gets the password.
	 * 
	 * @return the password of the sip profile Using this from an external
	 *         application will always be empty
	 */
	public String getPassword() {
		return data;
	}

	/**
	 * Gets the (user-defined) name of the profile.
	 * 
	 * @return the display name for this profile
	 */
	public String getProfileName() {
		return display_name;
	}

	/**
	 * Gets the network address of the server outbound proxy.
	 * 
	 * @return the first proxy server if any else empty string
	 */
	public String getProxyAddress() {
		if (proxies != null && proxies.length > 0) {
			return proxies[0];
		}
		return "";
	}

	/**
	 * Gets the SIP domain when acc_id is username@domain.
	 * 
	 * @return the sip domain for this account
	 */
	public String getSipDomain() {
		ParsedSipContactInfos parsed = SipUri.parseSipContact(acc_id);
		if (parsed.domain != null) {
			return parsed.domain;
		}
		return "";
	}

	/**
	 * Gets the SIP URI string of this profile.
	 */
	public String getUriString() {
		return acc_id;
	}

	/**
	 * Gets the username when acc_id is username@domain. WARNING : this is
	 * different from username of SipProfile which is the authentication name
	 * cause of pjsip naming
	 * 
	 * @return the username of the account sip id. <br/>
	 *         Example if acc_id is "Display Name" <sip:user@domain.com>, it
	 *         will return user.
	 */
	public String getSipUserName() {
		ParsedSipContactInfos parsed = SipUri.parseSipContact(acc_id);
		if (parsed.userName != null) {
			return parsed.userName;
		}
		return "";
	}

	public ParsedSipContactInfos formatCalleeNumber(String fuzzyNumber) {
		ParsedSipContactInfos finalCallee = SipUri.parseSipContact(fuzzyNumber);

		if (TextUtils.isEmpty(finalCallee.domain)) {
			String defaultDomain = getDefaultDomain();
			if (TextUtils.isEmpty(defaultDomain)) {
				finalCallee.domain = finalCallee.userName;
				finalCallee.userName = null;
			} else {
				finalCallee.domain = defaultDomain;
			}
		}
		if (TextUtils.isEmpty(finalCallee.scheme)) {
			if (!TextUtils.isEmpty(default_uri_scheme)) {
				finalCallee.scheme = default_uri_scheme;
			} else {
				finalCallee.scheme = SipManager.PROTOCOL_SIP;
			}
		}
		return finalCallee;
	}
	
	public void setPjsuaId(int PjsuaId) {
		this.pjsipId=PjsuaId;
	}
	
	public int getPjsuaId() {
		return this.pjsipId;
	}

	private SipProfileState state;

	public void setSipProfileState(SipProfileState state) {
		this.state = state;
	}

	public SipProfileState getSipProfileState() {
		return state;
	}

	// @Override
	// public String toString() {
	// return "SipProfile [primaryKey=" + primaryKey + ", id=" + id
	// + ", display_name=" + display_name + ", transport=" + transport
	// + ", default_uri_scheme=" + default_uri_scheme + ", active="
	// + active + ", acc_id=" + acc_id + ", reg_uri=" + reg_uri
	// + ", publish_enabled=" + publish_enabled + ", reg_timeout="
	// + reg_timeout + ", ka_interval=" + ka_interval
	// + ", pidf_tuple_id=" + pidf_tuple_id + ", force_contact="
	// + force_contact + ", allow_contact_rewrite="
	// + allow_contact_rewrite + ", contact_rewrite_method="
	// + contact_rewrite_method + ", allow_via_rewrite="
	// + allow_via_rewrite + ", allow_sdp_nat_rewrite="
	// + allow_sdp_nat_rewrite + ", proxies="
	// + Arrays.toString(proxies) + ", realm=" + realm + ", username="
	// + username + ", scheme=" + scheme + ", datatype=" + datatype
	// + ", data=" + data + ", initial_auth=" + initial_auth
	// + ", auth_algo=" + auth_algo + ", use_srtp=" + use_srtp
	// + ", use_zrtp=" + use_zrtp + ", reg_use_proxy=" + reg_use_proxy
	// + ", sip_stack=" + sip_stack + ", vm_nbr=" + vm_nbr
	// + ", reg_delay_before_refresh=" + reg_delay_before_refresh
	// + ", try_clean_registers=" + try_clean_registers + ", icon="
	// + icon + ", use_rfc5626=" + use_rfc5626
	// + ", rfc5626_instance_id=" + rfc5626_instance_id
	// + ", rfc5626_reg_id=" + rfc5626_reg_id + ", vid_in_auto_show="
	// + vid_in_auto_show + ", vid_out_auto_transmit="
	// + vid_out_auto_transmit + ", rtp_port=" + rtp_port
	// + ", rtp_public_addr=" + rtp_public_addr + ", rtp_bound_addr="
	// + rtp_bound_addr + ", rtp_enable_qos=" + rtp_enable_qos
	// + ", rtp_qos_dscp=" + rtp_qos_dscp + ", android_group="
	// + android_group + ", mwi_enabled=" + mwi_enabled
	// + ", sip_stun_use=" + sip_stun_use + ", media_stun_use="
	// + media_stun_use + ", ice_cfg_use=" + ice_cfg_use
	// + ", ice_cfg_enable=" + ice_cfg_enable + ", turn_cfg_use="
	// + turn_cfg_use + ", turn_cfg_enable=" + turn_cfg_enable
	// + ", turn_cfg_server=" + turn_cfg_server + ", turn_cfg_user="
	// + turn_cfg_user + ", turn_cfg_password=" + turn_cfg_password
	// + ", ipv6_media_use=" + ipv6_media_use + ", state=" + state.toString()
	// + "]";
	// }

}
