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

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
			holder.comment = (Button) convertView.findViewById(R.id.comment);

			holder.commentView = convertView.findViewById(R.id.comment_view);
			holder.commentBtn = (Button) convertView.findViewById(R.id.comment_button);
			holder.praiseBtn = (Button) convertView.findViewById(R.id.praise_button);
			holder.praiseText = (TextView) convertView.findViewById(R.id.praise_text);
			
			convertView.setTag(holder);			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.commentView.setVisibility(View.INVISIBLE);
		
		FriendInfo friend = datas.get(position);
		friend.setVisibleFlag(false);
		
		if (friend.isbPraiseFlag()) {
			holder.praiseBtn.setBackground(context.getDrawable(R.drawable.red_heart));
		} else {
			holder.praiseBtn.setBackground(context.getDrawable(R.drawable.icon_like));
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
					} else {
						holder.praiseText.setText("赞");
						holder.praiseBtn.setBackground(context.getDrawable(R.drawable.icon_like));
					}

					holder.commentView.clearAnimation();
					holder.commentView.setVisibility(View.GONE);
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
				msg.obj = holder.comment;
				handler.sendMessage(msg);
				
			}
		});
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
		private Button comment;
		
		private View commentView;
		private Button commentBtn;
		private Button praiseBtn;
		private TextView praiseText;
//		private EditText commentEdit;
	}
}
