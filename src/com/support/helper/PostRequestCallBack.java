package com.support.helper;

import com.support.exception.HttpException;
import com.xutils.http.ResponseInfo;

public interface PostRequestCallBack {
	public void onSuccess(@SuppressWarnings("rawtypes") ResponseInfo responseInfo, short uniqueMark);

	public void onFailure(HttpException error, String msg, short uniqueMark);

	public void onStartRequest(short uniqueMark);

	public void onCancelled(short uniqueMark);

	public void onLoading(long total, long current, boolean isUploading, short uniqueMark);
}
