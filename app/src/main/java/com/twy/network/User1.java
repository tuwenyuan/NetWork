package com.twy.network;

import com.google.gson.Gson;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/7/3.
 * PS: Not easy to write code, please indicate.
 */
public class User1 {
    public String loginId;
    public String code;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
