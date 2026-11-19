/**
* @file Utils.java
* @brief 
* @author Hansen.Z
* @version 1.0
* @date 2019-05-24
*/

package com.yyg.schoolsports.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
	private final static String SP = "config";
	private final static String KEY_NVRAM_SAVED = "nvram_saved";

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (byte b : src) {
            buffer[0] = Character.forDigit((b >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(b & 0x0F, 16);
            stringBuilder.append(" ");
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString().toUpperCase();
    }

	public static byte[] stringToBytes(String str) {
		byte[] byteArray;
		if (str == null || str.length() == 0) {
			return null;
		}
		if (str.length() == 1) {
			str = "0"+str;
		}
		byteArray = new byte[str.length() >> 1];
		for (int i = 0; i < byteArray.length; i++){
			String subStr = str.substring(i<<1, (i<<1) + 2);
			byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
		}
		return byteArray;
	}

	public static void setNvramFlag(Context context, boolean saved) {
		SharedPreferences sp = context.getSharedPreferences(SP, Context.MODE_PRIVATE);
		sp.edit().putBoolean(KEY_NVRAM_SAVED, saved).commit();
	}

	public static boolean getNvramFlag(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP, Context.MODE_PRIVATE);
		return sp.getBoolean(KEY_NVRAM_SAVED, false);
	}
}
