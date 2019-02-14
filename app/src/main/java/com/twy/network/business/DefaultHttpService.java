package com.twy.network.business;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.twy.network.Exception.HttpException;
import com.twy.network.interfaces.DataListener;
import com.twy.network.interfaces.HttpService;
import com.twy.network.model.HttpMethod;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/8.
 * PS: Not easy to write code, please indicate.
 */
public class DefaultHttpService extends HttpService {

    HttpURLConnection urlConn = null;

    @Override
    public void excuteGetRequest(Map<String, String> headers, String params, final DataListener listener) {
        try{
            urlConn = createGetRequest(params);
            //添加请求头
            if(headers!=null) {
                for (String key : headers.keySet()) {
                    urlConn.setRequestProperty(key, requestInfo.getHeads().get(key));
                }
            }
            int responseCode = urlConn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                urlConn.disconnect();
                throw new HttpException(responseCode,urlConn.getResponseMessage());
            }

            BufferedReader bis = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "utf-8"));

            StringBuilder sb = new StringBuilder();
            String lines;
            while ((lines = bis.readLine()) != null) {
                sb.append(lines);
            }
            final String finalLines = sb.toString();
            listener.converter(finalLines);
            urlConn.disconnect();
        }catch (final Exception e){
            listener.onError(e);
            listener.onComplate();
        }
    }

    @Override
    public void excutePostRequest(Map<String, String> headers, String params, final DataListener listener) {
        try{
            urlConn = createPostRequest(params);
            //添加请求头
            if(headers!=null) {
                for (String key : headers.keySet()) {
                    urlConn.setRequestProperty(key, requestInfo.getHeads().get(key));
                }
            }
            int responseCode = urlConn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                urlConn.disconnect();
                throw new HttpException(responseCode,urlConn.getResponseMessage());
            }

            BufferedReader bis = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "utf-8"));

            StringBuilder sb = new StringBuilder();
            String lines;
            while ((lines = bis.readLine()) != null) {
                sb.append(lines);
            }
            final String finalLines = sb.toString();
            listener.converter(finalLines);
            urlConn.disconnect();
        }catch (final Exception e){
            listener.onError(e);
            listener.onComplate();
        }
    }

    @Override
    public void excuteUploadFileRequest(Map<String, String> headers, String params, File file, DataListener listener) {
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        InputStream is = null;
        DataOutputStream dos = null;
        try {
            URL url = new URL(requestInfo.getUrl().contains("?")?requestInfo.getUrl()+"&"+params:requestInfo.getUrl()+"?"+params);
            urlConn = (HttpURLConnection) url.openConnection();
            //添加请求头
            if(headers!=null) {
                for (String key : headers.keySet()) {
                    urlConn.setRequestProperty(key, requestInfo.getHeads().get(key));
                }
            }
            urlConn.setReadTimeout(100000000);
            urlConn.setConnectTimeout(100000000);
            urlConn.setDoInput(true); // 允许输入流
            urlConn.setDoOutput(true); // 允许输出流
            urlConn.setUseCaches(false); // 不允许使用缓存
            urlConn.setRequestMethod("POST"); // 请求方式
            urlConn.setRequestProperty("Charset", "utf-8"); // 设置编码
            urlConn.setRequestProperty("connection", "keep-alive");
            urlConn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+ BOUNDARY);
            urlConn.getOutputStream().write(params.getBytes("utf-8"));
            /**
             * 当文件不为空，把文件包装并且上传
             */
            OutputStream outputSteam = urlConn.getOutputStream();

            dos = new DataOutputStream(outputSteam);
            StringBuffer sb = new StringBuffer();
            sb.append("--"+BOUNDARY+"\r\n");//数据分割线
            /**
             * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */
            sb.append("Content-Disposition: form-data; name=\"data\"; filename=\""+ file.getName() + "\"\r\n");
            sb.append("Content-Type: application/octet-stream; charset="+ "utf-8\r\n");
            sb.append("\r\n");
            dos.write(sb.toString().getBytes());
            is = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = is.read(bytes)) != -1) {
                dos.write(bytes, 0, len);
            }

            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                    .getBytes();
            dos.write(end_data);
            dos.flush();
            /**
             * 获取响应码 200=成功 当响应成功，获取响应的流
             */
            int res = urlConn.getResponseCode();
            if (res == 200) {
                //return SUCCESS;
                BufferedReader bis = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "utf-8"));

                StringBuilder sb1 = new StringBuilder();
                String lines;
                while ((lines = bis.readLine()) != null) {
                    sb1.append(lines);
                }
                final String finalLines = sb1.toString();
                listener.converter(finalLines);
                urlConn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void cancelRequest() {
        if(urlConn!=null)
            urlConn.disconnect();
    }

    private HttpURLConnection createPostRequest(String params) throws IOException {
        URL getUrl = new URL(requestInfo.getUrl());
        HttpURLConnection urlConn = (HttpURLConnection) getUrl.openConnection();
        urlConn.setDoOutput(true);
        urlConn.setConnectTimeout(10000);
        urlConn.setRequestMethod("POST");
        urlConn.getOutputStream().write(params.getBytes("utf-8"));
        return urlConn;
    }

    private HttpURLConnection createGetRequest(String params) throws IOException {
        StringBuilder builder = new StringBuilder(requestInfo.getUrl());
        if (params.length() > 0) {
            if(requestInfo.getUrl().contains("?")){
                builder.append("&").append(params);
            }else{
                builder.append("?").append(params);
            }
        }
        String url = builder.toString();
        URL getUrl = new URL(url);
        HttpURLConnection urlConn = (HttpURLConnection) getUrl.openConnection();
        urlConn.setDoInput(true);
        urlConn.setUseCaches(false);
        urlConn.setConnectTimeout(10000);
        urlConn.setRequestMethod("GET");
        urlConn.connect();
        return urlConn;
    }

}
