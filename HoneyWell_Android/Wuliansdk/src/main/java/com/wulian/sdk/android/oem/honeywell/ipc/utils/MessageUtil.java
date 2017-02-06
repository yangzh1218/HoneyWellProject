package com.wulian.sdk.android.oem.honeywell.ipc.utils;

import android.os.Bundle;

/**
 * 作者：Administrator on 2016/7/13 10:33
 * 邮箱：huihui@wuliangroup.com
 */
public class MessageUtil {
    public static android.os.Message set(int type, String key, String val){
        android.os.Message message = new android.os.Message();
        message.what = type;
        Bundle bundle = new Bundle();
        bundle.putString(key, val);
        message.setData(bundle);
        return message;
    }

}
