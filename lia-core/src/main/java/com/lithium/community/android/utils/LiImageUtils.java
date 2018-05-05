/*
 * Copyright 2018 Lithium Technologies Pvt Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lithium.community.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lithium.community.android.manager.LiSDKManager;

/**
 * Created by Lithium Technologies Inc on 6/22/17.
 */

public class LiImageUtils {
    /**
     * Util function for compressing image.
     *
     * @param filePath
     * @param fileName
     * @param context  {@link Context} The Android context.
     * @return Absolute Path of compressed image.
     */
    public static File compressImage(String filePath, String fileName, Context context, int reqWidth, int reqHeight, int imageQuality) {

        File originalFile = new File(filePath);
        Log.i("Original Image: ", originalFile.length() + "");

        Bitmap compressedBitmap = getCompressedBitmap(filePath, reqWidth, reqHeight);

        String fileDirectory = String.valueOf(android.os.Environment.getExternalStorageDirectory()) + File.separator
                + LiSDKManager.getInstance().getCredentials().getTenantId() + "-community" + File.separator;
        final File communityRoot = new File(fileDirectory);
        communityRoot.mkdirs();
        File compressedFile = new File(fileDirectory + "/" + fileName);
        FileOutputStream out;
        try {
            out = new FileOutputStream(compressedFile);
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, out);
            } else if (fileName.endsWith(".png")) {
                compressedBitmap.compress(Bitmap.CompressFormat.PNG, imageQuality, out);
            }
            Log.i("compressed Image: ", compressedFile.length() + "");
            out.close();
        } catch (IOException e) {
            Log.e("Exception", e.toString());
        }
        return compressedFile;
    }

    public static Bitmap getCompressedBitmap(String filePath, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
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
