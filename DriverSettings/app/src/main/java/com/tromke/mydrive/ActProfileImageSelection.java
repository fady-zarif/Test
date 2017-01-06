package com.tromke.mydrive;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.astuetz.PagerSlidingTabStrip;
import com.tromke.mydrive.Home.Adapters.AdptImageSelection;
import com.tromke.mydrive.util.Config;

/**
 * Created by Devrath on 11-09-2016.
 */
public class ActProfileImageSelection extends AppCompatActivity {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private AdptImageSelection adapter;
    String driverKey;
    String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_profile_image_selection);
        driverKey = getIntent().getStringExtra(Config.DRIVER_KEY);
        imageName = getIntent().getStringExtra(Config.IMAGE_NAME);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new AdptImageSelection(getSupportFragmentManager());

        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public String getDriverKey() {
        return driverKey;
    }

    public String getImageName() {
        return imageName;
    }
}