package com.yundao.ydwms.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

public class TextViewWithoutPaddings extends android.support.v7.widget.AppCompatTextView {

    private final Paint mPaint = new Paint();

    private final Rect mBounds = new Rect();

    public TextViewWithoutPaddings(Context context) {
        super(context);
    }

    public TextViewWithoutPaddings(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public TextViewWithoutPaddings(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        final String text = calculateTextParams();

        final int left = mBounds.left;
        final int bottom = mBounds.bottom;
        mBounds.offset(-mBounds.left, -mBounds.top);
        mPaint.setAntiAlias(true);
        mPaint.setColor(getCurrentTextColor());
        String textFinalText = "" ;
        int maxWidth = getMaxWidth();
        int boundWidth = mBounds.right - mBounds.left;
        if( getEllipsize() != null && boundWidth > maxWidth ) {
            int finalWidth = Math.min(maxWidth, boundWidth);
            TextPaint textPaint = new TextPaint(mPaint);
            TextUtils.TruncateAt ellipsize = getEllipsize();
            textFinalText = TextUtils.ellipsize(text, textPaint, finalWidth, ellipsize).toString();
        }else{
            textFinalText = text ;
        }
        canvas.drawText(textFinalText, -left + 1, mBounds.bottom - bottom + 1, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculateTextParams();
        int finalWidth = Math.min( getMaxWidth(), mBounds.right - mBounds.left + 5);
        setMeasuredDimension(finalWidth, -mBounds.top + mBounds.bottom + 5);
    }

    private String calculateTextParams() {
        final String text = getText().toString();
        final int textLength = text.length();
        mPaint.setTextSize(getTextSize());
        mPaint.getTextBounds(text, 0, textLength, mBounds);
        if (textLength == 0) {
            mBounds.right = mBounds.left;
        }
        return text;
    }
}
