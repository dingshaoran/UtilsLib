package com.support.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class XmlListUtil {
	/**
	 * 解析xml到list
	 * 
	 * @param clazz
	 *            javabean的字节码
	 * @param input
	 *            xml文件的字节流
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> xmlParse(Class<T> clazz, InputStream input) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(input, "UTF-8");
		List<T> list = null;
		T t = null;
		int event = parser.getEventType();
		String simpleName = clazz.getSimpleName();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				list = new ArrayList<T>();
				break;
			case XmlPullParser.START_TAG:
				if (parser.getName().equalsIgnoreCase(simpleName)) {
					t = clazz.newInstance();
				} else {
					Field[] fields = clazz.getDeclaredFields();
					for (Field field : fields) {
						if (field.getName().equalsIgnoreCase(parser.getName())) {
							field.setAccessible(true);
							field.set(t, parser.nextText());
						}
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equalsIgnoreCase(simpleName)) {
					list.add(t);
					t = null;
				}
				break;
			}
			event = parser.next();
		}
		System.out.println(list.size());
		return list;
	}

	/**
	 * 把xml写到文件中javabean中只能放String
	 * 
	 * @param list装有xml的集合
	 * @param path
	 *            文件存放路径
	 * @throws Exception
	 */
	public static <T> void xmlWrite(List<T> list, String path) throws Exception {
		XmlSerializer serializer = Xml.newSerializer();
		File file = new File(path);
		OutputStream out = new FileOutputStream(file);
		serializer.setOutput(out, "UTF-8");
		serializer.startDocument("UTF-8", true);
		serializer.startTag(null, "root");
		for (T t : list) {
			String classname = t.getClass().getSimpleName();
			Field[] fields = t.getClass().getDeclaredFields();
			serializer.startTag(null, classname);
			// serializer.attribute("", "id", p1.getId());
			for (Field field : fields) {
				field.setAccessible(true);
				String value = (String) field.get(t);
				if (field != null && value != null) {
					serializer.startTag(null, field.getName());
					serializer.text(value);
					serializer.endTag(null, field.getName());
				}
			}
			serializer.endTag(null, classname);
		}
		serializer.endTag(null, "root");
		serializer.endDocument();

		serializer.flush();
		out.close();
	}
}
