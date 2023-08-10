package me.kokostrike.creatortools;

import com.mojang.brigadier.CommandDispatcher;
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
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static int seconds = 0;

	private boolean isOpen = true;


	private List<ScheduledExecutorService> tasks = new ArrayList<>();

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		ClientCommandRegistrationCallback.EVENT.register(this::registerClientCommand);
	}

	private void registerClientCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(ClientCommandManager.literal("creatortools")
				.then(ClientCommandManager.literal("test").executes((context -> {
					showToast("Toast!", "What a great toast!");
					runCommand("gamemode survival");
					return 0;

				})))
				.then(ClientCommandManager.literal("run").executes((context -> {
					ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
					executor.scheduleAtFixedRate(() -> showToast("Toast!", "What a great toast!"), 0, 15, TimeUnit.SECONDS);
					tasks.add(executor);
					return 0;
				})))
				.then(ClientCommandManager.literal("stopAll").executes((context -> {
					for (ScheduledExecutorService task : tasks) {
						task.shutdown();
					}
					return 0;
				})))
				.then(ClientCommandManager.literal("openGUI").executes((context -> {
					buildScreen();
					return 0;
				}))).executes((context -> {
					sendMessage("Test 2");
					return 0;
				})));
	}

	private void runCommand(String command) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) return;
		player.networkHandler.sendCommand(command);
	}

	private void sendMessage(String message) {
		if (MinecraftClient.getInstance().player != null) MinecraftClient.getInstance().player.sendMessage(Text.of(message));
	}

	private boolean showToast(String title, String subtitle) {
		MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, Text.of(title), Text.of(subtitle))));

		return true;
	}

	private void buildScreen() {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(MinecraftClient.getInstance().currentScreen)
				.setTitle(Text.literal("Creator Tools"));
		builder.setSavingRunnable(() -> {
			LOGGER.info(String.format("The amount of seconds are: %s", seconds));
		});

		ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		general.addEntry(entryBuilder.startIntField(Text.literal("Seconds"), seconds)
				.setDefaultValue(0)
				.setTooltip(Text.literal("Second value!"))
				.setSaveConsumer(s -> seconds = s)
				.build());

		ClientTickEvents.START_CLIENT_TICK.register((listener) -> {
			if (isOpen) {
				listener.setScreen(builder.build());
				isOpen = false;
			}
		});
	}
}