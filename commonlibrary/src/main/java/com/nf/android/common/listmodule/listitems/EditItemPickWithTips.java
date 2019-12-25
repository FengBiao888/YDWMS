package com.nf.android.common.listmodule.listitems;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nf.android.common.R;;


/**
 * Created by liangjianhua on 2018/6/19.
 */

public class EditItemPickWithTips extends EditItemPick {

    String tips;
    int colorId ;
    int tipsGravity;
    private int tipsId ;

    public EditItemPickWithTips(Context context, String typeName, boolean isMust, String inputHint, String tips, int colorId) {
        super(context, typeName, isMust, inputHint);
        this.tips = tips ;
        this.colorId = colorId ;
        tipsGravity = Gravity.TOP ;
    }


    @Override
    public View getView(int i, ViewGroup viewGroup) {
        ViewGroup view = null;
        if(Gravity.TOP == tipsGravity){
            view = (ViewGroup) LayoutInflater.from( context ).inflate(R.layout.item_edititem_pick_with_toptips, null);
        }else if(Gravity.BOTTOM == tipsGravity){
            view = (ViewGroup) LayoutInflater.from( context ).inflate(R.layout.item_edititem_pick_with_bottomtips, null);
        }

        updateView( view, i, viewGroup );
        return view ;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);

        TextView tipsTv = view.findViewById( R.id.item_tips );
        tipsTv.setText( tips );
        tipsTv.setTextColor( colorId );
    }

    public void setTipsGravity(int gravity) {
        this.tipsGravity = gravity;
    }
}
