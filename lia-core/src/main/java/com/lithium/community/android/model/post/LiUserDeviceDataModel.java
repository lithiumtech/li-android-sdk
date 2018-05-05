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

package com.lithium.community.android.model.post;

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
