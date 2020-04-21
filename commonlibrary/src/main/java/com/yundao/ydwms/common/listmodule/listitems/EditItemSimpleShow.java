package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yundao.ydwms.common.R;;


/**
 * Created by liangjianhua on 2018/5/22.
 */

public class EditItemSimpleShow extends EditItemPick {


    public EditItemSimpleShow(Context context, String typeName, boolean isMust, String inputHint) {
        super(context, typeName, isMust, inputHint);
        setEnabled(false);
    }

    public EditItemSimpleShow(Context context, String typeName, boolean isMust, String inputHint, boolean leftTextGray999) {
        super(context, typeName, isMust, inputHint, leftTextGray999);
      setEnabled(false);
    }

    public EditItemSimpleShow(Context context, String typeName, boolean isMust, String inputHint, String inputMessage) {
        super(context, typeName, isMust, inputHint);
        setInputMessage( inputMessage );
      setEnabled(false);
    }

    public EditItemSimpleShow(Context context, String typeName, boolean isMust, String inputHint, String inputMessage, boolean leftTextGray999) {
        super(context, typeName, isMust, inputHint);
        setInputMessage( inputMessage );
        if( leftTextGray999 ) {
            setLeftTextColor(context.getResources().getColor(R.color.color_999));
        }
      setEnabled(false);
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {
        View view = LayoutInflater.from( context ).inflate(R.layout.item_edititem_simple_show, null);
        updateView( view, i, viewGroup );
        return view;
    }


    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);

        TextView itemRight = view.findViewById( R.id.item_right ) ;
        itemRight.setGravity( isAlignRight ? Gravity.RIGHT : Gravity.LEFT );
    }

    @Override
    public boolean isEnabled() {
        return isEnabled ;
    }

}
