package lithium.community.android.sdk.model.post;

import org.junit.Test;

import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.response.LiMessage;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/2/16.
 */

public class LiReplyMessageModelTest {

    private final String type = "reply_message";
    private final String body = "this is reply";
    private LiMessage parent = new LiMessage();
    private LiReplyMessageModel liReplyMessageModel = new LiReplyMessageModel();

    @Test
    public void getTypeTest() {
        liReplyMessageModel.setType(type);
        assertEquals(type, liReplyMessageModel.getType());
    }

    @Test
    public void getBodyTest() {
        liReplyMessageModel.setBody(body);
        assertEquals(body, liReplyMessageModel.getBody());
    }

    @Test
    public void getParentTest() {
        LiBaseModelImpl.LiInt liInt = new LiBaseModelImpl.LiInt();
        liInt.setValue((long) 100);
        parent.setId(liInt);
        liReplyMessageModel.setParent(parent);
        assertEquals(parent, liReplyMessageModel.getParent());
    }
}
