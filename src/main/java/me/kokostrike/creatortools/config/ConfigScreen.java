package me.kokostrike.creatortools.config;

import lombok.Getter;
import me.kokostrike.creatortools.CreatorTools;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class ConfigScreen {
    private int minutes = 5;
    private boolean isOpen = false;

    private final CreatorTools mod;

    @Getter
    private Screen configScreen;

    private ConfigSettings configSettings;

    public ConfigScreen(CreatorTools mod) {
        this.mod = mod;
        this.configSettings = ConfigSettingsProvider.getConfigSettings();

        ClientTickEvents.START_CLIENT_TICK.register((listener) -> {
            if (isOpen) {
                listener.setScreen(configScreen);
                isOpen = false;
            }
        });
    }

    public void buildAndOpen() {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(MinecraftClient.getInstance().currentScreen)
                .setTitle(Text.literal("Creator Tools"));
        builder.setSavingRunnable(() -> {
            ConfigSettingsProvider.updateSettings(configSettings);
        });

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // General
        general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Streamer Mode"), configSettings.isStreamerMode())
                .setDefaultValue(false)
                .setSaveConsumer(s -> configSettings.setStreamerMode(s))
                .build());
        general.addEntry(entryBuilder.startStrField(Text.literal("Split Character"), configSettings.getSplitCharacter())
                .setDefaultValue("/")
                .setSaveConsumer(s -> configSettings.setSplitCharacter(s))
                .setTooltip(Text.of("Character used to split the title and subtitle in the reminders."))
                .build());

        // Reminders
        ConfigCategory reminders = builder.getOrCreateCategory(Text.literal("Reminders"));
        reminders.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Reminders"), configSettings.isEnableReminders())
                .setDefaultValue(false)
                .setSaveConsumer(s -> configSettings.setEnableReminders(s))
                .build());

        reminders.addEntry(entryBuilder.startIntField(Text.literal("Reminder Interval"), minutes)
                .setDefaultValue(minutes)
                .setSaveConsumer(s -> configSettings.setTimeInterval(s))
                .setTooltip(Text.of("Interval between reminders."))
                .build());

        reminders.addEntry(entryBuilder.startEnumSelector(Text.literal("Time Unit"), TimeUnit.class, configSettings.getSelectedTimeUnit())
                .setDefaultValue(TimeUnit.SECONDS)
                .setSaveConsumer(s -> configSettings.setSelectedTimeUnit(s))
                .setTooltip(Text.of("Time unit for the reminder interval."))
                .build());

        reminders.addEntry(entryBuilder.startStrList(Text.literal("Reminders"), configSettings.getReminderList())
                .setDefaultValue(new ArrayList<>())
                .setSaveConsumer(s -> configSettings.setReminderList(s))
                .setTooltip(Text.of("Reminders to be displayed in the screen every couple of minutes.\nFormat: 'Title'" + configSettings.getSplitCharacter() + "'Subtitle'"))
                .build());

        open(builder);
    }

    private void open(ConfigBuilder builder) {
        configScreen = builder.build();
        isOpen = true;
    }

}
