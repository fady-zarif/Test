package com.tromke.mydrive.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Devrath on 28-09-2016.
 */

public class ResponseData implements Parcelable {

    int errorCode;
    String token;
    String errorMessage;


    protected ResponseData(Parcel in) {
        errorCode = in.readInt();
        token = in.readString();
        errorMessage = in.readString();
    }

    public static final Creator<ResponseData> CREATOR = new Creator<ResponseData>() {
        @Override
        public ResponseData createFromParcel(Parcel in) {
            return new ResponseData(in);
        }

        @Override
        public ResponseData[] newArray(int size) {
            return new ResponseData[size];
        }
    };

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(errorCode);
        dest.writeString(token);
        dest.writeString(errorMessage);
    }
}
