package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yundao.ydwms.common.R;

;


public class ItemNoData extends AbsListItem {

    int mEmptyLogoId;
    String mTextString ;
    private int imageMarginTop = -1 ;
    private int textMarginTop = -1 ;
    private int textMarginBottom = -1 ;

    private int color ;
    private float textSize ;


    public ItemNoData(Context context) {
        this(context, -1, -1);
    }

    /**
     * @param context
     */
    public ItemNoData(Context context, int emptymsgid, int emptylogoid) {
        super(context, "item_no_data");
        String text = "" ;
        if( emptymsgid != -1){
            text = context.getString( emptylogoid );
        }
        mEmptyLogoId = emptylogoid;
        mTextString = text;
    }

    /**
     * @param context
     */
    public ItemNoData(Context context, String emptymsg, int emptylogoid) {
        super(context, "item_no_data");
        mEmptyLogoId = emptylogoid;
        mTextString = emptymsg;
    }

    public void setImageMarginTop(int imageMarginTop) {
        this.imageMarginTop = imageMarginTop;
    }

    public void setTextMarginTop(int textMarginTop) {
        this.textMarginTop = textMarginTop;
    }

    public void setTextMarginBottom(int textMarginBottom) {
        this.textMarginBottom = textMarginBottom;
    }

    @Override
    public View getView(int position, ViewGroup parent) {
        View view = LayoutInflater.from( context ).inflate(R.layout.listitem_no_date, null);
        view.setOnClickListener(null);
        updateView(view, position, parent);
        return view;
//	if (mEmptyMsgId != -1 && mEmptyLogoId != -1) {
//	    LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
//	    View view = inflater.inflate(R.layout.result_empty_layout, null);
//	    view.setOnClickListener(null);
//	    updateView(view, position, parent);
//	    return view;
//	} else {
//	    TextView textview = new TextView(mContext);
//	    updateView(textview, position, parent);
//	    textview.setGravity(Gravity.CENTER);
//	    textview.setTextSize(18.0f);
//	    int textcolor = mContext.getResources().getColor(android.R.color.black);
//	    textview.setTextColor(textcolor);
//	    return textview;
//	}
    }

    @Override
    public void updateView(View view, int position, ViewGroup parent) {
        TextView emptymessage = view.findViewById(R.id.errmsg);
        ImageView emptyicon = view.findViewById(R.id.imageview1);
        if (mEmptyLogoId != -1 ) {
            if (mEmptyLogoId != -1)
                emptyicon.setImageResource(mEmptyLogoId);
            else
                emptyicon.setVisibility(View.GONE);
        }

        if( imageMarginTop != -1 ){
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) emptyicon.getLayoutParams();
            layoutParams.topMargin = imageMarginTop;
            emptyicon.setLayoutParams( layoutParams );
        }

        if( textMarginTop != -1 ){
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) emptymessage.getLayoutParams();
            layoutParams.topMargin = textMarginTop;
            emptymessage.setLayoutParams( layoutParams );
        }

        if( textMarginBottom != -1 ){
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) emptymessage.getLayoutParams();
            layoutParams.bottomMargin = textMarginBottom;
            emptymessage.setLayoutParams( layoutParams );
        }

        if(!TextUtils.isEmpty( mTextString )){
            emptymessage.setText(mTextString);
        }

        if( color != 0 ) {
            emptymessage.setTextColor(color);
        }
        if( textSize != 0 ) {
            emptymessage.setTextSize(textSize);
        }


    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }
}
