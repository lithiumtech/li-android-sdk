package lithium.community.android.sdk.model.post;

import com.google.gson.annotations.SerializedName;

/**
 * Created by saiteja.tokala on 4/03/17.
 */

public class LiMarkMessagesModel extends LiBasePostModel {

    private String type;
    private String user;
    @SerializedName("message_ids")
    private String messageIds;
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

    public String getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(String messageIds) {
        this.messageIds = messageIds;
    }

    public boolean isMarkUnread() {
        return markUnread;
    }

    public void setMarkUnread(boolean markUnread) {
        this.markUnread = markUnread;
    }
}
