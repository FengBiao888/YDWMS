package com.yundao.ydwms.protocal.request;

import com.yundao.ydwms.protocal.BaseRequestBean;
import com.yundao.ydwms.protocal.ProductionLogDto;

import java.util.List;

public class ProductUpdateRequest extends BaseRequestBean {

    public List<ProductionLogDto> productionLogs;
}
