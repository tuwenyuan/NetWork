package com.twy.network;

import com.google.gson.Gson;
import com.twy.network.interfaces.Converter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/11.
 * PS: Not easy to write code, please indicate.
 */
public class GsonResponseBodyConverter<T> implements Converter<String ,T> {

    private final Gson gson;
    private final Type type;

    GsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(String value) throws Exception {
        if(((Class) type).getName().equals("java.lang.String")) {
            return (T) value;
        }else{
            return gson.fromJson(value, type);
        }
    }
}
