package me.kokostrike.creatortools.managers;

import com.github.philippheuer.events4j.api.domain.IEventSubscription;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import me.kokostrike.creatortools.CreatorTools;
import me.kokostrike.creatortools.config.ConfigSettings;
import me.kokostrike.creatortools.config.ConfigSettingsProvider;
import me.kokostrike.creatortools.enums.ChatPlace;
import me.kokostrike.creatortools.models.ActionCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class TwitchManager {

    private final CreatorTools mod;

    private ScheduledExecutorService executor;
    private ConfigSettings configSettings;

    private Map<String, ActionCommand> actionCommands;

    private TwitchClient client;

    private IEventSubscription chatEvent;

    public TwitchManager(CreatorTools mod) {
        this.mod = mod;
        this.configSettings = ConfigSettingsProvider.getConfigSettings();
        this.actionCommands = mod.getYouTubeManager().getActionCommands();
        this.client = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .build();
        if (!configSettings.getChannelName().isEmpty())
            client.getChat().joinChannel(configSettings.getChannelName());
        //chat messages
        if (configSettings.isTwitchEnabled())
            this.chatEvent = client.getEventManager().onEvent(ChannelMessageEvent.class, this::twitchChatEvent);
    }

    private void twitchChatEvent(ChannelMessageEvent event) {
        if (!configSettings.isTwitchEnabled()) return;

        if (actionCommands.containsKey(event.getMessage().split(" ")[0])) {
            actionCommands.get(event.getMessage().split(" ")[0]).run(event.getMessage());
            return;
        }

        if (configSettings.getTwitchLiveChatIn().equals(ChatPlace.NONE)) return;
        if (configSettings.getTwitchLiveChatIn().equals(ChatPlace.CHAT))
            sendMessage(String.format("§c§lLIVE §r-> §a%s§r: %s", event.getMessageEvent().getUserDisplayName().get(), event.getMessage()));
        else showToast(event.getMessageEvent().getUserDisplayName().get(), event.getMessage());
    }

    private String getIntAmount(String amount) {
        if (amount.contains("₪"))
            return amount.split("₪")[0];
        if (amount.contains("$"))
            return amount.split("\\$")[0];
        return amount;
    }


    public void update() {
        configSettings = ConfigSettingsProvider.getConfigSettings();
        actionCommands = mod.getYouTubeManager().getActionCommands();
        client.getChat().getChannels().forEach(s -> client.getChat().leaveChannel(s));
        client.getChat().joinChannel(configSettings.getChannelName());
        if (chatEvent != null) chatEvent.dispose();
        if (configSettings.isTwitchEnabled())
            chatEvent = client.getEventManager().onEvent(ChannelMessageEvent.class, this::twitchChatEvent);
    }

    private void runCommand(String command) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        player.networkHandler.sendCommand(command);
    }

    private void sendMessage(String message) {
        if (MinecraftClient.getInstance().player != null) MinecraftClient.getInstance().player.sendMessage(Text.of(message));
    }
    private void showToast(String title, String subtitle) {
        MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, Text.of(title), Text.of(subtitle))));
    }
}
