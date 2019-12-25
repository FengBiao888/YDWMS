package com.nf.android.common.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nf.android.common.R;
import com.nf.android.common.titlebar.SimpleToolbar;
import com.nf.android.common.utils.ConstantUtil;
import com.nf.android.common.utils.SysUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

;


/**
 * Created: liangjianhua
 * Function: title 基类
 * Desc:
 */

public abstract class ImmersiveBaseActivity extends AppCompatActivity {

    /**
     * 当用户注销登录和收到“1002”类型的消息时，发送的重新登录的广播ACTION
     */
//    public static String ACTION_LOGOUT;

    private static int priority = 10;//定位阻止有序广播优先级

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

//    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (ACTION_LOGOUT.equals(intent.getAction())) {
//                finish();
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SysUtil.isPad()) {
          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        initIntent(getIntent());
//    PushAgent.getInstance(getActivity()).onAppStart();
        Log.d(TAG, "onCreate");
        this.mContext = this;
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

//        ACTION_LOGOUT = ConstantUtil.getInstance().getActionLogout();

//        IntentFilter filter = new IntentFilter();
//        filter.setPriority(++priority);
//        filter.addAction(ACTION_LOGOUT);
//        registerReceiver(logoutReceiver, filter);
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

//        priority --;
//        unregisterReceiver(logoutReceiver);
        System.gc();
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

    /**
     * @param activity
     * @param bundle
     */
    public static void startActivity(Activity mContext, Class<? extends Activity> activity, Bundle bundle) {
        if (mContext == null) {
            return;
        }
        Intent intent = new Intent(mContext, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        mContext.startActivity(intent);
    }

    public static void startActivity(Activity mContext, Class<? extends Activity> activity) {
        startActivity(mContext, activity, null);
    }

    public void startActivity(Class<? extends Activity> activity, Bundle bundle) {
        startActivity(mContext, activity, bundle);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
