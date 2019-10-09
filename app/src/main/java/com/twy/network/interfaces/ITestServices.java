package com.twy.network.interfaces;

import com.twy.network.User1;
import com.twy.network.business.Observable;
import com.twy.network.model.CommBean;
import com.twy.network.model.User;

/**
 * Author by twy, Email 499216359@qq.com, Date on 2019/1/11.
 * PS: Not easy to write code, please indicate.
 */
public interface ITestServices {
    @Headers({"header1:header1value","header2:header2value"})
    @POST("servlet/GetUser")
    Observable<User> getUserPost(@Query(value = "userName",encoded = true) String userName, @Query("password") String password);
    @Headers({"header1:header1value","header2:header2value"})
    @GET("servlet/GetUser")
    Observable<User> getUserGet(@Query(value = "userName",encoded = true) String userName, @Query("password") String password);
    @Multipart
    @POST("servlet/UploadFile")
    Observable<CommBean> uploadFile(@Query(value = "params",encoded = true) String params, @FileType("iconFile") java.io.File file);

    @POST("http://tt.ugou88.com/ugou-wx/i/custom_page/getIndexPageData")
    Observable<String> getIndexPageData(@Query(value = "pageNumber") int pageNumber,@Query("pageSize") int pageSize,@Query("pcid") int pcid,@Query("pid") int pid);

    @POST("http://39.108.81.229:8001/v1/api/account/logon")
    Observable<String> logon(@Body User1 user);
}
