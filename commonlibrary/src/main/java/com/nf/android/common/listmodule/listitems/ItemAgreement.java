package com.nf.android.common.listmodule.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nf.android.common.R;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class ItemAgreement extends AbsListItem {
    private View view;


    public ItemAgreement(Context context, String tipText) {
        super(context, tipText);
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        view = LayoutInflater.from( context ).inflate(R.layout.item_tips_view, null);
        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView( view, position, parent );
        TextView tipTextView = view.findViewById( R.id.tv_tips );
        tipTextView.setText( getTypeName() );
    }
}
