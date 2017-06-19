/*
 * LiImageUtil.java
 * Created on Jan 24, 2017
 *
 * Copyright 2017 Lithium Technologies, Inc.
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

import lithium.community.android.sdk.manager.LiSDKManager;

/**
 * Utility class to compress image to be uploaded to community. This is to allow
 * images of size more than 1 MB to get uploaded.
 * Created by shoureya.kant on 1/24/17.
 */

public class LiImageUtil {

    public static File compressedImageFile;

    /**
     * Util function for compressing image.
     * @param filePath
     * @param fileName
     * @return Absolute Path of compressed image.
     */
    public static String compressImage(String filePath, String fileName, Context context){

        File originalFile = new File(filePath);
        Log.i("Original Image: ", originalFile.length() + "");
        Bitmap original = BitmapFactory.decodeFile(filePath);
        String fileDirectory = String.valueOf(android.os.Environment.getExternalStorageDirectory()) +
                File.separator +
                LiSDKManager.getInstance().getLiAppCredentials().getTenantId() +
                "-community" +
                File.separator;
        File compressedFile = new File(fileDirectory + "/" + fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(compressedFile);
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                original.compress(Bitmap.CompressFormat.JPEG, 40, out);
            }
            else if (fileName.endsWith(".png")) {
                original.compress(Bitmap.CompressFormat.PNG, 40, out);
            }
        }
        catch(FileNotFoundException ex){
            Log.e("Exception", ex.toString());
            return fileName;
        }
        Log.i("compressed Image: ", compressedFile.length() + "");
        compressedImageFile = compressedFile;
        return compressedFile.getAbsolutePath();
    }
}



