package com.yundao.ydwms.protocal.respone;

public class CheckedMonthRespone extends BaseRespone {

    public CheckedMonthInfo[] content ;
    public int totalElements ;

    public class CheckedMonthInfo{
        public long id ;
        public String period ;// "2019-12",
        public String code ;//         "code":null,
        public String warehouseName;//         "warehouseName":"半成品仓",
    }
}
