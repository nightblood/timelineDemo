package com.zlf.testdemo01;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zlf.testdemo01.domain.FriendInfo;
import com.zlf.testdemo01.domain.ImageInfo;
import com.zlf.testdemo01.domain.MsgInfoGet;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static String URL = "http://client.e0575.com/quanzi/app.php?c=quanzi&a=mainlist";
	private HttpClient client;
	private List<FriendInfo> friendList;
	private ListView listview;
	private MyAdapter adapter;
	
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
//			System.out.println("Handler handling message!!");
			Bitmap bitmap = null;
			ImageView image = (ImageView) msg.obj;
			
			int friendIndex = msg.what >> 0x04;
			int imageIndex = msg.what & 0x0f;
			
			System.out.println(">>>>>handling ( " + friendIndex + " " + imageIndex + " )!!!");
			if (0x0f == imageIndex){
				bitmap = friendList.get(friendIndex).getIconBitmap();				
			} else {
				bitmap = friendList.get(friendIndex).getImages().get(imageIndex).getBitmap();
			}
			

			
			image.setImageBitmap(bitmap);			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/***************获取服务端数据*************************/
		client = new DefaultHttpClient();
		// HttpClient Get方法 得到网络数据
		readNet(URL);
		if (null == friendList)
			System.out.println("No friends data yet!!");
		try {
			Thread.sleep(1000);
			System.out.println("Sleep end!" + friendList);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		listview = (ListView) findViewById(R.id.lv);
		
		while (null == friendList){}
		adapter = new MyAdapter(friendList, this, handler);
		listview.setAdapter((ListAdapter) adapter);
		
		System.out.println("onCreate end!");
	}

	/* 异步方式读取服务端数据 */
	private void readNet(String url) {
		new AsyncTask<String, Void, String>(){
			@Override
			protected String doInBackground(String... arg0){
				System.out.println("doInBackground start!");
				String urlStr = arg0[0];
				HttpGet get = new HttpGet(urlStr);
				try{
					HttpResponse response = client.execute(get);
					String val = EntityUtils.toString(response.getEntity());
					
					//System.out.println(val);
					//System.out.println("Get server data successful! doInBackground end!!" + val);
					friendList = getFriendsList(val);
					return val;
					
				} catch (Exception e){
					e.printStackTrace();
				}
				
				System.out.println("doInBackground end!");
				return null;
			}
		}.execute(url);
	}
	
	public static List<FriendInfo> getFriendsList(String jsonStr)
	{
		List<FriendInfo> fList;
		List<ImageInfo> iList;
		JSONObject jsonObj;
		System.out.println("getFriends start!");
		if (null == jsonStr)
			return null;
		
		fList = new ArrayList<FriendInfo>();
		try
		{// 将json字符串转换为json对象
			jsonObj = new JSONObject(jsonStr);
			// 得到指定json key对象的value对象
			String action = jsonObj.getString("action");
			
			if (!action.equals("list_info_success")){
				System.out.println("Get Failure!!");
				return null;
			}
			JSONArray friendsList = jsonObj.getJSONArray("value");			
			// 遍历jsonArray
			//System.out.println("friend count: " + friendsList.length());
			for (int i = 0; i < friendsList.length(); ++i)
			{
				// 获取每一个朋友的json对象
				JSONObject jsonItem = friendsList.getJSONObject(i);
				FriendInfo friend = new FriendInfo();
				friend.setId(jsonItem.getInt("id"));
				//System.out.println(friend.getId());
				friend.setName(jsonItem.getString("user_name"));
				friend.setIcon(jsonItem.getString("user_icon"));
				friend.setGender(jsonItem.getString("user_gender"));
				friend.setContent(jsonItem.getString("content"));
				friend.setInsertTime("insert_time");
				
				//获取每一张图片对象
				JSONArray imagesList = jsonItem.getJSONArray("images");
				friend.setLayoutType(imagesList.length());
				
				if (0 != imagesList.length()){
					iList = new ArrayList<ImageInfo>();
					for (int j = 0; j < imagesList.length(); ++j){
						ImageInfo image = new ImageInfo();
						JSONObject imageItem = imagesList.getJSONObject(j);
						image.setUrl(imageItem.getString("url"));
						image.setHeight(imageItem.getInt("height"));
						image.setWidth(imageItem.getInt("width"));
						System.out.println(image.getUrl());
						iList.add(image);					
					}
					friend.setImages(iList);
				}
				fList.add(friend);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return fList;
	}
}
