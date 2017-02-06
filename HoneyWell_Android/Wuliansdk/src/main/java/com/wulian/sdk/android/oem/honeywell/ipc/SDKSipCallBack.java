package com.wulian.sdk.android.oem.honeywell.ipc;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.siplibrary.api.SipMsgApiType;

/**
 * 作者：Administrator on 2016/8/9 10:03
 * 邮箱：huihui@wuliangroup.com
 */
public interface SDKSipCallBack {
    void DoSucceed(ErrorCode errorCode, SipMsgApiType apiType, String msg);

    void DoFailed(ErrorCode errorCode, SipMsgApiType apiTyp, String msg);

}
