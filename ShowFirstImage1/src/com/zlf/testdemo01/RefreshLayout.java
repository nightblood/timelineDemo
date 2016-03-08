package com.zlf.testdemo01;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;


/**
 * 下拉刷新，下拉加载
 * @author Administrator
 *
 */
public class RefreshLayout extends SwipeRefreshLayout implements OnScrollListener {

	private int mTouchSlop;
	private ListView mListView;
	private OnLoadListener mOnLoadListener;
	private View mListViewFooter;
	private int mYDown;
	private int mLastY;
	private boolean isLoading = false;

	public RefreshLayout(Context context) {
		this(context, null);
	}

	@SuppressLint("InflateParams")
	public RefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null, false);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (mListView == null) {
			getListView();
		}
	}

	private void getListView() {
		int childs = getChildCount();
		if (childs > 0) {
			View childView = getChildAt(0);
			if (childView instanceof ListView) {
				mListView = (ListView) childView;
				mListView.setOnScrollListener(this);
				Log.d(VIEW_LOG_TAG, "### 鎵惧埌listview");
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mYDown = (int) event.getRawY();
			break;

		case MotionEvent.ACTION_MOVE:
			mLastY = (int) event.getRawY();
			break;

		case MotionEvent.ACTION_UP:
			if (canLoad()) {
				loadData();
			}
			break;
		default:
			break;
		}

		return super.dispatchTouchEvent(event);
	}

	private boolean canLoad() {
		return isBottom() && !isLoading && isPullUp();
	}

	private boolean isBottom() {

		if (mListView != null && mListView.getAdapter() != null) {
			return mListView.getLastVisiblePosition() == 
					(mListView.getAdapter().getCount() - 1);
		}
		return false;
	}

	private boolean isPullUp() {
		return (mYDown - mLastY) >= mTouchSlop;
	}

	private void loadData() {
		if (mOnLoadListener != null) {
			setLoading(true);
			mOnLoadListener.onLoad();
		}
	}

	public void setLoading(boolean loading) {
		isLoading = loading;
		if (isLoading) {
			mListView.addFooterView(mListViewFooter);
		} else {
			mListView.removeFooterView(mListViewFooter);
			mYDown = 0;
			mLastY = 0;
		}
	}

	/**
	 * @param loadListener
	 */
	public void setOnLoadListener(OnLoadListener loadListener) {
		mOnLoadListener = loadListener;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (canLoad()) {
			loadData();
		}
	}
	
	public static void setRefreshing(SwipeRefreshLayout refreshLayout,
			boolean refreshing, boolean notify) {
		Class<? extends SwipeRefreshLayout> refreshLayoutClass = refreshLayout.getClass();
		if (refreshLayoutClass != null) {
			try {
				Method setRefreshing = refreshLayoutClass.getDeclaredMethod(
						"setRefreshing", boolean.class, boolean.class);
				setRefreshing.setAccessible(true);
				setRefreshing.invoke(refreshLayout, refreshing, notify);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}

	public static interface OnLoadListener {
		public void onLoad();
	}
}