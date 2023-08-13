package me.kokostrike.creatortools;

import lombok.Getter;
import me.kokostrike.creatortools.event.KeyInputHandler;
import net.fabricmc.api.ClientModInitializer;


public class CreatorToolsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CreatorTools.registerKeyInputHandler(new KeyInputHandler());
    }


}

