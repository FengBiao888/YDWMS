package com.nf.android.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class SysUtil {
  private static final String TAG = SysUtil.class.getSimpleName();

	/**
	 * 得到版本号
	 *
	 * @param context
	 * @return
	 */
	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			// Log.e(TAG, e.getMessage());
		}
		return verCode;
	}

	/**
	 * 得到版本名称
	 *
	 * @param context
	 * @return
	 */
	public static String getVerName(Context context) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packageInfo.versionName;
	}

	public static String getMac() {
		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat/sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return macSerial;
	}

	/**
	 * 获取当前系统的版本号
	 *
	 * @return
	 */
	public static int getAndroidSDKInt() {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		return currentapiVersion;
	}

	/**
	 * 获取手机牌子
	 *
	 * @return 手机牌子
	 */
	public static String getPhoneBrand() {
		return android.os.Build.BRAND;
	}

	/**
	 * 获取手机型号
	 *
	 * @return 手机型号
	 */
	public static String getPhoneModel() {
		return android.os.Build.MODEL;
	}

	/**
	 * 获取Android手机的版本号 android2.3
	 *
	 * @return 返回android版本号
	 */
	public static String getAndroidVersionNumber() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 检测当前网络是否连接可用
	 *
	 *
	 * @return true|false
	 */
	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean isNetWorkWifi(Context context) {
		if (isNetworkAvailable(context)) {
			ConnectivityManager conManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (conManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	/** 调用系统发送短信 */
	public static void sendMessage(Context context, String smsto) {

		Uri uri = Uri.parse("smsto:" + smsto);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		context.startActivity(it);
	}

	/** 调用系统打电话 */
	public static void call(Context context, String tel) {

		try {//当用户拒绝授予优服通权限时，调用拨打电话将报错
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + tel.replace("-", "")));
			context.startActivity(intent);
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	public static String getNetworkType(Context context) {
        String strNetworkType = "";

        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifiMgr.getConnectionInfo();
                strNetworkType = info != null ? info.getSSID() : null;

            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                Log.e("SysUtil", "Network getSubtypeName : " + _strSubTypeName);

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }

                Log.e("SysUtil", "Network getSubtype : " + Integer.valueOf(networkType).toString());
            }
        }

        Log.e("SysUtil", "Network Type : " + strNetworkType);

        return strNetworkType;
    }

	// 隐藏软键盘
	public static void hiddenSoftInput(Activity context) {
		if (context.getCurrentFocus() != null) {

			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(context.getCurrentFocus()
					.getWindowToken(), 0); // 强制隐藏键盘
			context.getCurrentFocus().clearFocus();
		}
	}

  /**
   * 判断是否为平板
   *
   * @return
   */
/*  public static boolean isPad(Context context) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    DisplayMetrics dm = new DisplayMetrics();
    display.getMetrics(dm);
    double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
    double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
    // 屏幕尺寸
    double screenInches = Math.sqrt(x + y);
    Log.i(TAG, "screenInches==>" + screenInches);
    Log.i(TAG, "phone model==>" + SysUtil.getPhoneModel());
    // 大于10尺寸则为Pad
    if (screenInches >= 10.0) {
      return true;
    }
    return false;
  }*/

    public static boolean isPad() {
      if (!TextUtils.isEmpty(SysUtil.getPhoneModel()) && SysUtil.getPhoneModel().startsWith("CMR-AL19")) {
        return true;
      }
      return false;
    }

  /**
   * 判断是否为平板
   * @param context
   * @return
   */
  public static boolean isTablet(Context context) {
    return (context.getResources().getConfiguration().screenLayout
      & Configuration.SCREENLAYOUT_SIZE_MASK)
      >= Configuration.SCREENLAYOUT_SIZE_LARGE;
  }
}
