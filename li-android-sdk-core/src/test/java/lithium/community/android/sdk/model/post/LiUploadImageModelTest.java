package lithium.community.android.sdk.model.post;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiUploadImageModelTest {

    private final String type = "Image";
    private final String title = "getUploadImageClient";
    private final String field = "image.content";
    private final String visibility = "public";
    private final String description = "TestImage";

    private LiUploadImageModel liUploadImageModel = new LiUploadImageModel();

    @Test
    public void getParamsTest() {
        liUploadImageModel.setType(type);
        liUploadImageModel.setVisibility(visibility);
        liUploadImageModel.setField(field);
        liUploadImageModel.setVisibility(visibility);
        liUploadImageModel.setDescription(description);
        liUploadImageModel.setTitle(title);
        assertEquals(type, liUploadImageModel.getType());
        assertEquals(title, liUploadImageModel.getTitle());
        assertEquals(field, liUploadImageModel.getField());
        assertEquals(visibility, liUploadImageModel.getVisibility());
        assertEquals(description, liUploadImageModel.getDescription());
        assertEquals(
                "{\"nameValuePairs\":{\"request\":{\"data\":{\"type\":\"Image\",\"title\":\"getUploadImageClient\","
                        + "\"field\":\"image.content\",\"visibility\":\"public\",\"description\":\"TestImage\"}}}}",
                liUploadImageModel.toJsonString());

    }
}
