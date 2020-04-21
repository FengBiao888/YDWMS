package com.yundao.ydwms.common.listmodule;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.yundao.ydwms.common.listmodule.listitems.AbsListItem;


/**
 * 用于视图回收管理
 * @author lhy
 *
 */
public abstract class AbstractListViewRecycler {
    protected Context mActivity ;
    public AbstractListViewRecycler(Context activity){
	mActivity = activity ;
    }
    public abstract View getScrapView(AbsListItem itemdata, int position);
    public abstract void	addScrapView(AbsListItem itemdata, View view, int position);
    public abstract void	removeScrapView(AbsListItem itemdata) ;
    public abstract void	postInvalidate(AbsListItem itemdata, View view, ViewGroup parent, int position); //更新数据
    public abstract void	zapScrapViews(AbsListItem exceptItem);
    public abstract void    onActivityDestroy();
}
