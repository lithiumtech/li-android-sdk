package lithium.community.android.sdk.model.post;

import org.junit.Assert;
import org.junit.Test;

import lithium.community.android.sdk.model.LiBaseModelImpl;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/2/16.
 */

public class LiAcceptSolutionTest {

    private final String type = "accept_solution";
    private final String messageId = "100";

    private LiAcceptSolution liAcceptSolution = new LiAcceptSolution();

    @Test
    public void getTypeTest() {
        liAcceptSolution.setType(type);
        assertEquals(type, liAcceptSolution.getType());
    }

    @Test
    public void getMessageIdTest() {
        liAcceptSolution.setMessageid(messageId);
        assertEquals(messageId, liAcceptSolution.getMessageid());
    }

    @Test
    public void testToJson() {
        liAcceptSolution.setType(type);
        liAcceptSolution.setMessageid(messageId);
        Assert.assertEquals("{\"data\":{\"type\":\"accept_solution\",\"message_id\":\"100\"}}", liAcceptSolution.toJson().toString());
    }

    @Test
    public void testToJsonString() {
        liAcceptSolution.setType(type);
        liAcceptSolution.setMessageid(messageId);
        Assert.assertEquals("{\"data\":{\"type\":\"accept_solution\",\"message_id\":\"100\"}}", liAcceptSolution.toJsonString().toString());
    }
}
