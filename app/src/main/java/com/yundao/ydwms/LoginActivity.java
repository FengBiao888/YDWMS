package com.yundao.ydwms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    Button connectSetting ;
    Button login ;
    Button scan ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_login );

        connectSetting = (Button) findViewById( R.id.connect_setting );
        login = (Button) findViewById( R.id.login );
        scan = (Button) findViewById( R.id.scan );

        connectSetting.setOnClickListener( v -> {
            Intent intent = new Intent( this, ConnectingActivity.class );
            startActivity( intent );
        } );

        login.setOnClickListener( v -> {
            Intent intent = new Intent( this, LoginAccountIPSettingActivity.class );
            startActivity( intent );
        } );

        scan.setOnClickListener( v -> {
            Intent intent = new Intent( this, ScanListActivity.class );
            startActivity( intent );
        });
    }
}
