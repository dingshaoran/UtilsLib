package com.support.helper;

import com.support.exception.HttpException;
import com.xutils.http.ResponseInfo;
import com.xutils.http.callback.RequestCallBack;

@SuppressWarnings("rawtypes")
public class RequestCallBackImpl extends RequestCallBack {
	private final PostRequestCallBack prc;
	private short uniqueMark = -1;

	public RequestCallBackImpl(PostRequestCallBack prc) {
		super();
		this.prc = prc;
	}

	public void setUniqueMark(short uniqueMark) {
		this.uniqueMark = uniqueMark;
	}

	@Override
	public void onSuccess(ResponseInfo responseInfo) {
		if (prc != null) {
			prc.onSuccess(responseInfo, uniqueMark);
		}
	}

	@Override
	public void onFailure(HttpException error, String msg) {
		if (prc != null) {
			prc.onFailure(error, msg, uniqueMark);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (prc != null) {
			prc.onStartRequest(uniqueMark);
		}
	}

	@Override
	public void onCancelled() {
		super.onCancelled();
		if (prc != null) {
			prc.onCancelled(uniqueMark);
		}
	}

	@Override
	public void onLoading(long total, long current, boolean isUploading) {
		super.onLoading(total, current, isUploading);
		if (prc != null) {
			prc.onLoading(total, current, isUploading, uniqueMark);
		}
	}

}
