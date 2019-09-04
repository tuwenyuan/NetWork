package com.twy.network;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by twy on 2018/1/29.
 */

public class QueryParameterInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request request;
        String method = originalRequest.method();
        Headers headers = originalRequest.headers();

        HttpUrl.Builder newBuilder = originalRequest.url().newBuilder();
        newBuilder.addQueryParameter("commonClientType","2");
        //newBuilder.addQueryParameter("versionCode", StringUtil.getPackageVersionCode()+"");
        HttpUrl modifiedUrl = newBuilder.build();

        request = originalRequest.newBuilder().url(modifiedUrl).build();
        return chain.proceed(request);
    }
}
