package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.yundao.ydwms.common.R;;


/**
 * Created by liangjianhua on 2018/5/15.
 */

public abstract class AbsEditItem extends AbsListItem {

    protected boolean isMust ;
    protected String inputHint ;
    protected SpannableString spannableString ;

    /**
     * 选择或者输入的文字
     */
    protected String inputMessage ;

    public AbsEditItem(Context context, String typeName, boolean isMust, String inputHint) {
        super(context, typeName);
        this.isMust = isMust;
        this.inputHint = inputHint;
        isEnabled = true;
        if( isMust && !TextUtils.isEmpty( typeName )){
            spannableString = new SpannableString(typeName + " *");
            ForegroundColorSpan what = new ForegroundColorSpan(context.getResources().getColor(R.color.pure_red));
            spannableString.setSpan(what, typeName.length() ,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public void setTypeName(String typeName) {
        super.setTypeName(typeName);
        if( isMust && !TextUtils.isEmpty( typeName )){
            spannableString = new SpannableString(typeName + " *");
            ForegroundColorSpan what = new ForegroundColorSpan(context.getResources().getColor(R.color.pure_red));
            spannableString.setSpan(what, typeName.length() ,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public void setMust(boolean must) {
        isMust = must;
    }

    public void setInputMessage(String inputMessage) {
        this.inputMessage = inputMessage;
    }

    public String getInputMessage() {
        return inputMessage;
    }

    public void setInputHint(String inputHint) {
        this.inputHint = inputHint;
    }

    @Override
    public String toString() {
        return "AbsEditItem{" +
                "typeName='" + typeName + '\'' +
                ", isMust=" + isMust +
                ", inputHint='" + inputHint + '\'' +
                ", spannableString=" + spannableString +
                '}';
    }

    public boolean isMust() {
        return isMust;
    }



}
