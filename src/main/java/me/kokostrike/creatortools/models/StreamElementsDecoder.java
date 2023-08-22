package me.kokostrike.creatortools.models;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;

@Data
public class StreamElementsDecoder {
    private final String createdAt;
    private final String activityId;
    private final boolean isMock;
    private final int amount;
    private final boolean gifted;
    private final String providerId;
    private final String avatar;
    private final String message;
    private final String username;
    private final String provider;
    private final String channel;
    private final String _id;
    private final String type;
    private final String updatedAt;

    public StreamElementsDecoder(Object json) {
        Gson gson = new Gson();
        JsonObject objectMap = gson.toJsonTree(json).getAsJsonObject();
        JsonObject object = objectMap.get("map").getAsJsonObject();
        System.out.println(object);
        this.createdAt = object.get("createdAt").getAsString();
        this.activityId = object.get("activityId").getAsString();
        this.isMock = object.get("isMock").getAsBoolean();
        JsonObject dataObject = object.getAsJsonObject("data").getAsJsonObject("map");;
//                .getAsJsonArray("myArrayList")
//                .get(0).getAsJsonObject()
        System.out.println(dataObject);
        this.amount = dataObject.get("amount").getAsInt();
        if (dataObject.get("gifted") != null) this.gifted = dataObject.get("gifted").getAsBoolean();
        else this.gifted = false;
        this.providerId = dataObject.get("providerId").getAsString();
        this.avatar = dataObject.get("avatar").getAsString();
        if (dataObject.get("message") != null) this.message = dataObject.get("message").getAsString();
        else this.message = "";
        this.username = dataObject.get("username").getAsString();
        this.provider = object.get("provider").getAsString();
        this.channel = object.get("channel").getAsString();
        this._id = object.get("_id").getAsString();
        this.type = object.get("type").getAsString();
        this.updatedAt = object.get("updatedAt").getAsString();
    }
}
