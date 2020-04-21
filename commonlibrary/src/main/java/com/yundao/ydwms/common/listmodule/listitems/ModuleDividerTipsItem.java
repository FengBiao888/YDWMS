package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.yundao.ydwms.common.R;;


public class ModuleDividerTipsItem extends AbsListItem {

    private String subTitle ;
    private boolean needMust ;

    public ModuleDividerTipsItem(Context context) {
        super(context);
        leftColor = -1 ;
    }

    public ModuleDividerTipsItem(Context context, String typeName) {
        super(context, typeName);
        leftColor = -1 ;
    }

    private int leftColor ;

    @Override
    public View getView(int i, ViewGroup viewGroup) {
        View view = View.inflate( context, R.layout.item_module_divider_tips, null );
        TextView textView = view.findViewById( R.id.tv_title );
        TextPaint tp = textView.getPaint();
        tp.setFakeBoldText(true);
        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);
        TextView textView = view.findViewById( R.id.tv_title );
        TextView tvSub = view.findViewById( R.id.subtitle );
        TextView must = view.findViewById( R.id.item_state );
        textView.setText( typeName );

        View leftView = view.findViewById( R.id.view_left );
        if( leftColor != -1 ){
            leftView.setBackgroundColor( leftColor );
        }

        if( !TextUtils.isEmpty(subTitle) ){
            tvSub.setVisibility( View.VISIBLE );
            tvSub.setText( subTitle );
        }else{
            tvSub.setVisibility( View.GONE );
        }

        must.setVisibility( needMust ? View.VISIBLE : View.GONE );
    }

    @Override
    public boolean isEnabled() {
        return false ;
    }

    public boolean isNeedMust() {
        return needMust;
    }

    public void setLeftColor(int leftColor) {
        this.leftColor = leftColor;
    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof SubTitleItem ){
            return typeName.equals( ((SubTitleItem) o).typeName );
        }
        return super.equals(o);
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setNeedMust(boolean needMust) {
        this.needMust = needMust;
    }
}
