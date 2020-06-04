package com.yundao.ydwms.retrofit;


import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.protocal.URLConstant;
import com.yundao.ydwms.protocal.request.Baling;
import com.yundao.ydwms.protocal.request.BalingRequest;
import com.yundao.ydwms.protocal.request.CheckRequest;
import com.yundao.ydwms.protocal.request.LoginRequest;
import com.yundao.ydwms.protocal.request.ProductArrayLogRequest;
import com.yundao.ydwms.protocal.request.ProductUpdateRequest;
import com.yundao.ydwms.protocal.request.WareHouseChangingRequest;
import com.yundao.ydwms.protocal.request.WarehouseVo;
import com.yundao.ydwms.protocal.respone.BalingQueryRespone;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.CheckedMonthRespone;
import com.yundao.ydwms.protocal.respone.LoginRespone;
import com.yundao.ydwms.protocal.respone.OrderQueryRespone;
import com.yundao.ydwms.protocal.respone.ProductOutRespone;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;
import com.yundao.ydwms.protocal.respone.WarehouseQueryRespone;
import com.yundao.ydwms.protocal.respone.WarehouseRespone;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
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
     * 产品查询接口，多个
     * @return
     */
    @POST(URLConstant.PRODUCTION_ARRAY_LOG)
    Call<ProductionLogDto[]> productionLog(@Body ProductArrayLogRequest request);
    /**
     * 仓位查询接口
     * @return
     */
    @GET(URLConstant.QUERY_WAREHOUSE_LOG)
    Call<WarehouseQueryRespone> queryWarehouselog(@Query("barCode") String barCode);
    /**
     * 产品查询接口
     * @return
     */
    @GET(URLConstant.BALING_PRODUCTION_LOG)
    Call<BalingQueryRespone> balingProductionLog(@Query("balingBarCode") String barCode);
    /**
     * 产品进仓接口
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @PUT(URLConstant.PRODUCTION_INCOMING)
    Call<BaseRespone> productionIncoming(@Body WarehouseVo requestBody);
    /**
     * 产品出仓接口
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @PUT(URLConstant.PRODUCTION_OUTGOING)
    Call<BaseRespone> productionOutgoing(@Body WarehouseVo requestBody);
    /**
     * 半成品出仓接口
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @PUT(URLConstant.HALF_PRODUCTION_OUTGOING)
    Call<BaseRespone> halfProductionOutgoing(@Body WarehouseVo requestBody);
    /**
     * 半成品修改接口
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @PUT(URLConstant.PRODUCTION_LOG)
    Call<BaseRespone> halfProductionModify(@Body ProductionLogDto requestBody);
    /**
     * 产品加工
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @PUT(URLConstant.PRODUCTION_MACHINING)
    Call<BaseRespone> productionMachining(@Body WarehouseVo requestBody);
    /**
     * 改变仓库
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @PUT(URLConstant.CHANGE_WAREHOUSE_POSITION)
    Call<BaseRespone> changeWarehousePositon(@Body WareHouseChangingRequest requestBody);
    /**
     * 产品打包码
     * @return
     */
    @Headers({"Content-Type: application/json"})
    @PUT(URLConstant.PRODUCTION_BALING)
    Call<Baling> baling(@Body BalingRequest resourse);
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
     * 盘点
     * @return
     */
    @PUT(URLConstant.PRODUCTION_PDA_CHECK)
    Call<BaseRespone> pdaCheck(@Body CheckRequest info);
    /**
     * 盘点
     * @return
     */
    @GET(URLConstant.WAREHOUSE)
    Call<WarehouseRespone> warehouse(@Query("type") String type );
    /**
     * 盘点
     * @return
     */
    @PUT(URLConstant.MACHINING_PRODUCTION)
    Call<BaseRespone> machiningProduction(@Body BalingRequest info);
    /**
     * 盘点
     * @return
     */
    @PUT(URLConstant.PRODUCTION_UPDATE)
    Call<BaseRespone> productionUpdate(@Body ProductUpdateRequest info);
    /**
     * 盘点
     * @return
     */
    @GET(URLConstant.PRODUCTION_CHECKED_MONTH)
    Call<CheckedMonthRespone> productionCheckedMonth(@Query("warehouseName") String warehouseName, @Query("type") int type);
 /**
     * 盘点
     * @return
     */
    @PUT(URLConstant.PRODUCTION_RETURN)
    Call<BaseRespone> productionReturn(@Body WarehouseVo warehouseVo);
    /**
     * 成品出仓产品信息查询
     * @return
     */
    @GET(URLConstant.OUT_BALING)
    Call<ProductOutRespone> outBaling(@Query("barCode") String barCode);
    /**
     * 盘点
     * @return
     */
    @GET(URLConstant.ORDERS_QUERY)
    Call<OrderQueryRespone.OrdersInfo[]> ordersQuery(@Query("code") String code);

}
