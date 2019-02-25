package com.twy.network.interfaces;

import android.util.Log;

import com.google.gson.Gson;
import com.twy.network.business.Net;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/9.
 * PS: Not easy to write code, please indicate.
 */
public abstract class DataListener<M> {
    private Type type;
    public void setType(Type type) {
        this.type = type;
    }
    public void converter(String result) {
        //Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Converter<String, ?> stringConverter = Net.getInstance().getConverFactory().responseBodyConverter(type);
        try {
            onRecvData((M)stringConverter.convert(result));
            onComplate();
        } catch (IOException e) {
            e.printStackTrace();
            onError(e);
            onComplate();
        }
    }

    public void onStart(){

    }
    public void onComplate(){

    }
    public abstract void onRecvData(M data);
    public abstract void onError(Exception e);
}
