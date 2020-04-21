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

public class EditItemDeleteCircleButton extends AbsListItem {
    private int textColor;
    private int backgroundId ;

    public EditItemDeleteCircleButton(Context context, String btnText, int textColor) {
        super(context, btnText);
        this.textColor = textColor;
        backgroundId = -1 ;
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        View view = LayoutInflater.from( context ).inflate(R.layout.item_deletecircle_button, null);
        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView( view, position, parent );
        TextView btn = view.findViewById( R.id.bt_submit );
        if( backgroundId != -1 ){
            btn.setBackgroundResource( backgroundId );
        }
        btn.setText( getTypeName() );
        btn.setTextColor(textColor);
    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof  EditItemDeleteCircleButton){
            return getTypeName().equals(((EditItemDeleteCircleButton) o).getTypeName());
        }
        return false ;
    }

    public void setBtnText(String btnText) {
        typeName = btnText;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setBackgroundId(int backgroundId) {
        this.backgroundId = backgroundId;
    }
}
