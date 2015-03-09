package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Description:一个自定义的进度条，具备跳动的动画和平滑的进度，可以指定进度的同时指定进度进展的耗时。 Created by lipan on
 * 2014/8/15.
 */
public class ProgressBarSlowly extends View {

	public static final int BACKGROUND_COLOR1 = Color.rgb(32, 37, 45);
	public static final int BACKGROUND_COLOR2 = Color.rgb(29, 34, 42);

	public static final int PROGRESS_COLOR1 = Color.rgb(251, 185, 0);
	public static final int PROGRESS_COLOR2 = Color.rgb(239, 123, 0);

	public static final int PROGRESS_DURATION = 400;

	public static final int PROGRESS_DEFAULT_MAX = 100;

	public static final int MIN_HEIGHT = 10;
	public static final int MIN_WIDTH = 50;
	public static final int DEFAULT_RADIUS = 5;

	public static final int BOUNCE_DURATION = 400;
	public static final int BOUNCE_SLEEP = 1000;
	public static final int BOUNCE_DELAY = 200;
	public static final int BOUNCE_MARGIN = 5;

	private Drawable[] mBounceDrawables; // 跳动的图片
	private int mBounceDrawableHeight; // 跳动图片的高度
	private int mBounceDrawableWidth; // 跳动图片的宽度
	private int mBounceDrawablesTotalWidth; // 跳动图片的总宽度
	private int mBounceHeiht; // 跳动图片跳动的距离
	private int mBounceMargin = BOUNCE_MARGIN; // 跳动图片距离下面进度条的距离

	private Drawable mProgressBackGroundDrawable; // 进度条的背景色

	private int mProgressHeight = MIN_HEIGHT; // 进度条的最小高度
	private int mProgressMinWidth = MIN_WIDTH; // 进度条的最小宽度
	private Paint mProgressPaint1; // 进度条的画笔1
	private Paint mProgressPaint2; // 进度条的画笔2
	private Bitmap mProgressBitmap; // 进度条的图片

	private int mRadius = DEFAULT_RADIUS; // 圆角度数
	private float mScale; // 缩放比例

	private Interpolator mInterpolator; // 动画插入器

	public long mDrawStartTime; // 动画执行的起始时间

	private long mStartProgressTime; // 某段进度条执行的起始时间，每次设置进度后，该起始时间将会初始化为当前时间
	private int mMaxProgress = PROGRESS_DEFAULT_MAX; // 进度条的最大值，如果不设置，默认为100
	private int mStartProgress; // 某段进度的起始进度
	private int mCurrentProgress; // 当前的进度
	private int mProgress;// 最终要达到的进度
	private int mProgressDuration; // 该段进度执行的时间
	private String text;
	private int backdrawable;

	public ProgressBarSlowly(Context context) {
		super(context);
		init();
	}

	public ProgressBarSlowly(Context context, AttributeSet attrs) {
		super(context, attrs);
		backdrawable = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "src", -1);
		init();
	}

	public ProgressBarSlowly(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		backdrawable = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "src", -1);
		init();
	}

	/** 初始化一些变量，并根据屏幕密度对值进行缩放 */
	private void init() {
		if (backdrawable != -1) {
			mBounceDrawables = new Drawable[3];
			mBounceDrawablesTotalWidth = 0;
			for (int i = 0; i < mBounceDrawables.length; i++) {
				mBounceDrawables[i] = getResources().getDrawable(backdrawable);
				if (mBounceDrawables[i].getIntrinsicHeight() > mBounceDrawableHeight) {
					mBounceDrawableHeight = mBounceDrawables[i].getIntrinsicHeight();
				}

				if (mBounceDrawables[i].getIntrinsicWidth() > mBounceDrawableWidth) {
					mBounceDrawableWidth = mBounceDrawables[i].getIntrinsicWidth();
				}
			}
			mBounceDrawablesTotalWidth = mBounceDrawableWidth * mBounceDrawables.length;
		}
		mBounceHeiht = (int) (mBounceDrawableHeight * 0.65 + 0.5f);

		mInterpolator = new AccelerateDecelerateInterpolator();

		mScale = getContext().getResources().getDisplayMetrics().density;
		mBounceMargin = (int) (mBounceMargin * mScale + 0.5f);

		mProgressHeight = (int) (mProgressHeight * mScale + 0.5f);
		if (mProgressHeight % 2 != 0) {
			mProgressHeight++;
		}
		mProgressMinWidth = mProgressHeight * 5;
		mRadius = (int) (mRadius * mScale + 0.5f);

		mProgressBackGroundDrawable = createBackgroud();

		mProgressPaint1 = new Paint();
		mProgressPaint1.setAntiAlias(true);
		mProgressPaint1.setColor(PROGRESS_COLOR1);

		mProgressPaint2 = new Paint();
		mProgressPaint2.setAntiAlias(true);
		mProgressPaint2.setColor(PROGRESS_COLOR2);
		mProgressBitmap = createProgressBitmap();
	}

	/** 设置最大进度 */
	public void setMaxProgress(int max) {
		if (mMaxProgress != max) {
			mMaxProgress = max;
			postInvalidate();
		}
	}

	/** 设置当前进度 */
	public void setProgress(int progress, int duration) {
		if (mProgress != progress || mProgressDuration != duration) {
			mProgress = progress;
			mProgressDuration = duration;
			if (mProgress == 0) {
				mCurrentProgress = 0;
			}
			mStartProgress = mCurrentProgress;
			mStartProgressTime = System.currentTimeMillis();
			postInvalidate();
		}
	}

	public void setText(String text) {
		this.text = text;
	}

	/** 创建背景图片 */
	private Drawable createBackgroud() {
		GradientDrawable drawable = new GradientDrawable(); // 生成Shape
		drawable.setGradientType(GradientDrawable.RECTANGLE); // 设置矩形
		drawable.setColor(0xbb7aaaaa);// 内容区域的颜色
		drawable.setCornerRadius(mRadius); // 设置四角都为圆角
		return drawable;

	}

	/** 测量自身大小 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		if (widthMode == MeasureSpec.AT_MOST) {// 如果是包裹内容，宽度需要根据跳动图片的总宽度/进度条的最小宽度/和父控件传递过来的宽度决定
			int wrapWidth = mBounceDrawablesTotalWidth + getPaddingLeft() + getPaddingRight();
			if (wrapWidth < mProgressMinWidth) {
				wrapWidth = mProgressMinWidth;
			}
			if (wrapWidth < widthSize) {
				widthSize = wrapWidth;
			}
		}

		if (heightMode == MeasureSpec.AT_MOST) {// 如果是包裹内容，高度需要根据跳动图片的高度/跳动图片的跳动范围和margin/进度条的最小高度/和父控件传递过来的高度决定
			int bounceSpace = mBounceDrawableHeight + mBounceHeiht + mBounceMargin;
			int wrapHeight = bounceSpace + mProgressHeight + getPaddingTop() + getPaddingBottom();
			if (wrapHeight < mProgressHeight) {
				wrapHeight = mProgressHeight;
			}
			if (wrapHeight < heightSize) {
				heightSize = wrapHeight;
			}
		}
		setMeasuredDimension(widthSize, heightSize);
	}

	/** 画自身 */
	@Override
	protected void onDraw(Canvas canvas) {
		if (mDrawStartTime == 0) {
			mDrawStartTime = System.currentTimeMillis();
		}

		// Bitmap bitmap = createProgressBitmap();
		// canvas.drawBitmap(bitmap,0,0,new Paint());
		if (text != null) {
			drawText(canvas);
		}
		if (mBounceDrawables != null && mBounceDrawables.length != 0) {
			drawBounce(canvas);
		}
		drawProgress(canvas);

		needInvalidate();
	}

	/** 判断是否需要持续画 */
	private void needInvalidate() {
		if (getParent() != null && getVisibility() == View.VISIBLE) {
			postInvalidate();
		}
	}

	/** 画文字 */
	private void drawText(Canvas canvas) {
		int contentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
		int left = getPaddingLeft();
		int top = getPaddingTop() + mBounceDrawableHeight;
		int bottom = top + mBounceDrawableHeight;
		int right = contentWidth - mBounceDrawablesTotalWidth - getPaddingRight();
		Paint p = new Paint();
		p.setTextSize(50);
		Rect rect = new Rect();
		p.getTextBounds("我", 0, 1, rect);
		// System.out.println("~~~~~~  "+rect.width() +"  "+ rect.height() +
		// " "+rect.centerX());
		// canvas.drawText();
		int count = contentWidth / MIN_WIDTH;
		if (count <= 0) {
			count = 1;
		}
		int[] colors = new int[count * 2];
		for (int i = 0; i < count; i++) {
			colors[i * 2] = PROGRESS_COLOR1;
			colors[i * 2 + 1] = PROGRESS_COLOR2;
		}

		long time = System.currentTimeMillis() - mDrawStartTime;
		float factor = (time % PROGRESS_DURATION) / (float) PROGRESS_DURATION;
		int offset = -(int) ((1 - factor) * MIN_WIDTH);
		LinearGradient shader = new LinearGradient(offset, 0, (right - left + offset), (bottom - top), colors, null, Shader.TileMode.MIRROR);
		p.setShader(shader);
		canvas.drawText(text, left, top, p);
	}

	/** 画跳动的图片 */
	private void drawBounce(Canvas canvas) {
		int contentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
		int left = getPaddingLeft();
		int top;
		if (mBounceDrawablesTotalWidth < contentWidth) {
			left = contentWidth - mBounceDrawablesTotalWidth - getPaddingRight();
		}
		// 算出本次动画开始的时间
		long time = (System.currentTimeMillis() - mDrawStartTime) % (BOUNCE_DURATION * 2 + BOUNCE_SLEEP);
		long begin;
		long executed;
		for (int i = 0; i < mBounceDrawables.length; i++) {
			top = getPaddingTop();

			begin = i * BOUNCE_DELAY;// 当前图片开始时间
			if (begin >= time) {// 当前图片还没开始动画
				top += mBounceHeiht;
			} else {// 当前图片已经开始动画
				executed = time - begin;// 算出已经执行的时间
				if (executed <= BOUNCE_DURATION) { // 还在执行向上的动画
					float factor = mInterpolator.getInterpolation((float) executed / BOUNCE_DURATION);// 根据插入器算出当前的位置
					top += (int) ((1 - factor) * mBounceHeiht + 0.5f);
				} else if (executed > BOUNCE_DURATION && executed < BOUNCE_DURATION * 2) {// 执行向下的动画
					float factor = mInterpolator.getInterpolation((float) (executed - BOUNCE_DURATION) / BOUNCE_DURATION);// 根据插入器算出当前的位置
					top += (int) (factor * mBounceHeiht + 0.5f);
				} else { // 已经执行完毕了，处理休息时间
					top += mBounceHeiht;
				}
			}
			Drawable mBounceDrawable = mBounceDrawables[i];
			mBounceDrawable.setBounds(left, top, left + mBounceDrawableWidth, top + mBounceDrawableHeight);// 设置图片绘制的位置
			mBounceDrawable.draw(canvas);
			left += mBounceDrawableWidth;
		}
	}

	/** 画进度条 */
	private void drawProgress(Canvas canvas) {
		// 确定进度条的位置
		int top = getPaddingTop() + mBounceDrawableHeight + mBounceHeiht + mBounceMargin;
		int left = getPaddingLeft();
		int right = getMeasuredWidth() - getPaddingRight();
		int bottom = top + mProgressHeight;
		// 画背景
		mProgressBackGroundDrawable.setBounds(left, top, right, bottom);
		mProgressBackGroundDrawable.draw(canvas);
		// 计算进度条的宽度
		float factor;
		long currentTime = System.currentTimeMillis();
		if (mCurrentProgress >= mProgress) {
			factor = 1;
		} else {
			if (currentTime - mStartProgressTime < 0) {
				factor = 0;
			} else if (currentTime - mStartProgressTime < mProgressDuration) {
				factor = (currentTime - mStartProgressTime) / (float) mProgressDuration;
			} else {
				factor = 1;
			}
		}
		mCurrentProgress = (int) (mStartProgress + factor * (mProgress - mStartProgress) + 0.5f);
		int width = right - left;
		width = (int) (width * mCurrentProgress / (float) mMaxProgress + 0.5f);
		// 创建图片并确定位置进行绘制
		Bitmap bitmap = createBitmapByTime();
		Drawable drawable = tileify(bitmap);
		drawable.setBounds(left, top, left + width, bottom);
		drawable.draw(canvas);
	}

	/** 根据当前的时间创建图片 */
	private Bitmap createBitmapByTime() {
		long time = System.currentTimeMillis() - mDrawStartTime;

		float factor = (time % PROGRESS_DURATION) / (float) PROGRESS_DURATION;
		int width = (int) (mProgressMinWidth / 2.0f);
		int x = width - (int) (factor * (mProgressMinWidth / 2) + 0.5f);
		return Bitmap.createBitmap(mProgressBitmap, x, 0, width, mProgressHeight);
	}

	/** 创建一张最小的进度条图片 */
	private Bitmap createProgressBitmap() {
		Bitmap bitmap = Bitmap.createBitmap(mProgressMinWidth, mProgressHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Path path = new Path();

		path.moveTo(0, 0);
		path.lineTo(0, mProgressHeight);
		path.lineTo(mProgressHeight, 0);
		path.close();
		canvas.drawPath(path, mProgressPaint1);

		path = new Path();
		path.moveTo(mProgressHeight, 0);
		path.lineTo(0, mProgressHeight);
		path.lineTo(mProgressHeight, mProgressHeight);
		path.lineTo(mProgressHeight * 2, 0);
		path.close();
		canvas.drawPath(path, mProgressPaint2);

		path = new Path();
		path.moveTo(mProgressHeight * 2, 0);
		path.lineTo(mProgressHeight, mProgressHeight);
		path.lineTo((int) (mProgressHeight * 2.5 + 0.5f), mProgressHeight);
		path.lineTo((int) (mProgressHeight * 3.5 + 0.5f), 0);
		path.close();
		canvas.drawPath(path, mProgressPaint1);

		path = new Path();
		path.moveTo((int) (mProgressHeight * 3.5 + 0.5f), 0);
		path.lineTo((int) (mProgressHeight * 2.5 + 0.5f), mProgressHeight);
		path.lineTo((int) (mProgressHeight * 3.5 + 0.5f), mProgressHeight);
		path.lineTo((int) (mProgressHeight * 4.5 + 0.5f), 0);
		path.close();
		canvas.drawPath(path, mProgressPaint2);

		path = new Path();
		path.moveTo((int) (mProgressHeight * 4.5 + 0.5f), 0);
		path.lineTo((int) (mProgressHeight * 3.5 + 0.5f), mProgressHeight);
		path.lineTo(mProgressHeight * 5, mProgressHeight);
		path.lineTo(mProgressHeight * 5, 0);
		path.close();
		canvas.drawPath(path, mProgressPaint1);

		return bitmap;
	}

	/** 对图片进行重复拼接，并设置圆角 */
	private Drawable tileify(Bitmap bitmap) {
		float[] roundedCorners = new float[] { mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius };
		RoundRectShape roundRectShape = new RoundRectShape(roundedCorners, null, null);
		final ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
		final BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
		shapeDrawable.getPaint().setShader(bitmapShader);
		ClipDrawable drawable = new ClipDrawable(shapeDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
		drawable.setLevel(10000);
		return drawable;
	}
}
