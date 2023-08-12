package me.kokostrike.creatortools.managers;

import me.kokostrike.creatortools.config.ConfigSettings;
import me.kokostrike.creatortools.config.ConfigSettingsProvider;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;

import java.util.Objects;

public class StreamerModeManager {
    private final ConfigSettings configSettings;
    private String lastServerBeforeStreamerMode = "";
    private boolean changed;
    public StreamerModeManager() {
        this.configSettings = ConfigSettingsProvider.getConfigSettings();

        ClientTickEvents.START_CLIENT_TICK.register((listener) -> {
            if (listener.currentScreen instanceof MultiplayerScreen) {
                if (!listener.options.lastServer.equals("Streamer Mode is enabled!") && configSettings.isStreamerMode()) {
                    lastServerBeforeStreamerMode = listener.options.lastServer;
                    listener.options.lastServer = "Streamer Mode is enabled!";
                    changed = true;
                } else if (!listener.options.lastServer.equals(lastServerBeforeStreamerMode) && !configSettings.isStreamerMode() && changed) {
                    listener.options.lastServer = lastServerBeforeStreamerMode;
                    changed = false;
                }
            }
        });
    }
}
