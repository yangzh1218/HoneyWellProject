package com.wulian.sdk.android.oem.honeywell.ipc.error;

import android.content.Context;


import com.wulian.sdk.android.oem.honeywell.ipc.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 作者：Administrator on 2016/7/29 10:21
 * 邮箱：huihui@wuliangroup.com
 */
public class ErrorUtil {
    public static String errorMsg(Context context, JSONObject jsonObject) throws JSONException {
        String msg = jsonObject.toString();
//        if (jsonObject.getInt("error_code") == 400) {
//            if (jsonObject.getString("error_msg").equalsIgnoreCase("Invalid Password.")) {
//                msg = context.getResources().getString(R.string.pwd_small_or_simple);
//            } else if (jsonObject.getString("error_msg").equalsIgnoreCase("Invalid Param.")) {
//                msg = context.getResources().getString(R.string.param_error_or_illegal);
//            }
//        } else
        if (jsonObject.getInt("error_code") == 401) {
            msg = context.getResources().getString(R.string.exception_1111);
        } else if (jsonObject.getInt("error_code") == 409) {
            if(jsonObject.getString("error_msg").equalsIgnoreCase("Already Binded Or Authed.")){
                msg = context.getResources().getString(R.string.share_already_2);
            }
        } else if(jsonObject.getInt("error_code") == 410) {
            msg = context.getResources().getString(R.string.exception_1110);
        }
        return msg;
    }
}
