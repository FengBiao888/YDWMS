package com.yundao.ydwms;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yundao.ydwms.protocal.ProductInfo;

import java.util.List;
import java.util.Map;

import sysu.zyb.panellistlibrary.AbstractPanelListAdapter;
import sysu.zyb.panellistlibrary.PanelListLayout;

/**
 * <pre>
 *     author : zyb
 *     e-mail : hbdxzyb@hotmail.com
 *     time   : 2017/9/9
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class RoomPanelListAdapter extends AbstractPanelListAdapter {

    private Context context;
    private List<ProductInfo> roomList;
    private int resourceId;

    public RoomPanelListAdapter(Context context, PanelListLayout pl_root, ListView lv_content,
                                List<ProductInfo> roomList, int resourceId) {
        super(context, pl_root, lv_content);
        this.context = context;
        this.roomList = roomList;
        this.resourceId = resourceId;
    }

    @Override
    protected BaseAdapter getContentAdapter() {
        return new ContentAdapter(context,resourceId,roomList);
    }

    private class ContentAdapter extends ArrayAdapter {

        private int itemResourceId;
        private List<ProductInfo> roomList;

        public ContentAdapter(@NonNull Context context, @LayoutRes int resource, List<ProductInfo> roomList) {
            super(context, resource);
            this.itemResourceId = resource;
            this.roomList = roomList;
        }

        @Override
        public int getCount() {
            Log.d("ybz", "getCount: ");
            return roomList.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            ProductInfo info = roomList.get( position );
//            Map<Integer,Integer> roomDetail = roomList.get(position).getRoomDetail();
            //这种设置监听的方式并不是性能最优的，其实可以像复用ViewHolder一样复用监听器，每次改变监听的position参数即可
            //监听器作为一个tag-Object存放在convertView中。可能需要调试一下，不过应该没问题，有需要的自己可以动手试试
            RoomClickListener listener = new RoomClickListener(position);

            if (convertView == null){
                view = LayoutInflater.from(parent.getContext()).inflate(itemResourceId,parent,false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.index.setOnClickListener(listener);
            viewHolder.bar_code.setOnClickListener(listener);
            viewHolder.product_name.setOnClickListener(listener);
            viewHolder.splice.setOnClickListener(listener);
            viewHolder.train.setOnClickListener(listener);
            viewHolder.materielModel.setOnClickListener(listener);
            viewHolder.packing.setOnClickListener(listener);
            viewHolder.machine.setOnClickListener(listener);
            viewHolder.trayNumber.setOnClickListener(listener);
            viewHolder.length.setOnClickListener(listener);
            viewHolder.tareWeight.setOnClickListener(listener);
            viewHolder.netWeight.setOnClickListener(listener);
            viewHolder.volume.setOnClickListener(listener);

            viewHolder.index.setText( position + "" );
            viewHolder.bar_code.setText( info.barCode );
            viewHolder.product_name.setText( info.materielName );
            viewHolder.splice.setText( info.splice );
            viewHolder.train.setText( info.train );
            viewHolder.materielModel.setText( info.materielModel );
            viewHolder.packing.setText( info.packing );
            viewHolder.machine.setText( info.machine );
            viewHolder.trayNumber.setText( info.trayNumber );
            viewHolder.length.setText( info.length );
            viewHolder.tareWeight.setText( info.tareWeight == null ? "" : info.tareWeight.toString() );
            viewHolder.netWeight.setText( info.netWeight == null ? "": info.netWeight.toString() );
            viewHolder.volume.setText( info.volume );
//            viewHolder.tv_1.setBackgroundResource(getBackgroundResource(roomDetail.get(1)));
//            viewHolder.tv_1.setText(getText(roomDetail.get(1)));
//            viewHolder.tv_2.setBackgroundResource(getBackgroundResource(roomDetail.get(2)));
//            viewHolder.tv_2.setText(getText(roomDetail.get(2)));
//            viewHolder.tv_3.setBackgroundResource(getBackgroundResource(roomDetail.get(3)));
//            viewHolder.tv_3.setText(getText(roomDetail.get(3)));
//            viewHolder.tv_4.setBackgroundResource(getBackgroundResource(roomDetail.get(4)));
//            viewHolder.tv_4.setText(getText(roomDetail.get(4)));
//            viewHolder.tv_5.setBackgroundResource(getBackgroundResource(roomDetail.get(5)));
//            viewHolder.tv_5.setText(getText(roomDetail.get(5)));
//            viewHolder.tv_6.setBackgroundResource(getBackgroundResource(roomDetail.get(6)));
//            viewHolder.tv_6.setText(getText(roomDetail.get(6)));
//            viewHolder.tv_7.setBackgroundResource(getBackgroundResource(roomDetail.get(7)));
//            viewHolder.tv_7.setText(getText(roomDetail.get(7)));
            return view;
        }

        private class ViewHolder{
            private TextView index;
            private TextView bar_code;
            private TextView product_name;
            private TextView splice;
            private TextView train;
            private TextView materielModel;
            private TextView packing;
            private TextView machine;
            private TextView trayNumber;
            private TextView length;
            private TextView tareWeight;
            private TextView netWeight;
            private TextView volume;

            ViewHolder(View itemView) {
                index = itemView.findViewById(R.id.index);
                bar_code = itemView.findViewById(R.id.bar_code);
                product_name = itemView.findViewById(R.id.product_name);
                splice = itemView.findViewById(R.id.splice);
                train = itemView.findViewById(R.id.train);
                materielModel = itemView.findViewById(R.id.materielModel);
                packing = itemView.findViewById(R.id.packing);
                machine = itemView.findViewById(R.id.machine);
                trayNumber = itemView.findViewById(R.id.trayNumber);
                length = itemView.findViewById(R.id.length);
                tareWeight = itemView.findViewById(R.id.tareWeight);
                netWeight = itemView.findViewById(R.id.netWeight);
                volume = itemView.findViewById(R.id.volume);
            }
        }
    }

    class RoomClickListener implements View.OnClickListener{

        int position;

        public RoomClickListener(int i) {
            super();
            position = i;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
//                case R.id.id_tv_01:
//                    Toast.makeText(context, roomList.get(position).getRoomNo()+"房间周一的入住详情", Toast.LENGTH_SHORT).show();
//
//                    break;
//                case R.id.id_tv_02:
//                    Toast.makeText(context, roomList.get(position).getRoomNo()+"房间周二的入住详情", Toast.LENGTH_SHORT).show();
//
//                    break;
//                case R.id.id_tv_03:
//                    Toast.makeText(context, roomList.get(position).getRoomNo()+"房间周三的入住详情", Toast.LENGTH_SHORT).show();
//
//                    break;
//                case R.id.id_tv_04:
//                    Toast.makeText(context, roomList.get(position).getRoomNo()+"房间周四的入住详情", Toast.LENGTH_SHORT).show();
//
//                    break;
//                case R.id.id_tv_05:
//                    Toast.makeText(context, roomList.get(position).getRoomNo()+"房间周五的入住详情", Toast.LENGTH_SHORT).show();
//
//                    break;
//                case R.id.id_tv_06:
//                    Toast.makeText(context, roomList.get(position).getRoomNo()+"房间周六的入住详情", Toast.LENGTH_SHORT).show();
//
//                    break;
//                case R.id.id_tv_07:
//                    Toast.makeText(context, roomList.get(position).getRoomNo()+"房间周日的入住详情", Toast.LENGTH_SHORT).show();
//                    break;
//                default:
//                    break;
            }
        }
    }
}
