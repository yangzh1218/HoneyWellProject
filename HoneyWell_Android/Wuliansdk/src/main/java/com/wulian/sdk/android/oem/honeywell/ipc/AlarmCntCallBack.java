package com.wulian.sdk.android.oem.honeywell.ipc;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.siplibrary.api.SipMsgApiType;

/**
 * 作者：Administrator on 2016/8/25 19:59
 * 邮箱：huihui@wuliangroup.com
 */
public interface AlarmCntCallBack {
    void DoSucceed(ErrorCode errorCode, SipMsgApiType apiType, String json);
}
