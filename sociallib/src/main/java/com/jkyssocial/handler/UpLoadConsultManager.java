package com.jkyssocial.handler;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.jkys.jkysbase.Constant;
import com.jkys.jkysbase.data.NetWorkResult;
import com.jkys.jkysbase.data.UploadNetWorkResult;
import com.jkyshealth.manager.MedicalApi;
import com.jkyshealth.manager.MedicalApiManager;
import com.jkyshealth.manager.MedicalVolleyListener;
import com.jkyssocial.activity.ConsultActivity;
import com.jkyssocial.common.util.FileImageUpload;
import com.jkyssocial.data.ConsumeResult;
import com.jkyssocial.data.SubmitAskResult;
import com.jkyssocial.data.SumbitQuestionBean;
import com.jkyssocial.data.SumitImageBean;
import com.mintcode.base.BaseActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.dreamplus.wentang.BuildConfig;

/**
 * 上传咨询医生的图片和问题
 * Created by xiaoke on 16/11/25.
 */

public class UpLoadConsultManager extends Thread {
    private SumbitQuestionBean sumbitQuestionBean;
    private ConsultActivity.MedicalAskDoctorImpl medicalAskDoctorImpl;
    /**
     * 这里的path指的是服务器传回的地址
     */
    private List<String> picUpList;
    public UpLoadConsultManager(SumbitQuestionBean sumbitQuestionBean, ConsultActivity.MedicalAskDoctorImpl medicalAskDoctorImpl) {
        this.sumbitQuestionBean = sumbitQuestionBean;
        picUpList = new ArrayList<String>();
        this.medicalAskDoctorImpl = medicalAskDoctorImpl;
    }

    @Override
    public void run() {
        super.run();
        StringBuilder builder = new StringBuilder();
        if (sumbitQuestionBean.getImages()!=null){
            for (int i=0;i<sumbitQuestionBean.getImages().size()-1;i++){
                String filePath = sumbitQuestionBean.getImages().get(i);
                if (i!=0){
                    builder.append(";");// url1;url2;url3
                }
                if (TextUtils.isEmpty(filePath))continue;
                File file = new File(filePath);
                if (file ==null)continue;
                String data = FileImageUpload.upLoadImage_2(1, file,
                        MedicalApi.COMMIT_IMAGE,1 * 60*1000);
                SumitImageBean jsonData;
                if (data != null) {
                    jsonData = Constant.GSON.fromJson(data, SumitImageBean.class);
                    if (jsonData==null)continue;
                    SumitImageBean.HeaderBean headerBean = jsonData.getHeader();
                    SumitImageBean.BodyBean bodyBean = jsonData.getBody();
                    if (headerBean != null && bodyBean!=null && headerBean.getCode()==2000) {
                        builder.append(bodyBean.getFile_path());
                    }
                }
            }

        }
        sumbitQuestionBean.setUpload_files(builder.toString());
        MedicalApiManager.getInstance().submitCircleFriendQuetion(medicalAskDoctorImpl, sumbitQuestionBean);

    }


}
