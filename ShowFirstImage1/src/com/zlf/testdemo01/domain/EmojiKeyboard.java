package com.zlf.testdemo01.domain;

import java.util.ArrayList;
import java.util.List;

import com.zlf.testdemo01.EmotionUtils;
import com.zlf.testdemo01.ImageUtils;
import com.zlf.testdemo01.MyEmojiAdapter;
import com.zlf.testdemo01.MyEmojiViewPagerAdapter;
import com.zlf.testdemo01.R;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class EmojiKeyboard {

	private Context mContext;
	private int pageNum = 0;
	private ArrayList<View> cursorViews;
	private ArrayList<View> views;
	private ViewPager viewPager;
	private LinearLayout emojiCursor;
	
	public static List<EmotionInfo> emotionList;
	
	public EmojiKeyboard(Context c, ViewPager viewPager, LinearLayout emojiCursor) {
		mContext = c;
		this.viewPager = viewPager;
		this.emojiCursor = emojiCursor;
	}
	
	public void initEmojiView() {
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
			view = new GridView(mContext);

			adapter = new MyEmojiAdapter(mContext, emotionList.subList(start, end)); // (0,20)
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
			view.setOnItemClickListener((OnItemClickListener) mContext);

			view.setAdapter(adapter);
			views.add(view);
		}
		viewPager.setAdapter(new MyEmojiViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				setPageNum(arg0);
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
			imageView = new ImageView(mContext);
			lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.leftMargin = ImageUtils.dip2px(mContext, 5);
			lp.rightMargin = ImageUtils.dip2px(mContext, 5);
			lp.width = ImageUtils.dip2px(mContext, 5);
			lp.height = ImageUtils.dip2px(mContext, 5);

			emojiCursor.addView(imageView, lp);
			if (i == 0) {
				imageView.setBackgroundResource(R.drawable.cursor2);
			} else {
				imageView.setBackgroundResource(R.drawable.cursor1);
			}
			cursorViews.add(imageView);
		}
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	
	public void clickEmoji(EditText content, int pageNum, int position) {

		// 点击表情后，在编辑框中添加表情。
		EmotionInfo emoji = emotionList.get(pageNum * 21 + position);

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
		SpannableString res = addFace(mContext, emoji.getImageName(), emoji.getText());

		if (res != null)
			insertPhotoToEditText(content, res);
	}
	private void insertPhotoToEditText(EditText content, SpannableString ss) {
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

		String localPath = mContext.getApplicationContext().getFilesDir().getAbsolutePath(); // 包名的绝对路径:
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

}
