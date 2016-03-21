package com.zlf.testdemo01;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zlf.testdemo01.domain.BasePostActivity;
import com.zlf.testdemo01.domain.EmotionInfo;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
public class PostActivity extends BasePostActivity implements OnClickListener, OnItemClickListener
	{
	private List<File> imageFiles;
	private GridView gridView1; // 网格显示缩略图
	private Button buttonPublish; // 发布按钮
	private Button buttonCancle;
	private EditText content;
	private final int IMAGE_OPEN = 1; // 打开图片标记
	private List<String> pathImage = new ArrayList<String>();
	private ArrayList<HashMap<String, Object>> imageItem;
	private SimpleAdapter simpleAdapter; // 适配器
	private static String actionUrl = "http://client.e0575.com/quanzi/app.php?c=quanzi&a=topicadd";
	private static Button addEmotion;
	public static List<EmotionInfo> emotionList;
	private RelativeLayout emojiLayout;
	private static ViewPager viewPager = null;
	private GridView gv;
	private List<View> views;
	private int pageNum = 0;
	private boolean inputMethodFlag = false;
	public static String KEY_TO_SHOW_PICTURES_ACTIVITY = "CAN_SELECTED_PICTURE_COUNT";

	public static final int SHOW_PICTURE_CODE = 0x01;
	private static final float FLING_MIN_DISTANCE = 10;
	private static final float FLING_MIN_VELOCITY = 10;

	private static InputMethodManager inputManager;
	public static boolean bInputVisiable = false;
	public static boolean bScrolling = false;
	public static boolean bEmojiVisible = false;
	private Date emojiTime = null;

//	private LinearLayout emojiCursor;
	private ArrayList<View> cursorViews;
	private final int REQUEST_CODE_CAPTURE_CAMERA = 0xff0;
	private final int REQUEST_CODE_GET_LOCATION = 0xff1;
	private MyEmojiPopupWindow emojiWindow;
	private Button locationBtn;
//	private static View emojiView = null;
	private TextView locationContent;
	private Button atBtn;
	private Button picBtn;
	private GestureDetector gesture;
	private View postLayout;

	public static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 3) {
				// 隐藏输入法后必须隐藏表情页
				bInputVisiable = false;
			} else if (msg.what == 2){
				addEmotion.setBackgroundResource(R.drawable.emoji);
			} else if (msg.what == 1) {
				addEmotion.setBackgroundResource(R.drawable.emoji_showed);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		// mapImageCached = new HashMap();
		imageFiles = new ArrayList<File>();
		/*
		 * 防止键盘挡住输入框 不希望遮挡设置activity属性 android:windowSoftInputMode="adjustPan"
		 * 希望动态调整高度 android:windowSoftInputMode="adjustResize"
		 */
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		// 锁定屏幕
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_post);
		initBaseActivity(true, false);

		initView();

		picBtn.setOnClickListener(this);
		atBtn.setOnClickListener(this);
		if (emotionList != null) {
//			initPageView();
		}

		buttonPublish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (content.length() > 100) {
					Toast.makeText(PostActivity.this, "文字超过100字，请重新编辑！！！", Toast.LENGTH_SHORT).show();
					return;
				}
				RequestParams params = new RequestParams();
				params.addBodyParameter("content", content.getText().toString());
				// params.addBodyParameter(filePath.replace("/", ""), new
				// File(filePath));
				Bitmap bitmap;
				String fileName;
				if (0 != imageItem.size() - 1) {
					// System.out.println("=================要上传" +
					// imageFiles.size() + " 张照片");
					for (int i = 0; i < imageFiles.size(); ++i) {
						// 上传所有图片，判断图片是否需要裁剪。
						if (imageFiles.get(i).length() > 2048) {
							// 1.本地创建一个新的被裁剪过的图片文件
							// TODO 可以把缓存图片放在朋友圈缓存图片文件中，当刷新时可以从缓存中取。
							// 这种场景首先要保证是自己的状态才可以取本地缓存（这时缓存图片没有url等网络信息，也不需要这些信息），
							// 如果不是自己的状态则必须要匹配缓存图片和更新的图片信息一致才可以加载缓存图片。
							bitmap = ImageUtils.getSmallBitmap(imageFiles.get(i).getAbsolutePath());
							fileName = ImageUtils.bitmap2file(
									ImageUtils.rotateBitmap(bitmap,
											ImageUtils.readPictureDegree(imageFiles.get(i).getAbsolutePath())),
									imageFiles.get(i).getName());

							File file = new File(fileName);
							if (file != null)
								params.addBodyParameter("file" + i, file);
						} else {
							params.addBodyParameter("file" + i, imageFiles.get(i));
						}
					}
				}
				uploadMethod(params, actionUrl);
			}
		});
		buttonCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		/*
		 * 载入默认图片添加图片加号 ，通过适配器实现 SimpleAdapter参数imageItem为数据源
		 * R.layout.griditem_addpic为布局
		 */
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic); // 加号
		addImage(bmp);
		simpleAdapter = new SimpleAdapter(this, imageItem, R.layout.griditem_addpic, new String[] { "itemImage" },
				new int[] { R.id.imageView1 });
		/*
		 * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如 map.put("itemImage",
		 * R.drawable.img); 解决方法: 1.自定义继承BaseAdapter实现 2.ViewBinder()接口实现 参考
		 * http://blog.csdn.net/admin_/article/details/7257901
		 */
		simpleAdapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView i = (ImageView) view;
					i.setImageBitmap((Bitmap) data);
					return true;
				}
				return false;
			}
		});
		gridView1.setAdapter(simpleAdapter);

		/*
		 * 监听GridView点击事件 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
		 */
		gridView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				if (position == 0) {
					 if (imageItem.size() == 10) {
						 Toast.makeText(PostActivity.this, "图片数9张已满!",
								 Toast.LENGTH_SHORT).show();
						 return;
					 }
					 showPopupWindow();
				} else {
					// 提示是否移除添加好的图片
					dialog(position);
				}
			}
		});
		
		// 表情页 PopupWindow + ViewPager
		initEmojiPopupWindow();
		
	}

	private void initEmojiPopupWindow() {
		View contentView = LayoutInflater.from(this).inflate(R.layout.popup_window_emoji, null);
		emojiWindow = new MyEmojiPopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, handler);
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

	private void addImage(Bitmap bmp) {
		imageItem = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("itemImage", bmp);
		imageItem.add(map);
	}

	protected void showPopupWindow() {
		View contentView = LayoutInflater.from(this).inflate(R.layout.popup_window_add_button, null);
		final PopupWindow window = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);
		
		window.setAnimationStyle(R.style.anim_popup_add_picture);
		
		window.setTouchable(true);
		window.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return false;
			}
		});
		window.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				backgroundAlpha(1f);
			}
		});

		backgroundAlpha(0.7f);
		window.setWidth(getWindowManager().getDefaultDisplay().getWidth() * 7 / 8);
		window.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape2));
		window.showAtLocation(this.findViewById(R.id.slidingLayout), Gravity.CENTER, 0, 0);

		TextView camera = (TextView) contentView.findViewById(R.id.from_camera);
		TextView pictures = (TextView) contentView.findViewById(R.id.from_pictures);

		camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getImageFromCamera();
				window.dismiss();
			}
		});
		pictures.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getImageFromAlbum();
				window.dismiss();
			}

		});
	}
	private void getImageFromAlbum() {
		Intent intent = new Intent();
		intent.setClass(PostActivity.this, ShowPicturesActivity.class);
		intent.putExtra(KEY_TO_SHOW_PICTURES_ACTIVITY, 10 - imageItem.size());
		startActivityForResult(intent, SHOW_PICTURE_CODE);
	}
	private void getImageFromCamera() {  
	       String state = Environment.getExternalStorageState();  
	       if (state.equals(Environment.MEDIA_MOUNTED)) {  
	           Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");     
	           startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMERA);  
	       }  
	       else {  
	           Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();  
	       }  
	   }
	private void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = bgAlpha;
		getWindow().setAttributes(lp);
	}

	private void initView() {
		buttonPublish = (Button) findViewById(R.id.post_btn);
		buttonCancle = (Button) findViewById(R.id.cancle_btn);
		content = (EditText) findViewById(R.id.content_et);

//		emojiView = findViewById(R.id.emoji_layout);
		viewPager = (ViewPager) findViewById(R.id.emotion_viewpage);
//		emojiCursor = (LinearLayout) findViewById(R.id.emoji_cursor);
		addEmotion = (Button) findViewById(R.id.emotion_btn);
		addEmotion.setOnClickListener(this);
		gridView1 = (GridView) findViewById(R.id.gridView);
		
		locationBtn = (Button) findViewById(R.id.location);
		locationBtn.setOnClickListener(this);
		locationContent = (TextView) findViewById(R.id.location_content);
		
		inputManager = (InputMethodManager) content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		atBtn = (Button) findViewById(R.id.at);
		picBtn = (Button) findViewById(R.id.pic);
		
		postLayout = findViewById(R.id.slidingLayout);
//		postLayout.setOnTouchListener(this);
//		postLayout.setLongClickable(true);
//		gesture = new GestureDetector(this);
	}

	// 表情页显示
//	private void initPageView() {
//		int pageCount;
//
//		GridView view;
//		MyEmojiAdapter adapter;
//		int start = 0;
//		int end;
//
//		if (emotionList.size() == 0)
//			return;
//		views = new ArrayList<View>();
//		end = (emotionList.size() > 21) ? 21 : emotionList.size();
//
//		int temp = emotionList.size() / 21; // coloum : 7, row : 3
//		pageCount = emotionList.size() % (21 * temp) == 0 ? temp : temp + 1;
//
//		System.out.println("emotion page count : " + pageCount + " emotion count : " + emotionList.size());
//
//		// 初始化表情页的游标
//		initEmojiCursor(pageCount);
//
//		for (int i = 0; i < pageCount; ++i) {
//			view = new GridView(this);
//
//			adapter = new MyEmojiAdapter(this, emotionList.subList(start, end)); // (0,20)
//																					// (21,41)
//																					// (42,50)
//
//			start += 21;
//			end = ((end + 21) > emotionList.size()) ? emotionList.size() : end + 21;
//
//			view.setNumColumns(7);
//			view.setBackgroundColor(Color.TRANSPARENT);
//			view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
//			view.setCacheColorHint(0);
//			view.setSelector(new ColorDrawable(Color.TRANSPARENT));
//			view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//
//			view.setOnItemClickListener(this);
//
//			view.setAdapter(adapter);
//			views.add(view);
//			// viewPager.addView(view);
//		}
//		viewPager.setAdapter(new MyEmojiViewPagerAdapter(views));
//		viewPager.setCurrentItem(0);
//		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
//
//			@Override
//			public void onPageSelected(int arg0) {
//				pageNum = arg0;
//				for (int i = 0; i < cursorViews.size(); ++i) {
//					if (i == arg0) {
//						cursorViews.get(i).setBackgroundResource(R.drawable.cursor2);
//					} else {
//						cursorViews.get(i).setBackgroundResource(R.drawable.cursor1);
//					}
//				}
//			}
//
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//			}
//
//			@Override
//			public void onPageScrollStateChanged(int arg0) {
//
//			}
//		});
//	}

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

	// 获取图片路径 响应startActivityForResult
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SHOW_PICTURE_CODE && resultCode == ShowPicturesActivity.RETURN_CODE) {
			System.out.println("得到返回的结果");
			Bundle bundle = data.getExtras();
			pathImage = new ArrayList<String>();
			pathImage.addAll((List<String>) bundle.get("pictures"));

			for (int i = 0; i < pathImage.size(); ++i) {
				System.out.println(pathImage.get(i));
				imageFiles.add(new File(pathImage.get(i)));
			}
		} else if (requestCode == REQUEST_CODE_CAPTURE_CAMERA) {
			if (data == null)
				return;
			Uri uri = data.getData();
			if (uri == null)
				return;
			Bundle bundle = data.getExtras();
			if (bundle == null) 
				return;
			Bitmap bitmap = (Bitmap) bundle.get("data");
			
			pathImage = new ArrayList<String>();
			pathImage.add(ImageUtils.getRealFilePath(this, uri));

			imageFiles.add(new File(pathImage.get(0)));
		} else if (requestCode == REQUEST_CODE_GET_LOCATION) {
			if (data == null)
				return;
			Bundle bundle = data.getExtras();
			if (bundle == null) 
				return;
			locationContent.setText((CharSequence) bundle.get("location"));
			locationBtn.setBackgroundResource(R.drawable.location_showed);
		}
	}

	// 刷新图片
	@Override
	protected void onResume() {
		super.onResume();
		if (pathImage == null) {
			return;
		}
		Bitmap addbmp;
		for (int i = 0; i < pathImage.size(); ++i) {
			if (!TextUtils.isEmpty(pathImage.get(i))) {
				// addbmp =
				// ImageUtils.comp(BitmapFactory.decodeFile(pathImage.get(i)));
				addbmp = ImageUtils.getSmallBitmap(pathImage.get(i));
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("itemImage", ImageUtils.rotateBitmap(addbmp, ImageUtils.readPictureDegree(pathImage.get(i))));
				imageItem.add(map);
			}
		}
		simpleAdapter = new SimpleAdapter(this, imageItem, R.layout.griditem_addpic, new String[] { "itemImage" },
				new int[] { R.id.imageView1 });
		simpleAdapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView i = (ImageView) view;
					i.setImageBitmap((Bitmap) data);
					return true;
				}
				return false;
			}
		});
		gridView1.setAdapter(simpleAdapter);
		simpleAdapter.notifyDataSetChanged();
		// 刷新后释放防止手机休眠后自动添加
		pathImage = null;

	}

	/*
	 * Dialog对话框提示用户删除操作 position为删除图片位置
	 */
	protected void dialog(final int position) {
		AlertDialog.Builder builder = new Builder(PostActivity.this);
		builder.setMessage("确认要移除已添加图片吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				imageItem.remove(position);
				simpleAdapter.notifyDataSetChanged();
				// 移除imageFils中对应的文件
				imageFiles.remove(position - 1);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	public void uploadMethod(final RequestParams params, final String uploadHost) {
		HttpUtils http = new HttpUtils();
		// http.configTimeout(15000);
		http.send(HttpRequest.HttpMethod.POST, uploadHost, params, new RequestCallBack<String>() {
			@Override
			public void onStart() {
				Toast.makeText(PostActivity.this, "Post start", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				if (isUploading) {
				} else {
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					JSONObject jsonObj = new JSONObject(responseInfo.result);
					String action = jsonObj.getString("action");
					if (action.equals("success")) {
						Toast.makeText(PostActivity.this, "Post success!", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						String value = jsonObj.getString("value");
						Toast.makeText(PostActivity.this, "Post fail! " + value, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
				// TODO Auto-generated method stub

			}

		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {

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

	/**
	 * 将图片对象插入到光标处
	 * 
	 */
	private void insertPhotoToEditText(SpannableString ss) {
		Editable et = content.getText();
		int start = content.getSelectionStart();
		et.insert(start, ss);
		content.setText(et);
		content.setSelection(start + ss.length());
		content.setFocusableInTouchMode(true);
		content.setFocusable(true);
	}

	/**
	 * 
	 * @param context
	 * @param imageName
	 *            图片名字
	 * @param spannableString
	 *            已编辑的字符串
	 * @return
	 */
	public SpannableString addFace(Context context, String imageName, String spannableString) {
		if (TextUtils.isEmpty(spannableString))
			return null;

		System.out.println("Add Face! spannableString: " + spannableString);

		String localPath = getApplicationContext().getFilesDir().getAbsolutePath(); // 包名的绝对路径:
																					// /data/data/包名/

		Bitmap bitmap = BitmapFactory.decodeFile(localPath + "/emoticon/" + imageName);

		bitmap = Bitmap.createScaledBitmap(bitmap, EmotionUtils.dip2px(context, 25), EmotionUtils.dip2px(context, 25),
				true);
		// bitmap = Bitmap.createScaledBitmap(bitmap, 35, 35, true);
		Drawable drawable = new BitmapDrawable(bitmap);
		drawable.setBounds(0, 0, EmotionUtils.dip2px(context, 25), EmotionUtils.dip2px(context, 25));
		ImageSpan imageSpan = new ImageSpan(drawable);
		SpannableString spannable = new SpannableString(spannableString);
		spannable.setSpan(imageSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}

	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.location:
			Intent intent = new Intent();
			intent.setClass(PostActivity.this, LocationActivity.class);
			startActivityForResult(intent, REQUEST_CODE_GET_LOCATION);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.emotion_btn:
			if (new File(MainActivity.emotionPath).exists()) {
				showEmotion();
			} else {
				Toast.makeText(PostActivity.this, "没有表情包，请先下载！！！", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.at:
			atBtn.setBackgroundResource(R.drawable.at_selected);
			Toast.makeText(PostActivity.this, "待开发！！！", Toast.LENGTH_SHORT).show();
			break;
		case R.id.pic:
			Toast.makeText(PostActivity.this, "待开发！！！", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	private void showEmotion() {
		if (emojiWindow.isShowing()) {
//			addEmotion.setBackgroundResource(R.drawable.emoji);
		} else {
			emojiWindow.showAsDropDown(addEmotion, 0, 10);
//			addEmotion.setBackgroundResource(R.drawable.emoji_showed);	
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

}
