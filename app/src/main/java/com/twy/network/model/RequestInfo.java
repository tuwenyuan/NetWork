package com.twy.network.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/8.
 * PS: Not easy to write code, please indicate.
 */
public class RequestInfo {
    private HttpMethod method = HttpMethod.GET;
    private String url;
    private Map<String,String> params = new HashMap<>();
    private Map<String,String> heads = new HashMap<>();

    public Map<String, String> getHeads() {
        return heads;
    }

    public void setHeads(Map<String, String> heads) {
        this.heads.putAll(heads);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params.putAll(params);
    }
}
