package lithium.community.android.sdk.auth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by saiteja.tokala on 12/5/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LiSSOAuthResponse.class})
public class LiSSOAuthResponseTest {

    public static final String TEST_JSON_STRING = "testJsonString";
    public static final String TEST_USER_ID = "testUserId";
    public static final String TEST_STATE = "testState";
    public static final String TEST_API_PROXY_HOST = "testApiProxyHost";
    public static final String TEST_AUTH_CODE = "testAuthCode";
    public static final String TEST_TENANT_ID = "testTenantId";
    private LiSSOAuthResponse liSsoAuthResponse;
    @Before
    public void setupRequest() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);
        //systemClock= Mockito.mock(LiSystemClock.class);
    }


    @Test
    public void getJsonStringTest() {
        liSsoAuthResponse = new LiSSOAuthResponse();
        liSsoAuthResponse.setJsonString(TEST_JSON_STRING);
        assertNotEquals(null, liSsoAuthResponse.getJsonString());
        assertEquals(TEST_JSON_STRING, liSsoAuthResponse.getJsonString());
    }

    @Test
    public void getUserIdTest() {
        liSsoAuthResponse = new LiSSOAuthResponse();
        liSsoAuthResponse.setUserId(TEST_USER_ID);
        assertNotEquals(null, liSsoAuthResponse.getUserId());
        assertEquals(TEST_USER_ID, liSsoAuthResponse.getUserId());
    }

    @Test
    public void getStateTest() {
        liSsoAuthResponse = new LiSSOAuthResponse();
        liSsoAuthResponse.setState(TEST_STATE);
        assertNotEquals(null, liSsoAuthResponse.getState());
        assertEquals(TEST_STATE, liSsoAuthResponse.getState());
    }

    @Test
    public void getApiProxyHostTest() {
        liSsoAuthResponse = new LiSSOAuthResponse();
        liSsoAuthResponse.setApiProxyHost(TEST_API_PROXY_HOST);
        assertNotEquals(null, liSsoAuthResponse.getApiProxyHost());
        assertEquals(TEST_API_PROXY_HOST, liSsoAuthResponse.getApiProxyHost());
    }

    @Test
    public void getAuthCodeTest() {
        liSsoAuthResponse = new LiSSOAuthResponse();
        liSsoAuthResponse.setAuthCode(TEST_AUTH_CODE);
        assertNotEquals(null, liSsoAuthResponse.getAuthCode());
        assertEquals(TEST_AUTH_CODE, liSsoAuthResponse.getAuthCode());
    }

    @Test
    public void getTenantIdTest() {
        liSsoAuthResponse = new LiSSOAuthResponse();
        liSsoAuthResponse.setTenantId(TEST_TENANT_ID);
        assertNotEquals(null, liSsoAuthResponse.getTenantId());
        assertEquals(TEST_TENANT_ID, liSsoAuthResponse.getTenantId());
    }



}
