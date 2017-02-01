package lithium.community.android.sdk.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by kunal.shrivastava on 12/1/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({LiSystemClock.class, System.class})
public class LiSystemClockTest {
    private long currentTime;
    @Before
    public void setUp() {
        currentTime = 1L;
    }

    @Test
    public void testGetCurrentTimeMillis() {
        PowerMockito.mockStatic(System.class);
        BDDMockito.given(System.currentTimeMillis()).willReturn(currentTime);
        long output = LiSystemClock.INSTANCE.getCurrentTimeMillis();
        PowerMockito.verifyStatic();
        Assert.assertEquals(1L, output);
    }


}
