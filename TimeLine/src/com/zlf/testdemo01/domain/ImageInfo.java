package com.zlf.testdemo01.domain;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageInfo {
	private String url;
	private int width;
	private int height;
	private Bitmap bitmap;
	private ImageView imageview;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public ImageView getImageview() {
		return imageview;
	}
	public void setImageview(ImageView imageview) {
		this.imageview = imageview;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}
