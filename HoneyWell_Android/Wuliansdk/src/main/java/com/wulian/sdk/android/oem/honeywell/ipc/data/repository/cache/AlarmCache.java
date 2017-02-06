package com.wulian.sdk.android.oem.honeywell.ipc.data.repository.cache;

import android.util.Pair;

import com.wulian.sdk.android.oem.honeywell.ipc.model.Device;

import java.util.List;

/**
 * 作者：Administrator on 2016/7/19 19:13
 * 邮箱：huihui@wuliangroup.com
 */
public interface AlarmCache {

    List<Pair<String, Device>> getListPair();

    boolean isCached();

    /**
     * Evict all elements of the cache.
     */
    void evictAll();

    void put(List<Pair<String, Device>> listPair);

}
