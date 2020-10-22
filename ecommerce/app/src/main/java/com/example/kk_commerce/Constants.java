package com.example.kk_commerce;
import android.util.Log;

import java.net.InetAddress;


public class Constants {
    public static String BASE_URL = "http://192.168.0.11:8000/api/v1/";
    public static String MEDIA_BASE = "http://192.168.0.11:8000";

    public static boolean isServerReachable() {
        try {

            boolean result = InetAddress.getByName("http://192.168.0.11:8000").isReachable(2000); //Replace with your name
            Log.d("Connection Result", ""+result);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
