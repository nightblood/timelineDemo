package com.zlf.testdemo01;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.zlf.testdemo01.domain.FriendInfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint.Join;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MyThread implements Runnable{
	private int ThreadId;
	//private Bitmap bitmap;
	private String url;
	private Handler handler;
	private int friendIndex;
	private List<FriendInfo> friendsList;
	private int imageIndex;
	private LinearLayout imagegroup;
	private ImageView image;
	
	public MyThread(String url, Handler handler, int friendIndex, int imageIndex, List<FriendInfo> friendsList, 
									LayoutInflater inflater, ImageView image) {

		this.url = url;
		this.handler = handler;
		this.friendIndex = friendIndex;
		this.friendsList = friendsList;
		this.imageIndex = imageIndex;
//		this.imagegroup = imagegroup;
		this.image = image;
	}
    
	@Override
	public void run() {
		if (!friendsList.get(friendIndex).isImagesCached()){
			
//			System.out.println(">>>>>>>>>>>>>>>>>>" + ThreadId + " Downloading image!! <<<<<<<<<<<<<<<<<");
			System.out.println(">>Thread Handle friendId: " + friendIndex + " .imageId: " + imageIndex + ". url: " + url 
					+ ". UserID: " + friendsList.get(friendIndex).getId());
			
	        try {
	            InputStream is= new URL(url).openStream();	 
	            Message msg = new Message();
	            
	            if (0xffffffff == imageIndex){
	            	msg.what = friendIndex << 0x04 | 0x0f;
	            	friendsList.get(friendIndex).setIconBitmap(BitmapFactory.decodeStream(is));
	            } else {
	            	msg.what = friendIndex << 0x04 | imageIndex & 0x0f;
	            	friendsList.get(friendIndex).getImages().get(imageIndex).setBitmap(BitmapFactory.decodeStream(is));
	            }
	            
	            msg.obj = image;
	            
	            handler.sendMessage(msg);
//	            System.out.println(">>Download image successful!!");
	            is.close();
	            
	         } catch (Exception e) {
	            e.printStackTrace();	        }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
		} else {
			System.out.println("Thread. Use cached image!!!");
			Message msg = new Message();
			if (0xffffffff == imageIndex){
				msg.what = friendIndex << 0x04 | 0x0f;
			} else {
				msg.what = friendIndex << 0x04 | imageIndex & 0x0f;
			}
            msg.obj = image;            
            handler.sendMessage(msg);
		}
//		System.out.println("------------thread end!!!");
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
