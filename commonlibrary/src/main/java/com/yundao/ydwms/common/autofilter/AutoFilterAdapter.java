package com.yundao.ydwms.common.autofilter;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;

import com.yundao.ydwms.common.listmodule.listitems.AbsListItem;
import com.yundao.ydwms.common.listmodule.SimpleListAdapter;

import java.util.List;

/**
 * Created by liangjianhua on 2018/5/25.
 */
public class AutoFilterAdapter<T extends AbsListItem> extends SimpleListAdapter implements Filterable {

    private AutoFilter<T> mFilter ;
    private List<T> mDates ;

    public AutoFilterAdapter(Context context, List<T> dates){
        super(context, dates);
        mFilter = new AutoFilter<>(this, dates);
        mDates = dates ;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其未选中的第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            T date = mDates.get( i ) ;
            if( date.isFirstCapIndex( section ) ){
                return i;
            }
        }

        return 0;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }


}
