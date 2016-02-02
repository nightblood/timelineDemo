package com.zlf.testdemo01;

import java.util.List;

import com.zlf.testdemo01.domain.FriendInfo;
import com.zlf.testdemo01.domain.ImageInfo;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private List<FriendInfo> friendsList;
	public Context context;
	private Handler handler;

	public static final int LAYOUT_TYPE_NO_IMAGE = 0;
	public static final int LAYOUT_TYPE_1_IMAGE = 1;
	public static final int LAYOUT_TYPE_2_IMAGE = 2;
	public static final int LAYOUT_TYPE_3_IMAGE = 3;

	public MyAdapter(List<FriendInfo> list, Context context, Handler handler) {
		friendsList = list;
		this.context = context;
		this.handler = handler;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public int getCount() {
		return friendsList.size();
	}

	@Override
	public Object getItem(int position) {
		return friendsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		MyThread myTh1;
		MyThread myTh2;
		MyThread myTh3;
		MyThread myIconThread;
		Thread th1;
		Thread th2;
		Thread th3;
		Thread iconThread;
		ViewHolderNoImg holderNoImg = null;
		ViewHolder1Img holder1Img = null;
		ViewHolder2Img holder2Img = null;
		ViewHolder3Img holder3Img = null;
		

		List<ImageInfo> images = friendsList.get(position).getImages();
		int layoutType = getItemViewType(position);
		// LayoutInflater inflater = LayoutInflater.from(context);

//		System.out.println(position + "th. id:" + friendsList.get(position).getId());
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
//			System.out.println(">>>>>>convertView is null!!! Layout Type: " + layoutType);
			switch (layoutType) {
			case LAYOUT_TYPE_NO_IMAGE:
				convertView = mInflater.inflate(R.layout.moment_with_no_image, null);
				holderNoImg = new ViewHolderNoImg();
				holderNoImg.name = (TextView) convertView.findViewById(R.id.name);
				holderNoImg.content = (TextView) convertView.findViewById(R.id.content);
				holderNoImg.icon = (ImageView) convertView.findViewById(R.id.icon);

				holderNoImg.name.setText(friendsList.get(position).getName());
				holderNoImg.content.setText(friendsList.get(position).getContent());
				myIconThread = new MyThread(friendsList.get(position).getIcon(), handler, position, 0xffffffff, friendsList, mInflater, holderNoImg.icon);
				iconThread = new Thread(myIconThread);
				iconThread.start();
				try {
					iconThread.join();
				} catch (InterruptedException e3) {
					e3.printStackTrace();
				}
				convertView.setTag(holderNoImg);
				break;
			case LAYOUT_TYPE_1_IMAGE:
				convertView = mInflater.inflate(R.layout.moment_with_1_image, null);
				holder1Img = new ViewHolder1Img();

				holder1Img.image1 = (ImageView) convertView.findViewById(R.id.image1);
				holder1Img.name = (TextView) convertView.findViewById(R.id.name);
				holder1Img.content = (TextView) convertView.findViewById(R.id.content);
				holder1Img.icon = (ImageView) convertView.findViewById(R.id.icon);

				holder1Img.name.setText(friendsList.get(position).getName());
				holder1Img.content.setText(friendsList.get(position).getContent());
				
				myIconThread = new MyThread(friendsList.get(position).getIcon(), handler, position, 0xffffffff, friendsList, mInflater, holder1Img.icon);
				iconThread = new Thread(myIconThread);
				
				myTh1 = new MyThread(images.get(0).getUrl(), handler, position, 0, friendsList, mInflater, holder1Img.image1);
				th1 = new Thread(myTh1);
				th1.start();
				iconThread.start();
				try {
					th1.join();
					iconThread.join();
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
				convertView.setTag(holder1Img);
				break;
			case LAYOUT_TYPE_2_IMAGE:
				convertView = mInflater.inflate(R.layout.moment_with_2_image, null);
				holder2Img = new ViewHolder2Img();
				holder2Img.image1 = (ImageView) convertView.findViewById(R.id.image1);
				holder2Img.image2 = (ImageView) convertView.findViewById(R.id.image2);
				holder2Img.name = (TextView) convertView.findViewById(R.id.name);
				holder2Img.content = (TextView) convertView.findViewById(R.id.content);
				holder2Img.icon = (ImageView) convertView.findViewById(R.id.icon);

				holder2Img.name.setText(friendsList.get(position).getName());
				holder2Img.content.setText(friendsList.get(position).getContent());

				myIconThread = new MyThread(friendsList.get(position).getIcon(), handler, position, 0xffffffff, friendsList, mInflater, holder2Img.icon);
				myTh1 = new MyThread(images.get(0).getUrl(), handler, position, 0, friendsList, mInflater, holder2Img.image1);
				myTh2 = new MyThread(images.get(1).getUrl(), handler, position, 1, friendsList, mInflater, holder2Img.image2);

				th1 = new Thread(myTh1);
				th2 = new Thread(myTh2);
				iconThread = new Thread(myIconThread);
				
				th1.start();
				th2.start();
				iconThread.start();
				try {
					th1.join();
					th2.join();
					iconThread.join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				convertView.setTag(holder2Img);
				break;
			case LAYOUT_TYPE_3_IMAGE:
				convertView = mInflater.inflate(R.layout.moment_with_3_image, null);
				holder3Img = new ViewHolder3Img();
				holder3Img.image1 = (ImageView) convertView.findViewById(R.id.image1);
				holder3Img.image2 = (ImageView) convertView.findViewById(R.id.image2);
				holder3Img.image3 = (ImageView) convertView.findViewById(R.id.image3);
				holder3Img.name = (TextView) convertView.findViewById(R.id.name);
				holder3Img.content = (TextView) convertView.findViewById(R.id.content);
				holder3Img.icon = (ImageView) convertView.findViewById(R.id.icon);

				holder3Img.name.setText(friendsList.get(position).getName());
				holder3Img.content.setText(friendsList.get(position).getContent());

				myIconThread = new MyThread(friendsList.get(position).getIcon(), handler, position, 0xffffffff, friendsList, mInflater, holder3Img.icon);
				myTh1 = new MyThread(images.get(0).getUrl(), handler, position, 0, friendsList, mInflater, holder3Img.image1);
				myTh2 = new MyThread(images.get(1).getUrl(), handler, position, 1, friendsList, mInflater, holder3Img.image2);
				myTh3 = new MyThread(images.get(2).getUrl(), handler, position, 2, friendsList, mInflater, holder3Img.image3);

				iconThread = new Thread(myIconThread);
				th1 = new Thread(myTh1);
				th2 = new Thread(myTh2);
				th3 = new Thread(myTh3);
				iconThread.start();
				th1.start();
				th2.start();
				th3.start();

				try {
					iconThread.join();
					th1.join();
					th2.join();
					th3.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				convertView.setTag(holder3Img);
				break;
			default:
				return null;
			}
//			System.out.println("----------getView end!!");
		} else {
			System.out.println(position + ". ConvertView not null");
			switch (layoutType) {
			case LAYOUT_TYPE_NO_IMAGE:
				holderNoImg = (ViewHolderNoImg) convertView.getTag();
				holderNoImg.name.setText(friendsList.get(position).getName());
				holderNoImg.content.setText(friendsList.get(position).getContent());
				if (!friendsList.get(position).isImagesCached()){
					myIconThread = new MyThread(friendsList.get(position).getIcon(), handler, position, 0xffffffff, friendsList, mInflater, holderNoImg.icon);
					iconThread = new Thread(myIconThread);
					iconThread.start();
					try {
						iconThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					holderNoImg.icon.setImageBitmap(friendsList.get(position).getIconBitmap());
				}
				break;
			case LAYOUT_TYPE_1_IMAGE:
				holder1Img = (ViewHolder1Img) convertView.getTag();
				holder1Img.name.setText(friendsList.get(position).getName());
				holder1Img.content.setText(friendsList.get(position).getContent());
				
				if (!friendsList.get(position).isImagesCached()){
					myTh1 = new MyThread(images.get(0).getUrl(), handler, position, 0, friendsList, mInflater, holder1Img.image1);
					myIconThread = new MyThread(friendsList.get(position).getIcon(), handler, position, 0xffffffff, friendsList, mInflater, holder1Img.icon);
					th1 = new Thread(myTh1);
					iconThread = new Thread(myIconThread);
					th1.start();
					iconThread.start();
					try {
						th1.join();
						iconThread.join();
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
				} else {
					holder1Img.image1.setImageBitmap(friendsList.get(position).getImages().get(0).getBitmap());
					holder1Img.icon.setImageBitmap(friendsList.get(position).getIconBitmap());
				}
				break;
			case LAYOUT_TYPE_2_IMAGE:
				holder2Img = (ViewHolder2Img) convertView.getTag();
				holder2Img.name.setText(friendsList.get(position).getName());
				holder2Img.content.setText(friendsList.get(position).getContent());
				
				if (!friendsList.get(position).isImagesCached()){
					myIconThread = new MyThread(friendsList.get(position).getIcon(), handler, position, 0xffffffff, friendsList, mInflater, holder2Img.icon);
					myTh1 = new MyThread(images.get(0).getUrl(), handler, position, 0, friendsList, mInflater, holder2Img.image1);
					myTh2 = new MyThread(images.get(0).getUrl(), handler, position, 1, friendsList, mInflater, holder2Img.image2);
					iconThread = new Thread(myIconThread);
					th1 = new Thread(myTh1);
					th2 = new Thread(myTh2);
					iconThread.start();
					th1.start();
					th2.start();
					try {
						iconThread.join();
						th1.join();
						th2.join();
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
				} else {
					holder2Img.image1.setImageBitmap(friendsList.get(position).getImages().get(0).getBitmap());
					holder2Img.image2.setImageBitmap(friendsList.get(position).getImages().get(1).getBitmap());
					holder2Img.icon.setImageBitmap(friendsList.get(position).getIconBitmap());
				}
				break;
			case LAYOUT_TYPE_3_IMAGE:
				holder3Img = (ViewHolder3Img) convertView.getTag();
				holder3Img.name.setText(friendsList.get(position).getName());
				holder3Img.content.setText(friendsList.get(position).getContent());
				
				if (!friendsList.get(position).isImagesCached()){
					myIconThread = new MyThread(friendsList.get(position).getIcon(), handler, position, 0xffffffff, friendsList, mInflater, holder3Img.icon);
					myTh1 = new MyThread(images.get(0).getUrl(), handler, position, 0, friendsList, mInflater, holder3Img.image1);
					myTh2 = new MyThread(images.get(1).getUrl(), handler, position, 1, friendsList, mInflater, holder3Img.image2);
					myTh3 = new MyThread(images.get(2).getUrl(), handler, position, 2, friendsList, mInflater, holder3Img.image3);
					iconThread = new Thread(myIconThread);
					th1 = new Thread(myTh1);
					th2 = new Thread(myTh2);
					th3 = new Thread(myTh3);
					iconThread.start();
					th1.start();
					th2.start();
					th3.start();
					try {
						iconThread.join();
						th1.join();
						th2.join();
						th3.join();						
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
				} else {
					holder3Img.image1.setImageBitmap(friendsList.get(position).getImages().get(0).getBitmap());
					holder3Img.image2.setImageBitmap(friendsList.get(position).getImages().get(1).getBitmap());
					holder3Img.image3.setImageBitmap(friendsList.get(position).getImages().get(2).getBitmap());
					holder3Img.icon.setImageBitmap(friendsList.get(position).getIconBitmap());
				}
				break;
			default:
				return null;
			}
		}
		friendsList.get(position).setImagesCached(true);
		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		return friendsList.get(position).getLayoutType();
	}
	@Override
	public int getViewTypeCount() {
		return 4;
	}

	class ViewHolderNoImg {
		ImageView icon;
		TextView name;
		TextView content;
	}
	class ViewHolder1Img extends ViewHolderNoImg {
		ImageView image1;
	}
	class ViewHolder2Img extends ViewHolderNoImg {
		ImageView image1;
		ImageView image2;
	}
	class ViewHolder3Img extends ViewHolderNoImg {
		ImageView image1;
		ImageView image2;
		ImageView image3;
	}
}
