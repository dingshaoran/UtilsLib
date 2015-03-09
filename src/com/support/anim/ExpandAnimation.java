package com.support.anim;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ExpandAnimation extends Animation {
	private View mTargetView;
	private int mStartHeight;
	private int mEndHeight;
	private int mstartWidth;
	private int mendWidth;

	public ExpandAnimation(View view, int startHeight, int endHeight, int startWidth, int endWidth, int duration) {
		mTargetView = view;
		mStartHeight = startHeight;
		mEndHeight = endHeight;
		mstartWidth = startWidth;
		mendWidth = endWidth;
		setDuration(duration);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		LayoutParams layoutParams = mTargetView.getLayoutParams();
		if (layoutParams == null) {
			layoutParams = new LayoutParams(-2, -2);
		}
		int hdx = mEndHeight - mStartHeight;
		if (hdx != 0) {
			int newHeight = (int) (hdx * interpolatedTime + mStartHeight);
			layoutParams.height = newHeight;
		}
		int wdx = mendWidth - mstartWidth;
		if (wdx != 0) {
			int newWidth = (int) (wdx * interpolatedTime + mstartWidth);
			layoutParams.width = newWidth;
		}

		mTargetView.requestLayout();
	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
	}

	public View getmTargetView() {
		return mTargetView;
	}

	public void setmTargetView(View mTargetView) {
		this.mTargetView = mTargetView;
	}

	public int getmStartHeight() {
		return mStartHeight;
	}

	public void setmStartHeight(int mStartHeight) {
		this.mStartHeight = mStartHeight;
	}

	public int getmEndHeight() {
		return mEndHeight;
	}

	public void setmEndHeight(int mEndHeight) {
		this.mEndHeight = mEndHeight;
	}

	public int getMstartWidth() {
		return mstartWidth;
	}

	public void setMstartWidth(int mstartWidth) {
		this.mstartWidth = mstartWidth;
	}

	public int getMendWidth() {
		return mendWidth;
	}

	public void setMendWidth(int mendWidth) {
		this.mendWidth = mendWidth;
	}

};