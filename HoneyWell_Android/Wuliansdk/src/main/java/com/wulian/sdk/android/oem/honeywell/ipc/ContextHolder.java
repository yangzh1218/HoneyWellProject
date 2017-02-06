package com.wulian.sdk.android.oem.honeywell.ipc;

import android.content.Context;

/**
 * 作者：Administrator on 2016/9/5 09:58
 * 邮箱：huihui@wuliangroup.com
 */

public class ContextHolder {

    static Context ApplicationContext;

    public static void initial(Context context) {
        ApplicationContext = context;
    }
    public static Context getContext() {
        return ApplicationContext;
    }
}