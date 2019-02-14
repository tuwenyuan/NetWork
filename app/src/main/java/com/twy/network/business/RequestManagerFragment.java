package com.twy.network.business;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.twy.network.Exception.HttpException;
import com.twy.network.interfaces.DataListener;
import com.twy.network.interfaces.File;
import com.twy.network.interfaces.HttpService;
import com.twy.network.interfaces.OnRecvDataListener;
import com.twy.network.interfaces.Query;
import com.twy.network.model.ErrorCode;
import com.twy.network.model.HttpMethod;
import com.twy.network.model.RequestInfo;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/14.
 * PS: Not easy to write code, please indicate.
 */
public class RequestManagerFragment extends Fragment {

    public List<RequestHodler> requestHodlers = new ArrayList<>();
    private HttpTask httpTask;
    private FutureTask futureTask;
    protected Handler handler = new Handler(Looper.getMainLooper()) ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i("twy","fragment创建了");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unsubscribe();
        //Log.i("twy","fragment销毁了");
    }

    public void startRequestData(Observable observable, final OnRecvDataListener dataListener){
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
                requestHodlers.remove(requestHodler);
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

            HttpService service = Net.getInstance().getHttpService()==null?new DefaultHttpService():Net.getInstance().getHttpService();
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setMethod(observable.get==null? HttpMethod.POST:HttpMethod.GET);
            requestInfo.setMultipart(observable.isMultipart);
            requestInfo.setUrl(Net.getInstance().getBaseUrl()+(observable.get==null?observable.post.value():observable.get.value()));
            if(observable.paramValues!=null){
                Map<String,String> params = new HashMap<>();
                for(int i = 0;i<observable.paramValues.length;i++){
                    if(observable.paramNames.get(i) instanceof Query && observable.paramValues[i]!=null){
                        String value = observable.paramValues[i].toString();
                        if(((Query)observable.paramNames.get(i)).encoded()){
                            value = URLEncoder.encode(value,"UTF-8");
                        }
                        params.put(((Query)observable.paramNames.get(i)).value(),value);
                    }else if(observable.paramNames.get(i) instanceof File){
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
            service.setRequestInfo(requestInfo);
            requestHodler.setHttpService(service);
            requestHodler.getHttpService().setListener(listener);
            for(RequestHodler rg : requestHodlers){
                if(rg.compareTo(requestHodler)==0){
                    //Log.i("twy","不请求");
                    return;
                }
            }
            requestHodlers.add(requestHodler);
            httpTask = new HttpTask(requestHodler);
            futureTask = new FutureTask<>(httpTask, null);
            ThreadPoolManager.getInstance().execte(futureTask);
        } catch (Exception e) {
            listener.onError(e);
            listener.onComplate();
        }
    }

    public void unsubscribe(){
        try {
            if(ThreadPoolManager.getInstance().taskQuene.contains(futureTask)){
                ThreadPoolManager.getInstance().removeTask(futureTask);
            }else {
                httpTask.requestHodler.getHttpService().cancelRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
