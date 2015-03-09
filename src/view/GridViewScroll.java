package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ScrollView;

/**
 * 适配在ScrollView中的GrideView
 * 
 * @author JackWu
 * @version
 * @date 2014-10-29
 * @_QQ_ 651319154
 */
public class GridViewScroll extends GridView {

	public GridViewScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GridViewScroll(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GridViewScroll(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		ViewGroup view = (ViewGroup) getParent();
		while (!(view instanceof ScrollView)) {
			view = (ViewGroup) view.getParent();
		}
		view.requestDisallowInterceptTouchEvent(true);
		return super.onInterceptTouchEvent(ev);
	}
}
