package com.lithium.community.android.model.post;

import androidx.annotation.Nullable;

/**
 * A Data model to hold post request object for logout operation
 */
public class LiPostLogoutModel extends LiBasePostModel {

    @Nullable
    private final String deviceId;

    /**
     * Initializes with the registered device-Id, if any.
     *
     * @param deviceId - the device-id registered essentially for push notifications, otherwise null
     */
    public LiPostLogoutModel(@Nullable String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Retrieve the device-ID
     *
     * @return - the device id which was passed in.
     */
    @Nullable
    public String getDeviceId() {
        return deviceId;
    }

}
