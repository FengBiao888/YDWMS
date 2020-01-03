package com.yundao.ydwms;

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

import com.nf.android.common.base.ImmersiveBaseActivity;
import com.yundao.ydwms.protocal.ProductInfo;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.util.DialogUtil;
import com.yundao.ydwms.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import sysu.zyb.panellistlibrary.AbstractPanelListAdapter;
import sysu.zyb.panellistlibrary.PanelListLayout;

public abstract class ProductBaseActivity extends ImmersiveBaseActivity {

    public final static String SCAN_ACTION = ScanManager.ACTION_DECODE;//default action

    @BindView(R.id.id_pl_root)
    public PanelListLayout pl_root;
    @BindView(R.id.id_lv_content)
    public  ListView lv_content;
    @BindView( R.id.confirm )
    public Button submit ;
    @BindView( R.id.operator )
    public TextView operator ;
    @BindView( R.id.state )
    public  TextView totalCount ;
    @BindView( R.id.bar_code_value )
    public EditText barCode ;
    @BindView( R.id.material_value )
    public EditText material ;
    @BindView( R.id.product_name_value )
    public  EditText productName ;
    @BindView( R.id.specificationl_value )
    public EditText specification ;
    @BindView( R.id.number_value )
    public  EditText volume ;
    @BindView( R.id.pack_value )
    public EditText pack ;
    @BindView( R.id.remark )
    public TextView remark ;
    @BindView( R.id.remark_value )
    public EditText remarkValue ;

    public  EditText foucusEditText ;

    AbstractPanelListAdapter adapter;
    ArrayList<ProductInfo> roomList = new ArrayList<>();

    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid;

    List<String> columnDataList = new ArrayList<>();

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            soundpool.play(soundid, 1, 1, 0, 0, 1);
            mVibrator.vibrate(100);

            byte[] barcode = intent.getByteArrayExtra(ScanManager.DECODE_DATA_TAG);
            int barcodelen = intent.getIntExtra(ScanManager.BARCODE_LENGTH_TAG, 0);
            byte temp = intent.getByteExtra(ScanManager.BARCODE_TYPE_TAG, (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);
            String barcodeStr = new String(barcode, 0, barcodelen);
            if( foucusEditText != null ){
                if( foucusEditText == barCode ){ //条码
                    ProductInfo productInfo = new ProductInfo();
                    productInfo.barCode = barcodeStr ;
                    if( roomList.contains( productInfo ) ){
                        ToastUtil.showShortToast( "该产品已在列表中" );
                        return ;
                    }

                    if( !barcodeHasSpecialCondition() ){
                        foucusEditText.setText( barcodeStr );
                        dealwithBarcode( barcodeStr );
                    }
                }else if( foucusEditText == remarkValue && "仓位".equals( remark.getText().toString() ) ){
                    foucusEditText.setText( barcodeStr );
                }
            }

        }

    };

    public abstract void dealwithBarcode(String barcodeStr);

    public abstract boolean barcodeHasSpecialCondition();

    protected Enum anEnum ;

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

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        User user = YDWMSApplication.getInstance().getUser();
        if( user != null ){
            operator.setText( "操作员：" + user.username );
        }

        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if( hasFocus ){
                    foucusEditText = (EditText) v;
                }
            }
        };
        barCode.setOnFocusChangeListener(focusChangeListener);
        remarkValue.setOnFocusChangeListener( focusChangeListener );

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

        lv_content.setOnItemClickListener((parent, view, position, id) -> {
            ProductInfo productInfo = roomList.get(position);
            setProductInfo(productInfo);
        });

    }

    protected void setProductInfo(ProductInfo productInfo) {
        barCode.setText( productInfo.barCode );
        material.setText( productInfo.materielCode );
        productName.setText( productInfo.materielName );
        specification.setText( productInfo.materielModel );
        volume.setText( productInfo.volume );
        pack.setText( productInfo.packing );
        if( "备注".equals( remark.getText() ) ){
            remarkValue.setText( productInfo.remark );
        }
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
                   totalCount.setText( "合计：" + roomList.size() + "件" );
                   adapter.notifyDataSetChanged();

                }).show();
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
        }
        unregisterReceiver(mScanReceiver);
    }

    public List<String> genCodes(){
        List<String> list = new ArrayList<>();
        for( int i = 0 ; i < roomList.size() ; i ++ ){
            list.add( roomList.get(i).barCode );
        }
        return list ;
    }

}
