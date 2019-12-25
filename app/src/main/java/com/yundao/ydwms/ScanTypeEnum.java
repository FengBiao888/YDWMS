package com.yundao.ydwms;

import android.text.TextUtils;

public enum ScanTypeEnum {

//     <item>产品打包</item>
//        <item>成品进仓</item>
//        <item>成品出仓 </item>
//        <item>半成品进仓</item>
//        <item>半成品出仓</item>
//        <item>仓位变更</item>
//        <item>产品盘点</item>
    PRODUCT_PACKAGING( "产品打包", 1),
    PRODUCT_INCOMING( "成品进仓", 2 ),
    PRODUCT_OUTGOING( "成品出仓", 3 ),
    SEMI_PRODUCT_INCOMING( "半成品进仓", 4 ),
    SEMI_PRODUCT_OUTGOING( "半成品出仓", 5 ),
    WAREHOUSE_CHANGING( "仓位变更",6 ),
    PRODUCT_INVENTORY( "产品盘点",7 );


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
