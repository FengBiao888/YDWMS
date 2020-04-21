package com.yundao.ydwms;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.yundao.ydwms.common.base.BaseAbsListItemActivity;
import com.yundao.ydwms.common.listmodule.listitems.AbsListItem;
import com.yundao.ydwms.common.listmodule.listitems.BlankItem;
import com.yundao.ydwms.common.listmodule.listitems.EditItemSubmitButton;
import com.yundao.ydwms.common.listmodule.listitems.ItemCheckbox;
import com.yundao.ydwms.common.listmodule.listitems.ItemOneTextView;
import com.yundao.ydwms.protocal.respone.User;
import com.yundao.ydwms.util.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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



        ItemCheckbox halfProductionIncoming = new ItemCheckbox( getActivity(), ScanTypeEnum.HALF_PRODUCT_INCOMING.getCodeName() );
        halfProductionIncoming.setExtraObj( ScanTypeEnum.HALF_PRODUCT_INCOMING );
        ScanTypeClickListener clickListener = new ScanTypeClickListener( halfProductionIncoming );
        halfProductionIncoming.setClickListener( clickListener );

        ItemCheckbox halfProductionOutgoing = new ItemCheckbox( getActivity(), ScanTypeEnum.HALF_PRODUCT_OUTGOING.getCodeName() );
        halfProductionOutgoing.setExtraObj( ScanTypeEnum.HALF_PRODUCT_OUTGOING );
        clickListener = new ScanTypeClickListener( halfProductionOutgoing );
        halfProductionOutgoing.setClickListener( clickListener );

        ItemCheckbox productPackaging = new ItemCheckbox( getActivity(), ScanTypeEnum.PRODUCT_PACKAGING.getCodeName() );
        productPackaging.setExtraObj( ScanTypeEnum.PRODUCT_PACKAGING );
        clickListener = new ScanTypeClickListener( productPackaging );
        productPackaging.setClickListener( clickListener );

        ItemCheckbox productIncoming = new ItemCheckbox( getActivity(), ScanTypeEnum.PRODUCT_INCOMING.getCodeName() );
        clickListener = new ScanTypeClickListener( productIncoming );
        productIncoming.setExtraObj(ScanTypeEnum.PRODUCT_INCOMING);
        productIncoming.setClickListener( clickListener );

        ItemCheckbox productOutgoing = new ItemCheckbox( getActivity(), ScanTypeEnum.PRODUCT_OUTGOING.getCodeName() );
        clickListener = new ScanTypeClickListener( productOutgoing );
        productOutgoing.setExtraObj( ScanTypeEnum.PRODUCT_OUTGOING );
        productOutgoing.setClickListener( clickListener );

        ItemCheckbox productInventory = new ItemCheckbox( getActivity(), ScanTypeEnum.PRODUCT_INVENTORY.getCodeName() );
        productInventory.setExtraObj( ScanTypeEnum.PRODUCT_INVENTORY );
        clickListener = new ScanTypeClickListener( productInventory );
        productInventory.setClickListener( clickListener );

        ItemCheckbox productSlitting = new ItemCheckbox( getActivity(), ScanTypeEnum.PRODUCT_SLITTING.getCodeName() );
        productSlitting.setExtraObj( ScanTypeEnum.PRODUCT_SLITTING );
        clickListener = new ScanTypeClickListener( productSlitting );
        productSlitting.setClickListener( clickListener );

        ItemCheckbox rejectProductIncoming = new ItemCheckbox( getActivity(), ScanTypeEnum.REJECTED_PRODUCT_INCOMING.getCodeName() );
        rejectProductIncoming.setExtraObj( ScanTypeEnum.REJECTED_PRODUCT_INCOMING );
        clickListener = new ScanTypeClickListener( rejectProductIncoming );
        rejectProductIncoming.setClickListener( clickListener );

        ItemCheckbox warehouseChanging = new ItemCheckbox( getActivity(), ScanTypeEnum.WAREHOUSE_CHANGING.getCodeName() );
        warehouseChanging.setExtraObj( ScanTypeEnum.WAREHOUSE_CHANGING );
        clickListener = new ScanTypeClickListener( warehouseChanging );
        warehouseChanging.setClickListener( clickListener );

        ItemCheckbox productInfoChanging = new ItemCheckbox( getActivity(), ScanTypeEnum.PRODUCT_INFO_CHANGING.getCodeName() );
        productInfoChanging.setExtraObj(ScanTypeEnum.PRODUCT_INFO_CHANGING);
        clickListener = new ScanTypeClickListener( productInfoChanging );
        productInfoChanging.setClickListener( clickListener ) ;


//        HALF_PRODUCT_INCOMING( "半成品进仓", 1 ),
//                HALF_PRODUCT_OUTGOING( "半成品出仓", 2 ),
//                PRODUCT_PACKAGING( "成品打包", 3),
//                PRODUCT_INCOMING( "成品进仓", 4 ),
//                PRODUCT_OUTGOING( "成品出仓", 5 ),
//                PRODUCT_INVENTORY( "盘点",6 ),
//                REJECTED_PRODUCT_INCOMING( "退货进仓", 7 ),
//                WAREHOUSE_CHANGING( "仓位变更",8 ),
//                PRODUCT_INFO_CHANGING( "产品信息变更", 9 ) ;

        list.add( halfProductionIncoming );
        list.add( halfProductionOutgoing );
        list.add( productPackaging );
        list.add( productIncoming );
        list.add( productOutgoing );
        list.add( productInventory );
        list.add( productSlitting );
        list.add( rejectProductIncoming );
        list.add( warehouseChanging );
        list.add( productInfoChanging );

        checkboxes.add( halfProductionIncoming );
        checkboxes.add( halfProductionOutgoing );
        checkboxes.add( productPackaging );
        checkboxes.add( productIncoming );
        checkboxes.add( productOutgoing );
        checkboxes.add( productInventory );
        checkboxes.add( productSlitting );
        checkboxes.add( rejectProductIncoming );
        checkboxes.add( warehouseChanging );
        checkboxes.add( productInfoChanging );

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
            }else if( ScanTypeEnum.REJECTED_PRODUCT_INCOMING.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), RejectProductIncomingActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }else if( ScanTypeEnum.PRODUCT_OUTGOING.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), ProductOutgoingActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }else if( ScanTypeEnum.PRODUCT_INVENTORY.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), ProductInventoryChooseActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }else if( ScanTypeEnum.PRODUCT_INFO_CHANGING.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), ProductProductUpdateActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }else if( ScanTypeEnum.HALF_PRODUCT_OUTGOING.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), HalfProductOutgoingActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }else if( ScanTypeEnum.HALF_PRODUCT_INCOMING.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), HalfProductIncomingActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }else if( ScanTypeEnum.WAREHOUSE_CHANGING.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), ProductWarehouseChangingActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }else if( ScanTypeEnum.PRODUCT_PACKAGING.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), ProductPackagingActivity.class);
                intent.putExtra("pickScanType", (Serializable) checkboxSelected.getExtraObj());
            }else if( ScanTypeEnum.PRODUCT_SLITTING.equals( checkboxSelected.getExtraObj() ) ){
                intent = new Intent(getActivity(), ProductSlittingActivity.class);
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
