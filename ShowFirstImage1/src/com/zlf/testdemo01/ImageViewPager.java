package com.zlf.testdemo01;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ImageViewPager extends ViewPager{

	private static final String TAG = "HackyViewPager";
	
	public ImageViewPager(Context context) {
		super(context);
	}
	public ImageViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException e) {

			Log.e(TAG, "Image viewpager error1! IllegalArgumentException.");
			return false;
		} catch (ArrayIndexOutOfBoundsException e) {

			Log.e(TAG, "Image viewpager error2! ArrayIndexOutOfBoundsException.");
			return false;
		}
	}
}
