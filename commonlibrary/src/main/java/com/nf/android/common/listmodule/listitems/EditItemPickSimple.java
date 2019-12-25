package com.nf.android.common.listmodule.listitems;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nf.android.common.R;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class EditItemPickSimple extends AbsEditItem {

    private int leftTextColor;
    private int rightTextColor;

    public EditItemPickSimple(Context context, String typeName, boolean isMust, String inputHint) {
        super(context, typeName, isMust, inputHint);
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {
        View view = LayoutInflater.from( context ).inflate(R.layout.item_edititem_pick_simple, null);
        TextView itemRight = view.findViewById( R.id.item_right ) ;

        updateView( view, i, viewGroup );
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);
//        TextView itemName = (TextView) view.findViewById( R.id.item_name );
//        TextView itemRight = (TextView)view.findViewById( R.id.item_right ) ;
//        itemName.setText( isMust ? spannableString : typeName );
//        itemRight.setHint( inputHint );
//        if(!TextUtils.isEmpty(inputMessage)){
//            itemRight.setText(inputMessage);
//        }
        postInvalidate();
    }

    public void postInvalidate(){

        TextView itemName = viewParent.findViewById( R.id.item_name );
        TextView itemRight = viewParent.findViewById( R.id.item_right ) ;
        itemName.setText( isMust ? spannableString : typeName );
        if( /*!isEnabled() ||*/ leftTextColor != 0 ){
            itemName.setTextColor( leftTextColor );
        }else{
            itemName.setTextColor( context.getResources().getColor( android.R.color.black ) );
        }
        itemRight.setHint( inputHint );
        itemRight.setText(inputMessage);

    }

    public EditItemPickSimple setLeftTextColor(int leftcolor){
        this.leftTextColor = leftcolor ;
        return this ;
    }



    public EditItemPickSimple setRightTextColor(int rightTextColor) {
        this.rightTextColor = rightTextColor;
        return this ;
    }


}
