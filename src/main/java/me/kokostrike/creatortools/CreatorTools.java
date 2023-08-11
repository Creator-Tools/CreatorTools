package me.kokostrike.creatortools;

import com.mojang.brigadier.CommandDispatcher;
import lombok.Getter;
import me.kokostrike.creatortools.commands.CreatorToolsCommand;
import me.kokostrike.creatortools.config.ConfigScreen;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CreatorTools implements ModInitializer {
	public static final String MOD_ID = "creatortools";

	@Getter
    private final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Getter
	private static ConfigScreen configScreen;

	public static Screen getScreen(Screen parent) {
		return configScreen.getScreen(parent);
	}


	@Override
	public void onInitialize() {
		LOGGER.info("CreatorTools has been enabled!");

		loadConfig();
		loadCommands();
	}

	private void loadConfig() {
		configScreen = new ConfigScreen(this);
	}

	private void loadCommands() {
		new CreatorToolsCommand(this);
	}

}