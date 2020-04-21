package com.yundao.ydwms.common.listmodule.listitems;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yundao.ydwms.common.R;;


public class SubTitleItem extends AbsListItem {

  public SubTitleItem(Context context) {
    super(context);
  }

  public SubTitleItem(Context context, String typeName) {
    super(context, typeName);
  }

  private int topMargin, leftMargin, rightMargin ;

  @Override
  public View getView(int i, ViewGroup viewGroup) {
    View view = View.inflate( context, R.layout.item_subtitle, null );
    updateView(view, i, viewGroup);
    return view;
  }

  @Override
  public void updateView(View view, int position, ViewGroup parent) {
    super.updateView(view, position, parent);
    TextView textView = view.findViewById( R.id.tv_title );
    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textView.getLayoutParams();
    layoutParams.leftMargin = leftMargin ;
    layoutParams.rightMargin = rightMargin ;
    textView.setLayoutParams( layoutParams );
    textView.setText( typeName );

    View line = view.findViewById( R.id.view_right );
    ConstraintLayout.LayoutParams  lineLayoutParams = (ConstraintLayout.LayoutParams) line.getLayoutParams();
    lineLayoutParams.rightMargin = rightMargin ;
    line.setLayoutParams( lineLayoutParams );


  }

  @Override
  public boolean isEnabled() {
    return false ;
  }

  public void setMargin( int topMargin, int leftMargin, int rightMargin ){
    this.topMargin = topMargin ;
    this.leftMargin = leftMargin ;
    this.rightMargin = rightMargin ;
  }

  @Override
  public boolean equals(Object o) {
    if( o instanceof SubTitleItem ){
      return typeName.equals( ((SubTitleItem) o).typeName );
    }
    return super.equals(o);
  }
}
