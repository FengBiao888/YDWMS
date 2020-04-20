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
import com.yundao.ydwms.protocal.ProductionLogDto;
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

    public PanelListLayout pl_root; //产品信息父Layout
    public  ListView lv_content; //产品信息列表
    public Button submit ; //确定按钮
    public TextView operator ; //操作员
    public TextView totalCount ; //总条数

    protected Enum anEnum ; //传输过来的扫码类型

    AbstractPanelListAdapter adapter ;//产品列表adapter
    ArrayList<ProductionLogDto> productInfos = new ArrayList<>(); //显示出来的产品列表
    List<String> deleteOperators = new ArrayList<>();//最右侧删除用的操作栏，与productInfos数目保持一致


    public ProductionLogDto clickedProductionLogDto ;//点击选中的产品信息
    public int clickedPosition ;//选中的position
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

        pl_root = findViewById( R.id.id_pl_root ); //产品信息父Layout
         lv_content = findViewById( R.id.id_lv_content ); //产品信息列表
        submit = findViewById( R.id.confirm ); //确定按钮
        operator = findViewById( R.id.operator ); //操作员
        totalCount = findViewById( R.id.state ); //总条数

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        User user = YDWMSApplication.getInstance().getUser();
        if( user != null ){
            operator.setText( "操作员：" + user.username );
        }

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
            ProductionLogDto productInfo = productInfos.get(position);
            clickedProductionLogDto = productInfo ;
            clickedPosition = position ;
            setProductionLogDto(productInfo);
        });

    }

    /**
     * 根据ProductionLogDto塞入界面显示的条码，卷号等信息。
     * @param productInfo
     */
    protected abstract void setProductionLogDto(ProductionLogDto productInfo);

    /**
     * 清除界面信息
     */
    protected abstract void clearProductionLogDto();

    /**
     * 产品信息标题栏
     * @return
     */
    protected List<String> generateRowData(){
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
                    System.out.println( "kdkdkdk aaabbbcc： " + productInfos.size() + " , " + position );
                    productInfos.remove( position );
                    deleteOperators.remove( position );
                    clearProductionLogDto();
                    totalCount.setText( "合计：" + productInfos.size() + "件" );
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
