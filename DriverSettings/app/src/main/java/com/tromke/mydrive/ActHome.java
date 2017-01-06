package com.tromke.mydrive;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tromke.mydrive.Constants.Constants;
import com.tromke.mydrive.Home.Adapters.AdptDocNames;
import com.tromke.mydrive.Home.Adapters.Adpt_home;
import com.tromke.mydrive.Home.Interfaces.IntHomeView;
import com.tromke.mydrive.Home.Presenters.PresenterHome;
import com.tromke.mydrive.Models.ResponseData;
import com.tromke.mydrive.Utils.UtilActivitiesNavigation;
import com.tromke.mydrive.Utils.UtilSnackbar;
import com.tromke.mydrive.util.Config;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Devrath on 10-09-2016.
 */
public class ActHome extends AppCompatActivity implements IntHomeView {

    @BindView(R.id.spnDocsId)
    Spinner spnDocsId;

    @BindView(R.id.btnUpload)
    Button btnUpload;

    @BindView(R.id.grid_view)
    RecyclerView grid_view;

    @BindView(R.id.rootView)
    LinearLayout rootView;

    String driverKey;

    PresenterHome presenter;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_home);
        driverKey = getIntent().getStringExtra(Config.DRIVER_KEY);

        //Inject views from butter-knife
        ButterKnife.bind(this);
        //Set up Toolbar
        presenter = new PresenterHome(this, spnDocsId);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (presenter.isAllDocumentsAttached()) {
            menu.getItem(0).setEnabled(true);
        } else {
            menu.getItem(0).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_upload) {
            //Start Upload Process
            presenter.uploadData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void selectImage(String name) {
        if (null != name) {
            System.out.println(name);
            Intent intent = new Intent(this, ActProfileImageSelection.class);
            intent.putExtra(Config.DRIVER_KEY, driverKey);
            intent.putExtra(Config.IMAGE_NAME, Config.getPicFieldName(name));
            startActivityForResult(intent, Constants.INTENT_REQUEST_GET_N_IMAGES);
        }
    }

    @Override
    public void noImageSelected() {
        UtilSnackbar.showSnakbarTypeOne(rootView, getResources().getString(R.string.noImageSelected));
    }

    @Override
    public void setUpDocNames(AdptDocNames adapter) {
        spnDocsId.setAdapter(adapter);

    }

    @Override
    public void setUpRecyclerView(StaggeredGridLayoutManager view) {
        grid_view.setLayoutManager(view);
    }

    @Override
    public void setGridViewDocsAdapter(Adpt_home adapter) {
        grid_view.setAdapter(adapter);
    }

    @Override
    public void displayTheProofNameToBeShown(String mName) {
        UtilSnackbar.showSnakbarTypeOne(rootView, "Please add " + mName);
    }

    @Override
    public void isNewUser(boolean isNewUser, ResponseData mData) {

        if (isNewUser == true) {
            //New User
            //UtilSnackbar.showSnakbarTypeOne(rootView, mData.getErrorMessage());
            // UtilActivitiesNavigation.startActivityWithClassDataWithBackStackClear(ActHome.this, ActMessage.class,mData.getErrorMessage());
        } else if (isNewUser == false) {
            //Existing User
            //UtilSnackbar.showSnakbarTypeOne(rootView, mData.getErrorMessage());
            //UtilActivitiesNavigation.startActivityWithClassDataWithBackStackClear(ActHome.this, ActMessage.class,mData.getErrorMessage());

        }

    }

    @Override
    public void isRegisteredSuccess() {
     UtilActivitiesNavigation.startActivityWithClassDataWithBackStackClear(ActHome.this, ActMessage.class,getResources().getString(R.string.register_successfully));
   }

    @Override
    public void registrationFailed() {
        //Registration Failed
        UtilSnackbar.showSnakbarTypeOne(rootView, getResources().getString(R.string.txt_reg_failure));
    }

    @Override
    public void notOnline() {
        UtilSnackbar.showSnakbarTypeOne(rootView, getResources().getString(R.string.conn_noconnectivity));
    }


    @Override
    protected void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

        if (resuleCode == Activity.RESULT_OK) {
            if (requestCode == Constants.INTENT_REQUEST_GET_N_IMAGES) {
                presenter.getImageUri();
                invalidateOptionsMenu();
            }
        }

    }

    @Override
    public void onBackPressed() {
        handleExitApp();
    }

    private void handleExitApp() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        showSnackbar(getResources().getString(R.string.txt_press_back_to_exit));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar
                .make(rootView, message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        snackbar.show();
    }


    public void deleteDocument(int position) {
        presenter.deleteDocument(position);
        invalidateOptionsMenu();
    }

    public void addDocument(int position) {
        presenter.addDocument(position);
    }


}
