/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xutils.http.client.multipart.content;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import com.support.utils.CloseUtils;
import com.xutils.http.client.multipart.MIME;

/**
 * @since 4.0
 */
public class FileBody extends AbstractContentBody {

	private final File file;
	private final String filename;
	private final String charset;

	/**
	 * @since 4.1
	 */
	public FileBody(final File file, final String filename, final String mimeType, final String charset) {
		super(mimeType);
		if (file == null) {
			throw new IllegalArgumentException("File may not be null");
		}
		this.file = file;
		if (filename != null) {
			this.filename = filename;
		} else {
			this.filename = file.getName();
		}
		this.charset = charset;
	}

	/**
	 * @since 4.1
	 */
	public FileBody(final File file, final String mimeType, final String charset) {
		this(file, null, mimeType, charset);
	}

	public FileBody(final File file, final String mimeType) {
		this(file, null, mimeType, null);
	}

	public FileBody(final File file) {
		this(file, null, "application/octet-stream", null);
	}

	public InputStream getInputStream() throws IOException {
		return new FileInputStream(this.file);
	}

	@Override
	@SuppressWarnings("resource")
	public void writeTo(final OutputStream out) throws IOException {
		if (out == null) {
			throw new IllegalArgumentException("Output stream may not be null");
		}
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(this.file));
			byte[] tmp = new byte[4096];
			int l;
			while ((l = in.read(tmp)) != -1) {
				out.write(tmp, 0, l);
				callBackInfo.pos += l;
				if (!callBackInfo.doCallBack(false)) {
					throw new InterruptedIOException("cancel");
				}
			}
			out.flush();
		} finally {
			CloseUtils.close(in);
		}
	}

	@Override
	public String getTransferEncoding() {
		return MIME.ENC_BINARY;
	}

	@Override
	public String getCharset() {
		return charset;
	}

	@Override
	public long getContentLength() {
		return this.file.length();
	}

	@Override
	public String getFilename() {
		return filename;
	}

	public File getFile() {
		return this.file;
	}

}
