package com.twy.network.business;

import android.support.annotation.NonNull;

import com.twy.network.interfaces.DataListener;
import com.twy.network.interfaces.HttpService;
import com.twy.network.model.RequestInfo;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/8.
 * PS: Not easy to write code, please indicate.
 */
public class RequestHodler implements Comparable<RequestHodler> {
    private RequestInfo requestInfo;
    private DataListener listener;
    private String fragmentToString;

    public String getFragment() {
        return fragmentToString;
    }

    public void setFragment(String fragmentToString) {
        this.fragmentToString = fragmentToString;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public DataListener getListener() {
        return listener;
    }

    public void setListener(DataListener listener) {
        this.listener = listener;
    }

    /**
     * 请求地址一样 及 请求参数一样 则认为是同一个请求
     * @param requestHodler
     * @return 0 请求地址和请求参数一致 否则不一致
     */
    @Override
    public int compareTo(@NonNull RequestHodler requestHodler) {
        if(requestInfo.getUrl().equals(requestHodler.requestInfo.getUrl())){
            if(requestInfo.getParams().size() == requestHodler.requestInfo.getParams().size()){
                for (String key : requestInfo.getParams().keySet()) {
                    if(!requestInfo.getParams().get(key).equals(requestHodler.requestInfo.getParams().get(key))){
                        return -1;
                    }
                }
                return 0;
            }
        }
        return -1;
    }
}
