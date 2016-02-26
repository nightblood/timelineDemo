package com.zlf.testdemo01;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zlf.testdemo01.domain.EmotionInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;

public class EmotionUtils {

	public EmotionUtils(Context c) {
		scale = c.getResources().getDisplayMetrics().density;
	}
	/**
	 * px转dip
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	private static float scale;
	public static int px2dip(Context context, float pxValue) {
		if (scale == 0)
			scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	public static int dip2px(Context context, float dpValue) {  
		if (scale == 0)
			scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    } 
	/**
	 * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
	 * 
	 * @param context
	 * @param str
	 * @return
	 */
	public SpannableString getExpressionString(Context context, String str) {
		SpannableString spannableString = new SpannableString(str);
		// 正则表达式比配字符串里是否含有表情，如： 我好[开心]啊
		String zhengze = "\\[[^\\]]+\\]";
		// 通过传入的正则表达式来生成一个pattern
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
		try {
			dealExpression(context, spannableString, sinaPatten, 0);
		} catch (Exception e) {
//			Log.e("dealExpression", e.getMessage());
		}
		return spannableString;
	}

	/**
	 * 添加表情
	 * 
	 * @param context
	 * @param imgId
	 * @param spannableString
	 * @return
	 */
	public SpannableString addFace(Context context, int imgId, String spannableString) {
		if (TextUtils.isEmpty(spannableString)) {
			return null;
		}
//		System.out.println("Add Face! spannableString: " + spannableString);

		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imgId);
//		bitmap = Bitmap.createScaledBitmap(bitmap, 35, 35, true);
		bitmap = Bitmap.createScaledBitmap(bitmap, dip2px(context, 25), dip2px(context, 25), true);
		ImageSpan imageSpan = new ImageSpan(context, bitmap);
		SpannableString spannable = new SpannableString(spannableString);
		spannable.setSpan(imageSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}

	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 * 
	 * @param context
	 * @param spannableString
	 * @param patten
	 * @param start
	 * @throws Exception
	 */
	private void dealExpression(Context context, SpannableString spannableString, Pattern patten, int start)
			throws Exception {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			// 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
			if (matcher.start() < start) {
				continue;
			}
			EmotionInfo emoji = null;
			for(EmotionInfo i : PostActivity.emotionList){
				if (i.getText().equals(key)) {
					emoji = i;
					break;
				}
			}
			
			if (emoji == null) 
				continue;
			
			Bitmap bitmap = BitmapFactory.decodeFile(EmotionInfo.emotionPath + emoji.getImageName());
			
//			System.out.println("ffffffffffffffffff"+dip2px(context, 25));
			bitmap = Bitmap.createScaledBitmap(bitmap, dip2px(context, 25), dip2px(context, 25), true);
			// 通过图片资源id来得到bitmap，用一个ImageSpan来包装
			Drawable drawable=new BitmapDrawable(bitmap);
			drawable.setBounds(0, 0, EmotionUtils.dip2px(context, 25), EmotionUtils.dip2px(context, 25));
			ImageSpan imageSpan = new ImageSpan(drawable);
			//ImageSpan imageSpan = new ImageSpan(bitmap);
			// 计算该图片名字的长度，也就是要替换的字符串的长度
			int end = matcher.start() + key.length();
			// 将该图片替换字符串中规定的位置中
			spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			if (end < spannableString.length()) {
				// 如果整个字符串还未验证完，则继续。。
				dealExpression(context, spannableString, patten, end);
			}
			break;
		}
	}
}
