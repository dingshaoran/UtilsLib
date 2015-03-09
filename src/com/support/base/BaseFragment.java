package com.support.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.support.exception.HttpException;
import com.support.helper.PostRequestCallBack;
import com.support.helper.RequestCallBackImpl;
import com.support.utils.HandlerUtils;
import com.support.utils.HttpUtils;
import com.support.utils.LogUtils;
import com.xutils.http.RequestParams;
import com.xutils.http.ResponseInfo;
import com.xutils.http.client.HttpRequest.HttpMethod;

public abstract class BaseFragment extends Fragment implements PostRequestCallBack, OnClickListener {
	private Handler mHandler;

	protected int mHashCode;
	protected Intent mIntent;
	private String url;

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				BaseFragment.this.handleMessage(msg);
			}
		};
		mHashCode = hashCode();
		HandlerUtils.addHandler(mHashCode, mHandler);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View initView = initView();
		initOnClickListener();
		initNetWork();
		return initView;
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

	protected final void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void onDestroy() {
		HandlerUtils.removeHandler(mHashCode);
		super.onDestroy();
	}

	protected abstract View initView();

	protected void initOnClickListener() {
	}

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
	public void onClick(View view) {
	}

}
