package com.zlf.testdemo01;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zlf.testdemo01.domain.ImageInfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class NoScrollItemAdapter extends BaseAdapter{

	private List<ImageInfo> datas;
	private Context context;
	
	public NoScrollItemAdapter(List<ImageInfo> list, Context c) {
		datas = list;
		context = c;
	}
	
	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View inflate = View.inflate(context, R.layout.image_gridview, null);
		ImageView iv = (ImageView) inflate.findViewById(R.id.image);
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				//.bitmapConfig(Config.RGB_565)
				.build();
		ImageLoader.getInstance().displayImage(datas.get(arg0).getUrl(), iv, options);
		return inflate;
	}
}
