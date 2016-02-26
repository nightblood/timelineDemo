package com.zlf.testdemo01;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {

	public ArrayList<String> datas;
	public ImagePagerAdapter(FragmentManager fm, ArrayList<String> list) {
		super(fm);
		datas = list;
	}

	@Override
	public Fragment getItem(int arg0) {
		String url = datas.get(arg0);
		return ImageDetailFragment.newInstance(url);
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}
}
