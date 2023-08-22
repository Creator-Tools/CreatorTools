package me.kokostrike.creatortools.config;

import lombok.Data;
import me.kokostrike.creatortools.enums.ChatPlace;
import me.kokostrike.creatortools.enums.SafeTimeUnit;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConfigSettings {
    //reminder settings
    private List<String> reminderList;
    private SafeTimeUnit selectedTimeUnit;
    private boolean enableReminders;
    private int timeInterval;

    //general settings
    private boolean censorIPAddress;
    private boolean chatFilter;
    private List<String> chatFilterMessages;
    private String splitCharacter;
    private int keyValue;

    //YouTube settings
    private boolean youtubeEnabled;
    private String liveId;
    private ChatPlace liveChatIn;
    private ChatPlace donationsChatIn;
    private List<String> commandsOnDonation;
    private List<String> commandActions;
    //External Platforms
    private boolean streamLabs;
    private boolean streamElements;
    //API KEYS
    private String streamLabsToken;
    private String streamElementsToken;

    //Twitch settings
    private boolean twitchEnabled;
    private String channelName;
    private ChatPlace twitchLiveChatIn;
    private List<String> twitchCommandActions;

    public ConfigSettings(List<String> reminderList, SafeTimeUnit selectedTimeUnit, boolean enableReminders, int timeInterval, boolean censorIPAddress, boolean chatFilter, List<String> chatFilterMessages, String splitCharacter, int keyValue, boolean youtubeEnabled, String liveId, ChatPlace liveChatIn, ChatPlace donationsChatIn, List<String> commandsOnDonation, List<String> commandActions, boolean twitchEnabled, String channelName, ChatPlace twitchLiveChatIn, List<String> twitchCommandActions, boolean streamLabs, boolean streamElements, String streamLabsToken, String streamElementsToken) {
        this.reminderList = reminderList;
        this.selectedTimeUnit = selectedTimeUnit;
        this.enableReminders = enableReminders;
        this.timeInterval = timeInterval;
        this.censorIPAddress = censorIPAddress;
        this.chatFilter = chatFilter;
        this.chatFilterMessages = chatFilterMessages;
        this.splitCharacter = splitCharacter;
        this.keyValue = keyValue;
        this.youtubeEnabled = youtubeEnabled;
        this.liveId = liveId;
        this.liveChatIn = liveChatIn;
        this.donationsChatIn = donationsChatIn;
        this.commandsOnDonation = commandsOnDonation;
        this.commandActions = commandActions;
        this.streamLabs = streamLabs;
        this.streamElements = streamElements;
        this.streamLabsToken = streamLabsToken;
        this.streamElementsToken = streamElementsToken;
        this.twitchEnabled = twitchEnabled;
        this.channelName = channelName;
        this.twitchLiveChatIn = twitchLiveChatIn;
        this.twitchCommandActions = twitchCommandActions;
    }

    public ConfigSettings() {
        //reminder settings
        this.reminderList = new ArrayList<>();
        this.selectedTimeUnit = SafeTimeUnit.SECONDS;
        this.enableReminders = false;
        this.timeInterval = 5;

        //general settings
        this.censorIPAddress = false;
        this.chatFilter = false;
        this.chatFilterMessages = new ArrayList<>();
        this.splitCharacter = "/";
        this.keyValue = 75;

        //YouTube settings
        this.youtubeEnabled = false;
        this.liveId = "";
        this.liveChatIn = ChatPlace.NONE;
        this.donationsChatIn = ChatPlace.REMINDER;
        this.commandsOnDonation = new ArrayList<>();
        this.commandActions = new ArrayList<>();

        //External Platforms
        this.streamElements = false;
        this.streamLabs = false;

        //API KEYS
        this.streamLabsToken = "";
        this.streamElementsToken = "";

        //twitch
        this.twitchEnabled = false;
        this.channelName = "";
        this.twitchLiveChatIn = ChatPlace.NONE;
        this.twitchCommandActions = new ArrayList<>();
    }
}
