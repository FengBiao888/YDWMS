package com.yundao.ydwms.retrofit;

import android.text.TextUtils;

import com.yundao.ydwms.YDWMSApplication;
import com.yundao.ydwms.protocal.Log;
import com.yundao.ydwms.protocal.URLConstant;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class LoggingInterceptor implements Interceptor {

    private final String TAG = "LoggingInterceptor";
    private final Charset UTF8 = Charset.forName("UTF-8");

    public okhttp3.Response intercept(Chain chain) throws IOException {
        //获得请求信息，此处如有需要可以添加headers信息
        Request original = chain.request();
        HttpUrl httpUrl = chain.request().url();
        String url = httpUrl.url().toString();

        //添加头部信息
//        if( )
        Request.Builder requestBuilder = original.newBuilder();
        String authorization = YDWMSApplication.getInstance().getAuthorization();
        if( ! TextUtils.isEmpty(authorization) ) {
            requestBuilder.addHeader("Authorization", authorization);
        }
        if( url.contains(URLConstant.PRODUCTION_OUTGOING) ){//Content-Type: application/json
            requestBuilder.addHeader("Content-Type", "application/json");
        }
        requestBuilder.method(original.method(), original.body());
        Request request = requestBuilder.build();

        //打印请求信息
        syso("url:" + request.url());
        syso("method:" + request.method());
        Headers requestHeaders = request.headers();
        syso("request headers=========="+ requestHeaders.toString());
//    syso("request-body:" + request.body());
        RequestBody requestBody = request.body();
        String body = null;
        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            body = buffer.readString(charset);
        }
        syso("request-body:" + body);
        //记录请求耗时
        long startNs = System.nanoTime();
        okhttp3.Response response;
        try {
            //发送请求，获得相应，
            response = chain.proceed(request);
        } catch (Exception e) {
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        //打印请求耗时
        syso("耗时:" + tookMs + "ms");
        syso("url:" + request.url());
        //使用response获得headers(),可以更新本地Cookie。
        syso("respone headers==========");
        Headers headers = response.headers();
        syso(headers.toString());
        //获得返回的body，注意此处不要使用responseBody.string()获取返回数据，原因在于这个方法会消耗返回结果的数据(buffer)
        ResponseBody responseBody = response.body();
        //为了不消耗buffer，我们这里使用source先获得buffer对象，然后clone()后使用
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        //获得返回的数据
        Buffer buffer = source.buffer();
        //使用前clone()下，避免直接消耗
        syso("response:" + buffer.clone().readString(Charset.forName("UTF-8")));
        return response;
    }

    private void syso(String msg) {
//    System.out.println(msg);
        Log.d(TAG, msg);
    }
}
