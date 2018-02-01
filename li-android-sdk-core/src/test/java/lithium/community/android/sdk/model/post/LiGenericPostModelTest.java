package lithium.community.android.sdk.model.post;

import com.google.gson.JsonObject;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by shoureya.kant on 12/21/16.
 */

public class LiGenericPostModelTest {

    private JsonObject data;

    private LiGenericPostModel liGenericPostModel;

    @Test
    public void testParams() {
        data = new JsonObject();
        data.addProperty("test", "test value");
        liGenericPostModel = new LiGenericPostModel();
        liGenericPostModel.setData(data);
        Assert.assertEquals("test value", liGenericPostModel.getData().getAsJsonObject().get("test").getAsString());
    }

}
