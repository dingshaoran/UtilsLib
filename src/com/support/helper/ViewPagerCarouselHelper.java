package com.support.helper;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.support.utils.ImageViewUtils;
import com.xutils.bitmap.BitmapDisplayConfig;

public class ViewPagerCarouselHelper implements OnClickListener {
	private final Context context;
	ViewPager viewPager;
	private int POINT_MARGIN = 20;
	private final int DELAYTIME = 3000;
	private List<View> imageList;
	private boolean canScroll;
	private boolean autoScroll;

	public boolean isCanScoll() {
		return canScroll;
	}

	public void setCanScoll(boolean canScoll, boolean autoScroll) {
		this.canScroll = canScoll;
		this.autoScroll = autoScroll;
	}

	public void setPointMargin(int marg) {
		POINT_MARGIN = marg;
	}

	/**
	 * 带有文字描述和显示当前图片序列的radiogroup
	 * 必须在finish时调用此类的handler.removeCallbacksAndMessages(null)；
	 * 
	 * @param context
	 * @param viewPager一个viewpager
	 */
	public ViewPagerCarouselHelper(Context context, ViewPager viewPager) {
		this.context = context;
		this.viewPager = viewPager;
	}

	/**
	 * 
	 * @param descTv图片信息描述
	 * @param pointGroup图片显示序列的radiogroup是一个LinearLayout
	 * @param imageIds所有viewpager的图片资源id
	 * @param descs所有图片的描述信息
	 * @param selectPoint指示图片资源id数组 ，1选中图片的radio图片，0其他图片的radio图片id
	 * @return当前viewpager
	 */
	public ViewPagerCarouselHelper imageTextRadio(final TextView descTv, final LinearLayout pointGroup, String[] imageIds, final String[] descs, final int[] selectPoint) {
		imageList = new LinkedList<View>();
		ImageViewUtils viewUtils = new ImageViewUtils(context);
		BitmapDisplayConfig bitmapDisplayConfig = new BitmapDisplayConfig();
		if (canScroll) {
			ImageView imageLastAdd = new ImageView(context);
			viewUtils.display(imageLastAdd, imageIds[imageIds.length - 1], bitmapDisplayConfig);
			imageList.add(imageLastAdd);
		}
		for (int i = 0; i < imageIds.length; i++) {
			ImageView image = new ImageView(context);
			viewUtils.display(image, imageIds[i], bitmapDisplayConfig);
			imageList.add(image);
			ImageView point = new ImageView(context);
			point.setId(i);
			point.setOnClickListener(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.leftMargin = 20;
			point.setLayoutParams(params);
			if (pointGroup != null && selectPoint != null) {
				pointGroup.addView(point);
				if (i == 0) {
					point.setBackgroundResource(selectPoint[1]);
				} else {
					point.setBackgroundResource(selectPoint[0]);
				}
			}
		}
		if (canScroll) {
			ImageView imageFirstAdd = new ImageView(context);
			viewUtils.display(imageFirstAdd, imageIds[0], bitmapDisplayConfig);
			imageList.add(imageFirstAdd);
		}
		viewPager.setAdapter(adapter);
		if (descTv != null && descs != null) {
			descTv.setText(descs[0]);
		}
		if (canScroll) {
			viewPager.setCurrentItem(1, false);
		}
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			private int lastPointPosition = 0;
			private int position = -1;

			@Override
			public void onPageSelected(int position) {
				this.position = position;
				if (position == 0 || position == imageList.size() - 1) {
					if (!canScroll) {
						pointGroup.getChildAt(lastPointPosition).setBackgroundResource(selectPoint[0]);
						pointGroup.getChildAt(position).setBackgroundResource(selectPoint[1]);
						lastPointPosition = position;
					}
					return;
				}
				if (canScroll) {
					position--;
				}
				if (descTv != null && descs != null) {
					descTv.setText(descs[position]);
				}
				if (pointGroup != null && selectPoint != null) {
					pointGroup.getChildAt(lastPointPosition).setBackgroundResource(selectPoint[0]);
					pointGroup.getChildAt(position).setBackgroundResource(selectPoint[1]);
				}
				lastPointPosition = position;

			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == ViewPager.SCROLL_STATE_IDLE && canScroll) {
					if (position == 0) {
						viewPager.setCurrentItem(imageList.size() - 2, false);
					} else if (position == imageList.size() - 1) {
						viewPager.setCurrentItem(1, false);
					}
				}
			}
		});
		if (autoScroll) {
			handler.sendEmptyMessageDelayed(99, DELAYTIME);
		}
		return this;

	}

	/**
	 * 
	 * @param pointGroup显示序列的radiogroup是一个LinearLayout
	 * @param gvs_viewpager中每一个条目gv
	 * @param selectPoint选中1和未选中0的radio
	 * @return
	 */
	public ViewPagerCarouselHelper gridViewRadio(final LinearLayout pointGroup, List<BaseAdapter> gvs, int layouRes, final int[] selectPoint) {
		pointGroup.removeAllViews();
		imageList = new LinkedList<View>();
		if (canScroll) {
			GridView gvLastAdd = (GridView) View.inflate(context, layouRes, null);
			gvLastAdd.setAdapter(gvs.get(gvs.size() - 1));
			imageList.add(gvLastAdd);
		}
		for (int i = 0; i < gvs.size(); i++) {
			GridView gv = (GridView) View.inflate(context, layouRes, null);
			gv.setAdapter(gvs.get(i));
			imageList.add(gv);
		}
		if (canScroll) {
			GridView gvFirstAdd = (GridView) View.inflate(context, layouRes, null);
			gvFirstAdd.setAdapter(gvs.get(0));
			imageList.add(gvFirstAdd);
		}
		for (int i = 0; i < gvs.size(); i++) {
			ImageView point = new ImageView(context);
			point.setId(i);
			point.setOnClickListener(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.leftMargin = POINT_MARGIN;
			point.setLayoutParams(params);
			if (pointGroup != null && selectPoint != null) {

				pointGroup.addView(point);
				if (i == 0) {
					point.setBackgroundResource(selectPoint[1]);
				} else {
					point.setBackgroundResource(selectPoint[0]);
				}
			}
		}
		viewPager.setAdapter(adapter);
		if (canScroll) {
			viewPager.setCurrentItem(1, false);
		}
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			private int lastPointPosition = 0;
			private int position = -1;

			@Override
			public void onPageSelected(int position) {
				this.position = position;
				if (position == imageList.size() - 1 || position == 0) {
					if (!canScroll) {
						pointGroup.getChildAt(lastPointPosition).setBackgroundResource(selectPoint[0]);
						pointGroup.getChildAt(position).setBackgroundResource(selectPoint[1]);
						lastPointPosition = position;
					}
					return;
				}
				if (canScroll) {
					position--;
				}
				if (pointGroup != null && selectPoint != null) {
					pointGroup.getChildAt(lastPointPosition).setBackgroundResource(selectPoint[0]);
					pointGroup.getChildAt(position).setBackgroundResource(selectPoint[1]);
				}
				lastPointPosition = position;
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == ViewPager.SCROLL_STATE_IDLE && canScroll) {
					if (position == 0) {
						viewPager.setCurrentItem(imageList.size() - 2, false);
					} else if (position == imageList.size() - 1) {
						viewPager.setCurrentItem(1, false);
					}
				}
			}
		});
		if (autoScroll) {
			handler.sendEmptyMessageDelayed(99, DELAYTIME);
		}
		return this;

	}

	private int cur;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			handler.removeCallbacksAndMessages(null);
			if (cur == viewPager.getCurrentItem()) {
				viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
			}
			if (autoScroll) {
				sendEmptyMessageDelayed(99, DELAYTIME);
			}
			cur = viewPager.getCurrentItem();
		};
	};

	@Override
	public void onClick(View v) {
		handler.removeCallbacksAndMessages(null);
		int cur = viewPager.getCurrentItem();
		int curtrue = cur;
		int temp = v.getId() - curtrue > 0 ? v.getId() - curtrue : v.getId() - curtrue + imageList.size();
		for (int i = 0; i < temp; i++) {
			viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
		}
		if (autoScroll) {
			handler.sendEmptyMessageDelayed(99, DELAYTIME);
		}
	}

	private final PagerAdapter adapter = new PagerAdapter() {
		@Override
		public int getCount() {
			return imageList.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = imageList.get(position);
			container.addView(view);
			return view;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	};
}
