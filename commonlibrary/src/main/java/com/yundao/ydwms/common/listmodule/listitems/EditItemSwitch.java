package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.yundao.ydwms.common.R;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class EditItemSwitch extends AbsListItem {

    private boolean checked ;

    public EditItemSwitch(Context context, String typeName) {
        super(context, typeName);
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        View view = LayoutInflater.from( context ).inflate(R.layout.item_edititem_checkbox, null);
        updateView(view, i, viewGroup);
/*        CheckedTextView checkedTextView = (CheckedTextView) view.findViewById( R.id.bu_toggle );
        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                checkedTextView.setChecked( !isChecked() );
                setCheckState( !isChecked() );
            }
        });*/
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);

        CheckedTextView checkedTextView = view.findViewById( R.id.bu_toggle );
        checkedTextView.setChecked( isChecked() );
        checkedTextView.setText( typeName );

    }

    public boolean isChecked(){
        return checked ;
    }


    public void setCheckState(boolean checkState) {
        if ( viewParent == null ){
            checked = checkState ;
        }else {
            CheckedTextView checkedTextView = viewParent.findViewById(R.id.bu_toggle);
            checkedTextView.setChecked(checkState);
            checked = checkState;
        }
    }

    @Override
    public boolean isEnabled() {
        return true ;
    }
}
