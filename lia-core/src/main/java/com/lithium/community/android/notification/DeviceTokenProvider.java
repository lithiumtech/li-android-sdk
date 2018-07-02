package com.lithium.community.android.notification;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.lithium.community.android.utils.LiCoreSDKConstants;

import java.io.IOException;

/**
 * This interface is used by the SDK to get the device token to
 * register the device to receive push notifications from the
 * Community via Firebase Cloud Messaging.
 *
 * @author adityasharat
 * @since 1.3.1
 */
public abstract class DeviceTokenProvider {

    public static final String SCOPE_FCM = "FCM";

    /**
     * Return the {@link FirebaseInstanceId} to be used to generate the device token
     *
     * @return A {@link FirebaseInstanceId}.
     */
    @NonNull
    public abstract FirebaseInstanceId getFirebaseInstanceId();

    /**
     * Return the id of the Authorized Entity which will be used to send community push notifications. This is the
     * {@code sender ID} available in the Cloud Messaging tab of the Firebase console Settings pane. The sender ID
     * is used to identify each sender that can send messages to the client app. It is recommended to use a separate
     * Firebase application for sending community push notifications.
     *
     * @return The sender ID of the firebase application which will be used to send community push notifications
     */
    @NonNull
    public abstract String getAuthorizedEntity();

    /**
     * Return the action authorized for Authorized Entity. The default scope is {@link #SCOPE_FCM}.
     *
     * @return The action authorized by the Authorized Entity. By default it is {@link #SCOPE_FCM}.
     */
    public String getScope() {
        return SCOPE_FCM;
    }

    @WorkerThread
    public String getDeviceToken() {
        try {
            return getFirebaseInstanceId().getToken(getAuthorizedEntity(), getScope());
        } catch (IOException e) {
            Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "Exception in generating device token");
            e.printStackTrace();
            return null;
        }
    }
}
