package com.yundao.ydwms;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.yundao.ydwms.common.titlebar.SimpleToolbar;


/**
 * @author liangjianhua
 * 考勤界面的标题栏
 */
public class SearchToolBar extends SimpleToolbar {
    /**
     * 左侧EditText
     */
    private EditText editText;

    public SearchToolBar(Context context) {
        this(context,null);
    }

    public SearchToolBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SearchToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_search_toolbar, this);
        editText = findViewById(R.id.txt_left_title);
        mTxtRightTitle = findViewById(R.id.txt_right_title);
    }

    public void setOnTextChangeListener(TextWatcher textWatcher){
        editText.addTextChangedListener( textWatcher );
    }

    @Override
    public SimpleToolbar setTitleMainText(String text) {
        editText.setHint( text  );
        return this ;
    }
}
