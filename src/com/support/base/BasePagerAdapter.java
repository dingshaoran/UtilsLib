package com.support.base;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public abstract class BasePagerAdapter extends PagerAdapter {

	protected Context mContext;
	protected LayoutInflater mInflater;
	protected List<? extends Object> mDatas;// 数据集合

	public BasePagerAdapter(Activity context, List<? extends Object> datas) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		if (datas != null) {
			mDatas = datas;
		}
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
		View view = getView(container, position);
		container.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		return view;
	}

	/** 子类必须实现的处理getView的方法 不存在复用的机制 **/
	public abstract View getView(ViewGroup container, int position);

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

}
