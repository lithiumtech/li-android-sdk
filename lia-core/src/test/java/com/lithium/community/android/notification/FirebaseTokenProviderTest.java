package com.lithium.community.android.notification;

import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author adityasharat
 */
public class FirebaseTokenProviderTest {

    @Mock
    private FirebaseInstanceId instanceId;

    @Before
    public void before() throws IOException {
        instanceId = Mockito.mock(FirebaseInstanceId.class);
        Mockito.when(instanceId.getToken(Mockito.anyString(), Mockito.anyString())).thenReturn("qwertyuiop");
    }

    @Test
    public void getFirebaseInstanceId() {
        FirebaseTokenProvider provider = new FirebaseTokenProvider() {
            @NonNull
            @Override
            protected FirebaseInstanceId getFirebaseInstanceId() {
                return instanceId;
            }

            @NonNull
            @Override
            protected String getAuthorizedEntity() {
                return "qwertyuiop";
            }
        };

        assertNotNull(provider.getFirebaseInstanceId());
    }

    @Test
    public void getAuthorizedEntity() {
        FirebaseTokenProvider provider = new FirebaseTokenProvider() {
            @NonNull
            @Override
            protected FirebaseInstanceId getFirebaseInstanceId() {
                return instanceId;
            }

            @NonNull
            @Override
            protected String getAuthorizedEntity() {
                return "qwertyuiop";
            }
        };

        assertNotNull(provider.getAuthorizedEntity());
    }

    @Test
    public void getScope() {
        FirebaseTokenProvider provider = new FirebaseTokenProvider() {
            @NonNull
            @Override
            protected FirebaseInstanceId getFirebaseInstanceId() {
                return instanceId;
            }

            @NonNull
            @Override
            protected String getAuthorizedEntity() {
                return "qwertyuiop";
            }
        };

        assertNotNull(provider.getScope());
    }

    @Test
    public void getDeviceToken() throws IOException {
        FirebaseTokenProvider provider = new FirebaseTokenProvider() {
            @NonNull
            @Override
            protected FirebaseInstanceId getFirebaseInstanceId() {
                return instanceId;
            }

            @NonNull
            @Override
            protected String getAuthorizedEntity() {
                return "qwertyuiop";
            }
        };

        assertNotNull(provider.getDeviceToken());
    }

    @Test
    public void deleteDeviceToken() throws IOException {
        FirebaseTokenProvider provider = new FirebaseTokenProvider() {
            @NonNull
            @Override
            protected FirebaseInstanceId getFirebaseInstanceId() {
                return instanceId;
            }

            @NonNull
            @Override
            protected String getAuthorizedEntity() {
                return "qwertyuiop";
            }
        };

        provider.deleteDeviceToken();
    }
}