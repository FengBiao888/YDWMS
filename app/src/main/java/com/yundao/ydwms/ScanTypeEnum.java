package com.yundao.ydwms;

import android.text.TextUtils;

public enum ScanTypeEnum {

    HALF_PRODUCT_INCOMING( "半成品进仓", 1 ),
    HALF_PRODUCT_OUTGOING( "半成品出仓", 2 ),
    PRODUCT_PACKAGING( "成品打包", 3),
    PRODUCT_INCOMING( "成品进仓", 4 ),
    PRODUCT_OUTGOING( "成品出仓", 5 ),
    PRODUCT_INVENTORY( "盘点",6 ),
    PRODUCT_SLITTING( "半产品分切",7 ),
    REJECTED_PRODUCT_INCOMING( "退货进仓", 8 ),
    WAREHOUSE_CHANGING( "仓位变更",9 ),
    PRODUCT_INFO_CHANGING( "产品信息变更", 10 ) ,
    SUBSTANDARD_PRODUCT_INCOMING( "不良品进仓", 11 ),
    SUBSTANDARD_PRODUCT_OUTGOING( "不良品出仓", 12 );


    /**
     * 枚举名
     *
     * */
    private String codeName;

    /**
     * 枚举值
     *
     * */
    private int codeValue;


    ScanTypeEnum(String codeName, int codeValue) {
        this.codeName = codeName;
        this.codeValue = codeValue;
    }

    public static String getCodeNameByCode(int codeValue){
        if( codeValue == -1 ) return null ;
        for(ScanTypeEnum leaveTypeEnums : ScanTypeEnum.values()){
            if(codeValue == leaveTypeEnums.getCodeValue() ){
                return leaveTypeEnums.getCodeName();
            }
        }
        return null;
    }

    public static int getCodeValueByName(String name){
        if(TextUtils.isEmpty( name )) return 0 ;
        for(ScanTypeEnum leaveTypeEnums : ScanTypeEnum.values()){
            if(name.equals(leaveTypeEnums.getCodeName())){
                return leaveTypeEnums.getCodeValue();
            }
        }
        return 0;
    }

    public static String[] getAllCodeName(){
        ScanTypeEnum[] values = values();
        String[] codeNames = new String[ values.length ];
        for(int i = 0; i < values.length ; i ++ ) {
            codeNames[ i ] = values[i].codeName ;
        }
        return codeNames ;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public int getCodeValue() {
        return codeValue;
    }

    public void setCodeValue(int codeValue) {
        this.codeValue = codeValue;
    }


}
