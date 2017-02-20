package com.jkyssocial.common.network;

import android.os.AsyncTask;

import com.jkys.jkysbase.data.NetWorkResult;
import com.jkys.jkysbase.data.UploadNetWorkResult;
import com.jkys.jkysbase.Constant;
import com.jkyssocial.common.util.FileImageUpload;

import java.io.File;

import cn.dreamplus.wentang.BuildConfig;

/**
 * Created by on
 * Author: Zern
 * DATE: 2015/12/24
 * Time: 11:29
 * email: AndroidZern@163.com
 */
public class UpLoadCircleAvatarAsyncTask extends AsyncTask<File, Integer, UploadNetWorkResult> {

    private UploadCircleAvatarListener uploadCircleAvatarlistener;

    private int position;

    private int forBiz;

    public UpLoadCircleAvatarAsyncTask(int position, int forBiz,UploadCircleAvatarListener uploadCircleAvatarlistener) {
        this.uploadCircleAvatarlistener = uploadCircleAvatarlistener;
        this.position = position;
        this.forBiz = forBiz;
    }

    @Override
    protected UploadNetWorkResult doInBackground(File... params) {
        File file = params[0];
        if (file == null) {
            return null;
        }
        String data = FileImageUpload.upLoadImage(forBiz, file, BuildConfig.SOCIAL_SERVER_PATH + "10/upload");
        UploadNetWorkResult jsonData = null;
        if (data != null)
            jsonData = Constant.GSON.fromJson(data, UploadNetWorkResult.class);
        return jsonData;
    }

    @Override
    protected void onPostExecute(UploadNetWorkResult uploadNetWorkResult) {
        super.onPostExecute(uploadNetWorkResult);
        String returnCode = uploadNetWorkResult.getReturnCode();
        String returnMsg = uploadNetWorkResult.getReturnMsg();
        if (uploadCircleAvatarlistener != null) {
            String picUrl = null;
            if (uploadNetWorkResult != null && NetWorkResult.SUCCESS.equals(uploadNetWorkResult.getReturnCode())) {
                picUrl = uploadNetWorkResult.getUrl();
            }
            uploadCircleAvatarlistener.onUploadCircleAvatarAsyncResult(position, forBiz, picUrl, returnCode, returnMsg);
        }
    }

    public interface UploadCircleAvatarListener {
        public void onUploadCircleAvatarAsyncResult(int position, int forBiz, String picUrl, String returnCode, String returnMsg);
    }
}
