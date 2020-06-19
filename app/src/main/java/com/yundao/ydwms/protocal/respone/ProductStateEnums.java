package com.yundao.ydwms.protocal.respone;


import android.text.TextUtils;

/**
 * 审批类型
 * @author liangjianhua
 *
 * */
public enum ProductStateEnums {
//    类型（0=请假，1=补卡，2=报销, 3=加班）.
    NONE("已称重", 0),

    INCOMING ("库存", 1),

    OUTGOING("已出库", 2),

    RETURN("退货", 3) ;

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


    ProductStateEnums(String codeName, int codeValue) {
        this.codeName = codeName;
        this.codeValue = codeValue;
    }

    public static String getCodeNameByCode(int codeValue){
        if( codeValue == -1 ) return null ;
        for(ProductStateEnums leaveTypeEnums : ProductStateEnums.values()){
            if(codeValue == leaveTypeEnums.getCodeValue() ){
                return leaveTypeEnums.getCodeName();
            }
        }
        return null;
    }

    public static int getCodeValueByName(String name){
        if(TextUtils.isEmpty( name )) return 0 ;
        for(ProductStateEnums leaveTypeEnums : ProductStateEnums.values()){
            if(name.equals(leaveTypeEnums.getCodeName())){
                return leaveTypeEnums.getCodeValue();
            }
        }
        return 0;
    }

    public static String[] getAllCodeName(){
        ProductStateEnums[] values = values();
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
