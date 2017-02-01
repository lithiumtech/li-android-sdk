package lithium.community.android.sdk.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiMessageMetricsTest {

    private final Long viewCount = 1L;

    LiMessageMetrics messageMetrics = new LiMessageMetrics();

    @Test
    public void getParamsTest(){
        messageMetrics.setViewCount(viewCount);
        assertEquals(viewCount, messageMetrics.getViewCount());
    }
}
