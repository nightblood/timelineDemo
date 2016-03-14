package com.zlf.testdemo01;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class IgnoreSoftKeyboardEditText extends EditText {

	
	public IgnoreSoftKeyboardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public IgnoreSoftKeyboardEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public IgnoreSoftKeyboardEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Message msg = new Message();
			msg.what = 2;
			((Handler) MainActivity.editTextHandler).sendMessage(msg);
		}
		return false;
	}


}
