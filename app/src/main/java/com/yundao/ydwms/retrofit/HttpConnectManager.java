package com.yundao.ydwms.retrofit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.yundao.ydwms.protocal.URLConstant;
import com.yundao.ydwms.util.DialogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpConnectManager {

  private Context activity;
  private HttpConnectBuilder builder;
  private Dialog progressDialog;
  private OkHttpClient okHttpClient;
  private Retrofit retrofit;

  public <S> S createServiceClass(Class<S> serviceClass) {
    if (builder.showProgress) {
      progressDialog.show();
    }

    return retrofit.create( serviceClass );
  }

  private HttpConnectManager(Context activity, HttpConnectBuilder builder) {
    this.activity = activity ;
    this.builder = builder;
    if (builder.showProgress) {
      progressDialog = DialogUtil.createLoadingDialog(activity, "努力加载中...");
      progressDialog.setCancelable(true);
    }
    OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
    okhttpBuilder.addInterceptor(new LoggingInterceptor());//默认使用自定义的Log拦截器
    if (builder.interceptors != null && builder.interceptors.size() > 0) {
      for (Interceptor interceptor : builder.interceptors) {
        okhttpBuilder.addInterceptor(interceptor);
      }
    }
    if( builder.conntectTimeOut != 0 ) {
      okhttpBuilder.connectTimeout( builder.conntectTimeOut, TimeUnit.SECONDS );
    }else{
      okhttpBuilder.connectTimeout(120, TimeUnit.SECONDS); // 连接超时时间阈值
      okhttpBuilder.readTimeout(120, TimeUnit.SECONDS);   // 数据获取时间阈值
      okhttpBuilder.writeTimeout(120, TimeUnit.SECONDS);  // 写数据超时时间阈值
    }

    okHttpClient = okhttpBuilder.build();

    Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
    retrofitBuilder.baseUrl( builder.baseUrl ) // 设置 网络请求 Url
      .addConverterFactory(GsonConverterFactory.create()) //默认设置使用Gson解析(记得加入依赖)
//      .addConverterFactory( ResponseConvertFactory.create() )
      .client(okHttpClient);

    if (builder.converterFactorys != null && builder.converterFactorys.size() > 0) {
      for (Converter.Factory converterFactory : builder.converterFactorys) {
        retrofitBuilder.addConverterFactory(converterFactory);
      }
    }

    retrofit = retrofitBuilder.build();
  }

  public void dismissDialog(){
    if( activity instanceof Activity){

      if(!((Activity)activity).isDestroyed() && progressDialog != null && progressDialog.isShowing() ){
        progressDialog.cancel();
      }

    }
  }

  public static class HttpConnectBuilder {

    boolean showProgress;
    List<Interceptor> interceptors = new ArrayList<>();
    List<Converter.Factory> converterFactorys = new ArrayList<>();
    String baseUrl;
    boolean canelable;
    long conntectTimeOut ;

    public HttpConnectManager build(Context activity) {
//      if( SharedPreferenceUtil.getInt(SharedPreferenceUtil.SERVER_ADDRESS_CONFIG_TYPE,0 ) == 1 ){
//        baseUrl = SharedPreferenceUtil.getString( SharedPreferenceUtil.CUSTOM_IP, URLConstant.BASE_URL );
//      }else{
        baseUrl = URLConstant.BASE_URL ;
//      }

      HttpConnectManager httpConnectManager = new HttpConnectManager(activity, this);

      return httpConnectManager;
    }

    public HttpConnectBuilder setShowProgress(boolean showProgress) {
      this.showProgress = showProgress;
      return this;
    }

    public HttpConnectBuilder setProgressCancelable( boolean cancelable ){
      this.canelable = cancelable ;
      return this ;
    }

    public HttpConnectBuilder addInterceptor(Interceptor interceptor) {
      this.interceptors.add(interceptor);
      return this;
    }

    public HttpConnectBuilder addConverterFactory(Converter.Factory converter) {
      this.converterFactorys.add(converter);
      return this;
    }

    /**
     * 设置baseUrl，不调用时已默认设置{@Link URLConstant.BASE_URL}
     * @param baseUrl
     * @return
     */
    public HttpConnectBuilder setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    /**
     * 级别是秒，如60，即60秒
     * @param conntectTimeOut
     */
    public void setConntectTimeOut(long conntectTimeOut) {
      this.conntectTimeOut = conntectTimeOut;
    }
  }

}
