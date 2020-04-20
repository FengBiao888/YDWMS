package com.yundao.ydwms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.nf.android.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.protocal.request.Baling;
import com.yundao.ydwms.protocal.request.WarehouseVo;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.ProductOutRespone;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.DialogUtil;
import com.yundao.ydwms.util.ToastUtil;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

public class ProductOutgoingActivity extends ScanProductBaseActivity {

    public EditText warehouseName ; // 出货仓
    public EditText orderId ; //订单号
    public  EditText volumeSume ; //出库总卷数
    public EditText weightSum ; //出库总量（kg）

    BigDecimal totalWeight = null;

    private boolean isInit;

    @Override
    protected int getLayout() {
        return R.layout.activity_product_outgoing ;
    }

    @Override
    public void dealwithBarcode(String barcodeStr) {
        productionLog( getActivity(), true, barcodeStr );
    }

    @Override
    public boolean barcodeHasSpecialCondition() {
        return false;
    }

    @Override
    protected void setTitleBar() {
        super.setTitleBar();

//        orderId.setText( "XS2020011502" );
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
    public void initView(Bundle var1) {
        super.initView(var1);
        warehouseName = findViewById( R.id.warehouse_name_value ); // 出货仓
        orderId = findViewById( R.id.orderid_value ); //订单号
        volumeSume = findViewById( R.id.volume_sum_value ); //出库总卷数
        weightSum = findViewById( R.id.weight_sum_value ); //出库总量（kg）

        warehouseName.setText("成品仓");
        submit.setOnClickListener( v->{
            if( productInfos.size() == 0 ){
                ToastUtil.showShortToast( "请先扫条形码" );
                return ;
            }
            DialogUtil.showDeclareDialog( getActivity(),  "确定是否上传记录", v1 -> {
                productionOutgoing(getActivity(), true);
            }).show();
        });
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
    }

    @Override
    protected void clearProductionLogDto() {
        //与setProductionLogDto一样
        setProductionLogDto( null );
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
        Call<ProductOutRespone> productQueryResponeCall = postRequestInterface.outBaling( code );
        productQueryResponeCall
                .enqueue(new BaseCallBack<ProductOutRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<ProductOutRespone> call, Response<ProductOutRespone> response) {
                        super.onResponse(call, response);
                        ProductOutRespone body = response.body();
                        if( body != null && response.code() == 200 ){
//                            int totalElements = body.totalElements;
                            Baling[] content = body.content;
                            if(/* totalElements == content.length && */content.length > 0 ){
                                for( int i = 0 ; i < content.length ; i ++ ){
                                    Baling baling = content[i];
                                    ProductionLogDto info = new ProductionLogDto();
                                    info.trayNumber = baling.trayNumber;
                                    info.length = baling.meter ;
                                    info.productModel = baling.productModel ;
                                    info.productType = baling.productType ;
                                    info.productName = baling.productName;
                                    info.productCode = baling.productCode ;
                                    info.barCode = baling.barCode ;
                                    info.netWeight = baling.netWeight ;
                                    if( info.state == 1 ){ //产品打包，如果是已打包
                                        ToastUtil.showShortToast( "该产品已打包");
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
                                    volumeSume.setText( baling.amount.toString() );
                                    weightSum.setText( baling.netWeight.toString() );
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
     * 产品出仓
     * @param activity
     * @param showProgressDialog
     */
    public void productionOutgoing(Activity activity, boolean showProgressDialog ){

        WarehouseVo vo = new WarehouseVo();
        vo.ids = genCodes();
        vo.warehouseName = "成品仓" ;
        vo.ordersCode = orderId.getText().toString() ;
        vo.amountTotal = new BigDecimal( volumeSume.getText().toString() ) ;
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
                        }
                    }

                });

    }
}
