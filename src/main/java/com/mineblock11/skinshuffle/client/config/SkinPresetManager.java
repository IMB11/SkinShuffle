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
import com.mineblock11.skinshuffle.networking.ClientSkinHandling;
import com.mineblock11.skinshuffle.util.AuthUtil;
import com.mineblock11.skinshuffle.util.ToastHelper;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class SkinPresetManager {
    public static final Path PERSISTENT_SKINS_DIR = SkinShuffle.DATA_DIR.resolve("skins");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PRESETS = SkinShuffle.DATA_DIR.resolve("presets.json");

    private static final ArrayList<SkinPreset> loadedPresets = new ArrayList<>();
    private static SkinPreset chosenPreset = null;
    private static boolean cooldownActive;

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
     * Set a chosen preset, and apply it.
     *
     * @param preset The preset to apply.
     */
    public static void setChosenPreset(SkinPreset preset, boolean ignoreMatch) {
        if (chosenPreset == preset && !ignoreMatch) return;
        chosenPreset = preset;
        savePresets();

        apply();
    }

    /**
     * Save the currently loaded presets to the presets.json file.
     */
    public static void savePresets() {
        if (chosenPreset == null) {
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
        if (!PRESETS.toFile().exists()) savePresets();

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
            if (!PERSISTENT_SKINS_DIR.toFile().exists()) Files.createDirectories(PERSISTENT_SKINS_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add a preset.
     *
     * @param preset The preset to add.
     */
    public static void addPreset(SkinPreset preset) {
        loadedPresets.add(preset);
        savePresets();
    }

    /**
     * Delete a preset.
     *
     * @param skinPreset The skin preset to delete.
     */
    public static void deletePreset(SkinPreset skinPreset) {
        loadedPresets.remove(skinPreset);
        if (chosenPreset == skinPreset)
            chosenPreset = loadedPresets.get(0);
        savePresets();
    }

    /**
     * Apply the currently chosen preset - ran after configuration load.
     */
    public static void apply() {
        MinecraftClient client = MinecraftClient.getInstance();
        SkinPreset preset = getChosenPreset();

        if (!AuthUtil.isLoggedIn()) {
            AuthUtil.warnNotAuthed();
            return;
        } else if (!ClientSkinHandling.isInstalledOnServer() && client.world != null) {
            ToastHelper.showHandshakeOnChange(client);
        }

        try {
            if (preset.getSkin() instanceof UrlSkin urlSkin) {
                MojangSkinAPI.setSkinTexture(urlSkin.getUrl(), urlSkin.getModel());
            } else {
                ConfigSkin configSkin = preset.getSkin().saveToConfig();
                MojangSkinAPI.setSkinTexture(configSkin.getFile().toFile(), configSkin.getModel());
            }

            if (client.world != null) {
                sendUpdateToServer(client);
            }
        } catch (Exception e) {
            SkinShuffle.LOGGER.error("Failed to apply skin preset.", e);
        }
    }


    private static void sendUpdateToServer(MinecraftClient client) {
        if (cooldownActive) {
            client.getToastManager().add(SystemToast.create(client,
                    SystemToast.Type.PACK_LOAD_FAILURE,
                    Text.translatable("skinshuffle.cooldown.toast.title"),
                    Text.translatable("skinshuffle.cooldown.toast.message")));
        }

        ClientPlayNetworking.send(SkinShuffle.id("preset_changed"), PacketByteBufs.empty());
    }

    public static void setCooldown(boolean value) {
        cooldownActive = value;
    }
}
