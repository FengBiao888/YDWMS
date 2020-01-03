package com.yundao.ydwms.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class SharedPreferenceUtil {

    private final static String PREFERENCE_NAME = "com.yundao.safe.android";
    /**
     * 服务器地址配置方式，0：默认，1：自定义
     */
    public static final String SERVER_ADDRESS_CONFIG_TYPE = "server_address_config_type";
    /**
     * 自定义配置IP
     */
    public static final String CUSTOM_IP = "user_custom_ip";

    private static SharedPreferences preferences;
    private static Editor editor;

    /**
     * 存储手机号码的key
     */
    public static final String USER_BOBILE = "user_mobile";
    /**
     * 存储验证码的key
     */
    public static final String VALIDATE_CODE = "validate_code";
    /**
     * 存储密码的key
     */
    public static final String PASSWORD = "password";
    /**
     * 存储公司名称
     */
    public static final String COMPANY_NAME = "company_name";
    /**
     * 图片地址
     */
    public static final String RES_URL = "res_url";
    /**
     * 身份证号
     */
    public static final String ID_CARD = "id_card";
    /**
     * 业务员姓名
     */
    public static final String COMPANY_OPERATOR_NAME = "company_operator_name";
    /**
     * 业务员服务电话
     */
    public static final String COMPANY_OPERATOR_TELEPHONE = "company_operator_telephone";
    /**
     * 籍贯
     */
    public static final String NATIVE_PLACE = "nativePlace";
    /**
     * 存储公司帐号
     */
    public static final String COMPANY_ACCOUNT = "company_account";
    /**
     * 存储公司id
     */
    public static final String COMPANY_ID = "company_id";
    /**
     * 聊天系统ID
     */
    public static final String IM_LOGIN_NAME = "im_login_name";
    /**
     * 存储部门id
     */
    public static final String DEP_ID = "dep_id";
    /**
     * 存储部门名称
     */
    public static final String DEP_NAME = "dept_info";
    /**
     * 存储用户id
     */
    public static final String USER_ID = "user_id";
    /**
     * 存储用户姓名
     */
    public static final String USER_NAME = "user_name";
    /**
     * 存储用户是否是系统管理员
     */
    public static final String IS_USER_MANAGER = "is_user_manager";
    /**
     * 存储token_id
     */
    public static final String TOKEN_ID = "token_id";
    /**
     * 存储用户角色
     */
    public static final String USER_MANAGE = "user_manage";
    /**
     * 存储当前用户类型
     */
    public static final String USER_TYPE = "user_type";
    /**
     * 存储用户头像地址
     */
    public static final String USER_PICTURE = "user_picture";
    /**
     * 存储用户权限
     */
    public static final String PERMISSION_INFO = "permission_info";
    /**
     * 存储用户权限个数
     */
    public static final String PERMISSION_INFO_COUNT = "permission_info_count";
    /**
     * 是否自动登录
     */
    public static final String AUTO_LOGIN = "auto_login";
    /**
     * 是否第一次启动
     */
    public static final String FIRST_SET_UP = "first_set_up";
    /**
     * 是否开启上报轨迹
     */
    public static final String LOC_ON = "loc_on";
    /**
     * 保存最后一次上传的位置经度
     */
    public static final String LAST_LAT = "last_lat";
    /**
     * 保存最后一次上传的位置纬度
     */
    public static final String LAST_LNG = "last_lng";
    /**
     * 保存最后一次位置的时间
     */
    public static final String LAST_SAVED_TIME = "last_saved_time";
    /**
     * 最近一次定位的城市
     */
    public static final String CITY = "city";
    public static final String COUNTY = "county";
    /**
     * 最近一次考勤时间毫秒数
     */
    public static final String LAST_ATTENDANCE_TIME = "last_attendance_time";

    // 15* 24小时
    public static final long CLEARCACHE_INVERAL = 15 * 24 * 60 * 60 * 1000;

    /**
     * 上一次清除缓存的时间
     */
    public static final String LAST_CLEAR_CACHETIME = "last_clear_cachetime";

    /**
     * 考勤周期 保存类型为string 格式为星期一,星期二,星期三....
     */
    public static final String KQ_REPEAT = "kq_repeat";

    /**
     * 考勤上班时间
     */
    public static final String KQ_START_TIME = "kq_start_time";

    /**
     * 考勤考勤下班
     */
    public static final String KQ_END_TIME = "kq_end_time";

    /**
     * 考勤规则个数
     */
    public static final String KQ_RULE_COUNT = "kq_rule_count";
    /**
     * 保存打考勤类型的key
     */
    public static final String KQ_TYPE = "kq_type";
    /**
     * 保存最后一次更新通讯录数据库的版本
     */
    public static final String DATA_VERSION = "data_version";
    /**
     * 保存最后一次更新客户数据库的版本
     */
    public static final String CUSTOMER_DATA_VERSION = "customer_data_version";
     /**
      * 保存最后一次更新客户数据库的版本
     */
    public static final String ATTENDANCE_WIFI_LIST = "attendance_wifi_list";
    /**
     * 中小幼培训的用户id
     */
    public static final String STUDY_TRAIN_USERID = "study_train_userid";
    /**
     * 单位id(登录接口用)，登录时有多家公司业务员选的
     */
    public static final String LOGIN_COMPANY_ID = "login_company_id";
    /**
     * 入职登记的类
     */
    public static final String SHARE_HR_STAFF = "share_hr_staff";
    /**
     * 劳动合同签署
     */
    public static final String ELSIGNATURE_HR_STAFF = "elsignature_hr_staff";

    /**
     * 手机IMEI编码
     */
    public static final String DEVICE_IMEI = "device_imei";

    public static void initPreference(Context context) {
        preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInt(String key, int defValue) {
        return preferences.getInt(key, defValue);
    }

    public static void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getLong(String key, long defValue) {
        return preferences.getLong(key, defValue);
    }

    public static void putString(String key, String value) {
        try {
            editor.putString(key, AESUtil.encrypt(value)).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void putStringWithoutAES(String key, String value) {
        try {
            editor.putString(key, value).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getStringWithoutAES(String key, String defValue) {

        try {
            return preferences.getString(key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return preferences.getString(key, defValue);
    }

    public static String getString(String key, String defValue) {

        try {
            return AESUtil.decrypt(preferences.getString(key, defValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return preferences.getString(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    /**
     * 保存object对象
     * @param key
     * @param obj
     */
    public static void putObject(String key, Object obj) {
        try {
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ObjectOutputStream os=new ObjectOutputStream(bos);
            //将对象序列化写入byte缓存
            os.writeObject(obj);
            //将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            //保存该16进制数组
            editor.putString(key, bytesToHexString);
            boolean commited = editor.commit();
            Log.d("SharePreferenceUtil", "commited: " + commited);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * desc:将数组转为16进制
     * @param bArray
     * @return
     * modified:
     */
    public static String bytesToHexString(byte[] bArray) {
        if(bArray == null){
            return null;
        }
        if(bArray.length == 0){
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * desc:获取保存的Object对象
     * @param key
     * @return
     * modified:
     */
    public static Object getObject(String key){
        try {
            if (preferences.contains(key)) {
                String string = preferences.getString(key, "");
                if(TextUtils.isEmpty(string)){
                    return null;
                }else{
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis=new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is=new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    return readObject;
                }
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //所有异常返回null
        return null;

    }

    /**
     * desc:将16进制的数据转为数组
     * <p>创建人：聂旭阳 , 2014-5-25 上午11:08:33</p>
     * @param data
     * @return
     * modified:
     */
    public static byte[] StringToBytes(String data){
        String hexString=data.toUpperCase().trim();
        if (hexString.length()%2!=0) {
            return null;
        }
        byte[] retData=new byte[hexString.length()/2];
        for(int i=0;i<hexString.length();i++)
        {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); ////两位16进制数中的第一位(高位*16)
            int int_ch3;
            if(hex_char1 >= '0' && hex_char1 <='9')
                int_ch3 = (hex_char1-48)*16;   //// 0 的Ascll - 48
            else if(hex_char1 >= 'A' && hex_char1 <='F')
                int_ch3 = (hex_char1-55)*16; //// A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
            int int_ch4;
            if(hex_char2 >= '0' && hex_char2 <='9')
                int_ch4 = (hex_char2-48); //// 0 的Ascll - 48
            else if(hex_char2 >= 'A' && hex_char2 <='F')
                int_ch4 = hex_char2-55; //// A 的Ascll - 65
            else
                return null;
            int_ch = int_ch3+int_ch4;
            retData[i/2]=(byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }

    public static boolean remove(String key) {
        return editor.remove(key).commit();
    }

    public static boolean removeAll(Context context, String spName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences( spName, Context.MODE_PRIVATE );
        Editor edit = sharedPreferences.edit();
        return edit.clear().commit() ;
    }

    public static boolean contains(String key) {
        return preferences.contains(key);
    }


}
