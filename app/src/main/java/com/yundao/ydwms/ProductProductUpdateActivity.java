package com.yundao.ydwms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.yundao.ydwms.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.protocal.request.ProductUpdateRequest;
import com.yundao.ydwms.protocal.request.WarehouseVo;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.DialogUtil;
import com.yundao.ydwms.util.SharedPreferenceUtil;
import com.yundao.ydwms.util.ToastUtil;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Response;

public class ProductProductUpdateActivity extends ScanProductBaseActivity {

    private int index = 0 ;
    private String[] codes = new String[]{ "15908468698451", "15908482382951", "15908380552481" };


    public EditText barCode ; //条码
    public EditText material ; //料号
    public EditText productName ; //品名
    public EditText materialModel ; //规格
    public EditText netWeight ; //净重
    public EditText tareWeight ; //皮重
    public EditText grossWeight ; //毛重

    @Override
    protected int getLayout() {
        return R.layout.activity_product_change_info ;
    }

    @Override
    public void dealwithBarcode(String barcodeStr) {

        ProductionLogDto ProductionLogDto = new ProductionLogDto();
        ProductionLogDto.barCode = barcodeStr ;

        if( productInfos.contains( ProductionLogDto ) ){
            ToastUtil.showShortToast( "该产品已在列表中" );
            return ;
        }

        productionLog( getActivity(), true, barcodeStr );
    }

    @Override
    public boolean barcodeHasSpecialCondition() {
        return false;
    }

    @Override
    public void initView(Bundle var1) {
        super.initView(var1);
        SHARE_PREFERENCE_KEY = "PRODUCT_UPDATE_KEY";
        barCode = findViewById( R.id.bar_code_value ); //条码
        material = findViewById( R.id.material_value ); //料号
        productName = findViewById( R.id.product_name_value ); //品名
        materialModel = findViewById( R.id.specification_value ); //规格
        netWeight = findViewById( R.id.net_weight_value ); //净重
        tareWeight = findViewById( R.id.tare_weight_value ) ; //皮重
        grossWeight = findViewById( R.id.gross_weight_value ) ; //毛重

        materialModel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {  }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( !TextUtils.isEmpty( materialModel.getText().toString() ) ){
                    productInfos.get( clickedPosition ).masterBarModel = s.toString() ;
                }
            }
        });
        tareWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {  }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( !TextUtils.isEmpty( tareWeight.getText().toString() ) ){
                    productInfos.get( clickedPosition ).tareWeight = new BigDecimal( tareWeight.getText().toString() )  ;
                }
            }
        });
        grossWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {  }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( !TextUtils.isEmpty( grossWeight.getText().toString() ) ){
                    productInfos.get( clickedPosition ).grossWeight = new BigDecimal( grossWeight.getText().toString() ) ;
                }
            }
        });
        netWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {  }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( !TextUtils.isEmpty( netWeight.getText().toString() )  ){
                    productInfos.get( clickedPosition ).netWeight = new BigDecimal( netWeight.getText().toString() ) ;
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
                halfProductionModify(getActivity(), true );
            }).show();
        });

        barCode.setOnClickListener(v -> DialogUtil.showInputDialog(getActivity(), barCode.getText().toString(), (dialog, type, position) -> {
            barCode.setText( type );
            dealwithBarcode( type );
            dialog.dismiss();
        }));

        loadFromCache();
    }

    @Override
    protected void setProductionLogDto(ProductionLogDto productInfo) {
        barCode.setText( productInfo.barCode );
        material.setText( productInfo.productModel );
        productName.setText( productInfo.productName );
        materialModel.setText( productInfo.productModel );
        netWeight.setText( productInfo.netWeight == null ? "" : productInfo.netWeight.toString() );
        tareWeight.setText( productInfo.tareWeight == null ? "" : productInfo.tareWeight.toString() );
        grossWeight.setText( productInfo.grossWeight == null ? "" : productInfo.grossWeight.toString() );
    }

    @Override
    protected void clearProductionLogDto() {
        barCode.setText( "" );
        material.setText( "" );
        productName.setText( "" );
        materialModel.setText( "" );
        netWeight.setText( "" );
        tareWeight.setText( "" );
        grossWeight.setText( "" );
    }

    /**
     * 半成品信息修改
     * @param activity
     * @param showProgressDialog
     */
    public void halfProductionModify(Activity activity, boolean showProgressDialog ){

        WarehouseVo vo = new WarehouseVo();
        vo.ids = genCodes();

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.productionLogs = productInfos ;
        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.productionUpdate( request )
                .enqueue(new BaseCallBack<BaseRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<BaseRespone> call, Response<BaseRespone> response) {
                        super.onResponse(call, response);
                        if( response.code() == 200 || response.code() == 204 ){
                            productInfos.clear();
                            adapter.notifyDataSetChanged();
                            SharedPreferenceUtil.remove( SHARE_PREFERENCE_KEY );
                            ToastUtil.showShortToast( "半成品信息修改成功" );
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
