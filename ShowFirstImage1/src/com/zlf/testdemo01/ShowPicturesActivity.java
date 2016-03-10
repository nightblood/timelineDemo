package com.zlf.testdemo01;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zlf.testdemo01.ListImageDirPopupWindow.OnImageDirSelected;
import com.zlf.testdemo01.domain.ImageFloder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowPicturesActivity extends Activity implements OnImageDirSelected
{
	
	public static final int RETURN_CODE = 0xcc;
	private ProgressDialog mProgressDialog;

	private int mPicsSize;
	
	private File mImgDir;
	
	private List<String> mImgs;

	private GridView mGirdView;
	private ShowPictureAdapter mAdapter;
	
	private HashSet<String> mDirPaths = new HashSet<String>();

	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

	private RelativeLayout mBottomLy;

	private TextView mChooseDir;
	private TextView mImageCount;
	int totalCount = 0;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;
	private Button completeBtn;
	private Button returnBtn;
	public static int totalPictureCount = 9;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0xf) {
				if (msg.arg1 == 0) {
					completeBtn.setText("完成  ");
				} else {
					completeBtn.setText("完成  " + msg.arg1 + "/" + totalPictureCount);
				}
				return;
			}
			// 加载完成后，显示图片
			mProgressDialog.dismiss();
		
			data2View();
			
			initListDirPopupWindw();
		}
	};
	
	/**
	 * 刚刚启动activity时的第一次显示
	 * 绑定GridView的适配器，显示图片
	 */
	private void data2View() {
		if (mImgDir == null) {
			Toast.makeText(getApplicationContext(), "没有图片", Toast.LENGTH_SHORT).show();
			return;
		}

		mImgs = Arrays.asList(mImgDir.list());
	
		mAdapter = new ShowPictureAdapter(getApplicationContext(), mImgs, R.layout.grid_item, mImgDir.getAbsolutePath(), mHandler);
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText("共" + totalCount + "张");
	};

	
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
	
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_picture);

		Intent intent = getIntent();
		totalPictureCount = intent.getIntExtra(PostActivity.KEY_TO_SHOW_PICTURES_ACTIVITY, 9);
		
		
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

		initView();
		getImages();
		initEvent();

	}

	private void getImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "没有找到外部存储！！！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		mProgressDialog = ProgressDialog.show(this, null, "正在加载。。。");

		new Thread(new Runnable() {
			@Override
			public void run()
			{
				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = ShowPicturesActivity.this.getContentResolver();

				Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",	new String[] { "image/jpeg", "image/png" },
								MediaStore.Images.Media.DATE_MODIFIED);

				Log.e("TAG", mCursor.getCount() + "");
				while (mCursor.moveToNext()) {
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

					Log.e("TAG", path);
					
					if (firstImage == null)
						firstImage = path;
					// 
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					if (parentFile.list() == null) 
						continue;
					
					int picSize = parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename)
						{
							if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;
					
					totalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize) {
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				mCursor.close();

				mDirPaths = null;

				mHandler.sendEmptyMessage(0x110);
			}
		}).start();

	}

	private void initView() {
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.id_total_count);

		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

		completeBtn = (Button) findViewById(R.id.complete);
		returnBtn = (Button) findViewById(R.id.back);
		
		completeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("pictures", (Serializable) ShowPictureAdapter.mSelectedImage);
				intent.putExtras(bundle);
				setResult(RETURN_CODE, intent);
				finish();
				ShowPicturesActivity.this.onDestroy();
			}
		});
		returnBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		ShowPictureAdapter.mSelectedImage.clear();
		super.onResume();
	}
	/**
	 * 点击弹出相册概况的popupwindow事件
	 */
	private void initEvent() {
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
	}

	/* 
	 * 得到folder下的所有图片，并且用GridView绑定适配器显示。
	 */
	@Override
	public void selected(ImageFloder floder) {
		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		
		mAdapter = new ShowPictureAdapter(getApplicationContext(), mImgs, R.layout.grid_item, mImgDir.getAbsolutePath(), mHandler);
		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText("共" + floder.getCount() + "张");
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();
	}

}
