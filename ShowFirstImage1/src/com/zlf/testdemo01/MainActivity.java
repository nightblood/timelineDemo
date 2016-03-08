package com.zlf.testdemo01;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.zlf.testdemo01.RefreshLayout.OnLoadListener;
import com.zlf.testdemo01.domain.EmotionInfo;
import com.zlf.testdemo01.domain.FriendInfo;
import com.zlf.testdemo01.utils.FileUtils;
import com.zlf.testdemo01.utils.FriendOper;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, SwipeRefreshLayout.OnRefreshListener,
		OnLoadListener, OnGestureListener, OnTouchListener {

	
//	private HttpClient client;
	public static List<FriendInfo> friendList = new ArrayList<FriendInfo>();
	// private List<EmotionInfo> emotionList;
	private ListView listview;
	// private MyAdapter adapter;
	private MyItemAdapter adapter;
	private Button button;
	private Button download;
	public static String localPath;
	public static String emotionPath;
	public static String emotionDataPath;
//	private TextView testText;
	public static boolean flagHasData = false;
	public static String eMotionData;

	private RefreshLayout mSwipeLayout;
//	private PullToRefreshListView mSwipeLayout;
	private static final int REFRESH_COMPLETE = 0X110;
	public static int ON_LOAD = 0;
	public static int ON_REFERSH = 1;
	public static int pageNum = 1;
	public static boolean bInputVisiable = false;
//	private View popupView;
//	private EditText editComment;
	// private ImageView commentBtn;
	private Button commentBtn;
	private static View commentView;
	// private EditText commentEdit;
	private static EditText commentEdit;
	private static InputMethodManager inputManager;
	private GestureDetector dector;
	public static boolean bScrolling = false;
	public static View commentListView = null;
	private MyPopupWindow window = null;
//	private static boolean popupIsShowing = false;
//	private Date preTime = null;
//	private Date currTime = null;
	private static Date backTime = null;
	
	private FriendOper friendUtils = null;
	

	public static Handler editTextHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 2) {
				// 处理点击返回键事件，隐藏EditText 和虚拟键盘
				if (commentView.getVisibility() == View.VISIBLE) {
					commentView.setVisibility(View.INVISIBLE);
					backTime = new Date(System.currentTimeMillis());
				}

//				if (bInputVisiable == true)
//					inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			}
		}
	};
	
	

	private Handler refreshHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_COMPLETE:
				// 重新获取服务上朋友圈信息
				pageNum = 1;
				friendUtils.getTimeLineData(ON_REFERSH);
				break;
			}
		};
	};
	private View header;
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				// 处理点击发表评论按钮事务
				if (commentView.getVisibility() != View.VISIBLE) {
					commentView.setVisibility(View.VISIBLE);

					// 设置焦点
					commentEdit.setFocusable(true);
					commentEdit.setFocusableInTouchMode(true);
					commentEdit.requestFocus();

					if (bInputVisiable == false) {
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {
							public void run() {
								// 显示虚拟键盘输入法
								inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
							}
						}, 50);
					}
					// 调整 listview中item的位置，使其在编辑框的上方。
				} else {
					// 点击评论按钮已经显示编辑区的情况下，隐藏评论编辑区并且隐藏虚拟键盘
					commentView.setVisibility(View.INVISIBLE);
					if (bInputVisiable == true)
						inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				}
			
				break;
			case 2:
				// 处理点击返回键事件，隐藏EditText 和虚拟键盘
				if (commentView.getVisibility() == View.VISIBLE)
					commentView.setVisibility(View.INVISIBLE);

				if (bInputVisiable == true)
					inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			
				break;
			case 3:
				View view = (View) msg.obj;
				if (view.getVisibility() == View.VISIBLE) {
					view.setVisibility(View.INVISIBLE);
				}
			
				break;
			case 4:
				showPopupWindow(msg);
				
				break;
			case 5:
				mSwipeLayout.setLoading(false);
			
				break;
			case 6:
				mSwipeLayout.setRefreshing(false);
				break;
			case 7:
				adapter.notifyDataSetChanged();
				break;

			}
		}

	};
	private TextView praiseText = null;
	private Button praiseBtn = null;
	private FriendInfo friend = null;

	private void showPopupWindow(Message msg) {

		friend = friendList.get(msg.arg1);
		if (window == null) {
			View view = View.inflate(MainActivity.this, R.layout.popup_window_comment, null);
			praiseText = (TextView) view.findViewById(R.id.praise_text);
			praiseBtn = (Button) view.findViewById(R.id.praise_button);
			Button commentBtn = (Button) view.findViewById(R.id.comment_button);

			praiseBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Vibrator vibrator = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(50);

					String praise = praiseText.getText().toString();
					if (praise.equals("赞")) {
						friend.setPraiseFlag(true);
						praiseText.setText("取消");
						praiseBtn.setBackground(MainActivity.this.getDrawable(R.drawable.red_heart));
						Toast.makeText(MainActivity.this, "已点赞", Toast.LENGTH_SHORT).show();
					} else {
						friend.setPraiseFlag(false);
						praiseText.setText("赞");
						praiseBtn.setBackground(MainActivity.this.getDrawable(R.drawable.icon_like));
						Toast.makeText(MainActivity.this, "已取消点赞", Toast.LENGTH_SHORT).show();
					}
					window.dismiss();
				}
			});

			commentBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
					window.dismiss();
				}
			});

			window = new MyPopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			// 需要设置背景，用物理键返回的时候
			window.setBackgroundDrawable(new BitmapDrawable(MainActivity.this.getResources()));

			window.setFocusable(false);
			window.setTouchable(true);
			window.setOutsideTouchable(true);

			view.setOnTouchListener(new OnTouchListener() {// 需要设置，点击之后取消popupview，即使点击外面，也可以捕获事件
															// {
				public boolean onTouch(View v, MotionEvent event) {
					if (window.isShowing()) {
						window.dismiss();
					}
					return false;
				}
			});
		}
		if (window.isShowing()) {
			window.dismiss();
		} else {
			if (friend.isbPraiseFlag()) {
				praiseText.setText("取消");
				praiseBtn.setBackgroundResource(R.drawable.red_heart);
			} else {
				praiseText.setText("赞");
				praiseBtn.setBackgroundResource(R.drawable.icon_like);
			}
			window.showAsDropDown((View) msg.obj, -600, -100);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		header = View.inflate(this, R.layout.listview_header, null);
		initView();

		//header.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		listview.addHeaderView(header);
		
		inputManager = (InputMethodManager) commentEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setOnLoadListener(this);

		localPath = getApplicationContext().getFilesDir().getAbsolutePath();
		emotionPath = localPath + "/emotion.zip";
		emotionDataPath = localPath + "/emotion.txt";

		button.setOnClickListener(this);
		download.setOnClickListener(this);

		// adapter = new MyAdapter(friendList, this);
		adapter = new MyItemAdapter(handler, friendList, this);
		listview.setAdapter((ListAdapter) adapter);

		dector = new GestureDetector(this);
		listview.setOnTouchListener(this);
		
		friendUtils = new FriendOper(this, handler, adapter);

		EmotionInfo.emotionPath = localPath + "/emoticon/";
		File emotionDataFile = new File(emotionDataPath);
		if (emotionDataFile.exists()) {
			// 解析表情包数据
			String emotionData = FileUtils.readFileByChars(localPath + "/emotion.txt");
			friendUtils.getEmotionList(emotionData);
		}

		/*************** 获取服务端数据 *************************/
		friendUtils.getTimeLineData(ON_REFERSH);
		System.out.println("onCreate end!");
	}

	private void initView() {
		commentView = findViewById(R.id.comment_view);
//		commentEdit = (IgnoreSoftKeyboardEditText) findViewById(R.id.comment_edit);
		commentEdit = (EditText) findViewById(R.id.comment_edit);
		commentBtn = (Button) findViewById(R.id.comment_send);
		mSwipeLayout = (RefreshLayout) findViewById(R.id.id_swipe_ly);
//		mSwipeLayout = (PullToRefreshListView) findViewById(R.id.id_swipe_ly);
		listview = (ListView) findViewById(R.id.lv);
		button = (Button) header.findViewById(R.id.button1);
		download = (Button) header.findViewById(R.id.download_btn);
	}

	public void onRefresh() {
		pageNum = 1;
		friendUtils.getTimeLineData(ON_REFERSH);
	}
	@Override
	public void onLoad() {
		// 读取下一页朋友圈数据
		friendUtils.getTimeLineData(ON_LOAD);
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			Intent it = new Intent();
			it.setClass(MainActivity.this, PostActivity.class);

			startActivityForResult(it, 100);
			break;
		case R.id.download_btn:
			if (new File(emotionPath).exists()) {
				Toast.makeText(MainActivity.this, "表情包已下载!!", Toast.LENGTH_LONG).show();
			} else {
				friendUtils.downloadEmotionImages();
				friendUtils.readNet(FriendOper.EMOTION_URL);
			}
			break;
		default:
			break;
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP) {
//			System.out.println("----------------action up--------------");
			bScrolling = false;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	/*
	 * MotionEvent, android.view.MotionEvent, float, float) 滑动时，隐藏评论编辑区和输入法
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		System.out.println("`````````````onFling`````````````````" + bInputVisiable);
		bScrolling = false;
		return false;
	}

	/*
	 * MotionEvent, android.view.MotionEvent, float, float) 滑动时，隐藏评论编辑区和输入法
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		System.out.println("`````````````onScroll`````````````````");
		// if (commentListView != null && commentListView.getVisibility() ==
		// View.VISIBLE) {
		// commentListView.setVisibility(View.INVISIBLE);
		// }
		if (commentView.getVisibility() == View.VISIBLE) {
			commentView.setVisibility(View.INVISIBLE);
		}
		if (bInputVisiable == true && bScrolling != true) {
			inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			bScrolling = false;
			bInputVisiable = false;
		}
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		dector.onTouchEvent(event);
		return false;
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	@Override
	protected void onStart() {

		super.onStart();
	}

	@Override
	protected void onStop() {
		pageNum = 1;
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (commentView.getVisibility() == View.VISIBLE) {
				commentView.setVisibility(View.INVISIBLE);
//				System.out.println("11111111111111111111111111111111111111");
				return true;
			}
			if (backTime != null) {
				Date time = new Date(System.currentTimeMillis());
				
				if (time.getTime() - backTime.getTime() < 100) {
					return true;
				}				
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
