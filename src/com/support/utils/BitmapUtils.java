package com.support.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {
	/** 根据path加载图片bit **/
	public static Bitmap getBitmap(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;//
		options.inJustDecodeBounds = false;
		Bitmap b = BitmapFactory.decodeFile(path, options);
		return b;
	}

	/** 压缩图片（耗时） **/
	public static Bitmap compressBitmap(String path, int compressSize) {
		Bitmap b = getBitmap(path);
		return compressImage(b, compressSize);
	}

	/**
	 * 压缩图片比例（依据文件路径） 再压缩图片质量返回(耗时）
	 * 
	 * @param srcPath
	 * @param compressSize
	 * @method
	 */
	public static Bitmap compressImage(String srcPath, int compressSize) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 480f;// 这里设置高度为800f
		float ww = 320f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap, compressSize);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 压缩bitmap的质量（按照指定的压缩大小来处理）如果不传，默认为100kb（耗时）
	 * 
	 * @param bitmap
	 * @param compressSize
	 * @return
	 * @method
	 */
	public static Bitmap compressImage(Bitmap bitmap, int compressSize) {
		if (compressSize == 0)
			compressSize = 100;// 默认压缩100kb
		LogUtils.e("--------图片压缩------" + compressSize);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		if (baos.toByteArray().length / 1024 < compressSize) {
			LogUtils.e("当前图片<" + compressSize);
			return bitmap;
		}
		while (baos.toByteArray().length / 1024 > compressSize) { // 循环判断如果压缩后图片是否大于compressSizekb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 6;// 每次都减少6
			if (options <= 0)
				break;
			LogUtils.e("baos.toByteArray().length==" + baos.toByteArray().length);
			LogUtils.e("------压缩次数-------" + options
					+ "baos.toByteArray().length / 1024=="
					+ baos.toByteArray().length / 1024);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		LogUtils.e("------压缩结果-------" + baos.toByteArray().length / 1024);
		Bitmap bitNew = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitNew;
	}

	/**
	 * 保存图片到SD卡
	 * 
	 * @param bitmap
	 *            图片的bitmap对象
	 * @return
	 */
	public static String saveBitmap(Bitmap bitmap, String fileTarget) {
		if (!FileUtils.isSdcardExist()) {
			LogUtils.e("sd卡不存在");
			return null;
		}
		FileOutputStream fileOutputStream = null;
		File dir = new File(fileTarget);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String fileName = UUID.randomUUID().toString() + ".jpg";
		String newFilePath = fileTarget + fileName;
		File file = FileUtils.createNewFile(newFilePath);
		if (file == null) {
			LogUtils.e("创建文件失败");
			return null;
		}
		try {
			fileOutputStream = new FileOutputStream(newFilePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
		} catch (FileNotFoundException e1) {
			LogUtils.e("文件不存在");
			return null;
		} finally {
			try {
				fileOutputStream.flush();
				fileOutputStream.close();
			} catch (IOException e) {
				return null;
			}
		}
		return newFilePath;
	}

}
