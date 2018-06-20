package com.lithium.community.android.model.post;

public class LiPostLogoutModel extends LiBasePostModel {

    private String deviceId;

    public LiPostLogoutModel(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

}
