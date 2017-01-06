package com.tromke.mydrive;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.firebase.client.Firebase;


/**
 * Created by satyam on 23/07/15.
 */
public class ParseApplication extends MultiDexApplication {

    public static String PACKAGE_NAME;
    private static ParseApplication mInstance;
    public static String ACTIVIY_NAME;

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(getApplicationContext());

        // register to be informed of activities starting up
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity,
                                          Bundle savedInstanceState) {

                // new activity created; force its orientation to portrait
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
        PACKAGE_NAME = getApplicationContext().getPackageName();
        setPackageNameForRef(PACKAGE_NAME);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        mInstance = this;////
    }


    public static String getPackageNameForRef() {
        return PACKAGE_NAME;
    }

    public static void setPackageNameForRef(String packageName) {
        PACKAGE_NAME = packageName;
    }

    public static SharedPreferences getSharedPreferences() {
        return mInstance.getSharedPreferences(ParseApplication.getPackageNameForRef(), Context.MODE_PRIVATE);
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }

    public static String getActiviyName() {
        return ACTIVIY_NAME;
    }

    public static void setActiviyName(String activiyName) {
        ACTIVIY_NAME = activiyName;
    }


}
