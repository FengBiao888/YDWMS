package com.nf.android.common.listmodule.listitems;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nf.android.common.utils.DipPxUtil;
import com.nf.android.common.R;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class ItemOneTextView extends AbsListItem {

    private int color ;
    private int gravity ;
    private float textSize ;
    private int typeFace ;
    private boolean needLine;
    private float leftPadding ;
    private float rightPadding ;
    private float topPadding ;
    private float bottomPadding ;
    private int bgColor ;
    private TextUtils.TruncateAt truncateAt;
    private SpannableString spannableString ;
    private float lineSpace ;

    public ItemOneTextView(Context context, String tipsMessage, int color) {
        super(context, tipsMessage);
        this.color = color;
        this.textSize = context.getResources().getDimension( R.dimen.text_size_12sp ) ;
        this.textSize = DipPxUtil.pxToDip( context, this.textSize );
        gravity = Gravity.CENTER_HORIZONTAL ;
        typeFace = -1;
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        LinearLayout frameLayout = new LinearLayout(context);
        frameLayout.setOrientation( LinearLayout.VERTICAL );

        TextView textView = new TextView(context);
        textView.setId( R.id.item_name );
        textView.setTextColor( color );
        textView.setTextSize( textSize );
        textView.setGravity( gravity );
        if( lineSpace != 0 ){
            textView.setLineSpacing( lineSpace,1.0f);
        }
        if( typeFace != -1 ) {

            textView.getPaint().setTypeface(Typeface.defaultFromStyle( typeFace ));
        }
        if( truncateAt != null ) {
            textView.setSingleLine( true );
            textView.setEllipsize(truncateAt);
        }


//        textView.setLayoutParams( new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        frameLayout.setLayoutParams(layoutParams);
        int paddingLeft = DipPxUtil.dipToPx(context, leftPadding == 0.0 ? 12 : leftPadding);
        int paddingRight = DipPxUtil.dipToPx(context, rightPadding == 0.0 ? 12 : rightPadding);
        int paddingTop = DipPxUtil.dipToPx(context, topPadding == 0.0 ? 14 : topPadding);
        int paddingBottom = DipPxUtil.dipToPx(context, bottomPadding == 0.0 ? 10 : bottomPadding);
//        frameLayout.setPadding( paddingLeftRight, 0, paddingLeftRight, 0 );
        layoutParams.topMargin = paddingTop ;
        layoutParams.bottomMargin = paddingBottom ;
        layoutParams.leftMargin = paddingLeft;
        layoutParams.rightMargin = paddingRight ;
        frameLayout.addView( textView , layoutParams );

        if( needLine ) {
            View view = new View(context);
            view.setBackgroundColor(context.getResources().getColor(R.color.msg_h_line));
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            params.gravity = Gravity.BOTTOM;
            view.setLayoutParams(params);
            frameLayout.addView(view);
        }

        if( bgColor != 0 ){
            frameLayout.setBackgroundColor( bgColor );
        }

        updateView(frameLayout, i, viewGroup);
        return frameLayout;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);
        LinearLayout frameLayout = (LinearLayout) view ;
        TextView textView = frameLayout.findViewById( R.id.item_name );
        if( spannableString != null ){
            textView.setText( spannableString );
        }else {
            textView.setText(typeName);
        }
    }

    public void setSpannableString(SpannableString spannableString) {
        this.spannableString = spannableString;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setTextSize(int id) {
        this.textSize = context.getResources().getDimension( id );
        this.textSize = DipPxUtil.pxToDip( context, this.textSize );
    }

    public void setTypeFace(int typeFace) {
        this.typeFace = typeFace;
    }

    public void setLineSpace(float lineSpace) {
        this.lineSpace = lineSpace;
    }

    @Override
    public boolean isMatch(String prefix) {
        return typeName.indexOf(prefix) != -1;
    }

    public void setNeedLine(boolean needLine) {
        this.needLine = needLine;
    }

    public void setPadding( float topPadding, float leftPadding, float bottomPadding, float rightPadding ){
        this.topPadding = topPadding ;
        this.bottomPadding = bottomPadding ;
        this.rightPadding = rightPadding ;
        this.leftPadding = leftPadding ;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setTruncateAt(TextUtils.TruncateAt truncateAt) {
        this.truncateAt = truncateAt;
    }

    //    @Override
//    public boolean isFirstCapIndex(int ascii) {
//
//        CharacterParser parser = CharacterParser.getInstance();
//        String first = typeName.substring(0, 1);
//        String converFirst = parser.convert( first ).toUpperCase();
//        int typeAscii = converFirst.charAt( 0 );
//        if( typeAscii == ascii ){
//            return true ;
//        }
//        return false ;
//    }


    @Override
    public boolean equals(Object o) {
        if( o instanceof ItemOneTextView ){
            return typeName.equals( ((ItemOneTextView) o).typeName );
        }
        return false ;
    }
}
