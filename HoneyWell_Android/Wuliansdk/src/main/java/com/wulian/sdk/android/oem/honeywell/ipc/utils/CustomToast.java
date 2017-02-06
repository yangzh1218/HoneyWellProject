package com.wulian.sdk.android.oem.honeywell.ipc.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.sdk.android.oem.honeywell.ipc.R;

/**
 * @author Wangjj
 * @Function: 全局的土司, 缺陷在于同一个上下文只会显示最后一个土司
 * @date: 2014年11月4日
 */
public class CustomToast {
    private static Toast mToast;
    private static TextView toast_txt;
    private static Context old_Context;

    public static void show(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
//        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, CharSequence text, int duration) {
        text = text.toString().replaceAll("\\(\\d{3}\\)", "");
        // 自定义
        if (mToast == null || context != old_Context) {
            mToast = new Toast(context);
        }
        old_Context = context;

//        View view = LayoutInflater.from(context).inflate(R.layout.custom_toast,
//                null);
//        toast_txt = (TextView) view.findViewById(R.id.toast_txt);
//        toast_txt.setText(text);
//        mToast.setView(view);
        mToast.setText(text);
        mToast.setDuration(duration);
        mToast.show();
    }

    public static void show(Context context, int textid) {
        Toast.makeText(context, textid, Toast.LENGTH_SHORT).show();
//        show(context, textid, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int textid, int duration) {
        Toast.makeText(context, textid, duration).show();
//        String content = context.getResources().getString(textid);
//        show(context, content, duration);
    }
}