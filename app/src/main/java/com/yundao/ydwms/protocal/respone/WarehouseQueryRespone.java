package com.yundao.ydwms.protocal.respone;


public class WarehouseQueryRespone extends BaseRespone {

    public WarehouseLog[] content ;
    public int totalElements ;

    public class WarehouseLog{

        public long id ;// "id":"2",
        public String name ; //"name":"2号仓位",
        public long warehouseId ;//       "warehouseId":"2",
        public String warehouseName ;        //"warehouseName":"成品仓",
        public String code ;       // "code":"1.12",
        public String barCode ;     //  "barCode":"15774952757223",
    }
}
