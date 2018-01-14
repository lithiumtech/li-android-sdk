package lithium.community.android.sdk.model.post;

import org.junit.Test;

import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.response.LiMessage;
import lithium.community.android.sdk.model.response.LiUser;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 2/22/17.
 */
public class LiMarkAbuseModelTest {

    private Long messageId = 10L;
    private Long userId = 100L;

    @Test
    public void testMarkAbuseModel() {

        LiUser liUser = new LiUser();
        liUser.setId(100L);

        LiMessage liMessage = new LiMessage();
        LiBaseModelImpl.LiInt liInt = new LiBaseModelImpl.LiInt();
        liInt.setValue(10L);
        liMessage.setId(liInt);

        LiMarkAbuseModel liMarkAbuseModel = new LiMarkAbuseModel();
        liMarkAbuseModel.setReporter(liUser);
        liMarkAbuseModel.setMessage(liMessage);
        liMarkAbuseModel.setType("mark_abuse");
        liMarkAbuseModel.setBody("This is abusive");

        assertEquals("mark_abuse", liMarkAbuseModel.getType());
        assertEquals("This is abusive", liMarkAbuseModel.getBody());
        assertEquals(messageId, liMarkAbuseModel.getMessage().getId());
        assertEquals(userId, liMarkAbuseModel.getReporter().getId());
    }
}
