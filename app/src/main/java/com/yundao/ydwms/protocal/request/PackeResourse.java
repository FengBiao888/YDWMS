package com.yundao.ydwms.protocal.request;

import com.yundao.ydwms.protocal.BaseRequestBean;
import com.yundao.ydwms.protocal.ProductInfo;

import java.math.BigDecimal;
import java.util.List;

public class PackeResourse extends BaseRequestBean {

    public String id ;//平台返回id
    public String barCode ;
    public String customerName ;
    public String customerId;
    public long dateline ;
    //托盘号
    public String trayNumber ;
    //产品编号
    public String materielCode ;
    //产品名称
    public String materielName ;
    //产品规格
    public String materielModel ;
    //数量
    public int number;
    //总净重
    public BigDecimal netWeight ;
    //总米数
    public String meter ;

    public List<ProductInfo> productionLogs ;
}
