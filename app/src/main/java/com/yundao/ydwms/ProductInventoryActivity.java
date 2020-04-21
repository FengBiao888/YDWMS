package com.yundao.ydwms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.yundao.ydwms.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.protocal.request.CheckRequest;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.DialogUtil;
import com.yundao.ydwms.util.ToastUtil;

import retrofit2.Call;
import retrofit2.Response;

public class ProductInventoryActivity extends ScanProductBaseActivity {

    private boolean isInit;
    private long checkMonthId ;
    private ProductionLogDto checkInfo;

    @Override
    public void dealwithBarcode(String barcodeStr) {
        productionLog( getActivity(), true, barcodeStr );
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_product_info_list ;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        checkMonthId = intent.getLongExtra( "checkMonthId", 0 );
    }

    @Override
    public boolean barcodeHasSpecialCondition() {

        return false;
    }

    @Override
    public void initView(Bundle var1) {
        super.initView(var1);
        //产品盘点不需要确定按钮
        if( submit != null ) {
            submit.setOnClickListener(v -> {
                DialogUtil.showDeclareDialog(getActivity(), "确定是否上传记录", v1 -> {

                }).show();
            });
        }

//        monthIsChecked( getActivity(), true );
    }

    @Override
    protected void setProductionLogDto(ProductionLogDto productInfo) {

    }

    @Override
    protected void clearProductionLogDto() {

    }

    /**
     * 产品信息
     * @param activity
     * @param showProgressDialog
     * @param code
     */
    public void productionLog(Activity activity, boolean showProgressDialog, String code){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        Call<ProductQueryRespone> productQueryResponeCall = postRequestInterface.productionLog( code );
        productQueryResponeCall
                .enqueue(new BaseCallBack<ProductQueryRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<ProductQueryRespone> call, Response<ProductQueryRespone> response) {
                        super.onResponse(call, response);
                        ProductQueryRespone body = response.body();
                        if( body != null && response.code() == 200 ){
//                            int totalElements = body.totalElements;
                            ProductionLogDto[] content = body.content;
                            if(/* totalElements == content.length && */content.length > 0 ){
                                for( int i = 0 ; i < content.length ; i ++ ){
                                    checkInfo = content[i];
                                    pdaCheck(activity, true, checkInfo);
                                }
                            }else{
                                ToastUtil.showShortToast( "不能识别该产品" );
                            }
                        }else if( response.code() == 400 ){
                            ToastUtil.showShortToast( "不能识别该产品" );
                        }else if( response.code() == 401 ){
                            ToastUtil.showShortToast( "登录过期，请重新登录" );
                            AvoidOnResult avoidOnResult = new AvoidOnResult( getActivity() );
                            Intent intent = new Intent( getActivity(), LoginActivity.class );
                            avoidOnResult.startForResult(intent, new AvoidOnResult.Callback() {
                                @Override
                                public void onActivityResult(int requestCode, int resultCode, Intent data) {
                                    if( resultCode == Activity.RESULT_OK ){
                                        User user = YDWMSApplication.getInstance().getUser();
                                        if( user != null ){
                                            operator.setText( "操作员：" + user.username );
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
    }



    /**
     * 产品盘点
     * @param activity
     * @param showProgressDialog
     * @param info
     */
    public void pdaCheck(Activity activity, boolean showProgressDialog, ProductionLogDto info){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
//        info.classify = 1 ;
        CheckRequest request = new CheckRequest();
        request.checkedMonthId = checkMonthId ;
        request.productionLogId = info.id ;
        postRequestInterface.pdaCheck( request )
                .enqueue(new BaseCallBack<BaseRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<BaseRespone> call, Response<BaseRespone> response) {
                        super.onResponse(call, response);
                        if ( response.code() == 400  ){
                            ToastUtil.showShortToast( "数据已盘点" );
                            int index = productInfos.indexOf(info);
                            if( index != -1 ){
                                productInfos.remove( index );
                                deleteOperators.remove( index );
                            }
                        }else{
                            deleteOperators.add( "delete" );
                            productInfos.add( info );
                            if (!isInit) {
                                pl_root.setAdapter(adapter);
                                isInit = true;
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                            totalCount.setText("合计：" + productInfos.size() + "件");
                            setProductionLogDto( info );
                            ToastUtil.showShortToast( "盘点成功" );
                        }
                    }
                });

    }
}
