/*
 * LiUserDeviceData.java
 * Created on Dec 28, 2016
 *
 * Copyright 2016 Lithium Technologies, Inc.
 * San Francisco, California, U.S.A.  All Rights Reserved.
 *
 * This software is the  confidential and proprietary information
 * of  Lithium  Technologies,  Inc.  ("Confidential Information")
 * You shall not disclose such Confidential Information and shall
 * use  it  only in  accordance  with  the terms of  the  license
 * agreement you entered into with Lithium.
 */

package lithium.community.android.sdk.model.post;

import com.google.gson.annotations.SerializedName;

/**
 * Request body for POST call to fetch 'id' corresonding to device id.
 * Created by shoureya.kant on 12/28/16.
 */

public class LiUserDeviceDataModel extends LiBasePostModel {

    private String type;
    @SerializedName("device_id")
    private String deviceId;
    @SerializedName("client_id")
    private String clientId;
    @SerializedName("push_notification_provider")
    private String pushNotificationProvider;
    @SerializedName("application_type")
    private String applicationType;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPushNotificationProvider() {
        return pushNotificationProvider;
    }

    public void setPushNotificationProvider(String pushNotificationProvider) {
        this.pushNotificationProvider = pushNotificationProvider;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }
}
