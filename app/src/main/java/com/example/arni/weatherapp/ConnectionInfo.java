//package com.example.arni.weatherapp;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//
//
//
///**
// * Created by Arni on 2017-08-21.
// */
//
//public class ConnectionInfo extends BroadcastReceiver {
//
//    public static String NETWORK_KEY_MOBILE = "network_on_mobile";
//    public static String NETWORK_KEY_WiFi = "network_on_wifi";
//    private int connected = 1;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//
//
//        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
//            Intent networkOn = new Intent(context, MainActivity.class);
//            networkOn.putExtra(NETWORK_KEY_MOBILE, connected);
//            context.sendBroadcast(networkOn);
//        }
////        if (networkInfo != null) {
////
////            switch (networkInfo.getType()) {
////                case ConnectivityManager.TYPE_MOBILE:
////                    networkOn.putExtra(NETWORK_KEY_MOBILE, connected);
////                    break;
////                case ConnectivityManager.TYPE_WIFI:
////                    networkOn.putExtra(NETWORK_KEY_WiFi, connected);
////                    break;
////            }
////            context.sendBroadcast(networkOn);
//
//    }
//}
//
