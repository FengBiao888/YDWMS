package com.yundao.ydwms;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yundao.ydwms.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.protocal.request.WarehouseVo;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;
import com.yundao.ydwms.protocal.respone.ProductStateEnums;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.DialogUtil;
import com.yundao.ydwms.util.SharedPreferenceUtil;
import com.yundao.ydwms.util.ToastUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class HalfProductOutgoingActivity extends ScanProductBaseActivity {

    private int index = 0 ;
    private String[] codes = new String[]{ "15909653672381", "15909103829031" };

    public EditText barCode ; //条码
    public EditText warehouseName ; // 出货仓
    public EditText orderId ; //订单号
    public EditText volumeSume ; //出库总卷数
    public EditText weightSum ; //出库总量（kg）

    BigDecimal totalWeight = null;

    @Override
    public void dealwithBarcode(String barcodeStr) {
        ProductionLogDto ProductionLogDto = new ProductionLogDto();
        ProductionLogDto.barCode = barcodeStr ;

        if( productInfos.contains( ProductionLogDto ) ){
            ToastUtil.showShortToast( "该产品已在列表中" );
            return ;
        }

        productionLog( getActivity(), true, barcodeStr, ProductStateEnums.OUTGOING );

    }

    @Override
    public boolean barcodeHasSpecialCondition() {
        return false;
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
    protected int getLayout() {
        return R.layout.activity_product_outgoing ;
    }

    @Override
    public void initView(Bundle var1) {
        super.initView(var1);
        SHARE_PREFERENCE_KEY = "HALF_PRODUCT_OUTGOING_KEY" ;
        barCode = findViewById( R.id.bar_code_value ); //条码
        warehouseName = findViewById( R.id.warehouse_name_value ); // 出货仓
        orderId = findViewById( R.id.orderid_value ); //订单号
        volumeSume = findViewById( R.id.volume_sum_value ); //出库总卷数
        weightSum = findViewById( R.id.weight_sum_value ); //出库总量（kg）

        warehouseName.setText( "半成品仓" );
//        orderId.setText( "XS2020011502" );
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
                productionOutgoing(getActivity(), true);
            }).show();
        });

        orderId.setOnClickListener( v -> {
                Intent intent = new Intent( getActivity(), OrdersSearchActivity.class );
                AvoidOnResult avoidOnResult = new AvoidOnResult( getActivity() );
                avoidOnResult.startForResult(intent, new AvoidOnResult.Callback() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                        if( resultCode == Activity.RESULT_OK ){
                            String searchResult = data.getStringExtra("searchResult");
                            orderId.setText( searchResult );
                        }
                    }
                });
        });

        barCode.setOnClickListener(v -> DialogUtil.showInputDialog(getActivity(), barCode.getText().toString(), (dialog, type, position) -> {
            barCode.setText( type );
            dealwithBarcode( type );
            dialog.dismiss();
        }));

        loadFromCache(ProductStateEnums.OUTGOING);
    }

    @Override
    protected void setProductionLogDto(ProductionLogDto productInfo) {
        totalWeight = null ;
        //这里对所有产品的卷数相加
        for(int i = 0 ; i < productInfos.size() ; i ++ ){
            ProductionLogDto info = productInfos.get(i);
//            info.volume = ;
            if (info.netWeight != null) {//重量相加
                if (totalWeight == null) {
                    totalWeight = info.netWeight;
                } else {
                    totalWeight = totalWeight.add(info.netWeight).setScale(2, BigDecimal.ROUND_HALF_UP);
                }
            }
        }
        if( totalWeight != null ){
            weightSum.setText(totalWeight.toString() );
        }else{
            weightSum.setText( "" );
        }
        if( productInfos.size() > 0 ){
            volumeSume.setText( productInfos.size() + "" );
        }else{
            volumeSume.setText( "" );
        }

        if( productInfo != null ){
            orderId.setText( productInfo.ordersCode + "" );
        }else{
            orderId.setText( "" );
        }
    }

    @Override
    protected void clearProductionLogDto() {
        //与setProductionLogDto一样
        setProductionLogDto( null );
    }

    /**
     * 半成品品出仓
     * @param activity
     * @param showProgressDialog
     */
    public void productionOutgoing(Activity activity, boolean showProgressDialog ){

        WarehouseVo vo = new WarehouseVo();
        vo.ids = genCodes();
        vo.warehouseName = "半成品仓" ;
        vo.ordersCode = orderId.getText().toString() ;
        vo.amountTotal = new BigDecimal( productInfos.size() ) ;
        vo.number = new BigDecimal(  weightSum.getText().toString() ) ;

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.productionOutgoing( vo )
                .enqueue(new BaseCallBack<BaseRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<BaseRespone> call, Response<BaseRespone> response) {
                        super.onResponse(call, response);
                        if( response.code() == 200 || response.code() == 204 ){
                            productInfos.clear();
                            adapter.notifyDataSetChanged();
                            SharedPreferenceUtil.remove( SHARE_PREFERENCE_KEY );
                            ToastUtil.showShortToast( "出仓成功" );
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
