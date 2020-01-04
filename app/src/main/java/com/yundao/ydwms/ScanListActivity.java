package com.yundao.ydwms;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.nf.android.common.base.BaseAbsListItemActivity;
import com.nf.android.common.listmodule.listitems.AbsListItem;
import com.nf.android.common.listmodule.listitems.BlankItem;
import com.nf.android.common.listmodule.listitems.EditItemPick;
import com.nf.android.common.listmodule.listitems.EditItemSubmitButton;
import com.nf.android.common.listmodule.listitems.ItemCheckbox;
import com.nf.android.common.listmodule.listitems.ItemOneTextView;
import com.nf.android.common.widget.TimePickerDialogView;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.util.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;

import static com.yundao.ydwms.ScanTypeEnum.HALF_PRODUCT_OUTGOING;
import static com.yundao.ydwms.ScanTypeEnum.PRODUCT_INCOMING;
import static com.yundao.ydwms.ScanTypeEnum.PRODUCT_INVENTORY;
import static com.yundao.ydwms.ScanTypeEnum.PRODUCT_MACHINING;
import static com.yundao.ydwms.ScanTypeEnum.PRODUCT_OUTGOING;
import static com.yundao.ydwms.ScanTypeEnum.PRODUCT_PACKAGING;
import static com.yundao.ydwms.ScanTypeEnum.WAREHOUSE_CHANGING;

public class ScanListActivity extends BaseAbsListItemActivity {

    private EditItemSubmitButton confirm;
    private List<ItemCheckbox> checkboxes = new ArrayList<>();
    ItemCheckbox checkboxSelected ;

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
    }

    @Override
    protected void setTitleBar() {
        titleBar.setTitleMainText( "选择扫描仓库" );
        titleBar.setMainTitleColor( Color.WHITE );
    }

    @Override
    public void initView(Bundle var1) {


    }

    @Override
    public int getLayout() {
        return R.layout.layout_listview_with_titlebar ;
    }

    @Override
    public List<? extends AbsListItem> getItemList() {
        List<AbsListItem> list = new ArrayList<>();

        String username = "" ;
        User user = YDWMSApplication.getInstance().getUser();
        if( user != null ){
            username = user.username ;
        }
        ItemOneTextView workday = new ItemOneTextView(getActivity(), "操作员：" + username, Color.parseColor( "#303030"));
        workday.setEnabled( false );
        workday.setGravity(Gravity.LEFT);
        list.add(workday);

        ItemCheckbox productIncoming = new ItemCheckbox( getActivity(), ScanTypeEnum.PRODUCT_INCOMING.getCodeName() );
        ScanTypeClickListener clickListener = new ScanTypeClickListener( productIncoming );
        productIncoming.setExtraObj(ScanTypeEnum.PRODUCT_INCOMING);
        productIncoming.setClickListener( clickListener );


        ItemCheckbox productOutgoing = new ItemCheckbox( getActivity(), PRODUCT_OUTGOING.getCodeName() );
        clickListener = new ScanTypeClickListener( productOutgoing );
        productOutgoing.setExtraObj( PRODUCT_OUTGOING );
        productOutgoing.setClickListener( clickListener );


        ItemCheckbox warehouseChanging = new ItemCheckbox( getActivity(), WAREHOUSE_CHANGING.getCodeName() );
        warehouseChanging.setExtraObj( WAREHOUSE_CHANGING );
        clickListener = new ScanTypeClickListener( warehouseChanging );
        warehouseChanging.setClickListener( clickListener );

        ItemCheckbox productPackaging = new ItemCheckbox( getActivity(), PRODUCT_PACKAGING.getCodeName() );
        productPackaging.setExtraObj( PRODUCT_PACKAGING );
        clickListener = new ScanTypeClickListener( productPackaging );
        productPackaging.setClickListener( clickListener );

        ItemCheckbox productMachining = new ItemCheckbox( getActivity(), PRODUCT_MACHINING.getCodeName() );
        productMachining.setExtraObj( PRODUCT_MACHINING );
        clickListener = new ScanTypeClickListener( productMachining );
        productMachining.setClickListener( clickListener ) ;

        ItemCheckbox halfProductionOutgoing = new ItemCheckbox( getActivity(), HALF_PRODUCT_OUTGOING.getCodeName() );
        halfProductionOutgoing.setExtraObj( HALF_PRODUCT_OUTGOING );
        clickListener = new ScanTypeClickListener( halfProductionOutgoing );
        halfProductionOutgoing.setClickListener( clickListener );

        ItemCheckbox productInventory = new ItemCheckbox( getActivity(), PRODUCT_INVENTORY.getCodeName() );
        productInventory.setExtraObj( PRODUCT_INVENTORY );
        clickListener = new ScanTypeClickListener( productInventory );
        productInventory.setClickListener( clickListener );

        list.add( productIncoming );
        list.add( productOutgoing );
        list.add( warehouseChanging );
        list.add( halfProductionOutgoing );
        list.add( productMachining );
        list.add( productPackaging );
        list.add( productInventory );
        checkboxes.add( productIncoming );
        checkboxes.add( productOutgoing );
        checkboxes.add( halfProductionOutgoing );
        checkboxes.add( productMachining );
        checkboxes.add( warehouseChanging );
        checkboxes.add( productPackaging );
        checkboxes.add( productInventory );


        list.add( new BlankItem( getActivity(), 30, false) );
        confirm = new EditItemSubmitButton(getActivity(), "确定");
        confirm.setTextSize( 20 );
        confirm.setBtnBgLayoutId( R.drawable.selector_blue_solid );
        confirm.setSpecifyClickListener( v ->{
            if( checkboxSelected == null ){
                ToastUtil.showShortToast( "请选择扫描仓库类型" );
                return ;
            }
            Intent intent = null;
            if( ScanTypeEnum.PRODUCT_INCOMING.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), ProductIncomingActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }else if( ScanTypeEnum.PRODUCT_OUTGOING.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), ProductOutgoingActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }else if( ScanTypeEnum.PRODUCT_INVENTORY.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), ProductInventoryActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }else if( ScanTypeEnum.PRODUCT_MACHINING.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), ProductMachineActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }else if( ScanTypeEnum.HALF_PRODUCT_OUTGOING.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), HalfProductOutgoingActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }else if( ScanTypeEnum.WAREHOUSE_CHANGING.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), ProductHouseChangingActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }else if( ScanTypeEnum.PRODUCT_PACKAGING.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), ProductPackagingActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }
            startActivity( intent );
        }, R.id.bottom_submit);
        list.add(confirm);

        return list;
    }
    
    class ScanTypeClickListener implements View.OnClickListener{

        ItemCheckbox checkbox ;
        public ScanTypeClickListener( ItemCheckbox checkbox ) {
            this.checkbox = checkbox ;
        }

        @Override
        public void onClick(View v) {
            for (int i = 0; i < checkboxes.size(); i++) {
                if (checkboxes.get(i).getExtraObj().equals(checkbox.getExtraObj())) {
                    checkboxes.get(i).setChecked( true );
                    checkboxSelected = checkbox ;
                }else {
                    checkboxes.get(i).setChecked( false );
                }
                simpleListAdapter.notifyDataSetChanged();
            }
        }
    }
}
