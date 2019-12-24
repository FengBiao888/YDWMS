package com.yundao.ydwms;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class ConnectingActivity extends AppCompatActivity {

    int[] imageIcons = new int[]{R.id.dot1,R.id.dot2,R.id.dot3,R.id.dot4,R.id.dot5,R.id.dot6,R.id.dot7,R.id.dot8};
    ImageView[] imageViews = new ImageView[8];
    int index = 0 ;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            index ++ ;
            if(index > imageIcons.length ){
                index = 0 ;
            }
            for( int i = 0 ; i < imageIcons.length ; i ++ ){
                imageViews[i].setVisibility( i < index ? View.VISIBLE : View.INVISIBLE );
            }
            handler.sendEmptyMessageDelayed( 1, 800 );
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);
        for( int i = 0 ; i < imageIcons.length ; i ++ ){
            imageViews[i] = (ImageView) findViewById( imageIcons[i] );
        }

        handler.sendEmptyMessageDelayed( 1, 1000 );

    }
}
