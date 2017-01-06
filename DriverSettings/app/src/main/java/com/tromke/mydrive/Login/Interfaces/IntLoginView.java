package com.tromke.mydrive.Login.Interfaces;

/**
 * Created by Devrath on 10-09-2016.
 */
public interface IntLoginView {
    void loginSuccess(String mMsg,int mCode);
    void loginFailure();
    void validationEmailFailure();
    void validationPasswordFailure();
    void validationPasswordSuccess();
}
