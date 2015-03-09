package com.support.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class KeyBoardUtils {
	public static void show(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
	}

	public static void showOnCreate(Activity context) {
		context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	public static void hide(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 显示键盘
	 * 
	 * @param ctx
	 * @param view
	 */
	public static void showKeyboard(Context ctx, View view) {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
	}

	/**
	 * 隐藏键盘
	 * 
	 * @param ctx
	 * @param view
	 */
	public static void hideKeyboard(Context ctx, View view) {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		imm.hideSoftInputFromWindow(view.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
