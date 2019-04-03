package com.twy.network.model;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/9.
 * PS: Not easy to write code, please indicate.
 */
public class User {
    /*{"userName":"涂文远back","password":"123456back"}*/
    public String userName;
    public int password;

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
