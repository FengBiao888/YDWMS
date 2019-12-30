package com.yundao.ydwms.retrofit;


import android.support.design.widget.BaseTransientBottomBar;

import com.yundao.ydwms.protocal.URLConstant;
import com.yundao.ydwms.protocal.request.LoginRequest;
import com.yundao.ydwms.protocal.request.PackeResourse;
import com.yundao.ydwms.protocal.request.ProductionVo;
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
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
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
    @Headers({"Content-Type: application/json"})
    @PUT(URLConstant.PRODUCTION_INCOMING)
    Call<BaseRespone> productionIncoming(@Body ProductionVo requestBody);
    /**
     * 产品出仓接口
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @PUT(URLConstant.PRODUCTION_OUTGOING)
    Call<BaseRespone> productionOutgoing(@Body ProductionVo requestBody);
    /**
     * 产品加工
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @PUT(URLConstant.PRODUCTION_MACHINING)
    Call<BaseRespone> productionMachining(@Body ProductionVo requestBody);
    /**
     * 产品进仓接口
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @PUT(URLConstant.CHANGE_WAREHOUSE_POSITION)
    Call<BaseRespone> changeWarehousePositon(@Body ProductionVo requestBody);
    /**
     * 产品进仓接口
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @POST(URLConstant.PRODUCTION_BALING)
    Call<PackeResourse> baling(@Body PackeResourse resourse);
    /**
     * 产品进仓接口
     * @return
     */
    @GET(URLConstant.PRODUCTION_IS_CHECKED)
    Call<BaseRespone> monthIsChecked(@Query("date") String date );

}
