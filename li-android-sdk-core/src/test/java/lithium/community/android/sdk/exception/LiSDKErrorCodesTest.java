package lithium.community.android.sdk.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiSDKErrorCodesTest {

    @Test
    public void codesTest(){
        assertEquals(100, LiSDKErrorCodes.NETWORK_ERROR);
        assertEquals(101, LiSDKErrorCodes.JSON_SERIALIZATION_ERROR);
        assertEquals(102, LiSDKErrorCodes.ILLEGAL_ARG_ERROR);
        assertEquals(103, LiSDKErrorCodes.RUNTIME_ERROR);
    }
}
