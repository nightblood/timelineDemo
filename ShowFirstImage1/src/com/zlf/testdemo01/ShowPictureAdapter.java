package com.zlf.testdemo01;

import java.util.LinkedList;
import java.util.List;

import com.zlf.testdemo01.utils.CommonAdapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class ShowPictureAdapter extends CommonAdapter<String>
{

	public ShowPictureAdapter(Context context, List<String> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	public static List<String> mSelectedImage = new LinkedList<String>();

	private String mDirPath;
	private Handler mHandler;

	public ShowPictureAdapter(Context context, List<String> mDatas, int itemLayoutId, String dirPath, Handler handler) {
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
		this.mHandler = handler;
	}

	@Override
	public void convert(final com.zlf.testdemo01.utils.ViewHolder helper, final String item) {
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
		helper.setImageResource(R.id.id_item_select, R.drawable.picture_unselected);
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
		
		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);
		
		mImageView.setColorFilter(null);
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mSelectedImage.contains(mDirPath + "/" + item)) {
					mSelectedImage.remove(mDirPath + "/" + item);
					mSelect.setImageResource(R.drawable.picture_unselected);
					mImageView.setColorFilter(null);
				} else {
					if (mSelectedImage.size() >= ShowPicturesActivity.totalPictureCount) {
						Toast.makeText(mContext, "最多只能添加9张图片！", Toast.LENGTH_SHORT).show();
						return;
					}
					mSelectedImage.add(mDirPath + "/" + item);
					mSelect.setImageResource(R.drawable.pictures_selected);
					mImageView.setColorFilter(Color.parseColor("#77000000"));
				}
				Message msg = new Message();
				msg.what = 0xf;
				msg.arg1 = mSelectedImage.size();
				mHandler.sendMessage(msg);
			}
		});
		
		if (mSelectedImage.contains(mDirPath + "/" + item)) {
			mSelect.setImageResource(R.drawable.pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}
	}
}
