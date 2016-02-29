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

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MyItemAdapter extends BaseAdapter{

	private List<FriendInfo> datas;
	private Context context;
	private ImageLoader imageLoader;
	Handler handler;
	
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		final ViewHolder holder;
		DisplayImageOptions options;
		final List<ImageInfo> images;
		EmotionUtils emotionUtils = new EmotionUtils(context);
		SpannableString content;
		
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_list, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.gridView = (NoScrollGridView) convertView.findViewById(R.id.gridview);
			holder.time = (TextView) convertView.findViewById(R.id.insert_time);
			holder.comment = (ImageView) convertView.findViewById(R.id.comment);
//
//			mCommentView = convertView.findViewById(R.id.comment_view);
//			
			
			convertView.setTag(holder);			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 设置焦点
//		holder.comment.setFocusable(true);   
//		holder.comment.setFocusableInTouchMode(true);   
//		holder.comment.requestFocus(); 
		
		holder.comment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(50);
				
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		});
		FriendInfo friend = datas.get(position);
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
			holder.gridView.setAdapter(new NoScrollItemAdapter(images, context)); // 设置九宫格gridView的适配器
			
			holder.gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
					imageBrowse(position, images);
				}});
		}
		return convertView;
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
	class ViewHolder {
		private ImageView icon;
		private TextView name;
		private TextView content;
		private NoScrollGridView gridView;
		private TextView time;
		private ImageView comment;
//		private EditText commentEdit;
	}
}
