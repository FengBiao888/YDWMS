package com.yundao.ydwms.protocal;


/**
 * 请求接口地址常量类，不包含包含“http://host：port”
 *
 * @author yyh
 */
public class URLConstant {

     public static String BASE_URL = "http://121.201.76.121:8086/"; //新外网地址
     public static String BASE_H5_URL = "https://test.southhr.cn"; //新外网地址
    /**
     * 登录
     */
    public static final String LOGIN = "/auth/login" ;
    /**
     * 产品查询
     */
    public static final String PRODUCTION_LOG = "/api/productionLog";
    /**
     * 产品进仓
     */
    public static final String PRODUCTION_INCOMING = "api/goProduction";
    /**
     * 产品出仓
     */
    public static final String PRODUCTION_OUTGOING = "api/outProduction";
    /**
     * 产品出仓
     */
    public static final String HALF_PRODUCTION_OUTGOING = "api/outHalfProduction";
    /**
     * 仓位变更
     */
    public static final String CHANGE_WAREHOUSE_POSITION = "api/changeWarehousePosition";
    /**
     * 产品加工
     */
    public static final String PRODUCTION_MACHINING = "api/machiningProduction";
    /**
     * 产品加工
     */
    public static final String PRODUCTION_BALING = "api/baling";
    /**
     * 是否该月盘点过
     */
    public static final String PRODUCTION_IS_CHECKED = "api/isChecked";
    /**
     * 盘点
     */
    public static final String PRODUCTION_PDA_CHECK = "api/PDACheck";
}
