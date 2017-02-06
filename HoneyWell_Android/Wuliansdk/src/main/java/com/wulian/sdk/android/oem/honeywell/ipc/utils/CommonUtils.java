package com.wulian.sdk.android.oem.honeywell.ipc.utils;

import android.content.Context;

import java.util.Locale;

/**
 * 作者：Administrator on 2016/6/6 11:37
 * 邮箱：huihui@wuliangroup.com
 */
public class CommonUtils {
    private boolean isZh(Context context)
    {
        Locale locale =  context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if(language.endsWith("zh"))
        {
            return true;
        }
        else
            return false;
    }

    public static String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh-CN";
            } else if ("tw".equals(country)) {
                language = "zh-TW";
            }
        } else if ("pt".equals(language)) {
            if ("br".equals(country)) {
                language = "pt-BR";
            } else if ("pt".equals(country)) {
                language = "pt-PT";
            }
        }
        return language;
    }
}
