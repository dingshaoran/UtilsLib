package com.support.anim;

import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ObjectAnimator;

/**
 * 
 * @author JackWu
 * @version
 * @date 2014-10-20
 * @qq 651319154
 */
public class PropertyAnimation {
	/**
	 * @param 属性动画的封装
	 *            ，指定需要的enum。
	 * @param alpha透明度
	 *            ，1不透明0，全透明。
	 * @param translationX
	 *            /Y位移from、to相对于自身的位置0当前位置，to指定的移动距离
	 * @param X
	 *            /Y位移from、to屏幕上的绝对位置
	 * @param scaleX
	 *            /Y尺寸from、to相对于自身的大小，1和自身一样大
	 * @param Rotation
	 *            /X/Y 旋转动画x、y、z轴0~360
	 * @param PivotX
	 *            /Y相对的中心点
	 */

	public static ObjectAnimator anim(View target, PropertyFloat property, float from, float to, long dur, long delay, Interpolator i, int repeatcount, int repeatmode) {
		String prs = null;
		switch (property) {
		case Alpha:
			prs = "alpha";
			break;
		case TranslationX:
			prs = "translationX";
			break;
		case TranslationY:
			prs = "translationY";
			break;
		case Y:
			prs = "y";
			break;
		case X:
			prs = "x";
			break;
		case ScaleY:
			prs = "scaleY";
			break;
		case ScaleX:
			prs = "scaleX";
			break;
		case Rotation:
			prs = "rotation";
			break;
		case RotationX:
			prs = "rotationX";
			break;
		case RotationY:
			prs = "rotationY";
			break;
		case PivotX:
			prs = "pivotX";
			break;
		case PivotY:
			prs = "pivotY";
			break;
		default:
			break;

		}
		ObjectAnimator aa = ObjectAnimator.ofFloat(target, prs, from, to);
		aa.setDuration(dur);
		aa.setInterpolator(i);
		aa.setRepeatCount(repeatcount);
		aa.setRepeatMode(repeatmode);
		aa.setStartDelay(delay);
		aa.start();
		return aa;
	}

	/**
	 * @param 属性动画的封装
	 *            ，指定需要的enum。
	 * @param alpha透明度
	 *            ，1不透明0，全透明。
	 * @param translationX
	 *            /Y位移from、to相对于自身的位置0当前位置，to指定的移动距离
	 * @param X
	 *            /Y位移from、to屏幕上的绝对位置
	 * @param scaleX
	 *            /Y尺寸from、to相对于自身的大小，1和自身一样大
	 * @param Rotation
	 *            /X/Y 旋转动画x、y、z轴0~360
	 * @param PivotX
	 *            /Y相对的中心点
	 */
	public static ObjectAnimator anim(View target, PropertyFloat property, float from, float to, long dur) {
		return anim(target, property, from, to, dur, 0, new LinearInterpolator(), 0, 1);
	}

	/**
	 * 
	 * @param from
	 *            、to相对于屏幕的绝对位置
	 * @param BackgroundColor取值范围0xff000000
	 *            ~0xffffffff前两位为透明度ff为不透明
	 * @return
	 */
	public static ObjectAnimator anim(View target, PropertyInt property, int from, int to, long dur, long delay, Interpolator i, int repeatcount, int repeatmode) {
		String prs = null;
		switch (property) {
		case Top:
			prs = "top";
			break;
		case Bottom:
			prs = "bottom";
			break;
		case Right:
			prs = "right";
			break;
		case Left:
			prs = "left";
			break;
		case BackgroundColor:
			prs = "backgroundColor";
			break;
		default:
			break;

		}
		ObjectAnimator aa = ObjectAnimator.ofInt(target, prs, from, to);
		aa.setDuration(dur);
		aa.setInterpolator(i);
		aa.setRepeatCount(repeatcount);
		aa.setRepeatMode(repeatmode);
		aa.setStartDelay(delay);
		aa.start();
		return aa;
	}

	public static ObjectAnimator anim(View target, PropertyInt property, int from, int to, long dur) {
		return anim(target, property, from, to, dur, 0, new LinearInterpolator(), 0, 1);
	}

	public enum PropertyFloat {
		Alpha, TranslationX, TranslationY, Y, X, ScaleX, ScaleY, Rotation, RotationX, RotationY, PivotX, PivotY
	}

	public enum PropertyInt {
		Top, Bottom, Right, Left, BackgroundColor
	}
}
