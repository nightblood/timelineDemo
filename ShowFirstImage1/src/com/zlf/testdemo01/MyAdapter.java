package com.zlf.testdemo01;

import java.util.List;

import com.lidroid.xutils.BitmapUtils;
import com.zlf.testdemo01.domain.FriendInfo;
import com.zlf.testdemo01.domain.ImageInfo;

import android.content.Context;
import android.os.Handler;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private List<FriendInfo> friendsList;
	public Context context;

	public enum VIEW_TYPE {
		LAYOUT_TYPE_NO_IMAGE, 
		LAYOUT_TYPE_1_IMAGE, 
		LAYOUT_TYPE_2_IMAGE, 
		LAYOUT_TYPE_3_IMAGE,
		LAYOUT_TYPE_4_IMAGE,
		LAYOUT_TYPE_5_IMAGE,
		LAYOUT_TYPE_6_IMAGE,
		LAYOUT_TYPE_7_IMAGE,
		LAYOUT_TYPE_8_IMAGE,
		LAYOUT_TYPE_9_IMAGE,
		LAYOUT_BUTTOM;
	}

	public MyAdapter(List<FriendInfo> list, Context context) {
		friendsList = list;
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return friendsList.size();
	}

	@Override
	public Object getItem(int position) {
		return friendsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolderNoImg holderNoImg = null;
		ViewHolder1Img holder1Img = null;
		ViewHolder2Img holder2Img = null;
		ViewHolder3Img holder3Img = null;
		ViewHolder4Img holder4Img = null;
		ViewHolder5Img holder5Img = null;
		ViewHolder6Img holder6Img = null;
		ViewHolder7Img holder7Img = null;
		ViewHolder8Img holder8Img = null;
		ViewHolder9Img holder9Img = null;
		EmotionUtils emotionUtils = new EmotionUtils(context);
		SpannableString content;
		BitmapUtils bitmapUtils = new BitmapUtils(context);

		System.out.println(position + "th. id:" + friendsList.get(position).getId());
		// System.out.println("++++++++++++++++++++get view!");
		// System.out.println("---------------getView-" +
		// EmotionUtils.dip2px(context, 50));

		List<ImageInfo> images = friendsList.get(position).getImages();
		int layoutType = getItemViewType(position);
		
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			// System.out.println(">>>>>>convertView is null!!! Layout Type: " + layoutType);
			if (layoutType == VIEW_TYPE.LAYOUT_TYPE_NO_IMAGE.ordinal()){
				convertView = mInflater.inflate(R.layout.moment_with_no_image, null);
				holderNoImg = new ViewHolderNoImg();
				holderNoImg.name = (TextView) convertView.findViewById(R.id.name);
				holderNoImg.content = (TextView) convertView.findViewById(R.id.content);
				holderNoImg.icon = (ImageView) convertView.findViewById(R.id.icon);

				bitmapUtils.display(holderNoImg.icon, friendsList.get(position).getIcon());

				holderNoImg.name.setText(friendsList.get(position).getName());

				if (friendsList.get(position).getContent() != null) {
					content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
					holderNoImg.content.setText(content);
				}

				convertView.setTag(holderNoImg);
			} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_1_IMAGE.ordinal()){
				convertView = mInflater.inflate(R.layout.moment_with_1_image, null);
				holder1Img = new ViewHolder1Img();

				holder1Img.image1 = (ImageView) convertView.findViewById(R.id.image1);
				holder1Img.name = (TextView) convertView.findViewById(R.id.name);
				holder1Img.content = (TextView) convertView.findViewById(R.id.content);
				holder1Img.icon = (ImageView) convertView.findViewById(R.id.icon);

				holder1Img.name.setText(friendsList.get(position).getName());
				if (friendsList.get(position).getContent() != null) {
					content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
					holder1Img.content.setText(content);
				}

				bitmapUtils.display(holder1Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder1Img.image1, images.get(0).getUrl());

				convertView.setTag(holder1Img);
			} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_2_IMAGE.ordinal()) {
			
				convertView = mInflater.inflate(R.layout.moment_with_2_image, null);
				holder2Img = new ViewHolder2Img();
				holder2Img.image1 = (ImageView) convertView.findViewById(R.id.image1);
				holder2Img.image2 = (ImageView) convertView.findViewById(R.id.image2);
				holder2Img.name = (TextView) convertView.findViewById(R.id.name);
				holder2Img.content = (TextView) convertView.findViewById(R.id.content);
				holder2Img.icon = (ImageView) convertView.findViewById(R.id.icon);

				holder2Img.name.setText(friendsList.get(position).getName());
				if (friendsList.get(position).getContent() != null) {
					content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
					holder2Img.content.setText(content);
				}
				bitmapUtils.display(holder2Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder2Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder2Img.image2, images.get(1).getUrl());

				convertView.setTag(holder2Img);
			} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_3_IMAGE.ordinal()) {
				convertView = mInflater.inflate(R.layout.moment_with_3_image, null);
				holder3Img = new ViewHolder3Img();
				holder3Img.image1 = (ImageView) convertView.findViewById(R.id.image1);
				holder3Img.image2 = (ImageView) convertView.findViewById(R.id.image2);
				holder3Img.image3 = (ImageView) convertView.findViewById(R.id.image3);
				holder3Img.name = (TextView) convertView.findViewById(R.id.name);
				holder3Img.content = (TextView) convertView.findViewById(R.id.content);
				holder3Img.icon = (ImageView) convertView.findViewById(R.id.icon);

				holder3Img.name.setText(friendsList.get(position).getName());
				if (friendsList.get(position).getContent() != null) {
					content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
					holder3Img.content.setText(content);
				}
				bitmapUtils.display(holder3Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder3Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder3Img.image2, images.get(1).getUrl());
				bitmapUtils.display(holder3Img.image3, images.get(2).getUrl());

				convertView.setTag(holder3Img);
			} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_4_IMAGE.ordinal()) {
				convertView = mInflater.inflate(R.layout.moment_with_4_image, null);
				holder4Img = new ViewHolder4Img();
				holder4Img.image1 = (ImageView) convertView.findViewById(R.id.image1);
				holder4Img.image2 = (ImageView) convertView.findViewById(R.id.image2);
				holder4Img.image3 = (ImageView) convertView.findViewById(R.id.image3);
				holder4Img.image4 = (ImageView) convertView.findViewById(R.id.image4);
				holder4Img.name = (TextView) convertView.findViewById(R.id.name);
				holder4Img.content = (TextView) convertView.findViewById(R.id.content);
				holder4Img.icon = (ImageView) convertView.findViewById(R.id.icon);

				holder4Img.name.setText(friendsList.get(position).getName());
				if (friendsList.get(position).getContent() != null) {
					content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
					holder4Img.content.setText(content);
				}

				bitmapUtils.display(holder4Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder4Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder4Img.image2, images.get(1).getUrl());
				bitmapUtils.display(holder4Img.image3, images.get(2).getUrl());
				bitmapUtils.display(holder4Img.image4, images.get(3).getUrl());

				convertView.setTag(holder4Img);
			} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_5_IMAGE.ordinal()) {
				convertView = mInflater.inflate(R.layout.moment_with_5_image, null);
				holder5Img = new ViewHolder5Img();
				holder5Img.image1 = (ImageView) convertView.findViewById(R.id.image1);
				holder5Img.image2 = (ImageView) convertView.findViewById(R.id.image2);
				holder5Img.image3 = (ImageView) convertView.findViewById(R.id.image3);
				holder5Img.image4 = (ImageView) convertView.findViewById(R.id.image4);
				holder5Img.image5 = (ImageView) convertView.findViewById(R.id.image5);

				holder5Img.name = (TextView) convertView.findViewById(R.id.name);
				holder5Img.content = (TextView) convertView.findViewById(R.id.content);
				holder5Img.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder5Img.name.setText(friendsList.get(position).getName());

				if (friendsList.get(position).getContent() != null) {
					content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
					holder5Img.content.setText(content);
				}
				bitmapUtils.display(holder5Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder5Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder5Img.image2, images.get(1).getUrl());
				bitmapUtils.display(holder5Img.image3, images.get(2).getUrl());
				bitmapUtils.display(holder5Img.image4, images.get(3).getUrl());
				bitmapUtils.display(holder5Img.image5, images.get(4).getUrl());
				convertView.setTag(holder5Img);
			} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_6_IMAGE.ordinal()) {
				convertView = mInflater.inflate(R.layout.moment_with_6_image, null);
				holder6Img = new ViewHolder6Img();
				holder6Img.image1 = (ImageView) convertView.findViewById(R.id.image1);
				holder6Img.image2 = (ImageView) convertView.findViewById(R.id.image2);
				holder6Img.image3 = (ImageView) convertView.findViewById(R.id.image3);
				holder6Img.image4 = (ImageView) convertView.findViewById(R.id.image4);
				holder6Img.image5 = (ImageView) convertView.findViewById(R.id.image5);
				holder6Img.image6 = (ImageView) convertView.findViewById(R.id.image6);

				holder6Img.name = (TextView) convertView.findViewById(R.id.name);
				holder6Img.content = (TextView) convertView.findViewById(R.id.content);
				holder6Img.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder6Img.name.setText(friendsList.get(position).getName());

				if (friendsList.get(position).getContent() != null) {
					content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
					holder6Img.content.setText(content);
				}
				bitmapUtils.display(holder6Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder6Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder6Img.image2, images.get(1).getUrl());
				bitmapUtils.display(holder6Img.image3, images.get(2).getUrl());
				bitmapUtils.display(holder6Img.image4, images.get(3).getUrl());
				bitmapUtils.display(holder6Img.image5, images.get(4).getUrl());
				bitmapUtils.display(holder6Img.image6, images.get(5).getUrl());
				convertView.setTag(holder6Img);
			} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_7_IMAGE.ordinal()) {
				convertView = mInflater.inflate(R.layout.moment_with_7_image, null);
				holder7Img = new ViewHolder7Img();
				holder7Img.image1 = (ImageView) convertView.findViewById(R.id.image1);
				holder7Img.image2 = (ImageView) convertView.findViewById(R.id.image2);
				holder7Img.image3 = (ImageView) convertView.findViewById(R.id.image3);
				holder7Img.image4 = (ImageView) convertView.findViewById(R.id.image4);
				holder7Img.image5 = (ImageView) convertView.findViewById(R.id.image5);
				holder7Img.image6 = (ImageView) convertView.findViewById(R.id.image6);
				holder7Img.image7 = (ImageView) convertView.findViewById(R.id.image7);

				holder7Img.name = (TextView) convertView.findViewById(R.id.name);
				holder7Img.content = (TextView) convertView.findViewById(R.id.content);
				holder7Img.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder7Img.name.setText(friendsList.get(position).getName());

				if (friendsList.get(position).getContent() != null) {
					content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
					holder7Img.content.setText(content);
				}

				bitmapUtils.display(holder7Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder7Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder7Img.image2, images.get(1).getUrl());
				bitmapUtils.display(holder7Img.image3, images.get(2).getUrl());
				bitmapUtils.display(holder7Img.image4, images.get(3).getUrl());
				bitmapUtils.display(holder7Img.image5, images.get(4).getUrl());
				bitmapUtils.display(holder7Img.image6, images.get(5).getUrl());
				bitmapUtils.display(holder7Img.image7, images.get(6).getUrl());
				convertView.setTag(holder7Img);
			} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_8_IMAGE.ordinal()) {
				convertView = mInflater.inflate(R.layout.moment_with_8_image, null);
				holder8Img = new ViewHolder8Img();
				holder8Img.image1 = (ImageView) convertView.findViewById(R.id.image1);
				holder8Img.image2 = (ImageView) convertView.findViewById(R.id.image2);
				holder8Img.image3 = (ImageView) convertView.findViewById(R.id.image3);
				holder8Img.image4 = (ImageView) convertView.findViewById(R.id.image4);
				holder8Img.image5 = (ImageView) convertView.findViewById(R.id.image5);
				holder8Img.image6 = (ImageView) convertView.findViewById(R.id.image6);
				holder8Img.image7 = (ImageView) convertView.findViewById(R.id.image7);
				holder8Img.image8 = (ImageView) convertView.findViewById(R.id.image8);

				holder8Img.name = (TextView) convertView.findViewById(R.id.name);
				holder8Img.content = (TextView) convertView.findViewById(R.id.content);
				holder8Img.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder8Img.name.setText(friendsList.get(position).getName());

				if (friendsList.get(position).getContent() != null) {
					content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
					holder8Img.content.setText(content);
				}

				bitmapUtils.display(holder8Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder8Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder8Img.image2, images.get(1).getUrl());
				bitmapUtils.display(holder8Img.image3, images.get(2).getUrl());
				bitmapUtils.display(holder8Img.image4, images.get(3).getUrl());
				bitmapUtils.display(holder8Img.image5, images.get(4).getUrl());
				bitmapUtils.display(holder8Img.image6, images.get(5).getUrl());
				bitmapUtils.display(holder8Img.image7, images.get(6).getUrl());
				bitmapUtils.display(holder8Img.image8, images.get(7).getUrl());
				convertView.setTag(holder8Img);
			} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_9_IMAGE.ordinal()) {
				convertView = mInflater.inflate(R.layout.moment_with_9_image, null);
				holder9Img = new ViewHolder9Img();
				holder9Img.image1 = (ImageView) convertView.findViewById(R.id.image1);
				holder9Img.image2 = (ImageView) convertView.findViewById(R.id.image2);
				holder9Img.image3 = (ImageView) convertView.findViewById(R.id.image3);
				holder9Img.image4 = (ImageView) convertView.findViewById(R.id.image4);
				holder9Img.image5 = (ImageView) convertView.findViewById(R.id.image5);
				holder9Img.image6 = (ImageView) convertView.findViewById(R.id.image6);
				holder9Img.image7 = (ImageView) convertView.findViewById(R.id.image7);
				holder9Img.image8 = (ImageView) convertView.findViewById(R.id.image8);
				holder9Img.image9 = (ImageView) convertView.findViewById(R.id.image9);

				holder9Img.name = (TextView) convertView.findViewById(R.id.name);
				holder9Img.content = (TextView) convertView.findViewById(R.id.content);
				holder9Img.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder9Img.name.setText(friendsList.get(position).getName());

				if (friendsList.get(position).getContent() != null) {
					content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
					holder9Img.content.setText(content);
				}

				bitmapUtils.display(holder9Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder9Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder9Img.image2, images.get(1).getUrl());
				bitmapUtils.display(holder9Img.image3, images.get(2).getUrl());
				bitmapUtils.display(holder9Img.image4, images.get(3).getUrl());
				bitmapUtils.display(holder9Img.image5, images.get(4).getUrl());
				bitmapUtils.display(holder9Img.image6, images.get(5).getUrl());
				bitmapUtils.display(holder9Img.image7, images.get(6).getUrl());
				bitmapUtils.display(holder9Img.image8, images.get(7).getUrl());
				bitmapUtils.display(holder9Img.image9, images.get(8).getUrl());
				convertView.setTag(holder9Img);
			
			}
			// System.out.println("----------getView end!!");
		} else {
			// System.out.println(position + ". ConvertView not null");
		if (layoutType == VIEW_TYPE.LAYOUT_TYPE_NO_IMAGE.ordinal()) {

				holderNoImg = (ViewHolderNoImg) convertView.getTag();
				holderNoImg.name.setText(friendsList.get(position).getName());
				content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
				holderNoImg.content.setText(content);

				bitmapUtils.display(holderNoImg.icon, friendsList.get(position).getIcon());
		} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_1_IMAGE.ordinal()) {
				holder1Img = (ViewHolder1Img) convertView.getTag();
				holder1Img.name.setText(friendsList.get(position).getName());
				content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
				holder1Img.content.setText(content);

				bitmapUtils.display(holder1Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder1Img.image1, images.get(0).getUrl());
		} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_2_IMAGE.ordinal()) {
				holder2Img = (ViewHolder2Img) convertView.getTag();
				holder2Img.name.setText(friendsList.get(position).getName());
				content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
				holder2Img.content.setText(content);

				bitmapUtils.display(holder2Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder2Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder2Img.image2, images.get(1).getUrl());

		} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_3_IMAGE.ordinal()) {
				holder3Img = (ViewHolder3Img) convertView.getTag();
				holder3Img.name.setText(friendsList.get(position).getName());
				content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
				holder3Img.content.setText(content);

				bitmapUtils.display(holder3Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder3Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder3Img.image2, images.get(1).getUrl());
				bitmapUtils.display(holder3Img.image3, images.get(2).getUrl());
		} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_4_IMAGE.ordinal()) {
				holder4Img = (ViewHolder4Img) convertView.getTag();
				holder4Img.name.setText(friendsList.get(position).getName());
				content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
				holder4Img.content.setText(content);

				bitmapUtils.display(holder4Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder4Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder4Img.image2, images.get(1).getUrl());
				bitmapUtils.display(holder4Img.image3, images.get(2).getUrl());
				bitmapUtils.display(holder4Img.image4, images.get(3).getUrl());
		} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_5_IMAGE.ordinal()) {
				holder5Img = (ViewHolder5Img) convertView.getTag();
				holder5Img.name.setText(friendsList.get(position).getName());
				content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
				holder5Img.content.setText(content);

				bitmapUtils.display(holder5Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder5Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder5Img.image2, images.get(1).getUrl());
				bitmapUtils.display(holder5Img.image3, images.get(2).getUrl());
				bitmapUtils.display(holder5Img.image4, images.get(3).getUrl());
				bitmapUtils.display(holder5Img.image5, images.get(4).getUrl());
		} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_6_IMAGE.ordinal()) {
				holder6Img = (ViewHolder6Img) convertView.getTag();
				holder6Img.name.setText(friendsList.get(position).getName());
				content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
				holder6Img.content.setText(content);

				bitmapUtils.display(holder6Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder6Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder6Img.image2, images.get(1).getUrl());
				bitmapUtils.display(holder6Img.image3, images.get(2).getUrl());
				bitmapUtils.display(holder6Img.image4, images.get(3).getUrl());
				bitmapUtils.display(holder6Img.image5, images.get(4).getUrl());
				bitmapUtils.display(holder6Img.image6, images.get(5).getUrl());
		} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_7_IMAGE.ordinal()) {
				holder7Img = (ViewHolder7Img) convertView.getTag();
				holder7Img.name.setText(friendsList.get(position).getName());
				content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
				holder7Img.content.setText(content);

				bitmapUtils.display(holder7Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder7Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder7Img.image2, images.get(1).getUrl());
				bitmapUtils.display(holder7Img.image3, images.get(2).getUrl());
				bitmapUtils.display(holder7Img.image4, images.get(3).getUrl());
				bitmapUtils.display(holder7Img.image5, images.get(4).getUrl());
				bitmapUtils.display(holder7Img.image6, images.get(5).getUrl());
				bitmapUtils.display(holder7Img.image7, images.get(6).getUrl());
		} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_8_IMAGE.ordinal()) {
				holder8Img = (ViewHolder8Img) convertView.getTag();
				holder8Img.name.setText(friendsList.get(position).getName());
				content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
				holder8Img.content.setText(content);

				bitmapUtils.display(holder8Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder8Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder8Img.image2, images.get(1).getUrl());
				bitmapUtils.display(holder8Img.image3, images.get(2).getUrl());
				bitmapUtils.display(holder8Img.image4, images.get(3).getUrl());
				bitmapUtils.display(holder8Img.image5, images.get(4).getUrl());
				bitmapUtils.display(holder8Img.image6, images.get(5).getUrl());
				bitmapUtils.display(holder8Img.image7, images.get(6).getUrl());
				bitmapUtils.display(holder8Img.image8, images.get(7).getUrl());
		} else if (layoutType == VIEW_TYPE.LAYOUT_TYPE_9_IMAGE.ordinal()) {
				holder9Img = (ViewHolder9Img) convertView.getTag();
				holder9Img.name.setText(friendsList.get(position).getName());
				content = emotionUtils.getExpressionString(context, friendsList.get(position).getContent());
				holder9Img.content.setText(content);

				bitmapUtils.display(holder9Img.icon, friendsList.get(position).getIcon());
				bitmapUtils.display(holder9Img.image1, images.get(0).getUrl());
				bitmapUtils.display(holder9Img.image2, images.get(1).getUrl());
				bitmapUtils.display(holder9Img.image3, images.get(2).getUrl());
				bitmapUtils.display(holder9Img.image4, images.get(3).getUrl());
				bitmapUtils.display(holder9Img.image5, images.get(4).getUrl());
				bitmapUtils.display(holder9Img.image6, images.get(5).getUrl());
				bitmapUtils.display(holder9Img.image7, images.get(6).getUrl());
				bitmapUtils.display(holder9Img.image8, images.get(7).getUrl());
				bitmapUtils.display(holder9Img.image9, images.get(8).getUrl());
			
			}
		}
		// friendsList.get(position).setImagesCached(true);
		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		return friendsList.get(position).getLayoutType();
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE.LAYOUT_BUTTOM.ordinal();
	}

	class ViewHolderNoImg {
		ImageView icon;
		TextView name;
		TextView content;
	}

	class ViewHolder1Img extends ViewHolderNoImg {
		ImageView image1;
	}

	class ViewHolder2Img extends ViewHolderNoImg {
		ImageView image1;
		ImageView image2;
	}

	class ViewHolder3Img extends ViewHolderNoImg {
		ImageView image1;
		ImageView image2;
		ImageView image3;
	}

	class ViewHolder4Img extends ViewHolderNoImg {
		ImageView image1;
		ImageView image2;
		ImageView image3;
		ImageView image4;
	}

	class ViewHolder5Img extends ViewHolderNoImg {
		ImageView image1;
		ImageView image2;
		ImageView image3;
		ImageView image4;
		ImageView image5;
	}

	class ViewHolder6Img extends ViewHolderNoImg {
		ImageView image1;
		ImageView image2;
		ImageView image3;
		ImageView image4;
		ImageView image5;
		ImageView image6;
	}

	class ViewHolder7Img extends ViewHolderNoImg {
		ImageView image1;
		ImageView image2;
		ImageView image3;
		ImageView image4;
		ImageView image5;
		ImageView image6;
		ImageView image7;
	}

	class ViewHolder8Img extends ViewHolderNoImg {
		ImageView image1;
		ImageView image2;
		ImageView image3;
		ImageView image4;
		ImageView image5;
		ImageView image6;
		ImageView image7;
		ImageView image8;
	}

	class ViewHolder9Img extends ViewHolderNoImg {
		ImageView image1;
		ImageView image2;
		ImageView image3;
		ImageView image4;
		ImageView image5;
		ImageView image6;
		ImageView image7;
		ImageView image8;
		ImageView image9;
	}
}
