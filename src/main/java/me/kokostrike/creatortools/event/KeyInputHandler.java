package me.kokostrike.creatortools.event;

import lombok.Getter;
import lombok.Setter;
import me.kokostrike.creatortools.CreatorTools;
import me.kokostrike.creatortools.config.ConfigSettings;
import me.kokostrike.creatortools.config.ConfigSettingsProvider;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    private final String KEY_CATEGORY_CONFIG = "key.category." + CreatorTools.MOD_ID + ".config";
    private final String KEY_CONFIG = "key." + CreatorTools.MOD_ID + ".config";

    private ConfigSettings configSettings;


    @Getter
    private KeyBinding configKey;

    public KeyInputHandler() {
        configSettings = ConfigSettingsProvider.getConfigSettings();

        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_CONFIG,
                configSettings.getKeyValue(),
                KEY_CATEGORY_CONFIG
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKey.wasPressed()) {
                CreatorTools.getConfigScreen().buildAndOpen();
            }
        });
    }

    public KeyBinding getDefaultValue() {
        return new KeyBinding(KEY_CONFIG, GLFW.GLFW_KEY_K, KEY_CATEGORY_CONFIG);
    }

    public void update() {
        configSettings = ConfigSettingsProvider.getConfigSettings();

        configKey = new KeyBinding(
                KEY_CONFIG,
                configSettings.getKeyValue(),
                KEY_CATEGORY_CONFIG
        );
    }
}
