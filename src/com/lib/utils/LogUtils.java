package com.lib.utils;

import android.util.Log;

public class LogUtils {
	public static boolean debug = true;

	/** 以级别为 d 的形式输出LOG */
	public static void v(String tag, String msg) {
		if (debug) {
			Log.v(tag, msg);
		}
	}

	/** 以级别为 d 的形式输出LOG */
	public static void d(String tag, String msg) {
		if (debug) {
			Log.d(tag, msg);
		}
	}

	/** 以级别为 i 的形式输出LOG */
	public static void i(String tag, String msg) {
		if (debug) {
			Log.i(tag, msg);
		}
	}

	/** 以级别为 w 的形式输出LOG */
	public static void w(String tag, String msg) {
		if (debug) {
			Log.w(tag, msg);
		}
	}

	/** 以级别为 w 的形式输出Throwable */
	public static void w(String tag, Throwable tr) {
		if (debug) {
			Log.w(tag, "", tr);
		}
	}

	/** 以级别为 e 的形式输出LOG */
	public static void e(String tag, String msg) {
		if (debug) {
			Log.e(tag, msg);
		}
	}

	/** 以级别为 e 的形式输出Throwable */
	public static void e(String tag, Throwable tr) {
		if (debug) {
			Log.e(tag, "", tr);
		}
	}
}
