package me.kokostrike.creatortools.config;

import lombok.Data;
import me.kokostrike.creatortools.enums.ChatPlace;
import me.kokostrike.creatortools.enums.SafeTimeUnit;
import net.minecraft.client.option.KeyBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
@Data
public class ConfigSettings {
    //reminder settings
    private List<String> reminderList;
    private SafeTimeUnit selectedTimeUnit;
    private boolean enableReminders;
    private int timeInterval;

    //general settings
    private boolean streamerMode;
    private String splitCharacter;
    private int keyValue;

    //YouTube settings
    private boolean youtubeEnabled;
    private String liveId;
    private ChatPlace liveChatIn;
    private ChatPlace superChatIn;
    private List<String> commandOnSuperChat;
    private List<String> commandActions;

    public ConfigSettings(List<String> reminderList, SafeTimeUnit selectedTimeUnit, boolean enableReminders, int timeInterval, boolean streamerMode, String splitCharacter, int keyValue, boolean youtubeEnabled, String liveId, ChatPlace liveChatIn, ChatPlace superChatIn, List<String> commandOnSuperChat, List<String> commandActions) {
        this.reminderList = reminderList;
        this.selectedTimeUnit = selectedTimeUnit;
        this.enableReminders = enableReminders;
        this.timeInterval = timeInterval;
        this.streamerMode = streamerMode;
        this.splitCharacter = splitCharacter;
        this.keyValue = keyValue;
        this.youtubeEnabled = youtubeEnabled;
        this.liveId = liveId;
        this.liveChatIn = liveChatIn;
        this.superChatIn = superChatIn;
        this.commandOnSuperChat = commandOnSuperChat;
        this.commandActions = commandActions;
    }

    public ConfigSettings() {
        //reminder settings
        this.reminderList = new ArrayList<>();
        this.selectedTimeUnit = SafeTimeUnit.SECONDS;
        this.enableReminders = false;
        this.timeInterval = 5;

        //general settings
        this.streamerMode = false;
        this.splitCharacter = "/";
        this.keyValue = 75;

        //YouTube settings
        this.youtubeEnabled = false;
        this.liveId = "";
        this.liveChatIn = ChatPlace.NONE;
        this.superChatIn = ChatPlace.REMINDER;
        this.commandOnSuperChat = new ArrayList<>();
        this.commandActions = new ArrayList<>();
    }
}
