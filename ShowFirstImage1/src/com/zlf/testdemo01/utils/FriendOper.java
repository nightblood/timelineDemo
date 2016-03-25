package com.zlf.testdemo01.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zlf.testdemo01.ImageUtils;
import com.zlf.testdemo01.MainActivity;
import com.zlf.testdemo01.MyItemAdapter;
import com.zlf.testdemo01.PostActivity;
import com.zlf.testdemo01.R;
import com.zlf.testdemo01.domain.EmojiKeyboard;
import com.zlf.testdemo01.domain.EmotionInfo;
import com.zlf.testdemo01.domain.FriendInfo;
import com.zlf.testdemo01.domain.ImageInfo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class FriendOper {
	
	public final static String TIMELINE_URL = "http://client.e0575.com/quanzi/app.php?c=quanzi&a=mainlist";
	public final static String EMOTION_URL = "http://client.e0575.com/quanzi/app.php?c=quanzi&a=emoticon";
	
	public final static int HANDLER_ON_FRESH = 0XF6;
	public final static int HANDLER_ON_LOAD = 0XF7;
	public final static int HANDLER_ADAPTER_DATA_CHANGED_NOTIFY = 0XF8;
	public final static int HANGDLE_INIT_VIEW = 0XF0;
	
	private static Context context;
	private static Handler handler;
	private static List<EmotionInfo> emotionList;
	
	private static FriendOper friendOper;
	
	public static synchronized FriendOper getInstance(Context context, Handler handler) {
		if (friendOper == null) {
			friendOper = new FriendOper(context, handler);
		}
		FriendOper.context = context;
		return friendOper;
	}
	
	private FriendOper(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	/**
	 * 从服务器得到朋友圈内容
	 */
	public void getTimeLineData(final int flagPullOrRefersh, final int pageNum) {
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(-1);
		String url = TIMELINE_URL + "&page=" + pageNum;
		http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Message msg;
				
				if (handler == null) {
					System.out.println("Error : handler is null!!" );
					return;
				}
			
				// 解析得到的 json 数据
				if (flagPullOrRefersh == MainActivity.ON_LOAD) {
					// 上拉加载
					MainActivity.friendList.addAll(parseTimeLineJsonData(responseInfo.result));
					
					msg = new Message();
					msg.what = HANDLER_ON_LOAD;
					handler.sendMessage(msg);
					
				} else if (flagPullOrRefersh == MainActivity.ON_REFERSH) {
					// 下拉刷新
					MainActivity.friendList.clear();
					MainActivity.friendList.addAll(parseTimeLineJsonData(responseInfo.result));
					
					msg = new Message();
					msg.what = HANDLER_ON_FRESH;
					handler.sendMessage(msg);
				} else {
					System.out.println("Error input variable!!!");
				}
				
				if (MainActivity.friendList != null) {
					MainActivity.flagHasData = true;
				}
				msg = new Message();
				msg.what = HANDLER_ADAPTER_DATA_CHANGED_NOTIFY;
				handler.sendMessage(msg);
			}

			@Override
			public void onStart() {
			}
			@Override
			public void onFailure(HttpException error, String msg) {
			}
		});
	}

	/**
	 * 从json的字符串的解析得到所有表情的信息
	 * 
	 * @param jsonStr
	 * @return
	 */
	public List<EmotionInfo> getEmotionList() {
		if (emotionList != null) 
			return emotionList;
//		String path = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/emotion.txt";
		String jsonStr = FileUtils.readFileByChars(FileUtils.PATH_EMOJI_TXT);
		
		JSONObject jsonObj;
		if (null == jsonStr) {
			System.out.println("emotion.txt没有数据！！");
			return null;
		}

		System.out.println(jsonStr);
		emotionList = new ArrayList<EmotionInfo>();
		try {// 将json字符串转换为json对象
			jsonObj = new JSONObject(jsonStr);
			// 得到指定json key对象的value对象
			String action = jsonObj.getString("action");

			if (!action.equals("success")) {
				System.out.println("Get Failure!!");
				return null;
			}
			JSONArray eList = jsonObj.getJSONArray("value");
			for (int i = 0; i < eList.length(); ++i) {
				JSONObject jsonItem = eList.getJSONObject(i);
				EmotionInfo info = new EmotionInfo();
				info.setImageName(jsonItem.getString("image_name"));
				info.setText("[" + jsonItem.getString("text") + "]");
				emotionList.add(info);
			}
			// 保存表情，用于替换文字
			EmojiParser.initEmojiList(emotionList);
			
			// 在表情包里添加 删除表情 的图片
			EmotionInfo delete;
			int temp = emotionList.size() / 21; // coloum : 7, row : 3
			int pageCount = emotionList.size() % (21 * temp) == 0 ? temp : temp + 1;
			for (int i= 1; i < pageCount + 1; ++i) {
				delete = new EmotionInfo();
				delete.setImageName("delete.png");
				delete.setText("删除");
				
//				delete.emotionPath = FileUtils.DIR_EMOJI_IMAGES + "delete.png";
				if (i == pageCount) {
					emotionList.add(delete);
				} else {
					emotionList.add(20 * i + i-1, delete);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		EmojiKeyboard.emotionList = emotionList;
		return emotionList;
	}

	/**
	 * 从json的字符串的解析得到所有朋友发布的信息
	 * 
	 * @param jsonStr
	 * @return
	 */
	public List<FriendInfo> getFriendsList(String jsonStr) {
		List<FriendInfo> fList;
		List<ImageInfo> iList;
		JSONObject jsonObj;
		// System.out.println("getFriends start!");
		if (null == jsonStr)
			return null;

		fList = new ArrayList<FriendInfo>();
		try {// 将json字符串转换为json对象
			jsonObj = new JSONObject(jsonStr);
			// 得到指定json key对象的value对象
			String action = jsonObj.getString("action");

			if (!action.equals("list_info_success")) {
				// System.out.println("Get Failure!!");
				return null;
			}
			JSONArray friendsList = jsonObj.getJSONArray("value");

			// 遍历jsonArray
			// System.out.println("friend count: " + friendsList.length());
			for (int i = 0; i < friendsList.length(); ++i) {
				// 获取每一个朋友的json对象
				JSONObject jsonItem = friendsList.getJSONObject(i);
				FriendInfo friend = new FriendInfo();
				friend.setId(jsonItem.getInt("id"));
				// System.out.println(friend.getId());
				friend.setName(jsonItem.getString("user_name"));
				friend.setIcon(jsonItem.getString("user_icon"));
				friend.setGender(jsonItem.getString("user_gender"));
				friend.setContent(jsonItem.getString("content"));
				friend.setInsertTime("insert_time");

				// 获取每一张图片对象
				JSONArray imagesList = jsonItem.getJSONArray("images");
				friend.setLayoutType(imagesList.length());

				if (0 != imagesList.length()) {
					iList = new ArrayList<ImageInfo>();
					for (int j = 0; j < imagesList.length(); ++j) {
						ImageInfo image = new ImageInfo();
						JSONObject imageItem = imagesList.getJSONObject(j);
						image.setUrl(imageItem.getString("url"));
						image.setHeight(imageItem.getInt("height"));
						image.setWidth(imageItem.getInt("width"));
						// System.out.println(image.getUrl());
						iList.add(image);
					}
					friend.setImages(iList);
				}
				// TODO: 添加评论，以及本人是否已经点赞
				friend.setComments(null);
				friend.setPraiseFlag(false);
				friend.setVisibleFlag(false);

				fList.add(friend);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// System.out.println("----------------------" +
		// fList.get(0).getImages().size());
		return fList;
	}

	/**
	 * 解析从服务得到的朋友圈信息
	 * 
	 * @param jsonData
	 * @return
	 */
	private List<FriendInfo> parseTimeLineJsonData(String jsonData) {

		List<FriendInfo> fList;
		List<ImageInfo> iList;
		JSONObject jsonObj;
		// System.out.println("-----------------------getFriends
		// start!---------------------");
		if (null == jsonData)
			return null;

		fList = new ArrayList<FriendInfo>();
		try {// 将json字符串转换为json对象
			jsonObj = new JSONObject(jsonData);
			// 得到指定json key对象的value对象
			String action = jsonObj.getString("action");

			if (!action.equals("list_info_success")) {
				System.out.println("Get Failure!!");
				return null;
			}
			JSONArray friendsList = jsonObj.getJSONArray("value");
			// 遍历jsonArray
			// System.out.println("friend count: " +
			// friendsList.length());
			for (int i = 0; i < friendsList.length(); ++i) {
				// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>" + i + "th friend
				// ");
				// 获取每一个朋友的json对象
				JSONObject jsonItem = friendsList.getJSONObject(i);
				FriendInfo friend = new FriendInfo();
				friend.setId(jsonItem.getInt("id"));
				// System.out.println(friend.getId());
				friend.setName(jsonItem.getString("user_name"));
				friend.setIcon(jsonItem.getString("user_icon"));
				friend.setGender(jsonItem.getString("user_gender"));
				friend.setContent(jsonItem.getString("content"));
				friend.setInsertTime("insert_time");

				// 获取每一张图片对象
				JSONArray imagesList = jsonItem.getJSONArray("images");
				friend.setLayoutType(imagesList.length());
				if (imagesList.length() > 9) {
					System.out.println("Parse images count error!");
					return null;
				}

				if (0 != imagesList.length()) {
					iList = new ArrayList<ImageInfo>();
					// System.out.println(" image count: " +
					// imagesList.length());
					for (int j = 0; j < imagesList.length(); ++j) {
						ImageInfo image = new ImageInfo();
						JSONObject imageItem = imagesList.getJSONObject(j);
						image.setUrl(imageItem.getString("url"));
						image.setHeight(imageItem.getInt("height"));
						image.setWidth(imageItem.getInt("width"));
						// System.out.println(image.getUrl());
						iList.add(image);
					}
					friend.setImages(iList);
				}
				fList.add(friend);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return fList;
	}

	/**
	 * 从服务器得到表情数据
	 */
	public void getEmojiZipData() {
		HttpUtils http = new HttpUtils();
		String emojiZipUrl = "http://client.e0575.com/quanzi/doc/emoticon.zip";
		http.download(emojiZipUrl, FileUtils.PATH_EMOJI_ZIP, new RequestCallBack<File>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(context, "Download emoji.zip fail!!", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				Toast.makeText(context, "Download emoji pictures success!!", Toast.LENGTH_LONG).show();
				System.out.println("Download emojis success!!");
				// 解压表情压缩文件
				try {
					upZipFile(FileUtils.PATH_EMOJI_ZIP, FileUtils.DIR_APPLICATION + "/");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	/**
	 * 从服务器得到表情包内容
	 */
	public void getEmojiTxtData() {
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(-1);
		String url = "http://client.e0575.com/quanzi/app.php?c=quanzi&a=emoticon";
		http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Toast.makeText(context, "Download emoji text successs!!", Toast.LENGTH_LONG).show();
				System.out.println("表情数据下载成功！！！！！！！");
				MainActivity.eMotionData = responseInfo.result;
				
				FileUtils.appendFile(FileUtils.PATH_EMOJI_TXT, responseInfo.result);
				getEmotionList();
				Message msg = new Message();
				msg.what = HANGDLE_INIT_VIEW;
				handler.sendMessage(msg);
				
			}

			@Override
			public void onStart() {
			}

			@Override
			public void onFailure(HttpException error, String msg) {
			}
		});
	}

	public int upZipFile(String zipFilePath, String folderPath) throws ZipException, IOException {
		// public static void upZipFile() throws Exception{
		File zipFile = new File(zipFilePath);
		if (!zipFile.exists()) {
			return 1;
		}
		ZipFile zfile = new ZipFile(zipFile);
		Enumeration zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		while (zList.hasMoreElements()) {
			ze = (ZipEntry) zList.nextElement();
			if (ze.isDirectory()) {
				Log.d("upZipFile", "ze.getName() = " + ze.getName());
				String dirstr = folderPath + ze.getName();
				// dirstr.trim();
				dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
				Log.d("upZipFile", "str = " + dirstr);
				File f = new File(dirstr);
				f.mkdir();
				continue;
			}
			Log.d("upZipFile", "ze.getName() = " + ze.getName());

			OutputStream os = new BufferedOutputStream(
					new FileOutputStream(getRealPathFileName(folderPath, ze.getName())));
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen = 0;
			while ((readLen = is.read(buf, 0, 1024)) != -1) {
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
		}
		zfile.close();
		Log.d("upZipFile", "finishssssssssssssssssssss");
		return 0;
	}

	/**
	 * 给定根目录，返回一个相对路径所对应的实际文件名.
	 * 
	 * @param baseDir
	 *            指定根目录
	 * @param absFileName
	 *            相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文件
	 */

	public File getRealPathFileName(String baseDir, String absFileName) {
		String[] dirs = absFileName.split("/");
		String lastDir = baseDir;
		if (dirs.length > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				lastDir += (dirs[i] + "/");
				File dir = new File(lastDir);
				if (!dir.exists()) {
					dir.mkdirs();
					Log.d("getRealFileName", "create dir = " + (lastDir + "/" + dirs[i]));
				}
			}
			File ret = new File(lastDir, dirs[dirs.length - 1]);
			Log.d("upZipFile", "2ret = " + ret);
			return ret;
		} else {
			return new File(baseDir, absFileName);
		}
	}

}
