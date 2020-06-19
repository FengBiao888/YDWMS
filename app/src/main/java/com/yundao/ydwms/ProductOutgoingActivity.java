package com.yundao.ydwms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.yundao.ydwms.LoginActivity;
import com.yundao.ydwms.OrdersSearchActivity;
import com.yundao.ydwms.ProductCatalogueActivity;
import com.yundao.ydwms.R;
import com.yundao.ydwms.ScanProductBaseActivity;
import com.yundao.ydwms.UploadSuccessActivity;
import com.yundao.ydwms.YDWMSApplication;
import com.yundao.ydwms.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.protocal.request.Baling;
import com.yundao.ydwms.protocal.request.ProductArrayLogRequest;
import com.yundao.ydwms.protocal.request.WarehouseVo;
import com.yundao.ydwms.protocal.respone.BalingTotalQueryRespone;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.ProductOutRespone;
import com.yundao.ydwms.protocal.respone.ProductStateEnums;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.DialogUtil;
import com.yundao.ydwms.util.SharedPreferenceUtil;
import com.yundao.ydwms.util.ToastUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ProductOutgoingActivity extends ScanProductBaseActivity {

    private int index = 0 ;
    private String[] codes = new String[]{ "15913225105012", "15918477647002" };

    public EditText barCode ; //条码
    public EditText warehouseName ; // 出货仓
    public EditText orderId ; //订单号
    public  EditText volumeSume ; //出库总卷数
    public EditText weightSum ; //出库总量（kg）

    BigDecimal totalWeight = null;

    private boolean isInit;
    private List<String> cacheBarcodes = new ArrayList<>();
    private List<Long> uploadIds = new ArrayList<>();

    @Override
    protected int getLayout() {
        return R.layout.activity_product_outgoing ;
    }

    @Override
    public void dealwithBarcode(String barcodeStr) {

        if( barcodeStr.endsWith( "3" ) ){
            balingTotal( getActivity(), true , barcodeStr, ProductStateEnums.OUTGOING );
        }else{
            if( cacheBarcodes.contains( barcodeStr ) ){
                ToastUtil.showShortToast( "该产品已在列表中" );
                return ;
            }

            productionBalingLog( getActivity(), true, barcodeStr, ProductStateEnums.OUTGOING );
        }


    }

    @Override
    public boolean barcodeHasSpecialCondition() {

        return false ;
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
        SHARE_PREFERENCE_KEY = "PRODUCT_OUTGOING_KEY" ;
        barCode = findViewById( R.id.bar_code_value ); //条码
        warehouseName = findViewById( R.id.warehouse_name_value ); // 出货仓
        orderId = findViewById( R.id.orderid_value ); //订单号
        volumeSume = findViewById( R.id.volume_sum_value ); //出库总卷数
        weightSum = findViewById( R.id.weight_sum_value ); //出库总量（kg）

        warehouseName.setText("成品仓");
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

            if( TextUtils.isEmpty( orderId.getText().toString() ) ){
                ToastUtil.showLongToast( "请输入订单号" );
                return ;
            }

            DialogUtil.showDeclareDialog( getActivity(),  "确定是否上传记录", v1 -> {
                productionOutgoing(getActivity(), true);
            }).show();
        });

        orderId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        barCode.setOnClickListener(v -> DialogUtil.showInputDialog(getActivity(), barCode.getText().toString(), (dialog, type, position) -> {
            barCode.setText( type );
            dealwithBarcode( type );
            dialog.dismiss();
        }));

        loadFromCache( ProductStateEnums.OUTGOING );

    }

    @Override
    protected void loadFromCache(ProductStateEnums state) {
            Object object = SharedPreferenceUtil.getObject( SHARE_PREFERENCE_KEY );
            if( object instanceof ArrayList){
                ArrayList<String> codesArray = (ArrayList<String>) object;
//            cachedBarcodes.addAll( codesArray );
//            String[] codes = codesArray.toArray(new String[codesArray.size()]);
                productionBalingArrayLog( getActivity(), true, codesArray, state );
            }
    }

    @Override
    protected void setProductionLogDto(ProductionLogDto productInfo) {

        totalWeight = null ;
        //这里对所有产品的卷数相加
        for(int i = 0 ; i < productInfos.size() ; i ++ ){
            ProductionLogDto info = productInfos.get(i);
            if( info == null ) continue;
//            info.volume = ;
            if (info.netWeight != null) {//重量相加
                if (totalWeight == null) {
                    totalWeight = info.netWeight;
                } else {
                    totalWeight = totalWeight.add(info.netWeight).setScale(2, BigDecimal.ROUND_HALF_UP);
                }
            }
        }
//        barCode.setText( productInfo.barCode );
        if( totalWeight != null ){
            weightSum.setText( totalWeight.toString() );
        }else{
            weightSum.setText( "" );
        }
        if( productInfos.size() > 0 ){
            volumeSume.setText( productInfos.size() + "" );
        }else{
            volumeSume.setText( "" );
        }
    }

    protected void setBalingInfo(Baling baling) {

        if( baling.list == null || baling.list.size() == 0 ) return ;



        orderId.setText( baling.list.get(0).ordersCode + "" );
        barCode.setText( baling.barCode );
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
    public void balingTotal(Activity activity, boolean showProgressDialog, String code, ProductStateEnums state){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        Call<BalingTotalQueryRespone> productQueryResponeCall = postRequestInterface.balingTotal(code);

        productQueryResponeCall
                .enqueue(new BaseCallBack<BalingTotalQueryRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<BalingTotalQueryRespone> call, Response<BalingTotalQueryRespone> response) {
                        super.onResponse(call, response);
                        BalingTotalQueryRespone body = response.body();
                        if( body != null && response.code() == 200 ){
                            if( body.barCodes != null && body.barCodes.size() > 0 ){
                                productionBalingArrayLog( getActivity(), true, body.barCodes, state );
                            }else{
                                ToastUtil.showShortToast( "产品码数组为空" );
                            }
                        }if( response.code() == 401 ){
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

    /**
     * 产品信息
     * @param activity
     * @param showProgressDialog
     * @param code
     */
    public void productionBalingLog(Activity activity, boolean showProgressDialog, String code, ProductStateEnums state){

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
                            cacheBarcodes.add( code );
//                            int totalElements = body.totalElements;
                            Baling[] content = body.content;
                            if(/* totalElements == content.length && */content.length > 0 ){
                                for( int i = 0 ; i < content.length ; i ++ ){
                                    Baling baling = content[i];
                                    uploadIds.add( baling.id );
                                    setBalingInfo( baling );
                                    if( baling.amount != null ) {
                                        volumeSume.setText(baling.amount.toString());
                                    }
                                    if( baling.netWeight != null ) {
                                        weightSum.setText(baling.netWeight.toString());
                                    }

                                    for( int j = 0 ; j < baling.list.size() ; j ++ ) {
                                        ProductionLogDto info = baling.list.get( j );
                                        if( info == null ) continue;
                                        if( state == ProductStateEnums.INCOMING && info.productionState == 1 ){ //产品进仓
                                            ToastUtil.showShortToast( "条码为" + info.barCode + "的产品已进仓");
                                            continue;
                                        }else if(state == ProductStateEnums.OUTGOING && info.productionState == 2 ){
                                            ToastUtil.showShortToast( "条码为" + info.barCode + "的产品已出仓");
                                            continue;
                                        }
                                        deleteOperators.add("delete");
                                        productInfos.add(info);
                                    }

                                }

                                if( productInfos.size() > 0 ) {

                                    if (!isInit) {
                                        pl_root.setAdapter(adapter);
                                        isInit = true;
                                    } else {
                                        adapter.notifyDataSetChanged();
                                     }
                                    totalCount.setText("合计：" + productInfos.size() + "件");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        List<String> barcodes = cacheBarcodes ;
        if( barcodes.size() > 0 ) {
            SharedPreferenceUtil.putObject(SHARE_PREFERENCE_KEY, barcodes );
        }else{
            SharedPreferenceUtil.remove( SHARE_PREFERENCE_KEY );
        }
    }

    /**
     * 产品信息
     * @param activity
     * @param showProgressDialog
     * @param code
     */
    public void productionBalingArrayLog(Activity activity, boolean showProgressDialog, List<String> code, ProductStateEnums state){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        cacheBarcodes.addAll( code );
        ProductArrayLogRequest request = new ProductArrayLogRequest();
        request.barcodes = code ;

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        Call<Baling[]> productQueryResponeCall = postRequestInterface.outBalingArray( request );
        productQueryResponeCall
                .enqueue(new BaseCallBack<Baling[]>(activity, manager) {
                    @Override
                    public void onResponse(Call<Baling[]> call, Response<Baling[]> response) {
                        super.onResponse(call, response);
                        Baling[] body = response.body();
                        if( body != null && response.code() == 200 ){
//                            int totalElements = body.totalElements;
                            Baling[] content = body;
                            if(/* totalElements == content.length && */content.length > 0 ){
                                for( int i = 0 ; i < content.length ; i ++ ){
                                    Baling baling = content[i];
                                    if( uploadIds.contains( baling ) ) continue ;
                                    uploadIds.add( baling.id );
                                    setBalingInfo( baling );
                                    if( baling.amount != null ) {
                                        volumeSume.setText(baling.amount.toString());
                                    }
                                    if( baling.netWeight != null ) {
                                        weightSum.setText(baling.netWeight.toString());
                                    }

                                    if( baling.list != null ) {
                                        for (int j = 0; j < baling.list.size(); j++) {
                                            ProductionLogDto info = baling.list.get(j);

                                            if( productInfos.contains( info ) ) continue;

                                            if( state == ProductStateEnums.INCOMING && info.productionState == 1 ){ //产品进仓
                                                ToastUtil.showShortToast( "条码为" + info.barCode + "的产品已进仓");
                                                continue;
                                            }else if(state == ProductStateEnums.OUTGOING && info.productionState == 2 ){
                                                ToastUtil.showShortToast( "条码为" + info.barCode + "的产品已出仓");
                                                continue;
                                            }
                                            deleteOperators.add("delete");
                                            productInfos.add(info);
                                        }
                                    }
                                }

                                if( productInfos.size() > 0 ) {

                                    if (!isInit) {
                                        pl_root.setAdapter(adapter);
                                        isInit = true;
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }
                                    totalCount.setText("合计：" + productInfos.size() + "件");
                                    setProductionLogDto(productInfos.get(productInfos.size() - 1));
                                }
                            }else{
                                ToastUtil.showShortToast( "不能识别该产品" );
                                cacheBarcodes.clear();
                            }
                        }else if( response.code() == 400 ){
                            ToastUtil.showShortToast( "不能识别该产品" );
                            cacheBarcodes.clear();
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

    /**
     * 产品出仓
     * @param activity
     * @param showProgressDialog
     */
    public void productionOutgoing(Activity activity, boolean showProgressDialog ){

        WarehouseVo vo = new WarehouseVo();
        vo.ids = uploadIds ;

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
                            cacheBarcodes.clear();

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
