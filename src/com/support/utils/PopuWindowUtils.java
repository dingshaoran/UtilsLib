package com.support.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.support_libs.R;

public class PopuWindowUtils {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SuppressLint({ "NewApi", "InlinedApi" })
	public static PopupWindow showDropListMenu(final Activity context, View anchor, final Object map, final ShowDropListMenuOnClick listener, int textViewRes, final int width, final int hight, int xoff, int yoff) {
		View textVI = View.inflate(context, textViewRes, null);
		final Drawable NORMALBG = textVI.getBackground();
		final List<Integer> rli = new LinkedList<Integer>();
		final List<View> rlv = new LinkedList<View>();
		final List<String> llc0 = new LinkedList<String>();
		final List<String> llc1 = new LinkedList<String>();
		final List<String> llc2 = new LinkedList<String>();
		final Drawable divider = null;
		LinearLayout parent = new LinearLayout(context);
		ViewGroup.LayoutParams lpp = new ViewGroup.LayoutParams(DpAndPxUtils.getScreenWidth(context), DpAndPxUtils.getScreenHeight(context));
		parent.setLayoutParams(lpp);
		final ListView lvc0 = new ListView(context);
		parent.addView(lvc0);
		final ListView lvc1 = new ListView(context);
		parent.addView(lvc1);
		final ListView lvc2 = new ListView(context);
		parent.addView(lvc2);
		lvc0.setHeaderDividersEnabled(true);
		lvc0.setFooterDividersEnabled(true);
		lvc0.setDivider(divider);
		lvc0.setVerticalScrollBarEnabled(false);
		if (android.os.Build.VERSION.SDK_INT > 8) {
			lvc0.setOverScrollMode(View.OVER_SCROLL_NEVER);
		} else {
			lvc0.setFadingEdgeLength(0);
		}
		ViewGroup.LayoutParams lpc0 = lvc0.getLayoutParams();
		lpc0.width = width;
		lpc0.height = hight;
		lvc1.setHeaderDividersEnabled(true);
		lvc1.setFooterDividersEnabled(true);
		lvc1.setDivider(divider);
		lvc1.setVerticalScrollBarEnabled(false);
		if (android.os.Build.VERSION.SDK_INT > 8) {
			lvc1.setOverScrollMode(View.OVER_SCROLL_NEVER);
		} else {
			lvc1.setFadingEdgeLength(0);
		}
		lvc2.setHeaderDividersEnabled(true);
		lvc2.setFooterDividersEnabled(true);
		lvc2.setDivider(divider);
		lvc2.setVerticalScrollBarEnabled(false);
		ViewGroup.LayoutParams lpc2 = lvc1.getLayoutParams();
		lpc2.width = width;
		lpc2.height = hight;
		if (android.os.Build.VERSION.SDK_INT > 8) {
			lvc2.setOverScrollMode(View.OVER_SCROLL_NEVER);
		} else {
			lvc2.setFadingEdgeLength(0);
		}
		BaseAdapter ac0 = new ArrayAdapter<String>(context, textViewRes, llc0);
		lvc0.setAdapter(ac0);
		final BaseAdapter ac1 = new ArrayAdapter<String>(context, textViewRes, llc1);
		lvc1.setAdapter(ac1);
		final BaseAdapter ac2 = new ArrayAdapter<String>(context, textViewRes, llc2);
		lvc2.setAdapter(ac2);
		if (map instanceof List) {
			llc0.clear();
			llc0.addAll((List<String>) map);
			ac0.notifyDataSetChanged();
			lvc0.setOnItemClickListener(new OnItemClickListener() {

				private View preView1;

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (preView1 != null) {
						preView1.setBackground(NORMALBG);
					}
					preView1 = view;
					view.setBackgroundColor(0x33000000);
					rlv.add(view);
					rli.add(position);
					listener.OnClick(rlv, rli);
				}
			});
		} else if (map instanceof Map) {
			llc0.clear();
			llc0.addAll(((Map) map).keySet());
			ac0.notifyDataSetChanged();
			lvc0.setOnItemClickListener(new OnItemClickListener() {
				private View preView2;

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					llc2.clear();
					ac2.notifyDataSetChanged();
					if (preView2 != null) {
						preView2.setBackground(NORMALBG);
					}
					preView2 = view;
					view.setBackgroundColor(0x33000000);
					rlv.add(view);
					rli.add(position);
					LinearLayout.LayoutParams lpc1 = (LayoutParams) lvc1.getLayoutParams();
					lpc1.width = width;
					lpc1.height = hight;
					lpc1.setMargins(0, lvc0.getWidth() / llc0.size() / 2 * position, 0, 0);
					final Object map1 = ((Map) map).get(((TextView) view).getText());
					if (map1 instanceof List) {
						if (llc1.size() == 0) {
							llc1.addAll((List<String>) map1);
							lvc1.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_from_top));
						} else {
							llc1.clear();
							llc1.addAll((List<String>) map1);
						}
						lvc1.setAdapter(ac1);
						lvc1.setOnItemClickListener(new OnItemClickListener() {
							private View preView3;

							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								if (preView3 != null) {
									preView3.setBackground(NORMALBG);
								}
								preView3 = view;
								view.setBackgroundColor(0x33000000);
								rlv.add(view);
								rli.add(position);
								listener.OnClick(rlv, rli);
							}
						});
					} else if (map1 instanceof String) {
						listener.OnClick(rlv, rli);
					} else if (map1 instanceof Map) {
						if (llc1.size() == 0) {
							llc1.addAll(((Map) map1).keySet());
							lvc1.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_from_top));
						} else {
							llc1.clear();
							llc1.addAll(((Map) map1).keySet());
						}
						lvc1.setAdapter(ac1);
						lvc1.setOnItemClickListener(new OnItemClickListener() {
							private View preView4;

							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								if (preView4 != null) {
									preView4.setBackground(NORMALBG);
								}
								preView4 = view;
								view.setBackgroundColor(0x33000000);
								rlv.add(view);
								rli.add(position);
								LinearLayout.LayoutParams lpc2 = (LayoutParams) lvc2.getLayoutParams();
								lpc2.width = width;
								lpc2.height = hight;
								lpc2.setMargins(0, lvc1.getWidth() / llc1.size() / 2 * position, 0, 0);
								Object map2 = ((Map) map1).get(((TextView) view).getText());
								if (map2 instanceof List) {
									if (llc2.size() == 0) {
										llc2.addAll((List<String>) map2);
										lvc2.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_from_top));
									} else {
										llc2.clear();
										llc2.addAll((List<String>) map2);
									}
									lvc2.setAdapter(ac2);
									lvc2.setOnItemClickListener(new OnItemClickListener() {
										private View preView5;

										@Override
										public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
											if (preView5 != null) {
												preView5.setBackground(NORMALBG);
											}
											preView5 = view;
											view.setBackgroundColor(0x33000000);
											rlv.add(view);
											rli.add(position);
											listener.OnClick(rlv, rli);
										}
									});
								} else if (map2 instanceof String) {
									listener.OnClick(rlv, rli);
								}
							}
						});
					}
				}
			});

		}
		lvc0.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					for (int i = 0; i < view.getChildCount(); i++) {
						view.getChildAt(i).setBackground(NORMALBG);
					}
					llc1.clear();
					llc2.clear();
					ac1.notifyDataSetChanged();
					ac2.notifyDataSetChanged();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
		lvc1.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					for (int i = 0; i < view.getChildCount(); i++) {
						view.getChildAt(i).setBackground(NORMALBG);
					}
					llc2.clear();
					ac2.notifyDataSetChanged();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
		lvc2.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					for (int i = 0; i < view.getChildCount(); i++) {
						view.getChildAt(i).setBackground(NORMALBG);
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
		PopupWindow pw = showAsDropDown(anchor, parent, DpAndPxUtils.getScreenWidth(context), DpAndPxUtils.getScreenHeight(context), xoff, yoff, R.style.anim_fromtop);
		return pw;
	}

	/**
	 * 在anchor右下方显示一个popuwindow
	 * 
	 * @param anchor相对的view原点在它的右下方
	 * @param view要显示的view
	 * @param width宽
	 * @param hight高
	 * @param xoff在原点的基础上向右偏移量
	 * @param yoff在原点基础上向下偏移量
	 * @param animstyle动画
	 *            ，默认-1
	 * @return
	 */
	public static PopupWindow showAsDropDown(View anchor, View view, int width, int hight, int xoff, int yoff, int animstyle) {
		PopupWindow popupWindow = new PopupWindow(view, width, hight);
		popupWindow.setBackgroundDrawable(new ColorDrawable());
		popupWindow.setFocusable(true);
		popupWindow.setAnimationStyle(animstyle);
		popupWindow.setOutsideTouchable(true);
		popupWindow.showAsDropDown(anchor, xoff, yoff);
		return popupWindow;
	}

	/**
	 * 在当前window中显示一个popuwindow在的指定位置。
	 * 
	 * @param view要显示的view
	 * @param width显示的宽度
	 * @param hight显示的高度
	 * @param gravity对其方式
	 * @param xoff在对其方式的基础上向右偏移量
	 * @param yoff在对其方式的基础上向下偏移量
	 * @param animstyle显示和隐藏的动画
	 * @return 返回这个popuwindow方便隐藏
	 */
	public static PopupWindow showAtLocation(View view, int width, int hight, int gravity, int xoff, int yoff, int animstyle) {
		PopupWindow popupWindow = new PopupWindow(view, width, hight);
		popupWindow.setBackgroundDrawable(new ColorDrawable(0x55000000));
		popupWindow.setFocusable(true);
		popupWindow.setAnimationStyle(animstyle);
		popupWindow.setOutsideTouchable(true);
		popupWindow.showAtLocation(view, gravity, xoff, yoff);
		return popupWindow;
	}

	public interface ShowDropListMenuOnClick {
		public void OnClick(List<View> view, List<Integer> rli);
	}
}
