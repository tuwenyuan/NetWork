package com.twy.network.business;

import android.os.SystemClock;

import com.twy.network.interfaces.DataListener;
import com.twy.network.interfaces.HttpService;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/14.
 * PS: Not easy to write code, please indicate.
 */
public class OkHttpService  extends HttpService {
    OkHttpClient.Builder client = new OkHttpClient.Builder();
    {
        //设置超时
        client.connectTimeout(15, TimeUnit.SECONDS);
        client.readTimeout(20, TimeUnit.SECONDS);
        client.writeTimeout(20, TimeUnit.SECONDS);
    }
    Call call = null;
    @Override
    public void excuteGetRequest(Map<String,String> headers, String params, final DataListener listener) {
        Request.Builder builder = new Request.Builder()
                .url(requestInfo.getUrl()+"?"+params);
        if(headers!=null && headers.size()>0){
            builder.headers(Headers.of(headers));
        }
        Request request = builder.build();
        /*Request request = new Request.Builder()
                .url(requestInfo.getUrl()+"?"+params)
                .headers(okhttp3.Headers.of(headers))
                .build();*/
        call = client.build().newCall(request);
        try {
            Response response = call.execute();
            final String result = response.body().string();
            listener.converter(result);
        } catch (final IOException e) {
            e.printStackTrace();
            listener.onError(e);
        }
    }

    @Override
    public void excutePostRequest(Map<String,String> headers, String params, final DataListener listener) {
        FormBody.Builder fb = new FormBody.Builder();
        for (String str : params.split("&")) {
            fb.add(str.split("=")[0],str.split("=")[1]);
        }
        Request.Builder builder = new Request.Builder()
                .url(requestInfo.getUrl())
                .post(fb.build());
        if(headers!=null && headers.size()>0){
            builder.headers(okhttp3.Headers.of(headers));
        }
        Request request = builder.build();
        call = client.build().newCall(request);
        try {
            Response response=call.execute();
            final String result = URLDecoder.decode(response.body().string(),"UTF-8");
            listener.converter(result);
            //这里没有用call.enqueue方式连接，用了execute方式，都一样的，写法不一样而已，看个人喜欢
        } catch (final IOException e) {
            e.printStackTrace();//响应失败了，进行响应操作
            listener.onError(e);
        }
    }

    @Override
    public void cancelRequest() {
        if(call!=null)
            call.cancel();
    }
}
