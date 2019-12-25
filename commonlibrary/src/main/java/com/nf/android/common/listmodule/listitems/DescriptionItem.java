package com.nf.android.common.listmodule.listitems;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nf.android.common.R;;

public class DescriptionItem extends AbsListItem {

    String title ;
    StringBuffer descriptions ;

    private int bgColor ;
    private int titleColor ;
    private int desColor ;

    public DescriptionItem(Context context) {
        super(context);
        descriptions = new StringBuffer();
        bgColor = -1 ;
        titleColor = -1 ;
        desColor = -1 ;
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {
        View view = View.inflate( context, R.layout.item_description, null);

        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);
        ViewGroup bg = view.findViewById( R.id.parentPanel );
        TextView title = view.findViewById( R.id.title );
        TextView desc = view.findViewById( R.id.descriptions );

        title.setText( this.title );
        desc.setText( descriptions.toString() );

        if( bgColor != -1 ){
            bg.setBackgroundColor( bgColor );
        }
        if( titleColor != -1 ){
            title.setTextColor( titleColor );
        }
        if( desColor != -1 ){
            desc.setTextColor( desColor );
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescriptions(String... descs ){
        for( int i = 0 ; i < descs.length ; i ++ ){
            descriptions.append( descs[i] );
            if( i != descs.length - 1 ){
                descriptions.append( "\n");
            }
        }
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public void setDesColor(int desColor) {
        this.desColor = desColor;
    }
}
