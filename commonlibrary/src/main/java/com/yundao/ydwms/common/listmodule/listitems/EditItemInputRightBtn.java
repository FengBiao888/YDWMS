package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yundao.ydwms.common.R;

;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class EditItemInputRightBtn extends EditItemInput {

    private int rightDrawableId ;
    private View.OnClickListener mRightBtnClickListener ;
    private boolean rightBtnVisiable;

    public EditItemInputRightBtn(Context context, String typeName, boolean isMust, String inputHint, int rightId) {
        super(context, typeName, isMust, inputHint);
        rightDrawableId = rightId ;
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        View view = LayoutInflater.from( context ).inflate(R.layout.item_edititem_input_right_btn, null);
        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);
        if( view == null ){
            view = viewParent ;
        }
        ImageView rightBtn = view.findViewById( R.id.item_extrafilebtn );
        rightBtn.setImageResource( rightDrawableId );
        if( mRightBtnClickListener != null ){
            rightBtn.setOnClickListener( mRightBtnClickListener );
        }
        rightBtn.setVisibility( rightBtnVisiable ? View.VISIBLE : View.INVISIBLE );

    }

    public void setRightBtnClickListener(View.OnClickListener mRightBtnClickListener) {
        this.mRightBtnClickListener = mRightBtnClickListener;
    }

    public void setRightBtnVisiable(boolean rightBtnVisiable) {
        this.rightBtnVisiable = rightBtnVisiable;
    }
}
