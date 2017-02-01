package lithium.community.android.sdk.model.response;

import org.junit.Test;

import lithium.community.android.sdk.model.LiBaseModelImpl;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiSubscriptionsTest {

    private final Long id = 100L;
    private LiMessage liMessage = new LiMessage();
    private LiSubscriptions subscriptions = new LiSubscriptions();

    @Test
    public void getIdTest(){

        LiBaseModelImpl.LiInt liId = new LiBaseModelImpl.LiInt();
        liId.setValue(id);
        subscriptions.setId(liId);
        assertEquals(id, subscriptions.getId());
    }
    @Test
    public void getMessageTest(){

        LiBaseModelImpl.LiInt liId = new LiBaseModelImpl.LiInt();
        liId.setValue(id);
        liMessage.setId(liId);
        subscriptions.setLiMessage(liMessage);
        assertEquals(id, subscriptions.getLiMessage().getId());
    }

}
