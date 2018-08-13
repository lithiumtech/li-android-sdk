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

package com.lithium.community.android.ui.components.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.lithium.community.android.model.LiBaseModelImpl;
import com.lithium.community.android.model.response.LiMessage;
import com.lithium.community.android.ui.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by sumit.pannalall on 10/12/16.
 * This class consists of utilities that will be used by the SDK UI components
 */

public class LiUIUtils {

    /**
     * This method is used to show notification to the user in the form of a Toast.
     *
     * @param activity
     * @param messageResId this is the message resource ID (generally present in the strings.xml resource of android)
     *                     which contains the actual message that needs to shown on the notification
     */
    public static void showInAppNotification(final Activity activity, final int messageResId) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, messageResId, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * This method checks if internet is available when the app is running
     *
     * @param context android application context
     * @return return true or false based upon internet availability
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = null;
            if (cm != null) {
                netInfo = cm.getActiveNetworkInfo();
            }
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        }
        return false;
    }

    /**
     * This method returns a string for the time difference in simple english for e.g. Just now, 10 minutes ago
     *
     * @param postTime
     * @return String
     */
    public static String toDuration(Context context, long postTime) {
        long now = System.currentTimeMillis();
        long duration = now - postTime;
        List<Long> times = Arrays.asList(
                TimeUnit.DAYS.toMillis(365),
                TimeUnit.DAYS.toMillis(30),
                TimeUnit.DAYS.toMillis(1),
                TimeUnit.HOURS.toMillis(1),
                TimeUnit.MINUTES.toMillis(1),
                TimeUnit.SECONDS.toMillis(1));
        List<String> timesString =
                Arrays.asList(context.getResources().getStringArray(R.array.li_time));

        StringBuilder res = new StringBuilder();
        for (int i = 0; i < times.size(); i++) {
            Long current = times.get(i);
            long temp = duration / current;
            if (temp > 0) {
                res.append(temp).append(" ").append(timesString.get(i)).append(temp != 1
                        ? context.getResources().getString(R.string.li_pural) : "")
                        .append(" ")
                        .append(context.getString(R.string.li_ago));
                break;
            }
        }
        if ("".equals(res.toString())) {
            return context.getResources().getString(R.string.li_justNow);
        } else {
            return res.toString();
        }
    }

    /**
     * Clones the given LiMessage and returns a new instance.
     *
     * @param item @{@link LiMessage}
     * @return cloned @{@link LiMessage}
     */
    public static LiMessage clone(LiMessage item) {
        LiMessage newLiMessage = new LiMessage();
        newLiMessage.setId(item.getId());
        newLiMessage.setIsAcceptedSolution(item.isAcceptedSolution());
        newLiMessage.setAuthor(item.getAuthor());
        newLiMessage.setBoard(item.getBoard());
        newLiMessage.setBody(item.getBody());
        newLiMessage.setSubject(item.getSubject());
        newLiMessage.setKudoMetrics(item.getKudoMetrics());
        newLiMessage.setPostTime(item.getPostTime());
        newLiMessage.setLiConversation(item.getLiConversation());
        newLiMessage.setUserContext(item.getUserContext());
        return newLiMessage;
    }

    /**
     * Computes default json as string from raw folder.
     *
     * @param activity
     * @param rawResId
     * @return String is the raw file.
     */
    public static String getRawString(Activity activity, int rawResId) {
        String defaultJsonString;
        InputStream inputStream = activity.getResources().openRawResource(rawResId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int readPointer;
        try {
            if (inputStream != null) {
                readPointer = inputStream.read();
                while (readPointer != -1) {
                    byteArrayOutputStream.write(readPointer);
                    readPointer = inputStream.read();
                }
                inputStream.close();
            }
        } catch (IOException e) {
            Log.e(LiSDKConstants.GENERIC_LOG_TAG, "Could not load raw file");
        }
        defaultJsonString = byteArrayOutputStream.toString();
        return defaultJsonString;
    }

    public static void showMarkReadSuccessful(final Activity activity, final LiMessage item) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (item.getUserContext().getRead()) {
                    Toast.makeText(activity, R.string.li_mark_read_successful,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, R.string.li_mark_unread_successful,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void showMarkReadUnSuccessful(final Activity activity, final LiMessage item) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (item.getUserContext().getRead()) {
                    Toast.makeText(activity, R.string.li_mark_unread_unsuccessful,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, R.string.li_mark_read_unsuccessful,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void updateMarkReadPopmenuTitle(PopupMenu popup, LiMessage item) {
        MenuItem menuItem = popup.getMenu().findItem(R.id.li_action_mark_read);
        if (item.getUserContext() != null) {
            if (item.getUserContext() != null && item.getUserContext().getRead()) {
                menuItem.setTitle(R.string.li_action_mark_unread);
            } else {
                menuItem.setTitle(R.string.li_action_mark_read);
            }
        }
    }

}
