package com.twy.network.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/7/3.
 * PS: Not easy to write code, please indicate.
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Body {
}
