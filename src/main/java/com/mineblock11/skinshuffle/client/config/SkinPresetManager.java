/*
 * ALL RIGHTS RESERVED
 *
 * Copyright (c) 2024 Calum H. (IMB11) and enjarai
 *
 * THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.mineblock11.skinshuffle.client.config;

import com.google.gson.*;
import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.api.SkinAPIs;
import com.mineblock11.skinshuffle.api.SkinQueryResult;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import com.mineblock11.skinshuffle.client.skin.ConfigSkin;
import com.mineblock11.skinshuffle.client.skin.UrlSkin;
import com.mineblock11.skinshuffle.networking.ClientSkinHandling;
import com.mineblock11.skinshuffle.util.NetworkingUtil;
import com.mineblock11.skinshuffle.util.SkinCacheRegistry;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.MinecraftClient;
import org.mineskin.data.Skin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

public class SkinPresetManager {
    public static final Path PERSISTENT_SKINS_DIR = SkinShuffle.DATA_DIR.resolve("skins");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PRESETS = SkinShuffle.DATA_DIR.resolve("presets.json");

    private static final ArrayList<SkinPreset> loadedPresets = new ArrayList<>();
    private static SkinPreset chosenPreset = null;
    private static SkinPreset apiPreset = null;

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
     * Get the preset currently uploaded to Mojang API.
     */
    public static SkinPreset getApiPreset() {
        return apiPreset;
    }

    /**
     * Swap the positions of two presets.
     */
    public static void swapPresets(int index1, int index2) {
        if(loadedPresets.size() - 1 < index2 || loadedPresets.size() - 1 < index1)
            return;

        Collections.swap(loadedPresets, index1, index2);
        savePresets();
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
     * Set the preset currently uploaded to Mojang API.
     *
     * @param preset The preset to apply.
     */
    public static void setApiPreset(SkinPreset preset) {
        apiPreset = preset;
    }

    /**
     * Save the currently loaded presets to the presets.json file.
     */
    public static void savePresets() {
        JsonObject presetFile = new JsonObject();
        presetFile.addProperty("chosenPreset", loadedPresets.indexOf(chosenPreset));
        presetFile.addProperty("apiPreset", apiPreset == null ? -1 : loadedPresets.indexOf(apiPreset));

        JsonArray array = new JsonArray();
        for (SkinPreset loadedPreset : loadedPresets) {
            DataResult<JsonElement> dataResult = SkinPreset.CODEC.encodeStart(JsonOps.INSTANCE, loadedPreset);
            /*? <1.20.5 {*/
            /*array.add(dataResult.getOrThrow(false, SkinShuffle.LOGGER::error));
            *//*?} else {*/
            array.add(dataResult.result().orElseThrow(() -> new RuntimeException("Failed to encode skin preset.")));
            /*?}*/
        }
        presetFile.add("loadedPresets", array);

        String jsonString = GSON.toJson(presetFile);
        try {
            Files.writeString(PRESETS, jsonString, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean LOADING_LOCK = false;

    /**
     * Load presets from the presets.json file.
     */
    public static void loadPresets() {
        if(LOADING_LOCK) return;
        LOADING_LOCK = true;

        if (!PRESETS.toFile().exists()) {
            // Generate a preset from the currently equipped skin when generating the presets file
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

            int apiPresetIndex = -1;
            if (presetFile.has("apiPreset")) {
                apiPresetIndex = presetFile.get("apiPreset").getAsInt();
            }

            JsonArray array = presetFile.get("loadedPresets").getAsJsonArray();
            for (JsonElement jsonElement : array) {
                DataResult<Pair<SkinPreset, JsonElement>> dataResult = SkinPreset.CODEC.decode(JsonOps.INSTANCE, jsonElement);
                /*? <1.20.5 {*/
                /*Pair<SkinPreset, JsonElement> pair = dataResult.getOrThrow(false, SkinShuffle.LOGGER::error);
                *//*?} else {*/
                Pair<SkinPreset, JsonElement> pair = dataResult.result().orElseThrow(() -> new RuntimeException("Failed to decode skin preset."));
                /*?}*/
                SkinPreset preset = pair.getFirst();
                loadedPresets.add(preset);
            }
            chosenPreset = loadedPresets.get(chosenPresetIndex);
            apiPreset = apiPresetIndex < 0 ? null : loadedPresets.get(apiPresetIndex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LOADING_LOCK = false;
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

        // Skip applying if the config says to.
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
                if (preset.getSkin() instanceof UrlSkin urlSkin) {
                    boolean successful = SkinAPIs.setSkinTexture(urlSkin.getUrl(), urlSkin.getModel());
                    if(successful) setApiPreset(preset);
                } else {
                    boolean successful =  SkinAPIs.setSkinTexture(configSkin.getFile().toFile(), configSkin.getModel());
                    if(successful) setApiPreset(preset);
                }

                if (client.world != null && ClientSkinHandling.isInstalledOnServer()) {
                    new Thread(() -> {
                        client.executeTask(() -> {
                            try {
                                String cachedURL = SkinCacheRegistry.getCachedUploadedSkin(configSkin.getFile().toFile());
                                Skin result;
                                if(cachedURL != null) {
                                    result = SkinAPIs.MINESKIN_CLIENT.generateUrl(cachedURL).join();
                                } else {
                                    result = SkinAPIs.MINESKIN_CLIENT.generateUpload(configSkin.getFile().toFile()).join();
                                }

                                SkinQueryResult queryResult = new SkinQueryResult(false, null, preset.getSkin().getModel(), result.data.texture.signature,  result.data.texture.value);
                                ClientSkinHandling.sendRefresh(queryResult);
                            } catch (Exception e) {
                                SkinShuffle.LOGGER.error(e.getMessage());
                            }
                        });
                    }).start();
                }
            } catch (Exception e) {
                SkinShuffle.LOGGER.error("Failed to apply skin preset.", e);
            }
        } catch (Exception ignored) {
            SkinShuffle.LOGGER.info("Skipping skin preset application due to skin not being fully loaded. If this is first startup, please ignore this message.");
        }

        SkinPresetManager.savePresets();
    }
}
