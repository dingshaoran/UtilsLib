package com.support.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetUtils {
	/**
	 * 获取当前网络状态
	 * 
	 * @return true 有网
	 */
	public static boolean getNetWorkStates(Context ctx) {
		ConnectivityManager manager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	/**
	 * 获取当前的网络状态
	 * 
	 * @return
	 */
	public static NetWorkState getConnectState(Context mContext) {
		ConnectivityManager manager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		manager.getActiveNetworkInfo();
		State wifiState = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		State mobileState = manager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).getState();
		if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED == mobileState) {
			return NetWorkState.MOBILE;
		} else if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED != mobileState) {
			return NetWorkState.NONE;
		} else if (wifiState != null && State.CONNECTED == wifiState) {
			return NetWorkState.WIFI;
		}
		return NetWorkState.NONE;
	}

	public enum NetWorkState {
		WIFI, MOBILE, NONE;
	}
}
