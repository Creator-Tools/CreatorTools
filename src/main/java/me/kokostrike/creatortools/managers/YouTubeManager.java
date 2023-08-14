package me.kokostrike.creatortools.managers;

import com.github.kusaanko.youtubelivechat.ChatItem;
import com.github.kusaanko.youtubelivechat.ChatItemType;
import com.github.kusaanko.youtubelivechat.IdType;
import com.github.kusaanko.youtubelivechat.YouTubeLiveChat;
import me.kokostrike.creatortools.config.ConfigSettings;
import me.kokostrike.creatortools.config.ConfigSettingsProvider;
import me.kokostrike.creatortools.enums.ChatPlace;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class YouTubeManager {
    private ScheduledExecutorService executor;
    private ConfigSettings configSettings;
    private Map<String, String> actionCommands;
    private Map<String, String> superCommands;

    public YouTubeManager() {
        this.configSettings = ConfigSettingsProvider.getConfigSettings();
        this.actionCommands = listToMap(configSettings.getCommandActions());
        this.superCommands = listToMap(configSettings.getCommandOnSuperChat());

        if (configSettings.isYoutubeEnabled()) {
            start();
        }
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

    private void start() {
        executor = Executors.newScheduledThreadPool(1);
        try {
            YouTubeLiveChat chat = new YouTubeLiveChat(configSettings.getLiveId(), true, IdType.VIDEO);
            chat.update();
            executor.scheduleAtFixedRate(() -> {
                try {
                    chat.update();
                    for (ChatItem item : chat.getChatItems()) {
                        if (item.getType().equals(ChatItemType.MESSAGE)) {
                            System.out.println(actionCommands);
                            if (actionCommands.containsKey(item.getMessage())) {
                                runCommand(actionCommands.get(item.getMessage()));
                                continue;
                            }
                            if (configSettings.getLiveChatIn().equals(ChatPlace.NONE)) continue;

                            if (configSettings.getLiveChatIn().equals(ChatPlace.CHAT))
                                sendMessage(String.format("§c§lLIVE §r-> §a%s§r: %s", item.getAuthorName(), item.getMessage()));
                            else showToast(item.getAuthorName(), item.getMessage());
                            continue;
                        }
                        if (!configSettings.getSuperChatIn().equals(ChatPlace.NONE)) {
                            System.out.println(String.format("Author: %s, Message: %s, Amount: %s", item.getAuthorName(), item.getMessage(), item.getPurchaseAmount()));
                            if (configSettings.getSuperChatIn().equals(ChatPlace.REMINDER)) showToast(String.format("%s donated %s", item.getAuthorName(), item.getPurchaseAmount()), String.format("\"%s\"", item.getMessage()));
                            else sendMessage(String.format("§6§lSUPER CHAT %s §r-> §a%s§r: %s", item.getPurchaseAmount(), item.getAuthorName(), item.getMessage()));
                        }
                        if (!configSettings.getCommandOnSuperChat().isEmpty()) {
                            if (superCommands.containsKey(getIntAmount(item.getPurchaseAmount())))
                                runCommand(superCommands.get(item.getPurchaseAmount()));
                        }
                    }
                } catch (IOException e) {
                    showToast("Error!", "Something went wrong!");
                }

            },0, 1, TimeUnit.SECONDS);
        }catch (IllegalArgumentException | IOException e) {
            showToast("Error!", "Invalid Live ID");
        }

    }

    private void stop() {
        if (executor != null) executor.shutdown();
    }

    private String getIntAmount(String amount) {
        if (amount.contains("₪"))
            return amount.split("₪")[0];
        if (amount.contains("$"))
            return amount.split("\\$")[0];
        return amount;
    }

    private void restart() {
        stop();
        if (configSettings.isYoutubeEnabled()) start();
    }

    public void update() {
        configSettings = ConfigSettingsProvider.getConfigSettings();
        actionCommands = listToMap(configSettings.getCommandActions());
        superCommands = listToMap(configSettings.getCommandOnSuperChat());
        restart();
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
