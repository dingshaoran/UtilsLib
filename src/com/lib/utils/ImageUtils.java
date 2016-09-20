package com.lib.utils;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Rect;

/**
 * Description: 图片工具类 <br/>
 * Program Name: 优信拍商家 <br/>
 * Date: 2015年1月26日11:56:22
 * 
 * @author 黄炜 huangwei@youxinpai.com
 */

public class ImageUtils {
	/**
	 * 
	 * @param data 存有图片数据的byte[]
	 * @param size 需要裁剪到的大小
	 * @param mode 如果裁剪后的比例与原始比例不一致，压缩到需要尺寸的大小后的裁剪起始位置<br/>
	 * 				-2CenterCrop，-1FitXY, else 起始位置
	 * @return
	 */
	public static Bitmap byte2Bitmap(byte[] data, int w, int h, Rect mode) {
		float ratio;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		float wRatio = (float) options.outWidth / w;
		float hRatio = (float) options.outHeight / h;
		if (wRatio < hRatio) {// 选择比例小的，可以裁出比需要的尺寸，一边宽另一边相等的图片
			ratio = hRatio;
		} else {
			ratio = wRatio;
		}
		options.inSampleSize = (int) (ratio > 1 ? ratio : 1);// 由于垃圾手机拍的图片可能小，必须要控制这个值不能小于1
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		if (ratio <= 1) {
			return bitmap;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int marginTeft = mode.left;
		if (marginTeft == -2) {
			Matrix m = new Matrix();
			if ((float) width / height > (float) w / h) {//如果大于1.5代表这个图片宽了
				m.setScale((float) h / height, (float) h / height);
				int widthP = height * w / h;
				bitmap = Bitmap.createBitmap(bitmap, (width - widthP) / 2, 0, widthP, height, m, false);
			} else {//长了
				m.setScale((float) w / width, (float) w / width);
				int hightP = width * h / w;
				bitmap = Bitmap.createBitmap(bitmap, 0, (height - hightP) / 2, width, hightP, m, false);
			}
		} else if (marginTeft == -1) {
			Matrix matrix = new Matrix();
			matrix.isIdentity();
			matrix.rectStaysRect();
			float mWidth = ((float) w) / width;
			float mHeight = ((float) h) / height;
			matrix.setScale(mWidth, mHeight);// 获取缩放比例
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);// 根据缩放比例获取新的位图
		} else {
			int marginTop = mode.top;
			Matrix matrix = new Matrix();
			int dw = w + marginTeft + mode.right;
			int dh = h + marginTop + mode.bottom;
			float mWidth = ((float) dw) / width;
			float mHeight = ((float) dh) / height;
			matrix.setScale(mWidth, mHeight);// 获取缩放比例
			bitmap = Bitmap.createBitmap(bitmap, (int) (marginTeft / mWidth), (int) (marginTop / mHeight), (int) (w / mWidth), (int) (h / mHeight), matrix, false);
		}
		return bitmap;
	}

	/**
	 * 从资源中获取Bitmap
	 */
	public static Bitmap res2Bitmap(Context context, int drawableId) {
		Resources res = context.getResources();
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeResource(res, drawableId);
		} catch (OutOfMemoryError e) {
			if (bmp != null && !bmp.isRecycled()) {
				bmp.recycle();
			}
			Options opts = new Options();
			opts.inSampleSize = 10;
			opts.inPurgeable = true; // bitmap can be purged to disk
			opts.inInputShareable = true;
			bmp = BitmapFactory.decodeResource(res, drawableId, opts);
		} catch (Exception e) {
		}
		return bmp;
	}

	/**
	 * 压缩bitmap的质量以kb为单位（按照指定的压缩大小来处理）如果不传，默认为100kb（耗时）
	 * 
	 * @param bitmap
	 * @param compressSize
	 * @return
	 * @method
	 */
	public static byte[] getSmallBitmap(Bitmap bitmap, int size_kb) {
		if (bitmap == null) {
			return null;
		}
		if (size_kb == 0)
			size_kb = 100;// 默认压缩100kb
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 100;
		bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		while (baos.toByteArray().length / 1024 > size_kb) { // 循环判断如果压缩后图片是否大于compressSizekb,大于继续压缩
			options = (options - (180 / options + baos.toByteArray().length / 550 / size_kb));// 每次都减少动态算 size=length/100-x
			baos.reset();// 重置baos即清空baos
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			if (options <= 40)
				break;
		}
		return baos.toByteArray();
	}

}
