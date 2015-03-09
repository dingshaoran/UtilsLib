package com.support.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class Unzip {
	/**
	 * 压缩一个文件
	 * 
	 * @param src原文件名
	 * @param dest压缩后的文件名
	 * @throws IOException
	 */
	public static void zip(String src, String dest) throws IOException {
		ZipOutputStream out = null;
		try {

			File outFile = new File(dest);
			File fileOrDirectory = new File(src);
			out = new ZipOutputStream(new FileOutputStream(outFile));
			// 如果此文件是一个文件，否则为false。
			if (fileOrDirectory.isFile()) {
				zipFileOrDirectory(out, fileOrDirectory, "");
			} else {
				// 返回一个文件或空阵列。
				File[] entries = fileOrDirectory.listFiles();
				for (int i = 0; i < entries.length; i++) {
					// 递归压缩，更新curPaths
					zipFileOrDirectory(out, entries[i], "");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// 关闭输出流
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private static void zipFileOrDirectory(ZipOutputStream out,
			File fileOrDirectory, String curPath) throws IOException {
		// 从文件中读取字节的输入流
		FileInputStream in = null;
		try {
			// 如果此文件是一个目录，否则返回false。
			if (!fileOrDirectory.isDirectory()) {
				// 压缩文件
				byte[] buffer = new byte[4096];
				int bytes_read;
				in = new FileInputStream(fileOrDirectory);
				// 实例代表一个条目内的ZIP归档
				ZipEntry entry = new ZipEntry(curPath
						+ fileOrDirectory.getName());
				// 条目的信息写入底层流
				out.putNextEntry(entry);
				while ((bytes_read = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytes_read);
				}
				out.closeEntry();
			} else {
				// 压缩目录
				File[] entries = fileOrDirectory.listFiles();
				for (int i = 0; i < entries.length; i++) {
					// 递归压缩，更新curPaths
					zipFileOrDirectory(out, entries[i], curPath
							+ fileOrDirectory.getName() + "/");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			// throw ex;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * 解压缩功能. 将zipFile文件解压到folderPath目录下.
	 * 
	 * @param zipFile_压缩文件名
	 * @param 解压后的文件名
	 * @throws Exception
	 */
	public static void zipDecode(String zipFile, String location) throws IOException {
		int size;
		byte[] buffer = new byte[8192];

		try {
			if (!location.endsWith("/")) {
				location += "/";
			}
			File f = new File(location);
			if (!f.isDirectory()) {
				f.mkdirs();
			}
			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), 8192));
			try {
				ZipEntry ze = null;
				while ((ze = zin.getNextEntry()) != null) {
					String path = location + ze.getName();
					File unzipFile = new File(path);

					if (ze.isDirectory()) {
						if (!unzipFile.isDirectory()) {
							unzipFile.mkdirs();
						}
					} else {
						// check for and create parent directories if they don't
						// exist
						File parentDir = unzipFile.getParentFile();
						if (null != parentDir) {
							if (!parentDir.isDirectory()) {
								parentDir.mkdirs();
							}
						}

						// unzip the file
						FileOutputStream out = new FileOutputStream(unzipFile, false);
						BufferedOutputStream fout = new BufferedOutputStream(out, 8192);
						try {
							while ((size = zin.read(buffer, 0, 8192)) != -1) {
								fout.write(buffer, 0, size);
							}

							zin.closeEntry();
						} finally {
							fout.flush();
							fout.close();
						}
					}
				}
			} finally {
				zin.close();
			}
		} catch (Exception e) {
			LogUtils.e("" + e.toString());
		}
	}
}
