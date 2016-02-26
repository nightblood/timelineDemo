package com.zlf.testdemo01.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class EmotionInfo implements Parcelable {
	
	private String image_name;
	private String text;
	
	public static String emotionPath;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getImageName() {
		return image_name;
	}
	public void setImageName(String image_name) {
		this.image_name = image_name;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
