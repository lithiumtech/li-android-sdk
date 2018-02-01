package lithium.community.android.sdk.model.response;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/4/16.
 */

public class LiBrowseTest {

    private final String id = "100";
    private final String title = "browse test";
    private LiBrowse liParent = new LiBrowse();
    private LiBrowse liBrowse = new LiBrowse();

    @Test
    public void getId() {
        liBrowse.setId(id);
        assertEquals(id, liBrowse.getId());
    }

    @Test
    public void getTitle() {
        liBrowse.setTitle(title);
        assertEquals(title, liBrowse.getTitle());
    }

    @Test
    public void getParent() {
        liParent.setId("99");
        liBrowse.setParent(liParent);
        assertEquals("99", liBrowse.getParent().getId());

    }
}
