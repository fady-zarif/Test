package com.tromke.mydrive;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tromke.mydrive.util.ConnectionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity implements View.OnClickListener, TextView.OnEditorActionListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private FirebaseAuth auth;
    Button mRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, TripDetailsActivity.class));
            finish();
        }
        setContentView(com.tromke.mydrive.R.layout.activity_login);

       // Set up the login form.
        mEmailView = (EditText) findViewById(com.tromke.mydrive.R.id.username);

        mPasswordView = (EditText) findViewById(com.tromke.mydrive.R.id.password);
             mPasswordView.setOnEditorActionListener(this);
        Button mLoginButton = (Button) findViewById(com.tromke.mydrive.R.id.sign_in_button);
        mLoginButton.setOnClickListener(this);
        mRegisterBtn=(Button) findViewById(R.id.register_button);
        mRegisterBtn.setOnClickListener(this);
       // Button mRegisterButton = (Button) findViewById(R.id.register_button);
       // mRegisterButton.setOnClickListener(this);
        mProgressView = findViewById(com.tromke.mydrive.R.id.login_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Password field is required");
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);

           if(ConnectionManager.getInstance(getApplicationContext()).isDeviceConnectedToInternet()){
               final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
               dialog.setMessage("Please wait");
               dialog.show();

               //authenticate user
               auth.signInWithEmailAndPassword(email, password)
                       .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
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
                                   Context context = getApplicationContext();
                                   CharSequence text = "Error in authentication!";
                                   int duration = Toast.LENGTH_SHORT;

                                   Toast toast = Toast.makeText(context, task.getException().getLocalizedMessage(), duration);
                                   toast.show();

                               } else {
                                   Intent intent = new Intent(LoginActivity.this, TripDetailsActivity.class);
                                   startActivity(intent);
                                   finish();
                               }
                           }
                       });

        }  else{
               Toast.makeText(getApplicationContext(),getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
           }
        }


    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_in_button:
                attemptLogin();
                break;
           case R.id.register_button:
                 registerUser();
                 break;
            default: break;

        }
    }

    private void registerUser() {
        Intent intent = new Intent(LoginActivity.this, ActRegistration.class);
        startActivity(intent);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId ==R.id.password || actionId == EditorInfo.IME_NULL) {
            attemptLogin();
            return true;
        }
        return false;
    }
}



