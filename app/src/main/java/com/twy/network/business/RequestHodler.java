package com.twy.network.business;

import android.support.annotation.NonNull;

import com.twy.network.interfaces.HttpService;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/8.
 * PS: Not easy to write code, please indicate.
 */
public class RequestHodler implements Comparable<RequestHodler> {
    /**
     * 执行下载类
     */
    private HttpService httpService;
    public HttpService getHttpService() {
        return httpService;
    }

    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    /**
     * 请求地址一样 及 请求参数一样 则认为是同一个请求
     * @param requestHodler
     * @return 0 请求地址和请求参数一致 否则不一致
     */
    @Override
    public int compareTo(@NonNull RequestHodler requestHodler) {
        if(httpService.getRequestInfo().getUrl().equals(requestHodler.getHttpService().getRequestInfo().getUrl())){
            if(httpService.getRequestInfo().getParams().size() == requestHodler.getHttpService().getRequestInfo().getParams().size()){
                for (String key : httpService.getRequestInfo().getParams().keySet()) {
                    if(!httpService.getRequestInfo().getParams().get(key).equals(requestHodler.getHttpService().getRequestInfo().getParams().get(key))){
                        return -1;
                    }
                }
                return 0;
            }
        }
        return -1;
    }
}
