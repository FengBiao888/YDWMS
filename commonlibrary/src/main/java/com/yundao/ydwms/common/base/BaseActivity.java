package com.yundao.ydwms.common.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yundao.ydwms.common.titlebar.SimpleToolbar;
import com.yundao.ydwms.common.titlebar.utils.StatusBarUtil;
import com.yundao.ydwms.common.R;
;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created: liangjianhua
 * Function: title 基类
 * Desc:
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected SimpleToolbar titleBar;
    protected Activity mContext;
    protected boolean mIsFirstShow = true;
    private Unbinder mUnBinder;
    protected int type = 0;
    protected boolean isWhite = false;
    protected View mContentView;
    protected String TAG = getClass().getSimpleName();

    protected abstract void setTitleBar();

    protected boolean isShowLine() {
        return true;
    }

    @LayoutRes
    protected abstract int getLayout();

    protected void loadData() {
    }

    protected void beforeSetView() {
    }

    protected void beforeInitView() {
    }

    protected void initIntent(Intent intent) {

    }

    public abstract void initView(Bundle var1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntent(getIntent());
//    PushAgent.getInstance(getActivity()).onAppStart();
        Log.d(TAG, "onCreate");
        this.mContext = this;
        StatusBarUtil.setStatusBarColor(this, Color.WHITE, true);
        this.beforeSetView();
        mContentView = View.inflate(mContext, getLayout(), null);
        this.setContentView(mContentView);
//    mContentView.setBackgroundResource(R.color.login_bg);
        mUnBinder = ButterKnife.bind(this);
        initTitle();
        this.beforeInitView();
//    Drawable drawableTop = new ColorDrawable(Color.LTGRAY);
//    DrawableUtil.setDrawableWidthHeight(drawableTop, SizeUtil.getScreenWidth(), SizeUtil.dp2px(0.5f));
        this.initView(savedInstanceState);
    }

    protected void initTitle() {
        titleBar = mContentView.findViewById(R.id.titleBar);
        if (titleBar == null) {
            return;
        }
        titleBar.setLeftTitleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setTitleBar();
    }

    protected TextView getTitleView() {
        return titleBar.getMiddleTitle();
    }

    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }

    @Override
    protected void onResume() {
        if (this.mIsFirstShow) {
            this.mIsFirstShow = false;
            this.loadData();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
