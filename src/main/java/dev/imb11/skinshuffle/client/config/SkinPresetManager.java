package dev.imb11.skinshuffle.client.config;

import com.google.gson.*;
import dev.imb11.skinshuffle.SkinShuffle;
import dev.imb11.skinshuffle.api.MojangSkinAPI;
import dev.imb11.skinshuffle.api.SkinShuffleAPI;
import dev.imb11.skinshuffle.api.data.SkinQueryResult;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
import dev.imb11.skinshuffle.client.skin.ConfigSkin;
import dev.imb11.skinshuffle.client.skin.UrlSkin;
import dev.imb11.skinshuffle.networking.ClientSkinHandling;
import dev.imb11.skinshuffle.util.NetworkingUtil;
import dev.imb11.skinshuffle.util.SkinCacheRegistry;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SkinPresetManager {
    public static final Path PERSISTENT_SKINS_DIR = SkinShuffle.DATA_DIR.resolve("skins");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<SkinPreset> loadedPresets = new ArrayList<>();
    public static boolean LOADING_LOCK = false;
    private static Path PRESETS = SkinShuffle.DATA_DIR.resolve("presets.json");
    private static SkinPreset chosenPreset = null;
    private static SkinPreset apiPreset = null;
    private static SkinShuffleAPI SKIN_SHUFFLE_API;

    public static List<SkinPreset> getLoadedPresets() {
        return Collections.unmodifiableList(loadedPresets);
    }

    public static SkinPreset getChosenPreset() {
        if (chosenPreset == null) {
            if (!loadedPresets.isEmpty()) {
                chosenPreset = loadedPresets.get(0);
            } else {
                chosenPreset = SkinPreset.generateDefaultPreset();
                loadedPresets.add(chosenPreset);
            }
            savePresets();
        }
        return chosenPreset;
    }

    public static SkinPreset getApiPreset() {
        return apiPreset;
    }

    public static void setApiPreset(SkinPreset preset) {
        apiPreset = preset;
    }

    public static void swapPresets(int index1, int index2) {
        if (index1 < 0 || index2 < 0 || index1 >= loadedPresets.size() || index2 >= loadedPresets.size()) {
            return;
        }
        Collections.swap(loadedPresets, index1, index2);
        savePresets();
    }

    public static void setChosenPreset(SkinPreset preset, boolean ignoreMatch) {
        if (chosenPreset == preset && !ignoreMatch) return;
        chosenPreset = preset;
        savePresets();
        apply();
    }

    public static void savePresets() {
        JsonObject presetFile = new JsonObject();
        presetFile.addProperty("chosenPreset", loadedPresets.indexOf(chosenPreset));
        presetFile.addProperty("apiPreset", apiPreset == null ? -1 : loadedPresets.indexOf(apiPreset));

        JsonArray array = new JsonArray();
        for (SkinPreset loadedPreset : loadedPresets) {
            DataResult<JsonElement> dataResult = SkinPreset.CODEC.encodeStart(JsonOps.INSTANCE, loadedPreset);
            array.add(dataResult.result().orElseThrow(() -> new RuntimeException("Failed to encode skin preset.")));
        }
        presetFile.add("loadedPresets", array);

        String jsonString = GSON.toJson(presetFile);
        try {
            Files.writeString(PRESETS, jsonString, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasLoadedPresets() {
        return chosenPreset != null;
    }

    public static Path getAccountPresetsPath(String username) {
        return SkinShuffle.DATA_DIR.resolve("presets-" + username + ".json");
    }

    public static Path getGlobalPresetsPath() {
        return SkinShuffle.DATA_DIR.resolve("presets.json");
    }

    public static void loadPresets() {
        if (LOADING_LOCK) return;
        LOADING_LOCK = true;

        SKIN_SHUFFLE_API = new SkinShuffleAPI(SkinShuffleConfig.get().mineskinProxyDomain);

        if (SkinShuffleConfig.get().enableMultiAccountSupport) {
            var username = MinecraftClient.getInstance().getGameProfile().getName();
            PRESETS = getAccountPresetsPath(username);
        } else {
            PRESETS = getGlobalPresetsPath();
        }

        if (!Files.exists(PRESETS)) {
            if (chosenPreset == null) {
                chosenPreset = SkinPreset.generateDefaultPreset();
                apiPreset = chosenPreset;
                loadedPresets.add(chosenPreset);
            }
            savePresets();
        }

        loadedPresets.clear();
        chosenPreset = null;

        try {
            String jsonString = Files.readString(PRESETS);
            JsonObject presetFile = GSON.fromJson(jsonString, JsonObject.class);
            int chosenPresetIndex = presetFile.get("chosenPreset").getAsInt();
            int apiPresetIndex = presetFile.has("apiPreset") ? presetFile.get("apiPreset").getAsInt() : -1;

            JsonArray array = presetFile.get("loadedPresets").getAsJsonArray();
            for (JsonElement jsonElement : array) {
                DataResult<Pair<SkinPreset, JsonElement>> dataResult = SkinPreset.CODEC.decode(JsonOps.INSTANCE, jsonElement);
                Pair<SkinPreset, JsonElement> pair = dataResult.result().orElseThrow(() -> new RuntimeException("Failed to decode skin preset."));
                SkinPreset preset = pair.getFirst();
                loadedPresets.add(preset);
            }
            chosenPreset = loadedPresets.get(chosenPresetIndex);
            apiPreset = apiPresetIndex < 0 ? null : loadedPresets.get(apiPresetIndex);
        } catch (Exception e) {
            SkinShuffle.LOGGER.error("Failed to load presets, resetting!");
            if (chosenPreset == null) {
                chosenPreset = SkinPreset.generateDefaultPreset();
                apiPreset = chosenPreset;
                loadedPresets.add(chosenPreset);
            }
            savePresets();
        } finally {
            LOADING_LOCK = false;
        }
    }

    public static void setup() {
        try {
            if (!Files.exists(PERSISTENT_SKINS_DIR)) Files.createDirectories(PERSISTENT_SKINS_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addPreset(SkinPreset preset) {
        loadedPresets.add(preset);
        savePresets();
    }

    public static void deletePreset(SkinPreset skinPreset) {
        loadedPresets.remove(skinPreset);
        if (chosenPreset == skinPreset && !loadedPresets.isEmpty()) {
            chosenPreset = loadedPresets.getFirst();
        }
        savePresets();
    }

    public static void apply() {
        MinecraftClient client = MinecraftClient.getInstance();
        SkinPreset preset = getChosenPreset();

        if (SkinShuffleConfig.get().disableAPIUpload) {
            SkinShuffle.LOGGER.info("Skipping skin preset application due to user preference.");
            return;
        }

        if (!NetworkingUtil.isLoggedIn()) {
            SkinShuffle.LOGGER.warn("Skipping skin preset application due to offline mode being active.");
            return;
        }

        try {
            ConfigSkin configSkin = preset.getSkin().saveToConfig();

            try {
                boolean successful;
                if (preset.getSkin() instanceof UrlSkin urlSkin) {
                    successful = MojangSkinAPI.setSkinTexture(urlSkin.getUrl(), urlSkin.getModel());
                } else {
                    successful = MojangSkinAPI.setSkinTexture(configSkin.getFile().toFile(), configSkin.getModel());
                }
                if (successful) setApiPreset(preset);
                CompletableFuture.runAsync(() -> client.executeTask(() -> {
                    try {
                        String cachedURL = SkinCacheRegistry.getCachedUploadedSkin(configSkin.getFile().toFile());

                        SkinQueryResult result;
                        if (cachedURL != null) {
                            result = SKIN_SHUFFLE_API.uploadUrlSkin(cachedURL, preset.getSkin().getModel());
                        } else {
                            result = SKIN_SHUFFLE_API.uploadFileSkin(configSkin.getFile(), preset.getSkin().getModel());
                        }

                        if (result == null) {
                            throw new Exception("Failed to upload skin to MineSkin proxy.");
                        }

                        if (client.world != null && ClientSkinHandling.isInstalledOnServer()) {
                            ClientSkinHandling.sendRefresh(result);
                        }
                    } catch (Exception e) {
                        SkinShuffle.LOGGER.error(e.getMessage());
                    }
                }), Util.getMainWorkerExecutor());
            } catch (Exception e) {
                SkinShuffle.LOGGER.error("Failed to apply skin preset.", e);
            }
        } catch (Exception ignored) {
            SkinShuffle.LOGGER.info("Skipping skin preset application due to skin not being fully loaded. If this is first startup, please ignore this message.");
        }

        savePresets();
    }
}