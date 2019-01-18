package com.twy.network.business;

import android.os.SystemClock;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/8.
 * PS: Not easy to write code, please indicate.
 */
public class HttpTask implements Runnable{
    protected RequestHodler requestHodler;

    public HttpTask(RequestHodler requestHodler){
        this.requestHodler = requestHodler;
    }
    @Override
    public void run() {
        requestHodler.getHttpService().excute();
    }
}
