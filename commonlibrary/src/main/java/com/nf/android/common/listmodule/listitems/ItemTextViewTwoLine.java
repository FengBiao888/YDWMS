package com.nf.android.common.listmodule.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nf.android.common.R;

;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class ItemTextViewTwoLine extends AbsListItem {

    private String bottomText;

    public ItemTextViewTwoLine(Context context, String leftText, String bottomText) {
        super(context, leftText);
        this.bottomText = bottomText ;
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        View view = LayoutInflater.from( context ).inflate(R.layout.item_textview_two_line, null);

        updateView(view, i, viewGroup);

        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);

        TextView top = view.findViewById( R.id.text1 );
        TextView bottom = view.findViewById( R.id.text2 );

        top.setText( typeName );
        bottom.setText(bottomText);

    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }
}
