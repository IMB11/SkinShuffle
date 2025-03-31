package dev.imb11.skinshuffle.util;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.imb11.skinshuffle.SkinShuffle;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SkinCacheRegistry {
    private static final Path CACHE_FILE = SkinShuffle.DATA_DIR.resolve("skin-caches.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final HashMap<String, Path> DOWNLOADED_SKIN_CACHES = new HashMap<>();
    private static final HashMap<String, String> UPLOADED_SKIN_CACHES = new HashMap<>();

    private static void saveCache() {
        try {
            JsonObject object = new JsonObject();
            JsonObject uploaded = new JsonObject();
            JsonObject downloaded = new JsonObject();

            for (Map.Entry<String, Path> entry : DOWNLOADED_SKIN_CACHES.entrySet()) {
                downloaded.addProperty(entry.getKey(), entry.getValue().toString());
            }

            for (Map.Entry<String, String> entry : UPLOADED_SKIN_CACHES.entrySet()) {
                uploaded.addProperty(entry.getKey(), entry.getValue());
            }

            object.add("uploaded", uploaded);
            object.add("downloaded", downloaded);

            String jsonString = GSON.toJson(object);
            Files.writeString(CACHE_FILE, jsonString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save SkinCacheRegistry to file.", e);
        }
    }


    public static void initialize() {
        try {
            if (!CACHE_FILE.toFile().exists()) saveCache();

            String jsonString = java.nio.file.Files.readString(CACHE_FILE);
            JsonObject jsonObject = GSON.fromJson(jsonString, JsonObject.class);

            JsonObject uploadedCaches = jsonObject.get("uploaded").getAsJsonObject();
            JsonObject downloadedCaches = jsonObject.get("downloaded").getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : uploadedCaches.entrySet()) {
                String hash = entry.getKey();
                String url = entry.getValue().getAsString();
                UPLOADED_SKIN_CACHES.put(hash, url);
            }

            for (Map.Entry<String, JsonElement> entry : downloadedCaches.entrySet()) {
                String hash = entry.getKey();
                Path path = Path.of(entry.getValue().getAsString());
                DOWNLOADED_SKIN_CACHES.put(hash, path);
            }

            validateCaches();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SkinCacheRegistry", e);
        }
    }

    public static @Nullable String getCachedUploadedSkin(File skinTexture) throws IOException {
        String hash = getHash(Files.readAllBytes(skinTexture.toPath()));
        return UPLOADED_SKIN_CACHES.get(hash);
    }

    public static void saveUploadedSkin(File skinTexture, String url) throws IOException {
        String hash = getHash(Files.readAllBytes(skinTexture.toPath()));
        UPLOADED_SKIN_CACHES.put(hash, url);
        saveCache();
    }

    /**
     * Gets a sha256 hashcode in string format from texture bytes.
     *
     * @param skinTexture The texture bytes.
     * @return A sha256 hashcode in string format.
     */
    private static String getHash(byte[] skinTexture) {
        return Hashing.sha256().hashBytes(skinTexture).toString();
    }

    /**
     * Remove any invalid cache entries.
     */
    public static void validateCaches() {
        ArrayList<String> hashesToInvalidate = new ArrayList<>();

        for (Map.Entry<String, Path> entry : DOWNLOADED_SKIN_CACHES.entrySet()) {
            String hash = entry.getKey();
            Path path = entry.getValue();
            if (!path.toFile().exists()) hashesToInvalidate.add(hash);
        }

        hashesToInvalidate.forEach(DOWNLOADED_SKIN_CACHES::remove);
    }
}
