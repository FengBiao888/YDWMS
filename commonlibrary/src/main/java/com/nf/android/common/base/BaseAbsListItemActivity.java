package com.nf.android.common.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nf.android.common.R;
import com.nf.android.common.listmodule.SimpleListAdapter;
import com.nf.android.common.listmodule.listitems.AbsListItem;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAbsListItemActivity extends ImmersiveBaseActivity implements AdapterView.OnItemClickListener{

  public ListView listView ;
  public SimpleListAdapter simpleListAdapter;
  public List<AbsListItem> absListItems ;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    listView = findViewById( R.id.list_view );
    absListItems = new ArrayList<>();
    absListItems.addAll( getItemList() );
    simpleListAdapter = new SimpleListAdapter( getActivity(), absListItems );
    if( listView != null ) {
      listView.setOnItemClickListener(this);
      listView.setAdapter(simpleListAdapter);
      listView.setBackgroundColor( getResources().getColor( R.color.login_bg ) );
    }
  }

  public abstract List<? extends AbsListItem> getItemList();

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

  }

}
