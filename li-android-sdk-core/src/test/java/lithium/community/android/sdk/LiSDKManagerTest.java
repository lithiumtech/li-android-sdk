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

package lithium.community.android.sdk;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import lithium.community.android.sdk.auth.LiAppCredentials;
import lithium.community.android.sdk.manager.LiSDKManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by saiteja.tokala on 9/28/16.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(MockitoJUnitRunner.class)
public class LiSDKManagerTest {


    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Activity mContext;
    private SharedPreferences mMockSharedPreferences;
    private Resources resource;

    @Before
    public void setUpTest() throws Exception {
        MockitoAnnotations.initMocks(this);
        mContext = mock(Activity.class);
        mMockSharedPreferences = mock(SharedPreferences.class);
        resource = mock(Resources.class);
        when(resource.getBoolean(anyInt())).thenReturn(true);
        when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
        when(mMockSharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        when(mContext.getResources()).thenReturn(resource);


    }

    @Test(expected = IllegalArgumentException.class)
    public void testInit_nullContext() throws MalformedURLException, URISyntaxException {
        LiSDKManager.init(null, TestHelper.getTestAppCredentials());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInit_nullSdkInitParams() throws MalformedURLException, URISyntaxException {
        LiSDKManager.init(mContext, null);
    }

    @Test
    public void testInit() throws MalformedURLException, URISyntaxException {
        LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());

    }

    @Test
    public void testGetInstance() throws MalformedURLException, URISyntaxException {
        LiSDKManager liSDKManager = LiSDKManager.init(
                mContext, TestHelper.getTestAppCredentials());
        LiSDKManager instance = liSDKManager.getInstance();
        assertEquals(liSDKManager, instance);
    }

    @Test
    public void testSingletonGetInstance() throws MalformedURLException, URISyntaxException {
        LiSDKManager liSDKManager = LiSDKManager.init(mContext,
                TestHelper.getTestAppCredentials());
        LiSDKManager instance = liSDKManager.getInstance();
        assertEquals(liSDKManager, instance);
        LiSDKManager liClientManger2 = LiSDKManager.init(mContext,
                TestHelper.getTestAppCredentials());
        assertEquals(liClientManger2, instance);
    }

    @Test
    public void testGetAppCredentialsTest() throws MalformedURLException, URISyntaxException {
        LiSDKManager liSDKManager = LiSDKManager.init(mContext,
                TestHelper.getTestAppCredentials());
        LiSDKManager instance = liSDKManager.getInstance();
        LiAppCredentials liAppCredentials = instance.getLiAppCredentials();
        assertEquals(liAppCredentials, instance.getLiAppCredentials());
    }

    @Test
    public void testGetAppCredentialsTestData() throws MalformedURLException, URISyntaxException {
        LiSDKManager liSDKManager = LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
        LiSDKManager instance = liSDKManager.getInstance();
        LiAppCredentials liAppCredentials = instance.getLiAppCredentials();
        assertEquals(liAppCredentials, instance.getLiAppCredentials());
        assertEquals(liAppCredentials.getClientSecret(), instance.getLiAppCredentials().getClientSecret());
    }
}
