package com.zlf.testdemo01;

import java.util.List;

import com.zlf.testdemo01.domain.ImageFloder;
import com.zlf.testdemo01.utils.BasePopupWindowForListView;
import com.zlf.testdemo01.utils.CommonAdapter;
import com.zlf.testdemo01.utils.ViewHolder;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFloder> {
	private ListView mListDir;

	public ListImageDirPopupWindow(int width, int height, List<ImageFloder> datas, View convertView) {
		super(convertView, width, height, true, datas);
	}

	@Override
	public void initViews() {
		mListDir = (ListView) findViewById(R.id.id_list_dir);
		mListDir.setAdapter(new CommonAdapter<ImageFloder>(context, mDatas, R.layout.list_dir_item) {
			@Override
			public void convert(ViewHolder helper, ImageFloder item) {
				helper.setText(R.id.id_dir_item_name, item.getName());
				helper.setImageByUrl(R.id.id_dir_item_image, item.getFirstImagePath());
				helper.setText(R.id.id_dir_item_count, item.getCount() + "张");
				if (item.isSelected()) {
					helper.setImageViewVisiable(R.id.selected_image, View.VISIBLE);
				} else {
					helper.setImageViewVisiable(R.id.selected_image, View.INVISIBLE);
				}
			}
		});
	}

	public interface OnImageDirSelected {
		void selected(ImageFloder floder);
	}

	private OnImageDirSelected mImageDirSelected;

	public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected) {
		this.mImageDirSelected = mImageDirSelected;
	}

	/* 
	 * 在点击显示选择相册的GridView的item后，显示该相册下的所有相片。
	 */
	@Override
	public void initEvents() {
		mListDir.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (mImageDirSelected != null) {
					for (int i = 0; i < mDatas.size(); ++i) {
						if (i == position) {
							mDatas.get(i).setSelected(true);
						} else {
							mDatas.get(i).setSelected(false);							
						}
					}
					// 显示该相册下的所有相片
					mImageDirSelected.selected(mDatas.get(position));
				}
			}
		});
	}

	@Override
	public void init() {
	}

	@Override
	protected void beforeInitWeNeedSomeParams(Object... params) {
	}

}
