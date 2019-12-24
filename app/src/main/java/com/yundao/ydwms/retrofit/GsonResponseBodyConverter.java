package com.yundao.ydwms.retrofit;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;


public class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
  private final Gson gson;
  private final Type type;

  GsonResponseBodyConverter(Gson gson, Type type) {
    this.gson = gson;
    this.type = type;
  }

  @Override
  public T convert(ResponseBody value) throws IOException {
    String response = value.string();
    // 在这里可以打印出网页源码
    Log.d("flag--","convert(GsonResponseBodyConverter.java:26)-->>"+response);
    return gson.fromJson(response, type);
  }
}

