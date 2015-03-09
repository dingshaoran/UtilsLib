package com.support.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class ProcessUtil {
	/**
	 * 杀死进程
	 * 
	 * @param context用于获取ActivityManager
	 * @param processName进程名称
	 *            ，如果为空杀死全部进程
	 * @return 清理进程个数
	 */
	public static int killProcess(Context context, String processName) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (processName != null) {
			am.killBackgroundProcesses(processName);
			return 1;
		} else {
			List<RunningAppProcessInfo> runpros = am.getRunningAppProcesses();
			for (RunningAppProcessInfo runpro : runpros) {
				am.killBackgroundProcesses(runpro.processName);
			}
			return runpros.size();
		}
	}
}
