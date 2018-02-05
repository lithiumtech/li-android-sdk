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

package lithium.community.android.sdk.model.response;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiAppSdkSettingsTest {

    private String type = "AppSettings";
    private String id = "100";
    private String additionalInformation = "additional info";
    private String appType = "appType";
    private String clientId = "12345";

    private LiAppSdkSettings liAppSdkSettings = new LiAppSdkSettings();

    @Test
    public void getParamsTest() {
        liAppSdkSettings.setType(type);
        liAppSdkSettings.setId(id);
        liAppSdkSettings.setAdditionalInformation(additionalInformation);
        liAppSdkSettings.setAppType(appType);
        liAppSdkSettings.setClientId(clientId);
        assertEquals(id, liAppSdkSettings.getId());
        assertEquals(type, liAppSdkSettings.getType());
        assertEquals(additionalInformation, liAppSdkSettings.getAdditionalInformation());
        assertEquals(appType, liAppSdkSettings.getAppType());
        assertEquals(clientId, liAppSdkSettings.getClientId());
    }
}
