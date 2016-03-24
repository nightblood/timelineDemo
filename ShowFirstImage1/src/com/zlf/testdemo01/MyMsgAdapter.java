package com.zlf.testdemo01;

import java.util.List;

import com.zlf.testdemo01.domain.ChatContentEntity;
import com.zlf.testdemo01.utils.EmojiParser;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyMsgAdapter extends BaseAdapter {
	private Context context;
	private List<ChatContentEntity> datas;
	private EmojiParser emojiParser;
	
	public MyMsgAdapter(Context c, List<ChatContentEntity> msgList) {
		this.context = c;
		this.datas = msgList;
		
		EmojiParser.init(context);
		emojiParser = EmojiParser.getInstance(context);
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.msg_list_item, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon_image);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.content = (TextView) convertView.findViewById(R.id.msg_content);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.name.setText(datas.get(position).friendName);

		System.out.println(datas.get(position).latestMsg);
		if (datas.get(position).latestMsg == null) {
			holder.content.setText("");
		} else {
			holder.content.setText(datas.get(position).latestMsg.msgBelongName + ": ");
			holder.content.append(emojiParser.addSmileySpans(datas.get(position).latestMsg.chatData));
			
		}
		
		return convertView;
	}
	private class ViewHolder {
		public ImageView icon;
		public TextView content;
		public TextView name;
		
	}

}
