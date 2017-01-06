package com.tromke.mydrive.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.tromke.mydrive.R;

/**
 * Created by Devrath on 10-09-2016.
 */
public class UtilNetwork {

    public static Activity context;

    /*********************************************Network Check*********************************/
    public static boolean isOnline(Activity _context) {
        context=_context;
        ConnectivityManager cm =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    /*********************************************Network Check*********************************/

    public static boolean checkConnectivity(Activity activity, View view){

        //Perform network validation
        if(UtilNetwork.isOnline(activity)){
            //make the Api call
            return true;
        }else{
            //No Connectivity show the message
            UtilSnackbar.showSnakbarTypeOne(view, activity.getResources().getString(R.string.conn_noconnectivity));
            return false;
        }
    }


}
