package lithium.community.android.sdk.model.response;

import org.junit.Test;

import lithium.community.android.sdk.model.LiBaseModelImpl;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/4/16.
 */

public class LiSearchTest {

    private LiMessage liMessage = new LiMessage();
    private LiSearch search = new LiSearch();

    @Test
    public void getLiMessage() {
        LiBaseModelImpl.LiInt id = new LiBaseModelImpl.LiInt();
        id.setValue(100L);
        liMessage.setId(id);
        search.setLiMessage(liMessage);
        assertEquals(liMessage.getId(), search.getLiMessage().getId());

    }

}
