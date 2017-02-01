package lithium.community.android.sdk.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
   Created by mahaveer.udabal on 10/5/16.
 */

public class LiPrivacyLevelTest {

    @Test
    public void getExternalSourceTest(){
        assertEquals(LiPrivacyLevel.UNKNOWN, LiPrivacyLevel.getExternalSource("test"));
        assertEquals(LiPrivacyLevel.PRIVATE, LiPrivacyLevel.getExternalSource("private"));
        assertEquals(LiPrivacyLevel.PUBLIC, LiPrivacyLevel.getExternalSource("public"));
        assertEquals(LiPrivacyLevel.UNKNOWN, LiPrivacyLevel.getExternalSource(""));
        assertEquals(LiPrivacyLevel.UNKNOWN, LiPrivacyLevel.getExternalSource(null));
    }
}
