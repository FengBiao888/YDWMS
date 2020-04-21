package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.yundao.ydwms.common.autofilter.IMatchRule;


public abstract class AbsListItem implements IMatchRule {

    protected String typeName ;
    protected Context context ;

    protected View viewParent ;

    private int viewID[] ;
    private View.OnClickListener clickListener ;
    private String extraParams ;
    private Object extraObj ;
    private Drawable bgDrawable ;

    protected boolean isEnabled = true;

    public AbsListItem(Context context ){
        this.context = context ;
        isEnabled = true ;
    }

    public AbsListItem(Context context, String typeName ){
        this.context = context ;
        this.typeName = typeName ;
        isEnabled = true ;
    }

    public abstract View getView(int i, ViewGroup viewGroup);

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setSpecifyClickListener(  View.OnClickListener clickListener, int ...id ){
        viewID = id ;
        this.clickListener = clickListener ;
    }


    public void updateView(View view, int position,ViewGroup parent) {
        if( view == null ) return ;
        if( bgDrawable != null ){
            view.setBackground( bgDrawable );
        }
        viewParent = view ;
        if( clickListener != null ) {
            if (viewID == null ) {
                viewParent.setOnClickListener(clickListener);
            } else {
                for ( int id : viewID )
                    viewParent.findViewById( id ).setOnClickListener( clickListener );
            }
        }
    }

    /**
     * 返回itemView类型
     * @return
     */
    public int getItemViewType() {
        return 0;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * 表示是否能编辑
     * @return
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof AbsListItem ){
            if( TextUtils.isEmpty( typeName ) || TextUtils.isEmpty( ((AbsListItem) o).getTypeName() ) ){
                return false ;
            }else{
                return o.getClass().getCanonicalName().equals(getClass().getCanonicalName()) && getTypeName().equals(((AbsListItem) o).getTypeName());
            }
        }
        return super.equals( o );
    }

    public void setExtraParams(String extraParams) {
        this.extraParams = extraParams;
    }

    public String getExtraParams() {
        return extraParams;
    }

    public void setExtraObj(Object extraObj) {
        this.extraObj = extraObj;
    }

    public Object getExtraObj() {
        return extraObj;
    }

    @Override
    public boolean isMatch(String prefix) {
        return false;
    }

    @Override
    public boolean isFirstCapIndex( int ascii ){
        return false ;
    }

    public void setBgDrawable(Drawable bgDrawable) {
        this.bgDrawable = bgDrawable;
    }


}
