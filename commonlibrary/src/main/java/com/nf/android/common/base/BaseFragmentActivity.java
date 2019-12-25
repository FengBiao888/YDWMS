package com.nf.android.common.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nf.android.common.titlebar.SimpleToolbar;
import com.nf.android.common.titlebar.utils.StatusBarUtil;
import com.nf.android.common.R;;

import butterknife.ButterKnife;
import butterknife.Unbinder;


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
