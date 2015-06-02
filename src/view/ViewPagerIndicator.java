package view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.support_libs.R;

/**
 * 今日头条的viewpager指示器，随着滑动文字颜色也会跟着变
 * 
 * @author dsr
 *
 */
public class ViewPagerIndicator extends HorizontalScrollView {
	private final LayoutInflater mLayoutInflater;
	private final PageListener pageListener = new PageListener();
	private ViewPager pager;
	private final LinearLayout tabsContainer;
	private int tabCount;

	private int currentPosition = 0;
	private float currentPositionOffset = 0f;

	private final Rect indicatorRect;

	private final LinearLayout.LayoutParams defaultTabLayoutParams;

	private int scrollOffset = 10;
	private int lastScrollX = 0;

	private final Drawable indicator;
	private final TextDrawable[] drawables;
	private final Drawable left_edge;
	private final Drawable right_edge;

	public ViewPagerIndicator(Context context) {
		this(context, null);
	}

	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mLayoutInflater = LayoutInflater.from(context);
		drawables = new TextDrawable[3];
		int i = 0;
		while (i < drawables.length) {
			drawables[i] = new TextDrawable(getContext());
			i++;
		}

		indicatorRect = new Rect();

		setFillViewport(true);
		setWillNotDraw(false);

		tabsContainer = new LinearLayout(context);
		tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(tabsContainer);

		DisplayMetrics dm = getResources().getDisplayMetrics();
		scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);

		defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

		// 绘制高亮区域作为滑动分页指示器
		indicator = getResources().getDrawable(R.drawable.bg_category_indicator);
		// 左右边界阴影效果
		left_edge = getResources().getDrawable(R.drawable.ic_category_left_edge);
		right_edge = getResources().getDrawable(R.drawable.ic_category_right_edge);
	}

	// 绑定与CategoryTabStrip控件对应的ViewPager控件，实现联动
	public void setViewPager(ViewPager pager) {
		this.pager = pager;

		if (pager.getAdapter() == null) {
			throw new IllegalStateException("ViewPager does not have adapter instance.");
		}

		pager.setOnPageChangeListener(pageListener);

		notifyDataSetChanged();
	}

	// 当附加在ViewPager适配器上的数据发生变化时,应该调用该方法通知CategoryTabStrip刷新数据
	public void notifyDataSetChanged() {
		tabsContainer.removeAllViews();

		tabCount = pager.getAdapter().getCount();

		for (int i = 0; i < tabCount; i++) {
			addTab(i, pager.getAdapter().getPageTitle(i).toString());
		}

	}

	private void addTab(final int position, String title) {
		ViewGroup tab = (ViewGroup) mLayoutInflater.inflate(R.layout.category_tab, this, false);
		TextView category_text = (TextView) tab.findViewById(R.id.category_text);
		category_text.setText(title);
		category_text.setGravity(Gravity.CENTER);
		category_text.setSingleLine();
		category_text.setFocusable(true);
		category_text.setTextColor(getResources().getColor(R.color.category_tab_text));
		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pager.setCurrentItem(position);
			}
		});

		tabsContainer.addView(tab, position, defaultTabLayoutParams);
	}

	// 计算滑动过程中矩形高亮区域的上下左右位置
	private void calculateIndicatorRect(Rect rect) {
		ViewGroup currentTab = (ViewGroup) tabsContainer.getChildAt(currentPosition);
		TextView category_text = (TextView) currentTab.findViewById(R.id.category_text);

		float left = currentTab.getLeft() + category_text.getLeft();
		float width = (category_text.getWidth()) + left;

		if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {
			ViewGroup nextTab = (ViewGroup) tabsContainer.getChildAt(currentPosition + 1);
			TextView next_category_text = (TextView) nextTab.findViewById(R.id.category_text);

			float next_left = nextTab.getLeft() + next_category_text.getLeft();
			left = left * (1.0f - currentPositionOffset) + next_left * currentPositionOffset;
			width = width * (1.0f - currentPositionOffset) + currentPositionOffset * ((next_category_text.getWidth()) + next_left);
		}

		rect.set(((int) left) + getPaddingLeft(), getPaddingTop() + currentTab.getTop() + category_text.getTop(),
				((int) width) + getPaddingLeft(), currentTab.getTop() + getPaddingTop() + category_text.getTop() + category_text.getHeight());

	}

	// 计算滚动范围
	private int getScrollRange() {
		return getChildCount() > 0 ? Math.max(0, getChildAt(0).getWidth() - getWidth() + getPaddingLeft() + getPaddingRight()) : 0;
	}

	private void scrollToChild(int position, int offset) {

		if (tabCount == 0) {
			return;
		}

		calculateIndicatorRect(indicatorRect);

		int newScrollX = lastScrollX;
		if (indicatorRect.left < getScrollX() + scrollOffset) {
			newScrollX = indicatorRect.left - scrollOffset;
		} else if (indicatorRect.right > getScrollX() + getWidth() - scrollOffset) {
			newScrollX = indicatorRect.right - getWidth() + scrollOffset;
		}
		if (newScrollX != lastScrollX) {
			lastScrollX = newScrollX;
			scrollTo(newScrollX, 0);
		}

	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		calculateIndicatorRect(indicatorRect);

		if (indicator != null) {
			indicator.setBounds(indicatorRect);
			indicator.draw(canvas);
		}

		int i = 0;
		while (i < tabsContainer.getChildCount()) {
			if (i < currentPosition - 1 || i > currentPosition + 1) {
				i++;
			} else {
				ViewGroup tab = (ViewGroup) tabsContainer.getChildAt(i);
				TextView category_text = (TextView) tab.findViewById(R.id.category_text);
				if (category_text != null) {
					TextDrawable textDrawable = drawables[i - currentPosition + 1];
					int save = canvas.save();
					calculateIndicatorRect(indicatorRect);
					canvas.clipRect(indicatorRect);
					textDrawable.setText(category_text.getText());
					textDrawable.setTextSize(0, category_text.getTextSize());
					textDrawable.setTextColor(getResources().getColor(R.color.category_tab_highlight_text));
					int left = tab.getLeft() + category_text.getLeft() + (category_text.getWidth() - textDrawable.getIntrinsicWidth()) / 2 + getPaddingLeft();
					int top = tab.getTop() + category_text.getTop() + (category_text.getHeight() - textDrawable.getIntrinsicHeight()) / 2 + getPaddingTop();
					textDrawable.setBounds(left, top, textDrawable.getIntrinsicWidth() + left, textDrawable.getIntrinsicHeight() + top);
					textDrawable.draw(canvas);
					canvas.restoreToCount(save);
				}
				i++;
			}
		}

		i = canvas.save();
		int top = getScrollX();
		int height = getHeight();
		int width = getWidth();
		canvas.translate(top, 0.0f);
		if (left_edge == null || top <= 0) {
			if (right_edge == null || top >= getScrollRange()) {
				canvas.restoreToCount(i);
			}
			right_edge.setBounds(width - right_edge.getIntrinsicWidth(), 0, width, height);
			right_edge.draw(canvas);
			canvas.restoreToCount(i);
		}
		left_edge.setBounds(0, 0, left_edge.getIntrinsicWidth(), height);
		left_edge.draw(canvas);
		if (right_edge == null || top >= getScrollRange()) {
			canvas.restoreToCount(i);
		}
		right_edge.setBounds(width - right_edge.getIntrinsicWidth(), 0, width, height);
		right_edge.draw(canvas);
		canvas.restoreToCount(i);
	}

	private class PageListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			currentPosition = position;
			currentPositionOffset = positionOffset;

			scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

			invalidate();

		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				if (pager.getCurrentItem() == 0) {
					// 滑动到最左边
					scrollTo(0, 0);
				} else if (pager.getCurrentItem() == tabCount - 1) {
					// 滑动到最右边
					scrollTo(getScrollRange(), 0);
				} else {
					scrollToChild(pager.getCurrentItem(), 0);
				}
			}
		}

		@Override
		public void onPageSelected(int position) {

		}
	}

	static class TextDrawable extends Drawable {

		/* Platform XML constants for typeface */
		private static final int SANS = 1;
		private static final int SERIF = 2;
		private static final int MONOSPACE = 3;

		/* Resources for scaling values to the given device */
		private final Resources mResources;
		/* Paint to hold most drawing primitives for the text */
		private final TextPaint mTextPaint;
		/* Layout is used to measure and draw the text */
		private StaticLayout mTextLayout;
		/* Alignment of the text inside its bounds */
		private Layout.Alignment mTextAlignment = Layout.Alignment.ALIGN_NORMAL;
		/* Optional path on which to draw the text */
		private Path mTextPath;
		/* Stateful text color list */
		private ColorStateList mTextColors;
		/* Container for the bounds to be reported to widgets */
		private final Rect mTextBounds;
		/* Text string to draw */
		private CharSequence mText = "";

		/* Attribute lists to pull default values from the current theme */
		private static final int[] themeAttributes = {
				android.R.attr.textAppearance
		};
		private static final int[] appearanceAttributes = {
				android.R.attr.textSize,
				android.R.attr.typeface,
				android.R.attr.textStyle,
				android.R.attr.textColor
		};

		public TextDrawable(Context context) {
			super();
			//Used to load and scale resource items
			mResources = context.getResources();
			//Definition of this drawables size
			mTextBounds = new Rect();
			//Paint to use for the text
			mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
			mTextPaint.density = mResources.getDisplayMetrics().density;
			mTextPaint.setDither(true);

			int textSize = 15;
			ColorStateList textColor = null;
			int styleIndex = -1;
			int typefaceIndex = -1;

			//Set default parameters from the current theme
			TypedArray a = context.getTheme().obtainStyledAttributes(themeAttributes);
			int appearanceId = a.getResourceId(0, -1);
			a.recycle();

			TypedArray ap = null;
			if (appearanceId != -1) {
				ap = context.obtainStyledAttributes(appearanceId, appearanceAttributes);
			}
			if (ap != null) {
				for (int i = 0; i < ap.getIndexCount(); i++) {
					int attr = ap.getIndex(i);
					switch (attr) {
					case 0: //Text Size
						textSize = a.getDimensionPixelSize(attr, textSize);
						break;
					case 1: //Typeface
						typefaceIndex = a.getInt(attr, typefaceIndex);
						break;
					case 2: //Text Style
						styleIndex = a.getInt(attr, styleIndex);
						break;
					case 3: //Text Color
						textColor = a.getColorStateList(attr);
						break;
					default:
						break;
					}
				}

				ap.recycle();
			}

			setTextColor(textColor != null ? textColor : ColorStateList.valueOf(0xFF000000));
			setRawTextSize(textSize);

			Typeface tf = null;
			switch (typefaceIndex) {
			case SANS:
				tf = Typeface.SANS_SERIF;
				break;

			case SERIF:
				tf = Typeface.SERIF;
				break;

			case MONOSPACE:
				tf = Typeface.MONOSPACE;
				break;
			}

			setTypeface(tf, styleIndex);
		}

		/**
		 * Set the text that will be displayed
		 * 
		 * @param text
		 *            Text to display
		 */
		public void setText(CharSequence text) {
			if (text == null)
				text = "";

			mText = text;

			measureContent();
		}

		/**
		 * Return the text currently being displayed
		 */
		public CharSequence getText() {
			return mText;
		}

		/**
		 * Return the current text size, in pixels
		 */
		public float getTextSize() {
			return mTextPaint.getTextSize();
		}

		/**
		 * Set the text size. The value will be interpreted in "sp" units
		 * 
		 * @param size
		 *            Text size value, in sp
		 */
		public void setTextSize(float size) {
			setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
		}

		/**
		 * Set the text size, using the supplied complex units
		 * 
		 * @param unit
		 *            Units for the text size, such as dp or sp
		 * @param size
		 *            Text size value
		 */
		public void setTextSize(int unit, float size) {
			float dimension = TypedValue.applyDimension(unit, size,
					mResources.getDisplayMetrics());
			setRawTextSize(dimension);
		}

		/*
		 * Set the text size, in raw pixels
		 */
		private void setRawTextSize(float size) {
			if (size != mTextPaint.getTextSize()) {
				mTextPaint.setTextSize(size);

				measureContent();
			}
		}

		/**
		 * Return the horizontal stretch factor of the text
		 */
		public float getTextScaleX() {
			return mTextPaint.getTextScaleX();
		}

		/**
		 * Set the horizontal stretch factor of the text
		 * 
		 * @param size
		 *            Text scale factor
		 */
		public void setTextScaleX(float size) {
			if (size != mTextPaint.getTextScaleX()) {
				mTextPaint.setTextScaleX(size);
				measureContent();
			}
		}

		/**
		 * Return the current text alignment setting
		 */
		public Layout.Alignment getTextAlign() {
			return mTextAlignment;
		}

		/**
		 * Set the text alignment. The alignment itself is based on the text
		 * layout direction. For LTR text NORMAL is left aligned and OPPOSITE is
		 * right aligned. For RTL text, those alignments are reversed.
		 * 
		 * @param align
		 *            Text alignment value. Should be set to one of:
		 *
		 *            {@link Layout.Alignment#ALIGN_NORMAL},
		 *            {@link Layout.Alignment#ALIGN_NORMAL},
		 *            {@link Layout.Alignment#ALIGN_OPPOSITE}.
		 */
		public void setTextAlign(Layout.Alignment align) {
			if (mTextAlignment != align) {
				mTextAlignment = align;
				measureContent();
			}
		}

		/**
		 * Sets the typeface and style in which the text should be displayed.
		 * Note that not all Typeface families actually have bold and italic
		 * variants, so you may need to use {@link #setTypeface(Typeface, int)}
		 * to get the appearance that you actually want.
		 */
		public void setTypeface(Typeface tf) {
			if (mTextPaint.getTypeface() != tf) {
				mTextPaint.setTypeface(tf);

				measureContent();
			}
		}

		/**
		 * Sets the typeface and style in which the text should be displayed,
		 * and turns on the fake bold and italic bits in the Paint if the
		 * Typeface that you provided does not have all the bits in the style
		 * that you specified.
		 *
		 */
		public void setTypeface(Typeface tf, int style) {
			if (style > 0) {
				if (tf == null) {
					tf = Typeface.defaultFromStyle(style);
				} else {
					tf = Typeface.create(tf, style);
				}

				setTypeface(tf);
				// now compute what (if any) algorithmic styling is needed
				int typefaceStyle = tf != null ? tf.getStyle() : 0;
				int need = style & ~typefaceStyle;
				mTextPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
				mTextPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
			} else {
				mTextPaint.setFakeBoldText(false);
				mTextPaint.setTextSkewX(0);
				setTypeface(tf);
			}
		}

		/**
		 * Return the current typeface and style that the Paint using for
		 * display.
		 */
		public Typeface getTypeface() {
			return mTextPaint.getTypeface();
		}

		/**
		 * Set a single text color for all states
		 * 
		 * @param color
		 *            Color value such as {@link Color#WHITE} or
		 *            {@link Color#argb(int, int, int, int)}
		 */
		public void setTextColor(int color) {
			setTextColor(ColorStateList.valueOf(color));
		}

		/**
		 * Set the text color as a state list
		 * 
		 * @param colorStateList
		 *            ColorStateList of text colors, such as inflated from an
		 *            R.color resource
		 */
		public void setTextColor(ColorStateList colorStateList) {
			mTextColors = colorStateList;
			updateTextColors(getState());
		}

		/**
		 * Optional Path object on which to draw the text. If this is set,
		 * TextDrawable cannot properly measure the bounds this drawable will
		 * need. You must call {@link #setBounds(int, int, int, int)
		 * setBounds()} before applying this TextDrawable to any View.
		 *
		 * Calling this method with <code>null</code> will remove any Path
		 * currently attached.
		 */
		public void setTextPath(Path path) {
			if (mTextPath != path) {
				mTextPath = path;
				measureContent();
			}
		}

		/**
		 * Internal method to take measurements of the current contents and
		 * apply the correct bounds when possible.
		 */
		private void measureContent() {
			//If drawing to a path, we cannot measure intrinsic bounds
			//We must resly on setBounds being called externally
			if (mTextPath != null) {
				//Clear any previous measurement
				mTextLayout = null;
				mTextBounds.setEmpty();
			} else {
				//Measure text bounds
				double desired = Math.ceil(Layout.getDesiredWidth(mText, mTextPaint));
				mTextLayout = new StaticLayout(mText, mTextPaint, (int) desired,
						mTextAlignment, 1.0f, 0.0f, false);
				mTextBounds.set(0, 0, mTextLayout.getWidth(), mTextLayout.getHeight());
			}

			//We may need to be redrawn
			invalidateSelf();
		}

		/**
		 * Internal method to apply the correct text color based on the
		 * drawable's state
		 */
		private boolean updateTextColors(int[] stateSet) {
			int newColor = mTextColors.getColorForState(stateSet, Color.WHITE);
			if (mTextPaint.getColor() != newColor) {
				mTextPaint.setColor(newColor);
				return true;
			}

			return false;
		}

		@Override
		protected void onBoundsChange(Rect bounds) {
			//Update the internal bounds in response to any external requests
			mTextBounds.set(bounds);
		}

		@Override
		public boolean isStateful() {
			/*
			 * The drawable's ability to represent state is based on
			 * the text color list set
			 */
			return mTextColors.isStateful();
		}

		@Override
		protected boolean onStateChange(int[] state) {
			//Upon state changes, grab the correct text color
			return updateTextColors(state);
		}

		@Override
		public int getIntrinsicHeight() {
			//Return the vertical bounds measured, or -1 if none
			if (mTextBounds.isEmpty()) {
				return -1;
			} else {
				return (mTextBounds.bottom - mTextBounds.top);
			}
		}

		@Override
		public int getIntrinsicWidth() {
			//Return the horizontal bounds measured, or -1 if none
			if (mTextBounds.isEmpty()) {
				return -1;
			} else {
				return (mTextBounds.right - mTextBounds.left);
			}
		}

		@Override
		public void draw(Canvas canvas) {
			final Rect bounds = getBounds();
			final int count = canvas.save();
			canvas.translate(bounds.left, bounds.top);
			if (mTextPath == null) {
				//Allow the layout to draw the text
				mTextLayout.draw(canvas);
			} else {
				//Draw directly on the canvas using the supplied path
				canvas.drawTextOnPath(mText.toString(), mTextPath, 0, 0, mTextPaint);
			}
			canvas.restoreToCount(count);
		}

		@Override
		public void setAlpha(int alpha) {
			if (mTextPaint.getAlpha() != alpha) {
				mTextPaint.setAlpha(alpha);
			}
		}

		@Override
		public int getOpacity() {
			return mTextPaint.getAlpha();
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
			if (mTextPaint.getColorFilter() != cf) {
				mTextPaint.setColorFilter(cf);
			}
		}

	}

}
