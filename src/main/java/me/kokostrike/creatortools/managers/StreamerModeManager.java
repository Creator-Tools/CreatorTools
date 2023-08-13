package me.kokostrike.creatortools.managers;

import me.kokostrike.creatortools.config.ConfigSettings;
import me.kokostrike.creatortools.config.ConfigSettingsProvider;
import me.kokostrike.creatortools.menus.DirectConnectCensored;
import me.kokostrike.creatortools.menus.MultiplayerScreenCensored;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;

import java.util.Objects;

public class StreamerModeManager {
    private final ConfigSettings configSettings;
    public StreamerModeManager() {
        this.configSettings = ConfigSettingsProvider.getConfigSettings();

        ClientTickEvents.START_CLIENT_TICK.register((listener) -> {
            if (!configSettings.isStreamerMode()) return;

            if (!(listener.currentScreen instanceof MultiplayerScreenCensored) && listener.currentScreen instanceof MultiplayerScreen realScreen ) {
                listener.setScreen(new MultiplayerScreenCensored(realScreen));
            }
//
//            if (!(listener.currentScreen instanceof DirectConnectCensored) && listener.currentScreen instanceof DirectConnectScreen realScreen) {
//                listener.setScreen(new DirectConnectCensored(realScreen));
//                MinecraftClient.getInstance().options.lastServer = lastServer;
//            }

        });
    }
}
