package com.twy.network;

import com.google.gson.Gson;
import com.twy.network.interfaces.Converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/11.
 * PS: Not easy to write code, please indicate.
 */
public class ResponseConvertFactory extends Converter.Factory {
    private Gson gson;
    public static ResponseConvertFactory create(){
        return create(new Gson());
    }

    public static ResponseConvertFactory create(Gson gson) {
        return new ResponseConvertFactory(gson);
    }

    ResponseConvertFactory(Gson gson){
        if(gson==null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }


    @Override
    public Converter<String, ?> responseBodyConverter(Type type) {
        return new GsonResponseBodyConverter<>(gson, type);
    }
}
