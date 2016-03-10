package com.zlf.testdemo01;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.graphics.Bitmap.Config;
import android.media.ExifInterface;

public class ImageUtils {
	
	 public static Bitmap compressImageFromFile(String srcPath) {  
	        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
	        newOpts.inJustDecodeBounds = true; //只读边,不读内容  
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
	        newOpts.inSampleSize = be;//设置采样率  
	          
	        newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设  
	        newOpts.inPurgeable = true;// 同时设置才会有效  
	        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收  
	          
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
     * 根据目标大小获取压缩比例
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
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while ( baos.toByteArray().length / 1024 > 80) {	//循环判断如果压缩后图片是否大于100kb,大于继续压缩		
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos); //这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;//每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray()); //把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null); //把ByteArrayInputStream数据生成图片
		return bitmap;
	}
	public static Bitmap comp(Bitmap image) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if( baos.toByteArray().length > 512) { // 判断如果图片大于512 k,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出	
			baos.reset(); // 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos); // 这里压缩80%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f; //这里设置高度为800f
		float ww = 480f; //这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1; //be=1表示不缩放
		if (w > h && w > ww) { // 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) { // 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap); // 压缩好比例大小后再进行质量压缩
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
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}

	// 计算图片的缩放值
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	// 把bitmap转换成String
	public static String bitmapToString(String filePath) {

		Bitmap bm = getSmallBitmap(filePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
		byte[] b = baos.toByteArray();
		return Base64.encodeToString(b, Base64.DEFAULT);
	}

	// 根据路径获得图片并压缩，返回bitmap用于显示
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 480, 800);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}
	public static Bitmap rotateBitmap(Bitmap bitmap,int degress) {  
        if (bitmap != null) {  
            Matrix m = new Matrix();  
            m.postRotate(degress);   
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),  
                    bitmap.getHeight(), m, true);  
            return bitmap;  
        }  
        return bitmap;  
    }  
	public static int readPictureDegree(String path) {  
        int degree = 0;  
        try {  
            ExifInterface exifInterface = new ExifInterface(path);  
            int orientation = exifInterface.getAttributeInt(  
                    ExifInterface.TAG_ORIENTATION,  
                    ExifInterface.ORIENTATION_NORMAL);  
            switch (orientation) {  
            case ExifInterface.ORIENTATION_ROTATE_90:  
                degree = 90;  
                break;  
            case ExifInterface.ORIENTATION_ROTATE_180:  
                degree = 180;  
                break;  
            case ExifInterface.ORIENTATION_ROTATE_270:  
                degree = 270;  
                break;  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return degree;  
    } 
}
