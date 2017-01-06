package com.tromke.mydrive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.tromke.mydrive.Models.DriverData;

public class Driver_Profile extends AppCompatActivity {
    ImageView ProfileImage;
    TextView driverEmail, driverName, driverPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver__profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile");
        DriverData driverData = getIntent().getParcelableExtra("driver_profile");
        ProfileImage = (ImageView) findViewById(R.id.driver_profile);
        driverEmail = (TextView) findViewById(R.id.driver_email);
        driverName = (TextView) findViewById(R.id.driver_name);
        driverPhone = (TextView) findViewById(R.id.driver_phone);

        //get driver data from parcelable and set data
        try {
            Picasso.with(getApplicationContext()).load(driverData.getProfileImage()).resize(150, 150).placeholder(R.drawable.profile3)
                    .into(ProfileImage);

            driverEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
            driverName.setText(driverData.getName().toString());
            driverPhone.setText(driverData.getPhone().toString());
        } catch (Exception ex) {
            Toast.makeText(this, "Check your connection and try again soon", Toast.LENGTH_SHORT).show();

        }
    }
}
