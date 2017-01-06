package com.tromke.mydrive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.tromke.mydrive.Constants.ConstantsSharedPreferences;
import com.tromke.mydrive.Registration.Interfaces.IntRegistrationView;
import com.tromke.mydrive.Registration.Presenters.PresenterRegistration;
import com.tromke.mydrive.util.Config;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Devrath on 10-09-2016.
 */
public class ActRegistration extends AppCompatActivity implements IntRegistrationView {

    @BindView(R.id.btn_login)
    Button btn_login;

    @BindView(R.id.login_button)
    Button login_button;

    @BindView(R.id.edt_name_id)
    EditText edt_name_id;

    @BindView(R.id.edt_phone_id)
    EditText edt_phone_id;

    @BindView(R.id.rootId)
    LinearLayout rootId;


    PresenterRegistration presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_registration);
        //Inject views from butter-knife
        ButterKnife.bind(this);
        Firebase.setAndroidContext(this);

        presenter = new PresenterRegistration(this);
        presenter.initPresenter(edt_phone_id);
    }


    @OnClick(R.id.btn_login)
    public void registration() {
        //SignUp to server

        if (edt_name_id.getText().length() == 0 || edt_phone_id.getText().length() < 10) {
            Toast.makeText(this, "Please provide valid information", Toast.LENGTH_LONG).show();
            return;
        }

        //showing progress dialog.
        final ProgressDialog progressDialog = new ProgressDialog(ActRegistration.this, ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle(getResources().getText(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Firebase ref = new Firebase(Config.FIREBASE_URL).child("drivers");
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("name", edt_name_id.getText().toString());
        updates.put("phone", edt_phone_id.getText().toString());
        Map<String, Object> driverObject = new HashMap<String, Object>();
        final String driverKey = ref.push().getKey();
        driverObject.put(driverKey, updates);
        updates.put("profileImage", "");
        updates.put("DLImage", "");
        updates.put("RCImage", "");
        updates.put("taxPaidImage", "");
        updates.put("insuranceImage", "");
        updates.put("timestamp", ServerValue.TIMESTAMP);
        updates.put("customerId",getResources().getString(R.string.customer_id));
        ref.updateChildren(driverObject, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    //error
                } else {
                    ParseApplication.getSharedPreferences().edit().putBoolean(ConstantsSharedPreferences.DRIVER_REGISTERED, true).commit();
                    Intent intent = new Intent(getApplicationContext(), ActHome.class);
                    intent.putExtra(Config.DRIVER_KEY, driverKey);
                    startActivity(intent);
                    progressDialog.cancel();
                }
            }
        });
    }


    @Override
    public void registrationSuccess() {

    }

    @Override
    public void registrationFailure() {

    }

    @Override
    public void validationEmailFailure() {
        edt_name_id.setError("Please enter correct name");
    }

    @Override
    public void validationPasswordFailure() {
        edt_phone_id.setError("Please enter correct phone number");
    }

    @Override
    public void validationPasswordSuccess() {
        presenter.storeCredentials(edt_name_id.getText().toString(), edt_phone_id.getText().toString());
        startActivity(new Intent(getApplicationContext(), ActHome.class));
    }

    @OnClick(R.id.login_button)
    void loginUser() {
        Intent intent = new Intent(ActRegistration.this, ActLogin.class);
        startActivity(intent);
        finish();
    }
}