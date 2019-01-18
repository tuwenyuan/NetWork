package com.twy.network.interfaces;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/14.
 * PS: Not easy to write code, please indicate.
 */
public abstract class OnRecvDataListener<M> {
    /**
     * 开始请求
     */
    public void onStart(){

    }

    /**
     * 请求完成
     */
    public void onComplate(){

    }

    /**
     * 请求返回数据
     * @param data
     */
    public abstract void onRecvData(M data);

    /**
     * 请求发生错误
     * @param e
     */
    public abstract void onError(Exception e);

}
