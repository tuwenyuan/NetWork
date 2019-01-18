package com.twy.network.interfaces;

import com.twy.network.model.RequestInfo;


/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/8.
 * PS: Not easy to write code, please indicate.
 */
public interface IHttpService {

    /**
     * 设置请求数据
     * @param requestInfo
     */
    void setRequestInfo(RequestInfo requestInfo);

    /**
     * 执行获取网络
     */
    void excute();

    /**
     * 设置处理接口
     * @param httpListener
     */
    void setListener(DataListener httpListener);

}
