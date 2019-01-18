package com.twy.network.business;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.twy.network.Exception.HttpException;
import com.twy.network.interfaces.DataListener;
import com.twy.network.interfaces.HttpService;
import com.twy.network.model.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

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
            builder.append("?").append(params);
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
