package com.yundao.ydwms.protocal.respone;

import java.util.ArrayList;

public class WarehouseRespone extends BaseRespone {

    public WarehouseInfo[]  content ;
    public int totalElements ;

    public class WarehouseInfo {

        public long id ; // "id":"2",
        public String name ; // "name":"成品仓",
        public String code ;

        public WarehouseInfo[] warehousePositions;
    }
}
