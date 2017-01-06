package com.tromke.mydrive;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tromke.mydrive.Constants.ConstantsSharedPreferences;
import com.tromke.mydrive.Models.Trip;
import com.tromke.mydrive.util.ConnectionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripsActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter<Trip, TripsViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        RecyclerView trips = (RecyclerView) findViewById(R.id.trips);

        final ProgressDialog progressDialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle(getResources().getText(R.string.app_name));
        progressDialog.setMessage("Please wait..getting your trips information.!");
        progressDialog.show();

        com.google.firebase.database.Query query = FirebaseDatabase.getInstance().getReference().child("trips").orderByChild("driverId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    findViewById(R.id.trips).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Trip, TripsViewHolder>(Trip.class, R.layout.drivers_trip, TripsViewHolder.class, query) {
            @Override
            protected void populateViewHolder(final TripsViewHolder viewHolder, final Trip model, int position) {
                if (model.trip_status.equals("pending")) {
                    viewHolder.contactNumber.setText("" + model.booking.phone);
                    viewHolder.customerName.setText(model.booking.name);
                    viewHolder.timings.setText(model.booking.pickup_time);
                } else {
                    viewHolder.cardBaseLayout.setVisibility(View.GONE);
                }

                viewHolder.acceptTrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model.booking != null) {
                            Intent intent = new Intent(TripsActivity.this, MainActivity.class);
                            ArrayList<String> bookingDetails = new ArrayList<String>();
                            bookingDetails.add(model.tripId);
                            bookingDetails.add(model.booking.name);
                            bookingDetails.add(model.booking.drop_point);
                            bookingDetails.add(model.booking.drop_time);
                            bookingDetails.add(model.booking.pickup_point);
                            bookingDetails.add(model.booking.pickup_time);
                            bookingDetails.add(String.valueOf(model.booking.phone));
                            intent.putExtra(ConstantsSharedPreferences.INTENT_EXTRA_BOOKING, bookingDetails);
                            updateTrip(model.tripId, "accepted");
                            startActivity(intent);
                        }
                    }
                });
                viewHolder.rejectTrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.cardBaseLayout.setVisibility(View.GONE);
                        updateTrip(model.tripId,"rejected");
                    }
                });
            }
        };

        trips.setHasFixedSize(true);
        trips.setLayoutManager(new LinearLayoutManager(this));
        trips.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.cleanup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                if (ConnectionManager.getInstance(getApplicationContext()).isDeviceConnectedToInternet()) {
                    Intent intent = new Intent(TripsActivity.this, ActRegistration.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_internet),
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_shift:
                Intent intent = new Intent(TripsActivity.this, ShiftOnOffActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void updateTrip(String tripId, final String value) {
        Query query = FirebaseDatabase.getInstance().getReference().child("trips").orderByChild("tripId").equalTo(tripId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> tripStatus = new HashMap();
                        tripStatus.put("trip_status", value);
                        FirebaseDatabase.getInstance().getReference().child("trips").child(childDataSnapshot.getKey()).updateChildren(tripStatus);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
