package com.tromke.mydrive.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.tromke.mydrive.Constants.Constants;

import java.io.ByteArrayOutputStream;

/**
 * Created by Devrath on 10-09-2016.
 */
public class UtilEncodeDecodeBase64 {

    public static Bitmap scaledBitmap = null;

    /**********************************
     * ConvertIntoBase64Image
     **********************************/
    public static Bitmap decodeBase64ToImage(String mData) throws Exception {
        try {
            byte[] imageByteData = Base64.decode(mData, Base64.DEFAULT);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(imageByteData, 0, imageByteData.length, options);
            options.inSampleSize = calculateInSampleSize(options, Constants.REQ_WIDTH, Constants.REQ_HEIGHT);
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByteData, 0, imageByteData.length, options);
            return imageBitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * ConvertImageIntoBase64
     *
     * @param uriString
     **/
    public static String encodeImageToBase64(String uriString) {

        /*Bitmap bm = BitmapFactory.decodeFile(uriString.substring(7));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        //Log.d("BASE-ENCODED-***",Base64.encodeToString(byteArrayImage, Base64.DEFAULT));
        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);*/
        String encodedPortfolioImg = null;
        try {
            String filePath = uriString;
            if (filePath != null) {
                scaledBitmap = null;
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filePath, options);
                options.inSampleSize = calculateInSampleSize(options, Constants.REQ_WIDTH, Constants.REQ_HEIGHT);
                options.inJustDecodeBounds = false;
                options.inPurgeable = true;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                scaledBitmap = BitmapFactory.decodeFile(filePath.substring(7), options);
                encodedPortfolioImg = getEncoded64ImageStringFromBitmap(scaledBitmap);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return encodedPortfolioImg;

    }


    public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) throws Exception {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] byteFormat = stream.toByteArray();
            // get the base 64 string
            String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
            return imgString;
        } catch (Exception e) {
            throw e;
        }
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


}
