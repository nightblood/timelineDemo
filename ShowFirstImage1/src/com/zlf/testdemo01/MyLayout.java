package com.zlf.testdemo01;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

public class MyLayout extends RelativeLayout{

	private static boolean bStartupFlag = false;
	Boolean bInputVisiable = false;
	public MyLayout(Context context) {
		super(context);
	}
	private static int count = 0; 
    
    public MyLayout(Context context, AttributeSet attrs) { 
        super(context, attrs); 
    } 
     
    @Override 
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {     
        super.onSizeChanged(w, h, oldw, oldh); 
        if (bStartupFlag == false){
        	bStartupFlag = true;
        	return;
        }
        if (MainActivity.bScrolling == true) {
        	// �����У���main activity�Լ�����bInputVisiable��ֵ
        	return;
        }
        if (h > oldh) {
        	if (MainActivity.bInputVisiable == false) {
        		Log.e("onSizeChanged", "Error bInputVisiable!!!!!!!!!!!!!!!!!!" + MainActivity.bInputVisiable);
        	}

//        	MainActivity.bInputVisiable = false;
//        	// �������뷨�������ʾ�ײ�������,�������ر༭��
//        	Message msg = new Message();
//        	msg.what = 3;
//        	MainActivity.editTextHandler.sendMessage(msg);
//        	Log.e("onSizeChanged", "bInputInVisiable set false");
        	
        	bInputVisiable = false;
        	onChangeLayoutListener.layoutChanged(bInputVisiable); //bInputVisiable true; �������뷨�������ʾ�ײ�������,�������ر༭��
        } else if (h < oldh){
        	if (MainActivity.bInputVisiable == true) {
        		Log.e("onSizeChanged", "Error bInputVisiable!!!!!!!!!!!!!!!!!!" + MainActivity.bInputVisiable);
        	}
        	
        	bInputVisiable = true;
        	onChangeLayoutListener.layoutChanged(bInputVisiable); //bInputVisiable false
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
    
    private OnLayoutChangedListener onChangeLayoutListener;

	public void setOnChangeLayoutListener(OnLayoutChangedListener onChangeLayoutListener) {
		if (onChangeLayoutListener == null) {
			return;
		}
		this.onChangeLayoutListener = onChangeLayoutListener;
	} 
    
    
   
}
