package com.tromke.mydrive.Home.Validations;

import android.content.Context;
import android.net.ConnectivityManager;
import com.tromke.mydrive.Models.ItemObjects;

import java.util.LinkedList;

/**
 * Created by Devrath on 27-09-2016.
 */

public class ValHome {

    public ValHome(){
    }

    public boolean isProofNotAdded(LinkedList<ItemObjects> mData) {
        //Check if any of data is not present
        for (int pos = 0; pos < mData.size(); pos++) {
            if (mData.get(pos).isDataAdded() == false) {
                return true;
            }
        }
        return false;
    }

    public String whichProofNotPresent(LinkedList<ItemObjects> mData) {
        //Check if any of data is not present
        for (int pos = 0; pos < mData.size(); pos++) {
            if (mData.get(pos).isDataAdded() == false) {
                return mData.get(pos).getName();
            }
        }
        return "";
    }


    public boolean isOnline(Context mContext)
    {
        try
        {
            ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        catch (Exception e)
        {
            return false;
        }
    }




}
