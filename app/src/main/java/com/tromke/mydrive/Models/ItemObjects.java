package com.tromke.mydrive.Models;

import android.net.Uri;

public class ItemObjects {

    private String name;
    private Uri photo;
    private boolean isDataAdded;

    public ItemObjects(String name, Uri photo, boolean isDataAdded) {
        this.name = name;
        this.photo = photo;
        this.isDataAdded = isDataAdded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

    public boolean isDataAdded() {
        return isDataAdded;
    }

    public void setDataAdded(boolean dataAdded) {
        isDataAdded = dataAdded;
    }
}
