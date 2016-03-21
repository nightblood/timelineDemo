
package com.zlf.testdemo01;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zlf.testdemo01.domain.BaseActivity;
import com.zlf.testdemo01.domain.EmotionInfo;
import com.zlf.testdemo01.utils.FriendOper;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends BaseActivity implements OnClickListener, 
	OnItemClickListener, OnLayoutChangedListener{

	public static final int LEFT_TYPE = 0;
	public static final int RIGHT_TYPE = 1;
	private ListView msgListView;
	private List<String> msgDatas = null;
	private MyChatAdapter adapter;
	private Button emojiBtn;
	private PopupWindow emojiWindow = null;
	private View emojiView;
	private List<EmotionInfo> emotionList;
	private ArrayList<View> views;
	private ArrayList<View> cursorViews;
	private int pageNum = 0;
	private FriendOper friendOper;
	private EditText content;
	private static boolean bInputVisiable = false;
	private InputMethodManager inputManager;
	private MyLayout chatLayout;
//	private ImageView blankImg;
	private View editBar;

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				// 隐藏输入法，将编辑框上移
				
				break;
			case 2:
				break;
			case 3:
				
				if (bInputVisiable == true) {
					inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				}
				if (emojiLayout.getVisibility() == View.VISIBLE) {
					emojiLayout.setVisibility(View.GONE);
				} else {
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							emojiLayout.setVisibility(View.VISIBLE);
						}
					}, 150);
				}
				break;
			case 4:
				if (emojiLayout.getVisibility() == View.VISIBLE) {
					emojiLayout.setVisibility(View.GONE);
				}
				break;
			}
		};
	};
	private View emojiLayout;
	private GestureDetector gesture;
	private Button sendBtn;
	private static Map<String, String> emojiMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg);
		
		initBaseActivity(true, false);

		friendOper = new FriendOper(this, null, null);
		emotionList = friendOper.getEmotionList();

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		msgDatas = bundle.getStringArrayList("data");
		
		getDatas();
		
		initView();
		// 得到表情map
		initEmojiMap();
		
		inputManager = (InputMethodManager) content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

		if (emotionList != null && emotionList.size() != 0) {
			initEmojiLayout();
		}

		adapter = new MyChatAdapter();
		msgListView.setAdapter(adapter);

	}

	private void initEmojiMap(){
		emojiMap = new HashMap<String, String>(emotionList.size());
		for (int i = 0; i < emotionList.size(); ++i) {
			emojiMap.put(emotionList.get(i).getText(), emotionList.get(i).getImageName());
		}
	}
	
	
	private List<Spannable> datas = new ArrayList<Spannable>();;
	
	private void getDatas() {
		String temp;
		for (int i = 0; i < msgDatas.size(); ++i) {
			temp = msgDatas.get(i);
			datas.add(strToSpanable(temp));
		}
	}

	private Spannable strToSpanable(String temp) {
		int index;
		List<Integer> indexList = new ArrayList<Integer>();
		EmotionInfo emotion;
		String regex = "\\[.*?\\]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(temp);
		
		List<String> emojis = null;
		
		while (matcher.find()) {
			if (emojis == null) 
				emojis = new ArrayList<String>();
			emojis.add(matcher.group(0));
			indexList.add(matcher.start());
			System.out.println("``````````````````````" + matcher.group(0));
		}
		
		if (emojis == null) 
			return new SpannableString(temp);
		
		SpannableString spannalbeString = null;
		for (int i = 0; i < emojis.size(); ++i) {
			index = emotionList.indexOf(emojis.get(i));
			emotion = emotionList.get(i);
			
//			spannalbeString = addFace(this, emotion.getImageName(), temp.substring(indexList.get(i)));
			spannalbeString = addFace(this, emotion.getImageName(), temp);
			
		}
		
		return spannalbeString;
	}

	private void initView() {
		msgListView = (ListView) findViewById(R.id.msg_listview);
		emojiBtn = (Button) findViewById(R.id.emoji);
		emojiBtn.setOnClickListener(this);
		content = (EditText) findViewById(R.id.content);
		chatLayout = (MyLayout) findViewById(R.id.slidingLayout);

		editBar = findViewById(R.id.edit_bar);
		emojiLayout = findViewById(R.id.emoji_layout);
		sendBtn = (Button) findViewById(R.id.send);
		
		sendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (content.getText().toString() != null && content.getText().toString().length() != 0) {
					msgDatas.add("right:" + content.getText().toString());
					
//					addData("right:" + content.getText().toString());
					datas.add(content.getText());
					adapter.notifyDataSetChanged();
					content.setText("");
				}
			}
		});

		chatLayout.setOnChangeLayoutListener(new OnLayoutChangedListener() {
			@Override
			public void layoutChanged(Boolean bInputVisiable) {
				ChatActivity.bInputVisiable = bInputVisiable;
				Message msg = new Message();
				msg.what = 4;
				handler.sendMessage(msg);
			}
		});
		
	}

	

	private void initEmojiLayout() {
		ViewPager viewPager = (ViewPager) findViewById(R.id.emotion_viewpage);
		LinearLayout emojiCursor = (LinearLayout) findViewById(R.id.emoji_cursor);
		initEmojiPageView(viewPager, emojiCursor);
		
		
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
			lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
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
	private static final float FLING_MIN_DISTANCE = 10;
	private static final float FLING_MIN_VELOCITY = 10;

	public class MyChatAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			return datas.get(position);
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
			int startIndex = msgDatas.get(position).indexOf(':');
			int type = getItemViewType(position);
			if (type == ERROR_TYPE) {
				System.out.println("Error : type------------------------");
				return null;
			}
			
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
			holder.content.setText(datas.get(position).subSequence(startIndex, datas.get(position).length()));

			
			
			return convertView;
		}

		private int getViewType(String string) {
			String[] temp = string.split(":");
			if (temp[0].equals("right")) {
				return RIGHT_TYPE;
			} else if (temp[0].equals("left")) {
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
	
//	private Pattern buildPattern() {
//		StringBuilder patternString = new StringBuilder(mSmileyTexts.length * 3);
//		patternString.append('(');
//		for (String s : mSmileyTexts) {
//			patternString.append(Pattern.quote(s));
//			patternString.append('|');
//		}
//		patternString.replace(patternString.length() - 1, patternString.length(), ")");
//		return Pattern.compile(patternString.toString());
//	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.emoji:
			if (emojiLayout == null) {
				Toast.makeText(ChatActivity.this, "请先下载表情包！！！", Toast.LENGTH_SHORT).show();
				return;
			}

			if (bInputVisiable == true) {
				inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			}

//			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) editBar.getLayoutParams();
//			params.height = getSupportSoftInputHeight() + editBar.getHeight() + emojiLayout.getHeight();
//			editBar.setLayoutParams(params);
//			
			Message msg = new Message();
			msg.what = 3;
			handler.sendMessage(msg);
			

//			showEmoji();

			break;

		default:
			break;
		}
	}

	private void showEmoji() {
		int[] location = new int[2];
		emojiBtn.getLocationOnScreen(location);
		emojiWindow.showAtLocation(emojiBtn, Gravity.NO_GRAVITY, location[0],
				location[1] + emojiBtn.getHeight() + ImageUtils.dip2px(this, 5));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {


		// 点击表情后，在编辑框中添加表情。
		EmotionInfo emoji = emotionList.get(pageNum * 21 + arg2);

		// 删除表情按钮
		if (emoji.getText().equals("删除")) {
			Editable et = content.getText();
			int start = content.getSelectionStart(); // 光标位置
			int lastEmojiStart = 0;
			int lastEmojiEnd = 0;

			String str = content.getText().toString().substring(0, start);
			if (str.isEmpty())
				return;

			lastEmojiEnd = str.lastIndexOf(']');
			if (lastEmojiEnd != 0 && lastEmojiEnd == start - 1) {
				// 删除表情
				lastEmojiStart = str.lastIndexOf('[');
				et.delete(lastEmojiStart, lastEmojiEnd + 1);
				content.setText(et);
				content.setSelection(lastEmojiStart);
				content.setFocusableInTouchMode(true);
				content.setFocusable(true);

			} else {
				// 删除文字
				et.delete(start - 1, start);
				content.setText(et);
				content.setSelection(start - 1);
				content.setFocusableInTouchMode(true);
				content.setFocusable(true);
			}
			return;
		}

		// 得到表情图片的SpannableString对象
		SpannableString res = addFace(this, emoji.getImageName(), emoji.getText());

		if (res != null)
			insertPhotoToEditText(res);

		// System.out.println("content: " + content.getText().toString());
	
	}
	
	
	private void insertPhotoToEditText(SpannableString ss) {
		Editable et = content.getText();
		int start = content.getSelectionStart();
		et.insert(start, ss);
		content.setText(et);
		content.setSelection(start + ss.length());
		content.setFocusableInTouchMode(true);
		content.setFocusable(true);
	}
	
	public SpannableString addFace(Context context, String imageName, String spannableString) {
		if (TextUtils.isEmpty(spannableString))
			return null;

		System.out.println("Add Face! spannableString: " + spannableString);

		String localPath = getApplicationContext().getFilesDir().getAbsolutePath(); // 包名的绝对路径:
																					// /data/data/包名/

		Bitmap bitmap = BitmapFactory.decodeFile(localPath + "/emoticon/" + imageName);

		bitmap = Bitmap.createScaledBitmap(bitmap, EmotionUtils.dip2px(context, 25),
				EmotionUtils.dip2px(context, 25), true);
		// bitmap = Bitmap.createScaledBitmap(bitmap, 35, 35, true);
		Drawable drawable = new BitmapDrawable(bitmap);
		drawable.setBounds(0, 0, EmotionUtils.dip2px(context, 25), EmotionUtils.dip2px(context, 25));
		ImageSpan imageSpan = new ImageSpan(drawable);
		SpannableString spannable = new SpannableString(spannableString);
		spannable.setSpan(imageSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}

	@Override
	public void layoutChanged(Boolean bInputVisiable) {
		// TODO Auto-generated method stub

	}

	private int getSoftButtonsBarHeight() {
		DisplayMetrics metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int usableHeight = metrics.heightPixels;
		this.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
		int realHeight = metrics.heightPixels;
		if (realHeight > usableHeight) {
			return realHeight - usableHeight;
		} else {
			return 0;
		}
	}

	private int getSupportSoftInputHeight() {
		Rect r = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
		int screenHeight = this.getWindow().getDecorView().getRootView().getHeight();
		int softInputHeight = screenHeight - r.bottom;
		if (Build.VERSION.SDK_INT >= 18) {
			// When SDK Level >= 18, the softInputHeight will contain the height
			// of softButtonsBar (if has)
			softInputHeight = softInputHeight - getSoftButtonsBarHeight();
		}
		return softInputHeight;
	}

//	@Override
//	public boolean onDown(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//	@Override
//	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//		if(e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY)  
//        {  
////            Intent intent = new Intent(ChatActivity.this, ChatActivity.class);  
////            startActivity(intent);  
//             Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();   
//  
//        }  
//        else if (e2.getX()-e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) >FLING_MIN_VELOCITY) {  
//              
//            //切换Activity  
////            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
//        	
////            startActivity(intent);  
//        	Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();  
//        	super.finish();
//        	overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//
//        }  
//          
//        return false; 
//	}
//
//	@Override
//	public void onLongPress(MotionEvent e) {
//		// TODO Auto-generated method stub
//	}
//	@Override
//	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//	@Override
//	public void onShowPress(MotionEvent e) {
//		// TODO Auto-generated method stub
//	}
//	@Override
//	public boolean onSingleTapUp(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		
//		return gesture.onTouchEvent(event);
//	}

}
