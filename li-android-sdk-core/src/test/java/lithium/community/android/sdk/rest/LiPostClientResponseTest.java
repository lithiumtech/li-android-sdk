package lithium.community.android.sdk.rest;

import junit.framework.Assert;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

/**
 * Created by shoureya.kant on 12/6/16.
 */

public class LiPostClientResponseTest {

    private LiBaseResponse liBaseResponse;
    private LiPostClientResponse liPostClientResponse;

    @Test
    public void getResponseTest() {
        liBaseResponse = PowerMockito.mock(LiBaseResponse.class);
        liPostClientResponse = new LiPostClientResponse(liBaseResponse);
        Assert.assertEquals(liBaseResponse, liPostClientResponse.getResponse());
    }

    @Test
    public void trahsformedResponseTest() {
        liBaseResponse = PowerMockito.mock(LiBaseResponse.class);
        liPostClientResponse = new LiPostClientResponse(liBaseResponse);
        Assert.assertNull(liPostClientResponse.getTransformedResponse());
    }
}
