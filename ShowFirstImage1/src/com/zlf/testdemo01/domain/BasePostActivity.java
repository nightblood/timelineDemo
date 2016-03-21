package com.zlf.testdemo01.domain;

import com.zlf.testdemo01.MyPostLayout;
import com.zlf.testdemo01.MyPostLayout.OnSildingFinishListener;
import com.zlf.testdemo01.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 1. ��������͸�����,����͸�������Activity����һ������ɫ�����ڸ�Ч��
 * 2. Ҫ����initBaseActivity(boolean enableLeftSilde, boolean enableRigftSilde)
 * 
 * @ enableLeftSilde : �������
 * @ enableRightSilde : �����Ҳ�
 * 
 * */
public class BasePostActivity extends Activity implements OnSildingFinishListener{
	private MyPostLayout myLayout;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * @enableLeftSilde : �������, ���һ����˳�
 	 * @enableRightSilde : �����Ҳ�, ���󻬶��˳�
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
		Toast.makeText(getApplicationContext(), "ǰ��", Toast.LENGTH_SHORT).show();
		finish();
	}
}
