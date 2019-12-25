package com.nf.android.common.listmodule.listitems;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.nf.android.common.R;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class EditItemEditPick extends AbsEditItem {

    private EditItemPick relatePick ; //关联的选择项
    private String editInput;
    private EditText itemInput;

    public EditItemEditPick(Context context, String typeName, boolean isMust, String inputHint) {
        super(context, typeName, isMust, inputHint);
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {
        View view = LayoutInflater.from( context ).inflate(R.layout.item_edititem_edit_pick, null);
        updateView( view, i, viewGroup );
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);
//        TextView itemName = (TextView) view.findViewById( R.id.item_name );
//        TextView itemRight = (TextView)view.findViewById( R.id.item_right ) ;
//        itemName.setText( isMust ? spannableString : typeName );
//        itemRight.setHint( inputHint );
//        if(!TextUtils.isEmpty(inputMessage)){
//            itemRight.setText(inputMessage);
//        }
        postInvalidate();
    }

    public void postInvalidate(){

        TextView itemName = viewParent.findViewById( R.id.item_name );
        itemInput = viewParent.findViewById( R.id.et_item_input );
        TextView itemRight = viewParent.findViewById( R.id.item_right );
        itemName.setText( isMust ? spannableString : typeName );
//        if( !isEnabled() ){
//            itemName.setTextColor( context.getResources().getColor( R.color.color_edit_item_hint) );
//        }else{
//            itemName.setTextColor( context.getResources().getColor( android.R.color.black ) );
//        }
        itemRight.setHint( inputHint );
        if(!TextUtils.isEmpty(inputMessage)){
            itemRight.setText(inputMessage);
        }

        if (!TextUtils.isEmpty(editInput)) {
            itemInput.setText(editInput);
        }
    }

    public void setRelatePick(EditItemPick relatePick) {
        this.relatePick = relatePick;
    }

    @Override
    public boolean isEnabled() { //如果设置了关联的EditItemPick对象，则关联对像数据先选择才能
        if( relatePick == null )
            return true ;

        return !TextUtils.isEmpty( relatePick.getInputMessage() ) ;
    }

    public void setEditInput(String editInput) {
        this.editInput = editInput;
    }

    public String getEditInput() {
        return itemInput.getText().toString();
    }

}
