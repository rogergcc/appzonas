package com.example.s3k_user1.appzonas.helper;

import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rogergcc on 20/11/2019.
 * Copyright Ⓒ 2019 . All rights reserved.
 */
public class Utilies {


    public static void getInternet() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static String readKernelVersion() {
        try {
            Process p = Runtime.getRuntime().exec("uname -a");
            InputStream is = null;
            if (p.waitFor() == 0) {
                is = p.getInputStream();
            } else {
                is = p.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is), 1024);
            String line = br.readLine();
            br.close();
            return line;
        } catch (Exception ex) {
            return "ERROR: " + ex.getMessage();
        }
    }


    public static String getDeviceModelNumber() {
        String manufacturer = Build.VERSION.CODENAME;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static  String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
    // get System info.
    public static String OSNAME = System.getProperty("os.name");
    public static String OSVERSION = System.getProperty("os.version");
    public static String RELEASE = android.os.Build.VERSION.RELEASE;
    public static String DEVICE = android.os.Build.DEVICE;
    public static String MODEL = android.os.Build.MODEL;
    public static String PRODUCT = android.os.Build.PRODUCT;
    public static String BRAND = android.os.Build.BRAND;
    public static String DISPLAY = android.os.Build.DISPLAY;
    public static String CPU_ABI = android.os.Build.CPU_ABI;
    public static String CPU_ABI2 = android.os.Build.CPU_ABI2;
    public static String UNKNOWN = android.os.Build.UNKNOWN;
    public static String HARDWARE = android.os.Build.HARDWARE;
    public static String ID = android.os.Build.ID;
    public static String MANUFACTURER = android.os.Build.MANUFACTURER;
    public static String SERIAL = android.os.Build.SERIAL;
    public static String USER = android.os.Build.USER;
    public static String HOST = android.os.Build.HOST;
}
