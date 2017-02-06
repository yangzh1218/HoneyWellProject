package com.wulian.sdk.android.oem.honeywell.ipc;

import com.wulian.siplibrary.api.SipMsgApiType;

/**
 * 作者：Administrator on 2016/8/6 13:45
 * 邮箱：huihui@wuliangroup.com
 */
public interface SipCallBack {
    void SipDataReturn(boolean isSuccess, SipMsgApiType apiType,
                                 String xmlData, String from, String to);
}
