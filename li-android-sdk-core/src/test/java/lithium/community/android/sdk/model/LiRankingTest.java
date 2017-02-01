package lithium.community.android.sdk.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.LiRanking;
import lithium.community.android.sdk.model.LiRankingDisplay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/*
  Created by mahaveer.udabal on 10/18/16.
 */

public class LiRankingTest {

    private final String HREF = "Li_Href";
    private final String NAME = "Li_Rank";
    private final Long ID = 12345L;
    @Mock
    LiRankingDisplay lithiumRankingDisplay;
    LiRanking liRanking = new LiRanking();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getHrefTest() {
        liRanking.setHref(HREF);
        assertEquals(HREF, liRanking.getHref());
    }

    @Test
    public void getNameTest() {
        LiBaseModelImpl.LiString name = new LiBaseModelImpl.LiString();
        name.setValue(this.NAME);
        liRanking.setName(name);
        assertEquals(NAME, liRanking.getName());
        assertTrue(liRanking.getNameAsLithiumString() instanceof LiBaseModelImpl.LiString);
        assertEquals(NAME, liRanking.getNameAsLithiumString().getValue());
    }

    @Test
    public void getIdTest() {
        LiBaseModelImpl.LiInt id = new LiBaseModelImpl.LiInt();
        id.setValue(this.ID);
        liRanking.setId(id);
        assertEquals(this.ID, liRanking.getId());
        assertTrue(liRanking.getIdAsLithiumInt() instanceof LiBaseModelImpl.LiInt);
        assertEquals(this.ID, liRanking.getIdAsLithiumInt().getValue());
    }

    @Test
    public void getLithiumRankingDisplayTest() {
        liRanking.setLithiumRankingDisplay(lithiumRankingDisplay);
        assertNotEquals(null, liRanking.getLithiumRankingDisplay());
    }
}
