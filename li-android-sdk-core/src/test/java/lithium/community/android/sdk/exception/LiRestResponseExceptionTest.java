package lithium.community.android.sdk.exception;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by kunal.shrivastava on 12/2/16.
 */

public class LiRestResponseExceptionTest {

    @Test
    public void testLiRestResponseCreationTest() {
        LiRestResponseException liRestResponseException = new LiRestResponseException(500, "error", 300);
        Assert.assertEquals(500, liRestResponseException.getHttpCode());
        Assert.assertEquals(300, liRestResponseException.getLiErrorCode());
        Assert.assertEquals("error", liRestResponseException.getError());
        Gson gson = new Gson();
        System.out.print(gson.toJson(liRestResponseException));
    }

    @Test
    public void testFromJson() {
        String json = "{\"http_code\":500,\"code\":300,\"message\":\"error\",\"data\":{\"code\": 300}}";
        LiRestResponseException liRestResponseException = LiRestResponseException.fromJson(json, new Gson());
        Assert.assertEquals(500, liRestResponseException.getHttpCode());
        Assert.assertEquals("error", liRestResponseException.getError());
        Assert.assertEquals(300, liRestResponseException.getLiErrorCode());
    }

    @Test
    public void testEquals() {
        String json = "{\"http_code\":500,\"code\":300,\"message\":\"error\",\"data\":{\"code\": 300}}";
        LiRestResponseException liRestResponseException = LiRestResponseException.fromJson(json, new Gson());
        LiRestResponseException liRestResponseException2 = new LiRestResponseException(500, "error", 300);
        Assert.assertTrue(liRestResponseException.equals(liRestResponseException2));
    }

    @Test
    public void testEqualsForSameObject() {
        LiRestResponseException liRestResponseException = new LiRestResponseException(500, "error", 300);
        Assert.assertTrue(liRestResponseException.equals(liRestResponseException));
    }

    @Test
    public void testEqualsForNullObject() {
        LiRestResponseException liRestResponseException = new LiRestResponseException(500, "error", 300);
        LiRestResponseException liRestResponseException2 = null;
        Assert.assertFalse(liRestResponseException.equals(liRestResponseException2));
    }

    @Test
    public void testNetworkError() {
        LiRestResponseException liRestResponseException = LiRestResponseException.networkError("network error");
        Assert.assertEquals(503, liRestResponseException.getHttpCode());
        Assert.assertEquals("network error", liRestResponseException.getError());
        Assert.assertEquals(LiSDKErrorCodes.NETWORK_ERROR, liRestResponseException.getLiErrorCode());
    }

    @Test
    public void testJsonSerializationError() {
        LiRestResponseException liRestResponseException = LiRestResponseException.jsonSerializationError("json error");
        Assert.assertEquals(500, liRestResponseException.getHttpCode());
        Assert.assertEquals("json error", liRestResponseException.getError());
        Assert.assertEquals(LiSDKErrorCodes.JSON_SERIALIZATION_ERROR, liRestResponseException.getLiErrorCode());
    }

    @Test
    public void testIllegalArgumentError() {
        LiRestResponseException liRestResponseException = LiRestResponseException.illegalArgumentError("illegal error");
        Assert.assertEquals(500, liRestResponseException.getHttpCode());
        Assert.assertEquals("illegal error", liRestResponseException.getError());
        Assert.assertEquals(LiSDKErrorCodes.ILLEGAL_ARG_ERROR, liRestResponseException.getLiErrorCode());
    }

    @Test
    public void testRuntimeException() {
        LiRestResponseException liRestResponseException = LiRestResponseException.runtimeError("runtime exception");
        Assert.assertEquals(503, liRestResponseException.getHttpCode());
        Assert.assertEquals("runtime exception", liRestResponseException.getError());
        Assert.assertEquals(LiSDKErrorCodes.RUNTIME_ERROR, liRestResponseException.getLiErrorCode());
    }
}
