package com.yundao.ydwms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nf.android.common.base.ImmersiveBaseActivity;

public class UploadSuccessActivity extends ImmersiveBaseActivity {

    @Override
    protected void setTitleBar() {
        titleBar.setTitleMainText( "上传详情" );
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_upload_success ;
    }

    @Override
    public void initView(Bundle var1) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getActivity(), ScanListActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity( intent );
    }
}
