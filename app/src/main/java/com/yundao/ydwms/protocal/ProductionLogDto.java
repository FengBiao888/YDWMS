package com.yundao.ydwms.protocal;

import java.math.BigDecimal;
import java.io.Serializable;
/**
* @author jim
* @date 2020-01-15
*/
public class ProductionLogDto implements Serializable {

    /** 防止精度丢失 */
    public long id;

    /** 客户id */
    public long customerId;

    /** 客户名称 */
    public String customerAbbreviation;

    /** 产品id */
    public long productId;

    /** 产品编号 */
    public String productCode;

    public String balingId ;

    /** 产品名称 */
    public String productName;

    /** 产品规格 */
    public String productModel;

    /** 产品类型 */
    public String productType;

    /** 销售订单id */
    public long ordersId;

    public long cuttingTime ;

    public String ordersCode ;
    /** 卷号 */
    public String volume;

    /** 卷长 */
    public BigDecimal length;

    /** 驳口 */
    public String splice;

    /** 净重 */
    public BigDecimal netWeight;

    /** 皮重 */
    public BigDecimal tareWeight;

    /** 毛重 */
    public BigDecimal grossWeight;

    /** 班次 */
    public String train;

    /** 机台 */
    public String machine;

    /** 制膜台机 */
    public String membraneMachine;

    /** 吹膜/分切 */
    public String makeType;

    /** 成品包装 */
    public String productPackaging;

    /** 母卷条码 */
    public String masterBarCode;

    /** 母卷规格 */
    public String masterBarModel;

    /** 批号 */
    public String batchNumber;

    /** 条码号 */
    public String barCode;

    /** 生产单号 */
    public String productionCode;

    /** 类型 */
    public String type;

    /** 打包类型(未打包[0],已打包[1]) */
    public int state;

    /** 包装 */
    public String packing;

    /** 生产状态(已称重[0],库存[1],已出库[2],退货[3]) */
    public int productionState;

    /** 作废标志(未作废[0],已作废[1]) */
    public int cancel;

    /** 托盘号 */
    public String trayNumber;

    /** 生产时间 */
//    public Timestamp productionTime;

    /** 仓库名称 */
    public String warehouseName;

    /** 备注 */
    public String remark;

    /** 班组*/
    public String team;

    /** 创建人 */
    public String createBy;

    /** 创建时间 */
//    public Timestamp createTime;

    /** 修改人 */
    public String updateBy;

    /** 修改时间 */
//    public Timestamp updateTime;

    public String productionTaskCode;

    /** 仓位 */
    public String warehousePositionCode;

    @Override
    public boolean equals(Object obj) {
        if( obj instanceof ProductionLogDto ){
            return barCode.equals( ((ProductionLogDto) obj).barCode );
        }
        return super.equals(obj);
    }

    public boolean isSameType( ProductionLogDto info ){
        System.out.println( "aabbcc: " + productName + "," + info.productName + "," + productModel + "," + info.productModel);
        if( productName.equals( info.productName ) && productModel.equals( info.productModel )){
            return true ;
        }else{
            return false ;
        }
    }
}
