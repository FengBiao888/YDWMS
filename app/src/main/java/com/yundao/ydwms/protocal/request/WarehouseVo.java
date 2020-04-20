package com.yundao.ydwms.protocal.request;

import com.yundao.ydwms.protocal.ProductionLogDto;

import java.math.BigDecimal;
import java.util.List;

public class WarehouseVo {

    //ID数组
    public List<Long> ids;

    //产品ID
    public long productionLogId;

    //仓库名称
    public String warehouseName;

    //仓位号
    public String warehousePositionCode;

    //打包
//    public Baling baling;

    //打包Id
    public long balingId;

    //产品修改列表
    public List<ProductionLogDto> productionLogs;

    //期间
    public String period;

    //仓库ID
    public long warehouseId;

    //仓位ID
    public long warehousePositionId;

    //期间ID
    public long checkedMonthId;

    //订单ID
    public String ordersCode;

    //出库总量
    public BigDecimal number;

    //订单ID
    public BigDecimal amountTotal;
}
