package com.yundao.ydwms.common.base;

import android.support.v4.app.Fragment;

import com.yundao.ydwms.common.R;;


/**
 * Created: liangjianhua
 * Function: title 基类
 * Desc:
 */

public abstract class BaseFragmentActivity extends BaseActivity {

    @Override
    protected void onResume() {
        super.onResume();
        if (getCurFragment() != null) {
            getCurFragment().onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getCurFragment() != null) {
            getCurFragment().onStop();
        }
    }

    public abstract Fragment getCurFragment();

}
