package com.nf.android.common.utils;

import android.content.Context;
import android.view.WindowManager;

/**
 * dip与px的转换工具类
 * 
 * @author coder
 * 
 */
public class DipPxUtil {

	/**
	 * 将dip转换成px
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dipToPx(Context context, float dpValue) {
		if( context == null ) return (int) dpValue;
		float scale = context.getResources().getDisplayMetrics().density;

		return (int) (dpValue * scale + 0.5f);

	}

	/**
	 * 将px转换成dip
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int pxToDip(Context context, float pxValue) {

		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);

	}

	public static int getScreenHeightSizePX(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		return dipToPx(context,height);
	}

}
