package lithium.community.android.sdk.model.post;

import com.google.gson.annotations.SerializedName;

/**
 * Created by saiteja.tokala on 4/03/17.
 */

public class LiMarkTopicModel extends LiBasePostModel {

    private String type;
    private String user;
    @SerializedName("topic_id")
    private String topicId;
    @SerializedName("mark_unread")
    private boolean markUnread;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public boolean isMarkUnread() {
        return markUnread;
    }

    public void setMarkUnread(boolean markUnread) {
        this.markUnread = markUnread;
    }
}
