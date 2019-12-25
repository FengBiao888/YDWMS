package com.nf.android.common.listmodule;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView.OnScrollListener;

import com.nf.android.common.listmodule.listitems.AbsListItem;
import com.nf.android.common.utils.ImageUtil;
import com.nf.android.common.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


public class DefaultListViewRecycler extends AbstractListViewRecycler implements FlingObserver.IFlingListener, Runnable {
    private String TAG = getClass().getSimpleName();

    static int MAX_RECYCLER_SIZE = 60;
    static int MIN_RECYCLER_SIZE = 20;
    final int MAX_UPDATEVIEW_DELAY = 3000;
    final int MIN_UPDATEVIEW_INTERVAL = 10;
    private Map<AbsListItem, View> mScrapViews;
    private Map<AbsListItem, Integer> mInvalidateViews;
    private FlingObserver mFlingObserver;
    private Handler mHandler;
    private Runnable mUpdateViewRunnable;
    private int mScrollState;

    public DefaultListViewRecycler(Context activity) {
        super(activity);
        mScrapViews = new ConcurrentHashMap<AbsListItem, View>(8);
        mInvalidateViews = new ConcurrentHashMap<AbsListItem, Integer>();
        mFlingObserver = FlingObserver.getInstance();
        mFlingObserver.addFlingListener(this);
        mHandler = new Handler(activity.getMainLooper());
        mUpdateViewRunnable = this;
        mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    }

    public static void setRecyclerLimit(int min, int max) {
        MIN_RECYCLER_SIZE = min;
        MAX_RECYCLER_SIZE = max;
    }

    @Override
    public void addScrapView(AbsListItem itemdata, View view, int position) {
        if (mScrapViews.containsKey(itemdata)) {
            mScrapViews.remove(itemdata);
        }
        mScrapViews.put(itemdata, view);
        if (mScrapViews.size() > MAX_RECYCLER_SIZE) {
            zapScrapViews(itemdata);
        }
    }


    @Override
    public void removeScrapView(AbsListItem itemdata) {
        mScrapViews.remove(itemdata);
    }

    @Override
    public View getScrapView(AbsListItem itemdata, int position) {
        View v = mScrapViews.get(itemdata);
        if (v != null && v.getParent() != null) { //仍在容器中的View不能使用
            return null;
        } else {
            return v;
        }
    }

    private void zapScrapViewsInUI(final AbsListItem exceptItem) {
        if (mScrapViews.size() > MIN_RECYCLER_SIZE) {
            Set<Entry<AbsListItem, View>> set = mScrapViews.entrySet();
            List<AbsListItem> removeList = new ArrayList<AbsListItem>();
            View view;
            if (set != null) {
                try {
                    set = new CopyOnWriteArraySet<Entry<AbsListItem, View>>(set);
                    for (Entry<AbsListItem, View> e : set) {
                        view = e.getValue();
                        if (view.getParent() == null && e.getKey() != exceptItem) {
                            removeList.add(e.getKey());
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "zapScrapViews error,reason=" + e);
                }
            }
            View v = null;
            for (AbsListItem e : removeList) {
                v = mScrapViews.get(e);
                if (v instanceof ViewGroup) {
                    ImageUtil.recycleAllImageView((ViewGroup) v);
                } else {
                    ImageUtil.recycleImage(v);
                }
                mScrapViews.remove(e);
                if (Log.isDebug) {
                    Log.i(TAG, "zapScrapViews itemclass=" + e.getClass().getSimpleName());
                }
                if (mScrapViews.size() <= MIN_RECYCLER_SIZE) {
                    break;
                }
            }
        }
    }

    private boolean isUIThread() {
        long curid = Thread.currentThread().getId();
        long threadid = mHandler.getLooper().getThread().getId();
        return curid == threadid;
    }

    public void zapScrapViews(final AbsListItem exceptItem) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                zapScrapViewsInUI(exceptItem);
            }
        });
    }

    @Override
    public void onActivityDestroy() {
        mFlingObserver.removeFlingListener(this);
    }

    @Override
    public void postInvalidate(final AbsListItem itemdata, final View view, final ViewGroup parent, final int position) {
        if (isUIThread()) {
            postInvalidateInUI(itemdata, view, parent, position);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    postInvalidateInUI(itemdata, view, parent, position);
                }
            });
        }
    }

    public void postInvalidateInUI(final AbsListItem itemdata, final View view, final ViewGroup parent, final int position) {
        if (mInvalidateViews.get(itemdata) != null) {
            mInvalidateViews.remove(itemdata);
        }
        int delay = 0;
        mHandler.removeCallbacks(mUpdateViewRunnable);
        if (mScrollState == OnScrollListener.SCROLL_STATE_FLING) {
            mInvalidateViews.put(itemdata, position);
            delay = MAX_UPDATEVIEW_DELAY;
            mHandler.postDelayed(mUpdateViewRunnable, delay);
        } else {
            itemdata.updateView(view, position, parent);
            if (mInvalidateViews.size() > 0) {
                delay = MIN_UPDATEVIEW_INTERVAL;
                mHandler.postDelayed(mUpdateViewRunnable, delay);
            }
        }
        Log.i(TAG, "postInvalidate count=" + mInvalidateViews.size() + ",delay=" + delay);
    }


    @Override
    public void onScrollStart(int scrollstate) {
        mScrollState = scrollstate;
        mHandler.removeCallbacks(mUpdateViewRunnable);
    }

    @Override
    public void onFlingEnd() {
        mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
        mHandler.removeCallbacks(mUpdateViewRunnable);
        if (isUIThread()) {
            run();
        } else {
            mHandler.post(this);
        }
    }

    @Override
    public void run() { //Runnable
        View view = null;
        ViewParent vp = null;
        Integer pos = null;
        AbsListItem item;
        Set<Entry<AbsListItem, Integer>> set = mInvalidateViews.entrySet();
        List<AbsListItem> removedList = new ArrayList<AbsListItem>();
        boolean updated = false;
        if (set != null) {
            try {
                set = new CopyOnWriteArraySet<Entry<AbsListItem, Integer>>(set);
                for (Entry<AbsListItem, Integer> e : set) {
                    item = e.getKey();
                    pos = e.getValue();
                    view = mScrapViews.get(item);
                    if (pos != null && view != null) {
                        vp = view.getParent();
                        if (vp != null) {
                            if (!updated) {
                                try {
                                    item.updateView(view, pos.intValue(), (ViewGroup) vp);
                                } catch (Exception e1) {
                                    Log.e(TAG, "run error2,reason=" + e1);
                                }
                                removedList.add(item);
                                updated = true;
                            }
                        } else {
                            removedList.add(item);
                        }
                    } else {
                        removedList.add(item);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "run error,reason=" + e);
            }
        }
        for (AbsListItem e : removedList) {
            mInvalidateViews.remove(e);
        }
        if (mInvalidateViews.size() > 0) {
            if (Log.isDebug) {
                Log.i(TAG, "update view later, count=" + mInvalidateViews.size() + ",caller=" + this);
            }
            mHandler.postDelayed(mUpdateViewRunnable, MIN_UPDATEVIEW_INTERVAL);
        } else if (Log.isDebug) {
            Log.i(TAG, "update view complete ,caller=" + this);
        }
    }

}
