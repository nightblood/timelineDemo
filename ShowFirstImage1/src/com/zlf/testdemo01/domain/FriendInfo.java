package com.zlf.testdemo01.domain;

import java.util.ArrayList;
import java.util.List;

import com.zlf.testdemo01.Comment;

import android.graphics.Bitmap;

public class FriendInfo {
	
	private int layoutType;
	private int id;
	private String user_name;
	private String user_icon;
	private String user_gender;
	private String content;
	private String insert_time;
	private List<ImageInfo> images;
	private boolean imagesCached = false;
	private Bitmap iconBitmap;
	
	private ArrayList<Comment> comments = new ArrayList<Comment>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return user_name;
	}
	public void setName(String user_name) {
		this.user_name = user_name;
	}
	public String getIcon() {
		return user_icon;
	}
	public void setIcon(String user_icon) {
		this.user_icon = user_icon;
	}
	public String getGender() {
		return user_gender;
	}
	public void setGender(String user_gender) {
		this.user_gender = user_gender;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getInsertTime() {
		return insert_time;
	}
	public void setInsertTime(String insert_time) {
		this.insert_time = insert_time;
	}
	public List<ImageInfo> getImages() {
		return images;
	}
	public void setImages(List<ImageInfo> images) {
		this.images = images;
	}
	
	public boolean isImagesCached() {
		return imagesCached;
	}
	public void setImagesCached(boolean imagesCached) {
		this.imagesCached = imagesCached;
	}
	public int getLayoutType() {
		return layoutType;
	}
	public void setLayoutType(int layoutType) {
		this.layoutType = layoutType;
	}
	public Bitmap getIconBitmap() {
		return iconBitmap;
	}
	public void setIconBitmap(Bitmap iconBitmap) {
		this.iconBitmap = iconBitmap;
	}
	public ArrayList<Comment> getComments() {
		return comments;
	}
	public void setComments(ArrayList<Comment> comments) {
		this.comments = comments;
	}
}
