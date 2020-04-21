package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yundao.ydwms.common.R;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class EditItemPick extends AbsEditItem {

    private EditItemPick relatePick ; //关联的选择项
    private TextUtils.TruncateAt truncateAt;
    private int leftTextColor;
    private int rightTextColor;
    protected boolean isAlignRight; //是否对齐右边。默认左边
    private int emsLength ;
    private boolean isLeftGravityCenter ;

    public EditItemPick(Context context, String typeName, boolean isMust, String inputHint) {
        super(context, typeName, isMust, inputHint);
    }

    public EditItemPick(Context context, String typeName, boolean isMust, String inputHint, boolean leftTextGray999) {
        super(context, typeName, isMust, inputHint);
        if( leftTextGray999 ){
            setLeftTextColor( context.getResources().getColor( R.color.color_999 ) );
        }
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {
        View view = LayoutInflater.from( context ).inflate(R.layout.item_edititem_pick, null);
        TextView itemRight = view.findViewById( R.id.item_right ) ;
        itemRight.setGravity( isAlignRight ? Gravity.RIGHT : Gravity.LEFT );
        if( isLeftGravityCenter ) {
            TextView itemLeft = view.findViewById(R.id.item_name);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) itemLeft.getLayoutParams();
            layoutParams.bottomToBottom = itemRight.getId();
            itemLeft.setLayoutParams( layoutParams );

            itemRight.setMinEms( 1 );
        }

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
        if( rightTextColor != 0 ){
            itemRight.setTextColor( rightTextColor );
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

    public EditItemPick setRelatePick(EditItemPick relatePick) {
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

    public EditItemPick setLeftTextColor(int leftcolor){
        this.leftTextColor = leftcolor ;
        return this ;
    }

    public EditItemPick setEllipsize(TextUtils.TruncateAt truncateAt){
        this.truncateAt = truncateAt ;
        return this ;
    }

    public EditItemPick setRightTextColor(int rightTextColor) {
        this.rightTextColor = rightTextColor;
        return this ;
    }

    public EditItemPick setAlignRight(boolean alignRight) {
        isAlignRight = alignRight;
        return this ;
    }

    public void setLeftGravityCenter(boolean leftGravityCenter) {
        isLeftGravityCenter = leftGravityCenter;
    }

    public void setEmsLength(int emsLength) {
        this.emsLength = emsLength;
    }


}
