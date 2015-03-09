package com.support.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;
import android.util.Base64;

public class Base64Utils {

	/**
	 * 将文件转换为base64以后的字符串（ 耗时）
	 * 
	 * @param filepath
	 *            文件的具体路径
	 * @return
	 * @throws IOException
	 *             找不到或者读流异常
	 * @method
	 */
	public static String File2String(String filepath) throws IOException {
		InputStream in = new FileInputStream(filepath);
		byte[] bytes = new byte[in.available()];
		in.read(bytes);
		String bsStr = Base64.encodeToString(bytes, Base64.DEFAULT);
		in.close();
		return bsStr;
	}

	/**
	 * 将图片对象转换为Base64以后的字符串
	 * 
	 * @param bitmap
	 * @return
	 * @method
	 */
	public static String Bitmap2String(Bitmap bitmap) {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		String bsStr = Base64.encodeToString(bytes, Base64.DEFAULT);
		return bsStr;
	}

	/** base64解码 **/
	public static String base64decode(String base64str) {
		if (TextUtils.isEmpty(base64str)) {
			return base64str;
		}
		byte[] bytes = Base64.decode(base64str.getBytes(), Base64.DEFAULT);
		return new String(bytes);
	}

	/** base64加密 **/
	public static String encodeToString(String base64str) {
		if (TextUtils.isEmpty(base64str)) {
			return base64str;
		}
		return Base64.encodeToString(base64str.getBytes(), Base64.DEFAULT);
	}
}
