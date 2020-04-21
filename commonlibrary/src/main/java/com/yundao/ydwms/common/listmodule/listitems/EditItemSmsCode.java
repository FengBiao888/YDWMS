package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.yundao.ydwms.common.R;

public class EditItemSmsCode extends AbsEditItem {

    private boolean isPhoneCoding ;
    TextView getSms ;

    private CountDownListener listener ;

    /** 倒计时 */
    private TimeCount time ;

    public EditItemSmsCode(Context context, String typeName, boolean isMust, String inputHint) {
        super(context, typeName, isMust, inputHint);
    }

    @Override
    public View getView(int i, ViewGroup viewGroup) {
        View view = LayoutInflater.from( context ).inflate(R.layout.item_edititem_smscode , null);
        if( getSms == null ){
            getSms = view.findViewById( R.id.getsms );
        }

        updateView(view, i, viewGroup);
        return view;
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        super.updateView(view, position, parent);
        if( getSms == null ){
            getSms = view.findViewById( R.id.getsms );
        }
        TextView itemName = view.findViewById( R.id.item_name );
        itemName.setText( isMust ? spannableString : typeName );

        EditText editText = view.findViewById( R.id.ed_content );
        editText.setInputType( InputType.TYPE_CLASS_NUMBER );
        editText.setHint( inputHint );
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                inputMessage = charSequence.toString() ;
            }

            @Override
            public void afterTextChanged(Editable editable) {  }
        });

    }

    public void startCount(long time) {
        this.time = new TimeCount(time, 1000) ;
        this.time.start() ;
    }

    public void cancle() {
        if( this.time != null ) {
            this.time.cancel();
        }
    }

    /**
     * 验证码倒计时类
     *
     */
    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            isPhoneCoding = false;
            getSms.setClickable(true);
            getSms.setTextColor(context.getResources().getColor(R.color.color_458be9));
            getSms.setText("获取验证码");
            if( listener != null ) listener.onTick();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
            getSms.setClickable(false);
            //btObtainCode.setText("");
            //tvObtainCode.setBackgroundResource(R.drawable.gray_border_round_corner);
            getSms.setTextColor(context.getResources().getColor(R.color.color_999));
            getSms.setText(millisUntilFinished/1000+"s");
            if( listener != null ) listener.onTick();
            //btObtainCode.append("s后重试");

        }
    }

    public void setPhoneCoding(boolean phoneCoding) {
        isPhoneCoding = phoneCoding;
    }

    public void setCountDownListener(CountDownListener listener) {
        this.listener = listener;
    }

    public interface CountDownListener{

        public void onTick();

    }
}
