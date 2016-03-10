package com.zlf.testdemo01;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zlf.testdemo01.domain.EmotionInfo;

import android.app.ActionBar.LayoutParams;
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
	private GridView gridView1; // ������ʾ����ͼ
	private Button buttonPublish; // ������ť
	private Button buttonCancle;
	private EditText content;
	private final int IMAGE_OPEN = 1; // ��ͼƬ���
	private List<String> pathImage = new ArrayList<String>();
	// private List<String> pathImage = ArrayList<String>; // ѡ��ͼƬ·��
	private Bitmap bmp; // ������ʱͼƬ
	private ArrayList<HashMap<String, Object>> imageItem;
	private SimpleAdapter simpleAdapter; // ������
	private static String actionUrl = "http://client.e0575.com/quanzi/app.php?c=quanzi&a=topicadd";
	private Button addEmotion;
	public static List<EmotionInfo> emotionList;
	private RelativeLayout emojiLayout;
	private ViewPager viewPager;
	private GridView gv;
	private List<View> views;
	private int pageNum = 0;
	public static String KEY_TO_SHOW_PICTURES_ACTIVITY = "CAN_SELECTED_PICTURE_COUNT";

	public static final int SHOW_PICTURE_CODE = 0x01;
	// private static Map mapImageCached; // ����Ҫ�ϴ�ͼƬ

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// mapImageCached = new HashMap();
		imageFiles = new ArrayList<File>();
		/*
		 * ��ֹ���̵�ס����� ��ϣ���ڵ�����activity���� android:windowSoftInputMode="adjustPan"
		 * ϣ����̬�����߶� android:windowSoftInputMode="adjustResize"
		 */
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		// ������Ļ
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_post);

		initView();

		if (emotionList != null)
			initPageView();

		buttonPublish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (content.length() > 100) {
					Toast.makeText(PostActivity.this, "���ֳ���100�֣������±༭������", Toast.LENGTH_SHORT).show();
					return;
				}
				RequestParams params = new RequestParams();
				params.addBodyParameter("content", content.getText().toString());
				// params.addBodyParameter(filePath.replace("/", ""), new
				// File(filePath));
				Bitmap bitmap;
				String fileName;
				if (0 != imageItem.size() - 1) {
					// System.out.println("=================Ҫ�ϴ�" +
					// imageFiles.size() + " ����Ƭ");
					for (int i = 0; i < imageFiles.size(); ++i) {
						// �ϴ�����ͼƬ���ж�ͼƬ�Ƿ���Ҫ�ü���
						if (imageFiles.get(i).length() > 2048) {
							// 1.���ش���һ���µı��ü�����ͼƬ�ļ�
							// TODO ���԰ѻ���ͼƬ��������Ȧ����ͼƬ�ļ��У���ˢ��ʱ���Դӻ�����ȡ��
							// ���ֳ�������Ҫ��֤���Լ���״̬�ſ���ȡ���ػ��棨��ʱ����ͼƬû��url��������Ϣ��Ҳ����Ҫ��Щ��Ϣ����
							// ��������Լ���״̬�����Ҫƥ�仺��ͼƬ�͸��µ�ͼƬ��Ϣһ�²ſ��Լ��ػ���ͼƬ��
//							bitmap = ImageUtils.compressImageFromFile(imageFiles.get(i).getAbsolutePath());
							bitmap = ImageUtils.getSmallBitmap(imageFiles.get(i).getAbsolutePath());
							fileName = ImageUtils.bitmap2file(ImageUtils.rotateBitmap(bitmap, 
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
		 * ����Ĭ��ͼƬ���ͼƬ�Ӻ� ��ͨ��������ʵ�� SimpleAdapter����imageItemΪ����Դ
		 * R.layout.griditem_addpicΪ����
		 */
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic); // �Ӻ�
		imageItem = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("itemImage", bmp);
		imageItem.add(map);
		simpleAdapter = new SimpleAdapter(this, imageItem, R.layout.griditem_addpic, new String[] { "itemImage" },
				new int[] { R.id.imageView1 });
		/*
		 * HashMap����bmpͼƬ��GridView�в���ʾ,�������������ԴID����ʾ �� map.put("itemImage",
		 * R.drawable.img); �������: 1.�Զ���̳�BaseAdapterʵ�� 2.ViewBinder()�ӿ�ʵ�� �ο�
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
		 * ����GridView����¼� ����:�ú���������󷽷� ����Ҫ�ֶ�����import android.view.View;
		 */
		gridView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				
				 if (position == 0) {
					// // �����0��ͼƬ���Ӻ�ͼƬ����������ᡣ
					// Toast.makeText(PostActivity.this, "���ͼƬ",
					// Toast.LENGTH_SHORT).show();
					// // ѡ��ͼƬ
					// Intent intent = new Intent(Intent.ACTION_PICK,
					// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					// startActivityForResult(intent, IMAGE_OPEN);
					// // ͨ��onResume()ˢ������
					if (imageItem.size() == 10) {
						Toast.makeText(PostActivity.this, "ͼƬ��9������!", Toast.LENGTH_SHORT).show();
						return;
					}
					Intent intent = new Intent();
					intent.setClass(PostActivity.this, ShowPicturesActivity.class);
					intent.putExtra(KEY_TO_SHOW_PICTURES_ACTIVITY , 10 - imageItem.size());
					startActivityForResult(intent, SHOW_PICTURE_CODE);
				} else {
					// ��ʾ�Ƿ��Ƴ���Ӻõ�ͼƬ
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

	// ����ҳ��ʾ
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

			adapter = new MyEmojiAdapter(this, emotionList.subList(start, end)); // (0,21)
																					// (21,42)
																					// (42,50)

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
			// viewPager.addView(view);
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

	// ��ȡͼƬ·�� ��ӦstartActivityForResult
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SHOW_PICTURE_CODE && resultCode == ShowPicturesActivity.RETURN_CODE) {
			System.out.println("�õ����صĽ��");
			Bundle bundle = data.getExtras();
			pathImage = new ArrayList<String>();
			pathImage.addAll((List<String>) bundle.get("pictures"));

			for (int i = 0; i < pathImage.size(); ++i) {
				System.out.println(pathImage.get(i));
				imageFiles.add(new File(pathImage.get(i)));
			}
		}

		// ��ͼƬ
		// if (resultCode == RESULT_OK && requestCode == IMAGE_OPEN) {
		// Uri uri = data.getData();
		//
		// if (!TextUtils.isEmpty(uri.getAuthority())) {
		// System.out.println("-------Ҫ�ϴ���ͼƬ------------->" + uri);
		// // ��ѯѡ��ͼƬ
		// Cursor cursor = getContentResolver().query(uri, new String[] {
		// MediaStore.Images.Media.DATA }, null,
		// null, null);
		// // ���� û�ҵ�ѡ��ͼƬ
		// if (null == cursor) {
		// return;
		// }
		// // ����ƶ�����ͷ ��ȡͼƬ·��
		// cursor.moveToFirst();
		// pathImage =
		// cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
		// File f = new File(pathImage);
		// imageFiles.add(f);
		//
		// }
		// } // end if ��ͼƬ

	}

	// ˢ��ͼƬ
	@Override
	protected void onResume() {
		super.onResume();
		if (pathImage == null) {
			return;
		}
		Bitmap addbmp;
		for (int i = 0; i < pathImage.size(); ++i) {
			if (!TextUtils.isEmpty(pathImage.get(i))) {
//				addbmp = ImageUtils.comp(BitmapFactory.decodeFile(pathImage.get(i)));
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
		// ˢ�º��ͷŷ�ֹ�ֻ����ߺ��Զ����
		pathImage = null;

	}

	/*
	 * Dialog�Ի�����ʾ�û�ɾ������ positionΪɾ��ͼƬλ��
	 */
	protected void dialog(final int position) {
		AlertDialog.Builder builder = new Builder(PostActivity.this);
		builder.setMessage("ȷ��Ҫ�Ƴ������ͼƬ��");
		builder.setTitle("��ʾ");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				imageItem.remove(position);
				simpleAdapter.notifyDataSetChanged();
				// �Ƴ�imageFils�ж�Ӧ���ļ�
				imageFiles.remove(position - 1);
				// ɾ��ͼƬ����
//				if (null != mapImageCached.get(imageFiles.get(position - 1).getName()))
//					mapImageCached.remove(imageFiles.get(position - 1).getName());
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
			public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		// ���������ڱ༭������ӱ��顣
		EmotionInfo emoji = emotionList.get(pageNum * 21 + arg2);
		
		// �õ�����ͼƬ��SpannableString����
		SpannableString res = addFace(this, emoji.getImageName(), emoji.getText());

		if (res != null) 
			insertPhotoToEditText(res);
		
//		System.out.println("content: " + content.getText().toString());
	}
	
	/**
	 * ��ͼƬ������뵽��괦
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
	 * @param imageName 		ͼƬ����
	 * @param spannableString 	�ѱ༭���ַ���
	 * @return
	 */
	public SpannableString addFace(Context context, String imageName, String spannableString) {
		if (TextUtils.isEmpty(spannableString)) 
			return null;
		
		System.out.println("Add Face! spannableString: " + spannableString);
		
//		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imgId);
		String localPath = getApplicationContext().getFilesDir().getAbsolutePath(); // �����ľ���·��: /data/data/����/
		
		Bitmap bitmap = BitmapFactory.decodeFile(localPath + "/emoticon/" + imageName);

		bitmap = Bitmap.createScaledBitmap(bitmap, EmotionUtils.dip2px(context, 25), EmotionUtils.dip2px(context, 25),
				true);
		// bitmap = Bitmap.createScaledBitmap(bitmap, 35, 35, true);
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
				Toast.makeText(PostActivity.this, "û�б�������������أ�����", Toast.LENGTH_SHORT).show();
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
