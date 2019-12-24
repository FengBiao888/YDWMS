package com.yundao.ydwms.protocal.respone;

public class BaseRespone {

    public String code ;
    public String version ;
    public boolean success ;
    public String message;
    //兼容旧的权限错误返回方式
    public String common_return ;
    public String return_info ;

}
