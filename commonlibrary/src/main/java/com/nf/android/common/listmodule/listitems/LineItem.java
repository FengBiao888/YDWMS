package com.nf.android.common.listmodule.listitems;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.nf.android.common.utils.DipPxUtil;

public class LineItem extends AbsListItem {

    private int blankHeight ;
    private String blankColor = "#ffffff" ;

    public LineItem(Context context, String color) {
        super(context);
        this.blankHeight = DipPxUtil.dipToPx( context, 0.5f);
        this.blankColor = color ;

    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {


        View view = new View( context );
        view.setLayoutParams( new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , blankHeight ) );
        view.setBackgroundColor(Color.parseColor(blankColor));
        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {

        view.setBackgroundColor(Color.parseColor(blankColor));
    }
}
