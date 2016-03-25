package com.zlf.testdemo01;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.zlf.testdemo01.RefreshLayout.OnLoadListener;
import com.zlf.testdemo01.domain.ChatContentEntity;
import com.zlf.testdemo01.domain.ChatEntity;
import com.zlf.testdemo01.domain.EmojiKeyboard;
import com.zlf.testdemo01.domain.EmotionInfo;
import com.zlf.testdemo01.domain.FriendInfo;
import com.zlf.testdemo01.domain.MyViewHolder;
import com.zlf.testdemo01.utils.DBManager;
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
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
		implements OnClickListener, OnGestureListener, OnTouchListener, OnPageChangeListener, OnItemClickListener {

	
	
	private final static int DELAY_TIME = 200;
	public static List<FriendInfo> friendList = new ArrayList<FriendInfo>();
	private ListView listview;
	private MyItemAdapter adapter;
	private Button button;
	private Button download;
	public static String localPath;
//	public static String emotionPath; // Ñ¹Ëõ°üÂ·¾¶
//	public static String emotionDataPath; // emotion.txt ±íÇé°ü¶ÔÓ¦Êý¾ÝÂ·¾¶
	public static boolean flagHasData = false;
	public static String eMotionData;

	private RefreshLayout mSwipeLayout;
	protected static final int REQUST_CODE_CHAT_ACTIVITY = 0xc0;
	protected static final int HANDLER_CLICK_COMMENT = 0xa0;
	
	public static int ON_LOAD = 0;
	public static int ON_REFERSH = 1;
	public static int pageNum = 1;
	public static boolean bInputVisiable = false;
	private static View commentView;
	private static EditText commentEdit;
	private static InputMethodManager inputManager;
	private GestureDetector dector;
	public static boolean bScrolling = false;
	public static View commentListView = null;
	private MyPopupWindow window = null;
	private static Date backTime = null;

	private FriendOper friendUtils = null;

	private View header;
	private TextView praiseText = null;
	private Button praiseBtn = null;
	private FriendInfo friend = null;
	private ViewPager viewPager;
	private ViewPagerAdapter viewAdapter;
	private Button commitPraiseBtn;
	private BottomViewItem item;
	private ArrayList<View> mViewItems = new ArrayList<View>();
	private final int TIMELINE_ID = 2;
	private boolean onLoadedFlag = false;
	private boolean firstClickFlag = false;
	private static View bottomLayout = null;
	private MyLayout mLayout;

	public static Handler editTextHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:
				// ´¦Àíµã»÷ ·µ»Ø¼ü ÊÂ¼þ£¬Òþ²ØEditText ºÍÐéÄâ¼üÅÌ
				if (commentView.getVisibility() == View.VISIBLE) {
					commentView.setVisibility(View.INVISIBLE);
					delayShowBottomLayout(editTextHandler);
					backTime = new Date(System.currentTimeMillis());
				}
				if (emojiView.getVisibility() == View.VISIBLE) {
					emojiView.setVisibility(View.GONE);
				}
				break;
			case 3:
				commentView.setVisibility(View.INVISIBLE);
				delayShowBottomLayout(editTextHandler);
			}
		}
	};

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_CLICK_COMMENT:
				// ´¦Àíµã»÷·¢±íÆÀÂÛ°´Å¥ÊÂÎñ
				if (commentView.getVisibility() != View.VISIBLE) {
					commentView.setVisibility(View.VISIBLE);
					bottomLayout.setVisibility(View.INVISIBLE);
					// ÉèÖÃ½¹µã
					commentEdit.setFocusable(true);
					commentEdit.setFocusableInTouchMode(true);
					commentEdit.requestFocus();

					if (bInputVisiable == false) {
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {
							public void run() {
								// ÏÔÊ¾ÐéÄâ¼üÅÌÊäÈë·¨
								inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
							}
						}, 50);
					}
					// µ÷Õû listviewÖÐitemµÄÎ»ÖÃ£¬Ê¹ÆäÔÚ±à¼­¿òµÄÉÏ·½¡£
				} else {
					// µã»÷ÆÀÂÛ°´Å¥ÒÑ¾­ÏÔÊ¾±à¼­ÇøµÄÇé¿öÏÂ£¬Òþ²ØÆÀÂÛ±à¼­Çø²¢ÇÒÒþ²ØÐéÄâ¼üÅÌ
					commentView.setVisibility(View.INVISIBLE);
					delayShowBottomLayout(handler);

					if (bInputVisiable == true)
						inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
					if (emojiView.getVisibility() == View.VISIBLE) {
						emojiView.setVisibility(View.GONE);
					}
				}

				break;
			case 2:
				// ´¦Àíµã»÷·µ»Ø¼üÊÂ¼þ£¬Òþ²ØEditText ºÍÐéÄâ¼üÅÌ
				if (commentView.getVisibility() == View.VISIBLE) {
					commentView.setVisibility(View.INVISIBLE);
					delayShowBottomLayout(handler);
				}

				if (bInputVisiable == true)
					inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

				if (emojiView.getVisibility() == View.VISIBLE) {
					emojiView.setVisibility(View.GONE);
				}
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
			case FriendOper.HANDLER_ON_LOAD:
				mSwipeLayout.setLoading(false);

				break;
			case FriendOper.HANDLER_ON_FRESH:
				mSwipeLayout.setRefreshing(false);
				break;
			case FriendOper.HANDLER_ADAPTER_DATA_CHANGED_NOTIFY:
				pageNum++;
				adapter.notifyDataSetChanged();
				break;
			case 8:
				// »¬¶¯Ê±£¬Òþ²ØÆÀÂÛ±à¼­¿ò£¬²¢ÇÒÏÔÊ¾µ×²¿µ¼º½À¸
				commentView.setVisibility(View.INVISIBLE);
				delayShowBottomLayout(handler);
				if (emojiView.getVisibility() == View.VISIBLE) {
					emojiView.setVisibility(View.GONE);
				}
				break;
			case 9:
				// ÒÑÏÔÊ¾ÆÀÂÛ±à¼­¿òÇ°ÌáÏÂ£¬µã»÷ÅóÓÑÈ¦Í¼Æ¬£¬»òÕßÆäËû¿Ø¼þ£¬ÐèÒªÒþ²Ø±à¼­¿ò£¬²¢ÇÒÏÔÊ¾µ×²¿µ¼º½À¸
				if (commentView.getVisibility() == View.VISIBLE) {
					commentView.setVisibility(View.INVISIBLE);
					delayShowBottomLayout(handler);
				}
				if (bInputVisiable == true)
					inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				if (emojiView.getVisibility() == View.VISIBLE) {
					emojiView.setVisibility(View.GONE);
				}
				break;
			case 10:
				if (emojiView.getVisibility() == View.GONE) {
					if (bInputVisiable == true) {
						inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								emojiView.setVisibility(View.VISIBLE);
							}
						}, DELAY_TIME);
					} else {
						emojiView.setVisibility(View.VISIBLE);
					}
				} else {
					emojiView.setVisibility(View.GONE);
				}
				break;
			case 11:
				if (emojiView.getVisibility() == View.VISIBLE) {
					emojiView.setVisibility(View.GONE);
				}
				break;
			case FriendOper.HANGDLE_INIT_VIEW:

				initView();

				initMsgView();

				setTabSelection(0);
				break;
			}
		}
	};

	/**
	 * ÑÓ³ÙÏÔÊ¾µ×²¿µ¼º½À¸
	 */
	private static void delayShowBottomLayout(Handler handler) {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				bottomLayout.setVisibility(View.VISIBLE);
			}
		}, DELAY_TIME);
	}

	private void showPopupWindow(Message msg) {
		friend = friendList.get(msg.arg1);
		final MyViewHolder holder = (MyViewHolder) msg.obj;

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
					if (praise.equals("ÔÞ")) {
						friend.setPraiseFlag(true);
						praiseText.setText("È¡Ïû");
						praiseBtn.setBackgroundResource(R.drawable.red_heart);
						// praiseBtn.setBackground(MainActivity.this.getDrawable(R.drawable.red_heart));
						Toast.makeText(MainActivity.this, "ÒÑµãÔÞ", Toast.LENGTH_SHORT).show();
					} else {
						friend.setPraiseFlag(false);
						praiseText.setText("ÔÞ");
						praiseBtn.setBackgroundResource(R.drawable.icon_like);
						// praiseBtn.setBackground(MainActivity.this.getDrawable(R.drawable.icon_like));
						Toast.makeText(MainActivity.this, "ÒÑÈ¡ÏûµãÔÞ", Toast.LENGTH_SHORT).show();
					}
					adapter.notifyDataSetChanged();
					window.dismiss();
				}
			});

			commentBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.what = HANDLER_CLICK_COMMENT;
					handler.sendMessage(msg);
					window.dismiss();
				}
			});

			window = new MyPopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			window.setAnimationStyle(R.style.anim_popup_comment);

			// ÐèÒªÉèÖÃ±³¾°£¬ÓÃÎïÀí¼ü·µ»ØµÄÊ±ºò
			window.setBackgroundDrawable(new BitmapDrawable(MainActivity.this.getResources()));

			window.setFocusable(false);
			window.setTouchable(true);
			window.setOutsideTouchable(true);

			view.setOnTouchListener(new OnTouchListener() {// ÐèÒªÉèÖÃ£¬µã»÷Ö®ºóÈ¡Ïûpopupview£¬¼´Ê¹µã»÷ÍâÃæ£¬Ò²¿ÉÒÔ²¶»ñÊÂ¼þ
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
				praiseText.setText("È¡Ïû");
				praiseBtn.setBackgroundResource(R.drawable.red_heart);
			} else {
				praiseText.setText("ÔÞ");
				praiseBtn.setBackgroundResource(R.drawable.icon_like);
			}
			if (holder != null)
				window.showAsDropDown((View) (holder).comment, ImageUtils.dip2px(MainActivity.this, -140),
						ImageUtils.dip2px(MainActivity.this, -25));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println("onCreate start!");

		localPath = getApplicationContext().getFilesDir().getAbsolutePath();
		FileUtils.PATH_EMOJI_TXT = localPath + "/emotion.txt";
		FileUtils.PATH_EMOJI_ZIP = localPath + "/emotion.zip";
		FileUtils.DIR_EMOJI_IMAGES = localPath + "/emoticon";
		FileUtils.DIR_APPLICATION = localPath;

		friendUtils = FriendOper.getInstance(this, handler);
		
		if (!new File(FileUtils.PATH_EMOJI_ZIP).exists()) {
			friendUtils.getEmojiZipData();
			System.out.println("emotion.zip ²»´æÔÚ£¡£¡£¡");
		}
		if (!new File(FileUtils.PATH_EMOJI_TXT).exists()) {
			friendUtils.getEmojiTxtData();
			System.out.println("emotion.txt ²»´æÔÚ£¡£¡£¡");
		} else {
			
			friendUtils.getEmotionList();
			initView();

			initMsgView();

			setTabSelection(0);
		}

		System.out.println("onCreate end!");
	}

	private void initView() {
		item = BottomViewItem.getInstance();
		bottomLayout = findViewById(R.id.bottom_layout);

		viewPager = (ViewPager) findViewById(R.id.main_viewpager);
		for (int i = 0; i < item.viewNum; i++) {
			if (i != TIMELINE_ID) {
				mViewItems.add(getLayoutInflater().inflate(item.layouts_id[i], null));
			} else {
				mViewItems.add(getLayoutInflater().inflate(R.layout.listview, null));
			}
		}
		viewAdapter = new ViewPagerAdapter(this, mViewItems);
		viewPager.setAdapter(viewAdapter);
		viewPager.setOnPageChangeListener(this);
		for (int i = 0; i < item.viewNum; i++) {
			item.linears[i] = (LinearLayout) findViewById(item.linears_id[i]);
			item.linears[i].setOnClickListener(this);
			item.images[i] = (ImageView) findViewById(item.images_id[i]);
			item.texts[i] = (TextView) findViewById(item.texts_id[i]);
		}
	}

	private ListView msgListView;
	private List<ChatContentEntity> msgList = new ArrayList<ChatContentEntity>();
	private MyMsgAdapter msgAdapter;
	private Button emojiBtn;
	private EmojiKeyboard emojiKeyBoard;
	private static View emojiView;
	private DBManager dbManager;

	private void initMsgView() {
		View msgView = mViewItems.get(0);

		msgListView = (ListView) msgView.findViewById(R.id.msg_listview);
		msgListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();

				bundle.putString("friendName", "friendName");
				intent.putExtras(bundle);
				intent.setClass(MainActivity.this, ChatActivity.class);

				startActivityForResult(intent, REQUST_CODE_CHAT_ACTIVITY);
				// startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		msgAdapter = new MyMsgAdapter(MainActivity.this, msgList);
		msgListView.setAdapter(msgAdapter);
		getMsgDatas();
		msgAdapter.notifyDataSetChanged();
	}

	private void getMsgDatas() {
		ChatContentEntity chatContent;
		chatContent = new ChatContentEntity();
		dbManager = new DBManager(this);
		ChatEntity query = dbManager.queryLatestMsg("friendName");
		chatContent.latestMsg = query;
		chatContent.friendName = "friendName";
		msgList.add(chatContent);
	}

	private void initTimeLineView() {
		View timeline = mViewItems.get(TIMELINE_ID);
		header = View.inflate(this, R.layout.listview_header, null);
		commentView = timeline.findViewById(R.id.comment_view);
		// commentEdit = (IgnoreSoftKeyboardEditText)
		// findViewById(R.id.comment_edit);
		commentEdit = (EditText) timeline.findViewById(R.id.comment_edit);
		mSwipeLayout = (RefreshLayout) timeline.findViewById(R.id.id_swipe_ly);
		listview = (ListView) timeline.findViewById(R.id.lv);
		button = (Button) header.findViewById(R.id.button1);
		download = (Button) header.findViewById(R.id.download_btn);

		listview.addHeaderView(header);

		commitPraiseBtn = (Button) timeline.findViewById(R.id.comment_send);
		commitPraiseBtn.setOnClickListener(this);

		mLayout = (MyLayout) timeline.findViewById(R.id.timeline_mylayout);
		mLayout.setOnChangeLayoutListener(new OnLayoutChangedListener() {

			@Override
			public void layoutChanged(Boolean bInputVisiable) {
				MainActivity.bInputVisiable = bInputVisiable;

				if (bInputVisiable == true) {
					Message msg = new Message();
					msg.what = 11;
					handler.sendMessage(msg);
				}
			}
		});

		emojiView = timeline.findViewById(R.id.emoji_layout);
		emojiBtn = (Button) timeline.findViewById(R.id.emoji_btn);
		emojiBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = 10;
				handler.sendMessage(msg);
			}
		});

		emojiKeyBoard = new EmojiKeyboard(this, (ViewPager) timeline.findViewById(R.id.emotion_viewpage),
				(LinearLayout) timeline.findViewById(R.id.emoji_cursor));
		emojiKeyBoard.initEmojiView();
	}

	@Override
	public void onClick(View v) {
		int viewPagerId = 5;
		switch (v.getId()) {
		case R.id.comment_send:
			String comment = commentEdit.getText().toString();
			System.out.println("........................" + friend.getId());
			if (comment == null) {
				return;
			} else {
				friend.addCommentContent("¿ª·¢Õß: " + comment);
			}
			adapter.notifyDataSetChanged();
			commentEdit.setText("");
			Message msg = new Message();
			msg.what = 2;
			handler.sendMessage(msg);
			break;
		case R.id.button1:
			Intent it = new Intent();
			it.setClass(MainActivity.this, PostActivity.class);

			startActivityForResult(it, 100);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.download_btn:
			if (new File(FileUtils.PATH_EMOJI_ZIP).exists()) {
				Toast.makeText(MainActivity.this, "±íÇé°üÒÑÏÂÔØ!!", Toast.LENGTH_LONG).show();
			} else {
				friendUtils.getEmojiZipData();
//				friendUtils.readNet(FriendOper.EMOTION_URL);
				friendUtils.getEmojiTxtData();
			}
			break;
		case R.id.message_layout:
			viewPagerId = 0;
			break;
		case R.id.contacts_layout:
			viewPagerId = 1;
			break;
		case R.id.news_layout:
			if (firstClickFlag == true) {
				listview.setSelection(0);
				listview.setSelectionAfterHeaderView();
				listview.smoothScrollToPosition(0);
			}
			viewPagerId = 2;
			break;
		case R.id.setting_layout:
			viewPagerId = 3;
			break;
		default:
			break;
		}
		if (viewPagerId != 5) {
			viewPager.setCurrentItem(viewPagerId);
			setTabSelection(viewPagerId);
			if (viewPagerId != TIMELINE_ID) {
				firstClickFlag = false;
			} else {
				firstClickFlag = true;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP) {
			// System.out.println("----------------action up--------------");
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
	 * »¬¶¯Ê±£¬Òþ²ØÆÀÂÛ±à¼­ÇøºÍÊäÈë·¨
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		System.out.println("`````````````onFling`````````````````" + bInputVisiable);
		bScrolling = false;
		return false;
	}

	/*
	 * »¬¶¯Ê±£¬Òþ²ØÆÀÂÛ±à¼­ÇøºÍÊäÈë·¨
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		System.out.println("`````````````onScroll`````````````````");
		if (commentView.getVisibility() == View.VISIBLE) {
			Message msg = new Message();
			msg.what = 8;
			handler.sendMessage(msg);
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
	protected void onStop() {
		pageNum = 1;
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (commentView != null && commentView.getVisibility() == View.VISIBLE) {
				Message msg = new Message();
				msg.what = 8;
				handler.sendMessage(msg);
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

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int arg0) {
		setTabSelection(arg0);
		if (arg0 == TIMELINE_ID && onLoadedFlag == false) {
			onLoadedFlag = true;
			initTimeLineView();

			inputManager = (InputMethodManager) commentEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

			mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
				@Override
				public void onRefresh() {
					pageNum = 1;
					friendUtils.getTimeLineData(ON_REFERSH, pageNum);
				}
			});
			mSwipeLayout.setOnLoadListener(new OnLoadListener() {
				@Override
				public void onLoad() {
					
					friendUtils.getTimeLineData(ON_LOAD, pageNum);
				}
			});

			button.setOnClickListener(this);
			download.setOnClickListener(this);

			adapter = new MyItemAdapter(handler, friendList, this);
			listview.setAdapter((ListAdapter) adapter);

			dector = new GestureDetector(this);
			listview.setOnTouchListener(this);

			friendUtils = FriendOper.getInstance(this, handler);

			File emotionDataFile = new File(FileUtils.PATH_EMOJI_TXT);
			if (emotionDataFile.exists()) {
				// ½âÎö±íÇé°üÊý¾Ý
				PostActivity.emotionList = friendUtils.getEmotionList();
			}

			friendUtils.getTimeLineData(ON_REFERSH, pageNum);
		}
	}

	private void setTabSelection(int arg0) {
		clearSelection();
		item.images[arg0].setImageResource(item.images_selected[arg0]);
		item.texts[arg0].setTextColor(getResources().getColor(R.color.bottom_text_selected));
	}

	private void clearSelection() {
		for (int i = 0; i < item.viewNum; i++) {
			item.images[i].setImageResource(item.images_unselected[i]);
			item.texts[i].setTextColor(getResources().getColor(R.color.bottom_text_unselected));
		}
	}

	@Override
	protected void onPause() {
		if (commentView != null && commentView.getVisibility() == View.VISIBLE) {
			Message msg = new Message();
			msg.what = 9;
			handler.sendMessage(msg);
		}
		super.onPause();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// µã»÷emoji±íÇé£¬Ìí¼Óµ½±à¼­¿ò
		Toast.makeText(MainActivity.this, "emoji:" + emojiKeyBoard.getPageNum() + " -- " + arg2, Toast.LENGTH_SHORT)
				.show();
		emojiKeyBoard.clickEmoji(commentEdit, emojiKeyBoard.getPageNum(), arg2);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1 && requestCode == REQUST_CODE_CHAT_ACTIVITY) {
			msgList.get(0).latestMsg = dbManager.queryLatestMsg("friendName");
			msgAdapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
