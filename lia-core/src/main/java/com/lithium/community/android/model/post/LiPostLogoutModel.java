package com.lithium.community.android.model.post;

public class LiPostLogoutModel extends LiBasePostModel {

    private String clientId;

    private String deviceId;

    public LiPostLogoutModel(String clientId, String deviceId) {
        this.clientId = clientId;
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getClientId() {
        return clientId;
    }
}
