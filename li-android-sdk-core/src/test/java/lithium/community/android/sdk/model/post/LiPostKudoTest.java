package lithium.community.android.sdk.model.post;

import org.junit.Test;

import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.response.LiMessage;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/2/16.
 */

public class LiPostKudoTest {

    private final String type = "kudo";
    private LiMessage liMessage = new LiMessage();
    private LiPostKudoModel kudoModel = new LiPostKudoModel();

    @Test
    public void getTypeTest() {
        kudoModel.setType(type);
        assertEquals(type, kudoModel.getType());
    }

    @Test
    public void getMessageTest() {
        LiBaseModelImpl.LiInt liInt = new LiBaseModelImpl.LiInt();
        liInt.setValue((long) 100);
        liMessage.setId(liInt);
        kudoModel.setMessage(liMessage);
        assertEquals(liMessage, kudoModel.getMessage());
    }
}
