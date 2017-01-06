package com.tromke.mydrive.Home.Presenters;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;


import com.tromke.mydrive.Constants.Constants;
import com.tromke.mydrive.Constants.ConstantsSharedPreferences;
import com.tromke.mydrive.Constants.RequestCodes;
import com.tromke.mydrive.Models.FinalMasterData;
import com.tromke.mydrive.Models.ItemContentData;
import com.tromke.mydrive.Models.ItemObjects;
import com.tromke.mydrive.Models.ResponseData;
import com.tromke.mydrive.Home.Adapters.AdptDocNames;
import com.tromke.mydrive.Home.Adapters.Adpt_home;
import com.tromke.mydrive.Home.Interfaces.IntHomeView;
import com.tromke.mydrive.Home.Interfaces.InterMasterData;
import com.tromke.mydrive.Home.Validations.ValHome;
import com.tromke.mydrive.ParseApplication;
import com.tromke.mydrive.R;
import com.tromke.mydrive.ActHome;
import com.tromke.mydrive.Utils.UtilEncodeDecodeBase64;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Devrath on 9/5/2016.
 */
public class PresenterHome implements AdapterView.OnItemSelectedListener,InterMasterData {

    private IntHomeView view;
    private StaggeredGridLayoutManager gridLayoutManager;
    private Adpt_home rcAdapter;
    private Activity context;
    List<ItemObjects> mList;
    Spinner spnDocsId;
    TypedArray docNames,docImgs;
    Uri mImageUriLoc=null;
    HashSet<Uri> mMedia;
    int mDocPosition;
    LinkedList<ItemObjects> listViewItems;
    ValHome mValHome;
    List<ItemContentData> mFinalData;
    ProgressDialog pd;
    Activity mActivity;
    FinalMasterData mData;

    public PresenterHome(ActHome view, Spinner spnDocsId) {

        //Set the user view
        this.view=view;
        mActivity=view;
        this.context=view;
        this.spnDocsId=spnDocsId;



        initializePresenter();

    }


    public void initializePresenter(){
        mFinalData=new LinkedList<>();

        //Presentation Logic
        initPresenter();
        //Validation instance initilization
        mValHome=new ValHome();

        setUpProgress();
    }


    public void setUpProgress(){
        pd = new ProgressDialog(mActivity);
        pd.setMessage(context.getResources().getString(R.string.txt_loading));
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position!=0)
        selectImage(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void initPresenter() {
        //Set Spinner Selected values
        setDocImages();
        setDocNames();
    }

    private void setDocSelectors() {
        docImgs =context.getResources().obtainTypedArray(R.array.doc_logos);
        docNames =context.getResources().obtainTypedArray(R.array.doc_names);
    }

    private void setDocNames() {
        //Set Spinner Selected values
        setDocSelectors();
        //Set the listener for spinner
        spnDocsId.setOnItemSelectedListener(this);
        //Set the adapter for the spinner
        AdptDocNames customAdapter=new AdptDocNames(context, docImgs, docNames);
        view.setUpDocNames(customAdapter);
    }

    private void setDocImages() {
        //Set up data for adapter
        setDataForAdapter();
        //staggeredGridviewSetup
        staggeredViewSetup();
    }

    private void setDataForAdapter() {
        mList = getListItemData();
    }

    private void staggeredViewSetup() {
        gridLayoutManager = new StaggeredGridLayoutManager(1, 1);
        view.setUpRecyclerView(gridLayoutManager);

        rcAdapter = new Adpt_home(context, mList);
        view.setGridViewDocsAdapter(rcAdapter);
    }


    private List<ItemObjects> getListItemData(){
        listViewItems = new LinkedList<>();
        Uri path = Uri.parse("android.resource://"+context.getPackageName()+"/"+R.drawable.ic_add_circle_black_36dp);
        listViewItems.add(new ItemObjects(context.getResources().getString(R.string.doc_name_yourpic),path,false));
        listViewItems.add(new ItemObjects(context.getResources().getString(R.string.doc_name_driving_licence), path,false));
        listViewItems.add(new ItemObjects(context.getResources().getString(R.string.doc_name_taxi_pic), path,false));
        listViewItems.add(new ItemObjects(context.getResources().getString(R.string.doc_name_rc_pic), path,false));
        listViewItems.add(new ItemObjects(context.getResources().getString(R.string.doc_name_insurance_pic), path,false));

        return listViewItems;
    }




    private void selectImage(int position) {
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasCameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);

        if (!(hasWriteStoragePermission == PackageManager.PERMISSION_GRANTED && hasCameraPermission == PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCodes.PERMISSION_REQUEST_CODE);
        } else {
            mMedia = new HashSet<Uri>();
            mImageUriLoc = null;
            mDocPosition=position;
            view.selectImage(listViewItems.get(position-1).getName());

        }
    }


    public void getImageUri() {
        boolean canServerCallBeMade = false;
        try {
            if (Constants.CAMERA_FOLDER_PATH!=null ){
                //Call the Server call in Profile fragment
                canServerCallBeMade = true;
                mImageUriLoc=Constants.CAMERA_FOLDER_PATH;
                imageCompression(canServerCallBeMade);
            } else {
                view.noImageSelected();
            }

        } catch (Exception ex) {
            Constants.CAMERA_FOLDER_PATH=null;
            ex.printStackTrace();
            //Image has not captured
            canServerCallBeMade=false;
            imageCompression(canServerCallBeMade);
        }

    }

    public void imageCompression(boolean canServerCallBeMade) {
        if (canServerCallBeMade) {
            if(listViewItems.size()==5){
                if(mDocPosition==1){
                    listViewItems.set(mDocPosition-1, new ItemObjects(context.getResources().getString(R.string.doc_name_yourpic), getImageUriLoc(),true));
                }else if(mDocPosition==2){
                    listViewItems.set(mDocPosition-1, new ItemObjects(context.getResources().getString(R.string.doc_name_driving_licence), getImageUriLoc(),true));
                }else if(mDocPosition==3){
                    listViewItems.set(mDocPosition-1, new ItemObjects(context.getResources().getString(R.string.doc_name_taxi_pic), getImageUriLoc(),true));
                }else if(mDocPosition==4){
                    listViewItems.set(mDocPosition-1, new ItemObjects(context.getResources().getString(R.string.doc_name_rc_pic), getImageUriLoc(),true));
                }else if(mDocPosition==5){
                    listViewItems.set(mDocPosition-1, new ItemObjects(context.getResources().getString(R.string.doc_name_insurance_pic), getImageUriLoc(),true));
                }
            }else{
                if(mDocPosition==1){
                    listViewItems.add(new ItemObjects(context.getResources().getString(R.string.doc_name_yourpic), getImageUriLoc(),true));
                }else if(mDocPosition==2){
                    listViewItems.add(new ItemObjects(context.getResources().getString(R.string.doc_name_driving_licence), getImageUriLoc(),true));
                }else if(mDocPosition==3){
                    listViewItems.add(new ItemObjects(context.getResources().getString(R.string.doc_name_taxi_pic), getImageUriLoc(),true));
                }else if(mDocPosition==4){
                    listViewItems.add(new ItemObjects(context.getResources().getString(R.string.doc_name_rc_pic), getImageUriLoc(),true));
                }else if(mDocPosition==5){
                    listViewItems.add(new ItemObjects(context.getResources().getString(R.string.doc_name_insurance_pic), getImageUriLoc(),true));
                }

            }

            //Refresh GridView
            rcAdapter.notifyDataSetChanged();

            //Reset DocName position in the spinner
            spnDocsId.setSelection(0);

        }
    }

    public Uri getImageUriLoc() {
        //Return the Uri of the image
        return mImageUriLoc;
    }


    public  void deleteDocument(int position) {

        Uri path = Uri.parse("android.resource://"+context.getPackageName()+"/"+R.drawable.ic_add_circle_black_36dp);
        if(position==0){
            listViewItems.set(position, new ItemObjects(context.getResources().getString(R.string.doc_name_yourpic),path,false));
        }else if(position==1){
            listViewItems.set(position, new ItemObjects(context.getResources().getString(R.string.doc_name_driving_licence),path,false));
        }else if(position==2){
            listViewItems.set(position, new ItemObjects(context.getResources().getString(R.string.doc_name_taxi_pic),path,false));
        }else if(position==3){
            listViewItems.set(position, new ItemObjects(context.getResources().getString(R.string.doc_name_rc_pic),path,false));
        }else if(position==4){
            listViewItems.set(position, new ItemObjects(context.getResources().getString(R.string.doc_name_insurance_pic),path,false));
        }

        //Refresh GridView
        rcAdapter.notifyItemRemoved(position);
        rcAdapter.notifyItemRangeChanged(position,mList.size());
    }


    public void uploadData(){

        if(mValHome.isProofNotAdded(listViewItems)){
            //Show Which Address Proof is not added
            String mData=mValHome.whichProofNotPresent(listViewItems);
            if(!mData.equalsIgnoreCase("")){
                //Display the Proof name
                view.displayTheProofNameToBeShown(mData);
            }
        }else{
            //Continue with the flow
            FinalMasterData myData=prepareData();

            if(mValHome.isOnline(mActivity)){
                view.isRegisteredSuccess();
                //comment retrofit code
               /* RetroRegistration mNetworkData=new RetroRegistration(PresenterHome.this,myData);
                pd.show();
                mNetworkData.serverCall();*/
            }else{
                //Show Not online message
                view.notOnline();
            }


        }

    }



    public FinalMasterData prepareData(){

        for(int pos=0;pos<listViewItems.size();pos++){
            ItemObjects mObj=listViewItems.get(pos);
            ItemContentData mFinal=new ItemContentData(mObj.getName()+".png",
                    UtilEncodeDecodeBase64.encodeImageToBase64(mObj.getPhoto().toString()),mObj.getName());
            mFinalData.add(mFinal);
        }


        //commented by amar
        mData=new FinalMasterData(ParseApplication.getSharedPreferences().getString(ConstantsSharedPreferences.STRING_PHONE_NUMBER,null),
                ParseApplication.getSharedPreferences().getString(ConstantsSharedPreferences.STRING_USER_NAME,null),
                "",
                context.getResources().getString(R.string.os_type),
                mFinalData);

        return mData;

    }


    public void addDocument(int position) {
        //Add document on Row click
        selectImage(position+1);
    }

    @Override
    public void isNewUser(boolean isNewUser, ResponseData mData) {
        pd.dismiss();
        view.isNewUser(isNewUser,mData);
    }

    @Override
    public void registrationFailure() {
        pd.dismiss();
        view.registrationFailed();
        initializePresenter();
    }

    @Override
    public void setLocalData(String mTolken) {
        ParseApplication.getSharedPreferences().edit().putString(ConstantsSharedPreferences.STRING_AUTH_TOLKEN,mTolken).commit();
        ParseApplication.getSharedPreferences().edit().putBoolean(ConstantsSharedPreferences.BOOL_IS_USER_REGISTERED,true).commit();
    }

    public boolean isAllDocumentsAttached() {
        return !mValHome.isProofNotAdded(listViewItems);
    }


}
