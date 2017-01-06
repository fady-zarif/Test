package com.tromke.mydrive.Models;

import java.util.List;

/**
 * Created by Devrath on 28-09-2016.
 */

public class FinalMasterData {

    String phone_number;
    String name;
    String device_id;
    String os_type;
    List<ItemContentData> documents;


    public FinalMasterData(String phone_number, String name, String device_id, String os_type, List<com.tromke.mydrive.Models.ItemContentData> documents) {
        this.phone_number = phone_number;
        this.name = name;
        this.device_id = device_id;
        this.os_type = os_type;
        this.documents = documents;
    }


    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getOs_type() {
        return os_type;
    }

    public void setOs_type(String os_type) {
        this.os_type = os_type;
    }

    public List<ItemContentData> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ItemContentData> documents) {
        this.documents = documents;
    }
}
