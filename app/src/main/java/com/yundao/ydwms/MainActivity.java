package com.yundao.ydwms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.yundao.ydwms.common.avoidonresult.AvoidOnResult;
import com.yundao.ydwms.util.ToastUtil;

public class MainActivity extends AppCompatActivity {

    Button connectSetting ;
    Button login ;
    Button scan ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        connectSetting = findViewById( R.id.connect_setting );
        login = findViewById( R.id.login );
        scan = findViewById( R.id.scan );

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
            if( YDWMSApplication.getInstance().getUser() == null ){
                ToastUtil.showShortToast( "请先登录" );
//                return ;
            }
            Intent intent = new Intent( this, ScanListActivity.class );
            startActivity( intent );
        });



    }


}
