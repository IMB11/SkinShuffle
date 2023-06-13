/*
 *
 *     Copyright (C) 2023 Calum (mineblock11), enjarai
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */

package com.mineblock11.skinshuffle.client.config;

import com.google.gson.*;
import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.api.MojangSkinAPI;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import com.mineblock11.skinshuffle.client.skin.ConfigSkin;
import com.mineblock11.skinshuffle.client.skin.UrlSkin;
import com.mineblock11.skinshuffle.util.AuthUtil;
import com.mojang.datafixers.util.Pair;
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

    /**
     * Get all loaded presets.
     */
    public static ArrayList<SkinPreset> getLoadedPresets() {
        return loadedPresets;
    }

    /**
     * Get the currently chosen preset.
     */
    public static SkinPreset getChosenPreset() {
        return chosenPreset;
    }

    /**
     * Save the currently loaded presets to the presets.json file.
     */
    public static void savePresets() {
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

    /**
     * Load presets from the presets.json file.
     */
    public static void loadPresets() {
        if(!PRESETS.toFile().exists()) savePresets();

        loadedPresets.clear();
        chosenPreset = null;

        try {
            String jsonString = Files.readString(PRESETS);
            JsonObject presetFile = GSON.fromJson(jsonString, JsonObject.class);
            int chosenPresetIndex = presetFile.get("chosenPreset").getAsInt();
            JsonArray array = presetFile.get("loadedPresets").getAsJsonArray();
            for (JsonElement jsonElement : array) {
                DataResult<Pair<SkinPreset, JsonElement>> dataResult = SkinPreset.CODEC.decode(JsonOps.INSTANCE, jsonElement);
                Pair<SkinPreset, JsonElement> pair = dataResult.getOrThrow(false, SkinShuffle.LOGGER::error);
                SkinPreset preset = pair.getFirst();
                loadedPresets.add(preset);
            }
            chosenPreset = loadedPresets.get(chosenPresetIndex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create the necessary directories and cache files.
     */
    public static void setup() {
        try {
            if(!PERSISTENT_SKINS_DIR.toFile().exists()) Files.createDirectories(PERSISTENT_SKINS_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add a preset.
     * @param preset The preset to add.
     */
    public static void addPreset(SkinPreset preset) {
        loadedPresets.add(preset);
        savePresets();
    }

    /**
     * Set a chosen preset, and apply it.
     * @param preset The preset to apply.
     */
    public static void setChosenPreset(SkinPreset preset) {
        if(chosenPreset == preset) return;
        chosenPreset = preset;
        savePresets();

        apply();
    }

    /**
     * Delete a preset.
     * @param skinPreset The skin preset to delete.
     */
    public static void deletePreset(SkinPreset skinPreset) {
        loadedPresets.remove(skinPreset);
        if(chosenPreset == skinPreset)
            chosenPreset = loadedPresets.get(0);
        savePresets();
    }

    /**
     * Apply the currently chosen preset - ran after configuration load.
     */
    public static void apply() {
        SkinPreset preset = getChosenPreset();

        if(preset.getSkin() instanceof UrlSkin)
            MojangSkinAPI.resetCache();

        if(!AuthUtil.isLoggedIn()) {
            AuthUtil.warnNotAuthed();
            return;
        }

        try {
            if(preset.getSkin() instanceof UrlSkin urlSkin) {
                MojangSkinAPI.setSkinTexture(urlSkin.getUrl(), urlSkin.getModel());
            } else {
                ConfigSkin configSkin = preset.getSkin().saveToConfig();
                MojangSkinAPI.setSkinTexture(configSkin.getFile().toFile(), preset.getSkin().getModel());
            }
        } catch (Exception e) {
            SkinShuffle.LOGGER.error("Failed to apply skin preset.", e);
        }
    }
}
