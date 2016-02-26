package com.zlf.testdemo01;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zlf.testdemo01.domain.EmotionInfo;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
public class PostActivity extends Activity implements OnClickListener, OnItemClickListener {
	private List<File> imageFiles;
	private GridView gridView1; // 网格显示缩略图
	private Button buttonPublish; // 发布按钮
	private Button buttonCancle;
	private EditText content;
	private final int IMAGE_OPEN = 1; // 打开图片标记
	private String pathImage; // 选择图片路径
	private Bitmap bmp; // 导入临时图片
	private ArrayList<HashMap<String, Object>> imageItem;
	private SimpleAdapter simpleAdapter; // 适配器
	private static String actionUrl = "http://client.e0575.com/quanzi/app.php?c=quanzi&a=topicadd";
	private Button addEmotion;
	public static List<EmotionInfo> emotionList;
	private RelativeLayout emojiLayout;
	private ViewPager viewPager;
	private GridView gv;
	private List<View> views;
	private int pageNum = 0;
//	private static Map mapImageCached; // 缓存要上传图片

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		mapImageCached = new HashMap();
		imageFiles = new ArrayList<File>();
		/*
		 * 防止键盘挡住输入框 不希望遮挡设置activity属性 android:windowSoftInputMode="adjustPan"
		 * 希望动态调整高度 android:windowSoftInputMode="adjustResize"
		 */
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		// 锁定屏幕
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_post);

		initView();

		if (emotionList != null) 
			initPageView();

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
//					System.out.println("=================要上传" + imageFiles.size() + " 张照片");
					for (int i = 0; i < imageFiles.size(); ++i) {
						// 上传所有图片，判断图片是否需要裁剪。
						if (imageFiles.get(i).length() > 2048) {
							// 1.本地创建一个新的被裁剪过的图片文件
							// TODO 可以把缓存图片放在朋友圈缓存图片文件中，当刷新时可以从缓存中取。
							//这种场景首先要保证是自己的状态才可以取本地缓存（这时缓存图片没有url等网络信息，也不需要这些信息），
							//如果不是自己的状态则必须要匹配缓存图片和更新的图片信息一致才可以加载缓存图片。
							bitmap = ImageUtils.compressImageFromFile(imageFiles.get(i).getAbsolutePath());
							
							fileName = ImageUtils.bitmap2file(bitmap, imageFiles.get(i).getName());
							
							File file = new File(fileName);
							if (file != null)
								params.addBodyParameter("file" + i, file);
							System.out.println("before compress: " + imageFiles.get(i).getAbsolutePath() + " size: " + imageFiles.get(i).length() + ".................. after compress"
								+ fileName + " size: " + file.length());
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
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic); // 加号
		imageItem = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("itemImage", bmp);
		imageItem.add(map);
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
				// TODO Auto-generated method stub
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
				if (imageItem.size() == 10) {
					// 第一张为默认图片（加号图片）
					Toast.makeText(PostActivity.this, "图片数9张已满", Toast.LENGTH_SHORT).show();
				} else if (position == 0) {
					// 点击第0张图片（加号图片），访问相册。
					Toast.makeText(PostActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
					// 选择图片
					Intent intent = new Intent(Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, IMAGE_OPEN);
					// 通过onResume()刷新数据
				} else {
					// 提示是否移除添加好的图片
					dialog(position);
				}
			}
		});
	}

	private void initView() {
		buttonPublish = (Button) findViewById(R.id.post_btn);
		buttonCancle = (Button) findViewById(R.id.cancle_btn);
		content = (EditText) findViewById(R.id.content_et);
		emojiLayout = (RelativeLayout) findViewById(R.id.ll_emotion);
		viewPager = (ViewPager) findViewById(R.id.vp_contains);
		addEmotion = (Button) findViewById(R.id.emotion_btn);
		addEmotion.setOnClickListener(this);
		gridView1 = (GridView) findViewById(R.id.gridView);
	}

	// 表情页显示
	private void initPageView() {
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

		System.out.println("emotion page count : " + pageCount + " emotion count : " + emotionList.size());

		// just for debug
//		pageCount = 2;

		for (int i = 0; i < pageCount; ++i) {
			view = new GridView(this);
			// list = new ArrayList<EmotionInfo>();

			adapter = new MyEmojiAdapter(this, emotionList.subList(start, end)); // (0,21) (21,42) (42,50)

			start += 21;
			end = ((end + 21) > emotionList.size()) ? emotionList.size() : end + 21;

			view.setAdapter(adapter);
			view.setNumColumns(7);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setHorizontalSpacing(1);
			view.setVerticalSpacing(1);
			view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			view.setCacheColorHint(0);
			view.setPadding(2, 0, 2, 0);
			view.setSelector(new ColorDrawable(Color.TRANSPARENT));
			view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			view.setGravity(Gravity.CENTER);
			view.setOnItemClickListener(this);

			views.add(view);
			//viewPager.addView(view);
		}
		viewPager.setAdapter(new MyEmojiViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				pageNum = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	// 获取图片路径 响应startActivityForResult
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 打开图片
		if (resultCode == RESULT_OK && requestCode == IMAGE_OPEN) {
			Uri uri = data.getData();
			 
			if (!TextUtils.isEmpty(uri.getAuthority())) {
				System.out.println("-------要上传的图片------------->" + uri);
				// 查询选择图片
				Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null,
						null, null);
				// 返回 没找到选择图片
				if (null == cursor) {
					return;
				}
				// 光标移动至开头 获取图片路径
				cursor.moveToFirst();
				pathImage = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				File f = new File(pathImage);
				imageFiles.add(f);
				
			}
		} // end if 打开图片
	}

	// 刷新图片
	@Override
	protected void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(pathImage)) {
			Bitmap addbmp = ImageUtils.comp(BitmapFactory.decodeFile(pathImage));
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", addbmp);
			imageItem.add(map);
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
				// 删除图片缓存
//				if (null != mapImageCached.get(imageFiles.get(position - 1).getName()))
//					mapImageCached.remove(imageFiles.get(position - 1).getName());
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
//		http.configTimeout(15000);
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
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(PostActivity.this, "Post fail : " + msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		// 点击表情后，在编辑框中添加表情。
		EmotionInfo emoji = emotionList.get(pageNum * 21 + arg2);
		
		// 得到表情图片的SpannableString对象
		SpannableString res = addFace(this, emoji.getImageName(), emoji.getText());

		if (res != null) 
			insertPhotoToEditText(res);
		
//		System.out.println("content: " + content.getText().toString());
	}
	
	/**
	 * 将图片对象插入到光标处
	 * @param ss
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
	 * @param imageName 		图片名字
	 * @param spannableString 	已编辑的字符串
	 * @return
	 */
	public SpannableString addFace(Context context, String imageName, String spannableString) {
		if (TextUtils.isEmpty(spannableString)) 
			return null;
		
		System.out.println("Add Face! spannableString: " + spannableString);
		
//		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imgId);
		String localPath = getApplicationContext().getFilesDir().getAbsolutePath(); // 包名的绝对路径: /data/data/包名/
		
		Bitmap bitmap = BitmapFactory.decodeFile(localPath + "/emoticon/" + imageName);

		bitmap = Bitmap.createScaledBitmap(bitmap, EmotionUtils.dip2px(context, 25), EmotionUtils.dip2px(context, 25), true);
//		bitmap = Bitmap.createScaledBitmap(bitmap, 35, 35, true);
		Drawable drawable = new BitmapDrawable(bitmap);
		drawable.setBounds(0, 0, EmotionUtils.dip2px(context, 25), EmotionUtils.dip2px(context, 25));
		ImageSpan imageSpan = new ImageSpan(drawable);
		SpannableString spannable = new SpannableString(spannableString);
		spannable.setSpan(imageSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		spannable.setSpan(imageSpan, 0, content.getSelectionStart(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.emotion_btn:
			if (new File(MainActivity.emotionPath).exists()) {
				showEmotion();
			} else {
				Toast.makeText(PostActivity.this, "没有表情包，请先下载！！！", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	private void showEmotion() {
		if (emojiLayout.getVisibility() == RelativeLayout.VISIBLE) {
			emojiLayout.setVisibility(RelativeLayout.GONE);
		} else {
			emojiLayout.setVisibility(RelativeLayout.VISIBLE);
		}
	}

}
