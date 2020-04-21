package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.yundao.ydwms.common.R;;


public class ItemCheckbox extends AbsListItem {

    private boolean isChecked ;
    private int checkBoxSelecterId ;

    private int paddingLeftRight ;
    private float textSize ;
    private int height ;

    public ItemCheckbox(Context context, String typeName) {
        super(context, typeName);
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {
        View view = LayoutInflater.from( context ).inflate(R.layout.item_check_textview, null);
        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);
        CheckedTextView checkedTextView = view.findViewById( R.id.text1 );
        checkedTextView.setText( typeName );
        checkedTextView.setChecked( isChecked );
        if( checkBoxSelecterId != 0 ) {
            checkedTextView.setCheckMarkDrawable(checkBoxSelecterId);
        }

        if( paddingLeftRight != 0 ){
            checkedTextView.setPadding( paddingLeftRight, 0, paddingLeftRight, 0 );
        }

        if( textSize != 0 ){
            checkedTextView.setTextSize( textSize );
        }

        if( height != 0 ){
            ViewGroup.LayoutParams layoutParams = checkedTextView.getLayoutParams();
            layoutParams.height = height ;
            checkedTextView.setLayoutParams( layoutParams );
        }
    }

    public void setCheckMarkDrawable(int checkBoxSelecterId) {
        this.checkBoxSelecterId = checkBoxSelecterId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setPaddingLeftRight(int paddingLeftRight) {
        this.paddingLeftRight = paddingLeftRight;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
