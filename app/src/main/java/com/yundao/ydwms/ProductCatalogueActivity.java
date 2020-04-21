package com.yundao.ydwms;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.yundao.ydwms.common.base.ImmersiveBaseActivity;
import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.util.DialogUtil;

import java.util.ArrayList;
import java.util.List;

import sysu.zyb.panellistlibrary.AbstractPanelListAdapter;
import sysu.zyb.panellistlibrary.PanelListLayout;

public class ProductCatalogueActivity extends ImmersiveBaseActivity {

    PanelListLayout pl_root;//产品信息父Layout
    ListView lv_content;//产品信息列表

    AbstractPanelListAdapter adapter;//产品列表adapter
    ArrayList<ProductionLogDto> productInfos = new ArrayList<>(); //显示出来的产品列表
    List<String> deleteOperators = new ArrayList<>();//最右侧删除用的操作栏，与productInfos数目保持一致

    private boolean dataChanged  = false ;

    @Override
    protected void setTitleBar() {
        titleBar.setTitleMainText( "产品分类" )
            .setRightText( "完成" )
            .setRightTitleClickListener( v -> {
                //完成后把数据传回去
                Intent intent = new Intent();
                intent.putExtra( "productInfoList", productInfos );
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
        //根据传过来的数据初始化
        List<ProductionLogDto> lists = (List<ProductionLogDto>) intent.getSerializableExtra( "productInfoList" );
        productInfos.addAll( lists );
        for( int i = 0 ; i < productInfos.size() ; i ++ ){
            deleteOperators.add( "delete" );
        }
    }

    @Override
    public void initView(Bundle var1) {

        pl_root = findViewById( R.id.id_pl_root );//产品信息父Layout
        lv_content = findViewById( R.id.id_lv_content );//产品信息列表

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        adapter.setColumnDataList( deleteOperators );
        adapter.setColumnAdapter( new ColumnAdapter( deleteOperators ) );
        adapter.setTitle("操作");
        pl_root.setAdapter(adapter);

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
                   productInfos.remove( position );
                   deleteOperators.remove( position );
                   dataChanged = true ;
                   adapter.notifyDataSetChanged();
                }).show();
            } );
            return view;
        }
    }

}
