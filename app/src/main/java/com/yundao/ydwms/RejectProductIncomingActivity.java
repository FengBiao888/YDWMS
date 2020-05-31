package com.yundao.ydwms;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.yundao.ydwms.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.common.listmodule.listitems.EditItemPick;
import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.protocal.request.WarehouseVo;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.protocal.respone.WarehouseRespone;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.DialogUtil;
import com.yundao.ydwms.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class RejectProductIncomingActivity extends ScanProductBaseActivity {

    LinearLayout productInfoParent ;

    private boolean isInit;
    private EditItemPick warehouse;
    private EditItemPick warehousePosition;

    @Override
    protected int getLayout() {
        return R.layout.activity_return_product ;
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

        productInfoParent = findViewById( R.id.product_info_parent ) ;

//        warehouseName.setEnabled( true );
        warehouse = new EditItemPick( getActivity(), "仓库", true, "请选择");
        warehouse.setAlignRight( true );
        View warehouseView = warehouse.getView(0, null);
        productInfoParent.addView(warehouseView);
        warehousePosition = new EditItemPick( getActivity(), "仓位", true, "请选择");
        warehousePosition.setAlignRight( true );
        warehousePosition.setRelatePick( warehouse );
        View warehousePositionView = warehousePosition.getView(0, null);
        productInfoParent.addView(warehousePositionView);

        warehouseView.setOnClickListener( v -> {
            warehouse( getActivity(), true );
        } );
        warehousePositionView.setOnClickListener( v->{
            WarehouseRespone.WarehouseInfo[] warehouseInfos = (WarehouseRespone.WarehouseInfo[]) warehouse.getExtraObj();
            if ( warehouseInfos == null ){
                ToastUtil.showShortToast( "请先选择仓库" );
                return ;
            }
            for( int i = 0 ; i < warehouseInfos.length; i ++ ){
                WarehouseRespone.WarehouseInfo warehouseInfo = warehouseInfos[i];
                if( warehouseInfo.name == warehouse.getInputMessage() ){
                    WarehouseRespone.WarehouseInfo[] warehousePositions = warehouseInfo.warehousePositions;
                    String[] positions = new String[ warehousePositions.length ];
                    for( int j = 0 ; j < warehousePositions.length ; j ++ ){
                        positions[j] = warehousePositions[j].code;
                    }
                    DialogUtil.showTypeDialog(getActivity(), "请选择仓位", positions, new DialogUtil.OnItemSelectListener() {
                        @Override
                        public void onItemSelect(Dialog dialog, String type, int position) {
                            warehousePosition.setInputMessage( type );
                            warehousePosition.postInvalidate();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        submit.setOnClickListener( v->{
            if( productInfos.size() == 0 ){
                ToastUtil.showShortToast( "请先扫条形码" );
                return ;
            }
            DialogUtil.showDeclareDialog( getActivity(),  "确定是否上传记录", v1 -> {
                productionIncoming(getActivity(), true);
            }).show();
        });
    }

    @Override
    protected void setProductionLogDto(ProductionLogDto productInfo) {
//        barCode.setText( productInfo.barCode );
//        material.setText( productInfo.productModel );
//        productName.setText( productInfo.productName );
    }

    @Override
    protected void clearProductionLogDto() {
//        barCode.setText( "" );
//        material.setText( "" );
//        productName.setText( "" );
//        warehouseName.setText( "" );
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
//                                        barCode.setText( "" );
                                        continue;
                                    }
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

    public void warehouse(Activity activity, boolean showProgressDialog){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.warehouse( "产品")
                .enqueue(new BaseCallBack<WarehouseRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<WarehouseRespone> call, Response<WarehouseRespone> response) {
                        super.onResponse(call, response);
                        if( response.code() == 400 ){//未盘点

                        }else if( response.code() == 401 ){
                            ToastUtil.showShortToast( "登录过期，请重新登录" );
                            AvoidOnResult avoidOnResult = new AvoidOnResult( getActivity() );
                            Intent intent = new Intent( getActivity(), LoginActivity.class );
                            avoidOnResult.startForResult(intent, new AvoidOnResult.Callback() {
                                @Override
                                public void onActivityResult(int requestCode, int resultCode, Intent data) {
                                    if( resultCode == Activity.RESULT_OK ){

                                    }
                                }
                            });
                        }else if( response.body() != null ){
                            WarehouseRespone.WarehouseInfo[] warehouseInfos = response.body().content;
                            if( warehouseInfos.length != 0 ) {
                                String[] warehouses = new String[warehouseInfos.length];
                                for (int i = 0; i < warehouseInfos.length; i++) {
                                    warehouses[i] = warehouseInfos[i].name;
                                }
                                DialogUtil.showTypeDialog(getActivity(), "请选择仓库类型", warehouses, (dialog, type, position1) -> {
                                    warehouse.setInputMessage(type);
                                    warehouse.postInvalidate();
                                    warehouse.setExtraObj( warehouseInfos );
                                    dialog.dismiss();
                                });
                            }
                        }
                    }
                });

    }


    /**
     * 产品退仓
     * @param activity
     * @param showProgressDialog
     */
    public void productionIncoming(Activity activity, boolean showProgressDialog ){

        WarehouseVo vo = new WarehouseVo();
        vo.ids = genCodes();

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);
        WarehouseVo warehouseVo = new WarehouseVo();
        List<Long> ids = new ArrayList<Long>();
        for( int i = 0 ; i < productInfos.size() ; i ++ ) {
            ProductionLogDto productionLogDto = productInfos.get(i);
            ids.add( productionLogDto.id );
        }
        warehouseVo.ids = ids ;
        warehouseVo.warehouseName = warehouse.getInputMessage() ;
        warehouseVo.warehousePositionCode = warehousePosition.getInputMessage() ;

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.productionReturn( warehouseVo )
                .enqueue(new BaseCallBack<BaseRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<BaseRespone> call, Response<BaseRespone> response) {
                        super.onResponse(call, response);
                        if( response.code() == 200 || response.code() == 204 ){
                            ToastUtil.showShortToast( "退货进仓成功" );
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
