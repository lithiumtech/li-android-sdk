/*
 * LiImageUtils.java
 * Created on Dec 27, 2016
 *
 * Copyright 2016 Lithium Technologies, Inc.
 * San Francisco, California, U.S.A.  All Rights Reserved.
 *
 * This software is the  confidential and proprietary information
 * of  Lithium  Technologies,  Inc.  ("Confidential Information")
 * You shall not disclose such Confidential Information and shall
 * use  it  only in  accordance  with  the terms of  the  license
 * agreement you entered into with Lithium.
 */

package lithium.community.android.sdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import lithium.community.android.sdk.R;
import lithium.community.android.sdk.manager.LiSDKManager;

/**
 * Created by Lithium Technologies Inc on 6/22/17.
 */

public class LiImageUtils {
    /**
     * Util function for compressing image.
     * @param filePath
     * @param fileName
     * @param {@link Context}
     * @return Absolute Path of compressed image.
     */
    public static File compressImage(String filePath, String fileName, Context context, int reqWidth, int reqHeight, int imageQuality){

        File originalFile = new File(filePath);
        Log.i("Original Image: ", originalFile.length() + "");

        Bitmap compressedBitmap = getCompressedBitmap(filePath, reqWidth, reqHeight);

        String fileDirectory = String.valueOf(android.os.Environment.getExternalStorageDirectory()) +
                File.separator +
                LiSDKManager.getInstance().getLiAppCredentials().getTenantId() +
                "-community" +
                File.separator;
        final File communityRoot = new File(fileDirectory);
        communityRoot.mkdirs();
        File compressedFile = new File(fileDirectory + "/" + fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(compressedFile);
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, out);
            }
            else if (fileName.endsWith(".png")) {
                compressedBitmap.compress(Bitmap.CompressFormat.PNG, imageQuality, out);
            }
            out.close();
        }
        catch(FileNotFoundException ex){
            Log.e("Exception", ex.toString());
            return originalFile;
        } catch (IOException e) {
            Log.e("Exception", e.toString());
        }
        Log.i("compressed Image: ", compressedFile.length() + "");

        return compressedFile;
    }

    public static Bitmap getCompressedBitmap(String filePath, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap compressedBitmap = BitmapFactory.decodeFile(filePath, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        compressedBitmap = BitmapFactory.decodeFile(filePath, options);
        return compressedBitmap;
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
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
