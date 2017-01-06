package com.tromke.mydrive.Models;

/**
 * Created by Devrath on 13/10/16.
 */

public class LoginData {


    String phone_number;
    String password;


    public LoginData(String mName, String mNumber) {
        this.phone_number = mNumber;
        this.password = mName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
