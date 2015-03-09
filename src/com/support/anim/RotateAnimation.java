package com.support.anim;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class RotateAnimation extends Animation {

	private Camera mCamera;
	private final float mCenterX;
	private final float mCenterY;
	private final Mode mMode;
	private float mFromDegrees;
	private float mToDegrees;
	private final int mPivotXType;
	private final int mPivotYType;
	private float mPivotX;
	private float mPivotY;

	public RotateAnimation(float centerX, float centerY, Mode mode) {
		mPivotXType = ABSOLUTE;
		mPivotYType = ABSOLUTE;
		mCenterX = centerX;
		mCenterY = centerY;
		mMode = mode;
	}

	public RotateAnimation(float from, float to, int relationTypeX, float centerX, int relationTypeY, float centerY, Mode mode) {
		mMode = mode;
		mFromDegrees = from;
		mToDegrees = to;
		mCenterX = centerX;
		mCenterY = centerY;
		mPivotXType = relationTypeX;
		mPivotYType = relationTypeY;
	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mPivotX = resolveSize(mPivotXType, mCenterX, width, parentWidth);
		mPivotY = resolveSize(mPivotYType, mCenterY, height, parentHeight);
		mCamera = new Camera();
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		float deg = mFromDegrees + ((mToDegrees - mFromDegrees) * interpolatedTime);
		Matrix matrix = t.getMatrix();
		mCamera.save();
		if (mMode == Mode.X)
			mCamera.rotateX(deg);
		if (mMode == Mode.Y)
			mCamera.rotateY(deg);
		if (mMode == Mode.Z)
			mCamera.rotateZ(deg);

		mCamera.getMatrix(matrix);
		mCamera.restore();
		matrix.preTranslate(-mPivotX, -mPivotY);
		matrix.postTranslate(mPivotX, mPivotY);

	}

	public enum Mode {
		X, Y, Z;
	}
}
