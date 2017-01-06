package com.tromke.mydrive;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tromke.mydrive.util.Config;

import java.util.HashMap;
import java.util.Map;

import io.hypertrack.lib.transmitter.model.HTTrip;
import io.hypertrack.lib.transmitter.model.HTTripParams;
import io.hypertrack.lib.transmitter.model.HTTripParamsBuilder;
import io.hypertrack.lib.transmitter.model.callback.HTTripStatusCallback;
import io.hypertrack.lib.transmitter.service.HTTransmitterService;

/**
 * Created by satyam on 23/07/15.
 */
public class ParseLogger {
    private static Context mContext;
    private long tripID;
    private int startStopIndicator = 0;
    private Location mLastLocation;
    private static ParseLogger logger = null;
    private ParsetripIDCallback actionListener;
    String tripObjectId;
    private String hyperTrackTripId;

    protected ParseLogger() {

    }

    public interface ParsetripIDCallback {
        public void succsess(int id);

        public void fail();
    }

    public void settripIdListner(ParsetripIDCallback listener) {
        actionListener = listener;
    }


    public static ParseLogger getInstance(Context ctx) {
        mContext = ctx;
        if (logger == null) {
            logger = new ParseLogger();
        }

        return logger;
    }

    public void createNewTripID() {
        tripID = System.currentTimeMillis();
    }

    public void setLastLocation(Location lastLocation, String tripId) {
        mLastLocation = lastLocation;
        if (tripId != null) {
            startStopIndicator = 1;
        }
        if (startStopIndicator == 1) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String locationString = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
            Firebase ref = new Firebase(Config.FIREBASE_URL).child("Location");
            Map<String, Object> locationUpdates = new HashMap<String, Object>();
            locationUpdates.put("driverId", userId);
            locationUpdates.put("location", locationString);
            if (tripId != null) {
                locationUpdates.put("trip", tripId);
            } else {
                locationUpdates.put("trip", tripObjectId);
            }
            locationUpdates.put("timestamp", ServerValue.TIMESTAMP);
            Map<String, Object> locationObject = new HashMap<String, Object>();
            final String locationKey = ref.push().getKey();
            locationObject.put(locationKey, locationUpdates);
            ref.updateChildren(locationObject, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        Toast.makeText(mContext, mContext.getString(R.string.location_update_failed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.location_updated_message),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }

    public void startTracking(Location location, final String hyperTrackId, final String tripId) {
        mLastLocation = location;
        startStopIndicator = 1;
        final String locationString = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();

        HTTripParamsBuilder htTripParamsBuilder = new HTTripParamsBuilder();
        HTTripParams htTripParams = htTripParamsBuilder.setDriverID(hyperTrackId).createHTTripParams();

        HTTransmitterService transmitterService = HTTransmitterService.getInstance(mContext);
        transmitterService.startTrip(htTripParams, new HTTripStatusCallback() {
            @Override
            public void onError(Exception error) {
                actionListener.fail();
            }

            @Override
            public void onSuccess(boolean isOffline, HTTrip trip) {
                hyperTrackTripId = trip.getId();
                Query ref = FirebaseDatabase.getInstance().getReference().child("trips").orderByChild("tripId").equalTo(tripId);
                final Map<String, Object> updates = new HashMap<String, Object>();
                updates.put("startLocation", locationString);
                updates.put("tripStartTime", ServerValue.TIMESTAMP);
                updates.put("hyperTrackTripId", hyperTrackTripId);
                updates.put("endLocation", "");
                updates.put("trip_status", "started");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot childDataSnapShot : dataSnapshot.getChildren()) {
                                if (childDataSnapShot.exists()) {
                                    String tripHash = childDataSnapShot.getKey();
                                    FirebaseDatabase.getInstance().getReference().child("trips").child(tripHash).updateChildren(updates, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError != null) {
                                                actionListener.fail();
                                            } else {
                                                actionListener.succsess(1);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public void stopTracking(Location location, final String tripId, String hyperTrackID, String htTripId) {
        mLastLocation = location;
        if (hyperTrackID != null) {
            HTTransmitterService transmitterService = HTTransmitterService.getInstance(mContext);
            String hyperTrackTripID = "";
            if (hyperTrackTripId != null && !hyperTrackTripId.trim().isEmpty()) {
                hyperTrackTripID = hyperTrackTripId;
            } else {
                hyperTrackTripID = htTripId;
            }
            transmitterService.endTrip(hyperTrackTripID, new HTTripStatusCallback() {

                @Override
                public void onError(Exception e) {
                    actionListener.fail();
                }

                @Override
                public void onSuccess(boolean b, HTTrip htTrip) {
                    String locationString = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
                    Query ref = FirebaseDatabase.getInstance().getReference().child("trips").orderByChild("tripId").equalTo(tripId);
                    final Map<String, Object> updates = new HashMap<String, Object>();
                    updates.put("endLocation", locationString);
                    updates.put("tripEndTime", ServerValue.TIMESTAMP);
                    updates.put("trip_status", "completed");

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot childDataSnapShot : dataSnapshot.getChildren()) {
                                    if (childDataSnapShot.exists()) {
                                        String tripHash = childDataSnapShot.getKey();
                                        FirebaseDatabase.getInstance().getReference().child("trips").child(tripHash).updateChildren(updates, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if (databaseError != null) {
                                                    actionListener.fail();
                                                } else {
                                                    actionListener.succsess(2);
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
            startStopIndicator = 0;
        }
    }
}
