package com.lithium.community.android.model.post;

import junit.framework.Assert;

import org.junit.Test;

public class LiPostLogoutModelTest {

    @Test
    public void testModel(){
        String clientId = "client-id-asdewr23";
        String deviceId = "device-id-13413233";
        LiPostLogoutModel model = new LiPostLogoutModel(clientId, deviceId);
        Assert.assertNotNull(model);
        Assert.assertNotNull(model.getClientId());
        Assert.assertNotNull(model.getDeviceId());
        Assert.assertEquals(clientId, model.getClientId());
        Assert.assertEquals(deviceId, model.getDeviceId());
    }
}
