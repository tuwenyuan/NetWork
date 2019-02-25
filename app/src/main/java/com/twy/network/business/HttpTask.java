package com.twy.network.business;

import android.os.SystemClock;

import com.twy.network.interfaces.HttpService;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/8.
 * PS: Not easy to write code, please indicate.
 */
public class HttpTask implements Runnable{
    protected RequestHodler requestHodler;
    private HttpService service = Net.getInstance().getHttpService()==null?new DefaultHttpService():Net.getInstance().getHttpService();

    public HttpTask(RequestHodler requestHodler){
        this.requestHodler = requestHodler;
    }
    @Override
    public void run() {
        service.excute(requestHodler);
    }
}
