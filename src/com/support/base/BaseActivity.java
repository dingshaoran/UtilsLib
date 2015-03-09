package com.support.base;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.support.exception.HttpException;
import com.support.helper.PostRequestCallBack;
import com.support.helper.RequestCallBackImpl;
import com.support.utils.HandlerUtils;
import com.support.utils.HttpUtils;
import com.support.utils.LogUtils;
import com.support.utils.ToastUtils;
import com.support_libs.R;
import com.xutils.http.RequestParams;
import com.xutils.http.ResponseInfo;
import com.xutils.http.client.HttpRequest.HttpMethod;

public abstract class BaseActivity extends FragmentActivity implements PostRequestCallBack, OnClickListener {
	private String url;
	protected Handler mHandler;
	private int mHashCode;
	private boolean mEnableDoubleExit = false;// 是否双击退出
	private final long[] clicktimes = { 0, 0 };// 控制点击次数

	@SuppressLint("HandlerLeak")
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new BaseHanlder(this);
		mHashCode = hashCode();
		HandlerUtils.addHandler(mHashCode, mHandler);
		setContentView(initView());
		initOnClickListener();
		initNetWork();
	}

	/**
	 * 请求网络的方法
	 * 
	 * @param params通过addbodyparams添加请求参数
	 * @param uniqueMark每一个请求对应唯一标识
	 *            ，回调的时候根据标识做不通处理
	 */
	@SuppressWarnings("unchecked")
	protected final void requestHttpPost(RequestParams params, short uniqueMark) {
		HttpUtils hu = new HttpUtils();
		RequestCallBackImpl rcb = new RequestCallBackImpl(this);
		rcb.setUniqueMark(uniqueMark);
		hu.send(HttpMethod.POST, url, params, rcb);
	}

	protected final void setBackPressExit() {
		mEnableDoubleExit = true;
	}

	protected final void setUrl(String url) {
		this.url = url;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		HandlerUtils.removeHandler(mHashCode);
		mHandler.removeCallbacksAndMessages(null);
	}

	@Override
	public void onBackPressed() {
		if (!mEnableDoubleExit) {
			super.onBackPressed();
		} else {
			if (SystemClock.uptimeMillis() - clicktimes[0] < 2000) {
				// 当点击两次后要实现的功能
				BaseApplication.exitApp();
			} else {
				clicktimes[clicktimes.length - 1] = SystemClock.uptimeMillis();
				System.arraycopy(clicktimes, 1, clicktimes, 0, clicktimes.length - 1);
				ToastUtils.show(this, "退出请在此点击返回键");
			}
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.fade_out_from_right_slow);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_from_left_slow, R.anim.slide_out_from_right);
	}

	protected abstract View initView();

	protected void initOnClickListener() {
	}

	/**
	 * 初始化网络，在oncreate中调用在这个方法里，需要使用{@link #requestHttpPost}请求联网
	 */
	protected void initNetWork() {
	}

	protected void handleMessage(Message msg) {
	}

	@Override
	public void onStartRequest(short uniqueMark) {
	}

	@Override
	public void onLoading(long total, long current, boolean isUploading, short uniqueMark) {
	}

	@Override
	public void onSuccess(@SuppressWarnings("rawtypes") ResponseInfo responseInfo, short uniqueMark) {
	}

	@Override
	public void onFailure(HttpException error, String msg, short uniqueMark) {
		LogUtils.e(error);
	}

	@Override
	public void onCancelled(short uniqueMark) {
	}

	@Override
	public void onClick(View v) {
	}

	private final class BaseHanlder extends Handler {
		private final WeakReference<Context> mact;

		public BaseHanlder(Context context) {
			mact = new WeakReference<Context>(context);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Integer.MAX_VALUE) {
				BaseActivity.this.finish();
			}
			BaseActivity.this.handleMessage(msg);
		}
	}
}
