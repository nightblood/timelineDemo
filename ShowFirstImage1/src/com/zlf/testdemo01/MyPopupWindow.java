package com.zlf.testdemo01;

import java.util.Date;

import android.view.View;
import android.widget.PopupWindow;

public class MyPopupWindow extends PopupWindow {

	private Date missTime = null;
	public MyPopupWindow(View contentView, int width, int height) {
		super(contentView, width, height);
	}
	@Override
	public void dismiss() {
		missTime = new Date(System.currentTimeMillis());
		super.dismiss();
	}
	
	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
		if (missTime != null) {
			Date currTime = new Date(System.currentTimeMillis());
			if (currTime.getTime() - missTime.getTime() > 180.0) {
				super.showAsDropDown(anchor, xoff, yoff, gravity);
			}
			return;
		}
		super.showAsDropDown(anchor, xoff, yoff, gravity);
	}
}
