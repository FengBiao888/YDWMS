package com.yundao.ydwms.retrofit;


import android.support.design.widget.BaseTransientBottomBar;

import com.yundao.ydwms.protocal.ProductInfo;
import com.yundao.ydwms.protocal.URLConstant;
import com.yundao.ydwms.protocal.request.LoginRequest;
import com.yundao.ydwms.protocal.request.PackeResourse;
import com.yundao.ydwms.protocal.request.ProductionVo;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.LoginRespone;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;

import java.math.BigDecimal;
import java.security.Timestamp;
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
     * 半产品出仓接口
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @PUT(URLConstant.HALF_PRODUCTION_OUTGOING)
    Call<BaseRespone> halfProductionOutgoing(@Body ProductionVo requestBody);
    /**
     * 产品加工
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @PUT(URLConstant.PRODUCTION_MACHINING)
    Call<BaseRespone> productionMachining(@Body ProductionVo requestBody);
    /**
     * 改变仓库
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @PUT(URLConstant.CHANGE_WAREHOUSE_POSITION)
    Call<BaseRespone> changeWarehousePositon(@Body ProductionVo requestBody);
    /**
     * 产品打包码
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @POST(URLConstant.PRODUCTION_BALING)
    Call<PackeResourse> baling(@Body PackeResourse resourse);
    /**
     * 产品出仓码的打包码查询
     * @return
     */
    @GET(URLConstant.PRODUCTION_BALING)
    Call<ProductQueryRespone> balingQuery(@Query("barCode") String barCode);
    /**
     * 当月是否盘点
     * @return
     */
    @GET(URLConstant.PRODUCTION_IS_CHECKED)
    Call<BaseRespone> monthIsChecked( );
    /**
     * 产品进仓接口
     * @return
     */
    @PUT(URLConstant.PRODUCTION_PDA_CHECK)
    Call<BaseRespone> pdaCheck(@Body ProductInfo info);

}
