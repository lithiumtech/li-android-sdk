package lithium.community.android.sdk.auth;

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by saiteja.tokala on 12/5/16.
 */

@RunWith(MockitoJUnitRunner.class)
public class LiRefreshTokenRequestTest {

    public static final Uri TEST_URI = Uri.parse("testUri");
    public static final String TEST_CLIENT_ID = "testClientId";
    public static final String TEST_CLIENT_SECRET = "testClientSecret";
    public static final String TEST_GRANT_TYPE = "testGrantType";
    public static final String TEST_REFRESH_TOKEN = "testRefreshToken";
    private LiRefreshTokenRequest liRefreshTokenRequest;


    @Before
    public void setupRequest() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getUriTest() {
        liRefreshTokenRequest = new LiRefreshTokenRequest();
        liRefreshTokenRequest.setUri(TEST_URI);
        assertNotEquals(null, liRefreshTokenRequest.getUri());
        assertEquals(TEST_URI, liRefreshTokenRequest.getUri());
    }

    @Test
    public void getClientIdTest() {
        liRefreshTokenRequest = new LiRefreshTokenRequest();
        liRefreshTokenRequest.setClientId(TEST_CLIENT_ID);
        assertNotEquals(null, liRefreshTokenRequest.getClientId());
        assertEquals(TEST_CLIENT_ID, liRefreshTokenRequest.getClientId());
    }

    @Test
    public void getClientSecretTest() {
        liRefreshTokenRequest = new LiRefreshTokenRequest();
        liRefreshTokenRequest.setClientSecret(TEST_CLIENT_SECRET);
        assertNotEquals(null, liRefreshTokenRequest.getClientSecret());
        assertEquals(TEST_CLIENT_SECRET, liRefreshTokenRequest.getClientSecret());
    }

    @Test
    public void getGrantTypeTest() {
        liRefreshTokenRequest = new LiRefreshTokenRequest();
        liRefreshTokenRequest.setGrantType(TEST_GRANT_TYPE);
        assertNotEquals(null, liRefreshTokenRequest.getGrantType());
        assertEquals(TEST_GRANT_TYPE, liRefreshTokenRequest.getGrantType());
    }

    @Test
    public void getRefreshTokenTest() {
        liRefreshTokenRequest = new LiRefreshTokenRequest();
        liRefreshTokenRequest.setRefreshToken(TEST_REFRESH_TOKEN);
        assertNotEquals(null, liRefreshTokenRequest.getRefreshToken());
        assertEquals(TEST_REFRESH_TOKEN, liRefreshTokenRequest.getRefreshToken());
    }

}
