package com.nopo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class Config {
    public static boolean usePointer = true;
    public static float playerX = -500;
    public static float playerY = -500;
    static FileHandle configFile = Gdx.files.external("epicnopogame/save.json");

    // look i know this code is full on doggers but its funny and it works
    public static void writeConfig() {
        configFile.writeString("{ This is not json and the top and brackets both dont do anything", false);
        configFile.writeString("\n    usePointer: " + usePointer, true);
        configFile.writeString("\n    playerX: " + playerX, true);
        configFile.writeString("\n    playerY: " + playerY, true);
        //configFile.writeString("\n    configOption4: " + configOption4, true);
        configFile.writeString("\n} and yet and are needed for operation", true);
        System.out.println(configFile.readString());
    }

    public static void loadConfig() {
        if (configFile.exists()) {
            String configAsString = configFile.readString();
            if (hasConfigOption(configAsString, 2)) usePointer = configBoolean(configAsString, 2);
            if (hasConfigOption(configAsString, 3)) playerX = configFloat(configAsString, 3);
            if (hasConfigOption(configAsString, 4)) playerY = configFloat(configAsString, 4);
            //if (hasConfigOption(configAsString, 5)) configOption4 = configInt(configAsString, 5);
        } else {
            System.out.println("https://cdn.discordapp.com/attachments/702456294874808330/964859861370302514/attachment.png");
        }
        writeConfig();
    }

    static boolean hasConfigOption(String s, int i) {
        return Utils.getLineLength(s) > i;
    }

    static boolean configBoolean(String s, int line) {
        return Boolean.parseBoolean(Utils.getLine(s, line).replaceAll("(.)*:", "").trim());
    }

    static String configString(String s, int line) {
        return Utils.getLine(s, line).replaceAll("(.)*:", "").trim();
    }

    static int configInt(String s, int line) {
        return Integer.parseInt(Utils.getLine(s, line).replaceAll("(.)*:", "").trim());
    }

    static float configFloat(String s, int line) {
        return Float.parseFloat(Utils.getLine(s, line).replaceAll("(.)*:", "").trim());
    }
}
