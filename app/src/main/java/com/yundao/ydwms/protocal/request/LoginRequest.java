package com.yundao.ydwms.protocal.request;

import com.yundao.ydwms.protocal.BaseRequestBean;
import com.yundao.ydwms.protocal.respone.BaseRespone;

public class LoginRequest extends BaseRequestBean {

    public String code ;
    public String username ;
    public String password ;
    public String uuid ;
}
