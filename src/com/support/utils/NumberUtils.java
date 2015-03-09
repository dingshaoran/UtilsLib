package com.support.utils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtils {
	/** 四舍五入 **/
	public static String halfFormat(String str) {
		DecimalFormat df = new DecimalFormat("###,##0.00");
		String result = df.format(Double.parseDouble(str));
		return result;
	}

	/** 判断一个字符串是否为整数 **/
	public static boolean isInt(String str) {
		Pattern pattern = Pattern.compile("^\\d+$|-\\d+$");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches())
			return false;
		return true;
	}

	/** 判断字符串是否为小数 **/
	public static boolean isDouble(String str) {
		Pattern pattern = Pattern.compile("\\d+\\.\\d+$|-\\d+\\.\\d+$");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches())
			return false;
		return true;
	}
}
