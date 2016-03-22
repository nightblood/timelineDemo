package com.zlf.testdemo01.utils;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zlf.testdemo01.EmotionUtils;
import com.zlf.testdemo01.domain.EmotionInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

public class EmojiParser {
	// Singleton stuff
	private static EmojiParser sInstance;

	private static List<EmotionInfo> emojiList;

	public static EmojiParser getInstance(Context c) {
		mContext = c;
		return sInstance;
	}

	public static void initEmojiList(List<EmotionInfo> emojiList) {
		EmojiParser.emojiList = emojiList;
	}
	public static void init(Context context) {
		sInstance = new EmojiParser(context);
	}

	private static Context mContext;
	private static String[] mSmileyTexts;
	private static Pattern mPattern = null;
	private static HashMap<String, String> mSmileyPath = null;

	private EmojiParser(Context context) {
		mContext = context;
		mSmileyTexts = new String[emojiList.size()];
		
		for (int i = 0; i < emojiList.size(); ++i) {
			mSmileyTexts[i] = emojiList.get(i).getText();
		}
		mSmileyPath = buildSmileyPath();
		mPattern = buildPattern();
	}

	private HashMap<String, String> buildSmileyPath() {
		HashMap<String, String> map = new HashMap<String, String>(emojiList.size());
		for (int i = 0; i < emojiList.size(); ++i) {
			map.put(mSmileyTexts[i], emojiList.get(i).getImageName());
		}
		return map;
	}

	// 构建正则表达式
	private Pattern buildPattern() {
		StringBuilder patternString = new StringBuilder(mSmileyTexts.length * 3);
		patternString.append('(');
		for (String s : mSmileyTexts) {
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		patternString.replace(patternString.length() - 1, patternString.length(), ")");
		return Pattern.compile(patternString.toString());
	}
	// 根据文本替换成图片
	public CharSequence addSmileySpans(CharSequence text) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Matcher matcher = mPattern.matcher(text);
		while (matcher.find()) {
			// int resId = mSmileyToRes.get(matcher.group());
			String emojiPath = mSmileyPath.get(matcher.group());

			String localPath = mContext.getApplicationContext().getFilesDir().getAbsolutePath(); // 包名的绝对路径:
			// /data/data/包名/

			Bitmap bitmap = BitmapFactory.decodeFile(localPath + "/emoticon/" + emojiPath);

			bitmap = Bitmap.createScaledBitmap(bitmap, EmotionUtils.dip2px(mContext, 25),
					EmotionUtils.dip2px(mContext, 25), true);
			// bitmap = Bitmap.createScaledBitmap(bitmap, 35, 35, true);
			Drawable drawable = new BitmapDrawable(bitmap);
			drawable.setBounds(0, 0, EmotionUtils.dip2px(mContext, 25), EmotionUtils.dip2px(mContext, 25));
			ImageSpan imageSpan = new ImageSpan(drawable);

			builder.setSpan(imageSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return builder;
	}

}
