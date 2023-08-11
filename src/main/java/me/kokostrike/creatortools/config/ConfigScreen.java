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
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ConfigScreen {
    private int minutes = 5;
    private boolean isOpen = false;

    private final CreatorTools mod;
    private List<String> reminderList = new ArrayList<>();
    private TimeUnit selectedTimeUnit = TimeUnit.SECONDS;
    private boolean streamerMode = false;
    private boolean enableReminders = false;
    private String splitCharacter = "/";


    private Screen screen;

    public ConfigScreen(CreatorTools mod) {
        this.mod = mod;
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
            mod.getLOGGER().info("SAVING VARIABLES!!!");
        });

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // General
        general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Streamer Mode"), streamerMode)
                .setDefaultValue(false)
                .setSaveConsumer(s -> streamerMode = s)
                .build());
        general.addEntry(entryBuilder.startStrField(Text.literal("Split Character"), splitCharacter)
                .setDefaultValue("/")
                .setSaveConsumer(s -> splitCharacter = s)
                .setTooltip(Text.of("Character used to split the title and subtitle in the reminders."))
                .build());

        // Reminders
        ConfigCategory reminders = builder.getOrCreateCategory(Text.literal("Reminders"));
        reminders.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Reminders"), enableReminders)
                .setDefaultValue(false)
                .setSaveConsumer(s -> enableReminders = s)
                .build());

        reminders.addEntry(entryBuilder.startIntField(Text.literal("Reminder Interval"), minutes)
                .setDefaultValue(minutes)
                .setSaveConsumer(s -> minutes = s)
                .setTooltip(Text.of("Interval between reminders."))
                .build());

        reminders.addEntry(entryBuilder.startEnumSelector(Text.literal("Time Unit"), TimeUnit.class, selectedTimeUnit)
                .setDefaultValue(TimeUnit.MINUTES)
                .setSaveConsumer(s -> selectedTimeUnit = s)
                .setTooltip(Text.of("Time unit for the reminder interval."))
                .build());

        reminders.addEntry(entryBuilder.startStrList(Text.literal("Reminders"), reminderList)
                .setDefaultValue(reminderList)
                .setSaveConsumer(s -> reminderList = s)
                .setTooltip(Text.of("Reminders to be displayed in the screen every couple of minutes.\nFormat: 'Title'" + splitCharacter + "'Subtitle'"))
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
