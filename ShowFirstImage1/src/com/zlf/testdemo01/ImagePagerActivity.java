package com.zlf.testdemo01;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TextView;

public class ImagePagerActivity extends FragmentActivity{
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index"; 
	public static final String EXTRA_IMAGE_URLS = "image_urls";
	
	private int pagerPosition;
	private ArrayList<String> urls;
	private ImageLoader imageLoader;
	private TextView indicator;
	private ImageViewPager pager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_pager);
		
		ImagePagerAdapter adapter;
		
		pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		urls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
		
//		System.out.println("µã»÷Í¼Æ¬²é¿´..." + pagerPosition + " " + urls);
		
		pager = (ImageViewPager) findViewById(R.id.pager);
		indicator = (TextView) findViewById(R.id.indicator);
		
		adapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
		
		pager.setAdapter(adapter);
		
		String text = getString(R.string.viewpager_indicator, 1, pager.getAdapter().getCount());
		indicator.setText(text);
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				String text = getString(R.string.viewpager_indicator, arg0 + 1, pager.getAdapter().getCount());
				indicator.setText(text);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}
		pager.setCurrentItem(pagerPosition);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}
}
