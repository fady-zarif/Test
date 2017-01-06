package com.tromke.mydrive.util;

/**
 * Created by Amar on 01-11-2016.
 */
public class Config {
    public static final String FIREBASE_URL = "https://driver-app-b6825.firebaseio.com/";
    public static final String FIREBASE_STORAGE_URL = "gs://driver-app-b6825.appspot.com";
    public static final String DRIVER_KEY = "driverKey";
    public static final String IMAGE_NAME = "imageName";
    public static final String HYPER_TRACK_ID = "hyperTrackId";

    public static String getPicFieldName(String name) {
        String lname = null;
        switch (name) {
            case "Your Photo":
                lname = "profileImage";
                break;
            case "Driving Licence":
                lname = "DLImage";
                break;
            case "Tax Receipt":
                lname = "taxPaidImage";
                break;
            case "RC Card Photo":
                lname = "RCImage";
                break;
            case "Insurance Photo":
                lname = "insuranceImage";
                break;
            default:
                break;
        }
        return lname;
    }
}
