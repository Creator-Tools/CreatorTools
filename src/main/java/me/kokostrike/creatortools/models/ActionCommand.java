package me.kokostrike.creatortools.models;

import lombok.Data;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ActionCommand {

    private final String commandToInput;

    private final Map<Integer, String> tokens;

    private final String commandToRun;


    public ActionCommand(String splitCharacter, String fieldInput) {
        String[] commands = fieldInput.split(splitCharacter);
        tokens = new HashMap<>();

        String[] partsOfInput = commands[0].split(" ");

        int index = 0;
        for (String part : partsOfInput) {
            if (part.startsWith("$")) tokens.put(index, part);
            index++;
        }

        this.commandToInput = partsOfInput[0];
        this.commandToRun = commands[1];
    }


    public void run(String input, String name) {
        //!mob $mob/summon $mob ~ ~ ~
        //!mob zombie -> summon $mob ~ ~ ~
        String[] inputParts = input.split(" ");
        Map<String, String> toReplace = new HashMap<>();
        int index = 0;
        for (String part : inputParts) {
            System.out.println(part);
            if (tokens.containsKey(index)) toReplace.put(tokens.get(index), part);
            index++;
        }

        String editedCommand = defaultParametersEdit(commandToRun, name);

        if (toReplace.isEmpty()) runCommand(editedCommand);
        else {
            String editedCommandToRun = editedCommand;
            for (Map.Entry<String, String> entry : toReplace.entrySet()) {
                editedCommandToRun = editedCommandToRun.replace(entry.getKey(), entry.getValue().replace("-", "_"));
            }
            runCommand(editedCommandToRun);
        }
    }


    private String defaultParametersEdit(String toEdit, String name) {
        return toEdit.replace("{name}", name);
    }


    private void runCommand(String command) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        player.networkHandler.sendCommand(command);
    }
}
