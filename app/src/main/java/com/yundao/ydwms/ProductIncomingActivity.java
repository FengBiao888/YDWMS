package com.yundao.ydwms;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.yundao.ydwms.LoginActivity;
import com.yundao.ydwms.ProductCatalogueActivity;
import com.yundao.ydwms.R;
import com.yundao.ydwms.ScanProductBaseActivity;
import com.yundao.ydwms.UploadSuccessActivity;
import com.yundao.ydwms.YDWMSApplication;
import com.yundao.ydwms.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.protocal.request.Baling;
import com.yundao.ydwms.protocal.request.BalingRequest;
import com.yundao.ydwms.protocal.request.ProductArrayLogRequest;
import com.yundao.ydwms.protocal.request.WarehouseVo;
import com.yundao.ydwms.protocal.respone.BalingQueryRespone;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ProductIncomingActivity extends ScanProductBaseActivity {

    private int index = 0 ;
    private String[] codes = new String[]{ "15936749193212" };

    public EditText barCode ; //条码
    public EditText material ; //料号
    public EditText productName ; //品名
    public EditText warehouseName ; //仓库名
    public EditText warehousePositon ; //仓位
    public List<Baling> balingList ;
    public int totalSum ;

    @Override
    protected int getLayout() {
        return R.layout.activity_product_incoming ;
    }


    @Override
    public void dealwithBarcode(String barcodeStr) {

        if( barcodeStr.endsWith("2") ){
//            productInfos.clear();
//            clearProductionLogDto();
            List<String> codes = new ArrayList<>();
            codes.add( barcodeStr );
            balingProductionLog( getActivity(), true, codes, ProductStateEnums.OUTGOING );
        }else{
            ToastUtil.showShortToast( "该条码不是打托码" );
        }

//        ProductionLogDto ProductionLogDto = new ProductionLogDto();
//        ProductionLogDto.barCode = barcodeStr ;
//
//        if( productInfos.contains( ProductionLogDto ) ){
//            ToastUtil.showShortToast( "该产品已在列表中" );
//            return ;
//        }
//
//        productionLog( getActivity(), true, barcodeStr, ProductStateEnums.INCOMING, ProductStateEnums.INCOMING );
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
        balingList = new ArrayList<>();
        //产品信息的初始化
        adapter = new ProductionSumPanelListAdapter(this, pl_root, lv_content, balingList, R.layout.item_product_sum_info);
        adapter.setTitleWidth( 40 );
        adapter.setTitleHeight( 40 );
        adapter.setRowColor( "#4396FF" );
        adapter.setTitleColor( "#4396FF" );
        adapter.setColumnColor( "#FFFFFF" );
        adapter.setColumnDivider( new ColorDrawable( 0xFFEDEDED ) );
        adapter.setColumnDividerHeight( 1 );
        adapter.setTitleTextColor( "#494BFF" );
        adapter.setRowDataList(generateRowData());
        adapter.setColumnDataList(deleteOperators);
        adapter.setColumnAdapter( new NewColumnAdapter(deleteOperators) );
        adapter.setTitle("操作");
        //产品内容列表点击事情
        lv_content.setOnItemClickListener((parent, view, position, id) -> {
            Baling productInfo = balingList.get(position);
            clickedPosition = position ;
            setBalingInfo(productInfo);
        });

        System.out.println( "kdkdkdk -> initView"  );
        SHARE_PREFERENCE_KEY = "PRODUCT_INCOMING_KEY" ;
        barCode = findViewById( R.id.bar_code_value ); //条码
        material = findViewById( R.id.material_value ); //料号
        productName = findViewById( R.id.product_name_value ); //品名
        warehouseName = findViewById( R.id.warehouse_name_value ); //仓库名
        warehousePositon = findViewById( R.id.warehouse_position_value ) ; //仓位

        warehouseName.setText( "成品仓" );
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
            dealwithBarcode(type );
            dialog.dismiss();
        }));
//        dealwithBarcode("15844258895641" );

        loadFromCache(ProductStateEnums.INCOMING, ProductStateEnums.OUTGOING);
    }

    @Override
    protected void setProductionLogDto(ProductionLogDto productInfo) {
        barCode.setText( productInfo.barCode );
        material.setText( productInfo.productModel );
        productName.setText( productInfo.productName );

    }

    @Override
    protected void loadFromCache(ProductStateEnums... state) {
        Object object = SharedPreferenceUtil.getObject( SHARE_PREFERENCE_KEY );
        if( object != null ) {
            if (object instanceof ArrayList) {
                ArrayList<String> codesArray = (ArrayList<String>) object;
//            cachedBarcodes.addAll( codesArray );
//            String[] codes = codesArray.toArray(new String[codesArray.size()]);
                if( codesArray != null && codesArray.size() > 0 ) {
                    balingProductionLog(getActivity(), true, codesArray, state);
                }
            }
        }
    }

    protected void setBalingInfo(Baling productInfo) {
        barCode.setText( productInfo.barCode );
        material.setText( productInfo.productModel );
        productName.setText( productInfo.productCode );

    }



    @Override
    protected void clearProductionLogDto() {
        barCode.setText( "" );
        material.setText( "" );
        productName.setText( "" );
    }

    /**
     * 产品信息标题栏
     * @return
     */
    @Override
    protected List<String> generateRowData(){
        List<String> rowDataList = new ArrayList<>();
        rowDataList.add("序号");
        rowDataList.add("条码");
        rowDataList.add("品名");
        rowDataList.add("总净重");
        rowDataList.add("总米数");
        return rowDataList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        List<String> barcodes = new ArrayList<>() ;
        for( int i = 0 ; i < balingList.size() ; i ++ ){
            barcodes.add( balingList.get(i).barCode );
        }
        if( barcodes.size() > 0 ) {
            SharedPreferenceUtil.putObject(SHARE_PREFERENCE_KEY, barcodes );
        }else{
            SharedPreferenceUtil.remove( SHARE_PREFERENCE_KEY );
        }
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
                            productInfos.clear();
                            adapter.notifyDataSetChanged();
                            SharedPreferenceUtil.remove( SHARE_PREFERENCE_KEY );
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
     * 打包的产品信息
     * @param activity
     * @param showProgressDialog
     * @param codes
     */
    public void balingProductionLog(Activity activity, boolean showProgressDialog, List<String> codes, ProductStateEnums... state){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);
        ProductArrayLogRequest request = new ProductArrayLogRequest();

        request.barcodes = codes ;

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        Call<BalingQueryRespone[]> productQueryResponeCall = postRequestInterface.balingProductionLog(request);
        productQueryResponeCall
                .enqueue(new BaseCallBack<BalingQueryRespone[]>(activity, manager) {
                    @Override
                    public void onResponse(Call<BalingQueryRespone[]> call, Response<BalingQueryRespone[]> response) {
                        super.onResponse(call, response);
                        BalingQueryRespone[] body = response.body();
                        if( body != null && response.code() == 200 ){
                            for( int j = 0 ; j < body.length ; j ++ ) {
                                Baling baling = body[j].baling;
                                balingList.add(baling);
                                totalSum += baling.amount ;
                                deleteOperators.add("delete");
                                setBalingInfo(baling);

                                ProductionLogDto[] content = body[j].productionLogs;

                                boolean containInComing = false;
                                boolean containOutgoing = false;
                                if (content.length > 0) {
                                    for (int i = 0; i < content.length; i++) {
                                        ProductionLogDto info = content[i];
                                        if (info == null) continue;
//                                        if (info.balingId != 0) { //产品打包，如果是已打包
////                                        ToastUtil.showShortToast( "该产品已打包");
//                                            containPackaged = true;
//                                            continue;
//                                        }
                                        for( int k = 0 ; k < state.length ; k ++ ) {
                                            if (state[k] == ProductStateEnums.INCOMING && info.productionState == 1) { //产品进仓
//                                        ToastUtil.showShortToast( "条码为" + info.barCode + "的产品已进仓");
                                                containInComing = true;
                                            } else if (state[k] == ProductStateEnums.OUTGOING && info.productionState == 2) {
//                                        ToastUtil.showShortToast( "条码为" + info.barCode + "的产品已出仓");
                                                containOutgoing = true;
                                            }
                                        }
                                        if( containInComing || containOutgoing ){
                                            continue;
                                        }

                                        if( containOutgoing && containInComing ){
                                            ToastUtil.showShortToast( "包含已进仓和已出仓的产品" );
                                        }else if( containOutgoing ){
                                            ToastUtil.showShortToast( "包含已出仓的产品" );
                                        }else if( containInComing ){
                                            ToastUtil.showShortToast( "包含已进仓的产品" );
                                        }
                                        productInfos.add(info);
//                                        setProductionLogDto(info);
                                    }
                                } else {
                                    ToastUtil.showShortToast("不能识别该产品");
                                }
                            }

                            if (balingList.size() > 0) {
                                if (!isInit) {
                                    pl_root.setAdapter(adapter);
                                    isInit = true;
                                } else {
                                    adapter.notifyDataSetChanged();
                                }
                                totalCount.setText("合计：" + totalSum + "件");
                            }


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
                        }else if( response.code() == 400 ){
                            try {
                                ToastUtil.showShortToast( response.errorBody().string() );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            ToastUtil.showShortToast( "错误码:" + response.code()+ ",不能识别该产品" );
                        }
                    }
                });
    }

    /**
     * 根据产品列表，获取一维码集合
     * @return
     */
    @Override
    public List<Long> genCodes(){
        List<Long> list = new ArrayList<>();
        for(int i = 0; i < balingList.size() ; i ++ ){
            list.add( balingList.get(i).id );
        }
        return list ;
    }

    /**
     * 删除操作栏的适配器
     */
    class NewColumnAdapter extends BaseAdapter {

        List<String> deleteList ;

        NewColumnAdapter( List<String> roomNumber ){
            this.deleteList = roomNumber ;
        }

        @Override
        public int getCount() {
            return deleteList == null ? 0 : deleteList.size() ;
        }

        @Override
        public Object getItem(int position) {
            return deleteList == null ? null : deleteList.get( position );
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate( getActivity(), R.layout.layout_delete_icon, null );
            view.setOnClickListener( v->{
                DialogUtil.showDeclareDialog( getActivity(), "确认要删除该条数据吗?", v1 -> {
                    //产品栏删除对应条目录
                    Baling baling = balingList.get( position );
                    try {
                        balingList.remove(position);
                        deleteOperators.remove(position);
//                    cachedBarcodes.remove( position );
                        clearProductionLogDto();
                        totalSum -= baling.amount ;
                        totalCount.setText("合计：" + totalSum + "件");
                        adapter.notifyDataSetChanged();
                    }catch (IndexOutOfBoundsException ex ){
                        ex.printStackTrace();
                    }
                }).show();
            } );
            return view;
        }
    }

}
