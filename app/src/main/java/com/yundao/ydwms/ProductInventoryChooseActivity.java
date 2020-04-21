package com.yundao.ydwms;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.yundao.ydwms.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.common.base.BaseAbsListItemActivity;
import com.yundao.ydwms.common.datapicker.CustomDatePicker;
import com.yundao.ydwms.common.datapicker.DateFormatUtils;
import com.yundao.ydwms.common.listmodule.listitems.AbsEditItem;
import com.yundao.ydwms.common.listmodule.listitems.AbsListItem;
import com.yundao.ydwms.common.listmodule.listitems.BlankItem;
import com.yundao.ydwms.common.listmodule.listitems.EditItemPick;
import com.yundao.ydwms.protocal.respone.CheckedMonthRespone;
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

public class ProductInventoryChooseActivity extends BaseAbsListItemActivity {

    Button bottomSubmit ;

    EditItemPick wareHouseType ;
    EditItemPick inventoryDate ;


    @Override
    protected void setTitleBar() {
        titleBar.setTitleMainText( "盘点" );
    }

    @Override
    protected int getLayout() {
        return R.layout.layout_listview_with_bottombtn_with_titlebar ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = findViewById( R.id.list_view );
        listView.setOnItemClickListener(this);
        listView.setAdapter(simpleListAdapter);
        listView.setBackgroundColor(getResources().getColor(com.yundao.ydwms.common.R.color.login_bg));
    }

    @Override
    public void initView(Bundle var1) {

        bottomSubmit = findViewById( R.id.bottom_submit );
        bottomSubmit.setText( "确定" );
        bottomSubmit.setOnClickListener( v -> {
            if(TextUtils.isEmpty( wareHouseType.getInputMessage() ) ){
                ToastUtil.showShortToast( "请选择仓库类型" );
                return ;
            }
            if(TextUtils.isEmpty( inventoryDate.getInputMessage() ) ){
                ToastUtil.showShortToast( "盘点日期为空" );
                return ;
            }

            Intent intent = new Intent(getActivity(), ProductInventoryActivity.class);
            intent.putExtra( "warehouse_type", Integer.parseInt( wareHouseType.getExtraParams() ) );
            intent.putExtra( "checkMonthId", Long.parseLong( inventoryDate.getExtraParams() ) );
            intent.putExtra( "pickScanType", ScanTypeEnum.PRODUCT_INVENTORY );
            startActivity( intent );

        } );
    }


    @Override
    public List<? extends AbsListItem> getItemList() {

        List<AbsListItem> list = new ArrayList<>() ;

        list.add( new BlankItem( getActivity(), 16, true ) );
        wareHouseType = new EditItemPick( getActivity(), "仓库类型", true, "请选择");
        list.add( wareHouseType );

        inventoryDate = new EditItemPick( getActivity(), "盘点日期", true, "请选择");
        inventoryDate.setEnabled( false );
        list.add( inventoryDate );

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
//        Date dBefore = new Date();
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.MONTH,-1);//往上推一天  30推三十天  365推一年
//        dBefore = calendar.getTime();
//
//        inventoryDate.setInputMessage( sdf.format( dBefore ) );

        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object obj = absListItems.get( position );
        if( obj == wareHouseType ){
            warehouse(getActivity(), true);
        }/*else if ( obj == inventoryDate ){
            shDatePickDialog( getActivity(), inventoryDate, true, false);
        }*/
    }

    public static void shDatePickDialog(Context context, final AbsEditItem item, final boolean canLaterThenNow, boolean canShowDay) {

        long beginTimestamp = DateFormatUtils.str2Long("1940-01-01", false);
        long endTimestamp = System.currentTimeMillis() ;

        CustomDatePicker mDatePicker = new CustomDatePicker(context, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(Object obj, long timestamp) {
                if( !canLaterThenNow ){
                    if( timestamp > System.currentTimeMillis() ){
                        ToastUtil.showShortToast( item.getTypeName() + "不能比现在晚!" );
                        return ;
                    }
                }
                String inputMessage = DateFormatUtils.long2Str(timestamp, false);
                if( ! canShowDay ){
                    inputMessage = inputMessage.substring( 0, inputMessage.lastIndexOf( "-") );
                }
                item.setInputMessage( inputMessage );

            }
        }, beginTimestamp, endTimestamp );

        String inputMessage = item.getInputMessage();
        if( ! TextUtils.isEmpty( inputMessage ) && inputMessage.length() == 7 ){//只有月份，加上01日
            inputMessage += "-01" ;
        }
        // 不允许点击屏幕或物理返回键关闭
        mDatePicker.setCancelable(false);
        //不显示日
        mDatePicker.setCanShowDay( canShowDay );
        // 不显示时和分
        mDatePicker.setCanShowPreciseTime(false);
        // 不允许循环滚动
        mDatePicker.setScrollLoop(false);
        // 不允许滚动动画
        mDatePicker.setCanShowAnim(false);

        String defualtTime = TextUtils.isEmpty(inputMessage) ? DateFormatUtils.long2Str( System.currentTimeMillis(), false ) : inputMessage;
        mDatePicker.show( item, defualtTime );
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
                                String[] warehouse = new String[warehouseInfos.length];
                                for (int i = 0; i < warehouseInfos.length; i++) {
                                    warehouse[i] = warehouseInfos[i].name;
                                }
                                DialogUtil.showTypeDialog(getActivity(), "请选择仓库类型", warehouse, (dialog, type, position1) -> {
                                    wareHouseType.setInputMessage(type);
                                    wareHouseType.setExtraParams(position1 + "");
                                    simpleListAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                    monthIsChecked( getActivity(), true );
                                });
                            }
                        }
                    }
                });

    }

    public void monthIsChecked(Activity activity, boolean showProgressDialog){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.productionCheckedMonth( wareHouseType.getInputMessage(), 0 )
                .enqueue(new BaseCallBack<CheckedMonthRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<CheckedMonthRespone> call, Response<CheckedMonthRespone> response) {
                        super.onResponse(call, response);
                        if( response.code() == 200 ){//未盘点

                            CheckedMonthRespone body = response.body();
                            if( body != null && body.content != null ){
                                for( int i = 0 ; i < body.content.length ; i ++ ){
                                    String period = body.content[i].period;
                                    inventoryDate.setInputMessage( period );
                                    inventoryDate.setExtraParams( body.content[i].id + "" );
                                    simpleListAdapter.notifyDataSetChanged();
                                }
                            }

                        }else if( response.code() == 401 ){
                            ToastUtil.showShortToast( "登录过期，请重新登录" );
                            AvoidOnResult avoidOnResult = new AvoidOnResult( getActivity() );
                            Intent intent = new Intent( getActivity(), LoginActivity.class );
                            avoidOnResult.startForResult(intent, new AvoidOnResult.Callback() {
                                @Override
                                public void onActivityResult(int requestCode, int resultCode, Intent data) {
                                    if( resultCode == Activity.RESULT_OK ){

                                        monthIsChecked( getActivity(), true );
                                    }
                                }
                            });
                        }else{
                            Dialog dialog = DialogUtil.showDeclareDialog(activity, "上月已盘点", v -> {
                            });
                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finish();
                                }
                            });
                        }
                    }
                });

    }
}
