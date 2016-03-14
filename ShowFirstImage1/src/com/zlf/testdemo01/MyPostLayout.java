package com.zlf.testdemo01;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class MyPostLayout extends RelativeLayout{

	private static boolean bStartupFlag = false;
	public MyPostLayout(Context context) {
		super(context);
	}
//	private static int count = 0; 
    
    public MyPostLayout(Context context, AttributeSet attrs) { 
        super(context, attrs); 
    } 
     
    @Override 
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {     
        super.onSizeChanged(w, h, oldw, oldh); 
        if (bStartupFlag == false){
        	bStartupFlag = true;
        	return;
        }
        if (PostActivity.bScrolling == true) {
        	// 滑动中，有main activity自己设置bInputVisiable的值
        	return;
        }
        if (h > oldh) {
        	if (PostActivity.bInputVisiable == false) {
        		Log.e("onSizeChanged", "Error bInputVisiable!!!!!!!!!!!!!!!!!!" + PostActivity.bInputVisiable);
        	}
        	
        	// 隐藏输入法后必须隐藏表情页
        	Message msg = new Message();
        	msg.what = 3;
        	PostActivity.editTextHandler.sendMessage(msg);
        	Log.e("onSizeChanged", "bInputInVisiable set false");
        } else if (h < oldh){
        	if (PostActivity.bInputVisiable == true) {
        		Log.e("onSizeChanged", "Error bInputVisiable!!!!!!!!!!!!!!!!!!" + PostActivity.bInputVisiable);
        	}
        	PostActivity.bInputVisiable = true;
        	Log.e("onSizeChanged", "bInputInVisiable set true");
        }
//        Log.e("onSizeChanged " + count++, "=>onResize called! w="+w + ",h="+h+",oldw="+oldw+",oldh="+oldh); 
    } 
     
    @Override 
    protected void onLayout(boolean changed, int l, int t, int r, int b) { 
        super.onLayout(changed, l, t, r, b); 
//        Log.e("onLayout " + count++, "=>OnLayout called! l=" + l + ", t=" + t + ",r=" + r + ",b="+b); 
    } 
     
    @Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
        super.onMeasure(widthMeasureSpec, heightMeasureSpec); 
         
//        Log.e("onMeasure " + count++, "=>onMeasure called! widthMeasureSpec=" + widthMeasureSpec + ", heightMeasureSpec=" + heightMeasureSpec); 
    } 
    
    
   
}
