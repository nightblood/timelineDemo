package com.zlf.testdemo01;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zlf.testdemo01.domain.FriendInfo;
import com.zlf.testdemo01.domain.ImageInfo;
import com.zlf.testdemo01.domain.MyViewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyItemAdapter extends BaseAdapter{

	private List<FriendInfo> datas;
	private Context context;
	private ImageLoader imageLoader;
	Handler handler;
	private View v;
	
	public MyItemAdapter(List<FriendInfo> list, Context c) {
		datas = list;
		context = c;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
	}
	public MyItemAdapter(Handler handler, List<FriendInfo> list, Context c) {
		datas = list;
		context = c;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		this.handler = handler;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		final MyViewHolder holder;
		DisplayImageOptions options;
		final List<ImageInfo> images;
		EmotionUtils emotionUtils = new EmotionUtils(context);
		SpannableString content;
		
		if (convertView == null) {
			holder = new MyViewHolder();
			convertView = View.inflate(context, R.layout.item_list, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.gridView = (NoScrollGridView) convertView.findViewById(R.id.gridview);
			holder.time = (TextView) convertView.findViewById(R.id.insert_time);
			holder.comment = (Button) convertView.findViewById(R.id.comment);

			holder.commentView = convertView.findViewById(R.id.comment_view);
			holder.commentBtn = (Button) convertView.findViewById(R.id.comment_button);
			holder.praiseBtn = (Button) convertView.findViewById(R.id.praise_button);
			holder.praiseText = (TextView) convertView.findViewById(R.id.praise_text);
			holder.praiseBar = (TextView) convertView.findViewById(R.id.praise_bar);
			holder.commentContent = (TextView) convertView.findViewById(R.id.comment_content);
			
			convertView.setTag(holder);
		} else {
			holder = (MyViewHolder) convertView.getTag();
		}

		holder.commentView.setVisibility(View.INVISIBLE);
		
		FriendInfo friend = datas.get(position);
		friend.setVisibleFlag(false);
		
		if (friend.isbPraiseFlag()) {
			holder.praiseBtn.setBackground(context.getDrawable(R.drawable.red_heart));
			addPraiseImage(holder.praiseBar);
			holder.praiseBar.append("开发者");
			holder.praiseBar.setVisibility(View.VISIBLE);
		} else {
			holder.praiseBtn.setBackground(context.getDrawable(R.drawable.icon_like));
			holder.praiseBar.setText("");
			holder.praiseBar.setVisibility(View.GONE);
		}
		
		if (friend.getCommentContent() != null) {
			holder.commentContent.setText("");
			setCommentContent(holder.commentContent, friend.getCommentContent());
			holder.commentContent.setVisibility(View.VISIBLE);
		} else {
			holder.commentContent.setText("");
			holder.commentContent.setVisibility(View.INVISIBLE);
		}
		
		holder.name.setText(friend.getName());

		if (datas.get(position).getContent() != null) {
			content = emotionUtils.getExpressionString(context, datas.get(position).getContent());
			holder.content.setText(content);
		}
		// 设置插入时间
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss  yyyy/MM/dd", Locale.getDefault());		
		String currentTime="\n" + sdf.format(new Date());
		holder.time.setText(currentTime);
		
		// 设置头像图片
		if (friend.getIcon() != null) {
			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_launcher)
					.showImageOnFail(R.drawable.ic_launcher)
					.cacheInMemory(true)
					.cacheOnDisc(true)
					//.bitmapConfig(Config.RGB_565)
					.build();
			
			ImageLoader.getInstance().displayImage(friend.getIcon(), holder.icon, options);
		}
		
		// 设置内容图片
		images = friend.getImages();
		if (images == null || images.size() == 0 ) {
			holder.gridView.setVisibility(View.GONE);
		} else {
			holder.gridView.setVisibility(View.VISIBLE);
			holder.gridView.setAdapter(new NoScrollItemAdapter(images, context)); // 设置九宫格 gridView 的适配器
			
			holder.gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
					Message msg = new Message();
					msg.what = 9;
					handler.sendMessage(msg);
					imageBrowse(position, images);
				}});
		}
		
		holder.praiseBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(50);

					String praise = holder.praiseText.getText().toString();
					if (praise.equals("赞")) {
						holder.praiseText.setText("取消");						
						holder.praiseBtn.setBackground(context.getDrawable(R.drawable.red_heart));
						
						holder.praiseBar.setText("开发者");
					} else {
						holder.praiseText.setText("赞");
						holder.praiseBtn.setBackground(context.getDrawable(R.drawable.icon_like));
						holder.praiseBar.setText("。。。");
					}

					holder.commentView.clearAnimation();
					holder.commentView.setVisibility(View.GONE);
					holder.praiseBar.setVisibility(View.VISIBLE);
					
			}
		});
		holder.commentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(50);
				
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
				
				holder.commentView.clearAnimation();
				holder.commentView.setVisibility(View.GONE);
			
			}
		});
		holder.comment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// 使用popupwindow实现点击按钮出现点赞和评论按钮布局
				
				Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(50);
				
				Message msg = new Message();
				msg.what = 4;
				msg.arg1 = position;
				msg.obj = holder;
				handler.sendMessage(msg);
				
			}
		});
		return convertView;
	}

	private void setCommentContent(TextView commentContent, ArrayList<String> commentList) {
		String temp;
		int end = 0;
		int index;
		for (int i = 0; i < commentList.size(); ++i) {
			index = commentList.get(i).indexOf(':');
			temp = commentList.get(i).substring(0, index);
			
			end = temp.length() ;
			SpannableStringBuilder style = new SpannableStringBuilder(commentList.get(i));
			style.setSpan(new ForegroundColorSpan(ImageUtils.getResourcesColor(context, R.color.id_color)), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			commentContent.append(style);
			if (i != commentList.size() - 1) {
				commentContent.append("\n"); 
			} else {
			}
		}
	}
	private void addPraiseImage(TextView content) {
		
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.like);
		bitmap = Bitmap.createScaledBitmap(bitmap, EmotionUtils.dip2px(context, 20), EmotionUtils.dip2px(context, 20),
				true);
		Drawable drawable = new BitmapDrawable(bitmap);
		drawable.setBounds(EmotionUtils.dip2px(context, 1), EmotionUtils.dip2px(context, 1), 
				EmotionUtils.dip2px(context, 20), EmotionUtils.dip2px(context, 20));
		ImageSpan imageSpan = new ImageSpan(drawable);
		SpannableString spannable = new SpannableString("like");
		spannable.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		content.setText(spannable);
		content.append(" ");
	}
	
	/**
	 * 跳转到ImagePagerActivity，显示图片详情
	 * @param position
	 * @param images
	 */
	private void imageBrowse(int position, List<ImageInfo> images) {
		Intent in = new Intent(context, ImagePagerActivity.class);
		
		ArrayList<String> urls = new ArrayList<String>();
		for (int i = 0; i < images.size(); ++i) {
			urls.add(images.get(i).getUrl());
		}
		
		in.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		in.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
		
		context.startActivity(in);
	}

	
}
