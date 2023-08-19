package me.kokostrike.creatortools.managers;

import com.github.philippheuer.events4j.api.domain.IEventSubscription;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import me.kokostrike.creatortools.config.ConfigSettings;
import me.kokostrike.creatortools.config.ConfigSettingsProvider;
import me.kokostrike.creatortools.enums.ChatPlace;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class TwitchManager {
    private ScheduledExecutorService executor;
    private ConfigSettings configSettings;
    private Map<String, String> actionCommands;

    private TwitchClient client;

    private IEventSubscription chatEvent;

    public TwitchManager() {
        this.configSettings = ConfigSettingsProvider.getConfigSettings();
        this.actionCommands = listToMap(configSettings.getTwitchCommandActions());
        this.client = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .build();
        if (!configSettings.getChannelName().isEmpty())
            client.getChat().joinChannel(configSettings.getChannelName());
        //chat messages
        if (configSettings.isYoutubeEnabled())
            this.chatEvent = client.getEventManager().onEvent(ChannelMessageEvent.class, this::twitchChatEvent);
    }

    private void twitchChatEvent(ChannelMessageEvent event) {
        System.out.println("Test 1");
        if (!configSettings.isTwitchEnabled()) return;

        if (actionCommands.containsKey(event.getMessage())) {
            runCommand(actionCommands.get(event.getMessage()));
            return;
        }


        if (configSettings.getTwitchLiveChatIn().equals(ChatPlace.NONE)) return;
        if (configSettings.getTwitchLiveChatIn().equals(ChatPlace.CHAT))
            sendMessage(String.format("§c§lLIVE §r-> §a%s§r: %s", event.getMessageEvent().getUserDisplayName().get(), event.getMessage()));
        else showToast(event.getMessageEvent().getUserDisplayName().get(), event.getMessage());
    }

    private Map<String, String> listToMap(List<String> list) {
        Map<String, String> map = new HashMap<>();
        for (String s : list) {
            if (s.isEmpty() || !s.contains(configSettings.getSplitCharacter())) {
                list.remove(s);
                continue;
            }
            String[] parts = s.split(configSettings.getSplitCharacter());
            map.put(parts[0], parts[1]);
        }
        return map;
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
        actionCommands = listToMap(configSettings.getTwitchCommandActions());
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
