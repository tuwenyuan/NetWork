package com.twy.network.interfaces;

import com.twy.network.business.Observable;
import com.twy.network.model.CommBean;
import com.twy.network.model.User;

import java.lang.annotation.Annotation;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/11.
 * PS: Not easy to write code, please indicate.
 */
public interface ITestServices {
    @POST("servlet/GetUser")
    Observable<User> getUserPost(@Query(value = "userName",encoded = true) String userName, @Query("password") String password);
    @GET("servlet/GetUser")
    Observable<User> getUserGet(@Query(value = "userName",encoded = true) String userName, @Query("password") String password);
    @Multipart
    @POST("servlet/UploadFile")
    Observable<CommBean> uploadFile(@Query(value = "params",encoded = true) String params, @File java.io.File file);
}
