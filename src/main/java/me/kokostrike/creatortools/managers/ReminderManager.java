package me.kokostrike.creatortools.managers;

import me.kokostrike.creatortools.config.ConfigSettings;
import me.kokostrike.creatortools.config.ConfigSettingsProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderManager {

    private ScheduledExecutorService executor;
    private ConfigSettings configSettings;

    private int index;

    public ReminderManager() {
        this.configSettings = ConfigSettingsProvider.getConfigSettings();
        this.index = -1;

        if (configSettings.isEnableReminders()) {
            start();
        }
    }

    private void start() {
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            if (index == -1 || configSettings.getReminderList().size() < index+1) index = 0;
            if (configSettings.getReminderList().isEmpty()) return;
            String[] toast = configSettings.getReminderList().get(index).split(configSettings.getSplitCharacter());
            showToast(toast[0], toast[1]);
            index++;
        },0, configSettings.getTimeInterval(), TimeUnit.valueOf(configSettings.getSelectedTimeUnit().toString()));
    }

    public void updateConfig() {
        configSettings = ConfigSettingsProvider.getConfigSettings();
        restart();
    }

    private void stop() {
        if (executor != null) executor.shutdown();
    }

    private void restart() {
        stop();
        if (configSettings.isEnableReminders()) start();
    }

    private void showToast(String title, String subtitle) {
        MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, Text.of(title), Text.of(subtitle))));
    }

}
