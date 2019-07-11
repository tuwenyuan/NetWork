package com.twy.network.business;

import android.util.Log;

import com.twy.network.Exception.HttpException;
import com.twy.network.interfaces.DataListener;
import com.twy.network.interfaces.GET;
import com.twy.network.interfaces.HttpService;
import com.twy.network.interfaces.OnRecvDataListener;
import com.twy.network.interfaces.POST;
import com.twy.network.interfaces.PUT;
import com.twy.network.interfaces.Query;
import com.twy.network.model.ErrorCode;
import com.twy.network.model.HttpMethod;
import com.twy.network.model.RequestInfo;
import com.twy.network.utils.GenericsUtils;


import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/11.
 * PS: Not easy to write code, please indicate.
 */
public class Observable<T> {
    public GET get;
    public POST post;
    public PUT put;
    public List<Object> paramNames = new ArrayList<>();
    public Object[] paramValues;
    public String[] headers;//[ache-Control: max-age=640000,ache-Control: max-age=640000]
    public Type type;
    public boolean isMultipart;
    /*private FutureTask futureTask = null;
    private HttpTask httpTask;*/

    /*public void startRequestData(final OnRecvDataListener dataListener){
        final RequestHodler requestHodler = new RequestHodler();
        DataListener listener = new DataListener() {
            @Override
            public void onStart() {
                dataListener.onStart();
            }

            @Override
            public void onComplate() {
                Net.getInstance().requestHodlers.remove(requestHodler);
                dataListener.onComplate();
            }

            @Override
            public void onRecvData(Object data) {
                dataListener.onRecvData(data);
            }

            @Override
            public void onError(Exception e) {
                dataListener.onError(e);
            }
        };
        listener.setType(type);
        try {
            if(Net.getInstance()==null){
                throw new HttpException(ErrorCode.ConfigMsgRequired.getCode(),ErrorCode.ConfigMsgRequired.getName());
            }
            if(get==null && post==null){
                throw new HttpException(ErrorCode.GetOrPostRequired.getCode(),ErrorCode.GetOrPostRequired.getName());
            }

            //HttpService service = new DefaultHttpService();
            HttpService service = new OkHttpService();
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setMethod(get==null?HttpMethod.POST:HttpMethod.GET);
            requestInfo.setUrl(Net.getInstance().getBaseUrl()+(get==null?post.value():get.value()));
            Map<String,String> params = new HashMap<>();
            for(int i = 0;i<paramValues.length;i++){
                String value = paramValues[i].toString();
                if(paramNames.get(i).encoded()){
                    value = URLEncoder.encode(value,"UTF-8");
                }
                params.put(paramNames.get(i).value(),value);
            }
            requestInfo.setParams(params);

            if(headers!=null){
                Map<String,String> hds = new HashMap<>();
                for(int i = 0;i<headers.length;i++){
                    hds.put(headers[i].split(":")[0],headers[i].split(":")[1]);
                }
                requestInfo.setHeads(hds);
            }
            service.setRequestInfo(requestInfo);
            requestHodler.setHttpService(service);

            requestHodler.getHttpService().setListener(listener);

            for(RequestHodler rg : Net.getInstance().requestHodlers){
                if(rg.compareTo(requestHodler)==0){
                    Log.i("twy","不请求");
                    return;
                }
            }
            Net.getInstance().requestHodlers.add(requestHodler);
            httpTask = new HttpTask(requestHodler);
            futureTask = new FutureTask<>(httpTask, null);
            //httpTask.requestHodler.getHttpService().cancelRequest();
            ThreadPoolManager.getInstance().execte(futureTask);
        } catch (Exception e) {
            listener.onError(e);
            listener.onComplate();
        }
    }*/

    /*public void unsubscribe(){
        try {
            if(ThreadPoolManager.getInstance().taskQuene.contains(futureTask)){
                ThreadPoolManager.getInstance().removeTask(futureTask);
            }else {
                httpTask.requestHodler.getHttpService().cancelRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
