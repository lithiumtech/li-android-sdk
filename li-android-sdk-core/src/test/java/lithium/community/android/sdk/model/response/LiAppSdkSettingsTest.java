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
    public void getParamsTest(){
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
