package view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.support_libs.R;

@SuppressLint("InlinedApi")
public class XListView extends ListView implements OnScrollListener {

	private final static int SCROLL_BACK_HEADER = 0;
	private final static int SCROLL_BACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400;

	// when pull up >= 50px
	private final static int PULL_LOAD_MORE_DELTA = 50;

	// support iOS like pull
	private final static float OFFSET_RADIO = 1.8f;

	private float mLastY = -1;

	// used for scroll back
	private Scroller mScroller;
	// user's scroll listener
	private OnScrollListener mScrollListener;
	// for mScroller, scroll back from header or footer.
	private int mScrollBack;

	// the interface to trigger refresh and load more.
	private IXListViewListener mListener;

	private XHeaderView mHeader;
	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderContent;
	private TextView mHeaderTime;
	private int mHeaderHeight;

	private LinearLayout mFooterLayout;
	private XFooterView mFooterView;
	private boolean mIsFooterReady = false;

	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false;

	private boolean mEnablePullLoad = true;
	private boolean mEnableAutoLoad = false;
	private boolean mPullLoading = false;

	// total list items, used to detect is at the bottom of ListView
	private int mTotalItemCount;
	private GestureDetector mGestureDetector;

	public XListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	@Override
	public boolean isInEditMode() {
		return true;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void initWithContext(Context context) {
		mGestureDetector = new GestureDetector(context, mTouchGesture);
		mScroller = new Scroller(context, new DecelerateInterpolator());
		super.setOnScrollListener(this);
		if (android.os.Build.VERSION.SDK_INT > 8) {// 判断sdk版本，使用不用的api消除fedingedge
			setOverScrollMode(View.OVER_SCROLL_NEVER);
		} else {
			setFadingEdgeLength(0);
		}
		// init header view
		mHeader = new XHeaderView(context);
		mHeaderContent = (RelativeLayout) mHeader
				.findViewById(R.id.header_content);
		mHeaderTime = (TextView) mHeader.findViewById(R.id.header_hint_time);
		addHeaderView(mHeader);

		// init footer view
		mFooterView = new XFooterView(context);
		mFooterLayout = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		mFooterLayout.addView(mFooterView, params);

		// init header height
		ViewTreeObserver observer = mHeader.getViewTreeObserver();
		if (null != observer) {
			observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@SuppressWarnings("deprecation")
				@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
				@Override
				public void onGlobalLayout() {
					mHeaderHeight = mHeaderContent.getHeight();
					ViewTreeObserver observer = getViewTreeObserver();

					if (null != observer) {
						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
							observer.removeGlobalOnLayoutListener(this);
						} else {
							observer.removeOnGlobalLayoutListener(this);
						}
					}
				}
			});
		}
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// make sure XFooterView is the last footer view, and only add once.
		if (!mIsFooterReady) {
			mIsFooterReady = true;
			addFooterView(mFooterLayout);
		}

		super.setAdapter(adapter);
	}

	/**
	 * Enable or disable pull down refresh feature.
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;

		// disable, hide the content
		mHeaderContent.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
	}

	/**
	 * Enable or disable pull up load more feature.
	 * 
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;

		if (!mEnablePullLoad) {
			mFooterView.setBottomMargin(0);
			mFooterView.hide();
			mFooterView.setPadding(0, 0, 0, mFooterView.getHeight() * (-1));
			mFooterView.setOnClickListener(null);

		} else {
			mPullLoading = false;
			mFooterView.setPadding(0, 0, 0, 0);
			mFooterView.show();
			mFooterView.setState(XFooterView.STATE_NORMAL);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	/**
	 * Enable or disable auto load more feature when scroll to bottom.
	 * 
	 * @param enable
	 */
	public void setAutoLoadEnable(boolean enable) {
		mEnableAutoLoad = enable;
	}

	/**
	 * Stop refresh, reset header view.
	 */
	public void stopRefresh() {
		if (mPullRefreshing) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}

	/**
	 * Stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		if (mPullLoading) {
			mPullLoading = false;
			mFooterView.setState(XFooterView.STATE_NORMAL);
		}
	}

	/**
	 * Set last refresh time
	 * 
	 * @param time
	 */
	public void setRefreshTime(String time) {
		mHeaderTime.setText(time);
	}

	/**
	 * Set listener.
	 * 
	 * @param listener
	 */
	public void setXListViewListener(IXListViewListener listener) {
		mListener = listener;
	}

	/**
	 * Auto call back refresh.
	 */
	public void autoRefresh() {
		mHeader.setVisibleHeight(mHeaderHeight);

		if (mEnablePullRefresh && !mPullRefreshing) {
			// update the arrow image not refreshing
			if (mHeader.getVisibleHeight() > mHeaderHeight) {
				mHeader.setState(XHeaderView.STATE_READY);
			} else {
				mHeader.setState(XHeaderView.STATE_NORMAL);
			}
		}

		mPullRefreshing = true;
		mHeader.setState(XHeaderView.STATE_REFRESHING);
		refresh();
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener listener = (OnXScrollListener) mScrollListener;
			listener.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
		mHeader.setVisibleHeight((int) delta + mHeader.getVisibleHeight());

		if (mEnablePullRefresh && !mPullRefreshing) {
			// update the arrow image unrefreshing
			if (mHeader.getVisibleHeight() > mHeaderHeight) {
				mHeader.setState(XHeaderView.STATE_READY);
			} else {
				mHeader.setState(XHeaderView.STATE_NORMAL);
			}
		}

		// scroll to top each time
		setSelection(0);
	}

	private void resetHeaderHeight() {
		int height = mHeader.getVisibleHeight();
		if (height == 0)
			return;

		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && height <= mHeaderHeight)
			return;

		// default: scroll back to dismiss header.
		int finalHeight = 0;
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderHeight) {
			finalHeight = mHeaderHeight;
		}

		mScrollBack = SCROLL_BACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);

		// trigger computeScroll
		invalidate();
	}

	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;

		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) {
				// height enough to invoke load more.
				mFooterView.setState(XFooterView.STATE_READY);
			} else {
				mFooterView.setState(XFooterView.STATE_NORMAL);
			}
		}

		mFooterView.setBottomMargin(height);

		// scroll to bottom
		// setSelection(mTotalItemCount - 1);
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();

		if (bottomMargin > 0) {
			mScrollBack = SCROLL_BACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(XFooterView.STATE_LOADING);
		loadMore();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}
		mGestureDetector.onTouchEvent(ev);
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;

		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();

			if (getFirstVisiblePosition() == 0
					&& (mHeader.getVisibleHeight() > 0 || deltaY > 0)) {
				// the first item is showing, header has shown or pull down.
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();

			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				// last item, already pulled up or want to pull up.
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;

		default:

			// reset
			mLastY = -1;
			if (getFirstVisiblePosition() == 0) {
				// invoke refresh
				if (mEnablePullRefresh
						&& mHeader.getVisibleHeight() > mHeaderHeight) {
					mPullRefreshing = true;
					mHeader.setState(XHeaderView.STATE_REFRESHING);
					refresh();
				}

				resetHeaderHeight();

			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				// invoke load more.
				if (mEnablePullLoad
						&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
					startLoadMore();
				}
				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLL_BACK_HEADER) {
				mHeader.setVisibleHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}

			postInvalidate();
			invokeOnScrolling();
		}

		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}

		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if (mEnableAutoLoad && getLastVisiblePosition() == getCount() - 1) {
				startLoadMore();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// send to user's listener
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	private void refresh() {
		if (mEnablePullRefresh && null != mListener) {
			mListener.onRefresh();
		}
	}

	private void loadMore() {
		if (mEnablePullLoad && null != mListener) {
			mListener.onLoadMore();
		}
	}

	/**
	 * You can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * Implements this interface to get refresh/load more event.
	 * 
	 * @author markmjw
	 */
	public interface IXListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}

	private final SimpleOnGestureListener mTouchGesture = new SimpleOnGestureListener() {

		/** 快速滚动 */
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			smoothScrollBy(-(int) (velocityY / 5), 1700);//velocityY / 10控制fling的速度
			mLastY = -1;
			if (getFirstVisiblePosition() == 0) {
				// invoke refresh
				if (mEnablePullRefresh
						&& mHeader.getVisibleHeight() > mHeaderHeight) {
					mPullRefreshing = true;
					mHeader.setState(XHeaderView.STATE_REFRESHING);
					refresh();
				}

				resetHeaderHeight();

			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				// invoke load more.
				if (mEnablePullLoad
						&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
					startLoadMore();
				}
				resetFooterHeight();
			}
			return true;
		}

	};

	public class XFooterView extends LinearLayout {
		public final static int STATE_NORMAL = 0;
		public final static int STATE_READY = 1;
		public final static int STATE_LOADING = 2;

		private final int ROTATE_ANIM_DURATION = 180;

		private View mLayout;

		private View mProgressBar;

		private TextView mHintView;

		private Animation mRotateUpAnim;
		private Animation mRotateDownAnim;

		private int mState = STATE_NORMAL;

		public XFooterView(Context context) {
			super(context);
			initView(context);
		}

		public XFooterView(Context context, AttributeSet attrs) {
			super(context, attrs);
			initView(context);
		}

		@Override
		public boolean isInEditMode() {
			return true;
		}

		private void initView(Context context) {
			mLayout = LayoutInflater.from(context)
					.inflate(R.layout.vw_footer, null);
			mLayout.setLayoutParams(new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			addView(mLayout);

			mProgressBar = mLayout.findViewById(R.id.footer_progressbar);
			mHintView = (TextView) mLayout.findViewById(R.id.footer_hint_text);

			mRotateUpAnim = new RotateAnimation(0.0f, 180.0f,
					Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
					0.5f);
			mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
			mRotateUpAnim.setFillAfter(true);

			mRotateDownAnim = new RotateAnimation(180.0f, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
					0.5f);
			mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
			mRotateDownAnim.setFillAfter(true);
		}

		/**
		 * Set footer view state
		 * 
		 * @see #STATE_LOADING
		 * @see #STATE_NORMAL
		 * @see #STATE_READY
		 * 
		 * @param state
		 */
		public void setState(int state) {
			if (state == mState)
				return;

			if (state == STATE_LOADING) {
				mProgressBar.setVisibility(View.VISIBLE);
				mHintView.setVisibility(View.INVISIBLE);
			} else {
				mHintView.setVisibility(View.VISIBLE);
				// mHintImage.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.INVISIBLE);
			}

			switch (state) {
			case STATE_NORMAL:
				mHintView.setText("");
				break;

			case STATE_READY:
				if (mState != STATE_READY) {
					mHintView.setText("");
				}
				break;

			case STATE_LOADING:
				break;
			}

			mState = state;
		}

		/**
		 * Set footer view bottom margin.
		 * 
		 * @param margin
		 */
		public void setBottomMargin(int margin) {
			if (margin < 0)
				return;
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLayout
					.getLayoutParams();
			lp.bottomMargin = margin;
			mLayout.setLayoutParams(lp);
		}

		/**
		 * Get footer view bottom margin.
		 * 
		 * @return
		 */
		public int getBottomMargin() {
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLayout
					.getLayoutParams();
			return lp.bottomMargin;
		}

		/**
		 * normal status
		 */
		public void normal() {
			mHintView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
		}

		/**
		 * loading status
		 */
		public void loading() {
			mHintView.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		}

		/**
		 * hide footer when disable pull load more
		 */
		public void hide() {
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLayout
					.getLayoutParams();
			lp.height = 0;
			mLayout.setLayoutParams(lp);
		}

		/**
		 * show footer
		 */
		public void show() {
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLayout
					.getLayoutParams();
			lp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			mLayout.setLayoutParams(lp);
		}
	}

	public class XHeaderView extends LinearLayout {
		public final static int STATE_NORMAL = 0;
		public final static int STATE_READY = 1;
		public final static int STATE_REFRESHING = 2;

		private final int ROTATE_ANIM_DURATION = 180;

		private LinearLayout mContainer;

		private ImageView mArrowImageView;

		private ProgressBar mProgressBar;

		private TextView mHintTextView;

		private int mState = STATE_NORMAL;

		private Animation mRotateUpAnim;
		private Animation mRotateDownAnim;

		private boolean mIsFirst;

		public XHeaderView(Context context) {
			super(context);
			initView(context);
		}

		public XHeaderView(Context context, AttributeSet attrs) {
			super(context, attrs);
			initView(context);
		}

		@Override
		public boolean isInEditMode() {
			return true;
		}

		private void initView(Context context) {
			// Initial set header view height 0
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0);
			mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
					R.layout.vw_header, null);
			addView(mContainer, lp);
			setGravity(Gravity.BOTTOM);

			mArrowImageView = (ImageView) findViewById(R.id.header_arrow);
			mHintTextView = (TextView) findViewById(R.id.header_hint_text);
			mProgressBar = (ProgressBar) findViewById(R.id.header_progressbar);

			mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
					Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
					0.5f);
			mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
			mRotateUpAnim.setFillAfter(true);

			mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
					0.5f);
			mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
			mRotateDownAnim.setFillAfter(true);
		}

		public void setState(int state) {
			if (state == mState && mIsFirst) {
				mIsFirst = true;
				return;
			}

			if (state == STATE_REFRESHING) {
				// show progress
				mArrowImageView.clearAnimation();
				mArrowImageView.setVisibility(View.INVISIBLE);
				mProgressBar.setVisibility(View.VISIBLE);
			} else {
				// show arrow image
				mArrowImageView.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.INVISIBLE);
			}

			switch (state) {
			case STATE_NORMAL:
				if (mState == STATE_READY) {
					mArrowImageView.startAnimation(mRotateDownAnim);
				}

				if (mState == STATE_REFRESHING) {
					mArrowImageView.clearAnimation();
				}

				mHintTextView.setText("");
				break;

			case STATE_READY:
				if (mState != STATE_READY) {
					mArrowImageView.clearAnimation();
					mArrowImageView.startAnimation(mRotateUpAnim);
					mHintTextView.setText("");
				}
				break;

			case STATE_REFRESHING:
				mHintTextView.setText("");
				break;

			default:
				break;
			}

			mState = state;
		}

		/**
		 * Set the header view visible height.
		 * 
		 * @param height
		 */
		public void setVisibleHeight(int height) {
			if (height < 0)
				height = 0;
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer
					.getLayoutParams();
			lp.height = height;
			mContainer.setLayoutParams(lp);
		}

		/**
		 * Get the header view visible height.
		 * 
		 * @return
		 */
		public int getVisibleHeight() {
			return mContainer.getHeight();
		}
	}
}
