package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.yundao.ydwms.common.R;;


/**
 * Created by liangjianhua on 2018/12/20.
 */

public class SearchItem extends AbsEditItem {

    private int backgroundId ;
    private TextView.OnEditorActionListener onEditorActionListener;

    public SearchItem(Context context, String inputHint) {
        super(context, "", false, inputHint);
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {
        View view = View.inflate( context, R.layout.item_search, null);
        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);

        EditText editText = view.findViewById( R.id.item_right );
        editText.setHint( inputHint );
        editText.setText( inputMessage );
        editText.setBackgroundResource( backgroundId );

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputMessage = charSequence.toString() ;
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText.setOnEditorActionListener( onEditorActionListener );
    }

    public void setBackgroundId(int backgroundId) {
        this.backgroundId = backgroundId;
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener onEditorActionListener) {
        this.onEditorActionListener = onEditorActionListener;
    }
}
