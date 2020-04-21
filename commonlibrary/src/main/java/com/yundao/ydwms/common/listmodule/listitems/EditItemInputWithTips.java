package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yundao.ydwms.common.R;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class EditItemInputWithTips extends EditItemInput {

    String tips;
    int colorId ;

    public EditItemInputWithTips(Context context, String typeName, boolean isMust, String inputHint, String tips, int colorId) {
        super(context, typeName, isMust, inputHint);
        this.tips = tips ;
        this.colorId = colorId ;
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        View view = LayoutInflater.from( context ).inflate(R.layout.item_edititem_input_with_tips, null);
        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);
        TextView tipsTv = view.findViewById( R.id.item_tips );
        tipsTv.setText( tips );
        tipsTv.setTextColor( colorId );
    }

}
