package com.twy.network.Exception;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/9.
 * PS: Not easy to write code, please indicate.
 */
public class HttpException extends RuntimeException {
    private int code;
    private String errorMsg;

    public HttpException (int code,String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
