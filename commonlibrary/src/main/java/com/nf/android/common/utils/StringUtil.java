package com.nf.android.common.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static final String REGEX_MOBILE = "^[1][0-9][0-9]{9}$";

    /**
	 * 手机号码的验证，严格验证
	 *
	 * @param mobiles
	 *            要验证的手机号码
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		if (mobiles == null) {
			return false;
		}
		Pattern p = Pattern.compile( REGEX_MOBILE );
		Matcher m = p.matcher(mobiles);
		return m.matches();
//        return mobiles != null && mobiles.length() == 11 ;
	}

	 /**
     * 电话号码验证
     *
     * @param  str
     * @return 验证通过返回true
     */
    public static boolean isPhone(String str) {
    	if (str == null) {
			return false;
		}
        Pattern p1 = null,p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("[0,4,8]{1}[0-9]{2,3}[-]{0,1}[0-9]{7,8}");  // 验证带区号的  
        p2 = Pattern.compile("^[1-9]{1}[0-9]{4,8}$");         // 验证没有区号的  
        if(str.length() >9)
        {   m = p1.matcher(str);
            b = m.matches();
        }else{
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }

	/**
	 * E_mail的验证
	 *
	 * @param email
	 *            要验证的email
	 * @return
	 */
	public static boolean isEmail(String email) {
//		if (email == null) {
//			return false;
//		}
//		String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
//		Pattern p = Pattern.compile(str);
//		Matcher m = p.matcher(email);
//		return m.matches();
        //经与曾玞讨论，去掉所有邮箱校验
        return true ;
	}

	/**
	 * 验证qq是否是合法QQ
	 *
	 * @param qq
	 * @return
	 */
	public static boolean isQQ(String qq) {
//		if (qq == null) {
//			return false;
//		}
//		String str = "^[1-9][0-9]{4,}$";
//		Pattern p = Pattern.compile(str);
//		Matcher m = p.matcher(qq);
//		return m.matches();
        //与IOS统一，不需要长度校验
        return true ;
	}

    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
    
    
// 计算出该TextView中文字的长度(像素)
    public static float getTextViewLength(TextView textView){
	    String text = textView.getText().toString() ;
        TextPaint paint = textView.getPaint();
        // 得到使用该paint写上text的时候,像素为多少
        float textLength = paint.measureText(text);
        return textLength;
    }


    public static String getAssets(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }



    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }if (c[i]> 65280&& c[i]< 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    private static String regex ="[\u4e00-\u9fa5]";

    public static String wipeOfChinese(String orginal) {

        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(orginal);
        String repickStr = mat.replaceAll("");

        return repickStr ;
    }

    public static  String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }

        str = paramString.substring(i + 1);
        return str;
    }

    public static String getDayString( int year, int month, int day ){
        String dayStr = day < 10 ? "0" + day : String.valueOf( day ) ;
        String dateString = getMonthString( year, month ) + "-" + dayStr;
        return dateString ;
    }

    public static String getMonthString( int year, int month ){
        String monthStr = month < 10 ? "0" + month : String.valueOf( month ) ;
        String dateString = year + "-" + monthStr ;
        return dateString ;
    }

    /***
     * 返回工作日 0-6 ；
     * @param y 年
     * @param m 月
     * @param d 日
     * @return
     */
    public static int workDay(int y, int m, int d) {
        if (m == 1 || m == 2) {
            m += 12;
            y--;
        }
        int week = (d + 2 * m + 3 * (m + 1) / 5 + y + y / 4 - y / 100 + y / 400) % 7;   // 基姆拉尔森公式
        return week + 1 ;
    }
}
