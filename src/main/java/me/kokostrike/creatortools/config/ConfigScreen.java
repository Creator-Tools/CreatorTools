package me.kokostrike.creatortools.config;

import me.kokostrike.creatortools.CreatorTools;
import me.kokostrike.creatortools.enums.ChatPlace;
import me.kokostrike.creatortools.enums.SafeTimeUnit;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;


public class ConfigScreen {
    private boolean isOpen = false;

    private final CreatorTools mod;

    private Screen screen;

    private ConfigSettings configSettings;

    public ConfigScreen(CreatorTools mod) {
        this.mod = mod;
        this.configSettings = ConfigSettingsProvider.getConfigSettings();



        ClientTickEvents.START_CLIENT_TICK.register((listener) -> {
            if (isOpen) {
                listener.setScreen(screen);
                isOpen = false;
            }

        });
    }

    public Screen getScreen(Screen parent) {
        build(parent);
        return screen;
    }
    public void build(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
//                MinecraftClient.getInstance().currentScreen
                .setParentScreen(parent)
                .setTitle(Text.literal("Creator Tools"));
        builder.setSavingRunnable(() -> {
            ConfigSettingsProvider.updateSettings(configSettings);
            mod.getReminderManager().updateConfig();
            mod.getYouTubeManager().update();
            mod.getTwitchManager().update();
            CreatorTools.getKeyInputHandler().update();
        });

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        //    General
        // Streamer Mode
        SubCategoryBuilder streamerModSub = entryBuilder.startSubCategory(Text.literal("Streamer Mode"));
        streamerModSub.add(entryBuilder.startBooleanToggle(Text.literal("Censor IP Address"), configSettings.isCensorIPAddress())
                .setDefaultValue(false)
                .setSaveConsumer(s -> configSettings.setCensorIPAddress(s))
                .build());
        streamerModSub.add(entryBuilder.startBooleanToggle(Text.literal("Chat Filter"), configSettings.isChatFilter())
                .setDefaultValue(false)
                .setSaveConsumer(s -> configSettings.setChatFilter(s))
                .build());
        streamerModSub.add(entryBuilder.startStrList(Text.literal("Chat Filter Words"), configSettings.getChatFilterMessages())
                .setDefaultValue(new ArrayList<>())
                .setSaveConsumer(s -> configSettings.setChatFilterMessages(s))
                .build());

        general.addEntry(streamerModSub.build());

        general.addEntry(entryBuilder.startStrField(Text.literal("Split Character"), configSettings.getSplitCharacter())
                .setDefaultValue("/")
                .setSaveConsumer(s -> configSettings.setSplitCharacter(s))
                .setTooltip(Text.of("Character used to split the title and subtitle in the reminders."))
                .build());
        general.addEntry(entryBuilder.fillKeybindingField(Text.of("Keybind"), CreatorTools.getKeyInputHandler().getConfigKey())
                .setDefaultValue(CreatorTools.getKeyInputHandler().getDefaultValue().getDefaultKey())
                .setKeySaveConsumer(key -> configSettings.setKeyValue(key.getCode()))
                .setTooltip(Text.of("A key to open this config."))
                .build());

        // Reminders
        ConfigCategory reminders = builder.getOrCreateCategory(Text.literal("Reminders"));
        reminders.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Reminders"), configSettings.isEnableReminders())
                .setDefaultValue(false)
                .setSaveConsumer(s -> configSettings.setEnableReminders(s))
                .build());

        reminders.addEntry(entryBuilder.startIntField(Text.literal("Reminder Interval"), configSettings.getTimeInterval())
                .setDefaultValue(5)
                .setSaveConsumer(s -> configSettings.setTimeInterval(s))
                .setTooltip(Text.of("Interval between reminders."))
                .build());

        reminders.addEntry(entryBuilder.startEnumSelector(Text.literal("Time Unit"), SafeTimeUnit.class, configSettings.getSelectedTimeUnit())
                .setDefaultValue(SafeTimeUnit.SECONDS)
                .setSaveConsumer(s -> configSettings.setSelectedTimeUnit(s))
                .setTooltip(Text.of("Time unit for the reminder interval."))
                .build());

        reminders.addEntry(entryBuilder.startStrList(Text.literal("Reminders"), configSettings.getReminderList())
                .setDefaultValue(new ArrayList<>())
                .setSaveConsumer(s -> configSettings.setReminderList(s))
                .setTooltip(Text.of("Reminders to be displayed in the screen every couple of minutes.\nFormat: 'Title'" + configSettings.getSplitCharacter() + "'Subtitle'"))
                .build());

        ConfigCategory youtube = builder.getOrCreateCategory(Text.literal("Youtube"));
        youtube.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enabled"), configSettings.isYoutubeEnabled())
                .setDefaultValue(false)
                .setSaveConsumer(s -> configSettings.setYoutubeEnabled(s))
                .setTooltip(Text.literal("Is the YouTube feature enabled?"))
                .build());

        youtube.addEntry(entryBuilder.startStrField(Text.literal("Live ID"), configSettings.getLiveId())
                .setDefaultValue("")
                .setSaveConsumer(s -> {
                    if (s.contains("http") || s.contains("youtube.com")) {
                        String id = s;
                        try {
                            if (s.contains("live/")) {
                                String[] parts = s.split("live/");
                                id = parts[1].split("\\?")[0];
                            }
                            if (s.contains("?v=")) {
                                String[] parts = s.split("\\?v=");
                                id = parts[1].split("&")[0];
                            }
                        }catch (IndexOutOfBoundsException e) {
                            id = s;
                        }
                        configSettings.setLiveId(s);
                        return;
                    }
                    configSettings.setLiveId(s);
                })
                .setTooltip(Text.literal("The live ID"))
                .build());
        youtube.addEntry(entryBuilder.startEnumSelector(Text.literal("Live Chat in"), ChatPlace.class, configSettings.getLiveChatIn())
                .setDefaultValue(ChatPlace.NONE)
                .setSaveConsumer(s -> configSettings.setLiveChatIn(s))
                .setTooltip(Text.literal("Showing the live chat in the chosen place."))
                .build());
        SubCategoryBuilder superChatEvents = entryBuilder.startSubCategory(Text.literal("Super Chat Events"));
        superChatEvents.add(entryBuilder.startEnumSelector(Text.literal("Super Chat in"), ChatPlace.class, configSettings.getSuperChatIn())
                .setDefaultValue(ChatPlace.REMINDER)
                .setSaveConsumer(s -> configSettings.setSuperChatIn(s))
                .setTooltip(Text.literal("Showing the super chat in the chosen place."))
                .build());
        superChatEvents.add(entryBuilder.startStrList(Text.literal("Commands on Super Chat"), configSettings.getCommandOnSuperChat())
                .setDefaultValue(new ArrayList<>())
                .setSaveConsumer(s -> configSettings.setCommandOnSuperChat(s))
                .setTooltip(Text.of("Run a command when a super chat occurs.\nFormat: 'amount(example: 5)'" + configSettings.getSplitCharacter() + "'Command(example:gamemode creative)'"))
                .build());
        youtube.addEntry(superChatEvents.build());

        youtube.addEntry(entryBuilder.startStrList(Text.literal("Command Actions"), configSettings.getCommandActions())
                .setDefaultValue(new ArrayList<>())
                .setSaveConsumer(s -> configSettings.setCommandActions(s))
                .setTooltip(Text.of("Run a command when a action is sent.\nFormat: 'action(example: !creeper)'" + configSettings.getSplitCharacter() + "'Command(example:summon ~ ~ ~ creeper)'"))
                .build());

        //Twitch
        ConfigCategory twitch = builder.getOrCreateCategory(Text.literal("Twitch"));
        twitch.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enabled"), configSettings.isTwitchEnabled())
                .setDefaultValue(false)
                .setSaveConsumer(s -> configSettings.setTwitchEnabled(s))
                .setTooltip(Text.literal("Is the Twitch feature enabled?"))
                .build());

        twitch.addEntry(entryBuilder.startStrField(Text.literal("Channel Name"), configSettings.getChannelName())
                .setDefaultValue("")
                .setSaveConsumer(s -> configSettings.setChannelName(s))
                .setTooltip(Text.literal("The Channel Name"))
                .build());
        twitch.addEntry(entryBuilder.startEnumSelector(Text.literal("Live Chat in"), ChatPlace.class, configSettings.getTwitchLiveChatIn())
                .setDefaultValue(ChatPlace.NONE)
                .setSaveConsumer(s -> configSettings.setTwitchLiveChatIn(s))
                .setTooltip(Text.literal("Showing the live chat in the chosen place."))
                .build());
        twitch.addEntry(entryBuilder.startStrList(Text.literal("Command Actions"), configSettings.getTwitchCommandActions())
                .setDefaultValue(new ArrayList<>())
                .setSaveConsumer(s -> configSettings.setTwitchCommandActions(s))
                .setTooltip(Text.of("Run a command when a action is sent.\nFormat: 'action(example: !creeper)'" + configSettings.getSplitCharacter() + "'Command(example:summon ~ ~ ~ creeper)'"))
                .build());

        screen = builder.build();

    }
    public void buildAndOpen() {
        build(MinecraftClient.getInstance().currentScreen);
        open();
    }

    private void open() {
        isOpen = true;
    }

}
