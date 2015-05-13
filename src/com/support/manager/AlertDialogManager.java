package com.support.manager;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.support_libs.R;

public class AlertDialogManager {

	private final View view;
	private final View mBody;
	private final TextView mTitle;
	private final View mTitleLine;
	private final View mBottom;
	private final TextView mBodyContent;
	private final LinearLayout mBodyList;
	private final Context context;
	private final View mBodyLine;
	private final TextView mConfirm;
	private final TextView mCancel;
	private final View mBottomLine;
	private final AlertDialog dialog;
	private final TextView mSubContent;
	private final View mEditRL;
	private final EditText mEdit;
	private final TextView mEditUnit;

	public AlertDialogManager(Context context) {
		this.context = context;
		dialog = new AlertDialog.Builder(context).create();
		view = View.inflate(context, R.layout.dialog_common, null);
		mBody = view.findViewById(R.id.alertdialog_body);
		mTitle = (TextView) view.findViewById(R.id.alertdialog_title);
		mTitleLine = view.findViewById(R.id.alertdialog_title_line);
		mBodyContent = (TextView) view.findViewById(R.id.alertdialog_body_content);
		mBottom = view.findViewById(R.id.alertdialog_bottom);
		mBottomLine = view.findViewById(R.id.alertdialog_bottom_line);
		mBodyLine = view.findViewById(R.id.alertdialog_body_line);
		mBodyList = (LinearLayout) view.findViewById(R.id.alertdialog_body_list);
		mCancel = (TextView) view.findViewById(R.id.alertdialog_cancel);
		mConfirm = (TextView) view.findViewById(R.id.alertdialog_confirm);
		mEditRL = view.findViewById(R.id.alertdialog_body_edit_rl);
		mEdit = (EditText) view.findViewById(R.id.alertdialog_body_edit);
		mEditUnit = (TextView) view.findViewById(R.id.alertdialog_body_unit);
		mSubContent = (TextView) view.findViewById(R.id.alertdialog_body_subcontent);
		dialog.show();
		dialog.setContentView(view);
	}

	/**
	 * 对传过来的文字判空，不为空显示，为空gone
	 * 
	 * @param title
	 */
	public AlertDialogManager setTitle(CharSequence title) {
		if (title == null) {
			mTitle.setVisibility(View.GONE);
			mTitleLine.setVisibility(View.GONE);
		} else {
			mTitle.setText(title);
			mTitle.setVisibility(View.VISIBLE);
			mTitleLine.setVisibility(View.VISIBLE);
		}
		return this;
	}

	/**
	 * 根据ss是不是null显隐body部分，根据ss的个数显示content还是多个条目
	 * 
	 * @param ss
	 * @param listener
	 * @return
	 */
	public AlertDialogManager setBody(CharSequence[] ss, OnClickListener... listener) {
		if (ss == null || ss.length == 0) {
			mBottomLine.setVisibility(View.GONE);
			return this;
		}
		mBody.setVisibility(View.VISIBLE);
		if (ss.length == 1) {
			mBodyList.setVisibility(View.GONE);
			mBodyContent.setText(ss[0]);
		} else {
			mBodyLine.setVisibility(View.GONE);
			mBodyContent.setVisibility(View.GONE);
			for (int i = 0; i < ss.length; i++) {
				View inflate = View.inflate(context, R.layout.dialog_common_item, null);
				TextView itemText = (TextView) inflate.findViewById(R.id.alertdialog_body_item_text);
				if (listener != null && listener.length > i && listener[i] != null) {
					inflate.setOnClickListener(listener[i]);
				}
				itemText.setText(ss[i]);
				mBodyList.addView(inflate);
			}
		}
		return this;
	}

	public AlertDialogManager setConfirm(CharSequence ss, OnClickListener listener) {
		if (TextUtils.isEmpty(ss)) {
			mConfirm.setVisibility(View.GONE);
			mBottomLine.setVisibility(View.GONE);
			return this;
		}
		mBottom.setVisibility(View.VISIBLE);
		mConfirm.setText(ss);
		mConfirm.setOnClickListener(listener);
		return this;
	}

	public AlertDialogManager setCancel(CharSequence ss, OnClickListener listener) {
		if (TextUtils.isEmpty(ss)) {
			mCancel.setVisibility(View.GONE);
			mBottomLine.setVisibility(View.GONE);
			return this;
		}
		mBottom.setVisibility(View.VISIBLE);
		mCancel.setText(ss);
		mCancel.setOnClickListener(listener);
		return this;
	}

	public AlertDialogManager setOnBodyClickListener(OnClickListener l) {
		mBody.setOnClickListener(l);
		return this;
	}

	public AlertDialogManager setOnTitleClickListener(OnClickListener l) {
		mTitle.setOnClickListener(l);
		return this;
	}

	public AlertDialogManager setOnBottomClickListener(OnClickListener l) {
		mBottom.setOnClickListener(l);
		return this;
	}

	public AlertDialog getDialog() {
		return dialog;
	}

	/**
	 * 如果body中有两种文字样式或者是一个输入的文本框使用这个方法传一个需要显示的enum
	 * 
	 * @param type
	 * @param hint
	 * @return
	 */
	public EditText setOtherBody(Type type, CharSequence... hint) {
		mBody.setVisibility(View.VISIBLE);
		switch (type) {
		case EDITTEXT:
			dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			mEditRL.setVisibility(View.VISIBLE);
			mEditUnit.setText(hint[0]);
			break;

		case SUBCONTENT:
			mSubContent.setVisibility(View.VISIBLE);
			mSubContent.setText(hint[0]);
			break;
		}
		return mEdit;
	}

	/**
	 * 返回在edittext下面提示的view，
	 * 
	 * @param i
	 *            1，是灰色的。2，是红色的
	 * @return
	 */
	public TextView getTip(int i) {
		switch (i) {
		case 1:
			return (TextView) mBody.findViewById(R.id.tip_gray);
		case 2:
			return (TextView) mBody.findViewById(R.id.tip_red);
		}
		return null;
	}

	public enum Type {
		EDITTEXT, SUBCONTENT
	}
}
