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
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.TutorialToast;
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
                .executes((context -> {
                    mod.getConfigScreen().buildAndOpen();
                    return 0;
                })));
        dispatcher.register(ClientCommandManager.literal("ct").
                executes((context -> {
                    mod.getConfigScreen().buildAndOpen();
                    return 0;
                })));
    }
}
