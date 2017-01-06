
package com.tromke.mydrive.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MasterData implements Parcelable{

    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("os_type")
    @Expose
    private String osType;
    @SerializedName("documents")
    @Expose
    private List<Document> documents = new ArrayList<Document>();

    protected MasterData(Parcel in) {
        phoneNumber = in.readString();
        name = in.readString();
        deviceId = in.readString();
        osType = in.readString();
        documents = in.createTypedArrayList(Document.CREATOR);
    }

    public static final Creator<MasterData> CREATOR = new Creator<MasterData>() {
        @Override
        public MasterData createFromParcel(Parcel in) {
            return new MasterData(in);
        }

        @Override
        public MasterData[] newArray(int size) {
            return new MasterData[size];
        }
    };

    /**
     * 
     * @return
     *     The phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 
     * @param phoneNumber
     *     The phone_number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The deviceId
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * 
     * @param deviceId
     *     The device_id
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 
     * @return
     *     The osType
     */
    public String getOsType() {
        return osType;
    }

    /**
     * 
     * @param osType
     *     The os_type
     */
    public void setOsType(String osType) {
        this.osType = osType;
    }

    /**
     * 
     * @return
     *     The documents
     */
    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * 
     * @param documents
     *     The documents
     */
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phoneNumber);
        dest.writeString(name);
        dest.writeString(deviceId);
        dest.writeString(osType);
        dest.writeTypedList(documents);
    }
}
