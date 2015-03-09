package com.support.helper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.text.TextUtils;

import com.support.utils.FileUtils;
import com.support.utils.LogUtils;
import com.support.utils.SharedPreferencesUtils;
import com.support.utils.ToastUtils;

/**
 * 声音工具类
 * 
 * @author jack
 * 
 */
public class AudioHelper {
	public static final String FILE_NAME = "filename";
	public static final String FULL_FILE_NAME = "fullname";
	private final Context mContext;
	private boolean isPlaying;
	private boolean isUseMediaRecorder;
	private boolean mIsRecording = false;// 是否正在录音
	private MediaRecorder mRecorder;//
	private AudioRecord mAudioRecord = null;
	private Thread mRecThread = null;// 录音线程
	private boolean mHasVolume;
	private int mCurrentVolume;
	private Map<String, String> mAudioData;
	private String audioFilepath;
	private int mRecBufSize;
	private final String RECORD_WAY = "rec";
	private final String RECORDFILEDIR = "recf";
	private static final int FREQUENCY = 8000;
	@SuppressWarnings("deprecation")
	private static final int CHANNEL = AudioFormat.CHANNEL_CONFIGURATION_MONO;// 设置声道
	private static final int ENCODING_BITRATE = AudioFormat.ENCODING_PCM_16BIT;// 设置编码
	private AudioSupport mSupport;
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	public static final int MIN_VOLUME = 10;

	/**
	 * 
	 * 录音工具类对外提供的接口
	 */
	public interface AudioSupport {
		// 在录音结束后调用的方法
		void onStop();
	}

	public AudioHelper(Context context) {
		this.mContext = context;
		isUseMediaRecorder = getRecordWay() != 2;
	}

	/** 是否正在播放 **/
	public boolean isPlaying() {
		return isPlaying;
	}

	/** 设置播放状态 **/
	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	/** 获取录音方式 **/
	private int getRecordWay() {
		return SharedPreferencesUtils.getInt(RECORD_WAY, 0);
	}

	/** 设置录音方式 **/
	private void saveRecordWay(int way) {
		SharedPreferencesUtils.saveInt(RECORD_WAY, way);
	}

	/** 获取录音状态 **/
	public boolean getRecordState() {
		return mIsRecording;
	}

	/**
	 * 获取当前音量
	 * 
	 * @return
	 */
	public int getVolume() {
		int ret = 0;
		if (mIsRecording) {
			ret = isUseMediaRecorder ? mRecorder.getMaxAmplitude() : mCurrentVolume;
			if (ret > 0) {
				mHasVolume = true;
			}
		}
		return ret;
	}

	/** 开始录音 **/
	public boolean startRecord() {
		mHasVolume = false;
		if (isUseMediaRecorder) {
			return startMediaRecorder();
		}

		mAudioData = null;

		mRecBufSize = AudioRecord.getMinBufferSize(FREQUENCY, CHANNEL, ENCODING_BITRATE);
		mRecBufSize += 320 - mRecBufSize % 320;
		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, FREQUENCY, CHANNEL, ENCODING_BITRATE, mRecBufSize);
		mAudioRecord.startRecording();
		mIsRecording = true;
		mRecThread = new Thread(new Runnable() {
			@Override
			public void run() {
				writeAudioDataToFile();
			}
		}, "AudioRecorder Thread");

		mRecThread.start();

		return true;
	}

	// 将音频数据写入文件
	private void writeAudioDataToFile() {
		// 获取UUID为名的录音
		String fileName = UUID.randomUUID() + ".amr";
		byte data[] = new byte[mRecBufSize];
		FileOutputStream out = null;
		try {
			String dirs = getRecordFileDir();
			FileUtils.mkDirs(dirs);
			audioFilepath = dirs + fileName;
			out = new FileOutputStream(audioFilepath);
			out.write("#!AMR\n".getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		int read = 0;
		if (null != out) {
			mAudioData = new HashMap<String, String>();
			mAudioData.put(FULL_FILE_NAME, audioFilepath);

			while (mIsRecording) {
				read = mAudioRecord.read(data, 0, mRecBufSize);
				if (read > 0) {
					float v = 0;
					// 将 buffer 内容取出，进行平方和运算
					for (int i = 0; i < read; i++) {
						float one = data[i];
						v += one * one;
					}
					// 平方和除以数据总长度，得到音量大小。
					mCurrentVolume = (int) ((v / read - 2200) * 8);
				}
				if (AudioRecord.ERROR_INVALID_OPERATION != read) {
					try {
						InputStream in = new ByteArrayInputStream(data);
						InputStream amr = createAmrInputStream(in);

						while (true) {
							int count = amr.read(data, 0, mRecBufSize);
							if (count <= 0) {
								break;
							}
							out.write(data, 0, count);
						}

						in.close();
						amr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		mIsRecording = false;
	}

	/** 停止录音 **/
	public void stopRecord() {
		boolean useMediaRecorder = isUseMediaRecorder;
		if (getRecordWay() == 0) {
			saveRecordWay(mHasVolume ? 1 : 2);
			isUseMediaRecorder = mHasVolume;
		}

		if (useMediaRecorder) {
			stopMediaRecorder();
			return;
		}

		if (null != mAudioRecord) {
			mIsRecording = false;
			mAudioRecord.stop();
			mAudioRecord.release();
			mAudioRecord = null;
			mRecThread = null;
		}

		if (mAudioData == null) {
			ToastUtils.show(mContext, "录音失败");
		}

		String audio = mAudioData.get(FULL_FILE_NAME);
		if (TextUtils.isEmpty(audio)) {
			ToastUtils.show(mContext, "录音不完整");
		}
	}

	private void stopMediaRecorder() {
		mIsRecording = false;
		mRecorder.stop();
		mRecorder = null;

		if (mAudioData == null) {
			ToastUtils.show(mContext, "录音失败");
		}

		String audio = mAudioData.get(FULL_FILE_NAME);
		if (TextUtils.isEmpty(audio)) {
			ToastUtils.show(mContext, "录音不完整");
		}
	}

	/** 开始播放 **/
	public void startPlay(String aPath, AudioSupport support) {
		mSupport = support;
		String path = aPath;
		isPlaying = true;
		try {
			if (mMediaPlayer == null) {
				mMediaPlayer = new MediaPlayer();
			} else {
				mMediaPlayer.reset();
			}
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
			int maxVal = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			if (maxVal < MIN_VOLUME) {
				// TODO:bug 设置音量最大
				am.setStreamVolume(AudioManager.STREAM_MUSIC, MIN_VOLUME, AudioManager.FLAG_PLAY_SOUND);
			}

			mMediaPlayer.setLooping(false);
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					stopPlayer(true);
				}
			});

			mMediaPlayer.prepare();
			mMediaPlayer.start();

		} catch (Exception e) {
		}
	}

	/** 停止播放 **/
	public void stopPlayer(boolean updateDisplay) {
		if (isPlaying == true) {
			isPlaying = false;
			mMediaPlayer.stop();
			mMediaPlayer.reset();

			if (updateDisplay) {
				mSupport.onStop();
			}
		}
	}

	/** 获取录音文件的目录 **/
	private String getRecordFileDir() {
		return FileUtils.getSDPath() + RECORDFILEDIR;
	}

	/** 开始录音 **/
	@SuppressWarnings("deprecation")
	private boolean startMediaRecorder() {
		try {
			mIsRecording = true;
			mAudioData = null;
			String fileName = UUID.randomUUID() + ".amr";
			String recodeDir = getRecordFileDir();
			File fileDir = new File(recodeDir);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			audioFilepath = recodeDir + fileName;
			LogUtils.i("录音路径" + audioFilepath);
			File audioFile = new File(audioFilepath);
			if (audioFile.exists())
				audioFile.delete();
			audioFile.createNewFile();

			if (mRecorder != null)
				mRecorder.reset();
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			mRecorder.setAudioEncodingBitRate(44100);
			mRecorder.setOutputFile(audioFile.getAbsolutePath());
			mRecorder.prepare();
			mRecorder.start();
			mAudioData = new HashMap<String, String>();
			mAudioData.put(FILE_NAME, fileName);
			mAudioData.put(FULL_FILE_NAME, audioFilepath);

			return true;
		} catch (Exception e) {
			return false;
		}

	}

	private static final String AMR_INPUT_STREAM_CLASS = "android.media.AmrInputStream";

	private static Class<?> getAmrInputStreamClass() throws ClassNotFoundException {
		return Class.forName(AMR_INPUT_STREAM_CLASS);
	}

	private static InputStream createAmrInputStream(InputStream in) {
		InputStream ret = null;
		try {
			Class<?> clazz = getAmrInputStreamClass();
			Constructor<?> constructor = clazz.getConstructor(new Class[] { InputStream.class });
			ret = (InputStream) constructor.newInstance(new Object[] { in });
		} catch (Exception e) {
		}
		return ret;
	}

}
