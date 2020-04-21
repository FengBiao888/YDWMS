package com.yundao.ydwms.common.listmodule;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;


import com.yundao.ydwms.common.R;
import com.yundao.ydwms.common.listmodule.listitems.AbsListItem;
import com.yundao.ydwms.common.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class SimpleListAdapter<T extends AbsListItem> extends BaseAdapter {
    public static int MAX_SUPPORT_VIEWTYPE = 10;//最多支持在同一列表中共存的类型总数
    private final static String TAG = "SimpleListAdapter";
    private View mHeaderView; //自定义头，不用ListView自带的头
    private View mFooterView; //自定义脚，不用ListView的FooterView
    private List<T> mDataList;//
    private List<String> mItemViewTypes;
    private int mMaxViewTypeCount = MAX_SUPPORT_VIEWTYPE; //支持的列表类型数
    private AbstractListViewRecycler mListViewRecycler;


    public SimpleListAdapter(Context activity, List<T> datalist) {
        if (datalist != null) { //把空的数据排除出去
            List<T> copyList = new CopyOnWriteArrayList<T>(datalist);
            datalist.clear();
            for (T m : copyList) {
                if (m != null) {
                    datalist.add(m);
                }
            }
        }
        //设置一个默认的回收器
        mListViewRecycler = new DefaultListViewRecycler(activity);
        mDataList = datalist;
        mItemViewTypes = new ArrayList<String>();
    }

    public void setListViewRecycler(AbstractListViewRecycler recycler) {
        mListViewRecycler = recycler;
    }

    public AbstractListViewRecycler getListViewRecycler() {
        return mListViewRecycler;
    }

    public void setHeaderView(View headerview) {
        boolean changed = mHeaderView != headerview;
        mHeaderView = headerview;
        if (changed) {
            notifyDataSetChanged();
        }
    }

    public void setHeaderViewVisible(int visible) {
        if (mHeaderView != null) {
            boolean changed = mHeaderView.getVisibility() != visible;
            mHeaderView.setVisibility(visible);
            if (changed) {
                notifyDataSetChanged();
            }
        }
    }

    private boolean headerViewIsVisible() {
        return mHeaderView != null && mHeaderView.getVisibility() == View.VISIBLE;
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setFooterView(View footerview) {
        boolean changed = mFooterView != footerview;
        mFooterView = footerview;
        if (changed) {
            notifyDataSetChanged();
        }
    }

    public void setFooterViewVisible(int visible) {
        if (mFooterView != null) {
            boolean changed = mFooterView.getVisibility() != visible;
            mFooterView.setVisibility(visible);
            if (changed) {
                notifyDataSetChanged();
            }
        }
    }

    private boolean footerViewIsVisible() {
        return mFooterView != null && mFooterView.getVisibility() == View.VISIBLE;
    }


    public void addDataList(List<T> datalist, boolean clear) {
        if (datalist != null) { //把空的数据排除出去
            List<T> copyList = new CopyOnWriteArrayList<T>(datalist);
            datalist.clear();
            for (T m : copyList) {
                if (m != null) {
                    datalist.add(m);
                }
            }
        }
        if (mDataList == null) {
            mDataList = datalist;
        }
        else if (datalist != mDataList) {
            if (clear && datalist != mDataList) {
                mDataList.clear();
            }
            if (datalist != null) {
                mDataList.addAll(datalist);
            }
        }
    }

    /**
     * 2014.4.10：在指定位置增加UI单元
     *
     * @param item
     * @param position
     */
    public void addItem(T item, int position) {
        if (mDataList == null) {
            mDataList = new ArrayList<T>();
            mDataList.add(item);
        } else if (position >= 0 && position < mDataList.size()) {
            mDataList.add(position, item);
        } else {
            mDataList.add(item);
        }
    }

    /**
     * 删除指定的UI单元
     *
     * @param item
     */
    public void removeItem(T item) {
        if (mDataList != null) {
            mDataList.remove(item);
        }
    }

    public void clearDataList() {
        if (mDataList != null && mDataList.size() > 0) {
//	    mNotifyDataSetChanging = false;
            mDataList.clear();
            notifyDataSetChanged();
            mDataList = null;
        }

    }

    public void replaceWith(List<T> oldDatas, List<T> newDatas) {
        if (mDataList == null) {
            addDataList(newDatas, true);
            notifyDataSetChanged();
        } else if (oldDatas != null && newDatas != null) {
            List<T> copyArray = new ArrayList<T>();
            copyArray.addAll(oldDatas);
            int startpos = mDataList.size() - 1;
            int pos = 0;
            for (T o : oldDatas) {
                pos = mDataList.indexOf(o);
                if (pos < startpos) {
                    startpos = pos;
                }
            }
            for (T o : copyArray) {
                mDataList.remove(o);
            }
            List<T> newCopyArray = new ArrayList<T>();
            boolean equal = false;
            int k, N = 0;
            T o2;
            for (T o1 : newDatas) { //查相同的，以避免多数情况下内容为相同的情况
                equal = false;
                N = copyArray.size();
                for (k = 0; k < N && !equal; k++) {
                    o2 = copyArray.get(k);
                    if (o1.equals(o2)) {
                        equal = true;
                        newCopyArray.add(o2);
                        copyArray.remove(o2);
                    }
                }
                if (!equal) {
                    newCopyArray.add(o1);
                }
            }
            if (mListViewRecycler != null && copyArray.size() > 0) {
                for (T o : copyArray) {
                    mListViewRecycler.removeScrapView(o);
                }
            }
            if (startpos < 0) {
                startpos = 0;
            }
            try {
                if (startpos >= mDataList.size()) {
                    mDataList.addAll(newCopyArray);
                } else {
                    mDataList.addAll(startpos, newCopyArray);
                }
                Log.e(TAG, "replaceWith startpos=" + startpos);
            } catch (Exception e) {
                Log.e(TAG, "replaceWith error,reason=" + e);
                mDataList.addAll(0, newCopyArray);
            }
            notifyDataSetChanged();
        }
    }


    @Override
    public int getCount() {
        int count = mDataList == null ? 0 : mDataList.size();
        if (headerViewIsVisible()) {
            count++;
        }
        if (footerViewIsVisible()) {
            count++;
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        int numheader = headerViewIsVisible() ? 1 : 0;
        int numdata = mDataList != null ? mDataList.size() : 0;

        if (position >= numheader) {
            int adjposition = position - numheader;
            if (adjposition >= numdata) { //返回
                return mFooterView;
            } else {
                return mDataList.get(adjposition);
            }
        } else {
            return mHeaderView;
        }
    }

    @Override
    public long getItemId(int position) {
        int numheader = headerViewIsVisible() ? 1 : 0;
        int numdata = mDataList != null ? mDataList.size() : 0;
        if (position >= numheader) {
            int adjposition = position - numheader;
            if (adjposition >= numdata) { //返回
                return -1;
            } else {
                return adjposition;
            }
        } else {
            return -1;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int numheader = headerViewIsVisible() ? 1 : 0;
        int numdata = mDataList != null ? mDataList.size() : 0;
        if (position >= numheader) {
            int adjposition = position - numheader;
            if (adjposition >= numdata && mFooterView != null) {
                return mFooterView;
            } else {
                if (adjposition >= mDataList.size()) {
                    adjposition = mDataList.size() - 1;
                }
                if (adjposition < 0) {
                    adjposition = 0;
                }
                AbsListItem itemdata = mDataList.get(adjposition);
                int vt = getItemViewType(position);
                boolean viewcreated = false;
                boolean dontupdate = false;
                if (mListViewRecycler != null) {
                    convertView = mListViewRecycler.getScrapView(itemdata, position);
                    dontupdate = convertView != null;
                }
                if (convertView != null && convertView.getParent() != null) { //此视图被错用了,不能重用，否则会报错，该不会执行到
                    convertView = null;
                    dontupdate = false;
                }
                if (convertView == null) {
                    convertView = itemdata.getView(position, parent);
                    convertView.setTag(R.id.viewtype, vt);
                    viewcreated = true;
                } else {
                    Integer viewtype = (Integer) convertView.getTag(R.id.viewtype);
                    if (viewtype == null || viewtype != vt) {
                        convertView = null;
                        if (mListViewRecycler != null) {
                            convertView = mListViewRecycler.getScrapView(itemdata, position);
                            dontupdate = convertView != null;
                        }
                        if (convertView == null) {
                            convertView = itemdata.getView(position, parent);
                            viewcreated = true;
                        } else {
                            if (!dontupdate) {
                                itemdata.updateView(convertView, position, parent);
                            } else if (mListViewRecycler != null) {
                                mListViewRecycler.postInvalidate(itemdata, convertView, parent, position);
                            }
                        }
                        convertView.setTag(R.id.viewtype, vt);
                    } else {
                        if (!dontupdate) {
                            itemdata.updateView(convertView, position, parent);
                        } else if (mListViewRecycler != null) {
                            mListViewRecycler.postInvalidate(itemdata, convertView, parent, position);
                        }
                    }
                }
                if (viewcreated && mListViewRecycler != null) {
                    mListViewRecycler.addScrapView(itemdata, convertView, position);
                }
                return convertView;
            }
        } else if (position < numheader) {
            return mHeaderView;
        } else {
            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mListViewRecycler != null) {
            return AdapterView.ITEM_VIEW_TYPE_IGNORE;
        }
        int numheader = headerViewIsVisible() ? 1 : 0;
        int numdata = mDataList != null ? mDataList.size() : 0;
        if (position >= numheader) {
            int adjposition = position - numheader;
            if (adjposition >= numdata) {
                return AdapterView.ITEM_VIEW_TYPE_IGNORE;
            } else {
                AbsListItem itemdata = mDataList.get(adjposition);
                if (itemdata == null)
                    return AdapterView.ITEM_VIEW_TYPE_IGNORE;
                else {
                    int index = mItemViewTypes.indexOf(itemdata.getClass().getName());
                    if (index < 0) {
                        if (itemdata.getItemViewType() >= 0) {
                            mItemViewTypes.add(itemdata.getClass().getName());
                            index = mItemViewTypes.size() - 1;
                        } else {
                            index = AdapterView.ITEM_VIEW_TYPE_IGNORE;
                        }
                    }
                    if (index >= mMaxViewTypeCount) {
                        Log.e(TAG, "Warning! Please override AbstractListDataFactory.getSupportedViewTypeCount()," +
                                "or lead to leak memory!(maxtypecount=" + mMaxViewTypeCount + ",index=" + index);
                        index = AdapterView.ITEM_VIEW_TYPE_IGNORE;
                    }
                    return index;
                }
            }
        } else if (position < numheader) {
            return AdapterView.ITEM_VIEW_TYPE_IGNORE;
        } else {
            return AdapterView.ITEM_VIEW_TYPE_IGNORE;
        }
    }


    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        try {
            super.unregisterDataSetObserver(observer);
        } catch (Exception e) {

        }
    }

    public void setViewTypeCount(int supportViewType) {
        mMaxViewTypeCount = supportViewType;
        if (mMaxViewTypeCount < 3) {
            mMaxViewTypeCount = 3;
        }
    }

    @Override
    public int getViewTypeCount() {
        return mMaxViewTypeCount;
    }


    @Override
    public boolean isEnabled(int position) {
        int numheader = headerViewIsVisible() ? 1 : 0;
        int numdata = mDataList != null ? mDataList.size() : 0;
        if (position >= numheader) {
            int adjposition = position - numheader;
            if (adjposition >= numdata) { //底部不可选中
                return false;
            } else {
                AbsListItem itemdata = mDataList.get(adjposition);
                if (itemdata == null)
                    return false;
                else
                    //用于标记该列表项是否可以被选中
		            return itemdata.isEnabled();
//                    return false;
            }
        } else if (position < numheader) {
            return false;//列表标题不允许选中
        } else {
            return false;
        }

    }

}
