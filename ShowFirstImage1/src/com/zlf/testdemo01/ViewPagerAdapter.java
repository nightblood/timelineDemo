package com.zlf.testdemo01;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter{

	private Context mContext;

	private ArrayList<View> mViewItems;

	private View convertView;

	public ViewPagerAdapter(Context context, ArrayList<View> viewItems) {
		mContext = context;
		mViewItems = viewItems;
	}
	
	@Override
	public int getCount() {
		if (mViewItems == null || mViewItems.size() == 0)
			return 1;
		return mViewItems.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mViewItems.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		convertView = mViewItems.get(position);
		container.addView(mViewItems.get(position));
		return mViewItems.get(position);
	}

}
