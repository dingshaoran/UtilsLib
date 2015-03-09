package com.support.utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.ClipboardManager;
import android.text.Layout;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.support.anim.ExpandAnimation;

/**
 * 通用的工具类
 * 
 * @author JackWu
 * 
 */
@SuppressWarnings("deprecation")
public class CommonUtil {
	private CommonUtil() {
	}

	// private static final double PI = Math.PI;
	private static final double EARTH_RADIUS = 6378137.0;

	/**
	 * 通过手机照相获取图片
	 * 
	 * @param activity
	 * @return 照相后图片的路径
	 */
	public static String takePicture(Activity activity, String fileTarget) {
		File dir = new File(fileTarget);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String path = fileTarget + UUID.randomUUID().toString() + ".jpg";
		File file = FileUtils.createNewFile(path);
		if (file != null) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivityForResult(intent, 1);
		return path;
	}

	/**
	 * 安装apk
	 * 
	 * @param file_
	 *            *.apk文件
	 */
	public static void installApk(File file, Context ctx) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		ctx.startActivity(intent);
	}

	/**
	 * textview的maxline显示不全的展开时显示一个展开的动画
	 * 
	 * @param v这个textview
	 * @param dur动画时间
	 */
	public static void textViewExpand(final TextView v, int dur) {
		int height = v.getHeight();
		Layout layout = v.getLayout();
		int desired = layout.getLineTop(v.getLineCount());
		int padd = v.getCompoundPaddingTop() + v.getCompoundPaddingBottom();
		ExpandAnimation animation = new ExpandAnimation(v, height, desired + padd, 0, 0, dur);
		animation.setInterpolator(new OvershootInterpolator());
		v.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				v.setMaxLines(0xffffff);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
	}

	/**
	 * 将内容复制到剪贴板
	 * 
	 * @param context
	 * @param text
	 * @method
	 */
	public static void copyText2ClipBoard(Context context, CharSequence text) {
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(text);
	}

	/**
	 * MD5加密
	 * 
	 * @param context
	 * @return
	 * @method
	 */
	public static String MD5(String content) {
		if (TextUtils.isEmpty(content)) {
			LogUtils.e("加密内容为空");
			return "";
		}
		StringBuilder sb = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] result = digest.digest(content.getBytes());
			sb = new StringBuilder();
			for (byte b : result) {
				String hexString = Integer.toHexString(b & 0xFF);
				if (hexString.length() == 1) {
					sb.append("0" + hexString);// 0~F
				} else {
					sb.append(hexString);
				}
			}
		} catch (NoSuchAlgorithmException e) {
			LogUtils.e(e.toString());
			return content;
		}
		return sb.toString();
	}

	/** 计算两个经纬度之间的距离 **/
	public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
		double Lat1 = rad(latitude1);
		double Lat2 = rad(latitude2);
		double a = Lat1 - Lat2;
		double b = rad(longitude1) - rad(longitude2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

}
