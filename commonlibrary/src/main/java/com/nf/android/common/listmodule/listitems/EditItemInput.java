package com.nf.android.common.listmodule.listitems;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.nf.android.common.R;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public class EditItemInput extends AbsEditItem {

    private int inputType = -1;
    private String digistText ;
    public boolean dataHasChange;
    private InputFilter filters[] ;
    private View.OnFocusChangeListener focusChangeListener;
    private boolean isEditable ;
    private boolean isAlignRight ;
    public TextChangedListener textChangedListener ;

    public EditItemInput(Context context, String typeName, boolean isMust, String inputHint) {
        super(context, typeName, isMust, inputHint);
        isEditable = true ;
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {

        View view = LayoutInflater.from( context ).inflate(R.layout.item_edititem_input, null);
        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);
        if( view == null ){
            view = viewParent ;
        }
        TextView itemName = view.findViewById( R.id.item_name );
        final EditText editText = view.findViewById( R.id.item_right );
        itemName.setText( isMust ? spannableString : typeName );
        editText.setHint( inputHint );
        editText.setCursorVisible( isEditable );
        editText.setFocusable( isEditable );
        editText.setFocusableInTouchMode( isEditable );
        editText.setGravity( isAlignRight ? Gravity.RIGHT : Gravity.LEFT );
//        int textColor = isEnabled ? context.getResources().getColor( android.R.color.black )
//                : context.getResources().getColor( R.color.color_edit_item_hint );
//        editText.setTextColor( textColor );
        if( focusChangeListener != null ){
            editText.setOnFocusChangeListener( focusChangeListener );
        }
        if( !TextUtils.isEmpty( digistText) ) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
//            editText.setKeyListener(DigitsKeyListener.getInstance( digistText ));
            editText.setFilters( new InputFilter[]{new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                    System.out.println("kdkdkd source: " + source + ",start: " + start + ",end: " + end + ", dest:" + dest.toString()
//                    + ",dstart: " + dstart + ",dend: " + dend);
                    if( source != null ){
                        for( int i = 0 ; i < source.length(); i ++ ){
                            if( !digistText.contains( String.valueOf( source.charAt(i) ) ) ){
                                return "" ;
                            }
                        }
                    }
                    return null;
                }
            }});
        }else if( inputType != -1 ){
            editText.setInputType( inputType );
            editText.setFilters( new InputFilter[0] );
        }else {
            editText.setInputType( InputType.TYPE_CLASS_TEXT );
            editText.setFilters( new InputFilter[0] );
        }
        if( filters != null ){
            editText.setFilters( filters );
        }

        if( !TextUtils.isEmpty(inputMessage) ){
            editText.setText(inputMessage);
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if( textChangedListener != null ){
                    textChangedListener.beforeTextChanged(charSequence,i, i1, i2);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                inputMessage = charSequence.toString() ;
                if( textChangedListener != null && before != count ){
                    textChangedListener.onTextChanged(charSequence, start, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                dataHasChange = true;
                if( textChangedListener != null ){
                    textChangedListener.afterTextChanged(editable);
                }
            }
        });

    }

    /**
     * 设置EditText输入格式
     * @see {@link android.text.InputType}
     * @param inputType
     */
    public void setInputType(int inputType){
        this.inputType = inputType ;
    }

    /**
     * 设置EditText的输入格式,此时inputType为InputType.TYPE_CLASS_TEXT
     * @param digistText
     */
    public void setDigistText(String digistText) {
        this.digistText = digistText;
    }

    public void setFilter(InputFilter[] filters){
        this.filters = filters ;
    }

    public void clearInputMessage(){
        if( viewParent != null ){
            final EditText editText = viewParent.findViewById( R.id.item_right );
            editText.setText( "" );
        }
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public void setOnFocusChangeListener(View.OnFocusChangeListener focusChangeListener) {
        this.focusChangeListener = focusChangeListener;
    }

    public void setAlignRight(boolean alignRight) {
        isAlignRight = alignRight;
    }

    public void setTextChangedListener(TextChangedListener textChangedListener) {
        this.textChangedListener = textChangedListener;
    }

    public interface TextChangedListener{

        void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2);

        void onTextChanged(CharSequence charSequence, int i, int i1, int i2);

        void afterTextChanged(Editable editable);

    }
}
