package com.twy.network.business;

import android.os.SystemClock;
import android.text.TextUtils;

import com.twy.network.interfaces.DataListener;
import com.twy.network.interfaces.HttpService;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;


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

        Response response = null;
        try {
            response=call.execute();
            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            MediaType contentType = responseBody.contentType();
            Charset charset = Charset.forName("UTF-8");
            if (contentType != null) {
                charset = contentType.charset( Charset.forName("UTF-8"));
            }
            if (responseBody.contentLength() != 0) {
                String result  = buffer.readString(charset);
                if(TextUtils.isEmpty(result)){
                    listener.onError(new Exception("没有响应数据"));
                    listener.onComplate();
                }else{
                    listener.converter(result);
                }
            }
            cacel(call);
        } catch (final IOException e) {
            e.printStackTrace();
            cacel(call);
            listener.onError(e);
            listener.onComplate();
        }finally {
            try {
                response.body().source().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void excutePostRequest(Map<String, String> map, String s, DataListener listener,String bodyStr) {
        Request.Builder builder = new Request.Builder();
        if(bodyStr==null){
            FormBody.Builder fb = new FormBody.Builder();
            if(!TextUtils.isEmpty(s)) {
                for (String str : s.split("&")) {
                    fb.add(str.split("=")[0], str.split("=")[1]);
                }
            }
            builder.url(requestInfo.getUrl())
                    .post(fb.build());
        }else {
            if(TextUtils.isEmpty(s)){
                builder.url(requestInfo.getUrl())
                        .post(RequestBody.create(MediaType.parse("application/json"),bodyStr));
            }else {
                builder.url(requestInfo.getUrl().contains("?")?requestInfo.getUrl()+"&"+s:requestInfo.getUrl()+"?"+s)
                        .post(RequestBody.create(MediaType.parse("application/json"),bodyStr));
            }
        }

        /*Request.Builder builder = new Request.Builder()
                .url(requestInfo.getUrl())
                .post(fb.build());*/
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

        Response response = null;
        try {
            response=call.execute();
            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            MediaType contentType = responseBody.contentType();
            Charset charset = Charset.forName("UTF-8");
            if (contentType != null) {
                charset = contentType.charset( Charset.forName("UTF-8"));
            }
            if (responseBody.contentLength() != 0) {
                String result  = buffer.readString(charset);
                if(TextUtils.isEmpty(result)){
                    listener.onError(new Exception("没有响应数据"));
                    listener.onComplate();
                }else{
                    result = URLDecoder.decode(result,charset.name());
                    listener.converter(result);
                }
            }
            cacel(call);
        } catch (final IOException e) {
            e.printStackTrace();
            cacel(call);
            listener.onError(e);
            listener.onComplate();
        }finally {
            try {
                response.body().source().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void excutePutRequest(Map<String, String> headers, String params, DataListener listener, String bodyStr) {
        Request.Builder builder = new Request.Builder();
        if(bodyStr==null){
            FormBody.Builder fb = new FormBody.Builder();
            if(!TextUtils.isEmpty(params)) {
                for (String str : params.split("&")) {
                    fb.add(str.split("=")[0], str.split("=")[1]);
                }
            }
            builder.url(requestInfo.getUrl())
                    .put(fb.build());
        }else {
            if(TextUtils.isEmpty(params)){
                builder.url(requestInfo.getUrl())
                        .put(RequestBody.create(MediaType.parse("application/json"),bodyStr));
            }else {
                builder.url(requestInfo.getUrl().contains("?")?requestInfo.getUrl()+"&"+params:requestInfo.getUrl()+"?"+params)
                        .put(RequestBody.create(MediaType.parse("application/json"),bodyStr));
            }
        }

        /*Request.Builder builder = new Request.Builder()
                .url(requestInfo.getUrl())
                .post(fb.build());*/
        if(headers!=null && headers.size()>0){
            Headers.Builder builder1 = new Headers.Builder();
            for(String s1 : headers.keySet()){
                builder1.add(s1,headers.get(s1));
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

        Response response = null;
        try {
            response=call.execute();
            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            MediaType contentType = responseBody.contentType();
            Charset charset = Charset.forName("UTF-8");
            if (contentType != null) {
                charset = contentType.charset( Charset.forName("UTF-8"));
            }
            if (responseBody.contentLength() != 0) {
                String result  = buffer.readString(charset);
                if(TextUtils.isEmpty(result)){
                    listener.onError(new Exception("没有响应数据"));
                    listener.onComplate();
                }else{
                    result = URLDecoder.decode(result,charset.name());
                    listener.converter(result);
                }
            }
            cacel(call);
        } catch (final IOException e) {
            e.printStackTrace();
            cacel(call);
            listener.onError(e);
            listener.onComplate();
        }finally {
            try {
                response.body().source().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
