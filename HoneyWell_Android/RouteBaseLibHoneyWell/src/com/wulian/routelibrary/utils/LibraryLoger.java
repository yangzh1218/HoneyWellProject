/**
 * Project Name:  RouteLibrary
 * File Name:     LibraryLoger.java
 * Package Name:  com.wulian.routelibrary.utils
 * Date:          2014-9-5
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.utils;

import android.util.Log;

/**
 * ClassName: LibraryLoger Function: 库文件的日志 Date: 2014-9-5
 *
 * @author Puml email puml@wuliangroup.cn
 */
public class LibraryLoger {

    private static boolean DEBUG = false;// 请不要再改动，开发时为true，发布时自动变成false。

    public static boolean getLoger() {
        return DEBUG;
    }

    public static void setLoger(boolean d) {
        DEBUG = d;
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable throwable) {
        if (DEBUG) {
            Log.e(tag, msg, throwable);
        }
    }

    public static void e(String tag, Throwable throwable) {
        if (DEBUG) {
            Log.e(tag, "", throwable);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void v(String msg) {
        if (!DEBUG)
            return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        Log.v(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));
    }

    public static void d(String msg) {
        if (!DEBUG)
            return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];
        // Utils.sysoInfo(msg);
        Log.d(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));
    }

    public static void i(String msg) {
        if (!DEBUG)
            return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        Log.i(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));
    }

    public static void w(String msg) {
        if (!DEBUG)
            return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        Log.w(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));
    }

    public static void e(String msg) {
        if (!DEBUG)
            return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        Log.e(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));
    }

}
