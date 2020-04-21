package com.yundao.ydwms.common.widget;

import android.app.TimePickerDialog;
import android.content.Context;

/**
 * Created by lcm on 2016/2/19.
 */
public class TimePickerDialogView extends TimePickerDialog {
    public TimePickerDialogView(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
    }

    public TimePickerDialogView(Context context, int theme, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        super(context, theme, callBack, hourOfDay, minute, is24HourView);
    }

    @Override
    protected void onStop() {
        //解决魅族手机回调2次的问题
//        super.onStop();
    }


}
