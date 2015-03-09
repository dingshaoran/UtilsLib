package com.support.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.support.base.BaseApplication;

public class SharedPreferencesUtils {

	private SharedPreferencesUtils() {
	}

	public static void saveBoolean(String name, boolean value) {
		SharedPreferences.Editor editor = BaseApplication.getApplication().getSharedPreferences(name, Context.MODE_PRIVATE).edit();
		editor.putBoolean(name, value);
		editor.commit();
	}

	/**
	 * 获取boolean值
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBoolean(String name, boolean defaultValue) {
		return BaseApplication.getApplication().getSharedPreferences(name, Context.MODE_PRIVATE).getBoolean(name, defaultValue);
	}

	/**
	 * 保存long值
	 * 
	 * @param name
	 * @param value
	 */
	public static void saveLong(String name, long value) {
		SharedPreferences.Editor editor = BaseApplication.getApplication().getSharedPreferences(name, Context.MODE_PRIVATE).edit();
		editor.putLong(name, value);
		editor.commit();
	}

	/**
	 * 获取long值
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static long getLong(String name, long defaultValue) {
		return BaseApplication.getApplication().getSharedPreferences(name, Context.MODE_PRIVATE).getLong(name, defaultValue);
	}

	/**
	 * 保存int值
	 * 
	 * @param name
	 * @param value
	 */
	public static void saveInt(String name, int value) {
		SharedPreferences.Editor editor = BaseApplication.getApplication().getSharedPreferences(name, Context.MODE_PRIVATE).edit();
		editor.putInt(name, value);
		editor.commit();
	}

	/**
	 * 获取int值
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static int getInt(String name, int defaultValue) {
		return BaseApplication.getApplication().getSharedPreferences(name, Context.MODE_PRIVATE).getInt(name, defaultValue);
	}

	/**
	 * 保存float值
	 * 
	 * @param name
	 * @param value
	 */
	public static void saveFloat(String name, float value) {
		SharedPreferences.Editor editor = BaseApplication.getApplication().getSharedPreferences(name, Context.MODE_PRIVATE).edit();
		editor.putFloat(name, value);
		editor.commit();
	}

	/**
	 * 获取float值
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static float getFloat(String name, float defaultValue) {
		return BaseApplication.getApplication().getSharedPreferences(name, Context.MODE_PRIVATE).getFloat(name, defaultValue);
	}

	/**
	 * 保存字符串
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public static boolean saveString(String name, String value) {
		boolean flag = false;
		SharedPreferences.Editor editor = BaseApplication.getApplication().getSharedPreferences(name, Context.MODE_PRIVATE).edit();
		editor.putString(name, value);
		flag = editor.commit();
		return flag;
	}

	/**
	 * 获取字符串
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static String getString(String name, String defaultValue) {
		return BaseApplication.getApplication().getSharedPreferences(name, Context.MODE_PRIVATE).getString(name, defaultValue);
	}

	/**
	 * 清空所有用户偏好
	 * 
	 * @return
	 */
	public static boolean clear() {
		return BaseApplication.getApplication().getSharedPreferences(null, Context.MODE_PRIVATE).edit().clear().commit();
	}
}
