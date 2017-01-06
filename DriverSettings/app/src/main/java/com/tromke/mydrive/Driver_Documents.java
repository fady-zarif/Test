package com.tromke.mydrive;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tromke.mydrive.Models.DriverData;

public class Driver_Documents extends AppCompatActivity {
    ImageView DL, RC, Insurance, TaxPaid;
    TextView DocOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver__documents);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Documents");
        DL = (ImageView) findViewById(R.id.DL_Image);
        RC = (ImageView) findViewById(R.id.RC_Image);
        Insurance = (ImageView) findViewById(R.id.Insurance_Image);
        TaxPaid = (ImageView) findViewById(R.id.Tax_Paid);
        DocOwner = (TextView) findViewById(R.id.documents_owner);
        final DriverData driverData = getIntent().getParcelableExtra("driver_profile");
        try {
            DocOwner.setText(driverData.getName().toString());
            Picasso.with(getApplicationContext()).load(driverData.getDLImage()).resize(150, 150).placeholder(R.drawable.loading_image)
                    .into(DL);
            Picasso.with(getApplicationContext()).load(driverData.getRCImage()).resize(150, 150).placeholder(R.drawable.loading_image)
                    .into(RC);
            Picasso.with(getApplicationContext()).load(driverData.getInsuranceImage()).resize(150, 150).placeholder(R.drawable.loading_image)
                    .into(Insurance);
            Picasso.with(getApplicationContext()).load(driverData.getTaxPaidImage()).resize(150, 150).placeholder(R.drawable.loading_image)
                    .into(TaxPaid);
            DL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    show_in_dialog(driverData.getDLImage());
                }
            });

            RC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    show_in_dialog(driverData.getRCImage());
                }
            });
            Insurance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    show_in_dialog(driverData.getInsuranceImage());
                }
            });
            TaxPaid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    show_in_dialog(driverData.getTaxPaidImage());
                }
            });
        } catch (Exception ex) {
        }

    }

    public void show_in_dialog(String img) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Driver_Documents.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.maximiz, null);
        //   LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ImageView myimage = (ImageView) view.findViewById(R.id.myImageView);
        Picasso.with(getApplicationContext()).load(img).placeholder(R.drawable.loading_image).into(myimage);
        myimage.setScaleType(ImageView.ScaleType.FIT_XY);
        builder.setView(view);
        builder.show();
    }
}
