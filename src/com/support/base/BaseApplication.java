package com.support.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;

import com.support.utils.HandlerUtils;

/**
 * 
 * @author DSR
 * 
 */

public class BaseApplication extends Application {
	/** 全局Context，原理是因为Application类是应用最先运行的，所以在我们的代码调用时，该值已经被赋值过了 */
	private static BaseApplication mInstance;
	/** 主线程ID */
	private static int mMainThreadId = -1;
	/** 主线程ID */
	private static Thread mMainThread;
	/** 主线程Handler */
	private static Handler mMainThreadHandler;
	/** 主线程Looper */
	private static Looper mMainLooper;

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		// 严苛模式
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectCustomSlowCalls().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().penaltyFlashScreen().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().setClassInstanceLimit(this.getClass(), 100).penaltyLog().build());
		// 严苛模式

		mMainThreadId = android.os.Process.myTid();
		mMainThread = Thread.currentThread();
		mMainThreadHandler = new Handler();
		mMainLooper = getMainLooper();
		mInstance = this;
		super.onCreate();
	}

	public static BaseApplication getApplication() {
		return mInstance;
	}

	/** 获取主线程ID */
	public static int getMainThreadId() {
		return mMainThreadId;
	}

	/** 获取主线程 */
	public static Thread getMainThread() {
		return mMainThread;
	}

	/** 获取主线程的handler */
	public static Handler getMainThreadHandler() {
		return mMainThreadHandler;
	}

	/** 获取主线程的looper */
	public static Looper getMainThreadLooper() {
		return mMainLooper;
	}

	public static void exitApp() {
		HandlerUtils.sendMessage(Integer.MAX_VALUE);
	}
}
