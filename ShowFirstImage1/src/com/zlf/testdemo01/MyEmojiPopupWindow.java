package com.zlf.testdemo01;

import java.util.Date;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.PopupWindow;

public class MyEmojiPopupWindow extends PopupWindow {

	private Date missTime = null;
	private Handler handler;
	public MyEmojiPopupWindow(View contentView, int width, int height, Handler handler) {
		super(contentView, width, height);
		this.handler = handler;
	}
	@Override
	public void dismiss() {
		missTime = new Date(System.currentTimeMillis());
		Message msg = new Message();
		msg.what = 2;
		handler.sendMessage(msg);
		super.dismiss();
	}
	
	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
		if (missTime != null) {
			Date currTime = new Date(System.currentTimeMillis());
			if (currTime.getTime() - missTime.getTime() > 180.0) {
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
				super.showAsDropDown(anchor, xoff, yoff, gravity);
			}
			return;
		}
		Message msg = new Message();
		msg.what = 1;
		handler.sendMessage(msg);
		super.showAsDropDown(anchor, xoff, yoff, gravity);
	}
	
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		if (missTime != null) {
			Date currTime = new Date(System.currentTimeMillis());
			if (currTime.getTime() - missTime.getTime() > 180.0) {
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
				super.showAtLocation(parent, gravity, x, y);
			}
			return;
		}
		Message msg = new Message();
		msg.what = 1;
		handler.sendMessage(msg);
		super.showAtLocation(parent, gravity, x, y);
	}
}
