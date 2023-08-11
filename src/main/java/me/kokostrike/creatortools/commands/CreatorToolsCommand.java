package me.kokostrike.creatortools.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.kokostrike.creatortools.CreatorTools;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CreatorToolsCommand {

    private final CreatorTools mod;

    private LiteralCommandNode<FabricClientCommandSource> commandNode;

    private List<ScheduledExecutorService> tasks = new ArrayList<>();

    public CreatorToolsCommand(CreatorTools mod) {
        this.mod = mod;
        ClientCommandRegistrationCallback.EVENT.register(this::registerClientCommand);
    }

    private void registerClientCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        commandNode = dispatcher.register(ClientCommandManager.literal("creatortools")
                .then(ClientCommandManager.literal("test").executes((this::subShowToast)))
                .then(ClientCommandManager.literal("run").executes((this::subRunMultiple)))
                .then(ClientCommandManager.literal("stopAll").executes((this::subStopAll)))
                .then(ClientCommandManager.literal("openGUI").executes((this::subOpenGUI)))
                .executes((context -> {
                    sendMessage("Test 2");
                    return 0;
                })));
        dispatcher.register(ClientCommandManager.literal("ct").redirect(commandNode));
    }

    //sub commands

    private int subShowToast(CommandContext<FabricClientCommandSource> context) {

        showToast("test toast", "lol toast");

        return 0;
    }
    private int subRunMultiple(CommandContext<FabricClientCommandSource> context) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> showToast("Toast!", "What a great toast!"), 0, 15, TimeUnit.SECONDS);
        tasks.add(executor);
        return 0;
    }
    private int subStopAll(CommandContext<FabricClientCommandSource> context) {
        for (ScheduledExecutorService task : tasks) {
            task.shutdown();
        }
        return 0;
    }
    private int subOpenGUI(CommandContext<FabricClientCommandSource> context) {
        mod.getLOGGER().info(context.getInput());
        mod.getConfigScreen().buildAndOpen();
        return 0;
    }

    // helper functions
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

}
