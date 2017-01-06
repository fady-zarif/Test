package com.tromke.mydrive.Registration.Presenters;


import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import com.tromke.mydrive.Constants.ConstantsSharedPreferences;
import com.tromke.mydrive.ParseApplication;
import com.tromke.mydrive.Registration.Interfaces.IntRegistrationView;
import com.tromke.mydrive.Registration.Validations.ValRegistration;
import com.tromke.mydrive.ActRegistration;

/**
 * Created by Devrath on 9/5/2016.
 */
public class PresenterRegistration {

    private IntRegistrationView view;
    private ValRegistration mVal;
    private String blockCharacterSet = "~#^|$%&*!";

    public PresenterRegistration(ActRegistration view) {

        //Set the user view
        this.view=view;
        //validation Logic
        mVal=new ValRegistration();

    }

    public void initPresenter(EditText edt_phone_id) {
        //edt_phone_id.setFilters(new InputFilter[] { filter });
    }


    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null || blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    public void attemptRegister(String mail, String phone) {
        if(mVal.validateRegistrationEmail(mail)){
            //Email input is not correct
            view.validationEmailFailure();
        }else{
            if(mVal.validateLoginPasswordLessThanTenChars(phone)){
                //Password input is correct
                view.validationPasswordFailure();
            }else{
                //Password input is correct
                view.validationPasswordSuccess();
            }

        }


    }


    public void storeCredentials(String mName, String mNumber) {
        ParseApplication.getSharedPreferences().edit().putString(ConstantsSharedPreferences.STRING_USER_NAME,mName).commit();
        ParseApplication.getSharedPreferences().edit().putString(ConstantsSharedPreferences.STRING_PHONE_NUMBER,mNumber).commit();
    }
}
