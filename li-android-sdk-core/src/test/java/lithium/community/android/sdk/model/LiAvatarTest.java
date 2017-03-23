package lithium.community.android.sdk.model;

import org.json.JSONException;
import org.junit.Test;

import lithium.community.android.sdk.model.helpers.LiAvatar;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiAvatarTest {

    private static final String PROFILE_IMAGE_URL = "PROFILE_IMAGE_URL";
    private static final String MESSAGE_IMAGE_URL = "MESSAGE_IMAGE_URL";
    private String profile = "profile";
    private String message = "This is Avatar";

    private LiAvatar liAvatar = new LiAvatar();

    @Test
    public void getParamsTest() throws JSONException {
        liAvatar.setProfile(profile);
        liAvatar.setMessage(message);
        assertEquals(profile, liAvatar.getProfile());
        assertEquals(message, liAvatar.getMessage());
        assertEquals("{\"MESSAGE_IMAGE_URL\":\"This is Avatar\",\"PROFILE_IMAGE_URL\":\"profile\"}", liAvatar.jsonSerialize().toString());
        LiAvatar liAvatar1 = new LiAvatar();
        liAvatar1 = LiAvatar.jsonDeserialize(liAvatar.jsonSerialize());
        assertEquals(profile, liAvatar1.getProfile());
        assertEquals(message, liAvatar1.getMessage());
    }
}
