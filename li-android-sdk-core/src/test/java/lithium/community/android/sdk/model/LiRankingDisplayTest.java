package lithium.community.android.sdk.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.LiImage;
import lithium.community.android.sdk.model.LiRankingDisplay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/*
  Created by mahaveer.udabal on 10/18/16.
 */

public class LiRankingDisplayTest {

    private final Boolean BOLD = true;
    private final String COLOR = "RED";
    @Mock
    private LiImage leftImage, rightImage, threadImage;
    private LiRankingDisplay liRankingDisplay = new LiRankingDisplay();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getBoldTest() {
        LiBaseModelImpl.LiBoolean bold = new LiBaseModelImpl.LiBoolean();
        bold.setValue(this.BOLD);
        liRankingDisplay.setBold(bold);
        assertEquals(true, liRankingDisplay.getBold());
        assertTrue(liRankingDisplay.getBoldAsLithiumBoolean() instanceof LiBaseModelImpl.LiBoolean);
        assertEquals(BOLD, liRankingDisplay.getBoldAsLithiumBoolean().getValue());
    }

    @Test
    public void getColorTest() {
        LiBaseModelImpl.LiString color = new LiBaseModelImpl.LiString();
        color.setValue(this.COLOR);
        liRankingDisplay.setColor(color);
        assertEquals(COLOR, liRankingDisplay.getColor());
        assertTrue(liRankingDisplay.getColorAsLithiumString() instanceof LiBaseModelImpl.LiString);
        assertEquals(COLOR, liRankingDisplay.getColorAsLithiumString().getValue());
    }

    @Test
    public void getLeftImageTest() {
        liRankingDisplay.setLeftImage(leftImage);
        LiImage liImage = liRankingDisplay.getLeftImage();
        assertNotEquals(null, liImage);
    }

    @Test
    public void getRightImageTest() {
        liRankingDisplay.setRightImage(rightImage);
        LiImage liImage = liRankingDisplay.getRightImage();
        assertNotEquals(null, liImage);
    }

    @Test
    public void getThreadImageTest() {
        liRankingDisplay.setThreadImage(threadImage);
        LiImage liImage = liRankingDisplay.getThreadImage();
        assertNotEquals(null, liImage);
    }
}
