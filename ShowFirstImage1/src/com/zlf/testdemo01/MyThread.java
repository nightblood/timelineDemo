package com.zlf.testdemo01;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import com.lidroid.xutils.BitmapUtils;
import com.zlf.testdemo01.domain.FriendInfo;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MyThread implements Runnable {
	private int ThreadId;
	// private Bitmap bitmap;
	private String url;
	private Handler handler;
	private int friendIndex;
	private List<FriendInfo> friendsList;
	private int imageIndex;
	private LinearLayout imagegroup;
	private ImageView image;
	private Context context;
	BitmapUtils bitmapUtils;

	public MyThread(Context context, String url, Handler handler, int friendIndex, int imageIndex,
			List<FriendInfo> friendsList, LayoutInflater inflater, ImageView image) {

		this.url = url;
		this.handler = handler;
		this.friendIndex = friendIndex;
		this.friendsList = friendsList;
		this.imageIndex = imageIndex;
		// this.imagegroup = imagegroup;
		this.image = image;
		this.context = context;
		bitmapUtils = new BitmapUtils(context);
	}

	@Override
	public void run() {
		if (!friendsList.get(friendIndex).isImagesCached()) {
//			BitmapUtils bitmapUtils = new BitmapUtils(context);
			bitmapUtils.display(image, url);
			// 将图片保存到本地
		} else {
			
		}
		
//		if (!friendsList.get(friendIndex).isImagesCached()) {
//			// 内存中无缓存图片，从服务器读取
//
////			BitmapUtils bitmapUtils = new BitmapUtils(context);
////			bitmapUtils.display(image, url);
//			try {
//				InputStream is = new URL(url).openStream();
//
//				if (0xffffffff == imageIndex) {
//					
//					friendsList.get(friendIndex).setIconBitmap(BitmapFactory.decodeStream(is));
//				} else {
//					// friendsList.get(friendIndex).getImages().get(imageIndex).setBitmap(BitmapFactory.decodeStream(is));
//					friendsList.get(friendIndex).getImages().get(imageIndex)
//							.setBitmap(ImageUtils.comp(BitmapFactory.decodeStream(is)));
//				}
//				is.close();
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			friendsList.get(friendIndex).setImagesCached(true);
//		}
//		
//		if (0xffffffff == imageIndex) {
//			image.setImageBitmap(friendsList.get(friendIndex).getIconBitmap());
//		} else {
//			image.setImageBitmap(friendsList.get(friendIndex).getImages().get(imageIndex).getBitmap());
//		}

		
		// System.out.println("------------thread end!!!");
	}

	public List<FriendInfo> getFriendsList() {
		return friendsList;
	}

	public void setFriendsList(List<FriendInfo> friendsList) {
		this.friendsList = friendsList;
	}

	public int getThreadId() {
		return ThreadId;
	}

	public void setThreadId(int threadId) {
		ThreadId = threadId;
	}

}
