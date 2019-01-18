package com.twy.network.interfaces;

import android.os.Handler;
import android.os.Looper;

import com.twy.network.model.HttpMethod;
import com.twy.network.model.RequestInfo;

import java.util.Map;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/9.
 * PS: Not easy to write code, please indicate.
 */
public abstract class HttpService implements IHttpService {
    protected RequestInfo requestInfo;
    protected DataListener listener;

    /**
     * 注：这里是在子线程里面执行
     */
    @Override
    public void excute() {
        listener.onStart();
        Map<String,String> headers = requestInfo.getHeads().size()>0?requestInfo.getHeads():null;
        if (requestInfo.getMethod().equals(HttpMethod.GET)) {
            excuteGetRequest(headers,createParams(),listener);
        } else {
            excutePostRequest(headers,createParams(),listener);
        }
    }

    @Override
    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    @Override
    public void setListener(DataListener listener) {
        this.listener = listener;
    }

    public DataListener getListener() {
        return listener;
    }

    /**
     * 执行get请求
     * @param headers 请求头信息
     * @param params 请求参数
     * @param listener 请求成功或者失败回调
     */
    public abstract void excuteGetRequest(Map<String,String> headers, String params, DataListener listener);

    /**
     * 执行post请求
     * @param headers 请求头信息
     * @param params 请求参数
     * @param listener 请求成功或者失败回调
     */
    public abstract void excutePostRequest(Map<String,String> headers,String params,DataListener listener);

    public abstract void cancelRequest();

    /**
     * key=value&key1=value1
     * @return
     */
    private String createParams() {
        if (requestInfo.getParams() == null || requestInfo.getParams().size() == 0) {
            return "";
        }
        StringBuilder paramsBuilder = new StringBuilder();
        for (String key : requestInfo.getParams().keySet()) {
            paramsBuilder.append(key).append("=").append(requestInfo.getParams().get(key)).append("&");
        }
        paramsBuilder.deleteCharAt(paramsBuilder.length() - 1);
        return paramsBuilder.toString();
    }

}
