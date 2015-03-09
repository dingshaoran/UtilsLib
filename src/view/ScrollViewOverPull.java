package view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * 反弹效果的ScrollView
 */
public class ScrollViewOverPull extends ScrollView {
	private static final int MSG_REST_POSITION = 0x01;
	private static final int MSG_LISTEN_SCROLL = 0x02;
	private onScrollChangedListener scrollViewListener;
	private onScrollOverListener scrollViewOverListener;
	/** The max scroll height. */
	private static final int MAX_SCROLL_HEIGHT = 400;
	/** Damping, the smaller the greater the resistance */
	private static final float SCROLL_RATIO = 0.4f;

	private View mChildRootView;

	private ReboundScrollListener mListener;

	private float mTouchY;
	private boolean mTouchStop = false;

	private int mScrollY = 0;
	private int mScrollDy = 0;

	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (MSG_REST_POSITION == msg.what) {
				if (mScrollY != 0 && mTouchStop) {
					mScrollY -= mScrollDy;

					if ((mScrollDy <= 0 && mScrollY > 0)
							|| (mScrollDy >= 0 && mScrollY < 0)) {
						mScrollY = 0;
					}

					mChildRootView.scrollTo(0, mScrollY);
					// continue scroll after 20ms
					sendEmptyMessageDelayed(MSG_REST_POSITION, 10);
				}
			}
			/** 监听 **/
			if (MSG_LISTEN_SCROLL == msg.what) {
				if (mListener != null) {
					mListener.onLinstener();
				}
			}
		}
	};
	private float offset;
	private boolean disallowIntercept;

	public ScrollViewOverPull(Context context) {
		super(context);

		init();
	}

	public ScrollViewOverPull(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public ScrollViewOverPull(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	public void fling(int velocityY) {
		super.fling(velocityY / 2);// velocityY/4可以减慢滑动速度
	}

	@SuppressLint("NewApi")
	private void init() {
		setOverScrollMode(OVER_SCROLL_NEVER);
	}

	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			// when finished inflating from layout xml, get the first child view
			mChildRootView = getChildAt(0);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		this.disallowIntercept = false;
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {// 复写父类方法，子view不占满整个屏幕也可以滑动，by_dsr
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {// 修改前在onInterceptTouchEvent方法中
			mTouchY = ev.getY();
		}
		if (null != mChildRootView && !disallowIntercept) {// 修改前在ontouchEvent方法中
			doTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
		this.disallowIntercept = disallowIntercept;
		super.requestDisallowInterceptTouchEvent(disallowIntercept);
	}

	private void doTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (scrollViewListener != null) {
				scrollViewListener.onScrollStart(ev);
			}
			if (scrollViewOverListener != null) {
				scrollViewOverListener.onScrollStart(ev);
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mScrollY = mChildRootView.getScrollY();
			if (mScrollY != 0) {
				mTouchStop = true;
				mScrollDy = (int) (mScrollY / 20.0f);
				mHandler.sendEmptyMessage(MSG_REST_POSITION);
			}
			int scrolly = mScrollY;
			if (-scrolly > getHeight() / 6) {
				mHandler.sendEmptyMessageDelayed(MSG_LISTEN_SCROLL, 180);
			}
			if (scrollViewListener != null) {
				scrollViewListener.onScrollEnd(ev);
			}
			if (scrollViewOverListener != null) {
				scrollViewOverListener.onScrollEnd(ev);
			}
			break;

		case MotionEvent.ACTION_MOVE:
			float nowY = ev.getY();
			int deltaY = (int) (mTouchY - nowY);
			mTouchY = nowY;
			if (isNeedMove(deltaY)) {
				offset = mChildRootView.getScrollY();
				if (offset < MAX_SCROLL_HEIGHT && offset > -MAX_SCROLL_HEIGHT) {
					offset = offset + deltaY * SCROLL_RATIO;
					mChildRootView.scrollTo(0, (int) offset);
					if (scrollViewOverListener != null) {
						scrollViewOverListener.onScrolling(this, deltaY * SCROLL_RATIO);
					}
					mTouchStop = false;
				}
			}

			break;

		default:
			break;
		}
	}

	private boolean isNeedMove(int deltaY) {
		int viewHeight = mChildRootView.getMeasuredHeight();
		int scrollHeight = getHeight();
		int offset = viewHeight - scrollHeight;
		int scrollY = getScrollY();
		return (scrollY == 0 && deltaY < 0) || (scrollY == offset && deltaY > 0);
	}

	/** 设置反弹滑动监听 **/
	public void setReboundScrollListener(ReboundScrollListener listener) {
		this.mListener = listener;
	}

	/** 反弹下滑时的监听 **/
	public interface ReboundScrollListener {
		void onLinstener();
	}

	public void setonScrollListener(onScrollChangedListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}

	public void setonScrollOverListener(onScrollOverListener Listener) {
		this.scrollViewOverListener = Listener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		if (scrollViewListener != null) {
			scrollViewListener.onScrolling(this, x, y, oldx, oldy);
		}
	}

	public interface onScrollChangedListener {
		public void onScrollStart(MotionEvent ev);

		public void onScrolling(ScrollViewOverPull scrollView, int x, int y, int oldx, int oldy);

		public void onScrollEnd(MotionEvent ev);

	}

	public interface onScrollOverListener {
		public void onScrollStart(MotionEvent ev);

		public void onScrolling(ScrollViewOverPull scrollView, float distance);

		public void onScrollEnd(MotionEvent ev);

	}
}