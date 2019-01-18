package com.twy.network.business;


import android.support.v7.app.AppCompatActivity;

import com.twy.network.interfaces.Converter;
import com.twy.network.interfaces.HttpService;
import com.twy.network.interfaces.OnRecvDataListener;


/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/8.
 * PS: Not easy to write code, please indicate.
 */
public final class Net {

    private static Net net;

    public static Net getInstance(){
        return net;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    private String baseUrl;

    private HttpService httpService;

    public HttpService getHttpService() {
        return httpService;
    }

    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    public Converter.Factory getConverFactory() {
        return converFactory;
    }

    private Converter.Factory converFactory;
    Net(String baseUrl,Converter.Factory converFactory){
        this.baseUrl = baseUrl;
        this.converFactory = converFactory;
    }
    public final static class Builder{
        private String baseUrl;
        Converter.Factory converFactory;

        public Builder setHttpService(HttpService service) {
            this.service = service;
            return this;
        }

        private HttpService service;
        public  Builder setConverterFactory(Converter.Factory converFactory){
            this.converFactory = converFactory;
            return this;
        }
        public Builder baseUrl(String baseUrl){
            this.baseUrl = baseUrl;
            return this;
        }

        public Net build(){
            if(baseUrl == null){
                throw new IllegalStateException("Base URL required.");
            }
            net = new Net(baseUrl,converFactory);
            return net;
        }
    }

    public <T> T create(final Class<T> service){
        return  (T)MyProxyView.newInstance(new Class[]{service});
    }

    public static void startRequestData(AppCompatActivity activity, Observable observable, OnRecvDataListener listener){
        RequestManagerFragment fragment = (RequestManagerFragment) activity.getSupportFragmentManager().findFragmentByTag("myfragment");
        if(fragment==null){
            fragment = new RequestManagerFragment();
            activity.getSupportFragmentManager().beginTransaction().add(fragment, "myfragment").commit();
        }
        fragment.startRequestData(observable,listener);
    }
}
