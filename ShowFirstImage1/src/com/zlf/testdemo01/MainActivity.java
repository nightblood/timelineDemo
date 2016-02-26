package com.zlf.testdemo01;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zlf.testdemo01.RefreshLayout.OnLoadListener;
import com.zlf.testdemo01.domain.EmotionInfo;
import com.zlf.testdemo01.domain.FriendInfo;
import com.zlf.testdemo01.domain.ImageInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener ,SwipeRefreshLayout.OnRefreshListener,OnLoadListener{

	private static String TIMELINE_URL = "http://client.e0575.com/quanzi/app.php?c=quanzi&a=mainlist";
	private static String EMOTION_URL = "http://client.e0575.com/quanzi/app.php?c=quanzi&a=emoticon";
	private HttpClient client;
	private List<FriendInfo> friendList=new ArrayList<FriendInfo>();
	// private List<EmotionInfo> emotionList;
	private ListView listview;
//	private MyAdapter adapter;
	private MyItemAdapter adapter;
	private Button button;
	private Button download;
	public static String localPath;
	public static String emotionPath;
	public static String emotionDataPath;
	private TextView testText;
	private boolean flagHasData = false;
	private static String eMotionData;
	
//	private SwipeRefreshLayout mSwipeLayout;
	private RefreshLayout mSwipeLayout;
	private static final int REFRESH_COMPLETE = 0X110;
	private static int ON_LOAD = 0;
	private static int ON_REFERSH = 1;
	private static int pageNum = 1;
	
	private Handler refreshHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case REFRESH_COMPLETE:
				// ���»�ȡ����������Ȧ��Ϣ
				pageNum = 1;
				getTimeLineData(ON_REFERSH);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mSwipeLayout = (RefreshLayout) findViewById(R.id.id_swipe_ly);

		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setOnLoadListener(this);
		
		listview = (ListView) findViewById(R.id.lv);
		localPath = getApplicationContext().getFilesDir().getAbsolutePath();
		emotionPath = localPath + "/emotion.zip";
		emotionDataPath = localPath + "/emotion.txt";
		button = (Button) findViewById(R.id.button1);
		download = (Button) findViewById(R.id.download_btn);
		button.setOnClickListener(this);
		download.setOnClickListener(this);
		
//		adapter = new MyAdapter(friendList, this);
		adapter = new MyItemAdapter(friendList, this);
		listview.setAdapter((ListAdapter) adapter);

		EmotionInfo.emotionPath = localPath + "/emoticon/";
		File emotionDataFile = new File(emotionDataPath);
		if (emotionDataFile.exists()) {
			// �������������
			String emotionData = readFileByChars(localPath + "/emotion.txt");
			getEmotionList(emotionData);
		}

		/*************** ��ȡ��������� *************************/
//		client = new DefaultHttpClient();
		// HttpClient Get���� �õ���������
//		readNet(TIMELINE_URL);
		getTimeLineData(ON_REFERSH);	

		System.out.println("onCreate end!");
	}

	public void onRefresh()
	{
//		refreshHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
		pageNum = 1;
		getTimeLineData(ON_REFERSH);
	}
	/**
	 * ��ȡ�ļ����ݲ�����
	 * @param fileName
	 * @return
	 */
	public static String readFileByChars(String fileName) {
		File file = new File(fileName);
		String data = "";

		if (!file.exists()) {
			System.out.println("error file name!!");
			return null;
		}
		Reader reader = null;
		try {
//			System.out.println("���ַ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���ֽڣ�");
			// һ�ζ�һ���ַ�
			reader = new InputStreamReader(new FileInputStream(file));
			int tempchar;
			while ((tempchar = reader.read()) != -1) {
				// ����windows�£�\r\n�������ַ���һ��ʱ����ʾһ�����С�
				// ������������ַ��ֿ���ʾʱ���ỻ�����С�
				// ��ˣ����ε�\r����������\n�����򣬽������ܶ���С�
				if (((char) tempchar) != '\r') {
//					System.out.print((char) tempchar);
					data += (char)tempchar ;
					
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
//		System.out.print("read emotion data file: " + data);
		return data;
	}

	
	/**
	 * ��content����д���ļ�
	 * @param fileName
	 * @param content
	 */
	public static void appendFile(String fileName, String content) {
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * �ӷ������õ�����Ȧ����
	 */
	private void getTimeLineData(final int flagPullOrRefersh) {
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(-1);
		String url = TIMELINE_URL + "&page=" + pageNum;
		http.send(HttpRequest.HttpMethod.GET, url,
				new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current, boolean isUploading) {
//						testText.setText(current + "/" + total);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
//						testText.setText(responseInfo.result);
//						System.out.println(responseInfo.result);
//						System.out.println("111111111111get time line success1111111111111111");
						// �����õ��� json ����
						if (flagPullOrRefersh == ON_LOAD) {
							// ��������
							friendList.addAll(parseTimeLineJsonData(responseInfo.result));
							mSwipeLayout.setLoading(false);
							
						} else if (flagPullOrRefersh == ON_REFERSH){
							// ����ˢ��
							friendList.clear();
							friendList.addAll(parseTimeLineJsonData(responseInfo.result));
							mSwipeLayout.setRefreshing(false);
							
						}
						else {
							System.out.println("Error input variable!!!");
						}
						System.out.println("+++++++++++++++++++++++" + pageNum);
						
						System.out.println(responseInfo.result);
						pageNum++;
						if (friendList != null) {
							flagHasData = true;
						}
						adapter.notifyDataSetChanged();
						
					}

					@Override
					public void onStart() {
//						System.out.println("000000000000000000000000000000");
//						testText.setText("start");
					}

					@Override
					public void onFailure(HttpException error, String msg) {
//						System.out.println("2322222222222222222222222222222222");
//						testText.setText("Fail");
					}
				});
	}

	/* �첽��ʽ��ȡ��������� */
	private void readNet(String url) {
		new AsyncTask<String, Void, String>() {
			
			@Override
			protected String doInBackground(String... arg0) {
				System.out.println("doInBackground start!");
				String urlStr = arg0[0];
				HttpGet get = new HttpGet(urlStr);
				try {
					HttpResponse response = client.execute(get);
					String val = EntityUtils.toString(response.getEntity());

					// System.out.println(val);
					// System.out.println("Get server data successful!
					// doInBackground end!!" + val);
					if (urlStr.equals(TIMELINE_URL)) {
//						friendList = getFriendsList(val);
						friendList.clear();
						friendList.addAll(getFriendsList(val));
//						adapterFriendList = friendList;
						System.out.println("***************** get new data!");
						adapter.notifyDataSetChanged();
						
					} else if (urlStr.equals(EMOTION_URL)) {
						PostActivity.emotionList = getEmotionList(val);
						// appendFile(localPath + "emotion.txt", val);
						appendFile(emotionDataPath, val);
						// appendFile("/sdcard/emotion.txt",
						// "----------------");
					}
					return val;

				} catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println("doInBackground end!");
				return null;
			}
		}.execute(url);
	}

	/**
	 * ��json���ַ����Ľ����õ����б������Ϣ
	 * @param jsonStr
	 * @return
	 */
	protected List<EmotionInfo> getEmotionList(String jsonStr) {
		JSONObject jsonObj;
		if (null == jsonStr)
			return null;

		PostActivity.emotionList = new ArrayList<EmotionInfo>();
		try {// ��json�ַ���ת��Ϊjson����
			jsonObj = new JSONObject(jsonStr);
			// �õ�ָ��json key�����value����
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
				PostActivity.emotionList.add(info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return PostActivity.emotionList;
	}

	/**
	 * ��json���ַ����Ľ����õ��������ѷ�������Ϣ
	 * @param jsonStr
	 * @return
	 */
	public static List<FriendInfo> getFriendsList(String jsonStr) {
		List<FriendInfo> fList;
		List<ImageInfo> iList;
		JSONObject jsonObj;
		System.out.println("getFriends start!");
		if (null == jsonStr)
			return null;

		fList = new ArrayList<FriendInfo>();
		try {// ��json�ַ���ת��Ϊjson����
			jsonObj = new JSONObject(jsonStr);
			// �õ�ָ��json key�����value����
			String action = jsonObj.getString("action");

			if (!action.equals("list_info_success")) {
				System.out.println("Get Failure!!");
				return null;
			}
			JSONArray friendsList = jsonObj.getJSONArray("value");
			// ����jsonArray
			// System.out.println("friend count: " + friendsList.length());
			for (int i = 0; i < friendsList.length(); ++i) {
				// ��ȡÿһ�����ѵ�json����
				JSONObject jsonItem = friendsList.getJSONObject(i);
				FriendInfo friend = new FriendInfo();
				friend.setId(jsonItem.getInt("id"));
				// System.out.println(friend.getId());
				friend.setName(jsonItem.getString("user_name"));
				friend.setIcon(jsonItem.getString("user_icon"));
				friend.setGender(jsonItem.getString("user_gender"));
				friend.setContent(jsonItem.getString("content"));
				friend.setInsertTime("insert_time");

				// ��ȡÿһ��ͼƬ����
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
//						System.out.println(image.getUrl());
						iList.add(image);
					}
					friend.setImages(iList);
				}
				fList.add(friend);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
//		System.out.println("----------------------" + fList.get(0).getImages().size());
		return fList;
	}

	
	/**
	 * �����ӷ���õ�������Ȧ��Ϣ
	 * @param jsonData
	 * @return
	 */
	private List<FriendInfo> parseTimeLineJsonData(String jsonData) {

		List<FriendInfo> fList;
		List<ImageInfo> iList;
		JSONObject jsonObj;
		System.out.println("-----------------------getFriends start!---------------------");
		if (null == jsonData)
			return null;

		fList = new ArrayList<FriendInfo>();
		try {// ��json�ַ���ת��Ϊjson����
			jsonObj = new JSONObject(jsonData);
			// �õ�ָ��json key�����value����
			String action = jsonObj.getString("action");

			if (!action.equals("list_info_success")) {
				System.out.println("Get Failure!!");
				return null;
			}
			JSONArray friendsList = jsonObj.getJSONArray("value");
			// ����jsonArray
			// System.out.println("friend count: " +
			// friendsList.length());
			for (int i = 0; i < friendsList.length(); ++i) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>" + i + "th friend ");
				// ��ȡÿһ�����ѵ�json����
				JSONObject jsonItem = friendsList.getJSONObject(i);
				FriendInfo friend = new FriendInfo();
				friend.setId(jsonItem.getInt("id"));
				// System.out.println(friend.getId());
				friend.setName(jsonItem.getString("user_name"));
				friend.setIcon(jsonItem.getString("user_icon"));
				friend.setGender(jsonItem.getString("user_gender"));
				friend.setContent(jsonItem.getString("content"));
				friend.setInsertTime("insert_time");

				// ��ȡÿһ��ͼƬ����
				JSONArray imagesList = jsonItem.getJSONArray("images");
				friend.setLayoutType(imagesList.length());
				if (imagesList.length() > 9) {
					System.out.println("Parse images count error!");
					return null;
				}

				if (0 != imagesList.length()) {
					iList = new ArrayList<ImageInfo>();
					System.out.println(" image count: " + imagesList.length());
					for (int j = 0; j < imagesList.length(); ++j) {
						ImageInfo image = new ImageInfo();
						JSONObject imageItem = imagesList.getJSONObject(j);
						image.setUrl(imageItem.getString("url"));
						image.setHeight(imageItem.getInt("height"));
						image.setWidth(imageItem.getInt("width"));
//						System.out.println(image.getUrl());
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
	 * �ӷ������õ���������
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
				eMotionData = responseInfo.result;
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
	 * ���ر����
	 */
	private void downloadEmotionImages() {
		HttpUtils http = new HttpUtils();
		HttpHandler handler = http.download("http://client.e0575.com/quanzi/doc/emoticon.zip", emotionPath, true, // ���Ŀ���ļ����ڣ�����δ��ɵĲ��ּ������ء���������֧��RANGEʱ���������ء�
				true, // ��������󷵻���Ϣ�л�ȡ���ļ�����������ɺ��Զ���������
				new RequestCallBack<File>() {

					@Override
					public void onStart() {
						Toast.makeText(MainActivity.this, "Download start!!", Toast.LENGTH_LONG).show();
						// textView.setText("conn...");
					}

					@Override
					public void onLoading(long total, long current, boolean isUploading) {
						// textView.setText(current + "/" + total);
					}

					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						Toast.makeText(MainActivity.this, "Download success!!", Toast.LENGTH_LONG).show();
						// textView.setText("downloaded:" +
						// responseInfo.result.getPath());
						// ��ѹ����ѹ���ļ�
						String localPath = getApplicationContext().getFilesDir().getAbsolutePath();

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
					 * ������Ŀ¼������һ�����·������Ӧ��ʵ���ļ���.
					 * 
					 * @param baseDir
					 *            ָ����Ŀ¼
					 * @param absFileName
					 *            ���·������������ZipEntry�е�name
					 * @return java.io.File ʵ�ʵ��ļ�
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
						Toast.makeText(MainActivity.this, "Download fail!!", Toast.LENGTH_LONG).show();
						// textView.setText(msg);
					}
				});

		// ����cancel()����ֹͣ����
		// handler.cancel();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			Intent it = new Intent();
			it.setClass(MainActivity.this, PostActivity.class);

			// Bundle bundle = new Bundle();
			// bundle.putParcelableArrayList("emotionList", emotionList);
			// it.putExtras(bundle);

			// startActivity(it);
			startActivityForResult(it, 100);
			break;
		case R.id.download_btn:
			if (new File(emotionPath).exists()) {
				Toast.makeText(MainActivity.this, "�����������!!", Toast.LENGTH_LONG).show();
			} else {
				downloadEmotionImages();
				readNet(EMOTION_URL);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onLoad() {		
		// ��ȡ��һҳ����Ȧ����
		getTimeLineData(ON_LOAD);
		
	}
}
