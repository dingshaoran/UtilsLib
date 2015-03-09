package com.support.utils;

import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import com.support.base.BaseApplication;

public class HandlerUtils {
	private HandlerUtils() {
	}

	private static HandlerUtils mHandlerList = null;

	public static HandlerUtils getInstance() {
		if (mHandlerList == null) {
			mHandlerList = new HandlerUtils();
		}
		return mHandlerList;
	}

	private final SparseArray<Handler> mMap = new SparseArray<Handler>();

	/**
	 * 根据键名添加handler到消息池
	 * 
	 * @param hashCode
	 * @param handler
	 */
	public static void addHandler(int hashCode, Handler handler) {
		if (getInstance().mMap.get(hashCode) == null) {
			getInstance().mMap.put(hashCode, handler);
		}
	}

	/**
	 * 根据类名将对应的handler移除消息池
	 * 
	 * @param hashCode
	 */
	public static synchronized void removeHandler(final int hashCode) {
		getInstance().mMap.remove(hashCode);
		if (BaseApplication.getMainThreadId() == Thread.currentThread().getId()) {
			getInstance().mMap.remove(hashCode);
		} else {
			BaseApplication.getMainThreadHandler().post(new Runnable() {

				@Override
				public void run() {
					getInstance().mMap.remove(hashCode);
				}
			});
		}
	}

	/**
	 * 根据指定Handler发送消息
	 * 
	 * @param handler
	 * @param msgId
	 * @param msgObj
	 */
	public static void sendMessage(Handler handler, int msgId, Object msgObj) {
		Message message = handler.obtainMessage();
		message.what = msgId;
		message.obj = msgObj;
		handler.sendMessage(message);
	}

	/**
	 * 全局发送消息--（只要存活的Act有抓取指定的msg就会执行接收消息)
	 * 
	 * @param msgId
	 * @param msgObj
	 */
	public static void sendMessage(final int msgId, final Object msgObj) {
		final SparseArray<Handler> haM = getInstance().mMap;
		if (BaseApplication.getMainThreadId() == Thread.currentThread().getId()) {
			for (int i = 0; i < haM.size(); i++) {
				sendMessage(haM.get(haM.keyAt(i)), msgId, msgObj);
			}
		} else {
			BaseApplication.getMainThreadHandler().post(new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < haM.size(); i++) {
						sendMessage(haM.get(haM.keyAt(i)), msgId, msgObj);
					}
				}
			});
		}
	}

	/**
	 * 发送空消息
	 * 
	 * @param msgId
	 */
	public static void sendMessage(int msgId) {
		sendMessage(msgId, null);
	}

	/**
	 * 根据指定类名发送消息
	 * 
	 * @param hashCode
	 *            类名
	 * @param msgId
	 * @param msgObj
	 */
	public static void sendMessage(int hashCode, int msgId, Object msgObj) {
		Handler handler = getInstance().mMap.get(hashCode);
		if (handler != null) {
			sendMessage(handler, msgId, msgObj);
		}
	}

	/**
	 * 根据类名获取对应activity的Handler
	 * 
	 * @param class 类
	 * @return
	 * @method
	 */
	public static Handler getHandler(@SuppressWarnings("rawtypes") Class cls) {
		return getInstance().mMap.get(cls.hashCode());
	}

	/**
	 * 根据类名发送消息
	 * 
	 * @param cls
	 * @param msgId
	 * @param msgObj
	 */
	public static void sendMessage(@SuppressWarnings("rawtypes") Class cls, int msgId, Object msgObj) {
		sendMessage(cls.hashCode(), msgId, msgObj);
	}

	/**
	 * 根据类名发送空消息
	 * 
	 * @param cls
	 * @param msgId
	 */
	public static void sendMessage(@SuppressWarnings("rawtypes") Class cls, int msgId) {
		sendMessage(cls.hashCode(), msgId, null);
	}
}
