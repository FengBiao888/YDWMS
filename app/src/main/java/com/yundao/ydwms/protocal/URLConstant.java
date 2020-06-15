package com.yundao.ydwms.protocal;


/**
 * 请求接口地址常量类，不包含包含“http://host：port”
 *
 * @author yyh
 */
public class URLConstant {

     public static String BASE_URL = "http://api.dg.yuelongfilm.com"; //新外网地址
//     public static String BASE_URL = "http://121.201.76.116:8000/"; //新外网地址
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
     * 产品查询，组全
     */
    public static final String PRODUCTION_ARRAY_LOG = "api/productionLog/barcodes";
    /**
     * 产品查询
     */
    public static final String QUERY_WAREHOUSE_LOG = "/api/warehousePosition";
    /**
     * 打包的成品查询
     */
    public static final String BALING_PRODUCTION_LOG = "/api/productionLog/getBaling";
    /**
     * 产品进仓
     */
    public static final String PRODUCTION_INCOMING = "api/productionLog/goProduction";
    /**
     * 产品出仓
     */
    public static final String PRODUCTION_OUTGOING = "api/productionLog/outProduction";
    /**
     * 半产品出仓
     */
    public static final String HALF_PRODUCTION_OUTGOING = "api/outHalfProduction";

    /**
     * 仓位变更
     */
    public static final String CHANGE_WAREHOUSE_POSITION = "/api/productionLog/changeWarehousePosition";
    /**
     * 产品加工
     */
    public static final String PRODUCTION_MACHINING = "api/machiningProduction";
    /**
     * 产品打包
     */
    public static final String PRODUCTION_BALING = "/api/productionLog/baling";
    /**
     * 是否该月盘点过
     */
    public static final String PRODUCTION_IS_CHECKED = "api/isChecked";
    /**
     * 盘点
     */
    public static final String PRODUCTION_PDA_CHECK = "/api/productionLog/check";
    /**
     * 仓库
     */
    public static final String WAREHOUSE = "/api/warehouse";
    /**
     * 产品分切
     */
    public static final String MACHINING_PRODUCTION = "/api/productionLog/machiningProduction";
    /**
     * 产品
     */
    public static final String PRODUCTION_UPDATE = "/api/productionLog/updateProductionLogs";
    /**
     * 查月分
     */
    public static final String PRODUCTION_CHECKED_MONTH = "/api/checkedMonth";
    /**
     * 退仓
     */
    public static final String PRODUCTION_RETURN = "/api/productionLog/returnProduction";
    /**
     * 产品出库扫码查询
     */
    public static final String OUT_BALING = "/api/baling";
    /**
     * 产品出库扫码查询，数组
     */
    public static final String OUT_BALING_ARRAY = "/api/baling/barcodes";

    /**
     * 订单号搜索
     */
    public static final String ORDERS_QUERY = "api/orders/findCode";
    /**
     * 以3结尾的条码搜索
     */
    public static final String BALING_TOTAL = "api/baling/total";
}
