package com.twy.network;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.twy.network.business.Net;
import com.twy.network.business.Observable;
import com.twy.network.interfaces.ITestServices;
import com.twy.network.interfaces.OnRecvDataListener;
import com.twy.network.model.User;

public class MainActivity extends AppCompatActivity {


    private Observable<User> observable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_get = findViewById(R.id.btn_get);
        Button btn_post = findViewById(R.id.btn_post);
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserPost();
            }
        });
        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserGet();
            }
        });

        //.setHttpService(new OkHttpService())
        Net net = new Net.Builder()
                .setConverterFactory(new ResponseConvertFactory(new Gson()))
                .baseUrl("http://192.168.1.75:8080/test/")
                .build();
        services = net.create(ITestServices.class);
    }

    ITestServices services;
    private void getUserGet(){
        Net.startRequestData(this, services.getUserPost("涂文远", "123456"), new OnRecvDataListener<User>() {
            @Override
            public void onStart() {
                Log.i("twy",Thread.currentThread().getName()+"***onStart");
            }

            @Override
            public void onComplate() {
                Log.i("twy",Thread.currentThread().getName()+"***onComplate");
            }

            @Override
            public void onError(Exception e) {
                Log.i("twy",Thread.currentThread().getName()+"***onError"+e.getMessage());
            }

            @Override
            public void onRecvData(User data) {
                Log.i("twy",Thread.currentThread().getName()+"***onRecvData"+"***"+data.userName+"::::"+data.password+"");
            }
        });
    }

    private void getUserPost(){
        Net.startRequestData(this, services.getUserPost("涂文远", "123456"), new OnRecvDataListener<User>() {
            @Override
            public void onStart() {
                Log.i("twy",Thread.currentThread().getName()+"***onStart");
            }

            @Override
            public void onComplate() {
                Log.i("twy",Thread.currentThread().getName()+"***onComplate");
            }

            @Override
            public void onError(Exception e) {
                Log.i("twy",Thread.currentThread().getName()+"***onError"+e.getMessage());
            }

            @Override
            public void onRecvData(User data) {
                Log.i("twy",Thread.currentThread().getName()+"***onRecvData"+"***"+data.userName+"::::"+data.password+"");
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
