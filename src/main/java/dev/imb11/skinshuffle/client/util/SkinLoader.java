package dev.imb11.skinshuffle.client.util;

import dev.imb11.skinshuffle.client.preset.SkinPreset;
import dev.imb11.skinshuffle.client.skin.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class for loading skins from various sources.
 */
public class SkinLoader {

    /**
     * Load a skin from a source based on the type and apply it to a preset.
     *
     * @param sourceType The type of source (URL, FILE, etc.)
     * @param source     The source value (URL, file path, etc.)
     * @param model      The skin model (classic or slim)
     * @param preset     The preset to apply the skin to
     * @return CompletableFuture that completes when the skin is loaded
     */
    public static CompletableFuture<Void> loadSkin(SourceType sourceType, String source, String model, SkinPreset preset) {
        if (source.isEmpty() || sourceType == SourceType.UNCHANGED) {
            return CompletableFuture.completedFuture(null);
        }

        // Normalize the source content
        if (sourceType == SourceType.FILE || sourceType == SourceType.URL) {
            source = ValidationUtils.normalizeFilePath(source);
        }

        // Create skin based on source type
        Skin skin = createSkin(sourceType, source, model);

        if (skin == null) {
            return CompletableFuture.completedFuture(null);
        }

        // Apply the skin to the preset
        return CompletableFuture.runAsync(() -> {
            preset.setSkin(skin.saveToConfig());
            skin.getTexture();
            // Wait for the skin to finish loading
            while (preset.getSkin().isLoading()) {
                Thread.onSpinWait();
            }
        }, Util.getIoWorkerExecutor());
    }

    /**
     * Creates a skin instance based on source type.
     */
    private static Skin createSkin(SourceType sourceType, String source, String model) {
        return switch (sourceType) {
            case URL -> new UrlSkin(source, model);
            case FILE -> new FileSkin(Path.of(source), model);
            case UUID -> new UUIDSkin(UUID.fromString(source), model);
            case USERNAME -> new UsernameSkin(source, model);
            case RESOURCE_LOCATION -> new ResourceSkin(Identifier.tryParse(source), model);
            default -> Skin.randomDefaultSkin();
        };
    }

    /**
     * Enumeration of source types for skins.
     */
    public enum SourceType {
        UNCHANGED,
        USERNAME,
        UUID,
        URL,
        RESOURCE_LOCATION,
        FILE;

        /**
         * Gets translation key for invalid input messages.
         */
        public String getInvalidInputTranslationKey() {
            return "skinshuffle.edit.source.invalid_" + name().toLowerCase();
        }

        /**
         * Gets translation key for the source type.
         */
        public String getTranslationKey() {
            return "skinshuffle.edit.source." + name().toLowerCase();
        }
    }
}