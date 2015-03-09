package com.support.base;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.support.exception.HttpException;
import com.support.helper.PostRequestCallBack;
import com.support.helper.RequestCallBackImpl;
import com.support.utils.HttpUtils;
import com.support.utils.LogUtils;
import com.xutils.http.RequestParams;
import com.xutils.http.ResponseInfo;
import com.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 适配器基类
 * 
 * @author JackWu
 * 
 */
public abstract class BaseAdapterSimple extends BaseAdapter implements PostRequestCallBack {

	protected Context mContext;
	protected List<? extends Object> mDatas;// 数据集合
	private String url;

	public BaseAdapterSimple(Activity context, List<? extends Object> mDatas) {
		mContext = context;
		if (mDatas != null) {
			this.mDatas = mDatas;
		}
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

	public List<? extends Object> getDatas() {
		return mDatas;
	}

	/** 获取convertView **/
	protected View getConvertView(View convertView, int resid) {
		if (null == convertView) {
			convertView = View.inflate(mContext, resid, null);
		}
		return convertView;
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T getHolderCacheView(View convertView, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			convertView.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = convertView.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}

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
}
