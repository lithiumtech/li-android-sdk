package lithium.community.android.sdk.utils;

import org.junit.Assert;
import org.junit.Test;

import lithium.community.android.sdk.queryutil.LiQueryValueReplacer;

/**
 * Created by kunal.shrivastava on 12/7/16.
 */

public class LiQueryValueReplacerTest {

    @Test
    public void testGetReplacer() {
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", "test");
        Assert.assertEquals("test", liQueryValueReplacer.getReplacementMap().get("##"));
    }
}
