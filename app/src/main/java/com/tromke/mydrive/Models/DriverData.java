package com.tromke.mydrive.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by foda_ on 2017-01-04.
 */

public class DriverData implements Parcelable{
    String DLImage;
    String RCImage;
    String taxPaidImage;
    String insuranceImage;
    String name;
    String phone;
    String profileImage;
    

    public DriverData() {

    }

    public DriverData(String DLImage, String RCImage , String insuranceImage, String taxPaidImage,String name, String phone, String profileImage) {
        this.DLImage = DLImage;
        this.RCImage = RCImage;
        this.taxPaidImage=taxPaidImage;
        this.insuranceImage = insuranceImage;
        this.name = name;
        this.phone = phone;
        this.profileImage = profileImage;
    }


    protected DriverData(Parcel in) {
        DLImage = in.readString();
        RCImage = in.readString();
        taxPaidImage = in.readString();
        insuranceImage = in.readString();
        name = in.readString();
        phone = in.readString();
        profileImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(DLImage);
        dest.writeString(RCImage);
        dest.writeString(taxPaidImage);
        dest.writeString(insuranceImage);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(profileImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DriverData> CREATOR = new Creator<DriverData>() {
        @Override
        public DriverData createFromParcel(Parcel in) {
            return new DriverData(in);
        }

        @Override
        public DriverData[] newArray(int size) {
            return new DriverData[size];
        }
    };

    public String getDLImage() {
        return DLImage;
    }

    public String getRCImage() {
        return RCImage;
    }

    public String getInsuranceImage() {
        return insuranceImage;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setDLImage(String DLImage) {
        this.DLImage = DLImage;
    }

    public void setRCImage(String RCImage) {
        this.RCImage = RCImage;
    }

    public String getTaxPaidImage() {
        return taxPaidImage;
    }

    public void setTaxPaidImage(String taxPaidImage) {
        this.taxPaidImage = taxPaidImage;
    }
    public void setInsuranceImage(String insuranceImage) {
        this.insuranceImage = insuranceImage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }




}
