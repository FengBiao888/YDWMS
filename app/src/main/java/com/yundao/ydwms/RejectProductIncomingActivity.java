package com.yundao.ydwms;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.yundao.ydwms.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.common.listmodule.listitems.EditItemPick;
import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.protocal.request.WarehouseVo;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;
import com.yundao.ydwms.protocal.respone.ProductStateEnums;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.protocal.respone.WarehouseRespone;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.DialogUtil;
import com.yundao.ydwms.util.SharedPreferenceUtil;
import com.yundao.ydwms.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class RejectProductIncomingActivity extends ScanProductBaseActivity {

    private int index = 0 ;
    private String[] codes = new String[]{ "15908468698451", "15908482382951", "15908380552481" };

    LinearLayout productInfoParent ;

    public EditText barCode ; //条码
    private EditItemPick warehouse;
    private EditItemPick warehousePosition;

    @Override
    protected int getLayout() {
        return R.layout.activity_return_product ;
    }

    @Override
    public void dealwithBarcode(String barcodeStr) {

        ProductionLogDto ProductionLogDto = new ProductionLogDto();
        ProductionLogDto.barCode = barcodeStr ;

        if( productInfos.contains( ProductionLogDto ) ){
            ToastUtil.showShortToast( "该产品已在列表中" );
            return ;
        }

        productionLog( getActivity(), true, barcodeStr, ProductStateEnums.INCOMING );
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
        SHARE_PREFERENCE_KEY = "REJECT_PRODUCT_INCOMING_KEY" ;
        productInfoParent = findViewById( R.id.product_info_parent ) ;

        barCode = findViewById( R.id.bar_code_value ); //条码
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
            if( YDWMSApplication.getInstance().isUseLocalData() ) {
                if (index < codes.length) {
                    dealwithBarcode(codes[index]);
                    index++;
                    return;
                }
            }

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
            dealwithBarcode( type );
            dialog.dismiss();
        }));

        loadFromCache(ProductStateEnums.INCOMING);
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
                        }else{
                            try {
                                ToastUtil.showShortToast( response.errorBody().string() );
                            } catch (Exception e) {
                                e.printStackTrace();
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
                            productInfos.clear();
                            adapter.notifyDataSetChanged();
                            SharedPreferenceUtil.remove( SHARE_PREFERENCE_KEY );
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
                        }else{
                            try {
                                ToastUtil.showShortToast( response.errorBody().string() );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });

    }
}
