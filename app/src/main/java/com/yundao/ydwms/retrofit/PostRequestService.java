package com.yundao.ydwms.retrofit;


import com.yundao.ydwms.protocal.URLConstant;
import com.yundao.ydwms.protocal.request.LoginRequest;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.LoginRespone;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface PostRequestService {
    //  @Headers({"Content-Type: application/json;charset=UTF-8","Accept: application/json"})//需要添加头

    /**
     * 登录接口
     * @param inRequestBean
     * @return
     */
    @POST(URLConstant.LOGIN)
    Call<LoginRespone> login(@Body LoginRequest inRequestBean);
    /**
     * 产品查询接口
     * @return
     */
    @GET(URLConstant.PRODUCTION_LOG)
    Call<ProductQueryRespone> productionLog(@Query("barCode") String barCode);
    /**
     * 产品进仓接口
     * @return
     */
    @PUT(URLConstant.PRODUCTION_INCOMING)
    Call<BaseRespone> productionIncoming(@Body RequestBody requestBody);

}
