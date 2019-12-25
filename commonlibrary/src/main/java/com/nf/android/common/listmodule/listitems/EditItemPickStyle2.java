package com.nf.android.common.listmodule.listitems;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nf.android.common.R;

;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class EditItemPickStyle2 extends AbsEditItem {

    private EditItemPickStyle2 relatePick ; //关联的选择项
    private TextUtils.TruncateAt truncateAt;
    private int leftTextColor;
    private int rightTextColor;
    private int emsLength ;

    public EditItemPickStyle2(Context context, String typeName, boolean isMust, String inputHint) {
        super(context, typeName, isMust, inputHint);
    }

    public EditItemPickStyle2(Context context, String typeName, boolean isMust, String inputHint, boolean leftTextGray999) {
        super(context, typeName, isMust, inputHint);
        if( leftTextGray999 ){
            setLeftTextColor( context.getResources().getColor( R.color.color_999 ) );
        }
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {
        View view = LayoutInflater.from( context ).inflate(R.layout.item_edititem_pick_style2, null);
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
        if( emsLength != 0 ){
            itemName.setMaxEms( emsLength );
            itemName.setMinEms( emsLength );
        }
        if( truncateAt != null ){
            itemRight.setEllipsize( truncateAt );
        }
        itemRight.setHint( inputHint );
        itemRight.setText(inputMessage);

    }

    public EditItemPickStyle2 setRelatePick(EditItemPickStyle2 relatePick) {
        this.relatePick = relatePick;
        return this ;
    }

    @Override
    public boolean isEnabled() { //如果设置了关联的EditItemPick对象，则关联对像数据先选择才能
        if( relatePick == null ) {
//            System.out.println("kdkdkd aaa: " + isEnable );
            return isEnabled ;
        }else {
            return !TextUtils.isEmpty(relatePick.getInputMessage());
        }
    }

    public EditItemPickStyle2 setLeftTextColor(int leftcolor){
        this.leftTextColor = leftcolor ;
        return this ;
    }

    public EditItemPickStyle2 setEllipsize(TextUtils.TruncateAt truncateAt){
        this.truncateAt = truncateAt ;
        return this ;
    }

    public EditItemPickStyle2 setRightTextColor(int rightTextColor) {
        this.rightTextColor = rightTextColor;
        return this ;
    }

    public void setEmsLength(int emsLength) {
        this.emsLength = emsLength;
    }
}
