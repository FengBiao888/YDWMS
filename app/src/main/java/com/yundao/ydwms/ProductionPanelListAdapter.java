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

import com.yundao.ydwms.protocal.ProductionLogDto;

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

public class ProductionPanelListAdapter extends AbstractPanelListAdapter {

    private Context context;
    private List<ProductionLogDto> roomList;
    private int resourceId;

    public ProductionPanelListAdapter(Context context, PanelListLayout pl_root, ListView lv_content,
                                List<ProductionLogDto> roomList, int resourceId) {
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
        private List<ProductionLogDto> roomList;

        public ContentAdapter(@NonNull Context context, @LayoutRes int resource, List<ProductionLogDto> roomList) {
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
            ProductionLogDto info = roomList.get( position );
            if (convertView == null){
                view = LayoutInflater.from(parent.getContext()).inflate(itemResourceId,parent,false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.index.setText( ( position + 1 ) + "" );
            viewHolder.bar_code.setText( info.barCode );
            viewHolder.product_name.setText( info.productName );
            viewHolder.splice.setText( info.splice );
            viewHolder.train.setText( info.train );
            viewHolder.materielModel.setText( info.productModel );
            viewHolder.packing.setText( info.packing );
            viewHolder.machine.setText( info.machine );
            viewHolder.trayNumber.setText( info.trayNumber );
            viewHolder.length.setText( info.length + "" );
            viewHolder.tareWeight.setText( info.tareWeight == null ? "" : info.tareWeight.toString() );
            viewHolder.netWeight.setText( info.netWeight == null ? "": info.netWeight.toString() );
//            viewHolder.tareWeight.setText( info.tareWeight + "" );
//            viewHolder.netWeight.setText( info.netWeight + "" );
            viewHolder.volume.setText( info.volume );
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

}
