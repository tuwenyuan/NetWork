package com.twy.network.business;

import android.os.Handler;
import android.os.Looper;

import com.twy.network.Exception.HttpException;
import com.twy.network.interfaces.DataListener;
import com.twy.network.interfaces.FileType;
import com.twy.network.interfaces.HttpService;
import com.twy.network.interfaces.OnRecvDataListener;
import com.twy.network.interfaces.Query;
import com.twy.network.model.ErrorCode;
import com.twy.network.model.HttpMethod;
import com.twy.network.model.RequestInfo;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;

/**
 * Author by twy, Email 499216359@qq.com, Date on ${DATA}.
 * PS: Not easy to write code, please indicate.
 */
public class StartRequestData {
    public Map<RequestHodler,FutureTask> map = new HashMap<>();
    protected Handler handler = new Handler(Looper.getMainLooper()) ;

    public void startRequestNetData(RequestManagerFragment fragment, Observable observable, final OnRecvDataListener dataListener) {
        final RequestHodler requestHodler = new RequestHodler();
        DataListener listener = new DataListener() {
            @Override
            public void onStart() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dataListener.onStart();
                    }
                });
            }

            @Override
            public void onComplate() {
                map.remove(requestHodler);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dataListener.onComplate();
                    }
                });
            }

            @Override
            public void onRecvData(final Object data) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dataListener.onRecvData(data);
                    }
                });
            }

            @Override
            public void onError(final Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dataListener.onError(e);
                    }
                });
            }
        };
        listener.setType(observable.type);
        try {
            if(Net.getInstance()==null){
                throw new HttpException(ErrorCode.ConfigMsgRequired.getCode(),ErrorCode.ConfigMsgRequired.getName());
            }
            if(observable.get==null && observable.post==null){
                throw new HttpException(ErrorCode.GetOrPostRequired.getCode(),ErrorCode.GetOrPostRequired.getName());
            }
            if(observable.get!=null && observable.post!=null){
                throw new HttpException(ErrorCode.GetPostOne.getCode(),ErrorCode.GetPostOne.getName());
            }
            if(observable.isMultipart){
                if(observable.get!=null){
                    throw new HttpException(ErrorCode.UploadFileRequiredPostRequest.getCode(),ErrorCode.UploadFileRequiredPostRequest.getName());
                }
            }
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setMethod(observable.get==null? HttpMethod.POST:HttpMethod.GET);
            requestInfo.setMultipart(observable.isMultipart);
            String path;
            if(observable.get!=null){
                if(observable.get.value().startsWith("http")){
                    path = observable.get.value();
                }else {
                    path = Net.getInstance().getBaseUrl()+ observable.get.value();
                }
            }else {
                if(observable.post.value().startsWith("http")){
                    path = observable.post.value();
                }else {
                    path = Net.getInstance().getBaseUrl()+ observable.post.value();
                }
            }
            requestInfo.setUrl(path);
            if(observable.paramValues!=null){
                Map<String,String> params = new HashMap<>();
                for(int i = 0;i<observable.paramValues.length;i++){
                    if(observable.paramNames.get(i) instanceof Query && observable.paramValues[i]!=null){
                        String value = observable.paramValues[i].toString();
                        if(((Query)observable.paramNames.get(i)).encoded()){
                            value = URLEncoder.encode(value,"UTF-8");
                        }
                        params.put(((Query)observable.paramNames.get(i)).value(),value);
                    }else if(observable.paramNames.get(i) instanceof FileType){
                        if(observable.paramValues[i] instanceof java.io.File){
                            requestInfo.setFile((java.io.File) observable.paramValues[i]);
                        }else if(observable.isMultipart){
                            throw new HttpException(ErrorCode.UploadFileTypeRequired.getCode(),ErrorCode.UploadFileTypeRequired.getName());
                        }
                    }
                }
                requestInfo.setParams(params);
            }
            if(observable.headers!=null){
                Map<String,String> hds = new HashMap<>();
                for(int i = 0;i<observable.headers.length;i++){
                    hds.put(observable.headers[i].split(":")[0],observable.headers[i].split(":")[1]);
                }
                requestInfo.setHeads(hds);
            }
            requestHodler.setRequestInfo(requestInfo);
            requestHodler.setListener(listener);
            /*for(RequestHodler rg : map.keySet()){
                if(rg.compareTo(requestHodler)==0){
                    //Log.i("twy","不请求");
                    return;
                }
            }*/
            requestHodler.setFragment(fragment!=null?fragment.toString():null);
            HttpTask httpTask = new HttpTask(requestHodler);
            FutureTask futureTask = new FutureTask<>(httpTask, null);
            map.put(requestHodler,futureTask);
            ThreadPoolManager.getInstance().execte(futureTask);
        } catch (Exception e) {
            listener.onError(e);
            listener.onComplate();
        }
    }

    public void startRequestNetData(Observable observable, final OnRecvDataListener dataListener){
        startRequestNetData(null,observable,dataListener);
    }

    public void unsubscribe(){
        try {
            for (RequestHodler rh : map.keySet()){
                if(ThreadPoolManager.getInstance().taskQuene.contains(map.get(rh))){
                    ThreadPoolManager.getInstance().removeTask(map.get(rh));
                }else {
                    Net.getInstance().getHttpService().cancelRequest();
                }
                map.remove(rh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
