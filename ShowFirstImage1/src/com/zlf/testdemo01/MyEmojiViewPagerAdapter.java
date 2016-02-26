package com.zlf.testdemo01;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class MyEmojiViewPagerAdapter extends PagerAdapter {

	List<View> views;
	public MyEmojiViewPagerAdapter(List<View> views) {
		super();
		this.views = views;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return views.size();
	}

	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position));
	}

	@Override
    public Object instantiateItem(View arg0, int arg1) {
		System.out.println("instantiateItem " + arg1 + " view count:" + getCount());
        ((ViewPager)arg0).addView(views.get(arg1));
        return views.get(arg1);
    }

}
