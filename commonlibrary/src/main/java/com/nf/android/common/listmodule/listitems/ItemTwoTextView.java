package com.nf.android.common.listmodule.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nf.android.common.R;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class ItemTwoTextView extends AbsListItem {

    private int rightTextColor ;
    private String rightText ;

    public ItemTwoTextView(Context context, String leftText, String righttext) {
        super(context, leftText);
        rightText = righttext ;
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        View view = LayoutInflater.from( context ).inflate(R.layout.item_two_text_view, null);
        TextView right = view.findViewById( R.id.item_right );
        if( rightTextColor != 0 ){
            right.setTextColor( rightTextColor );
        }
        updateView(view, i, viewGroup);

        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);

        TextView left = view.findViewById( R.id.item_name );
        TextView right = view.findViewById( R.id.item_right );

        left.setText( typeName );
        right.setText(rightText);

    }

    public void setRightTextColor(int rightTextColor) {
        this.rightTextColor = rightTextColor;
    }
}
