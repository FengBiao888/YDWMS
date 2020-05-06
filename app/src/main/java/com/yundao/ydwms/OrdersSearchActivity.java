package com.yundao.ydwms;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yundao.ydwms.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.common.base.ImmersiveBaseActivity;
import com.yundao.ydwms.common.listmodule.SimpleListAdapter;
import com.yundao.ydwms.common.listmodule.listitems.AbsListItem;
import com.yundao.ydwms.common.listmodule.listitems.EditItemSimpleShow;
import com.yundao.ydwms.protocal.respone.OrderQueryRespone;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

public class OrdersSearchActivity extends ImmersiveBaseActivity implements AdapterView.OnItemClickListener {

    public static final int SEARCH_TYPE_OUT = 111 ; //外出考勤
    public static final int SEARCH_TYPE_RULE = 112 ; //新增考勤规则

    ListView listView ;
    SearchToolBar searchToolBar ;

    private List<AbsListItem> itemList = new ArrayList<>();
    private SimpleListAdapter simpleAdapter ;
    private double lat ;
    private double lng ;
    private String searchButtonText ;
    private int searchType ;


    @Override
    protected void setTitleBar() {
        searchToolBar = (SearchToolBar) titleBar;
        searchToolBar.setTitleMainText( TextUtils.isEmpty( searchButtonText ) ? "搜索" : searchButtonText/*"可选300米范围之内的地点"*/  )
                .setRightTitleClickListener( v -> {
                    onBackPressed();
                });
        searchToolBar.setOnTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( s != null && s.toString().length() > 0 ) {
                    ordersQuery(getActivity(), true, s.toString() );
                }else{
                    itemList.clear();
                    simpleAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        lat = intent.getDoubleExtra( "loclat", 0l);
        lng = intent.getDoubleExtra( "loclng", 0l);
        searchButtonText = intent.getStringExtra( "searchButtonText" );
        searchType = intent.getIntExtra( "searchType", -1 );
    }

    @Override
    public void initView(Bundle var1) {
        listView = findViewById( R.id.list_view );
        listView.setOnItemClickListener( OrdersSearchActivity.this );
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_orders_search;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * 查询订单号
     * @param activity
     * @param showProgressDialog
     */
    public void ordersQuery(Activity activity, boolean showProgressDialog, String codeValue ){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.ordersQuery( codeValue )
                .enqueue(new BaseCallBack<OrderQueryRespone.OrdersInfo[]>(activity, manager) {
                    @Override
                    public void onResponse(Call<OrderQueryRespone.OrdersInfo[]> call, Response<OrderQueryRespone.OrdersInfo[]> response) {
                        super.onResponse(call, response);
                        if( response.code() == 200 || response.code() == 204 ){
                            if(response.body() != null) {
                                OrderQueryRespone.OrdersInfo[] content = response.body();
                                if (content != null) {
                                    itemList.clear();
                                    for(OrderQueryRespone.OrdersInfo info : content){
                                        EditItemSimpleShow oneTextView = new EditItemSimpleShow(getActivity(), info.code, false, "");
                                        oneTextView.setBgDrawable( new ColorDrawable( Color.WHITE ) );
                                        oneTextView.setClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent();
                                                intent.putExtra("searchResult", info.code);
                                                setResult(RESULT_OK, intent);
                                                finish();
                                            }
                                        });
                                        itemList.add( oneTextView );
                                    }

                                    if( simpleAdapter == null ) {
                                        simpleAdapter = new SimpleListAdapter(getActivity(), itemList);
                                    }else{
                                        simpleAdapter.addDataList( itemList, true );
                                    }
                                    listView.setAdapter( simpleAdapter );
                                }
                            }

                        }else if( response.code() == 401 ){
                            ToastUtil.showShortToast( "登录过期，请重新登录" );
                            AvoidOnResult avoidOnResult = new AvoidOnResult( getActivity() );
                            Intent intent = new Intent( getActivity(), LoginActivity.class );
                            avoidOnResult.startForResult(intent, new AvoidOnResult.Callback() {
                                @Override
                                public void onActivityResult(int requestCode, int resultCode, Intent data) {

                                }
                            });
                        }
                    }

                });

    }

}
