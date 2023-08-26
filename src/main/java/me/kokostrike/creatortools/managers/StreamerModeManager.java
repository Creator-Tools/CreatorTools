package me.kokostrike.creatortools.managers;

import me.kokostrike.creatortools.config.ConfigSettings;
import me.kokostrike.creatortools.config.ConfigSettingsProvider;
import me.kokostrike.creatortools.menus.MultiplayerScreenCensored;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;

public class StreamerModeManager {
    private final ConfigSettings configSettings;
    public StreamerModeManager() {
        this.configSettings = ConfigSettingsProvider.getConfigSettings();

        ClientTickEvents.START_CLIENT_TICK.register((listener) -> {
            if (!configSettings.isCensorIPAddress()) return;

            if (!(listener.currentScreen instanceof MultiplayerScreenCensored) && listener.currentScreen instanceof MultiplayerScreen realScreen ) {
                listener.setScreen(new MultiplayerScreenCensored(realScreen, configSettings.getCensorMessage()));
            }
        });

    }
}
