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

import com.yundao.ydwms.protocal.ProductionLogDto;
import com.yundao.ydwms.protocal.request.Baling;

import java.util.List;

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

public class ProductionSumPanelListAdapter extends AbstractPanelListAdapter {

    private Context context;
    private List<Baling> roomList;
    private int resourceId;

    public ProductionSumPanelListAdapter(Context context, PanelListLayout pl_root, ListView lv_content,
                                         List<Baling> roomList, int resourceId) {
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
        private List<Baling> roomList;

        public ContentAdapter(@NonNull Context context, @LayoutRes int resource, List<Baling> roomList) {
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
            Baling info = roomList.get( position );
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
            viewHolder.product_name.setText( info.productCode );
            viewHolder.weight_sum.setText( info.netWeight == null ? "": info.netWeight.toString() );
            viewHolder.length_sum.setText( info.meter == null ? "": info.meter.toString() );
            return view;
        }

        private class ViewHolder{
            private TextView index;
            private TextView bar_code;
            private TextView product_name;
            private TextView length_sum ;
            private TextView weight_sum;

            ViewHolder(View itemView) {
                index = itemView.findViewById(R.id.index);
                bar_code = itemView.findViewById(R.id.bar_code);
                product_name = itemView.findViewById(R.id.product_name);
                length_sum = itemView.findViewById( R.id.length_sum );
                weight_sum = itemView.findViewById( R.id.weight_sum );
            }
        }
    }



}
