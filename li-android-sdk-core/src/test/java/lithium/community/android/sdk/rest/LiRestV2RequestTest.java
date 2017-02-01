package lithium.community.android.sdk.rest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import lithium.community.android.sdk.rest.LiBaseRestRequest;
import lithium.community.android.sdk.rest.LiRestV2Request;
import okhttp3.RequestBody;

/*
   Created by mahaveer.udabal on 10/18/16.
 */

public class LiRestV2RequestTest {

    private  Map<String, String> mParams;
    private final String LI_QL="Select * from Customers";
    private final String TYPE="getRequest";
    private final String requestBody = "request";

    @Before
    public void setUp(){
        mParams= new HashMap<String,String>();
        mParams.put("Q",LI_QL);
    }

    @Test
    public void liRestV2RequestWithAdditionalHeadersTest(){
        LiRestV2Request liRestV2Request = new LiRestV2Request(LI_QL,TYPE,mParams,true);
        assertEquals(TYPE,liRestV2Request.getType());
    }

    @Test
    public void liRestV2RequestNullTest(){
        LiRestV2Request liRestV2Request = new LiRestV2Request(null,null,null,false);
        assertEquals(null,liRestV2Request.getType());
    }

    @Test
    public void liRestV2RequestWithOutAdditionalHeadersTest(){
        LiRestV2Request liRestV2Request = new LiRestV2Request(LI_QL,TYPE);
        assertEquals(TYPE,liRestV2Request.getType());
    }

    @Test
    public void deleteRequestTest(){
        LiRestV2Request liRestV2Request = new LiRestV2Request();
        assertEquals(null,liRestV2Request.getType());
    }

    @Test
    public void postRequestTest(){
        LiRestV2Request liRestV2Request = new LiRestV2Request(LiBaseRestRequest.RestMethod.POST, requestBody);
        assertEquals(null,liRestV2Request.getType());
    }
}
