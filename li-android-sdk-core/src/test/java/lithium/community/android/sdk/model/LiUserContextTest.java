package lithium.community.android.sdk.model;

import org.junit.Test;

import lithium.community.android.sdk.model.helpers.LiUserContext;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiUserContextTest {

    private final Boolean kudo = true;
    private final Boolean read = true;
    private final boolean canKudo = true;
    private final boolean canReply = true;

    private LiUserContext liUserContext = new LiUserContext();

    @Test
    public void getParamsTest(){
        liUserContext.setKudo(kudo);
        liUserContext.setRead(read);
        liUserContext.setCanKudo(canKudo);
        liUserContext.setCanReply(canReply);
        assertEquals(kudo, liUserContext.getKudo());
        assertEquals(read, liUserContext.getRead());
        assertEquals(canKudo, liUserContext.isCanKudo());
        assertEquals(canReply, liUserContext.isCanReply());
    }
}
