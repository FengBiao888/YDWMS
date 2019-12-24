package com.yundao.ydwms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ScanListActivity extends AppCompatActivity {

    private TextView operator;
    private TextView state ;
    private Spinner scanTypeSpinner;
    private Button confirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanlist);

        operator = (TextView) findViewById( R.id.operator );
        state = (TextView) findViewById( R.id.state );
        scanTypeSpinner = (Spinner) findViewById( R.id.scan_type );
        confirm = (Button) findViewById( R.id.confirm );

        confirm.setOnClickListener(  v->{
            Intent intent = new Intent( this, ProductPackagingActivity.class );
            startActivity( intent );
        });
        scanTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] scanType = getResources().getStringArray(R.array.scan_type);
                String type = scanType[ position ];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
