package com.zlf.testdemo01;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LocationActivity extends Activity{
	private LocationClient locationClient;
	private LocationClientOption option;
	private TextView locationContent;
	private GeoCoder gc;
	private GeoListener geoListner = new GeoListener();
	private List<String> locations = new ArrayList<String>();
	private LocAdapter adapter;
	private ListView listview;
	public static final int RETURN_CODE = 0xcd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_location_list);
		
		listview = (ListView) findViewById(R.id.location_listview);
		adapter = new LocAdapter();
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("location", locations.get(arg2));
				intent.putExtras(bundle);
				setResult(RETURN_CODE, intent);
				finish();
//				LocationActivity.this.onDestroy();
				
			}
		});
		getLocation();
		
		gc = GeoCoder.newInstance();
		gc.setOnGetGeoCodeResultListener(geoListner);
	}
	
	private void getLocation() {
		
		locationClient = new LocationClient(this);
		option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		int span=1000;
		option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		locationClient.setLocOption(option);
		
		locationClient.registerLocationListener(new MyLocationListener());
		locationClient.start();
	}
	
	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			gc.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(arg0.getLatitude(), arg0.getLongitude())));
		}
	}
	private class GeoListener implements OnGetGeoCoderResultListener {
		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
		}
		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
			for (PoiInfo loc : arg0.getPoiList()) {
				locations.add(loc.address + ", " + loc.name);
			}
			adapter.notifyDataSetChanged();
			locationClient.stop();
		}
	}
	private class LocAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return locations.size();
		}

		@Override
		public Object getItem(int position) {
			return locations.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(LocationActivity.this, R.layout.location_item, null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.location_text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.text.setText(locations.get(position));
			
			return convertView;
		}
		private class ViewHolder {
			TextView text;
		}
	}
}
