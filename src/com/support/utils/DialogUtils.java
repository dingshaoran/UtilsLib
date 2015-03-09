package com.support.utils;

import view.ProgressBarSlowly;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.support.anim.RotateAnimation;
import com.support_libs.R;

public class DialogUtils {
	public static Object[] showHorizontalProgressDialog(Context context) {
		Object[] all = new Object[3];
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		AlertDialog ad = builder.create();
		ad.show();
		View ccv = View.inflate(context, R.layout.dialog_progress_horizontal, null);
		ad.setContentView(ccv);
		ProgressBarSlowly pb = (ProgressBarSlowly) ccv.findViewById(R.id.dialog_progress);
		TextView msg = (TextView) ccv.findViewById(R.id.dialog_message_horizontal);
		ad.setCanceledOnTouchOutside(false);
		ad.setCancelable(true);
		all[0] = ad;
		all[1] = pb;
		all[2] = msg;
		return all;
	}

	/**
	 * 显示等待对话框，上面图片下面文字
	 * 
	 * @param context
	 * @param imageRes要显示的图片id
	 * @param message要显示的文字
	 * @return
	 */
	public static AlertDialog showProgressDialog(Context context, int imageRes, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		AlertDialog ad = builder.create();
		ad.show();
		View ccv = View.inflate(context, R.layout.dialog_progress_circle, null);
		ImageView icon = (ImageView) ccv.findViewById(R.id.dialog_progress);
		icon.setBackgroundResource(imageRes);
		ad.setContentView(ccv);
		RotateAnimation mAnimation = new RotateAnimation(icon.getLayoutParams().height / 2.0F, icon.getHeight() / 2.0F, RotateAnimation.Mode.Y);
		mAnimation.setDuration(1000);
		mAnimation.setInterpolator(new LinearInterpolator());
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.RESTART);
		icon.startAnimation(mAnimation);
		TextView msg = (TextView) ccv.findViewById(R.id.dialog_message);
		msg.setText(message);
		ad.setCanceledOnTouchOutside(false);
		ad.setCancelable(true);
		return ad;
	}

	/**
	 * 显示等待对话框，翻转图片
	 * 
	 * @param context
	 * @param imageRes图片资源id
	 * @return
	 */
	public static AlertDialog showProgressDialog(Context context, int imageRes) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		AlertDialog ad = builder.create();
		ad.setCanceledOnTouchOutside(false);
		ad.setCancelable(true);
		ad.show();
		ImageView icon = new ImageView(context);
		icon.setBackgroundResource(imageRes);
		ad.setContentView(icon);
		RotateAnimation mAnimation = new RotateAnimation(icon.getBackground().getIntrinsicWidth() / 2.0F, 0, RotateAnimation.Mode.Y);
		mAnimation.setDuration(1000);
		mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.RESTART);
		icon.startAnimation(mAnimation);

		return ad;
	}

	/**
	 * 显示一个选择对话框
	 * 
	 * @param context
	 * @param title要显示的标题
	 * @param content要显示的文本
	 * @param leftT左边按钮的文字
	 * @param rightT右边按钮的文字
	 * @param rightListener点击监听
	 * @param leftListener点击监听
	 * @return
	 */
	public static AlertDialog showAlertDialog(Context context, String title, String content, String leftT, OnClickListener leftListener, String rightT, OnClickListener rightListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		AlertDialog ad = builder.create();
		ad.setCanceledOnTouchOutside(false);
		ad.setCancelable(true);
		ad.show();
		View view = View.inflate(context, R.layout.dialog_alert, null);
		ad.setContentView(view);
		TextView titletv = (TextView) view.findViewById(R.id.dialog_title);
		TextView contenttv = (TextView) view.findViewById(R.id.dialog_content);
		titletv.setText(title);
		contenttv.setText(content);
		Button leftb = (Button) view.findViewById(R.id.dialog_left);
		Button rightb = (Button) view.findViewById(R.id.dialog_right);
		leftb.setText(leftT);
		rightb.setText(rightT);
		leftb.setOnClickListener(leftListener);
		rightb.setOnClickListener(rightListener);
		return ad;
	}

}