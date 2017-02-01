package lithium.community.android.sdk.rest;

import com.google.gson.JsonObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by kunal.shrivastava on 12/8/16.
 */

public class LiBaseResponseTest {


    private LiBaseResponse liBaseResponse;

    @Before
    public void setUp(){
        liBaseResponse = new LiBaseResponse();
    }

    @Test
    public void testGetData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("test", "test");
        liBaseResponse.setData(jsonObject);
        JsonObject jsonObject1 = liBaseResponse.getData();
        Assert.assertEquals(jsonObject.get("test"), jsonObject1.get("test"));
    }

    @Test
    public void testGetStatus() {
        liBaseResponse.setStatus("success");
        Assert.assertEquals("success", liBaseResponse.getStatus());
    }

    @Test
    public void testGetMessage() {
        liBaseResponse.setMessage("success");
        Assert.assertEquals("success", liBaseResponse.getMessage());
    }

    @Test
    public void testGetHttpCode() {
        liBaseResponse.setHttpCode(100);
        Assert.assertEquals(100, liBaseResponse.getHttpCode());
    }
}
