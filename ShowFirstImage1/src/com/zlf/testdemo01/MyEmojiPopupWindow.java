package com.zlf.testdemo01;

import java.util.Date;

import android.os.Message;
import android.view.View;
import android.widget.PopupWindow;

public class MyEmojiPopupWindow extends PopupWindow {

	private Date missTime = null;
	public MyEmojiPopupWindow(View contentView, int width, int height) {
		super(contentView, width, height);
	}
	@Override
	public void dismiss() {
		missTime = new Date(System.currentTimeMillis());
		Message msg = new Message();
		msg.what = 2;
		PostActivity.editTextHandler.sendMessage(msg);
		super.dismiss();
	}
	
	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
		if (missTime != null) {
			Date currTime = new Date(System.currentTimeMillis());
			if (currTime.getTime() - missTime.getTime() > 180.0) {
				Message msg = new Message();
				msg.what = 1;
				PostActivity.editTextHandler.sendMessage(msg);
				super.showAsDropDown(anchor, xoff, yoff, gravity);
			}
			return;
		}
		Message msg = new Message();
		msg.what = 1;
		PostActivity.editTextHandler.sendMessage(msg);
		super.showAsDropDown(anchor, xoff, yoff, gravity);
	}
}
