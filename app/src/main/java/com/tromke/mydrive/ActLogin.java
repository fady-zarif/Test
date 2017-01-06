package com.tromke.mydrive;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tromke.mydrive.Constants.ConstantsSharedPreferences;
import com.tromke.mydrive.Login.Interfaces.IntLoginView;
import com.tromke.mydrive.Login.Presenters.PresenterLogin;
import com.tromke.mydrive.Utils.UtilActivitiesNavigation;
import com.tromke.mydrive.Utils.UtilSnackbar;
import com.tromke.mydrive.util.Config;
import com.tromke.mydrive.util.ConnectionManager;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Devrath on 12/10/16.
 */

public class ActLogin extends AppCompatActivity implements IntLoginView {

    @BindView(R.id.btn_login)
    Button btn_login;

    @BindView(R.id.edt_name_id)
    EditText edt_name_id;

    @BindView(R.id.edt_phone_id)
    EditText edt_phone_id;

    @BindView(R.id.rootId)
    LinearLayout rootId;


    PresenterLogin presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(ActLogin.this, ShiftOnOffActivity.class));
            finish();
        }
        setContentView(R.layout.act_login);
        //Inject views from butter-knife
        ButterKnife.bind(this);


        presenter = new PresenterLogin(this);
        presenter.initPresenter(edt_phone_id);


    }


    @OnClick(R.id.btn_login)
    public void registration() {
        //SignUp to server
        presenter.attemptRegister(edt_name_id.getText().toString(),
                edt_phone_id.getText().toString());
    }


    @Override
    public void loginSuccess(String mMsg,int mCode) {
        if(mCode == 200){
            UtilActivitiesNavigation.startActivityWithBackStackClear(ActLogin.this, ActHome.class);
        }else if(mCode == 201){
            UtilSnackbar.showSnakbarTypeOne(rootId, mMsg);
        }
    }

    @Override
    public void loginFailure() {
        UtilSnackbar.showSnakbarTypeOne(rootId, getResources().getString(R.string.login_failure));
    }

    @Override
    public void validationEmailFailure() {
        edt_name_id.setError("Please enter correct email");
    }

    @Override
    public void validationPasswordFailure() {
        edt_phone_id.setError("Please enter correct password");
    }

    @Override
    public void validationPasswordSuccess() {
        if(ConnectionManager.getInstance(getApplicationContext()).isDeviceConnectedToInternet()){
            final ProgressDialog dialog = new ProgressDialog(ActLogin.this,ProgressDialog.THEME_HOLO_LIGHT);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setTitle(getResources().getText(R.string.please_wait));
            dialog.setCancelable(false);
            dialog.show();

            //authenticate user
            FirebaseAuth.getInstance().signInWithEmailAndPassword(edt_name_id.getText().toString(), edt_phone_id.getText().toString())
                    .addOnCompleteListener(ActLogin.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            if (!task.isSuccessful()) {
                                // there was an error
                                Toast toast = Toast.makeText(ActLogin.this,task.getException().getLocalizedMessage(), Toast.LENGTH_LONG);
                                toast.show();

                            } else {
                                getHyperTrackIdForDriver();
                            }
                        }
                    });

        }  else{
            Toast.makeText(getApplicationContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
        }
    }

    public void getHyperTrackIdForDriver() {
        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase(Config.FIREBASE_URL).child("drivers");

        //Progress Dialog
        final ProgressDialog getDriverIdDialog = new ProgressDialog(ActLogin.this, AlertDialog.THEME_HOLO_LIGHT);
        getDriverIdDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        getDriverIdDialog.setTitle(getResources().getString(R.string.authenticating_driver));
        getDriverIdDialog.setCancelable(false);
        getDriverIdDialog.show();

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query queryRef = ref.orderByChild("UUID").equalTo(userId);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChild) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                    String driverId = String.valueOf(value.get("UUID"));
                    String hypertrack_id = String.valueOf(value.get("hypertrack_id"));
                    if (driverId.equals(userId.toString()) && !hypertrack_id.equals("null")) {
                        ParseApplication.getSharedPreferences().edit().putString(ConstantsSharedPreferences.HYPERTRACK_ID, hypertrack_id).commit();
                        getDriverIdDialog.dismiss();

                        Intent intent = new Intent(ActLogin.this, ShiftOnOffActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        getDriverIdDialog.dismiss();
                        Toast.makeText(ActLogin.this, "Network Error please login again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    getDriverIdDialog.dismiss();
                    Toast.makeText(ActLogin.this, "Network Error please login again.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                int i=0;
            }
        });
    }

}