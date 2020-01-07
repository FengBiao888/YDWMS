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

    public final static String SCAN_ACTION = ScanManager.ACTION_DECODE;//扫码广播的default action

    @BindView(R.id.id_pl_root)
    public PanelListLayout pl_root; //产品信息父Layout
    @BindView(R.id.id_lv_content)
    public  ListView lv_content; //产品信息列表
    @BindView( R.id.confirm )
    public Button submit ; //确定按钮
    @BindView( R.id.operator )
    public TextView operator ; //操作员
    @BindView( R.id.state )
    public TextView totalCount ; //总条数
    @BindView( R.id.bar_code_value )
    public EditText barCode ; //条码
    @BindView( R.id.material_value )
    public EditText material ; //料号
    @BindView( R.id.product_name_value )
    public  EditText productName ; //品名
    @BindView( R.id.specificationl_value )
    public EditText specification ; //规格
    @BindView( R.id.number_value )
    public  EditText volume ; //卷号
    @BindView( R.id.pack_value )
    public EditText pack ; //包装
    @BindView( R.id.remark )
    public TextView remark ; //备注或者是仓位的文字描述
    @BindView( R.id.remark_value )
    public EditText remarkValue ; //备注或者是仓位的输入内容

    public  EditText foucusEditText ;//获取焦点的EditText
    protected Enum anEnum ; //传输过来的扫码类型

    AbstractPanelListAdapter adapter ;//产品列表adapter
    ArrayList<ProductInfo> productInfos = new ArrayList<>(); //显示出来的产品列表
    List<String> deleteOperators = new ArrayList<>();//最右侧删除用的操作栏，与productInfos数目保持一致

    private Vibrator mVibrator; //打扫成功后的震动器
    private ScanManager mScanManager; //扫码manager
    private SoundPool soundpool = null;//打包成功后bee一声
    private int soundid; //声音文件id

    public ProductInfo clickedProductInfo ;//点击选中的产品信息

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() { //扫码结果广播监听

        @Override
        public void onReceive(Context context, Intent intent) {

            soundpool.play(soundid, 1, 1, 0, 0, 1);
            mVibrator.vibrate(100);

            byte[] barcode = intent.getByteArrayExtra(ScanManager.DECODE_DATA_TAG);
            int barcodelen = intent.getIntExtra(ScanManager.BARCODE_LENGTH_TAG, 0);
            byte temp = intent.getByteExtra(ScanManager.BARCODE_TYPE_TAG, (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);
            String barcodeStr = new String(barcode, 0, barcodelen);
            if( foucusEditText != null && "仓位".equals( remark.getText().toString() )){
                foucusEditText.setText( barcodeStr );
                foucusEditText.clearFocus();
                foucusEditText = null ;
            }else{
                ProductInfo productInfo = new ProductInfo();
                productInfo.barCode = barcodeStr ;
                if( productInfos.contains( productInfo ) ){
                    ToastUtil.showShortToast( "该产品已在列表中" );
                    return ;
                }

                if( !barcodeHasSpecialCondition() ){ //子类实现判断对于结果是否有特殊情况操作
                    barCode.setText( barcodeStr );
                    dealwithBarcode( barcodeStr );//子类实现方法，对于扫码结果如何操作
                }
            }

        }

    };

    /**
     * 子类实现判断对于结果是否有特殊情况操作
     * @param barcodeStr
     */
    public abstract void dealwithBarcode(String barcodeStr);

    /**
     * 子类实现方法，对于扫码结果如何操作
     * @return
     */
    public abstract boolean barcodeHasSpecialCondition();

    /**
     * 设置顶部标题栏
     */
    @Override
    protected void setTitleBar() {
        titleBar.setTitleMainText( ( (ScanTypeEnum)anEnum ).getCodeName() );

    }

    /**
     * 返回布局文件id，子类实现可对展示内容作修改
     * @return
     */
    @Override
    protected int getLayout() {
        return R.layout.activity_product_normal;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        anEnum = (Enum) intent.getSerializableExtra( "pickScanType" );
    }

    /**
     * 父类的初始化方法，这里初始化大部分设置，子类可重写方法作特殊处理
     * @param var1
     */
    @Override
    public void initView(Bundle var1) {

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        User user = YDWMSApplication.getInstance().getUser();
        if( user != null ){
            operator.setText( "操作员：" + user.username );
        }

        remarkValue.setOnClickListener( v -> {
            foucusEditText = (EditText) v;
        } );

        //产品信息的初始化
        adapter = new ProductionPanelListAdapter(this, pl_root, lv_content, productInfos, R.layout.item_product_info);
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
        adapter.setColumnAdapter( new ColumnAdapter(deleteOperators) );
        adapter.setTitle("操作");
        //产品内容列表点击事情
        lv_content.setOnItemClickListener((parent, view, position, id) -> {
            ProductInfo productInfo = productInfos.get(position);
            clickedProductInfo = productInfo ;
            setProductInfo(productInfo);
        });

    }

    /**
     * 根据ProductInfo塞入界面显示的条码，卷号等信息。
     * @param productInfo
     */
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

    /**
     * 清除界面信息
     */
    protected void clearProductInfo(){
        barCode.setText( "" );
        material.setText( "" );
        productName.setText( "" );
        specification.setText( "" );
        volume.setText( "" );
        pack.setText( "" );
        remarkValue.setText( "" );
    }

    /**
     * 产品信息标题栏
     * @return
     */
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

    /**
     * 删除操作栏的适配器
     */
    class ColumnAdapter extends BaseAdapter{

        List<String> deleteList ;

        ColumnAdapter( List<String> roomNumber ){
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
                    productInfos.remove( position );
                    deleteOperators.remove( position );
                    clearProductInfo();
                    totalCount.setText( "合计：" + productInfos.size() + "件" );
                    adapter.notifyDataSetChanged();
                }).show();
            } );
            return view;
        }
    }

    /**
     * 扫一维码功能的初始化
     */
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
        //扫一维码功能的初始化
        initScan();
        IntentFilter filter = new IntentFilter();
        int[] idbuf = new int[]{PropertyID.WEDGE_INTENT_ACTION_NAME, PropertyID.WEDGE_INTENT_DATA_STRING_TAG};
        String[] value_buf = mScanManager.getParameterString(idbuf);
        if(value_buf != null && value_buf[0] != null && !value_buf[0].equals("")) {
            filter.addAction(value_buf[0]);
        } else {
            filter.addAction(SCAN_ACTION);
        }
        //注册接收广播
        registerReceiver(mScanReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //反注册广播
        if(mScanManager != null) {
            mScanManager.stopDecode();
        }
        unregisterReceiver(mScanReceiver);
    }

    /**
     * 根据产品列表，获取一维码集合
     * @return
     */
    public List<String> genCodes(){
        List<String> list = new ArrayList<>();
        for(int i = 0; i < productInfos.size() ; i ++ ){
            list.add( productInfos.get(i).barCode );
        }
        return list ;
    }

}
