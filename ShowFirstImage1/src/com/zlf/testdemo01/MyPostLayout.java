package com.zlf.testdemo01;

import com.zlf.testdemo01.MyLayout.OnSildingFinishListener;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class MyPostLayout extends RelativeLayout{
	
private final String TAG = MyLayout.class.getName();  
    
    /** 
     * SildingFinishLayout���ֵĸ����� 
     */  
    private ViewGroup mParentView;  
      
    /** 
     * ��������С���� 
     */  
    private int mTouchSlop;  
    /** 
     * ���µ��X���� 
     */  
    private int downX;  
    /** 
     * ���µ��Y���� 
     */  
    private int downY;  
    /** 
     * ��ʱ�洢X���� 
     */  
    private int tempX;  
    /** 
     * ������ 
     */  
    private Scroller mScroller;  
    /** 
     * SildingFinishLayout�Ŀ�� 
     */  
    private int viewWidth;  
    /** 
     * ��¼�Ƿ����ڻ��� 
     */  
    private boolean isSilding;  
      
    private OnSildingFinishListener onSildingFinishListener;  
      
    private boolean enableLeftSildeEvent = true; //�Ƿ�������л��¼�  
    private boolean enableRightSildeEvent = true; // �Ƿ����Ҳ��л��¼�  
    private final int size = 20; //����ʱ��Χ(���������Χ�ھ������л��¼���Ŀ����ʹ���û������ұ߽���ʱ����Ӧ)  
    private boolean isIntercept = false; //�Ƿ����ش����¼�  
    private boolean canSwitch;//�Ƿ���л�  
    private boolean isSwitchFromLeft = false; //����л�  
    private boolean isSwitchFromRight = false; //�Ҳ���л�  

	private Context context;
	private static boolean bStartupFlag = false;
	
	public MyPostLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyPostLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		Log.e(TAG, "�豸����С��������:" + mTouchSlop);
		mScroller = new Scroller(context);
	}

    @Override 
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {     
        super.onSizeChanged(w, h, oldw, oldh); 
        if (bStartupFlag == false){
        	bStartupFlag = true;
        	return;
        }
        if (PostActivity.bScrolling == true) {
        	// �����У���main activity�Լ�����bInputVisiable��ֵ
        	return;
        }
        if (h > oldh) {
        	if (PostActivity.bInputVisiable == false) {
        		Log.e("onSizeChanged", "Error bInputVisiable!!!!!!!!!!!!!!!!!!" + PostActivity.bInputVisiable);
        	}
        	
        	// �������뷨��������ر���ҳ
        	Message msg = new Message();
        	msg.what = 3;
        	PostActivity.handler.sendMessage(msg);
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
        if (changed) {  
            // ��ȡSildingFinishLayout���ڲ��ֵĸ�����  
            mParentView = (ViewGroup) this.getParent();  
            viewWidth = this.getWidth();  
        } 
    } 
    
    public void setEnableLeftSildeEvent(boolean enableLeftSildeEvent) {  
        this.enableLeftSildeEvent = enableLeftSildeEvent;  
    }  
      
      
    public void setEnableRightSildeEvent(boolean enableRightSildeEvent) {  
        this.enableRightSildeEvent = enableRightSildeEvent;  
    } 
    public void setOnSildingFinishListener(OnSildingFinishListener onSildingFinishListener) {  
        this.onSildingFinishListener = onSildingFinishListener;  
    } 
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
        float downX = ev.getRawX();  
        Log.e(TAG, "downX =" + downX + ",viewWidth=" + viewWidth);  
        if(enableLeftSildeEvent && downX < size){
            Log.e(TAG, "downX ����෶Χ�� ,�����¼�");  
            isIntercept = true;  
            isSwitchFromLeft = true;  
            isSwitchFromRight = false;  
            return true;  
        }else if(enableRightSildeEvent && downX > (viewWidth - size)){  
            Log.e(TAG, "downX ���Ҳ෶Χ�� ,�����¼�");  
            isIntercept = true;  
            isSwitchFromRight = true;  
            isSwitchFromLeft = false;  
            return true;  
        }else{  
            Log.e(TAG, "downX ���ڷ�Χ�� ,�������¼�");  
            isIntercept = false;  
            isSwitchFromLeft = false;  
            isSwitchFromRight = false;  
        }  
        return super.onInterceptTouchEvent(ev);  
    }
     
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
        if(!isIntercept){//�������¼�ʱ ������  
            return false;  
        }  
        switch (event.getAction()){  
        case MotionEvent.ACTION_DOWN:  
            downX = tempX = (int) event.getRawX();  
            downY = (int) event.getRawY();  
            break;  
        case MotionEvent.ACTION_MOVE:  
            int moveX = (int) event.getRawX();  
            int deltaX = tempX - moveX;   
            tempX = moveX;  
            if (Math.abs(moveX - downX) > mTouchSlop && Math.abs((int) event.getRawY() - downY) < mTouchSlop) {  
                isSilding = true;  
            }  
              
            Log.e(TAG, "scroll deltaX=" + deltaX);            
            if(enableLeftSildeEvent){//��໬��  
                if (moveX - downX >= 0 && isSilding) {  
                    mParentView.scrollBy(deltaX, 0);  
                }  
            }  
              
            if(enableRightSildeEvent){//�Ҳ໬��  
                if (moveX - downX <= 0 && isSilding) {  
                    mParentView.scrollBy(deltaX, 0);  
                }  
            }  
              
            Log.e(TAG + "/onTouchEvent", "mParentView.getScrollX()=" + mParentView.getScrollX());  
            break;  
        case MotionEvent.ACTION_UP:  
            isSilding = false;  
            //mParentView.getScrollX() <= -viewWidth / 2  ==>ָ��໬��  
            //mParentView.getScrollX() >= viewWidth / 2   ==>ָ�Ҳ໬��  
            if (mParentView.getScrollX() <= -viewWidth / 2 || mParentView.getScrollX() >= viewWidth / 2) {  
                canSwitch = true;  
                if(isSwitchFromLeft){  
                    scrollToRight();  
                }  
                  
                if(isSwitchFromRight){  
                    scrollToLeft();  
                }  
            } else {  
                scrollOrigin();  
                canSwitch = false;  
            }  
            break;  
        }  
        return true;  
    }  
      
      
    /** 
     * �������������Ҳ� 
     */  
    private void scrollToRight() {  
        final int delta = (viewWidth + mParentView.getScrollX());  
        // ����startScroll����������һЩ�����Ĳ�����������computeScroll()�����е���scrollTo������item  
        mScroller.startScroll(mParentView.getScrollX(), 0, -delta + 1, 0, Math.abs(delta));  
        postInvalidate();  
    }  
      
    /** 
     * ��������������� 
     */  
    private void scrollToLeft() {  
        final int delta = (viewWidth - mParentView.getScrollX());  
        // ����startScroll����������һЩ�����Ĳ�����������computeScroll()�����е���scrollTo������item  
        mScroller.startScroll(mParentView.getScrollX(), 0, delta - 1, 0, Math.abs(delta));//�˴��Ͳ�����+1��Ҳ����ֱ����delta  
        postInvalidate();  
    }  
  
    /** 
     * ��������ʼλ�� 
     */  
    private void scrollOrigin() {  
        int delta = mParentView.getScrollX();  
        mScroller.startScroll(mParentView.getScrollX(), 0, -delta, 0,  
                Math.abs(delta));  
        postInvalidate();  
    }  
      
      
  
    @Override  
    public void computeScroll(){  
        // ����startScroll��ʱ��scroller.computeScrollOffset()����true��  
        if (mScroller.computeScrollOffset()) {  
            mParentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());  
            postInvalidate();  
  
            if (mScroller.isFinished()) {  
                if (onSildingFinishListener != null && canSwitch) {  
                    Log.e(TAG, "mScroller finish");  
                    if(isSwitchFromLeft){//�ص�������л��¼�  
                        onSildingFinishListener.onSildingBack();  
                    }  
                      
                    if(isSwitchFromRight){//�Ҳ��л��¼�  
                        onSildingFinishListener.onSildingForward();  
                    }  
                }  
            }  
        }  
    }  
      
  
    public interface OnSildingFinishListener {  
        public void onSildingBack();  
        public void onSildingForward();  
    } 
    @Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
        super.onMeasure(widthMeasureSpec, heightMeasureSpec); 
         
//        Log.e("onMeasure " + count++, "=>onMeasure called! widthMeasureSpec=" + widthMeasureSpec + ", heightMeasureSpec=" + heightMeasureSpec); 
    } 
    
    
   
}
