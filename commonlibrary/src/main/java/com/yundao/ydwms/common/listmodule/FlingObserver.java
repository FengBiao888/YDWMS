package com.yundao.ydwms.common.listmodule;

import android.view.View;
import android.widget.AbsListView.OnScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlingObserver {
    static FlingObserver	gInstance ;
    Map<View, Boolean> mFlingViews ;
    List<IFlingListener> mFlingListeners;
    public static FlingObserver getInstance(){
	if (gInstance == null){
	    gInstance = new FlingObserver();
	}
	return gInstance ;
    }
    
    public interface IFlingListener{
	void onScrollStart(int scrollstate);
	void	onFlingEnd();
    }
    
    FlingObserver(){
	mFlingViews = new HashMap<View, Boolean>();
	mFlingListeners = new ArrayList<IFlingListener>();
    }
    
    public void addFlingListener (IFlingListener listener){
	if (listener != null && !mFlingListeners.contains(listener)){
	    mFlingListeners.add(listener);
	}
    }
    
    public void removeFlingListener(IFlingListener listener){
	if (listener != null){
	    mFlingListeners.remove(listener);
	}
    }
    
    public void onScrollStateChanged(View view, int scrollState) {
	View rootView = view.getRootView() ;
	if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL || scrollState == OnScrollListener.SCROLL_STATE_FLING ){
	    try{
		for (IFlingListener listener : mFlingListeners) {
		    try{
			listener.onScrollStart(scrollState);
		    }catch(Exception e){
		    }
		}
	    }catch(Exception e){
	    }
	}
	if (scrollState == OnScrollListener.SCROLL_STATE_FLING){
	    mFlingViews.put(rootView, Boolean.TRUE);
	}else if (scrollState == OnScrollListener.SCROLL_STATE_IDLE){
	    mFlingViews.remove(rootView);
	    try{
		for (IFlingListener listener : mFlingListeners) {
		    try{
			listener.onFlingEnd();
		    }catch(Exception e){
		    }
		}
	    }catch(Exception e){
	    }
	}
    }
    
    public boolean isFling(View view){
	View rootView = view != null ? view.getRootView() : null;
	if (rootView == null){
	    return false;
	}else{
	    Boolean flag = mFlingViews.get(rootView);
	    if (flag == null)
		return false;
	    else
		return flag ;
	}
    }
}
