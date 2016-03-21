package com.zlf.testdemo01.domain;

import com.zlf.testdemo01.MyPostLayout;
import com.zlf.testdemo01.MyPostLayout.OnSildingFinishListener;
import com.zlf.testdemo01.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 1. 必须设置透明风格,设置透明后需对Activity设置一个背景色，其遮盖效果
 * 2. 要调用initBaseActivity(boolean enableLeftSilde, boolean enableRigftSilde)
 * 
 * @ enableLeftSilde : 开启左侧
 * @ enableRightSilde : 开启右侧
 * 
 * */
public class BasePostActivity extends Activity implements OnSildingFinishListener{
	private MyPostLayout myLayout;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * @enableLeftSilde : 开启左侧, 向右滑动退出
 	 * @enableRightSilde : 开启右侧, 向左滑动退出
 	 * */
	public void initBaseActivity(boolean enableLeftSilde, boolean enableRigftSilde){
		myLayout = (MyPostLayout) findViewById(R.id.slidingLayout);
		myLayout.setOnSildingFinishListener(this);
		myLayout.setEnableLeftSildeEvent(enableLeftSilde);
		myLayout.setEnableRightSildeEvent(enableRigftSilde);
	}


	@Override
	public void onSildingBack() {
		Toast.makeText(getApplicationContext(), "back", Toast.LENGTH_SHORT).show();
		finish();
	}


	@Override
	public void onSildingForward() {
		Toast.makeText(getApplicationContext(), "前进", Toast.LENGTH_SHORT).show();
		finish();
	}
}
