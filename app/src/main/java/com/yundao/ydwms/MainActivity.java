package com.yundao.ydwms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.nf.android.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.protocal.request.LoginRequest;
import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.protocal.respone.LoginRespone;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button connectSetting ;
    Button login ;
    Button scan ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        connectSetting = (Button) findViewById( R.id.connect_setting );
        login = (Button) findViewById( R.id.login );
        scan = (Button) findViewById( R.id.scan );

        connectSetting.setOnClickListener( v -> {
//            Intent intent = new Intent( this, ConnectingActivity.class );
//            startActivity( intent );
            Intent intent = new Intent( this, SettingActivity.class );
            startActivity( intent );
        } );

        login.setOnClickListener( v -> {
            Intent intent = new Intent( this, LoginActivity.class );
            AvoidOnResult avoidOnResult = new AvoidOnResult( this );
            avoidOnResult.startForResult(intent, new AvoidOnResult.Callback() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, Intent data) {
                    if( resultCode == RESULT_OK ){
                        login.setText( "重新登录" );
                    }
                }
            });
        } );

        scan.setOnClickListener( v -> {
            Intent intent = new Intent( this, ScanListActivity.class );
            startActivity( intent );
        });


    }


}
