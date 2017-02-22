package lithium.community.android.sdk.model.post;

import lithium.community.android.sdk.model.response.LiMessage;
import lithium.community.android.sdk.model.response.LiUser;

/**
 * Data Model for marking a message as abusive.
 * Created by shoureya.kant on 2/22/17.
 */
public class LiMarkAbuseModel extends LiBasePostModel {

    private String type;
    private LiUser reporter;
    private LiMessage message;
    private String body;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LiUser getReporter() {
        return reporter;
    }

    public void setReporter(LiUser reporter) {
        this.reporter = reporter;
    }

    public LiMessage getMessage() {
        return message;
    }

    public void setMessage(LiMessage message) {
        this.message = message;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
