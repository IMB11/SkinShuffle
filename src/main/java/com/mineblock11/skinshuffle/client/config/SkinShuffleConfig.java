package com.mineblock11.skinshuffle.client.config;

import com.google.gson.*;
import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import com.mineblock11.skinshuffle.client.skin.ConfigSkin;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class SkinShuffleConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PRESETS = SkinShuffle.DATA_DIR.resolve("presets.json");
    public static final Path PERSISTENT_SKINS_DIR = SkinShuffle.DATA_DIR.resolve("skins");
    public static final Path CONFIG_FILE = SkinShuffle.DATA_DIR.resolve("config.json");

    private static SkinPreset chosenPreset = null;
    private static final ArrayList<SkinPreset> loadedPresets = new ArrayList<>();

    public static void saveConfig() {
        if(chosenPreset == null) {
            chosenPreset = SkinPreset.generateDefaultPreset();
            loadedPresets.add(chosenPreset);
        }

        JsonObject presetFile = new JsonObject();
        presetFile.addProperty("chosenPreset", loadedPresets.indexOf(chosenPreset));

        JsonArray array = new JsonArray();
        for (SkinPreset loadedPreset : loadedPresets) {
            DataResult<JsonElement> dataResult = SkinPreset.CODEC.encodeStart(JsonOps.INSTANCE, loadedPreset);
            array.add(dataResult.getOrThrow(false, SkinShuffle.LOGGER::error));
        }
        presetFile.add("loadedPresets", array);

        String jsonString = GSON.toJson(presetFile);
        try {
            Files.writeString(PRESETS, jsonString, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadConfig() {
        if(!CONFIG_FILE.toFile().exists()) saveConfig();
    }

    public static void createDirectories() {
        try {
            Files.createDirectories(PERSISTENT_SKINS_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
