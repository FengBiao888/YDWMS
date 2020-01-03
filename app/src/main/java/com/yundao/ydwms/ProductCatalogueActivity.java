package com.yundao.ydwms;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.device.scanner.configuration.PropertyID;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.nf.android.common.avoidonresult.AvoidOnResult;
import com.nf.android.common.base.ImmersiveBaseActivity;
import com.yundao.ydwms.print.PrintJobMonitorService;
import com.yundao.ydwms.protocal.ProductInfo;
import com.yundao.ydwms.protocal.request.PackeResourse;
import com.yundao.ydwms.protocal.request.ProductionVo;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.BitmapUtil;
import com.yundao.ydwms.util.DateFormatUtils;
import com.yundao.ydwms.util.DialogUtil;
import com.yundao.ydwms.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;
import sysu.zyb.panellistlibrary.AbstractPanelListAdapter;
import sysu.zyb.panellistlibrary.PanelListLayout;

public class ProductCatalogueActivity extends ImmersiveBaseActivity {

    private final static String SCAN_ACTION = ScanManager.ACTION_DECODE;//default action

//    @BindView(R.id.id_pl_root)
    PanelListLayout pl_root;
//    @BindView(R.id.id_lv_content)
    ListView lv_content;

    AbstractPanelListAdapter adapter;
    ArrayList<ProductInfo> roomList = new ArrayList<>();

    private boolean dataChanged  = false ;

    List<String> columnDataList = new ArrayList<>();

    @Override
    protected void setTitleBar() {
        titleBar.setTitleMainText( "产品分类" )
            .setRightText( "完成" )
            .setRightTitleClickListener( v -> {
                Intent intent = new Intent();
                intent.putExtra( "productInfoList", roomList );
                setResult( dataChanged ? RESULT_OK : RESULT_CANCELED, intent ) ;
                finish();
            } );
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_product_catalogue ;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        List<ProductInfo> lists = (List<ProductInfo>) intent.getSerializableExtra( "productInfoList" );

        roomList.addAll( lists );
        for( int i = 0 ; i < roomList.size() ; i ++ ){
            columnDataList.add( "delete" );
        }
    }

    @Override
    public void initView(Bundle var1) {

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        pl_root = findViewById( R.id.id_pl_root ) ;
        lv_content = findViewById( R.id.id_lv_content ) ;


        adapter = new ProductionPanelListAdapter(this, pl_root, lv_content, roomList, R.layout.item_room);
        adapter.setTitleWidth( 40 );
        adapter.setTitleHeight( 40 );
        adapter.setRowColor( "#4396FF" );
        adapter.setTitleColor( "#4396FF" );
        adapter.setColumnColor( "#FFFFFF" );
        adapter.setColumnDivider( new ColorDrawable( 0xFFEDEDED ) );
        adapter.setColumnDividerHeight( 1 );
        adapter.setTitleTextColor( "#494BFF" );
        adapter.setRowDataList(generateRowData());
        adapter.setColumnDataList( columnDataList );
        adapter.setColumnAdapter( new ColumnAdapter( columnDataList ) );
        adapter.setTitle("操作");
        pl_root.setAdapter(adapter);

        lv_content.setOnItemClickListener((parent, view, position, id) -> {
        });

    }


    private List<String> generateRowData(){
        List<String> rowDataList = new ArrayList<>();
        rowDataList.add("序号");
        rowDataList.add("条码");
        rowDataList.add("品名");
        rowDataList.add("驳口");
        rowDataList.add("班次");
        rowDataList.add("规格");
        rowDataList.add("包装");
        rowDataList.add("机台");
        rowDataList.add("托盘");
        rowDataList.add("米数");
        rowDataList.add("皮重");
        rowDataList.add("净重");
        rowDataList.add("卷号");
        return rowDataList;
    }

    class ColumnAdapter extends BaseAdapter{

        List<String> roomNumber ;

        ColumnAdapter( List<String> roomNumber ){
            this.roomNumber = roomNumber ;
        }

        @Override
        public int getCount() {
            return roomNumber == null ? 0 : roomNumber.size() ;
        }

        @Override
        public Object getItem(int position) {
            return roomNumber == null ? null : roomNumber.get( position );
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
                   roomList.remove( position );
                   columnDataList.remove( position );
                   dataChanged = true ;
                   adapter.notifyDataSetChanged();

                }).show();
            } );
            return view;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
