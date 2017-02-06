/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua;

public class pjsip_tls_setting {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public pjsip_tls_setting(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(pjsip_tls_setting obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsuaJNI.delete_pjsip_tls_setting(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setCa_list_file(pj_str_t value) {
    pjsuaJNI.pjsip_tls_setting_ca_list_file_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getCa_list_file() {
    long cPtr = pjsuaJNI.pjsip_tls_setting_ca_list_file_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setCa_list_path(pj_str_t value) {
    pjsuaJNI.pjsip_tls_setting_ca_list_path_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getCa_list_path() {
    long cPtr = pjsuaJNI.pjsip_tls_setting_ca_list_path_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setCert_file(pj_str_t value) {
    pjsuaJNI.pjsip_tls_setting_cert_file_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getCert_file() {
    long cPtr = pjsuaJNI.pjsip_tls_setting_cert_file_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setPrivkey_file(pj_str_t value) {
    pjsuaJNI.pjsip_tls_setting_privkey_file_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getPrivkey_file() {
    long cPtr = pjsuaJNI.pjsip_tls_setting_privkey_file_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setPassword(pj_str_t value) {
    pjsuaJNI.pjsip_tls_setting_password_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getPassword() {
    long cPtr = pjsuaJNI.pjsip_tls_setting_password_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setMethod(pjsip_ssl_method value) {
    pjsuaJNI.pjsip_tls_setting_method_set(swigCPtr, this, value.swigValue());
  }

  public pjsip_ssl_method getMethod() {
    return pjsip_ssl_method.swigToEnum(pjsuaJNI.pjsip_tls_setting_method_get(swigCPtr, this));
  }

  public void setProto(long value) {
    pjsuaJNI.pjsip_tls_setting_proto_set(swigCPtr, this, value);
  }

  public long getProto() {
    return pjsuaJNI.pjsip_tls_setting_proto_get(swigCPtr, this);
  }

  public void setCiphers_num(long value) {
    pjsuaJNI.pjsip_tls_setting_ciphers_num_set(swigCPtr, this, value);
  }

  public long getCiphers_num() {
    return pjsuaJNI.pjsip_tls_setting_ciphers_num_get(swigCPtr, this);
  }

  public void setCiphers(SWIGTYPE_p_pj_ssl_cipher value) {
    pjsuaJNI.pjsip_tls_setting_ciphers_set(swigCPtr, this, SWIGTYPE_p_pj_ssl_cipher.getCPtr(value));
  }

  public SWIGTYPE_p_pj_ssl_cipher getCiphers() {
    long cPtr = pjsuaJNI.pjsip_tls_setting_ciphers_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_pj_ssl_cipher(cPtr, false);
  }

  public void setVerify_server(int value) {
    pjsuaJNI.pjsip_tls_setting_verify_server_set(swigCPtr, this, value);
  }

  public int getVerify_server() {
    return pjsuaJNI.pjsip_tls_setting_verify_server_get(swigCPtr, this);
  }

  public void setVerify_client(int value) {
    pjsuaJNI.pjsip_tls_setting_verify_client_set(swigCPtr, this, value);
  }

  public int getVerify_client() {
    return pjsuaJNI.pjsip_tls_setting_verify_client_get(swigCPtr, this);
  }

  public void setRequire_client_cert(int value) {
    pjsuaJNI.pjsip_tls_setting_require_client_cert_set(swigCPtr, this, value);
  }

  public int getRequire_client_cert() {
    return pjsuaJNI.pjsip_tls_setting_require_client_cert_get(swigCPtr, this);
  }

  public void setTimeout(pj_time_val value) {
    pjsuaJNI.pjsip_tls_setting_timeout_set(swigCPtr, this, pj_time_val.getCPtr(value), value);
  }

  public pj_time_val getTimeout() {
    long cPtr = pjsuaJNI.pjsip_tls_setting_timeout_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_time_val(cPtr, false);
  }

  public void setReuse_addr(int value) {
    pjsuaJNI.pjsip_tls_setting_reuse_addr_set(swigCPtr, this, value);
  }

  public int getReuse_addr() {
    return pjsuaJNI.pjsip_tls_setting_reuse_addr_get(swigCPtr, this);
  }

  public void setQos_type(pj_qos_type value) {
    pjsuaJNI.pjsip_tls_setting_qos_type_set(swigCPtr, this, value.swigValue());
  }

  public pj_qos_type getQos_type() {
    return pj_qos_type.swigToEnum(pjsuaJNI.pjsip_tls_setting_qos_type_get(swigCPtr, this));
  }

  public void setQos_params(pj_qos_params value) {
    pjsuaJNI.pjsip_tls_setting_qos_params_set(swigCPtr, this, pj_qos_params.getCPtr(value), value);
  }

  public pj_qos_params getQos_params() {
    long cPtr = pjsuaJNI.pjsip_tls_setting_qos_params_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_qos_params(cPtr, false);
  }

  public void setQos_ignore_error(int value) {
    pjsuaJNI.pjsip_tls_setting_qos_ignore_error_set(swigCPtr, this, value);
  }

  public int getQos_ignore_error() {
    return pjsuaJNI.pjsip_tls_setting_qos_ignore_error_get(swigCPtr, this);
  }

  public void setSockopt_params(SWIGTYPE_p_pj_sockopt_params value) {
    pjsuaJNI.pjsip_tls_setting_sockopt_params_set(swigCPtr, this, SWIGTYPE_p_pj_sockopt_params.getCPtr(value));
  }

  public SWIGTYPE_p_pj_sockopt_params getSockopt_params() {
    return new SWIGTYPE_p_pj_sockopt_params(pjsuaJNI.pjsip_tls_setting_sockopt_params_get(swigCPtr, this), true);
  }

  public void setSockopt_ignore_error(int value) {
    pjsuaJNI.pjsip_tls_setting_sockopt_ignore_error_set(swigCPtr, this, value);
  }

  public int getSockopt_ignore_error() {
    return pjsuaJNI.pjsip_tls_setting_sockopt_ignore_error_get(swigCPtr, this);
  }

  public pjsip_tls_setting() {
    this(pjsuaJNI.new_pjsip_tls_setting(), true);
  }

}
