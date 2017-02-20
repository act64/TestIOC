package com.jkyssocial.common.network;

import android.os.AsyncTask;

import com.jkys.jkysbase.data.NetWorkResult;
import com.jkys.jkysbase.data.UploadNetWorkResult;
import com.jkys.jkysbase.Constant;
import com.jkyssocial.common.util.FileImageUpload;

import java.io.File;

import cn.dreamplus.wentang.BuildConfig;


public class UploadFileForPublishAsyncTask extends
        AsyncTask<File, Integer, UploadNetWorkResult> {

	private UploadFileForPublishListener listener;

	private int position;

	private int forBiz;

	public UploadFileForPublishAsyncTask(int position, int forBiz,
                                         UploadFileForPublishListener listener) {
		this.listener = listener;
		this.position = position;
        this.forBiz = forBiz;
	}

	@Override
	protected UploadNetWorkResult doInBackground(File... params) {
		File file = params[0];
		if (file == null)
			return null;
		String data = FileImageUpload.upLoadImage(forBiz, file,
				BuildConfig.SOCIAL_SERVER_PATH + "10/upload");
		UploadNetWorkResult jsonData = null;
		if(data != null)
			jsonData = Constant.GSON.fromJson(data, UploadNetWorkResult.class);
		return jsonData;
	}

	@Override
	protected void onPostExecute(UploadNetWorkResult uploadNetWorkResult) {
		super.onPostExecute(uploadNetWorkResult);
		if (listener != null) {
			String picUrl = null;
			if (uploadNetWorkResult != null && NetWorkResult.SUCCESS.equals(uploadNetWorkResult.getReturnCode())) {
				picUrl = uploadNetWorkResult.getUrl();
			}
			listener.onUploadFileForPublishAsyncResult(position, forBiz, picUrl);
		}
	}

	public interface UploadFileForPublishListener {
		public void onUploadFileForPublishAsyncResult(int position, int forBiz, String picUrl);
	}
}
