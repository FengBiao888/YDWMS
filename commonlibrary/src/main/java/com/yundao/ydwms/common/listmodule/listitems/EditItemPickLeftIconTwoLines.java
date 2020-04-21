package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yundao.ydwms.common.R;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class EditItemPickLeftIconTwoLines extends AbsEditItem {

    private String topText;
    private String bottomText;
    private String bottomSubText ;
    private int lefticonId ;
    private int textGravity ;
    private float topTextSize ;
    private float bottomTextSize ;
    private int topTextColor ;
    private int bottomTextColor ;
    private int bottomSubTextColor ;
    private boolean isAlignLeftLine ; //是否对齐左边的标线。
    private boolean isTopTextSingleLine ;
    private boolean isBottomTextSingleLine ;

    private int bottomLineLeftRightMargin ;

    public EditItemPickLeftIconTwoLines(Context context, int lefticonId, boolean isMust, String hintText) {
        super(context, "", isMust, hintText);
        topTextColor = Color.BLACK ;
        this.lefticonId = lefticonId ;
        bottomTextColor = context.getResources().getColor( R.color.color_999 );
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        View view = LayoutInflater.from( context ).inflate(R.layout.item_edititem_pick_lefticon_two_line, null);
        TextView line1 = view.findViewById( R.id.text1 );
        TextView line2 = view.findViewById( R.id.text2 );
        line1.setTextColor( topTextColor );
        line2.setTextColor( bottomTextColor );

        if( textGravity != 0 ){
            line1.setGravity( textGravity );
            line2.setGravity( textGravity );
        }
        if( isAlignLeftLine ){
            View viewStub = view.findViewById( R.id.view_stub );
            ConstraintLayout.LayoutParams line1LayoutParams = (ConstraintLayout.LayoutParams) line1.getLayoutParams();
            line1LayoutParams.startToEnd = viewStub.getId() ;
            line1.setLayoutParams( line1LayoutParams );
            ConstraintLayout.LayoutParams line2LayoutParams = (ConstraintLayout.LayoutParams) line2.getLayoutParams();
            line2LayoutParams.startToEnd = viewStub.getId() ;
            line2.setLayoutParams( line2LayoutParams );
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

        ImageView leftIcon = viewParent.findViewById( R.id.left_icon);
        TextView line1 = viewParent.findViewById( R.id.text1 );
        TextView line2 = viewParent.findViewById( R.id.text2 );
        TextView itemRight = viewParent.findViewById( R.id.item_right );
        View bottomLine = viewParent.findViewById( R.id.line );
        if( lefticonId != 0 ){
            leftIcon.setImageResource( lefticonId );
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

        if( bottomLineLeftRightMargin != 0 ){
            ViewGroup.MarginLayoutParams viewGroup = (ViewGroup.MarginLayoutParams) bottomLine.getLayoutParams();
            viewGroup.leftMargin = viewGroup.rightMargin = bottomLineLeftRightMargin ;
            bottomLine.setLayoutParams( viewGroup );
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

    public EditItemPickLeftIconTwoLines setTopText(String topText) {
        this.topText = topText;
        return this ;
    }

    public EditItemPickLeftIconTwoLines setBottomText(String bottomText) {
        this.bottomText = bottomText;
        return this ;
    }

    public EditItemPickLeftIconTwoLines setTextGravity(int textGravity) {
        this.textGravity = textGravity;
        return this ;
    }

    public EditItemPickLeftIconTwoLines setTopTextSize(float topTextSize) {
        this.topTextSize = topTextSize;
        return this;
    }

    public EditItemPickLeftIconTwoLines setBottomTextSize(float bottomTextSize) {
        this.bottomTextSize = bottomTextSize;
        return this ;
    }

    public EditItemPickLeftIconTwoLines setTopTextColor(int topTextColor) {
        this.topTextColor = topTextColor;
        return this ;
    }

    public EditItemPickLeftIconTwoLines setBottomTextColor(int bottomTextColor) {
        this.bottomTextColor = bottomTextColor;
        return this ;
    }

    public EditItemPickLeftIconTwoLines setBottomSubText(String bottomSubText) {
        this.bottomSubText = bottomSubText;
        return this ;
    }

    public EditItemPickLeftIconTwoLines setBottomSubTextColor(int bottomSubTextColor) {
        this.bottomSubTextColor = bottomSubTextColor;
        return this ;
    }

    public void setTopTextSingleLine(boolean topTextSingleLine) {
        isTopTextSingleLine = topTextSingleLine;
    }

    public void setBottomTextSingleLine(boolean bottomTextSingleLine) {
        isBottomTextSingleLine = bottomTextSingleLine;
    }

    public void setAlignLeftLine(boolean alignLeftLine) {
        isAlignLeftLine = alignLeftLine;
    }

    public void setBottomLineLeftRightMargin(int bottomLineLeftRightMargin) {
        this.bottomLineLeftRightMargin = bottomLineLeftRightMargin;
    }
}
