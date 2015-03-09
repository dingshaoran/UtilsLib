package com.support.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;

import com.wutka.jox.JOXBeanInputStream;
import com.wutka.jox.JOXBeanOutputStream;

/**
 * 需要jar包jox
 * 
 * @author DSR
 * 
 */
public class XmlBeanUtil {

	/**
	 * 把xml转换为javabean,支持嵌套对象引用
	 * 
	 * @param <T>
	 * @param filePath
	 *            xml文件路径
	 * @param clazz
	 *            javabean字节码
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xml2bean(String filePath, Class<T> clazz) {
		JOXBeanInputStream jin = null;
		try {
			FileInputStream in = new FileInputStream(filePath);
			jin = new JOXBeanInputStream(in);
			T t = (T) jin.readObject(clazz);
			return t;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				jin.close();
			} catch (IOException e) {
				Log.d("dubug", clazz.getClass().getName(), e);
			} finally {
				jin = null;
			}
		}
	}

	/** * 把javabean转换为xml字符串 */
	@SuppressWarnings("null")
	public static String toXML(Object bean) {
		OutputStream xmlData = new ByteArrayOutputStream();
		JOXBeanOutputStream joxOut = new JOXBeanOutputStream(xmlData, "utf-8");
		try {
			joxOut.writeObject(bean.getClass().getSimpleName(), bean);
			byte[] buffer = null;
			xmlData.write(buffer);
			xmlData.flush();
			return buffer.toString();
		} catch (IOException exc) {
			exc.printStackTrace();
			return null;
		} finally {
			try {
				xmlData.close();
				joxOut.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				xmlData = null;
				joxOut = null;
			}
		}
	}
}
