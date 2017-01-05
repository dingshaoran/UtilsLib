package com.lib.utils;

import java.util.ArrayList;

import android.text.TextUtils;

public class StringUtils {
	/**
	 * 拼接两个字符串
	 * @param s1 前面的数组
	 * @param s2 后面的数组
	 * @return 拼接之后的数组
	 */
	public static String[] join(String[] s1, String[] s2) {
		if (s1 == null) {
			if (s2 == null) {
				return new String[0];
			} else {
				return s2;
			}
		} else if (s2 == null) {
			return s1;
		} else {
			String[] ss = new String[s1.length + s2.length];
			System.arraycopy(s1, 0, ss, 0, s1.length);
			System.arraycopy(s2, 0, ss, s1.length, s2.length);
			return ss;
		}
	}

	/**
	 * 数组的大小
	 * @param array
	 * @return 如果数组为空返回-1 
	 */
	public static int arraySize(Object[] array) {
		if (array != null) {
			return array.length;
		} else {
			return -1;
		}
	}

	/**
	 * 字符串的长度
	 * @param str
	 * @return str==null return-1,else return length
	 */
	public static int length(String str) {
		if (str == null) {
			return -1;
		} else {
			return str.length();
		}
	}

	/**
	 * 切个字符串返回不带空串的list
	 */
	public static ArrayList<String> split(String str, String split) {
		ArrayList<String> arrayList = new ArrayList<String>();
		if (str != null) {
			String[] split2 = str.split(split);
			for (String s : split2) {
				if (!TextUtils.isEmpty(s)) {
					arrayList.add(s);
				}
			}
		}
		return arrayList;
	}

	/**
	 * split分割的字符串
	 *
	 * @param array 要拼接的数组
	 * @param split 以什么分割
	 * @return string
	 */
	public static String join(String[] array, String split) {
		if (array == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder(array.length * 16);
		for (int i = 0; i < array.length; i++) {
			builder.append(array[i]).append(split);
		}
		if (builder.length() > 1 && split != null) {
			return builder.substring(0, builder.length() - split.length());
		}
		return "";
	}

	/**
	 *  split分割的字符串
	 *
	 * @param array 要拼接的数组
	 * @param split 以什么分割
	 * @return string
	 */
	public static String join(ArrayList<String> array, String split) {
		if (array == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder(array.size() * 16);
		for (int i = 0; i < array.size(); i++) {
			builder.append(array.get(i)).append(split);
		}
		if (builder.length() > 1 && split != null) {
			return builder.substring(0, builder.length() - split.length());
		}
		return "";
	}

	public static boolean equals(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		} else {
			return o1.equals(o2);
		}
	}
}
