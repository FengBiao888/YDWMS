package com.yundao.ydwms;

public enum ScanTypeEnum {

//     <item>产品打包</item>
//        <item>成品进仓</item>
//        <item>成品出仓 </item>
//        <item>半成品进仓</item>
//        <item>半成品出仓</item>
//        <item>仓位变更</item>
//        <item>产品盘点</item>
    PRODUCT_PACKAGING( 1, "产品打包"),
    PRODUCT_INCOMING( 2, "成品进仓"),
    PRODUCT_OUTGOING( 3, "成品出仓"),
    SEMI_PRODUCT_INCOMING( 4, "半成品进仓"),
    SEMI_PRODUCT_OUTGOING( 5, "半成品出仓"),
    WAREHOUSE_CHANGING( 6, "仓位变更"),
    PRODUCT_INVENTORY( 7, "产品盘点");


    private int type;
    private String nameValue;
    private ScanTypeEnum(int type, String name){
        this.type = type ;
        this.nameValue = name ;
    }
}
