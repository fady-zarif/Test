package com.tromke.mydrive.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tromke.mydrive.Constants.ConstantsSharedPreferences;
import com.tromke.mydrive.TripDetailsActivity;
import com.tromke.mydrive.Models.Trip;
import com.tromke.mydrive.R;
import com.tromke.mydrive.TripsViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripsFragment extends Fragment {

    FirebaseRecyclerAdapter<Trip, TripsViewHolder> firebaseRecyclerAdapter;
    com.google.firebase.database.Query query;

    public TripsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        query = FirebaseDatabase.getInstance().getReference().child("trips").orderByChild("driverId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    getActivity().findViewById(R.id.trips).setVisibility(View.VISIBLE);
                } else {
                    getActivity().findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trips, container, false);
        RecyclerView trips = (RecyclerView) view.findViewById(R.id.trips);
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Trip, TripsViewHolder>(Trip.class, R.layout.drivers_trip, TripsViewHolder.class, query) {
            @Override
            protected void populateViewHolder(final TripsViewHolder viewHolder, final Trip model, int position) {
                if (!model.trip_status.equals("completed")) {
                    viewHolder.contactNumber.setText("" + model.booking.phone);
                    viewHolder.customerName.setText(model.booking.name);
                    viewHolder.timings.setText(model.booking.pick_time);
                    viewHolder.tripStatus.setText("Trip " + model.trip_status);
                    if (model.trip_status.equals("accepted")) {
                        viewHolder.rejectTrip.setVisibility(View.GONE);
                        viewHolder.acceptTrip.setText("Trip Details");
                    }
                } else {
                    viewHolder.cardBaseLayout.setVisibility(View.GONE);
                }

                viewHolder.acceptTrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model.booking != null) {
                            Intent intent = new Intent(getActivity(), TripDetailsActivity.class);
                            ArrayList<String> bookingDetails = new ArrayList<String>();
                            bookingDetails.add(model.tripId);
                            bookingDetails.add(model.booking.name);
                            bookingDetails.add(model.booking.drop_address);
                            bookingDetails.add(model.booking.pickup_address);
                            bookingDetails.add(model.booking.pick_time);
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
                        updateTrip(model.tripId, "rejected");
                    }
                });
            }
        };

        trips.setHasFixedSize(true);
        trips.setLayoutManager(new LinearLayoutManager(getActivity()));
        trips.setAdapter(firebaseRecyclerAdapter);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.cleanup();
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
