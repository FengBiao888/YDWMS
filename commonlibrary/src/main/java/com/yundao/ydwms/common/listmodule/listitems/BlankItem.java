package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yundao.ydwms.common.utils.DipPxUtil;
import com.yundao.ydwms.common.R;;

public class BlankItem extends AbsListItem {

    private int blankHeight ;
    private int blankColor = -1 ;
    private boolean needBottomLine ;
    private int backgroundColor;

    public BlankItem(Context context, int dpValue) {
        this( context, dpValue, true);
    }

    public BlankItem(Context context, int dpValue, boolean needBottomLine) {
        super(context);
        this.blankHeight = DipPxUtil.dipToPx( context, dpValue);
        this.needBottomLine = needBottomLine ;
    }

  public BlankItem(Context context, int dpValue, boolean needBottomLine, int backgroundColor) {
    super(context);
    this.blankHeight = DipPxUtil.dipToPx( context, dpValue);
    this.needBottomLine = needBottomLine ;
    this.backgroundColor = backgroundColor;
  }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        LinearLayout linearLayout = new LinearLayout( context );
        linearLayout.setOrientation( LinearLayout.VERTICAL );

        View view = new View( context );
        view.setLayoutParams( new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , blankHeight ) );

        linearLayout.addView( view );

        if ( needBottomLine ) {
            View line = new View(context);
            line.setBackgroundColor(context.getResources().getColor(R.color.divider_color));
            line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            linearLayout.addView(line);
        }
        if( backgroundColor != 0 ){
          linearLayout.setBackgroundColor( backgroundColor );
        }
        updateView(linearLayout, i, viewGroup);
        return linearLayout;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {

        if( blankColor != -1 ){
            view.setBackgroundColor( blankColor );
        }
    }

    public void setBlankColor(int blankColor) {
        this.blankColor = blankColor;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
