package com.yundao.ydwms.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class NetUtil {

	private static final String TAG = "";

	public static NetworkInfo getCurrentActiveNetworkInfo(Context context) {
		NetworkInfo networkInfo = null;
		// 获取手机所有连接管理对象（包括对wi-fi,net,gsm,cdma等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager != null) {
			networkInfo = connectivityManager.getActiveNetworkInfo();

			Log.d(TAG, "Current Active Network : " + networkInfo);

		}

		return networkInfo;
	}

	public static NetType getCurrentNetType(Context context) {
		NetType type = NetType.NONE;

		// 获取当前活动的网络
		NetworkInfo info = getCurrentActiveNetworkInfo(context);
		if (info == null) {
			return type;
		}

		// 判断当前网络是否已经连接
		if (info.getState() == NetworkInfo.State.CONNECTED) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				type = NetType.WIFI;
			} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				String subTypeName = info.getSubtypeName().toUpperCase();

				if (subTypeName.indexOf("GPRS") > -1) {
					type = NetType.MOBILE_GPRS;
				} else if (subTypeName.indexOf("EDGE") > -1) {
					type = NetType.MOBILE_EDGE;
				} else {
					type = NetType.MOBILE_3G;
				}
			} else {
				type = NetType.UNKNOW;
			}
		} else if (info.getState() == NetworkInfo.State.CONNECTING) {
			type = NetType.UNKNOW;
			System.out.println("connecting " + info.getType());
		}

		return type;
	}
}
