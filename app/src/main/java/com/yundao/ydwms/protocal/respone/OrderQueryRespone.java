package com.yundao.ydwms.protocal.respone;

public class OrderQueryRespone extends BaseRespone {

    public OrdersInfo[]  content ;
    public int totalElements ;

    public class OrdersInfo {

        public String code ;

    }
}
