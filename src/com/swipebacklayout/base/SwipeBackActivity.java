package com.swipebacklayout.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;

import com.support.utils.DpAndPxUtils;
import com.swipebacklayout.lib.SwipeBackLayout;
import com.swipebacklayout.lib.Utils;

public class SwipeBackActivity extends FragmentActivity implements
		SwipeBackActivityBase {
	private SwipeBackActivityHelper mHelper;
	private SwipeBackLayout swipeBackLayout;
	private boolean activityIsShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mHelper = new SwipeBackActivityHelper(this);
		mHelper.onActivityCreate();
		swipeBackLayout = getSwipeBackLayout();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(newBase);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate();
	}

	@Override
	protected void onResume() {
		super.onResume();
		activityIsShow = true;// 解决startactivity时可以再次点击的问题
	}

	@Override
	public void startActivity(Intent intent) {
		activityIsShow = false;// 解决startactivity时可以再次点击的问题
		super.startActivity(intent);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {// 解决startactivity时可以再次点击的问题
		if (!activityIsShow) {
			return false;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public View findViewById(int id) {
		View v = super.findViewById(id);
		if (v == null && mHelper != null)
			return mHelper.findViewById(id);
		return v;
	}

	@Override
	public SwipeBackLayout getSwipeBackLayout() {
		return mHelper.getSwipeBackLayout();
	}

	@Override
	public void setSwipeBackEnable(boolean enable) {
		swipeBackLayout.setEnableGesture(enable);
		swipeBackLayout.setEdgeSize(DpAndPxUtils.getScreenWidth(this) / 2);//可以在半屏的位置滑动
	}

	@Override
	public void scrollToFinishActivity() {
		Utils.convertActivityToTranslucent(this);
		getSwipeBackLayout().scrollToFinishActivity();
	}
}
