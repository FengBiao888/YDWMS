package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.yundao.ydwms.common.R;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class EditItemEditText extends AbsEditItem {

    private int maxLength ;

    public EditItemEditText(Context context, String typeName, boolean isMust, String inputHint) {
        super(context, typeName, isMust, inputHint);
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        View view = LayoutInflater.from( context ).inflate(R.layout.item_edititem_edit, null);
        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);
        TextView itemName = view.findViewById( R.id.item_name );
        if( TextUtils.isEmpty( typeName ) ){
            itemName.setVisibility( View.INVISIBLE );
        }else {
            itemName.setText( isMust && spannableString != null ? spannableString : typeName );
        }
        final EditText editText = view.findViewById( R.id.item_right );
        if (editText != null) {
            editText.setHint( inputHint );
            if( !TextUtils.isEmpty(inputMessage) ){
                editText.setText(inputMessage);
            }
            if( maxLength != 0 ){
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter( maxLength )});
            }

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

            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                   // 解决scrollView中嵌套EditText导致不能上下滑动的问题
                     if (canVerticalScroll(editText))
                         v.getParent().requestDisallowInterceptTouchEvent(true);
                     switch (event.getAction() & MotionEvent.ACTION_MASK) {
                         case MotionEvent.ACTION_UP:
                             v.getParent().requestDisallowInterceptTouchEvent(false);
                             break;
                     }
                     return false;
                }
            });
        }
    }

    /**
     * * EditText竖直方向是否可以滚动
     * * @param editText 需要判断的EditText
     * * @return true：可以滚动  false：不可以滚动     */
    public static  boolean canVerticalScroll(EditText editText) {
        //滚动的距离
         int scrollY = editText.getScrollY();
         //控件内容的总高度
         int scrollRange = editText.getLayout().getHeight();
         //控件实际显示的高度
         int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() -editText.getCompoundPaddingBottom();
         //控件内容总高度与实际显示高度的差值
         int scrollDifference = scrollRange - scrollExtent;
         if(scrollDifference == 0) {
             return false;
         }
         return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
