package lithium.community.android.sdk.rest;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by shoureya.kant on 12/10/16.
 */

public class LiBaseRestRequestTest {

    private LiBaseRestRequest.RestMethod method;
    private RequestBody requestBody;
    private Map<String, String> additionalHttpHeaders;
    private String path;
    private boolean isAuthenticatedRequest = false;
    private LiBaseRestRequest liBaseRestRequest;

    @Test
    public void getParamsTest(){
        method = LiBaseRestRequest.RestMethod.POST;
        requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return  MediaType.parse("application/json; charset=utf-8");
            }
            @Override
            public void writeTo(BufferedSink sink) throws IOException {

            }
        };
        additionalHttpHeaders = new HashMap<>();
        additionalHttpHeaders.put("test", "test");
        path = "path";
        liBaseRestRequest = new LiBaseRestRequest(method, path, requestBody, additionalHttpHeaders, isAuthenticatedRequest);
        liBaseRestRequest.addQueryParam("param", "param");
        Assert.assertEquals(method, liBaseRestRequest.getMethod());
        Assert.assertEquals(requestBody, liBaseRestRequest.getRequestBody());
        Assert.assertEquals(additionalHttpHeaders, liBaseRestRequest.getAdditionalHttpHeaders());
        Assert.assertEquals(path, liBaseRestRequest.getPath());
        Assert.assertEquals(isAuthenticatedRequest, liBaseRestRequest.isAuthenticatedRequest());
        Assert.assertEquals("param", liBaseRestRequest.getQueryParams().get("param"));
        liBaseRestRequest.addRequestHeader("header", "header");
        Assert.assertEquals("header", liBaseRestRequest.getAdditionalHttpHeaders().get("header"));
        liBaseRestRequest.setPath("path1");
        Assert.assertEquals("path1", liBaseRestRequest.getPath());
    }
}
