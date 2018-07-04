package com.lithium.community.android.example;

import android.support.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.lithium.community.android.notification.FirebaseTokenProvider;

/**
 * This is a reference implementation for {@link FirebaseTokenProvider} which can be used for registering
 * the device to receive push notification from the community.
 *
 * @author adityasharat
 * @see MainApplication for usage
 */
public class ReferenceFirebaseTokenProvider extends FirebaseTokenProvider {

    private final String senderId;

    public ReferenceFirebaseTokenProvider(String senderId) {
        this.senderId = senderId;
    }

    @NonNull
    @Override
    protected FirebaseInstanceId getFirebaseInstanceId() {
        return FirebaseInstanceId.getInstance();
    }

    @NonNull
    @Override
    protected String getAuthorizedEntity() {
        return senderId;
    }
}
