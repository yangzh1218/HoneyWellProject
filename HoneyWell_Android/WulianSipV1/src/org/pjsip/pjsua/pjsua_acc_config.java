/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua;

public class pjsua_acc_config {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public pjsua_acc_config(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(pjsua_acc_config obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsuaJNI.delete_pjsua_acc_config(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setUser_data(byte[] value) {
    pjsuaJNI.pjsua_acc_config_user_data_set(swigCPtr, this, value);
  }

  public byte[] getUser_data() {
	return pjsuaJNI.pjsua_acc_config_user_data_get(swigCPtr, this);
}

  public void setPriority(int value) {
    pjsuaJNI.pjsua_acc_config_priority_set(swigCPtr, this, value);
  }

  public int getPriority() {
    return pjsuaJNI.pjsua_acc_config_priority_get(swigCPtr, this);
  }

  public void setId(pj_str_t value) {
    pjsuaJNI.pjsua_acc_config_id_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getId() {
    long cPtr = pjsuaJNI.pjsua_acc_config_id_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setReg_uri(pj_str_t value) {
    pjsuaJNI.pjsua_acc_config_reg_uri_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getReg_uri() {
    long cPtr = pjsuaJNI.pjsua_acc_config_reg_uri_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setReg_hdr_list(SWIGTYPE_p_pjsip_hdr value) {
    pjsuaJNI.pjsua_acc_config_reg_hdr_list_set(swigCPtr, this, SWIGTYPE_p_pjsip_hdr.getCPtr(value));
  }

  public SWIGTYPE_p_pjsip_hdr getReg_hdr_list() {
    return new SWIGTYPE_p_pjsip_hdr(pjsuaJNI.pjsua_acc_config_reg_hdr_list_get(swigCPtr, this), true);
  }

  public void setSub_hdr_list(SWIGTYPE_p_pjsip_hdr value) {
    pjsuaJNI.pjsua_acc_config_sub_hdr_list_set(swigCPtr, this, SWIGTYPE_p_pjsip_hdr.getCPtr(value));
  }

  public SWIGTYPE_p_pjsip_hdr getSub_hdr_list() {
    return new SWIGTYPE_p_pjsip_hdr(pjsuaJNI.pjsua_acc_config_sub_hdr_list_get(swigCPtr, this), true);
  }

  public void setMwi_enabled(int value) {
    pjsuaJNI.pjsua_acc_config_mwi_enabled_set(swigCPtr, this, value);
  }

  public int getMwi_enabled() {
    return pjsuaJNI.pjsua_acc_config_mwi_enabled_get(swigCPtr, this);
  }

  public void setMwi_expires(long value) {
    pjsuaJNI.pjsua_acc_config_mwi_expires_set(swigCPtr, this, value);
  }

  public long getMwi_expires() {
    return pjsuaJNI.pjsua_acc_config_mwi_expires_get(swigCPtr, this);
  }

  public void setPublish_enabled(int value) {
    pjsuaJNI.pjsua_acc_config_publish_enabled_set(swigCPtr, this, value);
  }

  public int getPublish_enabled() {
    return pjsuaJNI.pjsua_acc_config_publish_enabled_get(swigCPtr, this);
  }

  public void setPublish_opt(SWIGTYPE_p_pjsip_publishc_opt value) {
    pjsuaJNI.pjsua_acc_config_publish_opt_set(swigCPtr, this, SWIGTYPE_p_pjsip_publishc_opt.getCPtr(value));
  }

  public SWIGTYPE_p_pjsip_publishc_opt getPublish_opt() {
    return new SWIGTYPE_p_pjsip_publishc_opt(pjsuaJNI.pjsua_acc_config_publish_opt_get(swigCPtr, this), true);
  }

  public void setUnpublish_max_wait_time_msec(long value) {
    pjsuaJNI.pjsua_acc_config_unpublish_max_wait_time_msec_set(swigCPtr, this, value);
  }

  public long getUnpublish_max_wait_time_msec() {
    return pjsuaJNI.pjsua_acc_config_unpublish_max_wait_time_msec_get(swigCPtr, this);
  }

  public void setAuth_pref(pjsip_auth_clt_pref value) {
    pjsuaJNI.pjsua_acc_config_auth_pref_set(swigCPtr, this, pjsip_auth_clt_pref.getCPtr(value), value);
  }

  public pjsip_auth_clt_pref getAuth_pref() {
    long cPtr = pjsuaJNI.pjsua_acc_config_auth_pref_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pjsip_auth_clt_pref(cPtr, false);
  }

  public void setPidf_tuple_id(pj_str_t value) {
    pjsuaJNI.pjsua_acc_config_pidf_tuple_id_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getPidf_tuple_id() {
    long cPtr = pjsuaJNI.pjsua_acc_config_pidf_tuple_id_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setForce_contact(pj_str_t value) {
    pjsuaJNI.pjsua_acc_config_force_contact_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getForce_contact() {
    long cPtr = pjsuaJNI.pjsua_acc_config_force_contact_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setContact_params(pj_str_t value) {
    pjsuaJNI.pjsua_acc_config_contact_params_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getContact_params() {
    long cPtr = pjsuaJNI.pjsua_acc_config_contact_params_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setContact_uri_params(pj_str_t value) {
    pjsuaJNI.pjsua_acc_config_contact_uri_params_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getContact_uri_params() {
    long cPtr = pjsuaJNI.pjsua_acc_config_contact_uri_params_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setRequire_100rel(pjsua_100rel_use value) {
    pjsuaJNI.pjsua_acc_config_require_100rel_set(swigCPtr, this, value.swigValue());
  }

  public pjsua_100rel_use getRequire_100rel() {
    return pjsua_100rel_use.swigToEnum(pjsuaJNI.pjsua_acc_config_require_100rel_get(swigCPtr, this));
  }

  public void setUse_timer(pjsua_sip_timer_use value) {
    pjsuaJNI.pjsua_acc_config_use_timer_set(swigCPtr, this, value.swigValue());
  }

  public pjsua_sip_timer_use getUse_timer() {
    return pjsua_sip_timer_use.swigToEnum(pjsuaJNI.pjsua_acc_config_use_timer_get(swigCPtr, this));
  }

  public void setTimer_setting(pjsip_timer_setting value) {
    pjsuaJNI.pjsua_acc_config_timer_setting_set(swigCPtr, this, pjsip_timer_setting.getCPtr(value), value);
  }

  public pjsip_timer_setting getTimer_setting() {
    long cPtr = pjsuaJNI.pjsua_acc_config_timer_setting_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pjsip_timer_setting(cPtr, false);
  }

  public void setProxy_cnt(long value) {
    pjsuaJNI.pjsua_acc_config_proxy_cnt_set(swigCPtr, this, value);
  }

  public long getProxy_cnt() {
    return pjsuaJNI.pjsua_acc_config_proxy_cnt_get(swigCPtr, this);
  }

  public void setProxy(pj_str_t[] value) {
    pjsuaJNI.pjsua_acc_config_proxy_set(swigCPtr, this, pj_str_t.cArrayUnwrap(value));
  }

  public pj_str_t[] getProxy() {
    return pj_str_t.cArrayWrap(pjsuaJNI.pjsua_acc_config_proxy_get(swigCPtr, this), false);
  }

  public void setLock_codec(long value) {
    pjsuaJNI.pjsua_acc_config_lock_codec_set(swigCPtr, this, value);
  }

  public long getLock_codec() {
    return pjsuaJNI.pjsua_acc_config_lock_codec_get(swigCPtr, this);
  }

  public void setReg_timeout(long value) {
    pjsuaJNI.pjsua_acc_config_reg_timeout_set(swigCPtr, this, value);
  }

  public long getReg_timeout() {
    return pjsuaJNI.pjsua_acc_config_reg_timeout_get(swigCPtr, this);
  }

  public void setReg_delay_before_refresh(long value) {
    pjsuaJNI.pjsua_acc_config_reg_delay_before_refresh_set(swigCPtr, this, value);
  }

  public long getReg_delay_before_refresh() {
    return pjsuaJNI.pjsua_acc_config_reg_delay_before_refresh_get(swigCPtr, this);
  }

  public void setUnreg_timeout(long value) {
    pjsuaJNI.pjsua_acc_config_unreg_timeout_set(swigCPtr, this, value);
  }

  public long getUnreg_timeout() {
    return pjsuaJNI.pjsua_acc_config_unreg_timeout_get(swigCPtr, this);
  }

  public void setCred_count(long value) {
    pjsuaJNI.pjsua_acc_config_cred_count_set(swigCPtr, this, value);
  }

  public long getCred_count() {
    return pjsuaJNI.pjsua_acc_config_cred_count_get(swigCPtr, this);
  }

  public void setCred_info(pjsip_cred_info value) {
    pjsuaJNI.pjsua_acc_config_cred_info_set(swigCPtr, this, pjsip_cred_info.getCPtr(value), value);
  }

  public pjsip_cred_info getCred_info() {
    long cPtr = pjsuaJNI.pjsua_acc_config_cred_info_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pjsip_cred_info(cPtr, false);
  }

  public void setTransport_id(int value) {
    pjsuaJNI.pjsua_acc_config_transport_id_set(swigCPtr, this, value);
  }

  public int getTransport_id() {
    return pjsuaJNI.pjsua_acc_config_transport_id_get(swigCPtr, this);
  }

  public void setAllow_contact_rewrite(int value) {
    pjsuaJNI.pjsua_acc_config_allow_contact_rewrite_set(swigCPtr, this, value);
  }

  public int getAllow_contact_rewrite() {
    return pjsuaJNI.pjsua_acc_config_allow_contact_rewrite_get(swigCPtr, this);
  }

  public void setContact_rewrite_method(int value) {
    pjsuaJNI.pjsua_acc_config_contact_rewrite_method_set(swigCPtr, this, value);
  }

  public int getContact_rewrite_method() {
    return pjsuaJNI.pjsua_acc_config_contact_rewrite_method_get(swigCPtr, this);
  }

  public void setContact_use_src_port(int value) {
    pjsuaJNI.pjsua_acc_config_contact_use_src_port_set(swigCPtr, this, value);
  }

  public int getContact_use_src_port() {
    return pjsuaJNI.pjsua_acc_config_contact_use_src_port_get(swigCPtr, this);
  }

  public void setAllow_via_rewrite(int value) {
    pjsuaJNI.pjsua_acc_config_allow_via_rewrite_set(swigCPtr, this, value);
  }

  public int getAllow_via_rewrite() {
    return pjsuaJNI.pjsua_acc_config_allow_via_rewrite_get(swigCPtr, this);
  }

  public void setAllow_sdp_nat_rewrite(int value) {
    pjsuaJNI.pjsua_acc_config_allow_sdp_nat_rewrite_set(swigCPtr, this, value);
  }

  public int getAllow_sdp_nat_rewrite() {
    return pjsuaJNI.pjsua_acc_config_allow_sdp_nat_rewrite_get(swigCPtr, this);
  }

  public void setUse_rfc5626(long value) {
    pjsuaJNI.pjsua_acc_config_use_rfc5626_set(swigCPtr, this, value);
  }

  public long getUse_rfc5626() {
    return pjsuaJNI.pjsua_acc_config_use_rfc5626_get(swigCPtr, this);
  }

  public void setRfc5626_instance_id(pj_str_t value) {
    pjsuaJNI.pjsua_acc_config_rfc5626_instance_id_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getRfc5626_instance_id() {
    long cPtr = pjsuaJNI.pjsua_acc_config_rfc5626_instance_id_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setRfc5626_reg_id(pj_str_t value) {
    pjsuaJNI.pjsua_acc_config_rfc5626_reg_id_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getRfc5626_reg_id() {
    long cPtr = pjsuaJNI.pjsua_acc_config_rfc5626_reg_id_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setKa_interval(long value) {
    pjsuaJNI.pjsua_acc_config_ka_interval_set(swigCPtr, this, value);
  }

  public long getKa_interval() {
    return pjsuaJNI.pjsua_acc_config_ka_interval_get(swigCPtr, this);
  }

  public void setKa_data(pj_str_t value) {
    pjsuaJNI.pjsua_acc_config_ka_data_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getKa_data() {
    long cPtr = pjsuaJNI.pjsua_acc_config_ka_data_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setVid_in_auto_show(int value) {
    pjsuaJNI.pjsua_acc_config_vid_in_auto_show_set(swigCPtr, this, value);
  }

  public int getVid_in_auto_show() {
    return pjsuaJNI.pjsua_acc_config_vid_in_auto_show_get(swigCPtr, this);
  }

  public void setVid_out_auto_transmit(int value) {
    pjsuaJNI.pjsua_acc_config_vid_out_auto_transmit_set(swigCPtr, this, value);
  }

  public int getVid_out_auto_transmit() {
    return pjsuaJNI.pjsua_acc_config_vid_out_auto_transmit_get(swigCPtr, this);
  }

  public void setVid_wnd_flags(long value) {
    pjsuaJNI.pjsua_acc_config_vid_wnd_flags_set(swigCPtr, this, value);
  }

  public long getVid_wnd_flags() {
    return pjsuaJNI.pjsua_acc_config_vid_wnd_flags_get(swigCPtr, this);
  }

  public void setVid_cap_dev(SWIGTYPE_p_pjmedia_vid_dev_index value) {
    pjsuaJNI.pjsua_acc_config_vid_cap_dev_set(swigCPtr, this, SWIGTYPE_p_pjmedia_vid_dev_index.getCPtr(value));
  }

  public SWIGTYPE_p_pjmedia_vid_dev_index getVid_cap_dev() {
    return new SWIGTYPE_p_pjmedia_vid_dev_index(pjsuaJNI.pjsua_acc_config_vid_cap_dev_get(swigCPtr, this), true);
  }

  public void setVid_rend_dev(SWIGTYPE_p_pjmedia_vid_dev_index value) {
    pjsuaJNI.pjsua_acc_config_vid_rend_dev_set(swigCPtr, this, SWIGTYPE_p_pjmedia_vid_dev_index.getCPtr(value));
  }

  public SWIGTYPE_p_pjmedia_vid_dev_index getVid_rend_dev() {
    return new SWIGTYPE_p_pjmedia_vid_dev_index(pjsuaJNI.pjsua_acc_config_vid_rend_dev_get(swigCPtr, this), true);
  }

  public void setVid_stream_rc_cfg(SWIGTYPE_p_pjmedia_vid_stream_rc_config value) {
    pjsuaJNI.pjsua_acc_config_vid_stream_rc_cfg_set(swigCPtr, this, SWIGTYPE_p_pjmedia_vid_stream_rc_config.getCPtr(value));
  }

  public SWIGTYPE_p_pjmedia_vid_stream_rc_config getVid_stream_rc_cfg() {
    return new SWIGTYPE_p_pjmedia_vid_stream_rc_config(pjsuaJNI.pjsua_acc_config_vid_stream_rc_cfg_get(swigCPtr, this), true);
  }

  public void setRtp_cfg(pjsua_transport_config value) {
    pjsuaJNI.pjsua_acc_config_rtp_cfg_set(swigCPtr, this, pjsua_transport_config.getCPtr(value), value);
  }

  public pjsua_transport_config getRtp_cfg() {
    long cPtr = pjsuaJNI.pjsua_acc_config_rtp_cfg_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pjsua_transport_config(cPtr, false);
  }

  public void setIpv6_media_use(pjsua_ipv6_use value) {
    pjsuaJNI.pjsua_acc_config_ipv6_media_use_set(swigCPtr, this, value.swigValue());
  }

  public pjsua_ipv6_use getIpv6_media_use() {
    return pjsua_ipv6_use.swigToEnum(pjsuaJNI.pjsua_acc_config_ipv6_media_use_get(swigCPtr, this));
  }

  public void setSip_stun_use(pjsua_stun_use value) {
    pjsuaJNI.pjsua_acc_config_sip_stun_use_set(swigCPtr, this, value.swigValue());
  }

  public pjsua_stun_use getSip_stun_use() {
    return pjsua_stun_use.swigToEnum(pjsuaJNI.pjsua_acc_config_sip_stun_use_get(swigCPtr, this));
  }

  public void setMedia_stun_use(pjsua_stun_use value) {
    pjsuaJNI.pjsua_acc_config_media_stun_use_set(swigCPtr, this, value.swigValue());
  }

  public pjsua_stun_use getMedia_stun_use() {
    return pjsua_stun_use.swigToEnum(pjsuaJNI.pjsua_acc_config_media_stun_use_get(swigCPtr, this));
  }

  public void setIce_cfg_use(pjsua_ice_config_use value) {
    pjsuaJNI.pjsua_acc_config_ice_cfg_use_set(swigCPtr, this, value.swigValue());
  }

  public pjsua_ice_config_use getIce_cfg_use() {
    return pjsua_ice_config_use.swigToEnum(pjsuaJNI.pjsua_acc_config_ice_cfg_use_get(swigCPtr, this));
  }

  public void setIce_cfg(pjsua_ice_config value) {
    pjsuaJNI.pjsua_acc_config_ice_cfg_set(swigCPtr, this, pjsua_ice_config.getCPtr(value), value);
  }

  public pjsua_ice_config getIce_cfg() {
    long cPtr = pjsuaJNI.pjsua_acc_config_ice_cfg_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pjsua_ice_config(cPtr, false);
  }

  public void setTurn_cfg_use(pjsua_turn_config_use value) {
    pjsuaJNI.pjsua_acc_config_turn_cfg_use_set(swigCPtr, this, value.swigValue());
  }

  public pjsua_turn_config_use getTurn_cfg_use() {
    return pjsua_turn_config_use.swigToEnum(pjsuaJNI.pjsua_acc_config_turn_cfg_use_get(swigCPtr, this));
  }

  public void setTurn_cfg(pjsua_turn_config value) {
    pjsuaJNI.pjsua_acc_config_turn_cfg_set(swigCPtr, this, pjsua_turn_config.getCPtr(value), value);
  }

  public pjsua_turn_config getTurn_cfg() {
    long cPtr = pjsuaJNI.pjsua_acc_config_turn_cfg_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pjsua_turn_config(cPtr, false);
  }

  public void setUse_srtp(pjmedia_srtp_use value) {
    pjsuaJNI.pjsua_acc_config_use_srtp_set(swigCPtr, this, value.swigValue());
  }

  public pjmedia_srtp_use getUse_srtp() {
    return pjmedia_srtp_use.swigToEnum(pjsuaJNI.pjsua_acc_config_use_srtp_get(swigCPtr, this));
  }

  public void setSrtp_secure_signaling(int value) {
    pjsuaJNI.pjsua_acc_config_srtp_secure_signaling_set(swigCPtr, this, value);
  }

  public int getSrtp_secure_signaling() {
    return pjsuaJNI.pjsua_acc_config_srtp_secure_signaling_get(swigCPtr, this);
  }

  public void setSrtp_optional_dup_offer(int value) {
    pjsuaJNI.pjsua_acc_config_srtp_optional_dup_offer_set(swigCPtr, this, value);
  }

  public int getSrtp_optional_dup_offer() {
    return pjsuaJNI.pjsua_acc_config_srtp_optional_dup_offer_get(swigCPtr, this);
  }

  public void setReg_retry_interval(long value) {
    pjsuaJNI.pjsua_acc_config_reg_retry_interval_set(swigCPtr, this, value);
  }

  public long getReg_retry_interval() {
    return pjsuaJNI.pjsua_acc_config_reg_retry_interval_get(swigCPtr, this);
  }

  public void setReg_first_retry_interval(long value) {
    pjsuaJNI.pjsua_acc_config_reg_first_retry_interval_set(swigCPtr, this, value);
  }

  public long getReg_first_retry_interval() {
    return pjsuaJNI.pjsua_acc_config_reg_first_retry_interval_get(swigCPtr, this);
  }

  public void setReg_retry_random_interval(long value) {
    pjsuaJNI.pjsua_acc_config_reg_retry_random_interval_set(swigCPtr, this, value);
  }

  public long getReg_retry_random_interval() {
    return pjsuaJNI.pjsua_acc_config_reg_retry_random_interval_get(swigCPtr, this);
  }

  public void setDrop_calls_on_reg_fail(int value) {
    pjsuaJNI.pjsua_acc_config_drop_calls_on_reg_fail_set(swigCPtr, this, value);
  }

  public int getDrop_calls_on_reg_fail() {
    return pjsuaJNI.pjsua_acc_config_drop_calls_on_reg_fail_get(swigCPtr, this);
  }

  public void setReg_use_proxy(long value) {
    pjsuaJNI.pjsua_acc_config_reg_use_proxy_set(swigCPtr, this, value);
  }

  public long getReg_use_proxy() {
    return pjsuaJNI.pjsua_acc_config_reg_use_proxy_get(swigCPtr, this);
  }

  public void setCall_hold_type(pjsua_call_hold_type value) {
    pjsuaJNI.pjsua_acc_config_call_hold_type_set(swigCPtr, this, value.swigValue());
  }

  public pjsua_call_hold_type getCall_hold_type() {
    return pjsua_call_hold_type.swigToEnum(pjsuaJNI.pjsua_acc_config_call_hold_type_get(swigCPtr, this));
  }

  public void setRegister_on_acc_add(int value) {
    pjsuaJNI.pjsua_acc_config_register_on_acc_add_set(swigCPtr, this, value);
  }

  public int getRegister_on_acc_add() {
    return pjsuaJNI.pjsua_acc_config_register_on_acc_add_get(swigCPtr, this);
  }

  public pjsua_acc_config() {
    this(pjsuaJNI.new_pjsua_acc_config(), true);
  }

}
