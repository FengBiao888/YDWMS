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
import com.nf.android.common.base.ImmersiveBaseActivity;
import com.yundao.ydwms.print.PrintJobMonitorService;
import com.yundao.ydwms.protocal.ProductInfo;
import com.yundao.ydwms.protocal.request.PackeResourse;
import com.yundao.ydwms.protocal.request.ProductionVo;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.ProductQueryRespone;
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

public class ProductPackagingActivity extends ImmersiveBaseActivity {

    private final static String SCAN_ACTION = ScanManager.ACTION_DECODE;//default action

//    private String[] codes = new String[]{ "15774952757321", "15774952757331","15774952757341","15774952757351" };
    private String[] codes = new String[]{ "15774952757331", "15774952757351" };
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

    private PrintManager mgr = null;
    private WebView wv = null;
    PrintJob print ;

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
    private String printFilePath;

    @Override
    protected void setTitleBar() {
        titleBar.setTitleMainText( ( (ScanTypeEnum)anEnum ).getCodeName() );
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

        printFilePath = Environment.getExternalStorageDirectory() + "/print.html";
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        initRoomData();

        pl_root = findViewById( R.id.id_pl_root ) ;
        lv_content = findViewById( R.id.id_lv_content ) ;

        mgr = (PrintManager) getSystemService(PRINT_SERVICE);

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
            DialogUtil.showDeclareDialog( getActivity(),  "确定是否上传记录", v1 -> {
                if( anEnum.equals( ScanTypeEnum.PRODUCT_INCOMING  ) ) {
                    productionIncoming(getActivity(), true);
                }else if( anEnum.equals( ScanTypeEnum.WAREHOUSE_CHANGING  ) ){
                    changeWarehousePositon( getActivity(), true, remarkValue.getText().toString() );
                }else if( anEnum.equals( ScanTypeEnum.PRODUCT_MACHINING  ) ){
                    productionMachining( getActivity(), true );
                }else if( anEnum.equals( ScanTypeEnum.PRODUCT_OUTGOING  ) ){
                    productionOutgoing( getActivity(), true );
                } else if( anEnum.equals( ScanTypeEnum.PRODUCT_PACKAGING ) ){
                    if( codeIndex < codes.length ) {
                        productionLog(getActivity(), true, codes[codeIndex]);
                    }else{
                        PackeResourse resourse = new PackeResourse();
                        long timeMillis = System.currentTimeMillis();
                        resourse.barCode = timeMillis + "2" ;
                        resourse.dateline = timeMillis ;
                        int totalLength = 0 ;
                        BigDecimal totalWeight = null ;
                        for( int i = 0 ; i < roomList.size() ; i ++ ){
                            ProductInfo productInfo = roomList.get(i);
                            if( TextUtils.isEmpty( resourse.trayNumber ) && ! TextUtils.isEmpty( productInfo.trayNumber ) ){
                                resourse.trayNumber = productInfo.trayNumber ;
                            }
                            if( TextUtils.isEmpty( resourse.customerId ) && ! TextUtils.isEmpty( productInfo.customerId ) ){
                                resourse.customerId = productInfo.customerId ;
                            }
                            if( TextUtils.isEmpty( resourse.customerName ) && ! TextUtils.isEmpty( productInfo.customerAbbreviation ) ){
                                resourse.customerName = productInfo.customerAbbreviation ;
                            }
                            if( TextUtils.isEmpty( resourse.materielCode ) && ! TextUtils.isEmpty( productInfo.materielCode ) ){
                                resourse.materielCode = productInfo.materielCode ;
                            }
                            if( TextUtils.isEmpty( resourse.materielModel ) && ! TextUtils.isEmpty( productInfo.materielModel ) ){
                                resourse.materielModel = productInfo.materielModel ;
                            }
                            if( TextUtils.isEmpty( resourse.materielName ) && ! TextUtils.isEmpty( productInfo.materielName ) ){
                                resourse.materielName = productInfo.materielName ;
                            }
                            totalLength += productInfo.length ;

                            if( productInfo.netWeight != null ) {
                                if( totalWeight == null ){
                                    totalWeight = productInfo.netWeight ;
                                }else {
                                    totalWeight.add(productInfo.netWeight).setScale(2, BigDecimal.ROUND_HALF_UP);
                                }
                            }
                        }
                        resourse.meter = totalLength + "" ;
                        resourse.number = roomList.size() ;
                        resourse.netWeight = totalWeight ;
                        resourse.productionLogs = roomList ;
                        baling( getActivity(), true, resourse );
                    }
                }else if( codeIndex < codes.length ) {
                    productionLog(getActivity(), true, codes[codeIndex]);
                }
            } )
            .show();
//            productionLog( getActivity(), true, codes[codeIndex] );
        } );

        if( anEnum.equals( ScanTypeEnum.PRODUCT_INVENTORY )
                || anEnum.equals( ScanTypeEnum.WAREHOUSE_CHANGING )
                || anEnum.equals( ScanTypeEnum.PRODUCT_INCOMING  )
                ){
            remark.setText( "仓位" );
            if( anEnum.equals( ScanTypeEnum.WAREHOUSE_CHANGING ) ){
                remarkValue.setText( "15774952757123" );
            }else if( anEnum.equals( ScanTypeEnum.PRODUCT_INVENTORY ) ){
                monthIsChecked( getActivity(), true );
                remarkValue.setText( "15774952757123" );
            }
        }

        if( anEnum.equals( ScanTypeEnum.PRODUCT_PACKAGING ) ){
            submit.setText( "产品打包" );
//            15774952757321，15774952757331，15774952757341，15774952757351
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
                                    isInit = true ;
                                }else{
                                    adapter.notifyDataSetChanged();
                                }
                                codeIndex ++ ;

                                if( anEnum.equals( ScanTypeEnum.PRODUCT_INVENTORY ) ){
                                    pdaCheck( activity, true, content[0] );
                                }
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
//        list.add( "15774952757341" );

        ProductionVo vo = new ProductionVo();
        vo.codes = list ;

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
                            ToastUtil.showShortToast( "进仓成功" );
                        }
                    }

                });

    }

    //15774952757321，15774952757331，15774952757341，15774952757351
    public void productionOutgoing(Activity activity, boolean showProgressDialog ){

        List<String> list = new ArrayList<>();
        list.add( "15774952757321" );
        list.add( "15774952757331" );
//        list.add( "15774952757341" );

        ProductionVo vo = new ProductionVo();
        vo.codes = list ;

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
                        }
                    }

                });

    }

    //15774952757321，15774952757331，15774952757341，15774952757351
    public void productionMachining(Activity activity, boolean showProgressDialog ){

        List<String> list = new ArrayList<>();
        list.add( "15774952757321" );
        list.add( "15774952757331" );
//        list.add( "15774952757341" );

        ProductionVo vo = new ProductionVo();
        vo.codes = list ;

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.productionMachining( vo )
                .enqueue(new BaseCallBack<BaseRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<BaseRespone> call, Response<BaseRespone> response) {
                        super.onResponse(call, response);
                        if( response.code() == 200 || response.code() == 204 ){
                            ToastUtil.showShortToast( "加工成功" );
                        }
                    }

                });

    }

    //15774952757321，15774952757331，15774952757341，15774952757351
    public void changeWarehousePositon(Activity activity, boolean showProgressDialog, String wearhouseCode ){

        List<String> list = new ArrayList<>();
        list.add( "15774952757321" );
        list.add( "15774952757331" );
//        list.add( "15774952757341" );

        ProductionVo vo = new ProductionVo();
//        vo.codes = list ;
        vo.code = "15774952757321" ;
        vo.warehouseCode = wearhouseCode;

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.changeWarehousePositon( vo )
                .enqueue(new BaseCallBack<BaseRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<BaseRespone> call, Response<BaseRespone> response) {
                        super.onResponse(call, response);
                        if( response.code() == 200 || response.code() == 204 ){
                            ToastUtil.showShortToast( "仓位变更成功" );
                        }
                    }

                });

    }

    public void baling(Activity activity, boolean showProgressDialog, PackeResourse vo){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.baling( vo )
                .enqueue(new BaseCallBack<PackeResourse>(activity, manager) {
                    @Override
                    public void onResponse(Call<PackeResourse> call, Response<PackeResourse> response) {
                        super.onResponse(call, response);
                        if( response.code() == 201 ){
                            ToastUtil.showShortToast( "打包成功" );
                            writeHtml( vo );
                            printReport( "123123123123" );
                        }
                    }
                });

    }

    public void monthIsChecked(Activity activity, boolean showProgressDialog){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.monthIsChecked( )
                .enqueue(new BaseCallBack<BaseRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<BaseRespone> call, Response<BaseRespone> response) {
                        super.onResponse(call, response);
                        if( response.code() == 400 ){//未盘点
                            Dialog dialog = DialogUtil.showDeclareDialog(activity, "上月未消盘点", false, v -> {} );
                            dialog.show();
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

    public void pdaCheck(Activity activity, boolean showProgressDialog, ProductInfo info){

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        info.classify = 1 ;
        postRequestInterface.pdaCheck( info )
                .enqueue(new BaseCallBack<BaseRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<BaseRespone> call, Response<BaseRespone> response) {
                        super.onResponse(call, response);
                        if ( response.code() == 400  ){
                            ToastUtil.showShortToast( "盘点失败" );
                        }
                    }
                });

    }

    private void printReport(String barcodeStr) {
//        Template tmpl =
//                Mustache.compiler().compile(getString(R.string.report_body));
        WebView print = prepPrintWebView( "packaging", barcodeStr );
//        print.loadData( tmpl.execute(new TpsReportContext(prose.getText().toString()) ),
//                "text/html; charset=UTF-8", null);

        print.loadUrl("file://" + printFilePath);
    }

    private void writeHtml(PackeResourse vo){

        File dataFile = new File(Environment.getExternalStorageDirectory(), "print.html");
        try {
            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(dataFile);
            fos.write( htmltext( vo ).getBytes( "utf-8") );
            fos.flush();
            fos.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String htmltext(PackeResourse vo){
        StringBuffer buffer = new StringBuffer( );
        buffer.append("<style type=\"text/css\">\n" +
                "    table{ border-collapse: collapse; }\n" +
                "    table,table tr td { border:1px solid #000; }\n" +
                "    table tr td{ padding: 5px 10px; }\n" +
                "    .left { width: 50%; height: 40px; float: left; text-align:left; }\n" +
                "    .right { width: 50%; height: 40px; float: right; text-align:right; }\n" +
                "    .center { margin: auto; }\n" +
                "</style>\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "    <script type=\"text/javascript\">\n" +
                "\t\tfunction onSaveCallback(src){ document.getElementById(\"test\").src = src; }\n" +
                "\t</script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>客户名：").append( vo.customerName ).append("</h1>\n" +
                "<div id=\"middle\">\n" +
                "    <div class=\"left\"><text style=\"font-size:20px\">托盘号：").append( vo.trayNumber ).append("</text> </div>\n" +
                "    <div class=\"right\"><text style=\"font-size:20px\">日 期：").append(DateFormatUtils.long2Str( vo.dateline, false ) ).append("</text></div>\n" +
                "</div>\n" +
                "<table width=\"100%\" border=\"1\">\n" +
                "    <tr>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">规格</text></th>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">编号</text></th>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">重量</text></th>\n" +
                "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">米数</text></th>\n" +
                "    </tr>\n");
                for( int i = 0 ; i < vo.productionLogs.size() ; i ++ ){
                    ProductInfo productInfo = vo.productionLogs.get(i);
                    buffer.append( "    <tr>\n" )
                          .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.materielModel ).append("</text></th>\n" )
                          .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.materielCode ).append("</text></th>\n" )
                          .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.netWeight == null ? "" : productInfo.netWeight.toString() ).append("</text></th>\n" )
                          .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( productInfo.length ).append("</text></th>\n" ) ;
                    buffer.append("    </tr>\n");
                }
                buffer.append( "    <tr>\n" )
                      .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">合计</text></th>\n" )
                      .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( vo.productionLogs.size() ).append("件").append("</text></th>\n" )
                      .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( vo.netWeight == null ? "" : vo.netWeight.toString() ).append("KG").append("</text></th>\n" )
                      .append( "        <td width=\"25%\" style=\"text-align:center\"><text style=\"font-size:20px\">").append( vo.meter ).append("M").append("</text></th>\n" )
                      .append( "    </tr>\n");
                buffer.append("</table>\n" +
                "<div style=\"margin-top:5px\" align=\"center\" ><img id=\"test\" src=\"\" onclick=\"\"/></div>\n" +
                "<div style=\"margin-top:5px\" align=\"center\">123123123123</div>\n" +
                "</body>\n" +
                "</html>").append("\n");
        return buffer.toString() ;
    }

    private WebView prepPrintWebView(final String name, String barcodeStr) {
        WebView result = getWebView();


        result.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if( !url.equals( "file://" + printFilePath ) ) {

                }else{
                    try {
                        String url1 = "javascript:onSaveCallback('data:image/png;base64," + BitmapUtil.bitmaptoString(BitmapUtil.CreateOneDCode(barcodeStr)) + "')";
                        System.out.println("url1: " + url1);
                        view.loadUrl(url1);
                        print = print(name, view.createPrintDocumentAdapter(),
                                new PrintAttributes.Builder().build());

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return (result);
    }


    private WebView getWebView() {
        if (wv == null) {
            wv = new WebView(this);
            WebSettings settings = wv.getSettings();

            //支持JavaScript
            settings.setJavaScriptEnabled(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(false); //支持通过JS打开新窗口

        }

        return (wv);
    }

    private PrintJob print(String name, PrintDocumentAdapter adapter,
                           PrintAttributes attrs) {
        startService(new Intent(this, PrintJobMonitorService.class));

        return (mgr.print(name, adapter, attrs));
    }
}
