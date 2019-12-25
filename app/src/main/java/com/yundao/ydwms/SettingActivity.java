package com.yundao.ydwms;

import android.os.Bundle;
import android.view.Gravity;

import com.nf.android.common.base.BaseAbsListItemActivity;
import com.nf.android.common.listmodule.listitems.AbsListItem;
import com.nf.android.common.listmodule.listitems.BlankItem;
import com.nf.android.common.listmodule.listitems.EditItemInput;
import com.nf.android.common.listmodule.listitems.EditItemPick;
import com.nf.android.common.listmodule.listitems.EditItemSubmitButton;
import com.nf.android.common.listmodule.listitems.ItemOneTextView;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends BaseAbsListItemActivity {

    private EditItemInput serviceIp ;
    private EditItemInput extendDeivceIp ;
    private EditItemPick tagForm ;
    private EditItemPick printer ;
    private EditItemSubmitButton confirm;


    @Override
    public List<? extends AbsListItem> getItemList() {
        List<AbsListItem> itemList = new ArrayList<>();
        ItemOneTextView oneTextView = new ItemOneTextView(getActivity(), "系统设置", getResources().getColor(R.color.color_777));
        oneTextView.setGravity(Gravity.LEFT);
        oneTextView.setTextSize( R.dimen.text_size_16sp );
        itemList.add( oneTextView );

        serviceIp = new EditItemInput(getActivity(), "服务器IP地址", false, "");
        itemList.add( serviceIp );
        extendDeivceIp = new EditItemInput(getActivity(), "外部设备地址", false, "");
        itemList.add( extendDeivceIp );

        oneTextView = new ItemOneTextView(getActivity(), "打印设置", getResources().getColor(R.color.color_777));
        oneTextView.setGravity(Gravity.LEFT);
        oneTextView.setTextSize( R.dimen.text_size_16sp );
        itemList.add( oneTextView );

        tagForm = new EditItemPick( getActivity(), "标签及报表", false, "");
        itemList.add( tagForm );
        printer = new EditItemPick( getActivity(), "默认打印机", false, "");
        itemList.add( printer );

        itemList.add( new BlankItem( getActivity(), 30, false) );
        confirm = new EditItemSubmitButton(getActivity(), "确定");
        confirm.setBtnBgLayoutId( R.drawable.selector_blue_solid );
        itemList.add(confirm);

        return itemList;
    }

    @Override
    protected void setTitleBar() {
        titleBar.setTitleMainText( "设置" );

    }

    @Override
    protected int getLayout() {
        return R.layout.layout_listview_with_titlebar;
    }

    @Override
    public void initView(Bundle var1) {

    }

}
