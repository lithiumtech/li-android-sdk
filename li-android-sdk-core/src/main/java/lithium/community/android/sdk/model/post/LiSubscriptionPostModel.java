package lithium.community.android.sdk.model.post;

import lithium.community.android.sdk.model.response.LiMessage;

/**
 * Created by shoureya.kant on 3/30/17.
 */

public class LiSubscriptionPostModel extends LiBasePostModel {

    private String type;
    private LiMessage target;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LiMessage getTarget() {
        return target;
    }

    public void setTarget(LiMessage target) {
        this.target = target;
    }
}
