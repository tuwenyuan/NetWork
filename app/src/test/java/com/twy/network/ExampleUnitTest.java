package com.twy.network;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void test(){
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(15, TimeUnit.SECONDS);
        clientBuilder.readTimeout(20, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(20, TimeUnit.SECONDS);

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url("http://www.baidu.com");
        Headers.Builder headersBuilder = new Headers.Builder();
        headersBuilder.add("aaa","bbb");
        requestBuilder.headers(headersBuilder.build());

        Request request = requestBuilder.build();

        OkHttpClient client = clientBuilder.build();

        Call call = client.newCall(request);

        try {
            Response response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}