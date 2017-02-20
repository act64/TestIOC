package com.jkyssocial.common.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.helper.StringUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.mintcode.App;
import com.jkys.jkysim.database.KeyValueDBService;
import com.mintcode.util.Const;
import com.mintcode.util.Keys;

import cn.dreamplus.wentang.BuildConfig;

/**
 * * * 实现文件上传的工具类
 * 
 * @Title:
 * @Description: 实现TODO
 * @Copyright:Copyright (c) 2011
 * @Company:易程科技股份有限公司
 * @Date:2012-7-2
 * @author longgangbai
 * @version 1.0
 */
public class FileImageUpload {

	private static final int REQUEST_TIMEOUT = 3 * 60 * 1000;// 设置请求超时3分钟

	private static final int SO_TIMEOUT = 3 * 60 * 1000; // 设置等待数据超时时间3分钟

	private static final String TAG = "uploadFile";

	private static final int TIME_OUT = 10 * 10000000; // 超时时间

	private static final String CHARSET = "utf-8"; // 设置编码

	public static final String SUCCESS = "1";
	public static final String FAILURE = "0";

	private static String appAgent;

	/**
	 * * android上传文件到服务器
	 *
	 * @param file
	 *            需要上传的文件
	 * @param RequestURL
	 *            请求的rul
	 * @return 返回响应的内容
	 */
	public static String uploadFile(File file, String RequestURL) {
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET);
			// 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
			if (file != null) {
				/** * 当文件不为空，把文件包装并且上传 */
				OutputStream outputSteam = conn.getOutputStream();
				DataOutputStream dos = new DataOutputStream(outputSteam);
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */
				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int res = conn.getResponseCode();
				Object o = conn.getContent();
				String res1 = (String) conn.getContent();
				Log.e(TAG, "response code:" + res);
				if (res == 200) {
					return res1;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return FAILURE;
	}

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        try {
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                // Calculate the largest inSampleSize value that is a power of 2
                // and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }
        } catch (Exception e) {
            Log.w("", e);
        }
        return inSampleSize;
    }

    public static Bitmap imageZip(String filePath, ByteArrayOutputStream baos) {

//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
////		options.inJustDecodeBounds = false;
//		options.inPreferredConfig = Bitmap.Config.RGB_565;
////		options.inDither = true;
//
//
//		options.inSampleSize = calculateInSampleSize(options, 1024, 1920);
		try {
			ExifInterface exif=new ExifInterface(filePath);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);

			options.inSampleSize = calculateInSampleSize(options, 1024,
					1024);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			Bitmap bm = BitmapFactory.decodeFile(filePath, options);
			if (bm == null) {
				return null;
			}
			switch(orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					bm = rotateImage(bm, 90);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					bm = rotateImage(bm, 180);
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					bm = rotateImage(bm, 270);
					break;
				// etc.
			}
			bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
			return bm;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }

	public static Bitmap rotateImage(Bitmap source, float angle) {
		Bitmap retVal;

		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

		return retVal;
	}

	public static String upLoadImage(int forBiz, File file, String RequestURL) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageZip(file.getPath(), baos);
		return upLoadImage(baos.toByteArray(), RequestURL, file.getName(), forBiz,0);
	}

	public static String upLoadImage_2(int forBiz, File file, String RequestURL,int timeOut) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		imageZip(file.getPath(), baos);
		return upLoadImage(baos.toByteArray(), RequestURL, file.getName(), forBiz,timeOut);
	}
	
	private static String upLoadImage(byte[] bytes, String RequestURL, String fileName, int forBiz,int timeOut){
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		if (timeOut==0) {
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		}else{
			HttpConnectionParams.setSoTimeout(httpParams, timeOut);
		}
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost httppost = new HttpPost(RequestURL);
//		HttpEntity httpEntity = MultipartEntityBuilder.create()
//				.addBinaryBody("file", b, ContentType.create("image/jpeg"), fileName).build();
        String token = KeyValueDBService.getInstance().findValue(Keys.NEW_TOKEN);
        if(TextUtils.isEmpty(token))
            return null;
        List<BasicNameValuePair> params = Arrays.asList(
                new BasicNameValuePair("chr", Const.CHR),
                new BasicNameValuePair("token", token),
				new BasicNameValuePair("forBiz", String.valueOf(forBiz)),
                new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));
        try {
            HttpEntity httpEntity = makeMultipartEntity(params, fileName, bytes);
			if(appAgent == null){
				appAgent = android.os.Build.MODEL + "/Android" + android.os.Build.VERSION.RELEASE + "; " + Const.CHR + "/" + BuildConfig.VERSION_CODE;
			}
            httppost.setHeader("Accept", "application/json; charset=UTF-8");
            httppost.setHeader("appAgent", appAgent);
            httppost.setEntity(httpEntity);

            HttpResponse response;
			response = httpclient.execute(httppost);
			String jsonData = EntityUtils.toString(response.getEntity());
            return jsonData;
		} catch (ConnectTimeoutException | SocketTimeoutException e) {
			Log.e("FileImageUpload", "network timeout", e);
		} catch (Exception e) {
			Log.e("FileImageUpload", "unknown exception", e);
		}
		return null;
	}

    public static HttpEntity makeMultipartEntity(List<BasicNameValuePair> params, final String filename, final byte[] b) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);  //如果有SocketTimeoutException等情况，可修改这个枚举
        //builder.setCharset(Charset.forName("UTF-8")); //不要用这个，会导致服务端接收不到参数
        if (params != null && params.size() > 0) {
            for (BasicNameValuePair p : params) {
                builder.addTextBody(p.getName(), p.getValue());
            }
        }
        builder.addBinaryBody("file", b, ContentType.create("image/jpeg"), filename);
        return builder.build();
    }

}
