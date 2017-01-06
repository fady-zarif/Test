package com.tromke.mydrive;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.tromke.mydrive.Constants.ConstantsSharedPreferences;
import com.tromke.mydrive.Home.Adapters.NavigationDrawerCustomAdapter;
import com.tromke.mydrive.Models.NavigationDrawerDataModel;
import com.tromke.mydrive.util.ConnectionManager;

import io.hypertrack.lib.common.HyperTrack;
import io.hypertrack.lib.transmitter.model.HTShift;
import io.hypertrack.lib.transmitter.model.HTShiftParams;
import io.hypertrack.lib.transmitter.model.HTShiftParamsBuilder;
import io.hypertrack.lib.transmitter.model.callback.HTShiftStatusCallback;
import io.hypertrack.lib.transmitter.service.HTTransmitterService;

public class ShiftOnOffActivity extends AppCompatActivity implements Switch.OnCheckedChangeListener, TabLayout.OnTabSelectedListener {

    private Boolean isShiftStarted = false;
    private String hyperTrackId;
    private ProgressDialog loadingProgress;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    HTTransmitterService transmitterService;
    HTShiftParamsBuilder htShiftParamsBuilder;
    private String[] mNavigationDrawerTitlwes;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_on_off);

        HyperTrack.setPublishableApiKey("pk_ef36da6cd4f2c4570f1600f587cc0d7edb95bfb5", getApplicationContext());
        HTTransmitterService.initHTTransmitter(getApplicationContext());

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Trips"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        Pager pagerAdapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setOnTabSelectedListener(this);

        isShiftStarted = ParseApplication.getSharedPreferences().getBoolean(ConstantsSharedPreferences.SHIFT_STARTED, false);

        loadingProgress = new ProgressDialog(ShiftOnOffActivity.this,
                ProgressDialog.THEME_HOLO_LIGHT);
        loadingProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingProgress.setTitle(getResources().getString(R.string.app_name));
        loadingProgress.setCancelable(false);
        transmitterService = HTTransmitterService.getInstance(this);
        htShiftParamsBuilder = new HTShiftParamsBuilder();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shift, menu);
        MenuItem toggleService = menu.findItem(R.id.action_status);
        Switch actionStatus = (Switch) toggleService.getActionView();
        if (ParseApplication.getSharedPreferences().getBoolean(ConstantsSharedPreferences.SHIFT_STARTED, false)) {
            actionStatus.setText("Online");
            actionStatus.setChecked(true);
        } else {
            actionStatus.setText("Offline");
            actionStatus.setChecked(false);
        }
        actionStatus.setOnCheckedChangeListener(this);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean checkGPS() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return enabled;
    }

    public void showGpsAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ShiftOnOffActivity.this);
        // set title
        alertDialogBuilder.setTitle("Pola Driver");
        // set dialog message
        alertDialogBuilder
                .setMessage("Enable GPS")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 201);
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

    private void startAndStopShift() {
        if (isShiftStarted == false) {
            loadingProgress.setMessage("Starting shift...");
            loadingProgress.show();

            hyperTrackId = ParseApplication.getSharedPreferences().getString(ConstantsSharedPreferences.HYPERTRACK_ID, ConstantsSharedPreferences.HYPERTRACK_ID);
            HTShiftParams htShiftParams = htShiftParamsBuilder.setDriverID(hyperTrackId).createHTShiftParams();
            transmitterService.startShift(htShiftParams, new HTShiftStatusCallback() {
                @Override
                public void onSuccess(HTShift htShift) {
                    if (loadingProgress != null)
                        loadingProgress.cancel();
                    ParseApplication.getSharedPreferences().edit().putBoolean(ConstantsSharedPreferences.SHIFT_STARTED, true).commit();
                }

                @Override
                public void onError(Exception error) {
                    if (loadingProgress != null)
                        loadingProgress.cancel();
                    Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            });
        } else {
            loadingProgress.setMessage("Stoping shift...");
            loadingProgress.show();
            transmitterService.endShift(new HTShiftStatusCallback() {
                @Override
                public void onSuccess(HTShift htShift) {
                    if (loadingProgress != null)
                        loadingProgress.cancel();
                    ParseApplication.getSharedPreferences().edit().putBoolean(ConstantsSharedPreferences.SHIFT_STARTED, false).commit();
                }

                @Override
                public void onError(Exception error) {
                    if (loadingProgress != null)
                        loadingProgress.cancel();
                    Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            });

        }
        isShiftStarted = !isShiftStarted;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 201:
                startAndStopShift();
                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            buttonView.setText("Online");
        } else {
            buttonView.setText("Offline");
        }
        if (ConnectionManager.getInstance(getApplicationContext()).isDeviceConnectedToInternet()) {
            if (checkGPS()) {
                startAndStopShift();
            } else {
                showGpsAlert();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    void initializeNavigationDrawer() {
        mNavigationDrawerTitlwes = getResources().getStringArray(R.array.nav_drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        NavigationDrawerDataModel[] navDrawerItems = new NavigationDrawerDataModel[3];
        navDrawerItems[0] = new NavigationDrawerDataModel("About");
        navDrawerItems[1] = new NavigationDrawerDataModel("Profile");
        navDrawerItems[2] = new NavigationDrawerDataModel("Log out");

        NavigationDrawerCustomAdapter adapter = new NavigationDrawerCustomAdapter(this, R.layout.navigation_drawer_items, navDrawerItems);
        mDrawerList.setAdapter(adapter);
        //mDrawerList.setOnItemClickListener();
    }

    private class NavigationDrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionLogout:
                if (ConnectionManager.getInstance(getApplicationContext()).isDeviceConnectedToInternet()) {
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(ShiftOnOffActivity.this, ActRegistration.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.no_internet),
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
