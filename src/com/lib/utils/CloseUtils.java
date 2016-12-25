package com.lib.utils;

import java.io.Closeable;
import java.io.Writer;

public class CloseUtils {
	/** 关闭流 */
	public static boolean close(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (Throwable e) {// 还有其他异常IncompatibleClassChangeError 2.0
				LogUtils.e("CloseUtils", e);
			}
		}
		return true;
	}

	/** 关闭流 */
	public static boolean close(Writer io) {
		if (io != null) {
			try {
				io.close();
			} catch (Throwable e) {// 还有其他异常IncompatibleClassChangeError 2.0
				LogUtils.e("CloseUtils", e);
			}
		}
		return true;
	}
}
