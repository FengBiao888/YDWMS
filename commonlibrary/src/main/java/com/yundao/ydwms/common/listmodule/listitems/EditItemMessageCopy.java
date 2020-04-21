package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yundao.ydwms.common.R;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class EditItemMessageCopy extends AbsEditItem {

    public EditItemMessageCopy(Context context, String typeName, boolean isMust, String inputHint) {
        super(context, typeName, isMust, inputHint);
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        View view = LayoutInflater.from( context ).inflate(R.layout.item_edititem_messagecopy, null);
        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);
        TextView itemName = view.findViewById( R.id.item_name );
//        TextView itemRight = (TextView)view.findViewById( R.id.item_right ) ;
        itemName.setText( isMust ? spannableString : typeName );
        if( TextUtils.isEmpty(inputMessage) ){
            view.findViewById( R.id.item_messageto ).setVisibility( View.GONE );
//            view.findViewById( R.id.item_messagecopy ).setVisibility( View.VISIBLE );
        }else{
//            view.findViewById( R.id.item_messagecopy ).setVisibility( View.GONE );
            TextView tvMessageTo = view.findViewById( R.id.item_messageto );
            tvMessageTo.setVisibility( View.VISIBLE );
            tvMessageTo.setText(inputMessage);
        }
//        itemRight.setText( inputHint );
    }

}
