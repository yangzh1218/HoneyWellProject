package com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache;

import com.wulian.sdk.android.oem.honeywell.ipc.model.UserInfo;

/**
 * 作者：Administrator on 2016/7/19 19:21
 * 邮箱：huihui@wuliangroup.com
 */
public interface UserInfoCache {

    UserInfo getUserInfo();

    void put(UserInfo userInfo);

    boolean isCached();

    boolean isExpired();

    void putPassword(String pwd);

    /**
     * Evict all elements of the cache.
     */
    void evictAll();
}
