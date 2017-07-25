package lithium.community.android.sdk.model.response;

import com.google.gson.JsonObject;

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
    public void getTargetMessageTest(){
        JsonObject messageObject = new JsonObject();
        messageObject.addProperty("id", id);
        messageObject.addProperty("type", "message");
        subscriptions.setTargetObject(messageObject);
        assertEquals(id, subscriptions.getLiMessage().getId());
    }

    @Test
    public void getTargetBoardest(){
        JsonObject messageObject = new JsonObject();
        messageObject.addProperty("id", id);
        messageObject.addProperty("type", "board");
        subscriptions.setTargetObject(messageObject);
        assertEquals(id, Long.valueOf(subscriptions.getLiBoard().getId()));
    }

}
