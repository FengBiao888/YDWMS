package com.yundao.ydwms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.device.scanner.configuration.PropertyID;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nf.android.common.base.ImmersiveBaseActivity;
import com.yundao.ydwms.protocal.ProductInfo;
import com.yundao.ydwms.protocal.request.LoginRequest;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.DialogUtil;
import com.yundao.ydwms.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import sysu.zyb.panellistlibrary.AbstractPanelListAdapter;
import sysu.zyb.panellistlibrary.PanelListLayout;

public class ProductPackagingActivity extends ImmersiveBaseActivity {

    private final static String SCAN_ACTION = ScanManager.ACTION_DECODE;//default action

    private String[] codes = new String[]{ "15774952757321", "15774952757331","15774952757341","15774952757351" };
    private int codeIndex = 0 ;
//    @BindView(R.id.id_pl_root)
    PanelListLayout pl_root;
//    @BindView(R.id.id_lv_content)
    ListView lv_content;
    @BindView( R.id.confirm )
    Button submit ;
    @BindView( R.id.operator )
    TextView operator ;
    @BindView( R.id.state )
    TextView totalCount ;
    @BindView( R.id.bar_code_value )
    EditText barCode ;
    @BindView( R.id.material_value )
    EditText material ;
    @BindView( R.id.product_name_value )
    EditText productName ;
    @BindView( R.id.specificationl_value )
    EditText specification ;
    @BindView( R.id.number_value )
    EditText number ;
    @BindView( R.id.pack_value )
    EditText pack ;
    @BindView( R.id.remark )
    TextView remark ;
    @BindView( R.id.remark_value )
    EditText remarkValue ;


    AbstractPanelListAdapter adapter;
    List<ProductInfo> roomList = new ArrayList<>();

    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid;
    private String barcodeStr;
    private boolean isScaning = false;
    private boolean isInit = false ;

    List<String> columnDataList = new ArrayList<>();

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            isScaning = false;
            soundpool.play(soundid, 1, 1, 0, 0, 1);
            barCode.setText("");
            mVibrator.vibrate(100);

            byte[] barcode = intent.getByteArrayExtra(ScanManager.DECODE_DATA_TAG);
            int barcodelen = intent.getIntExtra(ScanManager.BARCODE_LENGTH_TAG, 0);
            byte temp = intent.getByteExtra(ScanManager.BARCODE_TYPE_TAG, (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);
            barcodeStr = new String(barcode, 0, barcodelen);
            barCode.append(" length："  +barcodelen);
            barCode.append(" barcode："  +barcodeStr);
            //showScanResult.setText(barcodeStr);

        }

    };
    private Enum anEnum ;

    @Override
    protected void setTitleBar() {
        titleBar.setTitleMainText( "产品打包" );
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_product_packaging ;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        anEnum = (Enum) intent.getSerializableExtra( "pickScanType" );
    }

    @Override
    public void initView(Bundle var1) {

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        initRoomData();

        pl_root = findViewById( R.id.id_pl_root ) ;
        lv_content = findViewById( R.id.id_lv_content ) ;


        adapter = new RoomPanelListAdapter(this, pl_root, lv_content, roomList, R.layout.item_room);
        adapter.setTitleWidth( 40 );
        adapter.setTitleHeight( 40 );
        adapter.setRowColor( "#4396FF" );
        adapter.setTitleColor( "#4396FF" );
        adapter.setColumnColor( "#FFFFFF" );
        adapter.setColumnDivider( new ColorDrawable( 0xFFEDEDED ) );
        adapter.setColumnDividerHeight( 1 );
        adapter.setTitleTextColor( "#494BFF" );
//        adapter.setRowDivider( new ColorDrawable( 0x494BFF ) );
//        adapter.setColumnDivider( new ColorDrawable( 0x494BFF ) );
        adapter.setRowDataList(generateRowData());
        adapter.setColumnDataList( columnDataList );
        adapter.setColumnAdapter( new ColumnAdapter( columnDataList ) );
        adapter.setTitle("操作");
//        adapter.setRowColor("#0288d1");
//        adapter.setColumnColor("#81d4fa");


        submit.setOnClickListener( v->{
//            DialogUtil.showDeclareDialog( getActivity(),  "确定是否上传记录", v1 -> {
//                productionIncoming( getActivity(), true );
//            } )
//            .show();
            productionLog( getActivity(), true, codes[codeIndex] );
        } );

        if( anEnum.equals( ScanTypeEnum.PRODUCT_INVENTORY )
                || anEnum.equals( ScanTypeEnum.WAREHOUSE_CHANGING )

                ){
            remark.setText( "仓位" );
        }

        if( anEnum.equals( ScanTypeEnum.PRODUCT_PACKAGING ) ){
            submit.setText( "产品打包" );
        }
    }

    /**
     * 初始化房间列表和房间信息
     */
    private void initRoomData() {
//        for (int i = 201;i<221;i++){
//            Room room = new Room(i);
//            room.setRoomDetail(Utility.generateRandomRoomDetail());
//            roomList.add(room);
//        }
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

    private List<String> generateColumnData(){
//        for (Room room : roomList){
//            columnDataList.add(String.valueOf(room.getRoomNo()));
//        }
        return columnDataList;
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
                ToastUtil.showLongToast( "删除" + getItem( position ) );
            } );
            return view;
        }
    }

    private void initScan() {
        // TODO Auto-generated method stub
        mScanManager = new ScanManager();
        mScanManager.openScanner();

        mScanManager.switchOutputMode( 0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initScan();
        barCode.setText("");
        IntentFilter filter = new IntentFilter();
        int[] idbuf = new int[]{PropertyID.WEDGE_INTENT_ACTION_NAME, PropertyID.WEDGE_INTENT_DATA_STRING_TAG};
        String[] value_buf = mScanManager.getParameterString(idbuf);
        if(value_buf != null && value_buf[0] != null && !value_buf[0].equals("")) {
            filter.addAction(value_buf[0]);
        } else {
            filter.addAction(SCAN_ACTION);
        }

        registerReceiver(mScanReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mScanManager != null) {
            mScanManager.stopDecode();
            isScaning = false;
        }
        unregisterReceiver(mScanReceiver);
    }


    public void productionLog(Activity activity, boolean showProgressDialog, String code){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.productionLog( code )
                .enqueue(new BaseCallBack<ProductQueryRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<ProductQueryRespone> call, Response<ProductQueryRespone> response) {
                        super.onResponse(call, response);
                        ProductQueryRespone body = response.body();
                        if( body != null && response.code() == 200 ){
                            int totalElements = body.totalElements;
                            ProductInfo[] content = body.content;
                            if( totalElements == content.length ){
                                for( int i = 0 ; i < content.length ; i ++ ){
                                    columnDataList.add( "delete" );
                                    roomList.add( content[i] );
                                }
                                if( ! isInit ){
                                    pl_root.setAdapter(adapter);
//                                    isInit = true ;
                                }else{
                                    adapter.notifyDataSetChanged();
                                }
                                codeIndex ++ ;
                            }
                        }
                    }
                });
    }

    //15774952757321，15774952757331，15774952757341，15774952757351
    public void productionIncoming(Activity activity, boolean showProgressDialog ){

        List<String> list = new ArrayList<>();
        list.add( "15774952757321" );
        list.add( "15774952757331" );
        list.add( "15774952757341" );

        String[] aabbcc = new String[]{ "15774952757321","15774952757331", "15774952757341" };
        LoginRequest request = new LoginRequest();


        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        Gson gson = new Gson();
        String json = gson.toJson(list);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.productionIncoming( body )
                .enqueue(new BaseCallBack<BaseRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<BaseRespone> call, Response<BaseRespone> response) {
                        super.onResponse(call, response);
                    }

                });

    }
}
