package com.sinomaps.geobookar.utility;

import android.util.Log;

public class MyLogger {

    /* renamed from: D */
    public static final int f95D = 2;

    /* renamed from: E */
    public static final int f96E = 5;

    /* renamed from: I */
    public static final int f97I = 3;
    public static String TAG = MyLogger.class.getSimpleName();

    /* renamed from: V */
    public static final int f98V = 1;

    /* renamed from: W */
    public static final int f99W = 4;
    public static String defaultTag = "MyLogger";
    public static boolean isShowLog = false;

    public static void init(boolean isShowLog2) {
        isShowLog = isShowLog2;
    }

    public static void init(boolean isShowLog2, String defaultTag2) {
        isShowLog = isShowLog2;
        defaultTag = defaultTag2;
    }

    /* renamed from: v */
    public static void m162v() {
        llog(1, defaultTag, null);
    }

    /* renamed from: v */
    public static void m163v(Object obj) {
        llog(1, defaultTag, obj);
    }

    /* renamed from: v */
    public static void m164v(String tag, Object obj) {
        llog(1, tag, obj);
    }

    /* renamed from: d */
    public static void m153d() {
        llog(2, defaultTag, null);
    }

    /* renamed from: d */
    public static void m154d(Object obj) {
        llog(2, defaultTag, null);
    }

    /* renamed from: d */
    public static void m155d(String tag, Object obj) {
        llog(2, tag, obj);
    }

    /* renamed from: i */
    public static void m159i() {
        llog(3, defaultTag, null);
    }

    /* renamed from: i */
    public static void m160i(Object obj) {
        llog(3, defaultTag, obj);
    }

    /* renamed from: i */
    public static void m161i(String tag, Object obj) {
        llog(3, tag, obj);
    }

    /* renamed from: w */
    public static void m165w() {
        llog(4, defaultTag, null);
    }

    /* renamed from: w */
    public static void m166w(Object obj) {
        llog(4, defaultTag, obj);
    }

    /* renamed from: w */
    public static void m167w(String tag, Object obj) {
        llog(4, tag, obj);
    }

    /* renamed from: e */
    public static void m156e() {
        llog(5, defaultTag, null);
    }

    /* renamed from: e */
    public static void m157e(Object obj) {
        llog(5, defaultTag, obj);
    }

    /* renamed from: e */
    public static void m158e(String tag, Object obj) {
        llog(5, tag, obj);
    }

    public static void llog(int type, String tagStr, Object obj) {
        String tag;
        String msg;
        if (isShowLog) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            String className = stackTrace[4].getFileName();
            String methodName = stackTrace[4].getMethodName();
            int lineNumber = stackTrace[4].getLineNumber();
            if (tagStr == null) {
                tag = className;
            } else {
                tag = tagStr;
            }
            String methodName2 = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[(").append(className).append(":").append(lineNumber).append(")#").append(methodName2).append("] ");
            if (obj == null) {
                msg = "Log with null Object";
            } else {
                msg = obj.toString();
            }
            if (msg != null) {
                stringBuilder.append(msg);
            }
            String logStr = stringBuilder.toString();
            switch (type) {
                case 1:
                    Log.v(tag, logStr);
                    return;
                case 2:
                    Log.d(tag, logStr);
                    return;
                case 3:
                    Log.i(tag, logStr);
                    return;
                case 4:
                    Log.w(tag, logStr);
                    return;
                case 5:
                    Log.e(tag, logStr);
                    return;
                default:
                    return;
            }
        }
    }
}
