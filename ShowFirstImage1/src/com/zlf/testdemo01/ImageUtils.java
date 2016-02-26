package com.zlf.testdemo01;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;

public class ImageUtils {
	
	 public static Bitmap compressImageFromFile(String srcPath) {  
	        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
	        newOpts.inJustDecodeBounds = true; //ֻ����,��������  
	        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
	  
	        newOpts.inJustDecodeBounds = false;  
	        int w = newOpts.outWidth;  
	        int h = newOpts.outHeight;  
	        float hh = 800f; 
	        float ww = 480f; 
//	        float hh = 400f;
//	        float ww = 240f;
	        int be = 1;  
	        if (w > h && w > ww) {  
	            be = (int) (newOpts.outWidth / ww);  
	        } else if (w < h && h > hh) {  
	            be = (int) (newOpts.outHeight / hh);  
	        }  
	        if (be <= 0)  
	            be = 1;  
	        newOpts.inSampleSize = be;//���ò�����  
	          
	        newOpts.inPreferredConfig = Config.ARGB_8888;//��ģʽ��Ĭ�ϵ�,�ɲ���  
	        newOpts.inPurgeable = true;// ͬʱ���òŻ���Ч  
	        newOpts.inInputShareable = true;//����ϵͳ�ڴ治��ʱ��ͼƬ�Զ�������  
	          
	        bitmap = BitmapFactory.decodeFile(srcPath, newOpts); 
	        return bitmap;  
	    }  
//	public static Bitmap resizePhoto(String filePath) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//
//        BitmapFactory.decodeFile(filePath, options);
//        
//        options.inSampleSize = getInSampleSize(options, 100, 100);
//        options.inJustDecodeBounds = false;
//
//		Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
//				
//        return bmp;
//    }

    /**
     * ����Ŀ���С��ȡѹ������
     * @param options
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    private static int getInSampleSize(BitmapFactory.Options options, int targetWidth, int targetHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;
        if (height > targetHeight || width > targetWidth) {
            final int heightRate = Math.round(height / targetHeight);
            final int widthRate = Math.round(width / targetWidth);

            inSampleSize = heightRate > widthRate ? heightRate : widthRate;
        }
        return inSampleSize;
    }
    
    public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
		int options = 100;
		while ( baos.toByteArray().length / 1024 > 80) {	//ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��		
			baos.reset();//����baos�����baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos); //����ѹ��options%����ѹ��������ݴ�ŵ�baos��
			options -= 10;//ÿ�ζ�����10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray()); //��ѹ���������baos��ŵ�ByteArrayInputStream��
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null); //��ByteArrayInputStream��������ͼƬ
		return bitmap;
	}
	public static Bitmap comp(Bitmap image) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if( baos.toByteArray().length > 512) { // �ж����ͼƬ����512 k,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���	
			baos.reset(); // ����baos�����baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos); // ����ѹ��80%����ѹ��������ݴ�ŵ�baos��
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// ��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// ���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ
		float hh = 800f; //�������ø߶�Ϊ800f
		float ww = 480f; //�������ÿ��Ϊ480f
		// ���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
		int be = 1; //be=1��ʾ������
		if (w > h && w > ww) { // �����ȴ�Ļ����ݿ�ȹ̶���С����
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) { // ����߶ȸߵĻ����ݿ�ȹ̶���С����
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//�������ű���
		// ���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap); // ѹ���ñ�����С���ٽ�������ѹ��
	}
	public static String bitmap2file(Bitmap bitmap, String name){
		String fileName = MainActivity.localPath + "/" + name;
//		String fileName = "/" + name;
		File file = new File(fileName);
		if (file.exists()) {
//			return fileName;
			file.delete();
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}
}
