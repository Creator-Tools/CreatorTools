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
public class ConfigScreen {
    private int seconds = 5;
    private boolean isOpen = false;

    private final CreatorTools mod;

    @Getter
    private Screen configScreen;

    public ConfigScreen(CreatorTools mod) {
        this.mod = mod;
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
            mod.getLOGGER().info("SAVING VARIABLES!!!");
        });

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        general.addEntry(entryBuilder.startIntField(Text.literal("Seconds"), seconds)
                .setDefaultValue(0)
                .setTooltip(Text.literal("Second value!"))
                .setSaveConsumer(s -> seconds = s)
                .build());
        open(builder);
    }

    private void open(ConfigBuilder builder) {
        configScreen = builder.build();
        isOpen = true;
    }

}
