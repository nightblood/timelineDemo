package com.zlf.testdemo01;

import java.util.List;

import com.zlf.testdemo01.domain.MsgInfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyMsgAdapter extends BaseAdapter {
	private Context context;
	private List<MsgInfo> datas;
	
	
	public MyMsgAdapter(Context c, List<MsgInfo> msgList) {
		this.context = c;
		this.datas = msgList;
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
		
		holder.name.setText(datas.get(position).getFriendName());
		String[] temp = datas.get(position).getContentList().get(datas.get(position).getContentList().size() - 1).split(":");
		if (temp[0].equals("left")) {
			holder.content.setText(datas.get(position).getFriendName() + ": " + temp[1]);
		} else {
			holder.content.setText("¿ª·¢Õß" + ": " + temp[1]);
		}
		
		
		return convertView;
	}
	private class ViewHolder {
		public ImageView icon;
		public TextView content;
		public TextView name;
		
	}

}
