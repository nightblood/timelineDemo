package com.zlf.testdemo01;

import java.util.List;

import com.zlf.testdemo01.domain.EmotionInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MyEmojiAdapter extends BaseAdapter{

	private List<EmotionInfo> data;

	private LayoutInflater inflater;

	private int size = 0;

	public MyEmojiAdapter(Context c, List<EmotionInfo> list) {
		inflater = LayoutInflater.from(c);
		data = list;
		size = list.size();
	}
	@Override
	public int getCount() {

		return size;
	}

	@Override
	public Object getItem(int arg0) {

		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup group) {

		Bitmap bm;
		ViewHolder vh;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.emotion, null);
			vh = new ViewHolder();
							
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		
		vh.iv = (ImageView) convertView.findViewById(R.id.image);
		vh.iv.setTag(data.get(position));
		bm = BitmapFactory.decodeFile(MainActivity.localPath + "/emoticon/" + data.get(position).getImageName());
		
//		System.out.println(MainActivity.localPath + "/emoticon/" + data.get(position).getImageName());
		
		vh.iv.setImageBitmap(bm);
		
//		vh.iv.setImageResource(R.drawable.ic_launcher);
		convertView.setTag(vh);
		
		return convertView;
	}

	class ViewHolder{
		ImageView iv;
	}

}
