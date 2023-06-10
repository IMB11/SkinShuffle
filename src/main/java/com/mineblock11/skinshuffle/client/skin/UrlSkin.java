package com.mineblock11.skinshuffle.client.skin;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import coresearch.cvurl.io.request.CVurl;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class UrlSkin implements Skin {
    // Keep track of how many instances exist for each texture id, so we can clean them up when they're no longer used
    public static final Object2IntMap<Identifier> INSTANCE_COUNTS = new Object2IntOpenHashMap<>();

    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("url");
    public static final Codec<UrlSkin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("url").forGetter(skin -> skin.url),
            Codec.STRING.fieldOf("model").forGetter(UrlSkin::getModel)
    ).apply(instance, UrlSkin::new));

    private final String url;
    private final String model;
    private boolean fetching = false;
    private boolean fetched = false;
    @Nullable
    private Identifier textureId;

    public UrlSkin(String url, String model) {
        this.url = url;
        this.model = model;
    }

    public void fetchSkin() {
        var id = SkinShuffle.id("skin/url/" + Math.abs(url.hashCode()));
        var textureManager = MinecraftClient.getInstance().getTextureManager();

        if (textureManager.getOrDefault(id, null) == null) {
            // Texture doesn't exist, we need to fetch it.
            fetching = true;

            new Thread(() -> {
                try {
                    Path cacheFolder = FabricLoader.getInstance().getGameDir().resolve(".cache/");
                    if(!cacheFolder.toFile().exists()) Files.createDirectory(cacheFolder);

                    Path temporaryFilePath = cacheFolder.resolve(Math.abs(url.hashCode()) + ".png");

                    try {
                        CVurl cVurl = new CVurl();
                        var response = cVurl.get(url).as(HttpResponse.BodyHandlers.ofByteArray()).get();
                        var bytes = response.getBody();

                        Files.write(temporaryFilePath, bytes);

                        var texture = new PlayerSkinTexture(temporaryFilePath.toFile(), url, new Identifier("minecraft:textures/entity/player/wide/steve.png"), true, () -> {
                            fetching = false;
                            fetched = true;
                            setTexture(id);

                            try {
                                if(temporaryFilePath.toFile().exists()) Files.delete(temporaryFilePath);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });

                        textureManager.registerTexture(id, texture);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, "UrlSkinFetcher").start();
        } else {
            // Texture already exists, we assume it hasn't changed
            fetched = true;
            setTexture(id);
        }
    }

    @Override
    public @Nullable Identifier getTexture() {
        // Fetch the skin if it hasn't been fetched yet and isn't being fetched
        if (textureId == null && !fetching && !fetched) {
            fetchSkin();
        }

        return textureId;
    }

    private void setTexture(Identifier textureId) {
        // Manages instance counts when the texture id changes
        if (this.textureId != null) {
            decrementInstanceCountAndCleanup(this.textureId);
        }
        this.textureId = textureId;
        incrementInstanceCount(textureId);
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public Identifier getSerializationId() {
        return SERIALIZATION_ID;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void finalize() throws Throwable {
        try {
            if (textureId != null) {
                decrementInstanceCountAndCleanup(textureId);
            }
        } finally {
            super.finalize();
        }
    }

    private static void incrementInstanceCount(Identifier id) {
        INSTANCE_COUNTS.compute(id, (k, v) -> v == null ? 1 : v + 1);
    }

    public static void decrementInstanceCountAndCleanup(Identifier id) {
        INSTANCE_COUNTS.compute(id, (k, v) -> v == null ? 0 : v - 1);
        if (INSTANCE_COUNTS.getInt(id) == 0) {
            MinecraftClient.getInstance().getTextureManager().destroyTexture(id);
            INSTANCE_COUNTS.removeInt(id);
        }
    }
}
