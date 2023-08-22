package me.kokostrike.creatortools.managers;

import com.github.kusaanko.youtubelivechat.ChatItem;
import com.github.kusaanko.youtubelivechat.ChatItemType;
import com.github.kusaanko.youtubelivechat.IdType;
import com.github.kusaanko.youtubelivechat.YouTubeLiveChat;
import io.socket.client.IO;
import io.socket.client.Socket;
import me.kokostrike.creatortools.config.ConfigSettings;
import me.kokostrike.creatortools.config.ConfigSettingsProvider;
import me.kokostrike.creatortools.enums.ChatPlace;
import me.kokostrike.creatortools.models.ActionCommand;
import me.kokostrike.creatortools.models.StreamLabsDecoder;
import me.kokostrike.creatortools.models.StreamElementsDecoder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class YouTubeManager {
    private ScheduledExecutorService executor;
    private ConfigSettings configSettings;
    private Map<String, ActionCommand> actionCommands;
    private Map<String, String> donationCommands;

    private Socket streamLabsSocket;
    private Socket streamElementsSocket;

    public YouTubeManager() {
        this.configSettings = ConfigSettingsProvider.getConfigSettings();
        this.actionCommands = listToActionCommands(configSettings.getCommandActions());
        this.donationCommands = listToMap(configSettings.getCommandsOnDonation());

        if (configSettings.isYoutubeEnabled()) {
            start();
        }
    }

    private Map<String, String> listToMap(List<String> list) {
        Map<String, String> map = new HashMap<>();
        List<String> toRemove = new ArrayList<>();
        for (String s : list) {
            if (s.isEmpty() || !s.contains(configSettings.getSplitCharacter())) {
                toRemove.add(s);
                continue;
            }
            String[] parts = s.split(configSettings.getSplitCharacter());
            map.put(parts[0], parts[1]);
        }
        if (!toRemove.isEmpty()) {
            list.removeAll(toRemove);
            configSettings.setCommandActions(list);
            ConfigSettingsProvider.updateSettings(configSettings);
        }
        return map;
    }

    private Map<String, ActionCommand> listToActionCommands(List<String> list) {
        Map<String, ActionCommand> map = new HashMap<>();
        List<String> toRemove = new ArrayList<>();
        for (String s : list) {
            if (s.isEmpty() || !s.contains(configSettings.getSplitCharacter())) {
                toRemove.add(s);
                continue;
            }
            ActionCommand actionCommand = new ActionCommand(configSettings.getSplitCharacter(), s);
            map.put(actionCommand.getCommandToInput(), actionCommand);
        }
        if (!toRemove.isEmpty()) {
            list.removeAll(toRemove);
            configSettings.setCommandActions(list);
            ConfigSettingsProvider.updateSettings(configSettings);
        }
        return map;
    }

    private void streamLabsDonationEvent(Object... data) {
        if (data[0].toString().contains("\"type\":\"donation\"")) {
            StreamLabsDecoder donationData = new StreamLabsDecoder(data[0]);
            donationEvent(donationData.getFrom(), donationData.getMessage(), donationData.getFormatted_amount());
        }
    }

    private void streamElementsDonationEvent(Object... data) {
        if (data[0].toString().contains("\"type\":\"tip\"")) {
            StreamElementsDecoder donationData = new StreamElementsDecoder(data[0]);
            donationEvent(donationData.getUsername(), donationData.getMessage(), donationData.getAmount() + "$");
        }
    }

    private void donationEvent(String author, String message, String amount) {
        if (!configSettings.getDonationsChatIn().equals(ChatPlace.NONE)) {
            if (configSettings.getDonationsChatIn().equals(ChatPlace.REMINDER)) showToast(String.format("%s donated %s", author, amount), String.format("\"%s\"", message));
            else sendMessage(String.format("§6§lDONATION %s §r-> §a%s§r: %s", amount, author, message));
        }
        if (!configSettings.getCommandsOnDonation().isEmpty()) {
            System.out.println(getIntAmount(amount));
            if (donationCommands.containsKey(getIntAmount(amount)))
                runCommand(donationCommands.get(getIntAmount(amount)));
        }
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
                            if (actionCommands.containsKey(item.getMessage().split(" ")[0])) {
                                actionCommands.get(item.getMessage().split(" ")[0]).run(item.getMessage());
                                continue;
                            }
                            if (configSettings.getLiveChatIn().equals(ChatPlace.NONE)) continue;

                            if (configSettings.getLiveChatIn().equals(ChatPlace.CHAT))
                                sendMessage(String.format("§c§lLIVE §r-> §a%s§r: %s", item.getAuthorName(), item.getMessage()));
                            else showToast(item.getAuthorName(), item.getMessage());
                            continue;
                        }
                        donationEvent(item.getAuthorName(), item.getMessage(), item.getPurchaseAmount());
                    }
                } catch (IOException e) {
                    showToast("Error!", "Something went wrong!");
                }

            },0, 1, TimeUnit.SECONDS);
        }catch (IllegalArgumentException | IOException e) {
            showToast("Error!", "Invalid Live ID");
        }

        try {
            if (configSettings.isStreamLabs() && !configSettings.getStreamLabsToken().isEmpty()) {
                streamLabsSocket = IO.socket("https://sockets.streamlabs.com?token=" + configSettings.getStreamLabsToken());

                streamLabsSocket.connect();

                streamLabsSocket.on("event", this::streamLabsDonationEvent);
            }
        } catch (URISyntaxException e) {
            showToast("StreamLabs error!", "Socket key is invalid!");
        }

        try {
            if (configSettings.isStreamElements() && !configSettings.getStreamElementsToken().isEmpty()) {
                streamElementsSocket = IO.socket("https://realtime.streamelements.com", IO.Options.builder().setTransports(new String[]{"websocket"}).build());

                streamElementsSocket.connect();

                streamElementsSocket.on("connect", (data) -> {
                    System.out.println("Connecting");
                    streamElementsSocket.emit("authenticate", Map.of(
                            "method", "jwt",
                            "token", configSettings.getStreamElementsToken()
                    ));
                });

                streamElementsSocket.on("authenticated", (data) -> System.out.println(Arrays.toString(data)));
                streamElementsSocket.on("unauthorized", (data) -> System.out.println(Arrays.toString(data)));



                streamElementsSocket.on("event", this::streamElementsDonationEvent);
                streamElementsSocket.on("event:test", this::streamElementsDonationEvent);
            }
        } catch (URISyntaxException e) {
            showToast("StreamLabs error!", "Socket key is invalid!");
        }
    }

    private void stop() {
        if (executor != null) executor.shutdown();
        if (streamLabsSocket != null) streamLabsSocket.close();
        if (streamElementsSocket != null) streamElementsSocket.close();
    }

    private String getIntAmount(String amount) {
        return String.valueOf((int) Double.parseDouble(amount.replace("₪", "").replace("$", "")));
    }

    private void restart() {
        stop();
        if (configSettings.isYoutubeEnabled()) start();
    }

    public void update() {
        configSettings = ConfigSettingsProvider.getConfigSettings();
        actionCommands = listToActionCommands(configSettings.getCommandActions());
        donationCommands = listToMap(configSettings.getCommandsOnDonation());
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
