package com.twy.network;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by twy on 2018/2/2.
 */

public class AddCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        /*if (chain == null)
            Log.d("http", "Addchain == null");*/
        Request.Builder builder = chain.request().newBuilder();
        String token = "sss";//CacheUtils.getString(WeiniApplication.instance, Constants.ACCESSTOKEN);

        if(token!=null){
            builder.header("Content-type","application/x-www-form-urlencoded; charset=UTF-8");
            builder.header("cookie", "_JSID=" + (token == null ? "" : token));
        }
        //builder.header("Ljf369", token == null ? "" : token);
        return chain.proceed(builder.build());
    }
}
