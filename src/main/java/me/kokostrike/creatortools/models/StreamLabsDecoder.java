package me.kokostrike.creatortools.models;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;

@Data
public class StreamLabsDecoder {
    private final String eventID;
    private final String _for;
    private final String type;
    private final String message;
    private final int amount;
    private final boolean isTest;
    private final String name;
    private final String currency;
    private final String from;
    private final String _id;
    private final String formatted_amount;

    public StreamLabsDecoder(Object json) {
        Gson gson = new Gson();
        JsonObject objectMap = gson.toJsonTree(json).getAsJsonObject();
        JsonObject object = objectMap.get("map").getAsJsonObject();
        this.eventID = object.get("event_id").getAsString();
        this._for = object.get("for").getAsString();
        this.type = object.get("type").getAsString();
        JsonObject messageObject = object.getAsJsonObject("message")
                .getAsJsonArray("myArrayList")
                .get(0).getAsJsonObject()
                        .getAsJsonObject("map");
        this.amount = messageObject.get("amount").getAsInt();
        this.isTest = messageObject.get("isTest").getAsBoolean();
        this.name = messageObject.get("name").getAsString();
        this.currency = messageObject.get("currency").getAsString();
        this.from = messageObject.get("from").getAsString();
        this._id = messageObject.get("_id").getAsString();
        this.message = messageObject.get("message").getAsString();
        this.formatted_amount = messageObject.get("formatted_amount").getAsString();
    }
}
