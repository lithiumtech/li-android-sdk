package com.lithium.community.android.model.post;

import android.support.annotation.Nullable;

public class LiPostLogoutModel extends LiBasePostModel {

    @Nullable
    private final String deviceId;

    public LiPostLogoutModel(@Nullable String deviceId) {
        this.deviceId = deviceId;
    }

    @Nullable
    public String getDeviceId() {
        return deviceId;
    }

}
