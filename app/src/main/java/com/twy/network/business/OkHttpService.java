package com.twy.network.business;

import android.os.SystemClock;
import android.text.TextUtils;

import com.twy.network.interfaces.DataListener;
import com.twy.network.interfaces.HttpService;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    Map<String,List<Call>> kvs = new HashMap<>();
    OkHttpClient.Builder client = new OkHttpClient.Builder();
    {
        //设置超时
        client.connectTimeout(15, TimeUnit.SECONDS);
        client.readTimeout(20, TimeUnit.SECONDS);
        client.writeTimeout(20, TimeUnit.SECONDS);
    }
    @Override
    public void excuteGetRequest(Map<String, String> map, String s, DataListener listener) {
        Request.Builder builder = new Request.Builder()
                .url(requestInfo.getUrl().contains("?")?requestInfo.getUrl()+"&":requestInfo.getUrl()+"?"+s);
        if(map!=null && map.size()>0){
            Headers.Builder builder1 = new Headers.Builder();
            for(String s1 : map.keySet()){
                builder1.add(s1,map.get(s1));
            }
            builder.headers(builder1.build());
        }
        Request request = builder.build();
        /*Request request = new Request.Builder()
                .url(requestInfo.getUrl()+"?"+params)
                .headers(okhttp3.Headers.of(headers))
                .build();*/
        Call call = client.build().newCall(request);
        if(fragmentToString!=null){
            if(kvs.get(fragmentToString)==null){
                List<Call> list = new ArrayList<>();
                list.add(call);
                kvs.put(fragmentToString,list);
            }else {
                kvs.get(fragmentToString).add(call);
            }
        }
        try {
            Response response = call.execute();
            final String result = response.body().string();
            cacel(call);
            listener.converter(result);
        } catch (final IOException e) {
            e.printStackTrace();
            cacel(call);
            listener.onError(e);
            listener.onComplate();
        }
    }

    @Override
    public void excutePostRequest(Map<String, String> map, String s, DataListener listener) {
        FormBody.Builder fb = new FormBody.Builder();
        if(!TextUtils.isEmpty(s)) {
            for (String str : s.split("&")) {
                fb.add(str.split("=")[0], str.split("=")[1]);
            }
        }
        Request.Builder builder = new Request.Builder()
                .url(requestInfo.getUrl())
                .post(fb.build());
        if(map!=null && map.size()>0){
            Headers.Builder builder1 = new Headers.Builder();
            for(String s1 : map.keySet()){
                builder1.add(s1,map.get(s1));
            }
            builder.headers(builder1.build());
        }
        Request request = builder.build();
        Call call = client.build().newCall(request);
        if(fragmentToString!=null){
            if(kvs.get(fragmentToString)==null){
                List<Call> list = new ArrayList<>();
                list.add(call);
                kvs.put(fragmentToString,list);
            }else {
                kvs.get(fragmentToString).add(call);
            }
        }
        try {
            Response response=call.execute();
            final String result = URLDecoder.decode(response.body().string(),"UTF-8");
            cacel(call);
            listener.converter(result);
            //这里没有用call.enqueue方式连接，用了execute方式，都一样的，写法不一样而已，看个人喜欢
        } catch (final IOException e) {
            e.printStackTrace();//响应失败了，进行响应操作
            cacel(call);
            listener.onError(e);
            listener.onComplate();
        }
    }

    @Override
    public void excuteUploadFileRequest(Map<String, String> map, String s, File file, DataListener dataListener) {

    }

    @Override
    public void cancelRequest() {
        if(kvs.get(fragmentToString)!=null){
            for(Call call : kvs.get(fragmentToString)){
                call.cancel();
                kvs.get(fragmentToString).remove(call);
            }
            kvs.remove(fragmentToString);
        }
    }

    private void cacel(Call call){
        if(fragmentToString!=null && kvs.get(fragmentToString)!=null){
            if(kvs.get(fragmentToString).size()==1){
                kvs.remove(fragmentToString);
            }else{
                kvs.get(fragmentToString).remove(call);
            }
        }
    }
}
