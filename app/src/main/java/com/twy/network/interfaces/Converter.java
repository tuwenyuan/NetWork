package com.twy.network.interfaces;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/11.
 * PS: Not easy to write code, please indicate.
 */
public interface Converter<F,T> {
    T convert(F value) throws Exception;

    abstract class Factory {
        public Converter<String, ?> responseBodyConverter(Type type) {
            return null;
        }

        public Converter<?, String> stringConverter(Type type) {
            return null;
        }
    }
}
