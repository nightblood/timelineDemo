package com.zlf.testdemo01;

import java.util.ArrayList;
import java.util.List;

import com.zlf.testdemo01.domain.EmotionInfo;
import com.zlf.testdemo01.utils.FriendOper;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity implements OnClickListener, OnItemClickListener {

	public static final int LEFT_TYPE = 0;
	public static final int RIGHT_TYPE = 1;
	private ListView msgListView;
	private List<String> msgDatas;
	private MyChatAdapter adapter;
	private Button emojiBtn;
	private PopupWindow emojiWindow = null;
	private View emojiView;
	private List<EmotionInfo> emotionList;
	private ArrayList<View> views;
	private ArrayList<View> cursorViews;
	private int pageNum = 0;
	private FriendOper friendOper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg);
		
		friendOper = new FriendOper(this, null, null);
		emotionList = friendOper.getEmotionList();
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		msgDatas = bundle.getStringArrayList("data");
		
		initView();

		if (emotionList != null && emotionList.size() != 0) {
			initEmojiPopupWindow();
		}
		
		
		adapter = new MyChatAdapter();
		msgListView.setAdapter(adapter);
		
	}
	private void initView() {
		msgListView = (ListView) findViewById(R.id.msg_listview);
		emojiBtn = (Button) findViewById(R.id.emoji);
		emojiBtn.setOnClickListener(this);
		
	}
	
	private void initEmojiPopupWindow() {
		View contentView = LayoutInflater.from(this).inflate(R.layout.popup_window_emoji, null);
		emojiWindow = new MyEmojiPopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		emojiWindow.setFocusable(false);
		emojiWindow.setTouchable(true);
		emojiWindow.setOutsideTouchable(true);
		
		emojiWindow.setAnimationStyle(R.style.anim_popup_dir);
		
		emojiWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape2));
		
		ViewPager viewPager = (ViewPager) contentView.findViewById(R.id.emotion_viewpage);
		View cursor = contentView.findViewById(R.id.emoji_cursor);
		LinearLayout emojiCursor = (LinearLayout) contentView.findViewById(R.id.emoji_cursor);
		initEmojiPageView(viewPager, emojiCursor);
		
		contentView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (emojiWindow.isShowing()) {
					emojiWindow.dismiss();
				}
				return false;
			}
		});
		
	}

	private void initEmojiPageView(ViewPager viewPager, LinearLayout emojiCursor) {
		int pageCount;
		GridView view;
		MyEmojiAdapter adapter;
		int start = 0;
		int end;
		if (emotionList.size() == 0)
			return;
		views = new ArrayList<View>();
		end = (emotionList.size() > 21) ? 21 : emotionList.size();

		int temp = emotionList.size() / 21; // coloum : 7, row : 3
		pageCount = emotionList.size() % (21 * temp) == 0 ? temp : temp + 1;

		// 初始化表情页的游标
		initEmojiCursor(pageCount, emojiCursor);
		for (int i = 0; i < pageCount; ++i) {
			view = new GridView(this);

			adapter = new MyEmojiAdapter(this, emotionList.subList(start, end)); // (0,20)
																					// (21,41)
																					// (42,50)
			start += 21;
			end = ((end + 21) > emotionList.size()) ? emotionList.size() : end + 21;

			view.setNumColumns(7);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			view.setCacheColorHint(0);
			view.setSelector(new ColorDrawable(Color.TRANSPARENT));
			view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			view.setOnItemClickListener(this);

			view.setAdapter(adapter);
			views.add(view);
			// viewPager.addView(view);
		}
		viewPager.setAdapter(new MyEmojiViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			

			@Override
			public void onPageSelected(int arg0) {
				pageNum = arg0;
				for (int i = 0; i < cursorViews.size(); ++i) {
					if (i == arg0) {
						cursorViews.get(i).setBackgroundResource(R.drawable.cursor2);
					} else {
						cursorViews.get(i).setBackgroundResource(R.drawable.cursor1);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	
	}
	
	private void initEmojiCursor(int pageCount, LinearLayout emojiCursor) {
		ImageView imageView;
		LinearLayout.LayoutParams lp;
		cursorViews = new ArrayList<View>();
		for (int i = 0; i < pageCount; ++i) {
			imageView = new ImageView(this);
			lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.leftMargin = ImageUtils.dip2px(this, 5);
			lp.rightMargin = ImageUtils.dip2px(this, 5);
			lp.width = ImageUtils.dip2px(this, 5);
			lp.height = ImageUtils.dip2px(this, 5);

			emojiCursor.addView(imageView, lp);
			if (i == 0) {
				imageView.setBackgroundResource(R.drawable.cursor2);
			} else {
				imageView.setBackgroundResource(R.drawable.cursor1);
			}
			cursorViews.add(imageView);
		}
	}
	
	private static final int ERROR_TYPE = 3;
	public class MyChatAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return msgDatas.size();
		}
		@Override
		public Object getItem(int position) {
			return msgDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return getViewType(msgDatas.get(position));
		}
	
		@Override
		public int getViewTypeCount() {
			return 2;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			String[] content = msgDatas.get(position).split(":");
			int type = getItemViewType(position);
			if (type == ERROR_TYPE) {
				System.out.println("Error : type------------------------");
				return null;
			}
			System.out.println("********************************" + content[0] + "***" + content[1]);
			String[] temp = msgDatas.get(position).split(":");
			if (convertView == null) {
				holder = new ViewHolder();
				
				switch (type) {
				case RIGHT_TYPE:
					convertView = View.inflate(ChatActivity.this, R.layout.right_char_item, null);
					break;
				case LEFT_TYPE:
					convertView = View.inflate(ChatActivity.this, R.layout.left_char_item, null);
					break;
				}
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.content = (TextView) convertView.findViewById(R.id.tv_chatcontent);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();

			}
			holder.content.setText(content[1]);
			
			return convertView;
		}
		
		private int getViewType(String string) {
			String[] temp = string.split(":");
			if (temp[0].equals("right")) {
				return RIGHT_TYPE;
			} else if (temp[0].equals("left")){
				return LEFT_TYPE;
			}
			return ERROR_TYPE;
		}
			
		public class ViewHolder {
			public ImageView icon;
			public TextView content;
			public ImageView background;
			
		}
		
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.emoji:
			if (emojiWindow == null) {
				Toast.makeText(ChatActivity.this, "请先下载表情包！！！", Toast.LENGTH_SHORT).show();
				return;
			}
			if (emojiWindow.isShowing()) {
				emojiWindow.dismiss();
			} else {
				emojiWindow.showAsDropDown(emojiBtn);
			}
			break;

		default:
			break;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		System.out.println("click emoji!!!!!!!!!!!!!!!!!!!!!!!!");
	}
}
