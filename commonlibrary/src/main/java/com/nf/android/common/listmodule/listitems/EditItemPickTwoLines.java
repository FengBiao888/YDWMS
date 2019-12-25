package com.nf.android.common.listmodule.listitems;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nf.android.common.R;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class EditItemPickTwoLines extends AbsEditItem {

    private String topText;
    private String bottomText;
    private String bottomSubText ;
    private int textGravity ;
    private float topTextSize ;
    private float bottomTextSize ;
    private int topTextColor ;
    private int bottomTextColor ;
    private int bottomSubTextColor ;
    private boolean isAlignRight ; //是否对齐左边的标线。
    private boolean isTopTextSingleLine ;
    private boolean isBottomTextSingleLine ;
    private int emsLength ;

    public EditItemPickTwoLines(Context context, String typeName, boolean isMust, String hintText) {
        super(context, typeName, isMust, hintText);
        topTextColor = Color.BLACK ;
        bottomTextColor = context.getResources().getColor( R.color.color_999 );
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        View view = LayoutInflater.from( context ).inflate(R.layout.item_edititem_pick_two_line, null);
        final TextView ItemRight = view.findViewById( R.id.item_right );
        TextView line1 = view.findViewById( R.id.text1 );
        TextView line2 = view.findViewById( R.id.text2 );
        line1.setTextColor( topTextColor );
        line2.setTextColor( bottomTextColor );

        if( textGravity != 0 ){
            line1.setGravity( textGravity );
            line2.setGravity( textGravity );

        }
        line1.setGravity( isAlignRight ? Gravity.RIGHT : Gravity.LEFT );
        line2.setGravity( isAlignRight ? Gravity.RIGHT : Gravity.LEFT );
        ItemRight.setGravity( isAlignRight ? Gravity.RIGHT : Gravity.LEFT );

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
        TextView line1 = viewParent.findViewById( R.id.text1 );
        TextView line2 = viewParent.findViewById( R.id.text2 );
        TextView itemRight = viewParent.findViewById( R.id.item_right );
        itemName.setText( isMust ? spannableString : typeName );
        if( emsLength != 0 ){
            itemName.setMaxEms( emsLength );
            itemName.setMinEms( emsLength );
        }
        line1.setTextColor( topTextColor );
        line2.setTextColor( bottomTextColor );

        itemRight.setHint( inputHint );
        line1.setText(topText);
        if (!TextUtils.isEmpty(topText)) {
            itemRight.setHint( "" );
        }
        line2.setText(bottomText);
        if (!TextUtils.isEmpty(bottomText)) {
            itemRight.setHint( "" );
        }
        if( topTextSize != 0 ){
            line1.setTextSize( topTextSize );
        }
        if( bottomTextSize != 0 ){
            line2.setTextSize( bottomTextSize );
        }
        if( !TextUtils.isEmpty( bottomSubText ) ){
            setTextViewColor( line2, bottomText, bottomSubText );
        }

        if( isTopTextSingleLine ){
            line1.setSingleLine( true );
            line1.setEllipsize( TextUtils.TruncateAt.END );
        }

        if( isBottomTextSingleLine ){
            line2.setSingleLine( true );
            line2.setEllipsize( TextUtils.TruncateAt.END );
        }

    }

    private void setTextViewColor(TextView textView, String message, String message2){
        if(TextUtils.isEmpty( message ) ){
            textView.setText( message2 );
        }else if( TextUtils.isEmpty( message2 ) ){
            textView.setText( message );
        }else {
            SpannableStringBuilder builder = new SpannableStringBuilder(message + message2);
            ForegroundColorSpan leftSpan = new ForegroundColorSpan( textView.getCurrentTextColor() );
            ForegroundColorSpan rightSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.color_777));
            builder.setSpan(leftSpan, 0, message.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            int timeStart = message.length();
            int timeEnd = +message.length() + message2.length();
            builder.setSpan(rightSpan, timeStart, timeEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(builder);
        }
    }

    public EditItemPickTwoLines setTopText(String topText) {
        this.topText = topText;
        return this ;
    }

    public EditItemPickTwoLines setBottomText(String bottomText) {
        this.bottomText = bottomText;
        return this ;
    }

    public EditItemPickTwoLines setTextGravity(int textGravity) {
        this.textGravity = textGravity;
        return this ;
    }

    public EditItemPickTwoLines setTopTextSize(float topTextSize) {
        this.topTextSize = topTextSize;
        return this;
    }

    public EditItemPickTwoLines setBottomTextSize(float bottomTextSize) {
        this.bottomTextSize = bottomTextSize;
        return this ;
    }

    public EditItemPickTwoLines setTopTextColor(int topTextColor) {
        this.topTextColor = topTextColor;
        return this ;
    }

    public EditItemPickTwoLines setBottomTextColor(int bottomTextColor) {
        this.bottomTextColor = bottomTextColor;
        return this ;
    }

    public EditItemPickTwoLines setBottomSubText(String bottomSubText) {
        this.bottomSubText = bottomSubText;
        return this ;
    }

    public EditItemPickTwoLines setBottomSubTextColor(int bottomSubTextColor) {
        this.bottomSubTextColor = bottomSubTextColor;
        return this ;
    }

    public void setTopTextSingleLine(boolean topTextSingleLine) {
        isTopTextSingleLine = topTextSingleLine;
    }

    public void setBottomTextSingleLine(boolean bottomTextSingleLine) {
        isBottomTextSingleLine = bottomTextSingleLine;
    }

    public void setAlignRight(boolean isAlignRight) {
        this.isAlignRight = isAlignRight;
    }

    public void setEmsLength(int emsLength) {
        this.emsLength = emsLength;
    }
}
