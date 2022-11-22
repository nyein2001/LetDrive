package com.lightidea.products.letdrive.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkCheck {

    public static boolean isConnected(Context context) {

        try {
            ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isConnected() || networkInfo.isConnectedOrConnecting();
            } else {
                return false;
            }
        } catch (Exception e){
            return false;
        }

    }

}
