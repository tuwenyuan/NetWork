package com.twy.network.business;

import android.os.SystemClock;
import android.text.TextUtils;

import com.twy.network.AddCookiesInterceptor;
import com.twy.network.BuildConfig;
import com.twy.network.LoggingInterceptor;
import com.twy.network.QueryParameterInterceptor;
import com.twy.network.interfaces.DataListener;
import com.twy.network.interfaces.HttpService;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
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

        //公共参数
        client.addInterceptor(new QueryParameterInterceptor());
        client.addInterceptor(new AddCookiesInterceptor());

        /**
         * Log信息拦截器
         */
        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? LoggingInterceptor.Level.BODY : LoggingInterceptor.Level.BASIC);
        client.addInterceptor(loggingInterceptor);

        client.sslSocketFactory(createSSLSocketFactory());
        client.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
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
                .url(requestInfo.getUrl());
        if(!TextUtils.isEmpty(s)) {
            StringBuilder strb = new StringBuilder("{");
            for (String str : s.split("&")) {
                strb.append("\""+str.split("=")[0]+"\":"+"\""+str.split("=")[1]+"\",");
            }
            strb.delete(strb.length()-1,strb.length());
            strb.append("}");
            builder.post(RequestBody.create(MediaType.parse("application/json"),strb.toString()));
        }*/


        if(map!=null && map.size()>0){
            Headers.Builder builder1 = new Headers.Builder();
            //builder1.add("Content-Type","application/json");
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
            if(response.code()==200){
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
            }else {
                listener.onError(new Exception("responseCode::"+response.code()));
                listener.onComplate();
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
    public void excuteDeleteRequest(Map<String, String> map, String s, DataListener listener, String bodyStr) {
        Request.Builder builder = new Request.Builder();
        if(bodyStr==null){
            /*FormBody.Builder fb = new FormBody.Builder();
            if(!TextUtils.isEmpty(s)) {
                for (String str : s.split("&")) {
                    fb.add(str.split("=")[0], str.split("=")[1]);
                }
            }
            builder.url(requestInfo.getUrl())
                    .delete(fb.build());*/
            builder.url(requestInfo.getUrl().contains("?")?requestInfo.getUrl()+"&"+s:requestInfo.getUrl()+"?"+s)
                    .delete();
        }else {
            if(TextUtils.isEmpty(s)){
                builder.url(requestInfo.getUrl())
                        .delete(RequestBody.create(MediaType.parse("application/json"),bodyStr));
            }else {
                builder.url(requestInfo.getUrl().contains("?")?requestInfo.getUrl()+"&"+s:requestInfo.getUrl()+"?"+s)
                        .delete(RequestBody.create(MediaType.parse("application/json"),bodyStr));
            }
        }
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
            if(response.code()==200){
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
            }else {
                listener.onError(new Exception("responseCode::"+response.code()));
                listener.onComplate();
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
    public void excuteUploadFileRequest(Map<String, String> map, String s, File file, DataListener listener) {
        //1.创建对应的MediaType 2.创建RequestBody
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"),file);
        //3.构建MultipartBody
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(s.split("=")[0], file.getName(), fileBody)
                .build();

        //4.构建请求
        Request request = new Request.Builder()
                .url(requestInfo.getUrl())
                .post(requestBody)
                .build();

        //5.发送请求
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
            if(response.code()==200){
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
            }else {
                listener.onError(new Exception("responseCode::"+response.code()));
                listener.onComplate();
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

    private static javax.net.ssl.SSLSocketFactory createSSLSocketFactory() {
        javax.net.ssl.SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

    public static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    }
}
