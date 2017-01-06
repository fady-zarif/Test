package com.tromke.mydrive.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.tromke.mydrive.ParseApplication;

/**
 * Created by Devrath on 10-09-2016.
 */
public class UtilSharedPrefs {

    public static Activity context;

    /*********************************************Network Check*********************************/
    public static void clearSharedPreferences(Activity _context) {
        context=_context;
        SharedPreferences.Editor editor = context.getSharedPreferences(ParseApplication.getPackageNameForRef(), Context.MODE_PRIVATE).edit();
        editor.clear().commit();
    }
    /*********************************************Network Check*********************************/


}
