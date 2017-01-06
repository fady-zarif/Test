package com.tromke.mydrive.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Devrath on 02/10/16.
 */

public class ResponseUserRegd implements Parcelable{


    int errorCode;
    String errorMessage;

    protected ResponseUserRegd(Parcel in) {
        errorCode = in.readInt();
        errorMessage = in.readString();
    }

    public static final Creator<ResponseUserRegd> CREATOR = new Creator<ResponseUserRegd>() {
        @Override
        public ResponseUserRegd createFromParcel(Parcel in) {
            return new ResponseUserRegd(in);
        }

        @Override
        public ResponseUserRegd[] newArray(int size) {
            return new ResponseUserRegd[size];
        }
    };

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(errorCode);
        parcel.writeString(errorMessage);
    }
}
