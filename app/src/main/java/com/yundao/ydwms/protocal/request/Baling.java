package com.yundao.ydwms.protocal.request;

import com.yundao.ydwms.protocal.ProductionLogDto;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.List;

/**
* @author jim
* @date 2020-01-16
*/
public class Baling implements Serializable {

    /** id */
    public Long id;
    
    /** 销售订单id */
    public long ordersId;

    public long customerId;

    /** 客户 */
    public String customerAbbreviation;

    /** 条形码 */
    public String barCode;

    /** 打包日期 */
    public long balingDate;

    /** 托盘号 */
    public String trayNumber;

    /** 数量 */
    public int amount;

    /** 总净重 */
    public BigDecimal netWeight;

    /** 总米数 */
    public BigDecimal meter;

    /** 0未出库1已出库 */
    public int state;

    /** 产品id */
    public long productId;

    /** 产品编号 */
    public String productCode;

    /** 产品名称 */
    public String productName;

    /** 产品规格 */
    public String productModel;

    /** 产品类型 */
    public String productType;

    public List<ProductionLogDto> list ;

    public List<Model> modelList ;

    public static class Model {
        /** 产品规格 */
        public String productModel;

//        public int number ;
//
//        public float netWeight ;
//
//        public int length ;

        /** 数量 */
        public int number;

        /** 总净重 */
        public BigDecimal netWeight;

        /** 总米数 */
        public BigDecimal length;
    }
}