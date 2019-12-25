package com.nf.android.common.listmodule.listitems;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.nf.android.common.R;
import com.nf.android.common.utils.DipPxUtil;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class EditItemSubmitButton extends AbsListItem {

    private Drawable btnBgDrawable ;
    private int btnBgLayoutId ;
    private int textColor;
    private int textSize ;
    private float marginTop ;
    private float marginBottom ;

    public EditItemSubmitButton(Context context, String btnText) {
        super(context, btnText);
        marginTop = context.getResources().getDimension( R.dimen.dp20 );
        marginBottom = context.getResources().getDimension( R.dimen.dp20 );
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        View view = LayoutInflater.from( context ).inflate(R.layout.item_submitbutton, null);
        Button btn = view.findViewById( R.id.bottom_submit );
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
        layoutParams.topMargin = (int) marginTop ;
        layoutParams.bottomMargin = (int) marginBottom ;
        btn.setLayoutParams( layoutParams );
        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView( view, position, parent );
        Button btn = view.findViewById( R.id.bottom_submit );
        btn.setText( getTypeName() );
        if( btnBgDrawable != null ){
            btn.setBackground( btnBgDrawable );
        }else if( btnBgLayoutId != 0 ){
            btn.setBackgroundResource( btnBgLayoutId );
        }
        if( textColor != 0 ){
            btn.setTextColor( textColor );
        }

        if( textSize != 0 ){
            btn.setTextSize( textSize );
        }

    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof EditItemSubmitButton ){
            return getTypeName().equals(((EditItemSubmitButton) o).getTypeName());
        }
        return false ;

    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
    }

    public void setMarginTop(float marginTop) {
        this.marginTop = marginTop;
    }

    public void setBtnBgDrawable(Drawable btnBgDrawable) {
        this.btnBgDrawable = btnBgDrawable;
    }

    public void setBtnBgLayoutId(int btnBgLayoutId) {
        this.btnBgLayoutId = btnBgLayoutId;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
