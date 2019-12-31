package com.yundao.ydwms.protocal;

import java.math.BigDecimal;

public class ProductInfo {

    public long id;

    public String balingId ;

    public long classify ;//类别,盘点时传1
    // 客户id
    public String customerId;

    // 客户简称
    public String customerAbbreviation;

    // 产品id
    public long materielId;

    // 产品名称
    public String materielName;

    // 规格
    public String materielModel;

    // 产品类型
    public String materielType;

    // 生成任务单编号
    public String productionTaskCode;

    // 卷号
    public String volume;

    // 长度
    public int length;

    // 驳口
    public String splice;

    // 净重
    public BigDecimal netWeight;

    // 皮重
    public BigDecimal tareWeight;

    // 毛重
    public BigDecimal grossWeight;

    //班组
    public String team;

    // 班次
    public String train;

    // 机台
    public String machine;

    // 制膜台机
    public String membraneMachine;

    // 吹膜/分切
    public long makeType;

    // 成品包装
    public String productPackaging;

    // 母卷条码
    public String masterBarCode;

    // 母卷规格
    public long masterBarModel;

    // 批号
    public long batchNumber;

    // 条形码
    public String barCode;

    // 生产单号
    public String productionCode;

    // 类型
    public String type;

    // 产品编号
    public String materielCode;

    // 包装
    public String packing;

    // 生产状态
    public String productionState;

    // 作废标志
    public String cancel;
    // 托盘号
    public String trayNumber;
    // 仓库名
    public String warehouseName;
    // 仓位名
    public String warehousePositionName;

    public long etectTime ;

}
