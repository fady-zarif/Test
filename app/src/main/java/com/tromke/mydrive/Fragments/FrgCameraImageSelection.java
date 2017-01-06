package com.tromke.mydrive.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tromke.mydrive.ActProfileImageSelection;
import com.tromke.mydrive.Constants.Constants;
import com.tromke.mydrive.Home.Camera.CameraView;
import com.tromke.mydrive.Home.Dialogs.DlgTransparentProgress;
import com.tromke.mydrive.R;
import com.tromke.mydrive.ActHome;
import com.tromke.mydrive.Utils.UtilEncodeDecodeBase64;
import com.tromke.mydrive.Utils.UtilLoaders;
import com.tromke.mydrive.util.Config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Devrath on 11-09-2016.
 */
public class FrgCameraImageSelection extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private Camera mCamera;
    private CameraView maPreview;
    private FrameLayout maLayoutPreview;
    private ImageView btnTakePhoto, btnSwitchCamera, btnCameraFlash;
    private boolean cameraFront = false;
    private boolean isFlashOn = false;
    private byte[] pictureCallBackData;
    private Camera pictureCallBackCamera;
    DlgTransparentProgress pd;


    public static FrgCameraImageSelection newInstance() {
        FrgCameraImageSelection fragment = new FrgCameraImageSelection();
        return fragment;
    }


    public Bitmap resizeImageForImageView(Bitmap bitmap) {
        Bitmap resizedBitmap = null;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        int height = (int) (originalWidth * 0.97f);
        try {
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, originalWidth, height);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return resizedBitmap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.surface_view_camera_fragment, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {
        requestForCameraPermission();
        maLayoutPreview = (FrameLayout) view.findViewById(R.id.camera_preview);
        maPreview = new CameraView(getActivity(), mCamera);
        maLayoutPreview.addView(maPreview);

        btnTakePhoto = (ImageView) view.findViewById(R.id.btnTakePhoto);
        btnSwitchCamera = (ImageView) view.findViewById(R.id.switchCamera);
        btnCameraFlash = (ImageView) view.findViewById(R.id.cameraFlash);
        btnTakePhoto.setOnClickListener(this);
        btnSwitchCamera.setOnClickListener(this);
        btnCameraFlash.setOnClickListener(this);
    }

    public void takePhoto() {
        mCamera.takePicture(null, null, pictureCallback());
    }

    private Camera.PictureCallback pictureCallback() {
        Camera.PictureCallback mPicture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                pictureCallBackData = data;
                pictureCallBackCamera = camera;
                new SaveFileTask().execute();
            }
        };
        return mPicture;
    }

    private String getFilePath(Uri data) {
        String path = "";

        // For non-gallery application
        path = data.getPath();

        // For gallery application
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(data, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            cursor.close();
        }
        return path;
    }

    public Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.setRotate(degree);
        if (android.os.Build.VERSION.SDK_INT > 13 && cameraFront) {
            float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
            mtx = new Matrix();
            Matrix matrixMirrorY = new Matrix();
            matrixMirrorY.setValues(mirrorY);
            mtx.postConcat(matrixMirrorY);
            mtx.preRotate(270);
        }
        if (Build.MODEL.equalsIgnoreCase("Nexus 5x") || Build.MODEL.equalsIgnoreCase("Nexus 6p")) {
            mtx.setRotate(270);
        }
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    private void gotoFeedPostScreen() {
        UtilLoaders.dismissCircularLoadingDialog();
        Intent intent = new Intent(getActivity(), ActHome.class);
        getActivity().setResult(getActivity().RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!cameraFront) {
            btnCameraFlash.setVisibility(View.VISIBLE);
        } else {
            btnCameraFlash.setVisibility(View.GONE);
        }
        if (mCamera == null) {
            mCamera = Camera.open(0);
            maPreview = new CameraView(getActivity(), mCamera);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCamera != null) {
            mCamera.release();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnTakePhoto:
                takePhoto();
                break;
            case R.id.switchCamera:
                btnCameraFlash.setVisibility(View.VISIBLE);
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    //release the old camera instance
                    //switch camera, from the front and the back and vice versa

                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(getActivity(), "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
            case R.id.cameraFlash:
                btnCameraFlash.setImageResource(0);
                if (isFlashOn) {
                    isFlashOn = false;
                    maPreview.turnOffFlash();
                    btnCameraFlash.setImageResource(R.drawable.flash);
                } else {
                    isFlashOn = true;
                    maPreview.turnOnFlash();
                    btnCameraFlash.setImageResource(R.drawable.flash_active);
                }
                break;
        }
    }

    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                pictureCallback();
                maPreview.refreshCamera(mCamera);
                btnSwitchCamera.setImageResource(0);
                btnSwitchCamera.setImageResource(R.drawable.flip_camera);
                btnCameraFlash.setVisibility(View.VISIBLE);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                pictureCallback();
                maPreview.refreshCamera(mCamera);
                btnSwitchCamera.setImageResource(0);
                btnSwitchCamera.setImageResource(R.drawable.flip_camera_active);
                btnCameraFlash.setVisibility(View.GONE);
            }
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }


    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void launchCamera() {
        mCamera = Camera.open(findBackFacingCamera());
    }

    public void requestForCameraPermission() {
        final String permission = Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(getActivity(), permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                showPermissionRationaleDialog("Test", permission);
            } else {
                requestForPermission(permission);
            }
        } else {
            launchCamera();
        }
    }

    private void showPermissionRationaleDialog(final String message, final String permission) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestForPermission(permission);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void requestForPermission(final String permission) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                final int numOfRequest = grantResults.length;
                final boolean isGranted = numOfRequest == 1
                        && PackageManager.PERMISSION_GRANTED == grantResults[numOfRequest - 1];
                if (isGranted) {
                    launchCamera();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class SaveFileTask extends AsyncTask<String, Integer, String> {
        File mediaFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            UtilLoaders.showCircularLoadingDialog(pd, getActivity());
        }

        @Override
        protected String doInBackground(String... path) {
            String fileName = new SimpleDateFormat("yyyyMMddhhmm'.txt'").format(new Date());
            File sampleImg = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName + ".jpg");
            Constants.CAMERA_FOLDER_PATH = Uri.fromFile(new File(Environment.getExternalStorageDirectory()
                    + File.separator + "test.jpg"));
            Bitmap capturedBitmap = null;

            try {

                BitmapFactory.Options opt;

                opt = new BitmapFactory.Options();
                opt.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(pictureCallBackData, 0, pictureCallBackData.length, opt);
                opt.inSampleSize = UtilEncodeDecodeBase64.calculateInSampleSize(opt, Constants.REQ_WIDTH, Constants.REQ_HEIGHT);
                opt.inJustDecodeBounds = false;
                opt.inPurgeable = true;
                opt.inPreferredConfig = Bitmap.Config.RGB_565;
                capturedBitmap = BitmapFactory.decodeByteArray(pictureCallBackData, 0, pictureCallBackData.length, opt);

                FileOutputStream fos = new FileOutputStream(sampleImg);
                ExifInterface ei = new ExifInterface(getFilePath(Uri.fromFile(sampleImg)));
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        capturedBitmap = rotate(capturedBitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        capturedBitmap = rotate(capturedBitmap, 180);
                        break;
                    default:
                        capturedBitmap = rotate(capturedBitmap, 90);
                        break;
                }
                capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();


                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmapOrg = BitmapFactory.decodeFile(sampleImg.getPath(), options);
                Bitmap cropped = resizeImageForImageView(bitmapOrg);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                cropped.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Townsee");
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        return null;
                    }
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
                Constants.CAMERA_FOLDER_PATH = Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg"));
                if (mediaFile.exists()) {
                    mediaFile.delete();
                }
                mediaFile.createNewFile();
                FileOutputStream fo = new FileOutputStream(mediaFile);
                fo.write(bytes.toByteArray());
                fo.close();

            } catch (FileNotFoundException e) {
                Log.d("Info", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("TAG", "Error accessing file: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (mediaFile != null) {
                final ProgressDialog pd = new ProgressDialog(getActivity());
                pd.setMessage("Uploading file...");
                pd.show();
                // DO your work here
                // get the data
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(Config.FIREBASE_STORAGE_URL);
                Uri file = Uri.fromFile(mediaFile);
                StorageReference riversRef = storageRef.child("driver/" + file.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        pd.cancel();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Firebase ref = new Firebase(Config.FIREBASE_URL).child("drivers");
                        String tripKey = ((ActProfileImageSelection) getActivity()).getDriverKey();
                        String imageName = ((ActProfileImageSelection) getActivity()).getImageName();
                        String imagePath = downloadUrl.toString();
                        if (imagePath != null && !imagePath.isEmpty()) {//Throws null pointer exception.
                            ref.child(tripKey).child(imageName).setValue(imagePath, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(final FirebaseError firebaseError, Firebase firebase) {
                                    pd.cancel();
                                    gotoFeedPostScreen();
                                }
                            });
                        }
                    }
                });

            } else {
                gotoFeedPostScreen();
            }
        }
    }
}