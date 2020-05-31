package com.yundao.ydwms;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.yundao.ydwms.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.protocal.request.WarehouseVo;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.DialogUtil;
import com.yundao.ydwms.util.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class HalfProductIncomingActivity extends ScanProductBaseActivity {

    public EditText barCode ; //条码
    public EditText material ; //料号
    public EditText productName ; //品名
    public EditText warehouseName ; //仓库名
    public EditText warehousePositon ; //仓位

    private boolean isInit;

    @Override
    protected int getLayout() {
        return R.layout.activity_product_incoming;
    }

    @Override
    public void dealwithBarcode(String barcodeStr) {
        productionLog( getActivity(), true, barcodeStr );
    }

    @Override
    protected void setTitleBar() {
        super.setTitleBar();
        titleBar.setRightText( "分类汇总" )
                .setRightTitleClickListener( v->{
                    if( productInfos.size() > 0 ) {
                        AvoidOnResult onResult = new AvoidOnResult(getActivity());
                        Intent intent = new Intent(getActivity(), ProductCatalogueActivity.class);
                        intent.putExtra("productInfoList", productInfos);
                        onResult.startForResult(intent, new AvoidOnResult.Callback() {
                            @Override
                            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                                if (resultCode == Activity.RESULT_OK) {
                                    List<ProductionLogDto> productInfoList = (List<ProductionLogDto>)data.getSerializableExtra("productInfoList");
                                    productInfos.clear();
                                    deleteOperators.clear();
                                    if( productInfoList.size() > 0 ) {
                                        productInfos.addAll(productInfoList);
                                        for (int i = 0; i < productInfos.size(); i++) {
                                            deleteOperators.add("delete");
                                        }
                                    }
                                    totalCount.setText( "合计：" + productInfos.size() + "件" );
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                } );
    }

    @Override
    public boolean barcodeHasSpecialCondition() {
        return false;
    }

    @Override
    public void initView(Bundle var1) {
        super.initView(var1);
        barCode = findViewById( R.id.bar_code_value ); //条码
        material = findViewById( R.id.material_value ); //料号
        productName = findViewById( R.id.product_name_value ); //品名
        warehouseName = findViewById( R.id.warehouse_name_value ); //仓库名
        warehousePositon = findViewById( R.id.warehouse_position_value ); //仓位

        warehouseName.setText( "半成品仓" );
        submit.setOnClickListener( v->{
            if( productInfos.size() == 0 ){
                ToastUtil.showShortToast( "请先扫条形码" );
                return ;
            }
            DialogUtil.showDeclareDialog( getActivity(),  "确定是否上传记录", v1 -> {
                productionIncoming(getActivity(), true);
            }).show();
        });

        barCode.setOnClickListener(v -> DialogUtil.showInputDialog(getActivity(), barCode.getText().toString(), (dialog, type, position) -> {
            barCode.setText( type );
            dialog.dismiss();
        }));
//        dealwithBarcode("15844258895641" );
    }

    @Override
    protected void setProductionLogDto(ProductionLogDto ProductionLogDto) {
        barCode.setText( ProductionLogDto.barCode );
        material.setText( ProductionLogDto.productModel );
        productName.setText( ProductionLogDto.productName );

    }

    @Override
    protected void clearProductionLogDto() {
        barCode.setText( "" );
        material.setText( "" );
        productName.setText( "" );
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
        Call<ProductQueryRespone> productQueryResponeCall = postRequestInterface.productionLog(code);
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
                                    ProductionLogDto info = content[i];
                                    if( info.state == 1 ){ //产品打包，如果是已打包
                                        ToastUtil.showShortToast( "该产品已打包");
                                        barCode.setText( "" );
                                        continue;
                                    }
                                    deleteOperators.add( "delete" );
                                    productInfos.add( info );
                                    if (!isInit) {
                                        pl_root.setAdapter(adapter);
                                        isInit = true;
                                    }
                                        adapter.notifyDataSetChanged();

                                    totalCount.setText("合计：" + productInfos.size() + "件");
                                    setProductionLogDto( info );

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
     * 产品进仓
     * @param activity
     * @param showProgressDialog
     */
    public void productionIncoming(Activity activity, boolean showProgressDialog ){

        WarehouseVo vo = new WarehouseVo();
        vo.ids = genCodes();
        vo.warehouseName = warehouseName.getText().toString() ;
        vo.warehousePositionCode = warehousePositon.getText().toString() ;

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.productionIncoming( vo )
                .enqueue(new BaseCallBack<BaseRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<BaseRespone> call, Response<BaseRespone> response) {
                        super.onResponse(call, response);
                        if( response.code() == 200 || response.code() == 204 ){
                            ToastUtil.showShortToast( "进仓成功" );
                            Intent intent = new Intent(getActivity(), UploadSuccessActivity.class);
                            startActivity( intent );
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
}
