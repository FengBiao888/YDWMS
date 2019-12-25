package com.yundao.ydwms;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.nf.android.common.base.ImmersiveBaseActivity;
import com.yundao.ydwms.util.DialogUtil;
import com.yundao.ydwms.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import sysu.zyb.panellistlibrary.AbstractPanelListAdapter;
import sysu.zyb.panellistlibrary.PanelListLayout;

public class ProductPackagingActivity extends ImmersiveBaseActivity {

    private PanelListLayout pl_root;
    private AbstractPanelListAdapter adapter;
    private ListView lv_content;
    private List<Room> roomList = new ArrayList<>();
    private Button submit ;

    @Override
    protected void setTitleBar() {
        titleBar.setTitleMainText( "产品打包" );
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_product_packaging ;
    }

    @Override
    public void initView(Bundle var1) {
        pl_root = findViewById(R.id.id_pl_root);
        lv_content = findViewById(R.id.id_lv_content);
        submit = findViewById( R.id.confirm );
        initRoomData();

        adapter = new RoomPanelListAdapter(this, pl_root, lv_content, roomList, R.layout.item_room);
        adapter.setTitleWidth( 40 );
        adapter.setTitleHeight( 40 );
        adapter.setRowColor( "#4396FF" );
        adapter.setTitleColor( "#4396FF" );
        adapter.setColumnColor( "#FFFFFF" );
        adapter.setTitleTextColor( "#494BFF" );
//        adapter.setRowDivider( new ColorDrawable( 0x494BFF ) );
//        adapter.setColumnDivider( new ColorDrawable( 0x494BFF ) );
        adapter.setRowDataList(generateRowData());
        adapter.setColumnDataList(generateColumnData());
        adapter.setColumnAdapter( new ColumnAdapter(generateColumnData()) );
        adapter.setTitle("操作");
//        adapter.setRowColor("#0288d1");
//        adapter.setColumnColor("#81d4fa");
        pl_root.setAdapter(adapter);

        submit.setOnClickListener( v->{
            DialogUtil.showDeclareDialog( getActivity(),  "确定是否上传记录", v1 -> { } )
            .show();
        } );
    }

    /**
     * 初始化房间列表和房间信息
     */
    private void initRoomData() {
        for (int i = 201;i<221;i++){
            Room room = new Room(i);
            room.setRoomDetail(Utility.generateRandomRoomDetail());
            roomList.add(room);
        }
    }

    private List<String> generateRowData(){
        List<String> rowDataList = new ArrayList<>();
        rowDataList.add("周一");
        rowDataList.add("周二");
        rowDataList.add("周三");
        rowDataList.add("周四");
        rowDataList.add("周五");
        rowDataList.add("周六");
        rowDataList.add("周日");
        return rowDataList;
    }

    private List<String> generateColumnData(){
        List<String> columnDataList = new ArrayList<>();
        for (Room room : roomList){
            columnDataList.add(String.valueOf(room.getRoomNo()));
        }
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
}
