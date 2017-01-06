package com.tromke.mydrive;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;



public class RegisterActivity extends AppCompatActivity {
    private ProgressDialog loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        loadingProgress = new ProgressDialog(RegisterActivity.this,
                ProgressDialog.THEME_HOLO_LIGHT);
        loadingProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingProgress.setTitle(getResources().getString(R.string.app_name));
        loadingProgress.setMessage("Loading..");
    }

    public void signIn(View v) {
        EditText username_edit = (EditText) findViewById(R.id.email);
        EditText pswd_edit = (EditText) findViewById(R.id.password);
        EditText cfmpswd_edit=(EditText)findViewById(R.id.cfmpassword);
        EditText addrs_edit=(EditText)findViewById(R.id.addrs);
        EditText contact_edit=(EditText)findViewById(R.id.mobile_number);
        if (!username_edit.getText().toString().isEmpty() && !pswd_edit.getText().toString().isEmpty()
                &&!cfmpswd_edit.getText().toString().isEmpty() && !addrs_edit.getText().toString().isEmpty()&& !contact_edit.getText().toString().isEmpty()) {
            if (pswd_edit.getText().toString().trim().equals(cfmpswd_edit.getText().toString().trim()))
            {
                Toast.makeText(getApplicationContext(), "Under Implementation.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Password did not match.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Please fill the fields", Toast.LENGTH_SHORT).show();
        }

    }


}
