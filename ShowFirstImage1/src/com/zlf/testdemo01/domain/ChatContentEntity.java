package com.zlf.testdemo01.domain;

import java.util.ArrayList;
import java.util.List;

public class ChatContentEntity {

	public String friendName;
	public List<ChatEntity> content;
	public ChatEntity latestMsg;
	
	public ChatContentEntity(String friendName, List<ChatEntity> content) {
		this.friendName = friendName;
		this.content = content;
	}
	public ChatContentEntity() {
		
	}
	
}
