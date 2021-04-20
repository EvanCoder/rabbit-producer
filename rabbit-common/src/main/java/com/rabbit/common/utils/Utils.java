package com.rabbit.common.utils;

import java.io.File;
import java.util.Calendar;

/**
 * @author Evan
 * @create 2021/3/4 10:44
 */
public class Utils {

    public static final String DOT_IDWORKERS = ".idworkers";

    private static String defaultRange = "0123456789ABCDFGHKMNPRSTWXYZ";

    public static File createIdWorkerHome(){
        String userHome = System.getProperty("user.home");
        File idWorkerHome = new File(userHome + File.separator + DOT_IDWORKERS);
        idWorkerHome.mkdirs();
        if (idWorkerHome.isDirectory()){
            return idWorkerHome;
        } else {
            throw new RuntimeException("Failed to create .idworkers at user home.");
        }
    }

    public static long midNightMills(){
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return date.getTimeInMillis();
    }

    public static String padLeft(String str, int size, char padchar){
        if (str.length() >= size){
            return str;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = size - str.length(); i > 0; --i){
            sb.append(padchar);
        }
        sb.append(str);

        return sb.toString();
    }

    public static String encode(long num){
        return encode(num, defaultRange);
    }

    public static String encode(long num, String symbols){
        final int b = symbols.length();
        StringBuilder sb = new StringBuilder();
        while (num != 0){
            sb.append(symbols.charAt((int) (num % b)));
            num /= b;
        }
        return sb.reverse().toString();
    }

}
