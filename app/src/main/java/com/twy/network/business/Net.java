package com.twy.network.business;


import android.support.v7.app.AppCompatActivity;

import com.twy.network.interfaces.Converter;
import com.twy.network.interfaces.HttpService;
import com.twy.network.interfaces.OnRecvDataListener;

import java.util.HashMap;
import java.util.Map;


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

    public Converter.Factory getConverFactory() {
        return converFactory;
    }

    private Converter.Factory converFactory;
    Net(String baseUrl,Converter.Factory converFactory,HttpService service){
        this.baseUrl = baseUrl;
        this.converFactory = converFactory;
        this.httpService = service;
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
            net = new Net(baseUrl,converFactory,service);
            return net;
        }
    }

    public <T> T create(final Class<T> service){
        return  (T)MyProxyView.newInstance(new Class[]{service});
    }
    private static Map<AppCompatActivity,RequestManagerFragment> map = new HashMap<>();
    public static void startRequestData(final AppCompatActivity activity, Observable observable, OnRecvDataListener listener){
        if(map.containsKey(activity)){
            map.get(activity).startRequestData(observable,listener, new IUnsubscribe() {
                @Override
                public void unsubscribe() {
                    map.remove(activity);
                }
            });
        }else {
            RequestManagerFragment fragment = (RequestManagerFragment) activity.getSupportFragmentManager().findFragmentByTag("myfragment");
            if (fragment == null) {
                fragment = new RequestManagerFragment();
                map.put(activity,fragment);
                activity.getSupportFragmentManager().beginTransaction().add(fragment, "myfragment").commit();
            }
            fragment.startRequestData(observable, listener, new IUnsubscribe() {
                @Override
                public void unsubscribe() {
                    map.remove(activity);
                }
            });
        }
    }
    private static StartRequestData requestData;
    public static void startRequestData( Observable observable, OnRecvDataListener listener){
        if(requestData ==null){
            requestData = new StartRequestData();
        }
        requestData.startRequestNetData(observable,listener);
    }

    public interface IUnsubscribe{
        void unsubscribe();
    }

}
