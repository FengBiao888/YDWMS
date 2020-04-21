package com.yundao.ydwms;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.yundao.ydwms.protocal.request.LoginRequest;
import com.yundao.ydwms.protocal.respone.LoginRespone;
import com.yundao.ydwms.retrofit.BaseCallBack;
import com.yundao.ydwms.retrofit.HttpConnectManager;
import com.yundao.ydwms.retrofit.PostRequestService;
import com.yundao.ydwms.util.ARSAUtils;
import com.yundao.ydwms.util.ToastUtil;

import retrofit2.Call;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANL378k3RiZHWx5AfJqdH9xRNBmD9wGD2iRe41HdTNF8RUhNnHit5NpMNtGL0NPTSSpPjjI1kJfVorRvaQerUgkCAwEAAQ==";
    EditText userName ;
    EditText password ;
    Button confirm ;
    ImageView close ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_login);

        userName = findViewById( R.id.topEditText );
        userName.setText( "admin" );
        password = findViewById( R.id.bottomEditText );
        password.setText( "123456" );
        confirm = findViewById( R.id.confirm );
        confirm.setOnClickListener( v-> {
            login( this, true );
        } );
        close = findViewById( R.id.btn_cancel );
        close.setOnClickListener( v ->{
            finish();
        } );
    }

    public void login(Activity activity, boolean showProgressDialog ){

        YDWMSApplication.getInstance().setAuthorization("");
        YDWMSApplication.getInstance().setUser( null );

        LoginRequest request = new LoginRequest();
        request.username = userName.getText().toString() ;

        String encrypted = ARSAUtils.encryptDataByPublicKey( password.getText().toString().getBytes(), ARSAUtils.keyStrToPublicKey(publicKey) );
        request.password = encrypted ;
        request.code = "code" ;

        HttpConnectManager manager = new HttpConnectManager.HttpConnectBuilder()
                .setShowProgress(showProgressDialog)
                .build(activity);

        PostRequestService postRequestInterface = manager.createServiceClass(PostRequestService.class);
        postRequestInterface.login(request)
                .enqueue(new BaseCallBack<LoginRespone>(activity, manager) {
                    @Override
                    public void onResponse(Call<LoginRespone> call, Response<LoginRespone> response) {
                        super.onResponse(call, response);
                        LoginRespone body = response.body();
                        if( body != null && response.code() == 200 ){
                            YDWMSApplication.getInstance().setAuthorization( body.token );
                            YDWMSApplication.getInstance().setUser( body.user );
                            setResult( RESULT_OK );
                            finish();
                        }else{
                            YDWMSApplication.getInstance().setAuthorization( "" );
                            YDWMSApplication.getInstance().setUser( null );
                            ToastUtil.showShortToast( "数据异常，请重试" );
                        }
                    }

                });
    }


}
