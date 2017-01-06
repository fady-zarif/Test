package com.tromke.mydrive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.tromke.mydrive.Constants.ConstantsSharedPreferences;
import com.tromke.mydrive.util.Config;
import com.tromke.mydrive.util.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import io.hypertrack.lib.common.HyperTrack;
import io.hypertrack.lib.transmitter.service.HTTransmitterService;

/**
 * Using location settings.
 * <p>
 * Uses the {@link com.google.android.gms.location.SettingsApi} to ensure that the device's system
 * settings are properly configured for the app's location needs. When making a request to
 * Location services, the device's system settings may be in a state that prevents the app from
 * obtaining the location data that it needs. For example, GPS or Wi-Fi scanning may be switched
 * off. The {@code SettingsApi} makes it possible to determine if a device's system settings are
 * adequate for the location request, and to optionally invoke a dialog that allows the user to
 * enable the necessary settings.
 * <p>
 * This sample allows the user to request location updates using the ACCESS_FINE_LOCATION setting
 * (as specified in AndroidManifest.xml). The sample requires that the device has location enabled
 * and set to the "High accuracy" mode. If location is not enabled, or if the location mode does
 * not permit high accuracy determination of location, the activity uses the {@code SettingsApi}
 * to invoke a dialog without requiring the developer to understand which settings are needed for
 * different Location requirements.
 */
public class TripDetailsActivity extends AppCompatActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult>, ParseLogger.ParsetripIDCallback {

    private Button startStopButton;
    private Boolean isTracking = false;
    private Boolean isTripID = false;
    protected static final String TAG = "TRMDrive";
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private String hyperTrackTripId;
    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 20000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            15000;

    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    Boolean isfrmActivityresult = false;
    Pubnub pubnub;
    private String routeObjectID;
    String tripId = null;
    ProgressDialog progreeDialog;
    ProgressDialog tripStatus;
    String hyperTrackID;
    ArrayList<String> intentExtras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        pubnub = new Pubnub("pub-calendar-3ac0cb29-18a9-4c52-bd61-aad190231691", "sub-calendar-b7f40b50-4300-11e5-a7a9-02ee2ddab7fe");

        //initialize.
        HyperTrack.setPublishableApiKey("pk_ef36da6cd4f2c4570f1600f587cc0d7edb95bfb5", getApplicationContext());
        HTTransmitterService.initHTTransmitter(getApplicationContext());
        hyperTrackID = ParseApplication.getSharedPreferences().getString(ConstantsSharedPreferences.HYPERTRACK_ID, "");

        intentExtras = getIntent().getStringArrayListExtra(ConstantsSharedPreferences.INTENT_EXTRA_BOOKING);
        initailizeTextViews(intentExtras);

        tripId = intentExtras.get(0);

        progreeDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
        progreeDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreeDialog.setTitle(getResources().getText(R.string.please_wait));
        progreeDialog.setMessage("Getting customer information..");
        progreeDialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 10);
                progreeDialog.dismiss();
            }
        }, 5000);

        Firebase.setAndroidContext(this);
        getEndLocation();

        tripStatus = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
        tripStatus.setTitle(getResources().getText(R.string.app_name));
        tripStatus.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        startStopButton = (Button) findViewById(R.id.startstop);

        startStopButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if (ConnectionManager.getInstance(getApplicationContext()).isDeviceConnectedToInternet()) {
                    if (checkGPS()) {
                        if (isTracking) {
                            tripStatus.setMessage(getResources().getText(R.string.stop_trip));
                            tripStatus.show();
                            ParseLogger.getInstance(getApplicationContext()).settripIdListner(TripDetailsActivity.this);
                            ParseLogger.getInstance(getApplicationContext()).stopTracking(mCurrentLocation, tripId, hyperTrackID, hyperTrackTripId);
                        } else {
                            if (mCurrentLocation != null) {
                                tripStatus.setMessage(getResources().getText(R.string.start_trip));
                                tripStatus.show();
                                ParseLogger.getInstance(getApplicationContext()).settripIdListner(TripDetailsActivity.this);
                                ParseLogger.getInstance(getApplicationContext()).startTracking(mCurrentLocation ,hyperTrackID,tripId);
                            }
                        }

                    } else {
                        showGpsAlert();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building the GoogleApiClient, LocationRequest, and
        // LocationSettingsRequest objects.

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(TripDetailsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateUI();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link LocationSettingsRequest.Builder} to build
     * a {@link LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(TripDetailsActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case 101:
                isfrmActivityresult = true;
                startLocationUpdates();
                break;
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
                setButtonsEnabledState();
            }
        });

    }

    /**
     * Updates all UI fields.
     */
    private void updateUI() {
        setButtonsEnabledState();
    }

    /**
     * Disables both buttons when functionality is disabled due to insuffucient location settings.
     * Otherwise ensures that only one button is enabled at any time. The Start Updates button is
     * enabled if the user is not requesting location updates. The Stop Updates button is enabled
     * if the user is requesting location updates.
     */
    private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
//            mStartUpdatesButton.setEnabled(false);
//            mStopUpdatesButton.setEnabled(true);
        } else {
//            mStartUpdatesButton.setEnabled(true);
//            mStopUpdatesButton.setEnabled(false);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
                setButtonsEnabledState();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
//            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // stopLocationUpdates();
        mGoogleApiClient.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (isfrmActivityresult) {
            isfrmActivityresult = false;
            if (isTracking) {
                ParseLogger.getInstance(getApplicationContext()).settripIdListner(TripDetailsActivity.this);
                ParseLogger.getInstance(getApplicationContext()).stopTracking(mCurrentLocation, tripId, hyperTrackID, hyperTrackTripId);
            } else {
                ParseLogger.getInstance(getApplicationContext()).settripIdListner(TripDetailsActivity.this);
                ParseLogger.getInstance(getApplicationContext()).startTracking(mCurrentLocation, tripId, hyperTrackID);
            }
        }
        if (ConnectionManager.getInstance(getApplicationContext()).isDeviceConnectedToInternet()) {
            if (isTripID) {
                ParseLogger.getInstance(getApplicationContext()).setLastLocation(mCurrentLocation, tripId);
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                Callback callback = new Callback() {
                    public void successCallback(String channel, Object response) {
                        System.out.println(response.toString());
                    }

                    public void errorCallback(String channel, PubnubError error) {
                        System.out.println(error.toString());
                    }
                };
                JSONObject obj = new JSONObject();
                try {
                    obj.put("lat", mCurrentLocation.getLatitude());
                    obj.put("lng", mCurrentLocation.getLongitude());
                    obj.put("alt", mCurrentLocation.getAltitude());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (routeObjectID == null) {
                    routeObjectID = auth.getCurrentUser().getUid();
                }
                if (routeObjectID != null)
                    pubnub.publish(routeObjectID, obj, callback);
            }

        } else {
            Toast.makeText(this, getResources().getString(R.string.no_internet),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }


    public boolean checkGPS() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return enabled;
    }

    public void showGpsAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                TripDetailsActivity.this);

        // set title
        alertDialogBuilder.setTitle(getResources().getText(R.string.app_name));

        // set dialog message
        alertDialogBuilder
                .setMessage("Enable GPS")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(i, 101);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void succsess(int id) {
        if (id == 1) {
            isTripID = true;
            isTracking = true;
            startStopButton.setText("Stop Trip");
        } else {
            isTripID = false;
            isTracking = false;
            startStopButton.setText("Start Trip");
            Intent intent = new Intent(TripDetailsActivity.this, ShiftOnOffActivity.class);
            startActivity(intent);
            finish();
        }
        if (tripStatus != null  && tripStatus.isShowing()) {
            tripStatus.cancel();
        }

    }

    @Override
    public void fail() {
        Toast.makeText(getApplicationContext(), "Trip not started, try again", Toast.LENGTH_SHORT).show();
    }

    public void getEndLocation() {
        Firebase ref = new Firebase(Config.FIREBASE_URL).child("trips");
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final String userId = user.getUid();
        Query queryRef = ref.orderByChild("driverId").equalTo(userId);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChild) {
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                String driverId = String.valueOf(value.get("driverId"));
                String endLocation = String.valueOf(value.get("endLocation"));
                if (driverId.equals(userId.toString()) && (endLocation == "" || endLocation == null)) {
                    routeObjectID = driverId;
                    isTripID = true;
                    isTracking = true;
                    startStopButton.setText("Stop Trip");
                    startStopButton.setBackgroundColor(getResources().getColor(R.color.active_color));
                    hyperTrackTripId = String.valueOf(value.get("hyperTrackTripId"));
                }
                if (tripStatus != null && tripStatus.isShowing()) {
                    tripStatus.dismiss();
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

            }
        });
    }

    void initailizeTextViews(ArrayList<String> values) {
        TextView customerName = (TextView) findViewById(R.id.customer_name);
        TextView contactNumber = (TextView) findViewById(R.id.contact_number);
        TextView pickUpPoint = (TextView) findViewById(R.id.pick_up_point);
        TextView dropPoint = (TextView) findViewById(R.id.drop_point);
        TextView pickUpTime = (TextView) findViewById(R.id.pick_up_time);

        customerName.setText(values.get(1));
        dropPoint.setText(values.get(2));
        pickUpPoint.setText(values.get(3));
        pickUpTime.setText(values.get(4));
        contactNumber.setText(values.get(5));

    }

}