/*
 * LiBaseDeleteClientTest.java
 * Created on Dec 27, 2016
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

package lithium.community.android.sdk.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import lithium.community.android.sdk.client.manager.LiAuthManager;
import lithium.community.android.sdk.client.manager.LiClientManager;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.rest.LiRestV2Request;
import lithium.community.android.sdk.rest.LiRestv2Client;

import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Created by shoureya.kant on 12/27/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(LiRestv2Client.class)
public class LiBaseDeleteClientTest {

    private static final String BASE_PATH = "http://localhost/";
    private LiBaseDeleteClient liClient;
    private LiRestv2Client liRestv2Client;

    @Before
    public void setUp() throws LiRestResponseException {
        liRestv2Client = mock(LiRestv2Client.class);
        PowerMockito.mockStatic(LiRestv2Client.class);
        BDDMockito.given(LiRestv2Client.getInstance()).willReturn(liRestv2Client);
        liClient = new LiBaseDeleteClient(BASE_PATH);
        PowerMockito.verifyStatic();
    }

    @Test
    public void testBasePostClientCreation() throws LiRestResponseException {
        Assert.assertEquals(null, liClient.querySettingsType);
        Assert.assertEquals(null, liClient.type);
        Assert.assertEquals(BASE_PATH,liClient.basePath);
        Assert.assertEquals(LiBaseClient.RequestType.DELETE,liClient.requestType);
        Assert.assertEquals(null, liClient.getRequestBody());
    }
}
