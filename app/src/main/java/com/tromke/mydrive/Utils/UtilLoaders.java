package com.tromke.mydrive.Utils;

import android.content.Context;

import com.tromke.mydrive.Home.Dialogs.DlgTransparentProgress;

/**
 * Created by Devrath on 11-09-2016.
 */
public class UtilLoaders {

    static DlgTransparentProgress mProgress;

    /**********************************ShowLoading**********************************/
    public static DlgTransparentProgress showCircularLoadingDialog(DlgTransparentProgress progress, Context activity, String text) {
        mProgress=progress;
        if (mProgress == null) {
            mProgress = new DlgTransparentProgress(activity, text);
            mProgress.show();
            return mProgress;
        }
        return null;
    }

    public static DlgTransparentProgress showCircularLoadingDialog(DlgTransparentProgress progress, Context activity) {
        mProgress=progress;
        if (mProgress == null) {
            mProgress = new DlgTransparentProgress(activity, "");
            mProgress.show();
            return mProgress;
        }
        return null;
    }

    /**********************************DismissLoading**********************************/
    public static void dismissCircularLoadingDialog() {

        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

}
