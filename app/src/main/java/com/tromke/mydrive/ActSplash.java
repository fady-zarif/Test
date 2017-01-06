package com.tromke.mydrive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;

public class ActSplash extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
       // presenter = new PresenterSplash(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(ActSplash.this, ShiftOnOffActivity.class));
            finish();
        } else {
            startActivity(new Intent(ActSplash.this,ActRegistration.class));
            finish();
        }
    }

}
