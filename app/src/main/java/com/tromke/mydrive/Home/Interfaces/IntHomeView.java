package com.tromke.mydrive.Home.Interfaces;

import android.support.v7.widget.StaggeredGridLayoutManager;

import com.tromke.mydrive.Models.ResponseData;
import com.tromke.mydrive.Home.Adapters.Adpt_home;
import com.tromke.mydrive.Home.Adapters.AdptDocNames;

/**
 * Created by Devrath on 10-09-2016.
 */
public interface IntHomeView {
    void selectImage(String name);
    void noImageSelected();
    void setUpDocNames(AdptDocNames adapter);
    void setUpRecyclerView(StaggeredGridLayoutManager view);
    void setGridViewDocsAdapter(Adpt_home adapter);
    void displayTheProofNameToBeShown(String mName);
    void isNewUser(boolean isNewUser, ResponseData mData);
    void isRegisteredSuccess();
    void registrationFailed();
    void notOnline();
}
