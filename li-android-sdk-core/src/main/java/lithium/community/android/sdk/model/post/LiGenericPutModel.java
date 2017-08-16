package lithium.community.android.sdk.model.post;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Request body for making any PUT call. It requires parameter as JsonObject.
 * Created by shoureya.kant on 4/11/17.
 */
public class LiGenericPutModel extends LiBasePostModel {

    private JsonObject data;

    public JsonObject getData() {
        return data;
    }
    public void setData(JsonObject data) {
        this.data = data;
    }
    @Override
    public JsonObject toJson() {
        return data.getAsJsonObject();
    }

    @Override
    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(data);
    }
}
