package me.kokostrike.creatortools;

import lombok.Getter;
import me.kokostrike.creatortools.commands.CreatorToolsCommand;
import me.kokostrike.creatortools.config.ConfigScreen;
import me.kokostrike.creatortools.config.ConfigSettingsProvider;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorTools implements ModInitializer {
	public static final String MOD_ID = "creatortools";

	@Getter
    private final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Getter
	private ConfigScreen configScreen;


	@Override
	public void onInitialize() {
		loadConfig();
		loadCommands();

		LOGGER.info("CreatorTools has been enabled!");
	}

	private void loadConfig() {
		ConfigSettingsProvider.setLogger(LOGGER);
		ConfigSettingsProvider.load();

		configScreen = new ConfigScreen(this);
	}

	private void loadCommands() {
		new CreatorToolsCommand(this);
	}

}