package com.support.anim;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.support.anim.RotateAnimation.Mode;

public class TweenAnimation {
	/**
	 * 透明渐变动画
	 * 
	 * @param from开始时候透明度
	 *            ,1不透明，0透明，中间值半透明
	 * @param to结束的透明度
	 * @param dur执行时常
	 * @param delay延迟开启时间
	 * @param i加速器
	 * @param fillBefore执行完动画回到刚开始执行动画的状态
	 *            ,否则停留在动画最后的转台
	 * @param repeatcount执行之后在重复的次数
	 *            ,如果值为1.总共执行2次
	 * @param repeatmode当repeatcount次数不为0时有效1重新开始
	 *            ,2反向开始。
	 * @param listener动画的监听器
	 * @return
	 */
	public static Animation alpha(float from, float to, long dur, long delay, Interpolator i, boolean fillBefore, int repeatcount, int repeatmode, AnimationListener listener) {
		Animation aa = new AlphaAnimation(from, to);
		aa.setDuration(dur);
		if (fillBefore) {
			aa.setFillBefore(true);
		} else {
			aa.setFillAfter(true);
		}
		aa.setAnimationListener(listener);
		aa.setInterpolator(i);
		aa.setRepeatCount(repeatcount);
		aa.setRepeatMode(repeatmode);
		aa.setStartOffset(delay);
		return aa;
	}

	/**
	 * 
	 * @param fromXType开始的值相对谁取
	 * @param fromXValue开始的值
	 * @param toXType结束的值相对谁取
	 * @param toXValue结束的值
	 * @param fromYType同上
	 * @param fromYValue同上
	 * @param toYType同上
	 * @param toYValue同上
	 * @param dur执行时常
	 * @param delay延迟开启时间
	 * @param i加速器
	 * @param fillBefore执行完动画回到刚开始执行动画的状态
	 *            ,否则停留在动画最后的转台
	 * @param repeatcount执行之后在重复的次数
	 *            ,如果值为1.总共执行2次
	 * @param repeatmode当repeatcount次数不为0时有效1重新开始
	 *            ,2反向开始。
	 * @param listener动画的监听器
	 * @return
	 */
	public static Animation translate(int fromXType, float fromXValue, int toXType, float toXValue, int fromYType, float fromYValue, int toYType, float toYValue, long dur, long delay, Interpolator i, boolean fillBefore, int repeatcount, int repeatmode, AnimationListener listener) {
		Animation aa = new TranslateAnimation(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue);
		aa.setDuration(dur);
		if (fillBefore) {
			aa.setFillBefore(true);
		} else {
			aa.setFillAfter(true);
		}
		aa.setAnimationListener(listener);
		aa.setInterpolator(i);
		aa.setRepeatCount(repeatcount);
		aa.setRepeatMode(repeatmode);
		aa.setStartOffset(delay);
		return aa;
	}

	/**
	 * 
	 * @param fromX开始的在屏幕上的绝对位置
	 * @param toX最终在屏幕上的绝对位置
	 * @param fromY开始的在屏幕上的绝对位置
	 * @param toY最终在屏幕上的绝对位置
	 * @param RelationTypeX中心相对谁取值
	 * @param centerX中心点值
	 * @param RelationTypeY中心相对谁取值
	 * @param centerY中心点值
	 * @param dur执行时常
	 * @param delay延迟开启时间
	 * @param i加速器
	 * @param fillBefore执行完动画回到刚开始执行动画的状态
	 *            ,否则停留在动画最后的转台
	 * @param repeatcount执行之后在重复的次数
	 *            ,如果值为1.总共执行2次
	 * @param repeatmode当repeatcount次数不为0时有效1重新开始
	 *            ,2反向开始。
	 * @param listener动画的监听器
	 * @return
	 */
	public static Animation scale(float fromX, float toX, float fromY, float toY, int RelationTypeX, float centerX, int RelationTypeY, float centerY, long dur, long delay, Interpolator i, boolean fillBefore, int repeatcount, int repeatmode, AnimationListener listener) {
		Animation aa = new ScaleAnimation(fromX, toX, fromY, toY, RelationTypeX, centerX, RelationTypeY, centerY);
		aa.setDuration(dur);
		if (fillBefore) {
			aa.setFillBefore(true);
		} else {
			aa.setFillAfter(true);
		}
		aa.setAnimationListener(listener);
		aa.setInterpolator(i);
		aa.setRepeatCount(repeatcount);
		aa.setRepeatMode(repeatmode);
		aa.setStartOffset(delay);
		return aa;
	}

	/**
	 * 
	 * @param from开始的角度0
	 *            ~360
	 * @param to结束的角度0
	 *            ~360
	 * @param RelationTypeX中心相对谁取值
	 * @param centerX中心点值
	 * @param RelationTypeY中心相对谁取值
	 * @param centerY中心点值
	 * @param mode以哪个轴转动X
	 *            、Y、Z
	 * @param dur执行时常
	 * @param delay延迟开启时间
	 * @param i加速器
	 * @param fillBefore执行完动画回到刚开始执行动画的状态
	 *            ,否则停留在动画最后的转台
	 * @param repeatcount执行之后在重复的次数
	 *            ,如果值为1.总共执行2次
	 * @param repeatmode当repeatcount次数不为0时有效1重新开始
	 *            ,2反向开始。
	 * @param listener动画的监听器
	 * @return
	 */
	public static Animation rotate(float from, float to, int RelationTypeX, float centerX, int RelationTypeY, float centerY, Mode mode, long dur, long delay, Interpolator i, boolean fillBefore, int repeatcount, int repeatmode, AnimationListener listener) {
		RotateAnimation aa = new RotateAnimation(from, to, RelationTypeX, centerX, RelationTypeY, centerY, mode);
		aa.setDuration(dur);
		if (fillBefore) {
			aa.setFillBefore(true);
		} else {
			aa.setFillAfter(true);
		}
		aa.setAnimationListener(listener);
		aa.setInterpolator(i);
		aa.setRepeatCount(repeatcount);
		aa.setRepeatMode(repeatmode);
		aa.setStartOffset(delay);
		return aa;
	}

	/**
	 * 
	 * @param relationType相对谁取值可以为父view
	 *            、自身、绝对位置
	 * @param fromXValue开始的x轴值
	 * @param toXValue结束的x轴值
	 * @param fromYValue
	 * @param toYValue
	 * @param dur执行的时长
	 * @param delay延迟开始的ms值
	 * @param i插入器类型
	 * @param fillBefore填充开始动画的状态
	 *            ，否则填充结束动画的状态
	 * @param repeatcount重复次数
	 *            ，值为1总共执行2次
	 * @param repeatmode重复模式
	 * @return
	 */
	public static Animation translate(int relationType, float fromXValue, float toXValue, float fromYValue, float toYValue, long dur, long delay, Interpolator i, boolean fillBefore, int repeatcount, int repeatmode) {
		return translate(relationType, fromXValue, relationType, toXValue, relationType, fromYValue, relationType, toYValue, dur, delay, i, fillBefore, repeatcount, repeatmode, null);
	}

	/**
	 * 停留在动画结束的状态，只执行1次动画
	 * 
	 * @param from开始时候透明度
	 *            ,1不透明，0透明，中间值半透明
	 * @param to结束的透明度
	 * @param dur执行的时长
	 * @param delay延迟开始的ms值
	 * @return
	 */
	public static Animation alpha(float from, float to, long dur, long delay) {
		return alpha(from, to, dur, delay, new LinearInterpolator(), false, 0, 2, null);
	}

	/**
	 * 开始和结束带有反弹效果的位移动画 停留在动画结束的状态，只执行1次动画
	 * 
	 * @param relationType相对谁取值可以为父view
	 *            、自身、绝对位置
	 * @param fromXValue开始的值
	 *            ，如果相对于自身取值1代表自身的宽高
	 * @param toXValue结束的值
	 * @param fromYValue
	 * @param toYValue
	 * @param dur执行的时长
	 * @param delay延迟开始的ms值
	 * @return
	 */
	public static Animation translate(int relationType, float fromXValue, float toXValue, float fromYValue, float toYValue, long dur, long delay) {
		return translate(relationType, fromXValue, relationType, toXValue, relationType, fromYValue, relationType, toYValue, dur, delay, new AnticipateOvershootInterpolator(), false, 0, 2, null);
	}

	/**
	 * 开始和结束带有反弹效果的尺寸动画
	 * 
	 * @param fromX开始相对于自身大小
	 *            ，值为1是自身的宽高
	 * @param toX最终相对于自身大小
	 * @param fromY开始相对于自身大小
	 * @param toY最终相对于自身大小
	 * @param centerX中心点值
	 * @param centerY中心点值
	 * @param dur执行时常
	 * @param delay延迟开启时间
	 * @return
	 */
	public static Animation scale(float fromX, float toX, float fromY, float toY, float centerX, float centerY, long dur, long delay) {
		return scale(fromX, toX, fromY, toY, 1, centerX, 1, centerY, dur, delay, new AnticipateOvershootInterpolator(), false, 0, 2, null);
	}

	/**
	 * 
	 * @param from开始的角度0
	 *            ~360
	 * @param to结束的角度0
	 *            ~360
	 * @param centerX中心点的位置
	 *            ，1为自身大小。相对于中心点旋转值为0.5
	 * @param centerY
	 * @param mode以哪个轴转动X
	 *            、Y、Z
	 * @param dur
	 * @param delay
	 * @return
	 */
	public static Animation rotate(float from, float to, float centerX, float centerY, Mode mode, long dur, long delay) {
		return rotate(from, to, 1, centerX, 1, centerY, mode, dur, delay, new LinearInterpolator(), false, -1, 1, null);
	}

	/**
	 * 
	 * @param from开始时候透明度
	 *            ,1不透明，0透明，中间值半透明
	 * @param to结束的透明度
	 * @param dur动画时长
	 * @return
	 */
	public static Animation alpha(float from, float to, long dur) {
		return alpha(from, to, dur, 0);
	}

	/**
	 * 
	 * @param relationType相对谁取值可以为父view
	 *            、自身、绝对位置
	 * @param fromXValue开始的值
	 *            ，如果相对于自身取值1代表自身的宽高
	 * @param toXValue结束的值
	 * @param fromYValue
	 * @param toYValue
	 * @param dur执行的时长
	 * @return
	 */
	public static Animation translate(int relationType, float fromXValue, float toXValue, float fromYValue, float toYValue, long dur) {
		return translate(relationType, fromXValue, toXValue, fromYValue, toYValue, dur, 0);
	}

	/**
	 * 以自身中心点为中心的尺寸动画
	 * 
	 * @param fromX开始相对于自身大小
	 *            ，值为1是自身的宽高
	 * @param toX最终相对于自身大小
	 * @param fromY开始相对于自身大小
	 * @param toY最终相对于自身大小
	 * @param dur执行时常
	 * @return
	 */
	public static Animation scale(float fromX, float toX, float fromY, float toY, long dur) {
		return scale(fromX, toX, fromY, toY, 0.5f, 0.5f, dur, 0);
	}

	/**
	 * 以自身中心点为中心的旋转动画，指定时间内旋转到指定的角度
	 * 
	 * @param from开始的角度0
	 *            ~360
	 * @param to结束的角度0
	 *            ~360
	 * @param mode以哪个轴转动X
	 *            、Y、Z
	 * @param dur执行的时长
	 * @return
	 */
	public static Animation rotate(float from, float to, Mode mode, long dur) {
		return rotate(from, to, 1, 0.5f, 1, 0.5f, mode, dur, 0, new LinearInterpolator(), false, 0, 1, null);
	}

	/**
	 * 1秒钟从全透明到不透明
	 * 
	 * @return
	 */
	public static Animation alpha() {
		return alpha(0, 1, 1000);
	}

	/**
	 * 在1秒钟内竖向移动指定位移
	 * 
	 * @param fromYValue开始的位置
	 *            ，0代表布局中的位置，x代表移动到布局位置x倍自身高度远的位置
	 * @param toYValue结束的位置
	 * @return
	 */
	public static Animation translateY(float fromYValue, float toYValue) {
		return translate(1, 0, 0, fromYValue, toYValue, 1000, 0);
	}

	/**
	 * 在1秒钟内横向移动指定位移
	 * 
	 * @param fromXValue开始的位置
	 *            ，0代表布局中的位置，x代表移动到布局位置x倍自身宽度远的位置
	 * @param toXValue结束的位置
	 * @return
	 */
	public static Animation translateX(float fromXValue, float toXValue) {
		return translate(1, fromXValue, toXValue, 0, 0, 1000, 0);
	}

	/**
	 * 以自身中心为中心点，1秒钟内从自身60%的大小放大到100%
	 * 
	 * @return
	 */
	public static Animation scale() {
		return scale(0.6f, 1, 0.6f, 1, 1000);
	}

	/**
	 * 围绕自身中心不断旋转动画，1秒钟每圈
	 * 
	 * @param mode围绕哪个轴转动X
	 *            、Y、Z
	 * @return
	 */
	public static Animation rotate(Mode mode) {
		return rotate(0, 360, 0.5f, 0.5f, mode, 1000, 0);
	}
}
