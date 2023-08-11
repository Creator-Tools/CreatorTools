package me.kokostrike.creatortools.config;

import com.google.gson.Gson;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.*;

public class ConfigSettingsProvider {

    @Getter
    private static ConfigSettings configSettings;
    
    private static Logger logger;
    
    public static void setLogger(Logger modLogger) {
        logger = modLogger;
    }

    public static void updateSettings(ConfigSettings newConfigSettings) {
        configSettings = newConfigSettings;
        save();
    }

    public static void save() {
        try {
            Gson gson = new Gson();
            File file = new File(FabricLoader.getInstance().getConfigDir() + "/settings.json");
            file.getParentFile().mkdir();
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            gson.toJson(configSettings, writer);
            writer.flush();
            writer.close();
            logger.info("Saved successfully!");
        } catch (IOException e) {
            logger.info("Something went wrong while saving the settings!");
        }
    }

    public static void load() {
        try {
            Gson gson = new Gson();
            File file = new File(FabricLoader.getInstance().getConfigDir() + "/settings.json");
            if (file.exists()) {
                Reader reader = new FileReader(file);
                configSettings = gson.fromJson(reader, ConfigSettings.class);
                logger.info("Settings loaded!");
            } else {
                configSettings = new ConfigSettings();
                logger.info("Settings file didn't exist, defaults is being used.");
            }
        } catch (IOException e){
            logger.info("Something went wrong while loading the settings file");
        }
    }
}
