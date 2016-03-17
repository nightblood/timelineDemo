package com.zlf.testdemo01.domain;

import java.util.HashMap;
import java.util.List;

public class MsgInfo {
	private int friendId;
	private String friendName;
	private String iconImage;
	private List<String> contentList;

	public String getIconImage() {
		return iconImage;
	}

	public void setIconImage(String iconImage) {
		this.iconImage = iconImage;
	}

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public int getFriendId() {
		return friendId;
	}

	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}

	public List<String> getContentList() {
		return contentList;
	}

	public void setContentList(List<String> contentList) {
		this.contentList = contentList;
	}
	
	
	
}
