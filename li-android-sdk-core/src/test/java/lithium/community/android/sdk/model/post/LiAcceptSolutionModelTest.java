package lithium.community.android.sdk.model.post;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/2/16.
 */

public class LiAcceptSolutionModelTest {

    private final String type = "accept_solution";
    private final String messageId = "100";

    private LiAcceptSolutionModel liAcceptSolutionModel = new LiAcceptSolutionModel();

    @Test
    public void getTypeTest() {
        liAcceptSolutionModel.setType(type);
        assertEquals(type, liAcceptSolutionModel.getType());
    }

    @Test
    public void getMessageIdTest() {
        liAcceptSolutionModel.setMessageid(messageId);
        assertEquals(messageId, liAcceptSolutionModel.getMessageid());
    }

    @Test
    public void testToJson() {
        liAcceptSolutionModel.setType(type);
        liAcceptSolutionModel.setMessageid(messageId);
        Assert.assertEquals("{\"data\":{\"type\":\"accept_solution\",\"message_id\":\"100\"}}",
                liAcceptSolutionModel.toJson().toString());
    }

    @Test
    public void testToJsonString() {
        liAcceptSolutionModel.setType(type);
        liAcceptSolutionModel.setMessageid(messageId);
        Assert.assertEquals("{\"data\":{\"type\":\"accept_solution\",\"message_id\":\"100\"}}",
                liAcceptSolutionModel.toJsonString().toString());
    }
}
