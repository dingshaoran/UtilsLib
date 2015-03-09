package com.support.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.support_libs.R;

/**
 * 下载apk并安装
 * 
 * @author JackWu
 * 
 */
public class DownloadApk {

	private final Context mContext;
	private final NotificationManager mManager;
	private DownloadVersionListener mListener;// 回调监听
	private Notification mNotify;
	private final int mNotifyIcon;// 默认是下载图标
	private final String mNotifyText;
	private int _progress = 0;
	private static final int NOTIFICATION_ID = 90000;// 通知的id

	private String apkFilePath = "";// 默认的下载地址
	public boolean isdoing;

	private DownloadApk(Context ctx, int drawable, String notify) {
		this.mContext = ctx;
		mManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotifyIcon = drawable;
		mNotifyText = notify;

		mNotify = new Notification();
		mNotify.icon = mNotifyIcon;
		mNotify.tickerText = mNotifyText;
		mNotify.when = System.currentTimeMillis();
		RemoteViews remoteView = new RemoteViews(ctx.getApplicationContext()
				.getPackageName(), R.layout.downloadapk_remoteview);
		mNotify.contentView = remoteView;

		mNotify.contentView.setProgressBar(R.id.zk_Pb, 100, 0, true);
		mNotify.contentView.setTextViewText(R.id.zk_tvProgress_Tv, "进度"
				+ _progress + "%");
		mNotify.contentView.setImageViewResource(R.id.zk_image_Iv, mNotifyIcon);
		mNotify.contentView.setTextViewText(R.id.zk_status_Tv, mNotifyText);
		mNotify.contentIntent = PendingIntent.getActivity(ctx, 0, new Intent(),
				Notification.FLAG_NO_CLEAR);

		// 默认的下载地址
		apkFilePath = Environment.getExternalStorageDirectory()
				+ "/.zkDownLoad/";

	}

	/**
	 * 重置apk存储在sd卡下的位置
	 * 
	 * @param filePath
	 */
	public void setApkFilePath(String filePath) {
		apkFilePath = filePath;

	}

	public void setListener(DownloadVersionListener listener) {
		this.mListener = listener;
	}

	/**
	 * 设置自定义的通知
	 * 
	 * @param notifyNotification
	 */
	public void setCustomNotifycation(Notification notifyNotification) {
		this.mNotify = notifyNotification;
	}

	public static String getFileName(String path) {
		int start = path.lastIndexOf("/") + 1;
		return path.substring(start);
	}

	@SuppressLint("HandlerLeak")
	public void downLoad(final String apkUrl) {
		File file = new File(apkFilePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		String pt = file.getAbsolutePath() + File.separator
				+ getFileName(apkUrl);
		final File newapkfile = new File(pt);
		mManager.notify(NOTIFICATION_ID, mNotify);
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					isdoing = true;
					URL url = new URL(apkUrl);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(5000);
					conn.connect();
					int max = conn.getContentLength();
					InputStream is = conn.getInputStream();
					@SuppressWarnings("resource")
					FileOutputStream fos = new FileOutputStream(newapkfile);
					byte[] buffer = new byte[1024 * 1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						if (!NetUtils.getNetWorkStates(mContext)) {
							downloadFail(newapkfile);
							return;
						}
						String pro = new DecimalFormat("00").format(
								((_progress / (double) max) * 100)).toString();
						fos.write(buffer, 0, len);
						_progress += len;

						Message message = mHandler.obtainMessage();
						message.what = 0x110;
						message.arg1 = Integer.parseInt(pro);
						message.sendToTarget();
						Thread.sleep(700);
					}
					mManager.cancel(NOTIFICATION_ID);
					_progress = 0;
					isdoing = false;
					Message msg = mHandler.obtainMessage();
					msg.what = 0x112;
					msg.obj = newapkfile;
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					isdoing = false;
					_progress = 0;
					mManager.cancel(NOTIFICATION_ID);
					newapkfile.delete();
					Message msg = mHandler.obtainMessage();
					msg.what = 0x111;
					mHandler.sendMessage(msg);
					e.printStackTrace();
				}
			}

			@SuppressWarnings("deprecation")
			private void downloadFail(final File newapkfile) {
				isdoing = false;
				_progress = 0;
				mManager.cancel(NOTIFICATION_ID);
				newapkfile.delete();
				Thread.currentThread().stop();
				Message msg = mHandler.obtainMessage();
				msg.what = 0x111;
				mHandler.sendMessage(msg);
			};
		};
		t.start();
	}

	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0X110:
				mNotify.contentView.setProgressBar(R.id.zk_Pb, 100, msg.arg1,
						false);
				mNotify.contentView.setTextViewText(R.id.zk_tvProgress_Tv, "进度"
						+ msg.arg1 + "%");
				mManager.notify(NOTIFICATION_ID, mNotify);
				break;
			case 0X111:
				Toast.makeText(mContext, "下载失败,请检查网络", Toast.LENGTH_SHORT).show();
				break;
			case 0X112:// 下载成功
				File file = (File) msg.obj;
				if (mListener != null)
					mListener.onSuccess(file);
				break;
			default:
				break;
			}
		};
	};

	public interface DownloadVersionListener {
		public void onSuccess(File file);
	}
}
