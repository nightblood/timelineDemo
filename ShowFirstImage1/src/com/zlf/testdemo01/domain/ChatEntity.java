package com.zlf.testdemo01.domain;

public class ChatEntity {

	public String chatData;
	public String msgBelongName;
	
	public ChatEntity(String msgBelongName, String chatData) {
		this.msgBelongName = msgBelongName;
		this.chatData = chatData;
	}
	public ChatEntity() {
	}
}
