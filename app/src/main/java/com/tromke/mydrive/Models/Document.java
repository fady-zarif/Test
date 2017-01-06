
package com.tromke.mydrive.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Document implements Parcelable{

    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("proofname")
    @Expose
    private String proofname;

    protected Document(Parcel in) {
        content = in.readString();
        name = in.readString();
        proofname = in.readString();
    }

    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel in) {
            return new Document(in);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };

    /**
     * 
     * @return
     *     The content
     */
    public String getContent() {
        return content;
    }

    /**
     * 
     * @param content
     *     The content
     */
    public void setContent(String content) {
        this.content = content;
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
     *     The proofname
     */
    public String getProofname() {
        return proofname;
    }

    /**
     * 
     * @param proofname
     *     The proofname
     */
    public void setProofname(String proofname) {
        this.proofname = proofname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(name);
        dest.writeString(proofname);
    }
}
