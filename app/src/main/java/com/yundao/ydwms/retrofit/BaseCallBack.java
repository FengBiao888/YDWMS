package com.yundao.ydwms.retrofit;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.yundao.ydwms.protocal.respone.BaseRespone;
import com.yundao.ydwms.util.NetType;
import com.yundao.ydwms.util.NetUtil;
import com.yundao.ydwms.util.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseCallBack<T> implements Callback<T> {

  private final String TAG = "BaseCallBack" ;

  Context activity;
  HttpConnectManager manager ;
  boolean needToast ;

  public BaseCallBack(Context activity, HttpConnectManager manager) {
    this( activity, manager, true );
  }

  public BaseCallBack(Context activity, HttpConnectManager manager, boolean needToast ) {
    this.activity = activity ;
    this.manager = manager ;
    this.needToast = needToast ;
  }

  @Override
  public void onResponse(Call<T> call, Response<T> response) {

    if( activity instanceof Activity){
      Activity activity1 = (Activity) activity;
      if( activity1.isFinishing() || activity1.isDestroyed() ){
        return ;
      }
    }
    manager.dismissDialog();
    if( needToast ) {
      try {
        printFailureRe((Response<? extends BaseRespone>) response);
      } catch (ClassCastException e) {
        Log.e(TAG, "BaseCallBack call can not cast to Call<? extends BaseRespone>)!!!");
//      e.printStackTrace();
      }
      if (response.body() == null) {
        ToastUtil.showShortToast("服务器未知异常，请重试");
      }
    }
  }

  @Override
  public void onFailure(Call<T> call, Throwable t) {

    if( activity instanceof Activity){
      Activity activity1 = (Activity) activity;
      if( activity1.isFinishing() || activity1.isDestroyed() ){
        return ;
      }
    }

    Log.e(TAG, call.toString() + "-->" + t.getMessage() );
    manager.dismissDialog();

    if( needToast ) {
      NetType currentNetType = NetUtil.getCurrentNetType(activity);
      if (currentNetType == NetType.NONE) {
        if (activity instanceof Activity && !((Activity) activity).isDestroyed()) {
          ToastUtil.showShortToast("请检查当前网络是否可用！");
        }
      } else {
        ToastUtil.showShortToast("请求失败！");
      }
    }
  }

  /**Toast出错误信息*/
  public void printFailureRe(Response<? extends BaseRespone> respone){

  }
}
