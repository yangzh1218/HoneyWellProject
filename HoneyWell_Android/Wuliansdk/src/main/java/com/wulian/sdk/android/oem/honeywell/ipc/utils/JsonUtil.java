package com.wulian.sdk.android.oem.honeywell.ipc.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 作者：Administrator on 2016/8/18 21:37
 * 邮箱：huihui@wuliangroup.com
 */
public class JsonUtil {
    public static JSONObject getJsonObj(String jsonS){
        try {
            return new JSONObject(jsonS);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getKey(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getString(key);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static int getIntKey(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getInt(key);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return 0;
    }
}
