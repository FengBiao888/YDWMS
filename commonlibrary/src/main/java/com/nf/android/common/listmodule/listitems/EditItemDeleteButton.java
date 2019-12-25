package com.nf.android.common.listmodule.listitems;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nf.android.common.R;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class EditItemDeleteButton extends AbsListItem {

    private int textColor ;
    private Drawable btnBgDrawable ;
    private int btnBgLayoutId ;

    public EditItemDeleteButton(Context context, String btnText) {
        super(context, btnText);
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        View view = LayoutInflater.from( context ).inflate(R.layout.item_deletebutton, null);
        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView( view, position, parent );
        TextView btn = view.findViewById( R.id.tv_delete );
        btn.setText( getTypeName() );
        if( textColor != 0 ){
            btn.setTextColor( textColor );
        }


    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof  EditItemDeleteButton){
            return getTypeName().equals(((EditItemDeleteButton) o).getTypeName());
        }
        return false ;

    }

    public void setBtnBgDrawable(Drawable btnBgDrawable) {
        this.btnBgDrawable = btnBgDrawable;
    }

    public void setBtnBgLayoutId(int btnBgLayoutId) {
        this.btnBgLayoutId = btnBgLayoutId;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
