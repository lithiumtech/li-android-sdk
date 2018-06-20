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

package com.lithium.community.android.manager;

import android.content.Context;

import com.lithium.community.android.TestHelper;
import com.lithium.community.android.auth.LiAppCredentials;
import com.lithium.community.android.callback.Callback;
import com.lithium.community.android.exception.LiInitializationException;
import com.lithium.community.android.exception.LiRestResponseException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;

/**
 * @author saiteja.tokala, adityasharat
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(MockitoJUnitRunner.class)
public class LiSDKManagerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Context mContext;

    @Before
    public void setUpTest() throws Exception {
        MockitoAnnotations.initMocks(this);
        mContext = TestHelper.createMockContext();

        Field instance = LiSecuredPrefManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        Field isInitialized = LiSecuredPrefManager.class.getDeclaredField("isInitialized");
        isInitialized.setAccessible(true);
        isInitialized.set(null, new AtomicBoolean(false));

        instance = LiSDKManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        isInitialized = LiSDKManager.class.getDeclaredField("isInitialized");
        isInitialized.setAccessible(true);
        isInitialized.set(null, new AtomicBoolean(false));

        LiSDKManager.initialize(mContext, TestHelper.getTestAppCredentials());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitializeWithNullContext() throws LiInitializationException {
        //noinspection ConstantConditions for test
        LiSDKManager.initialize(null, TestHelper.getTestAppCredentials());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitializeWithNullLiaCredentials() throws LiInitializationException {
        //noinspection ConstantConditions for test
        LiSDKManager.initialize(mContext, null);
    }

    @Test
    public void testInitialize() throws LiInitializationException {
        LiSDKManager.initialize(mContext, TestHelper.getTestAppCredentials());
    }

    @Test
    public void testInitializeOnlyOnce() throws LiInitializationException {
        LiSDKManager.initialize(mContext, TestHelper.getTestAppCredentials());
        LiSDKManager instance = LiSDKManager.getInstance();
        LiSDKManager.initialize(mContext, TestHelper.getTestAppCredentials());
        assertEquals(LiSDKManager.getInstance(), instance);
    }

    @Test
    public void testGetLiaCredentials() throws LiInitializationException {
        LiSDKManager.initialize(mContext, TestHelper.getTestAppCredentials());
        LiSDKManager instance = LiSDKManager.getInstance();
        assert instance != null;
        LiAppCredentials liAppCredentials = instance.getLiAppCredentials();
        assert liAppCredentials != null;
    }

    /*
     * Following are the unit tests which are use deprecated APIs
     */

    @Test(expected = IllegalArgumentException.class)
    public void testInit_nullContext_deprecated() throws URISyntaxException {
        //noinspection ConstantConditions,deprecation
        LiSDKManager.init(null, TestHelper.getTestAppCredentials());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInit_nullSdkInitParams_deprecated() throws URISyntaxException {
        //noinspection ConstantConditions,deprecation for test
        LiSDKManager.init(mContext, null);
    }

    @Test
    public void testInit_deprecated() throws URISyntaxException {
        //noinspection deprecation
        LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
    }

    @Test
    public void testGetInstance_deprecated() throws URISyntaxException {
        //noinspection deprecation
        LiSDKManager liSDKManager = LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
        assert liSDKManager != null;
        //noinspection AccessStaticViaInstance for legacy code
        LiSDKManager instance = liSDKManager.getInstance();
        assertEquals(liSDKManager, instance);
    }

    @Test
    public void testSingletonGetInstance_deprecated() throws URISyntaxException {
        //noinspection deprecation
        LiSDKManager liSDKManager = LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
        assert liSDKManager != null;
        //noinspection AccessStaticViaInstance for legacy code
        LiSDKManager instance = liSDKManager.getInstance();
        assertEquals(liSDKManager, instance);
        //noinspection deprecation
        LiSDKManager liClientManger2 = LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
        assertEquals(liClientManger2, instance);
    }

    @Test
    public void testGetAppCredentialsTest_deprecated() throws URISyntaxException {
        //noinspection deprecation
        LiSDKManager liSDKManager = LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
        assert liSDKManager != null;
        //noinspection AccessStaticViaInstance for legacy code
        LiSDKManager instance = liSDKManager.getInstance();
        assert instance != null;
        LiAppCredentials liAppCredentials = instance.getLiAppCredentials();
        assertEquals(liAppCredentials, instance.getLiAppCredentials());
    }

    @Test
    public void testGetAppCredentialsTestData_deprecated() throws URISyntaxException {
        //noinspection deprecation
        LiSDKManager liSDKManager = LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
        assert liSDKManager != null;
        //noinspection AccessStaticViaInstance for legacy code
        LiSDKManager instance = liSDKManager.getInstance();
        assert instance != null;
        LiAppCredentials liAppCredentials = instance.getLiAppCredentials();
        assertEquals(liAppCredentials, instance.getLiAppCredentials());
        assertEquals(liAppCredentials.getClientSecret(), instance.getLiAppCredentials().getClientSecret());
    }

    @Test
    public void testLogoutRequestCallback(){
        Context context = TestHelper.createMockContext();
        try {
            LiSDKManager.initialize(context, TestHelper.getTestAppCredentials());
            LiSDKManager manager = LiSDKManager.getInstance();
            LiSDKManager.LogoutRequestCallback callback = manager.new LogoutRequestCallback(context, new Callback<Void, Throwable>() {
                @Override
                public void success(Void v) {
                    Assert.assertTrue(true);
                }

                @Override
                public void failure(Throwable t) {
                    Assert.assertNotNull(t);
                }

                @Override
                public void abort(Throwable throwable) {
                    Assert.assertNotNull(throwable);
                }
            });
            Assert.assertNotNull(callback);
            try {
                callback.onSuccess(null, null);
            } catch (LiRestResponseException r) {
                callback.onError(r);
            }
            callback.onError(new LiRestResponseException(400, "Sample error", 400));
        }catch (Exception e){
            e.printStackTrace();
            Assert.assertTrue(true);
        }
    }

}
