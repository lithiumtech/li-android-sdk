package lithium.community.android.sdk.model.response;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiFloatedMessageModelTest {

    private final String id = "100";
    private final String href = "href";
    private LiUser user = new LiUser();
    private LiMessage message = new LiMessage();
    private final String scope = "scope";

    private LiFloatedMessageModel liFloatedMessageModel = new LiFloatedMessageModel();

    @Test
    public void getParamsTest(){

        liFloatedMessageModel.setId(id);
        liFloatedMessageModel.setHref(href);
        liFloatedMessageModel.setScope(scope);
        user.setHref(href);
        message.setIsAcceptedSolution(true);
        liFloatedMessageModel.setUser(user);
        liFloatedMessageModel.setMessage(message);
        assertEquals(id, liFloatedMessageModel.getId());
        assertEquals(href, liFloatedMessageModel.getHref());
        assertEquals(scope, liFloatedMessageModel.getScope());
        assertEquals(href, liFloatedMessageModel.getUser().getHref());
        assertEquals(true, liFloatedMessageModel.getMessage().isAcceptedSolution());
    }
}
