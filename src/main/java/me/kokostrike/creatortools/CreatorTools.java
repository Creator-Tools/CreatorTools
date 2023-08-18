package me.kokostrike.creatortools;

import lombok.Getter;
import me.kokostrike.creatortools.commands.CreatorToolsCommand;
import me.kokostrike.creatortools.config.ConfigScreen;
import me.kokostrike.creatortools.config.ConfigSettingsProvider;
import me.kokostrike.creatortools.event.KeyInputHandler;
import me.kokostrike.creatortools.managers.StreamerModeManager;
import me.kokostrike.creatortools.managers.ReminderManager;
import me.kokostrike.creatortools.managers.TwitchManager;
import me.kokostrike.creatortools.managers.YouTubeManager;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.gui.screen.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorTools implements ModInitializer {
	public static final String MOD_ID = "creatortools";

	@Getter
    private final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Getter
	private static ConfigScreen configScreen;

	@Getter
	private ReminderManager reminderManager;

	@Getter
	private StreamerModeManager streamerModeManager;

	@Getter
	private YouTubeManager youTubeManager;

	@Getter
	private TwitchManager twitchManager;

	@Getter
	private static KeyInputHandler keyInputHandler;

	public static Screen getScreen(Screen parent) {
		return configScreen.getScreen(parent);
	}


	@Override
	public void onInitialize() {
		loadConfig();
		loadCommands();
		loadManagers();

		LOGGER.info("CreatorTools has been enabled!");
	}

	private void loadConfig() {
		ConfigSettingsProvider.setLogger(LOGGER);
		ConfigSettingsProvider.load();

		configScreen = new ConfigScreen(this);
	}

	private void loadManagers() {
		streamerModeManager = new StreamerModeManager();
        reminderManager = new ReminderManager();
		youTubeManager = new YouTubeManager();
		twitchManager = new TwitchManager();
	}

	private void loadCommands() {
		new CreatorToolsCommand(this);
	}

	public static void registerKeyInputHandler(KeyInputHandler handler) {
		keyInputHandler = handler;
	}
}