package lithium.community.android.sdk.model;

import org.junit.Test;

import lithium.community.android.sdk.model.helpers.LiConversation;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiConversationTest {

    private final String type = "conversation";
    private final boolean solved = true;

    LiConversation liConversation = new LiConversation();

    @Test
    public void getParamsTest(){
        liConversation.setType(type);
        liConversation.setSolved(solved);
        assertEquals(type, liConversation.getType());
        assertEquals(solved, liConversation.isSolved());
    }
}
