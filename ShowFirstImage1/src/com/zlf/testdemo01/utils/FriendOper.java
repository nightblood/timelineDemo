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
	private static MyItemAdapter adapter;
	private Context context;
	private static Handler handler;
	private HttpClient client;
	
	public FriendOper(Context context, Handler handler, MyItemAdapter adapter) {
		this.context = context;
		this.handler = handler;
		this.adapter = adapter;
	}

	/**
	 * 从服务器得到朋友圈内容
	 */
	public void getTimeLineData(final int flagPullOrRefersh) {
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(-1);
		String url = TIMELINE_URL + "&page=" + MainActivity.pageNum;
		http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				// testText.setText(current + "/" + total);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Message msg;
			
				// 解析得到的 json 数据
				if (flagPullOrRefersh == MainActivity.ON_LOAD) {
					// 上拉加载
					MainActivity.friendList.addAll(parseTimeLineJsonData(responseInfo.result));
					
					msg = new Message();
					msg.what = 5;
					handler.sendMessage(msg);
//					mSwipeLayout.setLoading(false);
					
				} else if (flagPullOrRefersh == MainActivity.ON_REFERSH) {
					// 下拉刷新
					MainActivity.friendList.clear();
					MainActivity.friendList.addAll(parseTimeLineJsonData(responseInfo.result));
					/* 2013/3/8 : 使用开源项目的PullToRefershListView 代替RefreshView */
//					mSwipeLayout.setRefreshing(false);
//					mSwipeLayout.onRefreshComplete();
					msg = new Message();
					msg.what = 6;
					handler.sendMessage(msg);
				} else {
					System.out.println("Error input variable!!!");
				}
				
				MainActivity.pageNum++;
				if (MainActivity.friendList != null) {
					MainActivity.flagHasData = true;
				}
//				adapter.notifyDataSetChanged();
				msg = new Message();
				msg.what = 7;
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

	/* 异步方式读取服务端数据 */
	public void readNet(String url) {
		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				
				String urlStr = arg0[0];
				
				HttpGet get = new HttpGet(urlStr);
				try {
					HttpResponse response = client.execute(get);
					String val = EntityUtils.toString(response.getEntity());

					if (urlStr.equals(TIMELINE_URL)) {
						// friendList = getFriendsList(val);
						MainActivity.friendList.clear();
						MainActivity.friendList.addAll(getFriendsList(val));

						Message msg = new Message();
						msg.what = 7;				
						handler.sendMessage(msg);
						adapter.notifyDataSetChanged();

					} else if (urlStr.equals(EMOTION_URL)) {
						// appendFile(localPath + "emotion.txt", val);
						FileUtils.appendFile(MainActivity.emotionDataPath, val);
						// appendFile("/sdcard/emotion.txt",
						// "----------------");
						PostActivity.emotionList = getEmotionList();
					}
					return val;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute(url);
	}

	/**
	 * 从json的字符串的解析得到所有表情的信息
	 * 
	 * @param jsonStr
	 * @return
	 */
	public List<EmotionInfo> getEmotionList() {
		String path = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/emotion.txt";
		String jsonStr = FileUtils.readFileByChars(path);
		
		JSONObject jsonObj;
		if (null == jsonStr)
			return null;

		List<EmotionInfo> emotionList = new ArrayList<EmotionInfo>();
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
				delete.emotionPath = context.getApplicationContext().getFilesDir().getAbsolutePath() +  "/emoticon/" + "delete.png";
				if (i == pageCount) {
					emotionList.add(delete);
				} else {
					emotionList.add(20 * i + i-1, delete);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
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
	private void getEmotionData() {
		HttpUtils http = new HttpUtils();
		String emotionUrl = "http://client.e0575.com/quanzi/app.php?c=quanzi&a=emoticon";
		http.send(HttpRequest.HttpMethod.GET, emotionUrl, new RequestCallBack<String>() {
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				// testTextView.setText(current + "/" + total);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// textView.setText(responseInfo.result);
				MainActivity.eMotionData = responseInfo.result;
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
	 * 下载表情包
	 */
	public void downloadEmotionImages() {
		HttpUtils http = new HttpUtils();
		HttpHandler handler = http.download("http://client.e0575.com/quanzi/doc/emoticon.zip", MainActivity.emotionPath, true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
				true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
				new RequestCallBack<File>() {

					@Override
					public void onStart() {
						Toast.makeText(context, "Download start!!", Toast.LENGTH_LONG).show();
						// textView.setText("conn...");
					}

					@Override
					public void onLoading(long total, long current, boolean isUploading) {
						// textView.setText(current + "/" + total);
					}

					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						Toast.makeText(context, "Download success!!", Toast.LENGTH_LONG).show();
						// textView.setText("downloaded:" +
						// responseInfo.result.getPath());
						// 解压表情压缩文件
						String localPath = context.getApplicationContext().getFilesDir().getAbsolutePath();

						try {
							upZipFile(localPath + "/emotion.zip", localPath + "/");
						} catch (Exception e) {
							e.printStackTrace();
						}
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

					@Override
					public void onFailure(HttpException error, String msg) {
						Toast.makeText(context, "Download fail!!", Toast.LENGTH_LONG).show();
						// textView.setText(msg);
					}
				});

		// 调用cancel()方法停止下载
		// handler.cancel();

	}

}
