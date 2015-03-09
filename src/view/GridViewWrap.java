package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/**
 * 适配在ScrollView中的GrideView
 * 
 * @author JackWu
 * @version
 * @date 2014-10-29
 * @_QQ_ 651319154
 */
public class GridViewWrap extends GridView {
	private View child;

	public GridViewWrap(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GridViewWrap(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GridViewWrap(Context context) {
		super(context);
	}

	/**
	 * 解决ScrollView与GrideView的嵌套问题,修改前：scrollview占不满全屏是gridview为warpcontent。修改后：
	 * 占不满全屏是gridview填充剩余的部分，超过全屏是gridview显示全部 change_by_dsr
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, 0x8fffffff);
	}

}
