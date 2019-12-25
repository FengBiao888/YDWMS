package com.nf.android.common.autofilter;

import android.widget.BaseAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangjianhua on 2018/5/25.
 */

public class AutoFilter<T extends IMatchRule> extends Filter {

    private BaseAdapter mBaseAdapter ;
    /**
     * Contains the list of objects that represent the data of this Adapter.
     * Adapter数据源
     */
    private List<T> mDatas;
    /**
     * This lock is also used by the filter
     * (see {@link #performFiltering} to make a synchronized copy of
     * the original array of data.
     * 过滤器上的锁可以同步复制原始数据。
     *
     */
    private final Object mLock = new Object();

    // A copy of the original mObjects array, initialized from and then used instead as soon as
    // the mFilter ArrayFilter is used. mObjects will then only contain the filtered values.
    //对象数组的备份，当调用ArrayFilter的时候初始化和使用。此时，对象数组只包含已经过滤的数据。
    private ArrayList<T> mOriginalValues;

    public AutoFilter(BaseAdapter baseAdapter, List<T> dates){
        mBaseAdapter = baseAdapter ;
        mDatas = dates ;
    }

    /**
     * 执行过滤的方法
     * @param prefix
     * @return
     */
    @Override
    protected FilterResults performFiltering(CharSequence prefix) {
        // 过滤的结果
        FilterResults results = new FilterResults();
        // 原始数据备份为空时，上锁，同步复制原始数据
        if (mOriginalValues == null) {
            synchronized (mLock) {
                mOriginalValues = new ArrayList<>(mDatas);
            }
        }
        // 当首字母为空时
        if (prefix == null || prefix.length() == 0) {
            ArrayList<T> list;
            // 同步复制一个原始备份数据
            synchronized (mLock) {
                list = new ArrayList<>(mOriginalValues);
            }
            // 此时返回的results就是原始的数据，不进行过滤
            results.values = list;
            results.count = list.size();
        } else {
            String prefixString = prefix.toString().toLowerCase();

            ArrayList<T> values;
            // 同步复制一个原始备份数据
            synchronized (mLock) {
                values = new ArrayList<>(mOriginalValues);
            }
            final int count = values.size();
            final ArrayList<T> newValues = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                // 从List<T>中拿到T对象
                final T value = values.get(i);
                // 关键字是否和item的过滤参数匹配
                if ( value.isMatch( prefixString ) ) {
                    // 将这个item加入到数组对象中
                    newValues.add(value);
                } else {
                    // 处理首字符是空格
                    final String[] words = prefixString.split(" ");
                    final int wordCount = words.length;

                    for (int k = 0; k < wordCount; k++) {
                        // 一旦找到匹配的就break，跳出for循环
                        if ( value.isMatch(words[k])) {
                            newValues.add(value);
                            break;
                        }
                    }
                }
            }
            // 此时的results就是过滤后的List<TaskModel>数组
            results.values = newValues;
            results.count = newValues.size();
        }
        return results;
    }

    /**
     * 得到过滤结果
     *
     * @param prefix
     * @param results
     */
    @Override
    protected void publishResults(CharSequence prefix, FilterResults results) {

        // 此时，Adapter数据源就是过滤后的Results
//        mDatas = (List<T>) results.values ;
        mDatas.clear();
        mDatas.addAll( (List<T>) results.values );
        if (results.count > 0) {
            // 这个相当于从mDatas中删除了一些数据，只是数据的变化，故使用notifyDataSetChanged()
            mBaseAdapter.notifyDataSetChanged();
        } else {
            // 当results.count<=0时，此时数据源就是重新new出来的，说明原始的数据源已经失效了
            mBaseAdapter.notifyDataSetInvalidated();
        }
    }
}
