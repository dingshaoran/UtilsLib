package com.support.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
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

	/**
	 * 创建一个popuwindow里面是listview的如果anchor在屏幕的偏上的位置就把popu现在在下面，
	 * 如果anchor在屏幕的下面就把popu显示在上面
	 * 
	 * @param context所在的activity
	 * @param anchor要显示在哪个view上面或者下面
	 * @param objects
	 *            listview要显示的数据
	 * @param listener点击的回调监听
	 * @return
	 */

	public static PopupWindow showList(Activity context, View anchor, String[] objects, final OnItemClickListener listener) {

		int[] location = new int[2];
		anchor.getLocationInWindow(location);//获取view的绝对左边
		Rect frame = new Rect();
		context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;//状态栏的高度
		int c = context.getResources().getDisplayMetrics().widthPixels / 100;//大概3.6dp留个边距
		int childTop = location[1];
		int parentHeight = context.getResources().getDisplayMetrics().heightPixels;//屏幕高度
		boolean showUp = childTop > (parentHeight + anchor.getHeight()) / 2 ? true : false;//控制在view上面显示还是在下面
		LinearLayout ll = new LinearLayout(context);
		ListView listView = new ListView(context);
		LayoutParams lp = new LayoutParams(-1, -2);//外面套一层view，控制listview如果内容多就滑动，内容少就包裹内容
		lp.setMargins(0, c / 2, 0, c / 2);
		ll.addView(listView, lp);
		listView.setBackgroundColor(0xff666666);
		listView.setHeaderDividersEnabled(true);
		listView.setFooterDividersEnabled(true);
		listView.setSelector(new ColorDrawable());
		listView.setDivider(new ColorDrawable(Color.WHITE));
		listView.setVerticalScrollBarEnabled(false);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				listener.onItemClick(parent, view, position, id);
				popupWindow.dismiss();
				popupWindow = null;
			}
		});
		if (android.os.Build.VERSION.SDK_INT > 8) {
			listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		} else {
			listView.setFadingEdgeLength(0);
		}
		listView.setAdapter(new ArrayAdapter<String>(context, R.layout.item_textview, objects));
		int width = anchor.getWidth();
		if (showUp) {
			ll.setGravity(Gravity.BOTTOM);//让listview和底部对齐
			popupWindow = showAtLocation(ll, width + 2 * c, location[1] - statusBarHeight, Gravity.NO_GRAVITY, anchor.getLeft() - c, 0, -1);//上面显示
		} else {
			popupWindow = showAsDropDown(anchor, ll, width + c * 2, parentHeight - childTop - anchor.getHeight() - 2 * c, -c, 0, -1);//view下面显示
		}
		return popupWindow;
	}

	public interface ShowDropListMenuOnClick {
		public void OnClick(List<View> view, List<Integer> rli);
	}

	private static PopupWindow popupWindow;
}
