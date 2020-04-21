package com.yundao.ydwms.common.titlebar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yundao.ydwms.common.R;
import com.yundao.ydwms.common.utils.DipPxUtil;;


/**
 * @author liangjianhua
 * 自定义Toolbar
 */
public class SimpleToolbar extends Toolbar {
    /**
     * 左侧Title
     */
    protected TextView mTxtLeftTitle;
    /**
     * 中间Title
     */
    protected TextView mTxtMiddleTitle;
    /**
     * 右侧Title
     */
    protected TextView mTxtRightTitle;
    /**
     * 右侧顶部Title
     */
    protected TextView mTxtRightTopTitle;

    public SimpleToolbar(Context context) {
        this(context,null);
    }

    public SimpleToolbar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SimpleToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_simple_toolbar, this);
        mTxtLeftTitle = findViewById(R.id.txt_left_title);
        mTxtMiddleTitle = findViewById(R.id.txt_main_title);
        mTxtRightTitle = findViewById(R.id.txt_right_title);
        mTxtRightTopTitle = findViewById(R.id.txt_right_top_title);
    }


    //设置中间title的内容
    public SimpleToolbar setTitleMainText(String text) {
        this.setTitle(" ");
        mTxtMiddleTitle.setVisibility(View.VISIBLE);
        mTxtMiddleTitle.setText(text);
        return this ;
    }

    //设置中间title的内容
    public SimpleToolbar setTitleMainTextDrawable(Drawable drawable, int gravity) {
        this.setTitle(" ");
        mTxtMiddleTitle.setVisibility(View.VISIBLE);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
        Drawable left = null, top = null , right = null , bottom = null;
        switch ( gravity ){
            case Gravity.LEFT :
                left = drawable ;
                break;
            case Gravity.RIGHT:
                right = drawable ;
                break;
            case Gravity.TOP:
                top = drawable ;
                break ;
            case Gravity.BOTTOM:
                bottom = drawable ;
                break;
        }

        mTxtMiddleTitle.setCompoundDrawables(left, top, right, bottom);

        return this ;
    }

    public SimpleToolbar setMainTitleClickListener( View.OnClickListener listener ){
        if( mTxtMiddleTitle != null ){
            mTxtMiddleTitle.setOnClickListener( listener );
        }
        return this ;
    }

    //设置中间title的内容文字的颜色
    public SimpleToolbar setMainTitleColor(int color) {
        mTxtMiddleTitle.setTextColor(color);
        return this ;
    }

    //设置title左边文字
    public SimpleToolbar setLeftTitleText(String text) {
        mTxtLeftTitle.setVisibility(View.VISIBLE);
        mTxtLeftTitle.setText(text);
        return this ;
    }

    //设置title左边文字颜色
    public SimpleToolbar setLeftTitleColor(int color) {
        mTxtLeftTitle.setTextColor(color);
        return this ;
    }

    //设置title左边图标
    public SimpleToolbar setLeftTitleDrawable(int res) {
        Drawable dwLeft = ContextCompat.getDrawable(getContext(), res);
        dwLeft.setBounds(0, 0, dwLeft.getMinimumWidth(), dwLeft.getMinimumHeight());
        mTxtLeftTitle.setCompoundDrawables(dwLeft, null, null, null);
        return this ;
    }


    //设置title右边文字
    public SimpleToolbar setRightText(String text) {
        mTxtRightTitle.setVisibility(View.VISIBLE);
        mTxtRightTitle.setText(text);
        return this ;
    }

    //设置title右边顶部文字
    public SimpleToolbar setRightTopText(String text) {
      mTxtRightTopTitle.setVisibility(View.VISIBLE);
      mTxtRightTopTitle.setText(text);
      return this ;
    }

    //设置title右边顶部是否隐藏
    public SimpleToolbar setRightTopTextVisibility(boolean rightTopVisible) {
      if( mTxtRightTopTitle != null ) {
        mTxtRightTopTitle.setVisibility(rightTopVisible ? VISIBLE : GONE);
      }
      return this ;
    }

    //设置title右边文字
    public TextView getRightText() {
        return mTxtRightTitle ;
    }

    public TextView getLeftText() {
    return mTxtLeftTitle ;
  }

    //设置title右边文字颜色
    public SimpleToolbar setRightTitleColor(int color) {
        mTxtRightTitle.setTextColor(color);
        return this ;
    }

    //设置title右边图标
    public SimpleToolbar setRightTextDrawable(int res) {
        Drawable dwRight = null ;
        try {
            dwRight = ContextCompat.getDrawable(getContext(), res);
        }catch ( Resources.NotFoundException e ){
            e.printStackTrace();
        }
        if( dwRight != null ) {
            dwRight.setBounds(0, 0, dwRight.getMinimumWidth(), dwRight.getMinimumHeight());
            mTxtRightTitle.setCompoundDrawablePadding(DipPxUtil.dipToPx(getContext(), 5));//设置图片和text之间的间距
            mTxtRightTitle.setCompoundDrawables(null, null, dwRight, null);
        }else{
            mTxtRightTitle.setCompoundDrawables(null, null, null, null);
        }
        return this ;
    }

    //设置title右边顶部图标
    public SimpleToolbar setRightTopTextDrawable(int res) {
/*      Drawable dwRightTop = null ;
      try {
        dwRightTop = ContextCompat.getDrawable(getContext(), res);
      }catch ( Resources.NotFoundException e ){
        e.printStackTrace();
      }
      if( dwRightTop != null ) {
        dwRightTop.setBounds(0, 0, dwRightTop.getMinimumWidth(), dwRightTop.getMinimumHeight());
        mTxtRightTopTitle.setCompoundDrawablePadding(DipPxUtil.dipToPx(getContext(), 5));//设置图片和text之间的间距
        mTxtRightTopTitle.setCompoundDrawables(null, dwRightTop, null, null);
      }else{
        mTxtRightTopTitle.setCompoundDrawables(null, null, null, null);
      }*/
      mTxtRightTopTitle.setBackgroundResource(res);
      return this ;
    }

    //设置title右边点击事件
    public SimpleToolbar setRightTitleClickListener(OnClickListener onClickListener){
        mTxtRightTitle.setOnClickListener(onClickListener);
        return this ;
    }

    public TextView getMiddleTitle() {
        return mTxtMiddleTitle;
    }

    public SimpleToolbar setRightVisible(boolean rightVisible) {
        if( mTxtRightTitle != null ) {
            mTxtRightTitle.setVisibility(rightVisible ? VISIBLE : GONE);
        }
        return this ;
    }

    public SimpleToolbar setLeftVisible(boolean leftVisible) {
        if( mTxtLeftTitle != null ) {
            mTxtLeftTitle.setVisibility(leftVisible ? VISIBLE : GONE);
        }
        return this ;
    }

    //设置title左边点击事件
    public SimpleToolbar setLeftTitleClickListener(OnClickListener onClickListener){
        if( mTxtLeftTitle != null ) {
            mTxtLeftTitle.setOnClickListener(onClickListener);
        }
        return this ;
    }

    public SimpleToolbar setOnCenterClickListener(OnClickListener onClickListener) {
        if( mTxtMiddleTitle != null ) {
            mTxtMiddleTitle.setOnClickListener(onClickListener);
        }
        return this ;
    }


    public SimpleToolbar setRightTextSize(int i) {
        if( mTxtRightTitle != null ) {
            mTxtRightTitle.setTextSize( i );
        }
        return this ;
    }
}
