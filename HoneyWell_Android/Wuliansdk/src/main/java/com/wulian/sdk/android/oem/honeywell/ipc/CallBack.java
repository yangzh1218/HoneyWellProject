package com.wulian.sdk.android.oem.honeywell.ipc;

import com.wulian.routelibrary.common.RouteApiType;

/**
 * 作者：Administrator on 2016/7/20 08:42
 * 邮箱：huihui@wuliangroup.com
 */
public interface CallBack {
    void DoSucceed(ErrorCode errorCode, RouteApiType apiType, String json);
    void DoFailed(ErrorCode errorCode, RouteApiType apiType, Exception exception);
}
