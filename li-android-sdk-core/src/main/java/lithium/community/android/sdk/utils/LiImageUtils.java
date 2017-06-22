package lithium.community.android.sdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
    public static File compressImage(String filePath, String fileName, Context context){

        File originalFile = new File(filePath);
        Log.i("Original Image: ", originalFile.length() + "");
        Bitmap original = BitmapFactory.decodeFile(filePath);
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
                original.compress(Bitmap.CompressFormat.JPEG, 40, out);
            }
            else if (fileName.endsWith(".png")) {
                original.compress(Bitmap.CompressFormat.PNG, 40, out);
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
}
